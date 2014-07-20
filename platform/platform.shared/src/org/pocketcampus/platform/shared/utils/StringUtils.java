package org.pocketcampus.platform.shared.utils;

import java.io.InputStream;
import java.text.Normalizer;
import java.util.LinkedList;
import java.util.Scanner;

/**
 * String utilities.
 */
public class StringUtils {
	/** Prints the specified InputStream to a string. */
	public static String fromStream(final InputStream stream, String charsetName) {
		Scanner scanner = null;
		try {
			scanner = new Scanner(stream, charsetName).useDelimiter("\\A");
			return scanner.hasNext() ? scanner.next() : "";
		} finally {
			if (scanner != null) {
				scanner.close();
			}
		}
	}

	/** Capitalizes the first letter of the specified string and makes the rest lowercase. */
	public static String capitalize(final String s) {
		if (s.length() == 0) {
			return s;
		}
		return s.substring(0, 1).toUpperCase() + s.substring(1).toLowerCase();
	}

	/** Removes accents in the specified string. */
	public static String removeAccents(String str) {
		return Normalizer.normalize(str, Normalizer.Form.NFD).replaceAll("[^\\p{ASCII}]", "");
	}

	/** Gets the substring between the specified guards.
	 *  Unlike the Apache Commons version, this doesn't return null if it can't find 'after'. */
	public static String getSubstringBetween(String orig, String before, String after) {
		int b = orig.indexOf(before);
		if (b != -1) {
			orig = orig.substring(b + before.length());
		}
		int a = orig.indexOf(after);
		if (a != -1) {
			orig = orig.substring(0, a);
		}
		return orig;
	}

	/** Gets all substrings between the specified guards. */
	public static LinkedList<String> getAllSubstringsBetween(String orig, String before, String after) {
		LinkedList<String> ssl = new LinkedList<String>();
		if (orig.length() == 0 || before.length() == 0 || after.length() == 0)
			return ssl;
		while (true) {
			int b = orig.indexOf(before);
			if (b == -1)
				return ssl;
			int a = orig.indexOf(after, b + before.length());
			if (a == -1)
				return ssl;
			b = orig.lastIndexOf(before, a - before.length());
			ssl.add(orig.substring(b + before.length(), a));
			orig = orig.substring(a + after.length());
		}
	}

	/** Gets all substrings between the specified guards. */
	public static LinkedList<String> getAllSubstringsBetween(String orig, String before, String middle, String after) {
		LinkedList<String> ssl = new LinkedList<String>();
		if (orig.length() == 0 || before.length() == 0 || middle.length() == 0 || after.length() == 0)
			return ssl;
		while (true) {
			int m = orig.indexOf(middle);
			if (m == -1)
				return ssl;
			int a = orig.indexOf(after, m + middle.length());
			if (a == -1)
				return ssl;
			int b = orig.lastIndexOf(before, m - before.length());
			if (b == -1)
				return ssl;
			ssl.add(orig.substring(b + before.length(), a));
			orig = orig.substring(a + after.length());
		}
	}
}
