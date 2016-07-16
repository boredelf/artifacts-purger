package br.com.poupex.maven.extension.artifactspurger;

import java.io.File;
import java.io.IOException;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.NoSuchFileException;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.logging.FileHandler;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.logging.SimpleFormatter;

import br.com.poupex.maven.extension.artifactspurger.util.VersionComparator;
import org.apache.commons.io.FileUtils;
import org.apache.maven.AbstractMavenLifecycleParticipant;
import org.apache.maven.MavenExecutionException;
import org.apache.maven.artifact.Artifact;
import org.apache.maven.artifact.repository.ArtifactRepository;
import org.apache.maven.execution.MavenSession;
import org.codehaus.plexus.component.annotations.Component;

@Component(role = AbstractMavenLifecycleParticipant.class)
public class Main extends AbstractMavenLifecycleParticipant {

	private static final Logger LOGGER = Logger.getLogger(Main.class.getName());

	private int numVersionsToKeep = 3;

	@Override
	public void afterProjectsRead(MavenSession session) throws MavenExecutionException {
		super.afterProjectsRead(session);
		if (shouldExecute(session)) {
			try {
				this.configureLogger(session, LOGGER);
				this.execute(session);
			} catch (Exception e) {
				LOGGER.warning("An error ocurred: [" + e.getClass() + "] " + e.getMessage() + "(Cause: " + e.getCause() + ")");
			}
		}
	}

	private boolean shouldExecute(MavenSession session) {
		boolean shouldExecute = false;
		for (String targetGoal : Arrays.asList("install", "site", "deploy")) {
			if (session.getGoals().contains(targetGoal)) {
				shouldExecute = true;
			}
		}
		return shouldExecute;
	}

	private void configureLogger(MavenSession session, Logger logger) throws IOException {
		String mavenHome = session.getSystemProperties().getProperty("maven.home");
		File logDir = new File(mavenHome + "/log/artifacts-purger/");
		if (logDir.exists() || logDir.mkdirs()) {
			FileHandler fileHandler = new FileHandler(logDir.getAbsolutePath() + "/artifacts-purger.%u.%g.log", 1_000_000, 30, true);
			fileHandler.setLevel(Level.WARNING);
			fileHandler.setFormatter(new SimpleFormatter());
			logger.addHandler(fileHandler);
		}
	}

	private void execute(MavenSession session) {
		ArtifactRepository repository = session.getLocalRepository();
		Artifact artifact = repository.find(session.getCurrentProject().getArtifact());
		File groupIdDir = artifact.getFile().getParentFile().getParentFile();

		List<File> installedVersions = findInstalledVersions(groupIdDir);
		List<File> versionsToBeDeleted = getVersionsToBeDeleted(installedVersions);

		deleteVersions(versionsToBeDeleted);
	}

	public List<File> findInstalledVersions(File groupIdDir) {
		List<File> installedVersions = new ArrayList<>();
		try (DirectoryStream<Path> versions = Files.newDirectoryStream(groupIdDir.toPath())) {
			for (Path version : versions) {
				if (version.toFile().isDirectory()) {
					installedVersions.add(version.toFile());
				}
			}
		} catch (NoSuchFileException e) {
			LOGGER.info("This artifact doesn't have versions installed.");
			return Collections.emptyList();
		} catch (IOException e) {
			e.printStackTrace();
			return Collections.emptyList();
		}

		Collections.sort(installedVersions, new VersionComparator());
		Collections.reverse(installedVersions);
		return installedVersions;
	}

	public List<File> getVersionsToBeDeleted(List<File> installedVersions) {
		if (installedVersions.size() > getNumVersionsToKeep()) {
			return installedVersions.subList(getNumVersionsToKeep(), installedVersions.size());
		} else {
			return Collections.emptyList();
		}
	}

	public boolean deleteVersions(List<File> versionsToBeDeleted) {
		boolean success = true;
		for (File versionToBeDeleted : versionsToBeDeleted) {
			success = success && deleteVersion(versionToBeDeleted);
		}
		return success;
	}

	private boolean deleteVersion(File versionToBeDeleted) {
		try {
			FileUtils.deleteDirectory(versionToBeDeleted);
			return true;
		} catch (Exception e) {
			LOGGER.warning("Error while deleting: " + e);
			return false;
		}
	}

	public int getNumVersionsToKeep() {
		return numVersionsToKeep;
	}

	public void setNumVersionsToKeep(int numVersionsToKeep) {
		if (numVersionsToKeep > 0) {
			this.numVersionsToKeep = numVersionsToKeep;
		}
	}
}
