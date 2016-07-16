package br.com.poupex.maven.extension.artifactspurger.util;

import java.io.File;

public abstract class FileCreator {

	public static boolean createDirWithEmptyFile(File dir) {
		try {
			File emptyFile = new File(dir, "empty-file.jar");
			return emptyFile.getParentFile().mkdirs() && emptyFile.createNewFile();
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
	}

}
