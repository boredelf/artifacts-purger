package br.com.poupex.maven.extension.artifactspurger.purger;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.logging.Logger;

import br.com.poupex.maven.extension.artifactspurger.logging.LogConfiguration;
import br.com.poupex.maven.extension.artifactspurger.util.DirectoriesUtils;
import br.com.poupex.maven.extension.artifactspurger.util.VersionFileComparator;
import org.apache.commons.io.FileUtils;

import static java.util.logging.Level.WARNING;

public class ArtifactsPurger {

	private static final Logger LOGGER = LogConfiguration.getLogger(ArtifactsPurger.class);

	protected static final int NUM_VERSIONS_TO_KEEP = 10;
	protected static final String NUM_VERSIONS_TO_KEEP_PROP = "artifactspurger.numVersionsToKeep";

	public static void purge(File artifactGroupIdDirectory) {
		ArtifactsPurger purger = new ArtifactsPurger();
		List<File> installedVersions = purger.findInstalledVersions(artifactGroupIdDirectory);
		List<File> versionsToBeDeleted = purger.getVersionsToBeDeleted(installedVersions);
		purger.deleteVersions(versionsToBeDeleted);
	}

	// TODO: Create object InstalledVersion or return List<Artifact>?
	protected List<File> findInstalledVersions(File groupIdDir) {
		List<File> installedVersions = new ArrayList<File>();
		try {
			installedVersions.addAll(DirectoriesUtils.list(groupIdDir));
			Collections.sort(installedVersions, Collections.reverseOrder(new VersionFileComparator()));
		} catch (Exception e) {
			LOGGER.log(WARNING, "Error while listing installed versions.", e);
		}
		return installedVersions;
	}

	protected List<File> getVersionsToBeDeleted(List<File> installedVersions) {
		int numVersionsToKeep = getNumVersionsToKeep();
		return installedVersions.size() > numVersionsToKeep
			? installedVersions.subList(numVersionsToKeep, installedVersions.size())
			: Collections.<File>emptyList();
	}

	protected int getNumVersionsToKeep() {
		try {
			int numVersionsToKeep = Integer.parseInt(System.getProperty(NUM_VERSIONS_TO_KEEP_PROP));
			return (numVersionsToKeep < 1 || numVersionsToKeep > 100) ? NUM_VERSIONS_TO_KEEP : numVersionsToKeep;
		} catch (Exception e) {
			return NUM_VERSIONS_TO_KEEP;
		}
	}

	protected boolean deleteVersions(List<File> versionsToBeDeleted) {
		boolean hasDeletedAll = true;
		for (File versionToBeDeleted : versionsToBeDeleted) {
			hasDeletedAll = hasDeletedAll && deleteVersion(versionToBeDeleted);
		}
		return hasDeletedAll;
	}

	protected boolean deleteVersion(File versionToBeDeleted) {
		try {
			FileUtils.deleteDirectory(versionToBeDeleted);
			return true;
		} catch (Exception e) {
			LOGGER.log(WARNING, "Error while deleting version.", e);
			return false;
		}
	}

}
