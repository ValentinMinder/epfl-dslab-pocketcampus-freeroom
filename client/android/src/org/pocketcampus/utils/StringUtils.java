package org.pocketcampus.utils;

public class StringUtils {
	/**
	 * Formats a number as a String of nbChar characters,
	 * padding it with 0.
	 * @param number
	 * @param nbChar
	 * @return
	 */
	public static String pad(int number, int nbChar) {
		return String.format("%0" + nbChar + "d", number);
	}
}
