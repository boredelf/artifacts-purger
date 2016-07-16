package br.com.poupex.maven.extension.artifactspurger.util;

import java.io.File;
import java.util.Comparator;

public class VersionComparator implements Comparator<File> {

	/**
	 * Compares two version strings. Copied from:
	 * http://stackoverflow.com/questions/6701948/efficient-way-to-compare-version-strings-in-java
	 *
	 * Use this instead of String.compareTo() for a non-lexicographical
	 * comparison that works for version strings. e.g. "1.10".compareTo("1.6").
	 *
	 * It does not work if "1.10" is supposed to be equal to "1.10.0".
	 *
	 * @param version1 a string of ordinal numbers separated by decimal points.
	 * @param version2 a string of ordinal numbers separated by decimal points.
	 * @return The result is a negative integer if str1 is _numerically_ less than str2.
	 *         The result is a positive integer if str1 is _numerically_ greater than str2.
	 *         The result is zero if the strings are _numerically_ equal.
	 */
	@Override
	public int compare(File version1, File version2) {
		String[] vals1 = version1.getName().replaceAll("(?i)-SNAPSHOT", "").split("\\.");
		String[] vals2 = version2.getName().replaceAll("(?i)-SNAPSHOT", "").split("\\.");
		int i = 0;
		// set index to first non-equal ordinal or length of shortest version string
		while (i < vals1.length && i < vals2.length && vals1[i].equals(vals2[i])) {
			i++;
		}
		// compare first non-equal ordinal number
		if (i < vals1.length && i < vals2.length) {
			int diff = Integer.valueOf(vals1[i]).compareTo(Integer.valueOf(vals2[i]));
			return Integer.signum(diff);
		}
		// the strings are equal or one string is a substring of the other
		// e.g. "1.2.3" = "1.2.3" or "1.2.3" < "1.2.3.4"
		return Integer.signum(vals1.length - vals2.length);

	}

}