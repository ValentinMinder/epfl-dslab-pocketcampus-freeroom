package org.pocketcampus.shared.utils;

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
	
	/**
	 * Capitalize a String.
	 * @param
	 * @return
	 */
	public static String capitalize(String s) {
        if (s.length() == 0) return s;
        return s.substring(0, 1).toUpperCase() + s.substring(1).toLowerCase();
    }
}
