package br.com.poupex.maven.extension.artifactspurger;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import br.com.poupex.maven.extension.artifactspurger.util.FileCreator;
import org.apache.commons.io.FileUtils;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class MainTest {

	private static File resourcesDir = new File(MainTest.class.getResource("/").getFile());

	@Test
	public void findInstalledVersions_shouldBeEqual() throws Exception {
		File dupeAppDir = new File(resourcesDir, "dupe-app-1");
		List<File> versions = setUp(dupeAppDir);

		assertEquals(versions, new Main().findInstalledVersions(dupeAppDir));

		tearDown(dupeAppDir);
	}

	@Test
	public void getVersionsToBeDeleted_shouldBeEqual() throws Exception {
		File dupeAppDir = new File(resourcesDir, "dupe-app-2");
		List<File> versions = setUp(dupeAppDir);

		int numVersionsToKeep = 3;
		Main main = new Main();
		main.setNumVersionsToKeep(numVersionsToKeep);
		assertEquals(versions.subList(numVersionsToKeep, versions.size()), main.getVersionsToBeDeleted(versions));

		tearDown(dupeAppDir);
	}

	@Test
	public void deleteVersions_shouldBeTrue() throws Exception {
		File dupeAppDir = new File(resourcesDir, "dupe-app-3");
		List<File> versions = setUp(dupeAppDir);

		Main main = new Main();
		main.setNumVersionsToKeep(3);
		assertTrue(main.deleteVersions(main.getVersionsToBeDeleted(versions)));

		tearDown(dupeAppDir);
	}

	// TODO: Improve setUp and tearDown methods.
	public List<File> setUp(File dupeAppDir) throws Exception {
		if (!dupeAppDir.mkdir()) {
			throw new Exception("Impossible to setup test: error while creating dir.");
		}

		List<File> versionsFiles = new ArrayList<>();
		// TODO: Include versions with words like -SNAPSHOT, -BRANCH, -RC, etc.
		List<String> versions = Arrays.asList(
			"1.0.375", "1.0.190", "1.0.41", "1.0.40", "1.0.39",
			"1.0.38", "1.0.31", "1.0.27", "1.0.1"
		);

		for (String version : versions) {
			File versionFile = new File(dupeAppDir, version);
			versionsFiles.add(versionFile);
			FileCreator.createDirWithEmptyFile(versionFile);
		}

		return versionsFiles;
	}

	public void tearDown(File dupeAppDir) throws IOException {
		FileUtils.deleteDirectory(dupeAppDir);
	}

}