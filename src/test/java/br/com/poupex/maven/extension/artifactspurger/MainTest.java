package br.com.poupex.maven.extension.artifactspurger;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
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

	public List<File> setUp(File dupeAppDir) throws Exception {
		if (!dupeAppDir.mkdir()) {
			throw new Exception("Impossible to setup test: error while creating dir.");
		}

		FileCreator.createDirWithEmptyFile(new File(dupeAppDir, "1.0.375"));
		FileCreator.createDirWithEmptyFile(new File(dupeAppDir, "1.0.190"));
		FileCreator.createDirWithEmptyFile(new File(dupeAppDir, "1.0.41"));
		FileCreator.createDirWithEmptyFile(new File(dupeAppDir, "1.0.40"));
		FileCreator.createDirWithEmptyFile(new File(dupeAppDir, "1.0.39"));
		FileCreator.createDirWithEmptyFile(new File(dupeAppDir, "1.0.38"));
		FileCreator.createDirWithEmptyFile(new File(dupeAppDir, "1.0.31"));
		FileCreator.createDirWithEmptyFile(new File(dupeAppDir, "1.0.27"));
		FileCreator.createDirWithEmptyFile(new File(dupeAppDir, "1.0.1"));

		List<File> versions = new ArrayList<>();
		versions.add(new File(dupeAppDir, "1.0.375"));
		versions.add(new File(dupeAppDir, "1.0.190"));
		versions.add(new File(dupeAppDir, "1.0.41"));
		versions.add(new File(dupeAppDir, "1.0.40"));
		versions.add(new File(dupeAppDir, "1.0.39"));
		versions.add(new File(dupeAppDir, "1.0.38"));
		versions.add(new File(dupeAppDir, "1.0.31"));
		versions.add(new File(dupeAppDir, "1.0.27"));
		versions.add(new File(dupeAppDir, "1.0.1"));

		return versions;
	}

	public void tearDown(File dupeAppDir) throws IOException {
		FileUtils.deleteDirectory(dupeAppDir);
	}

}