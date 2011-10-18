package org.pocketcampus.platform.sdk.shared.utils;

/**
 * Utility methods that can be used by all plugins.
 * 
 */
public class Utils {

	/**
	 * ASCII Parser
	 * 
	 * @param txt
	 *            the text that will be parsed
	 * @param ascii
	 *            the number of the special ascii you want to filter
	 * */
	public static boolean containsSpecialAscii(String txt, int ascii) {
		for (int i = 0; i < txt.length(); ++i) {
			char c = txt.charAt(i);
			int j = (int) c;
			if (j == ascii) {
				return true;
			}
		}
		return false;
	}

}
