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

import static java.util.logging.Level.INFO;
import static java.util.logging.Level.WARNING;

@Component(role = AbstractMavenLifecycleParticipant.class)
public class Main extends AbstractMavenLifecycleParticipant {

	private static final Logger LOGGER = Logger.getLogger(Main.class.getName());

	private int numVersionsToKeep = 3; // TODO: Parameterize using System property?

	@Override
	public void afterProjectsRead(MavenSession session) throws MavenExecutionException {
		super.afterProjectsRead(session);

		if (shouldExecute(session)) {
			try {
				this.configureLogger(session, LOGGER);
				this.execute(session);
			} catch (Exception e) {
				LOGGER.log(WARNING, "An error ocurred.", e);
			}
		}
	}

	// TODO: Improve to not depend on MavenSession
	private boolean shouldExecute(MavenSession session) {
		boolean shouldExecute = false;
		for (String targetGoal : Arrays.asList("install", "site", "deploy")) {
			if (session.getGoals().contains(targetGoal)) {
				shouldExecute = true;
			}
		}
		return shouldExecute;
	}

	// TODO: Move to another class?
	private void configureLogger(MavenSession session, Logger logger) throws IOException {
		String mavenHome = session.getSystemProperties().getProperty("maven.home"); // TODO: Do I need maven session?
		File logDir = new File(mavenHome + "/log/artifacts-purger/");
		if (logDir.exists() || logDir.mkdirs()) {
			FileHandler fileHandler = new FileHandler(logDir.getAbsolutePath() + "/artifacts-purger.%u.%g.log", 10_000_000, 30, true);
			fileHandler.setLevel(WARNING);
			fileHandler.setFormatter(new SimpleFormatter());
			logger.addHandler(fileHandler);
		}
	}

	// TODO: Try to diminish use of variables
	private void execute(MavenSession session) {
		ArtifactRepository repository = session.getLocalRepository();
		Artifact artifact = repository.find(session.getCurrentProject().getArtifact());
		File groupIdDir = artifact.getFile().getParentFile().getParentFile();

		List<File> installedVersions = findInstalledVersions(groupIdDir);
		List<File> versionsToBeDeleted = getVersionsToBeDeleted(installedVersions);

		deleteVersions(versionsToBeDeleted);
	}

	// TODO: Create object InstalledVersion or return List<Artifact>?
	public List<File> findInstalledVersions(File groupIdDir) {
		List<File> installedVersions = new ArrayList<>();

		try (DirectoryStream<Path> versions = Files.newDirectoryStream(groupIdDir.toPath())) {
			for (Path version : versions) {
				if (version.toFile().isDirectory()) {
					installedVersions.add(version.toFile());
				}
			}
		} catch (NoSuchFileException e) {
			LOGGER.log(INFO, "This artifact doesn't have versions installed.");
		} catch (IOException e) {
			LOGGER.log(WARNING, "Erro while listing installed versions.", e);
		}

		if (installedVersions.size() > 0) {
			Collections.sort(installedVersions, new VersionComparator());
			Collections.reverse(installedVersions);
		}

		return installedVersions;
	}

	public List<File> getVersionsToBeDeleted(List<File> installedVersions) {
		return installedVersions.size() > getNumVersionsToKeep()
			? installedVersions.subList(getNumVersionsToKeep(), installedVersions.size())
			: Collections.<File>emptyList();
	}

	public boolean deleteVersions(List<File> versionsToBeDeleted) {
		boolean hasDeletedAll = true;
		for (File versionToBeDeleted : versionsToBeDeleted) {
			hasDeletedAll = hasDeletedAll && deleteVersion(versionToBeDeleted);
		}
		return hasDeletedAll;
	}

	private boolean deleteVersion(File versionToBeDeleted) {
		try {
			FileUtils.deleteDirectory(versionToBeDeleted);
			return true;
		} catch (Exception e) {
			LOGGER.log(WARNING, "Error while deleting dir.", e);
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
