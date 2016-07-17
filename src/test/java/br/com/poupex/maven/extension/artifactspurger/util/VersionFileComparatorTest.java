package br.com.poupex.maven.extension.artifactspurger.util;

import java.io.File;

import org.junit.Assert;
import org.junit.Test;

public class VersionFileComparatorTest {

	private static final VersionFileComparator comparator = new VersionFileComparator();

	@Test
	public void number_shouldBeBiggerThan_rc() {
		Assert.assertTrue(comparator.compare(new File("1.0.0"), new File("1.0.0-RC")) > 0);
	}

	@Test
	public void number_shouldBeBiggerThan_beta() {
		Assert.assertTrue(comparator.compare(new File("1.0.0"), new File("1.0.0-beta")) > 0);
	}

	@Test
	public void number_shouldBeBiggerThan_alpha() {
		Assert.assertTrue(comparator.compare(new File("1.0.0"), new File("1.0.0-alpha")) > 0);
	}

	@Test
	public void number_shouldBeSmallerThan_branch() {
		Assert.assertTrue(comparator.compare(new File("1.0.0"), new File("1.0.0-issue-122")) < 0);
	}

}
