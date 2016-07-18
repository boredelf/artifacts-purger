package br.com.poupex.maven.extension.artifactspurger.tests.util;

import java.io.File;
import java.util.logging.Logger;

import static java.util.logging.Level.WARNING;

public abstract class FileCreator {

	private static final Logger LOGGER = Logger.getLogger(FileCreator.class.getName());

	public static boolean createDirWithEmptyFile(File dir) {
		try {
			File emptyFile = new File(dir, "empty-file.jar");
			return emptyFile.getParentFile().mkdirs() && emptyFile.createNewFile();
		} catch (Exception e) {
			LOGGER.log(WARNING, "Error while creating dir.", e);
			return false;
		}
	}

}
