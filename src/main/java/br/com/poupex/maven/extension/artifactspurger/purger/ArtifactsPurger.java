package br.com.poupex.maven.extension.artifactspurger.purger;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import br.com.poupex.maven.extension.artifactspurger.util.DirectoriesUtils;
import br.com.poupex.maven.extension.artifactspurger.util.VersionFileComparator;
import org.apache.commons.io.FileUtils;

import static br.com.poupex.maven.extension.artifactspurger.logging.LogConfiguration.LOGGER;
import static java.util.Collections.reverseOrder;
import static java.util.logging.Level.WARNING;

public class ArtifactsPurger {

	protected static final int MIN_VERSIONS_TO_KEEP = 1;
	protected static final int NUM_VERSIONS_TO_KEEP = 10;
	protected static final int MAX_VERSIONS_TO_KEEP = 100;
	protected static final String NUM_VERSIONS_TO_KEEP_PROP = "artifactspurger.numVersionsToKeep";

	public static void purge(File artifactGroupIdDirectory) {
		ArtifactsPurger purger = new ArtifactsPurger();
		List<File> installedVersions = purger.findInstalledVersions(artifactGroupIdDirectory);
		List<File> versionsToBeDeleted = purger.getVersionsToBeDeleted(installedVersions);
		purger.deleteVersions(versionsToBeDeleted);
	}

	protected List<File> findInstalledVersions(File artifactIdDirectory) {
		List<File> installedVersions = new ArrayList<File>();
		try {
			installedVersions.addAll(DirectoriesUtils.list(artifactIdDirectory));
			Collections.sort(installedVersions, reverseOrder(new VersionFileComparator()));
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
			return (numVersionsToKeep < MIN_VERSIONS_TO_KEEP || numVersionsToKeep > MAX_VERSIONS_TO_KEEP)
				? NUM_VERSIONS_TO_KEEP : numVersionsToKeep;
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
