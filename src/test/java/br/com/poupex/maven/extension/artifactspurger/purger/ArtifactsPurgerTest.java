package br.com.poupex.maven.extension.artifactspurger.purger;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import br.com.poupex.maven.extension.artifactspurger.tests.util.FileCreator;
import org.apache.commons.io.FileUtils;
import org.junit.BeforeClass;
import org.junit.Test;

import static br.com.poupex.maven.extension.artifactspurger.purger.ArtifactsPurger.NUM_VERSIONS_TO_KEEP_PROP;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class ArtifactsPurgerTest {

	public static final int numVersionsToKeep = 3;
	private static File resourcesDir = new File(ArtifactsPurgerTest.class.getResource("/").getFile());

	@BeforeClass
	public static void beforeClass() {
		System.setProperty(NUM_VERSIONS_TO_KEEP_PROP, "" + numVersionsToKeep);
	}

	@Test
	public void findInstalledVersions_shouldBeEqual() throws Exception {
		File dupeAppDir = new File(resourcesDir, "dupe-app-1");
		List<File> versions = setUp(dupeAppDir);
		assertEquals(versions, new ArtifactsPurger().findInstalledVersions(dupeAppDir));
		tearDown(dupeAppDir);
	}

	@Test
	public void getVersionsToBeDeleted_shouldBeEqual() throws Exception {
		File dupeAppDir = new File(resourcesDir, "dupe-app-2");
		List<File> versions = setUp(dupeAppDir);

		assertEquals(
			versions.subList(numVersionsToKeep, versions.size()),
			new ArtifactsPurger().getVersionsToBeDeleted(versions)
		);

		tearDown(dupeAppDir);
	}

	@Test
	public void deleteVersions_shouldBeTrue() throws Exception {
		File dupeAppDir = new File(resourcesDir, "dupe-app-3");
		List<File> versions = setUp(dupeAppDir);

		ArtifactsPurger purger = new ArtifactsPurger();
		assertTrue(purger.deleteVersions(purger.getVersionsToBeDeleted(versions)));

		tearDown(dupeAppDir);
	}

	public List<File> setUp(File dupeAppDir) throws Exception {
		if (!dupeAppDir.mkdir()) {
			throw new Exception("Impossible to setup test: error while setting up.");
		}

		List<File> versionsFiles = new ArrayList<File>();
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