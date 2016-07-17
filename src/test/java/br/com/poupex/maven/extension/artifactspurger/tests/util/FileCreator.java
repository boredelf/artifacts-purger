package br.com.poupex.maven.extension.artifactspurger.tests.util;

import java.io.File;

public abstract class FileCreator {

	public static boolean createDirWithEmptyFile(File dir) {
		try {
			File emptyFile = new File(dir, "empty-file.jar");
			return emptyFile.getParentFile().mkdirs() && emptyFile.createNewFile();
		} catch (Exception e) {
			e.printStackTrace(); // TODO Use logger.
			return false;
		}
	}

}
