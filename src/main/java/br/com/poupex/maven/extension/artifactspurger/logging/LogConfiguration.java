package br.com.poupex.maven.extension.artifactspurger.logging;

import java.util.logging.Logger;

public abstract class LogConfiguration {

	public static Logger getLogger(Class clazz) {
		Logger logger = Logger.getLogger(clazz.getName());

		/* TODO: Test.
		String mavenHome = System.getProperty("maven.home"); // TODO: Do I need maven session? Maybe.
		File logDir = new File(mavenHome + "/log/artifacts-purger/");

		if (logDir.exists() || logDir.mkdirs()) {
			try {
				FileHandler fileHandler = new FileHandler(logDir.getAbsolutePath() + "/artifacts-purger.%u.%g.log", 10000000, 30, true);
				fileHandler.setLevel(WARNING);
				fileHandler.setFormatter(new SimpleFormatter());
				logger.addHandler(fileHandler);
			} catch (Exception e) {
				logger.log(WARNING, "It wasn't possible to create log file.", e);
			}
		}
		*/

		return logger;
	}

}
