package br.com.poupex.maven.extension.artifactspurger.util;

import java.io.File;
import java.util.Comparator;

import org.apache.maven.artifact.versioning.ComparableVersion;

public class VersionFileComparator implements Comparator<File> {

	@Override
	public int compare(File versionFile1, File versionFile2) {
		return new ComparableVersion(versionFile1.getName()).compareTo(new ComparableVersion(versionFile2.getName()));
	}
}
