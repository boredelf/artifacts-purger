package br.com.poupex.maven.extension.artifactspurger.logging;

import java.io.File;
import java.util.logging.FileHandler;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.logging.SimpleFormatter;

import static java.lang.System.getProperty;
import static java.util.logging.Level.INFO;
import static java.util.logging.Level.WARNING;

public abstract class LogConfiguration {

	private static final Integer MB = 1048576;
	public static final Logger LOGGER = getLogger(LogConfiguration.class, "artifacts-purger", WARNING);

	protected static Logger getLogger(Class clazz, String logFilename, Level level) {
		Logger logger = Logger.getLogger(clazz.getName());

		File logDir = new File(getLogDir());
		if (logDir.exists() || logDir.mkdirs()) {
			try {
				String logFullFilename = logDir.getAbsolutePath() + "/" + logFilename + ".%g.%u.log";
				FileHandler fileHandler = new FileHandler(logFullFilename, 10 * MB, 30, true);
				fileHandler.setFormatter(new SimpleFormatter());
				fileHandler.setLevel(level);
				logger.addHandler(fileHandler);
			} catch (Exception e) {
				LOGGER.log(WARNING, "It wasn't possible to create log file.", e);
			}
		}

		return logger;
	}

	protected static String getLogDir() {
		String logDir;
		try {
			logDir = getProperty("maven.home", ".");
		} catch (Exception e) {
			LOGGER.log(INFO, "It wasn't possible to get log's dir.", e);
			logDir = ".";
		}
		return logDir + "/log";
	}

}
