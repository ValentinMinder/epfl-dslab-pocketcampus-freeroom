package org.pocketcampus.platform.sdk.shared.utils;

import java.text.Normalizer;
import java.util.LinkedList;
import java.util.regex.Pattern;

public class StringUtils {

	/**
	 * Capitalize a String.
	 * @param
	 * @return
	 */
	public static String capitalize(String s) {
		if (s.length() == 0) return s;
		return s.substring(0, 1).toUpperCase() + s.substring(1).toLowerCase();
	}

	public static String removeAccents(String string) {
		String temp = Normalizer.normalize(string, Normalizer.Form.NFD);
		Pattern pattern = Pattern.compile("[\\p{InCombiningDiacriticalMarks}\\p{IsLm}\\p{IsSk}]+");
		return pattern.matcher(temp).replaceAll("");
	}

	public static String getSubstringBetween(String orig, String before, String after) {
		int b = orig.indexOf(before);
		if(b != -1) {
			orig = orig.substring(b + before.length());
		}
		int a = orig.indexOf(after);
		if(a != -1) {
			orig = orig.substring(0, a);
		}
		return orig;
	}

	public static LinkedList<String> getAllSubstringsBetween(String orig, String before, String after) {
		LinkedList<String> ssl = new LinkedList<String>();
		if(orig.length() == 0 || before.length() == 0 || after.length() == 0)
			return ssl;
		while(true) {
			int b = orig.indexOf(before);
			if(b == -1)
				return ssl;
			int a = orig.indexOf(after, b + before.length());
			if(a == -1)
				return ssl;
			b = orig.lastIndexOf(before, a - before.length());
			ssl.add(orig.substring(b + before.length(), a));
			orig = orig.substring(a + after.length());
		}
	}
	
	public static LinkedList<String> getAllSubstringsBetween(String orig, String before, String middle, String after) {
		LinkedList<String> ssl = new LinkedList<String>();
		if(orig.length() == 0 || before.length() == 0 || middle.length() == 0 || after.length() == 0)
			return ssl;
		while(true) {
			int m = orig.indexOf(middle);
			if(m == -1)
				return ssl;
			int a = orig.indexOf(after, m + middle.length());
			if(a == -1)
				return ssl;
			int b = orig.lastIndexOf(before, m - before.length());
			if(b == -1)
				return ssl;
			ssl.add(orig.substring(b + before.length(), a));
			orig = orig.substring(a + after.length());
		}
	}
}













