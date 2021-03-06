package br.com.poupex.maven.extension.artifactspurger.util;

import java.io.File;
import java.io.FileFilter;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.apache.commons.io.filefilter.DirectoryFileFilter.INSTANCE;

public abstract class DirectoriesUtils {

	public static List<File> list(File parentDirectory) {
		return parentDirectory == null || !parentDirectory.exists()
			? Collections.<File>emptyList()
			: Arrays.asList(parentDirectory.listFiles((FileFilter) INSTANCE));
	}

}
