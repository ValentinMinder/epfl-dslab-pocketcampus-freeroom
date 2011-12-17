package org.pocketcampus.plugin.transport.android.utils;

import java.util.HashMap;

/**
 * This class stores how we want to display the metro. The server sends us
 * "UMetm1" or "UMetm2" and we change it to simply "M1" or "M2". It is a special
 * case so we just have no other clever way to do this.
 * 
 * @author Florian <florian.laurent@epfl.ch>
 * @author Oriane <oriane.rodriguez@epfl.ch>
 */
public class TransportFormatter {
	static HashMap<String, String> niceNames_;

	/**
	 * Initialize the hash map containing the two names
	 */
	private static void initialize() {
		niceNames_ = new HashMap<String, String>();
		niceNames_.put("UMetm1", "M1");
		niceNames_.put("UMetm2", "M2");
	}

	/**
	 * returns the nice name corresponding to the not formatted name if the
	 * argument was "UMetm1"or "UMetm2".
	 * 
	 * @param lineName
	 *            the string we want to format
	 * @return lineName the same string formatted
	 */
	public static String getNiceName(String lineName) {
		if (niceNames_ == null) {
			initialize();
		}
		if (niceNames_.containsKey(lineName)) {
			return niceNames_.get(lineName);
		}

		if (lineName.contains("Bus")) {
			int index = lineName.indexOf("Bus");
			return "Bus " + lineName.substring(index + 3);
		}
		
		if (lineName.contains("Tram")) {
			int index = lineName.indexOf("Tram");
			return "Tram " + lineName.substring(index + 3);
		}
		
		while (lineName.matches(".*(1).*")) {
			String[] s = lineName.split("1");
			lineName = s[0];
		}
		while (lineName.matches(".*(2).*")) {
			String[] s = lineName.split("2");
			lineName = s[0]; 
		}
		while (lineName.matches(".*(3).*")) {
			String[] s = lineName.split("3");
			lineName = s[0];
		}
		while (lineName.matches(".*(4).*")) {
			String[] s = lineName.split("4");
			lineName = s[0];
		}
		while (lineName.matches(".*(5).*")) {
			String[] s = lineName.split("5");
			lineName = s[0]; 
		}
		while (lineName.matches(".*(6).*")) {
			String[] s = lineName.split("6");
			lineName = s[0]; 
		}
		while (lineName.matches(".*(7).*")) {
			String[] s = lineName.split("7");
			lineName = s[0];
		}
		while (lineName.matches(".*(8).*")) {
			String[] s = lineName.split("8");
			lineName = s[0];
		}
		while (lineName.matches(".*(9).*")) {
			String[] s = lineName.split("9");
			lineName = s[0];
		}

		return lineName;
	}

}