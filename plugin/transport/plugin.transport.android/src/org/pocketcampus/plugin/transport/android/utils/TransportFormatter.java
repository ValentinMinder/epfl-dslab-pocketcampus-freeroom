package org.pocketcampus.plugin.transport.android.utils;

import java.util.HashMap;

/**
 * Stores how we want to display the metro, the trains and buses. The server
 * sends us "UMetm1" or "UMetm2" or any transport name and we change it to
 * simply "M1" or "M2" or a nicer name.
 * 
 * @author Florian <florian.laurent@epfl.ch>
 * @author Oriane <oriane.rodriguez@epfl.ch>
 * 
 */
public class TransportFormatter {
	static HashMap<String, String> mNiceNames;

	/**
	 * Initializes the <code>HashMap</code> containing the metro stations names.
	 */
	private static void initialize() {
		mNiceNames = new HashMap<String, String>();
		mNiceNames.put("UMetm1", "M1");
		mNiceNames.put("UMetm2", "M2");
	}

	/**
	 * returns the nice name corresponding to the not formatted station name. If
	 * the station name does not correspond to any of the tested names, it is
	 * not changed.
	 * 
	 * @param stationName
	 *            the <code>String</code> we want to format.
	 * @return lineName the same <code>String</code> formatted if needed.
	 */
	public static String getNiceName(String stationName) {
		if (mNiceNames == null) {
			initialize();
		}
		if (mNiceNames.containsKey(stationName)) {
			return mNiceNames.get(stationName);
		}

		if(stationName.contains("BUS"))
			return "Bus";
		
		// Keeps only the type "Bus" and its number
		if (stationName.contains("Bus")) {
			int index = stationName.indexOf("Bus");
			return "Bus " + stationName.substring(index + 3);
		}

		// Keeps only the type "Tram" and its nuber
		if (stationName.contains("Tram")) {
			int index = stationName.indexOf("Tram");
			return "Tram " + stationName.substring(index + 3);
		}

		// Keeps only the type "RE"
		if (stationName.contains("RE")) {
			return "RE";
		}

		// Keeps only the type "IR"
		if (stationName.contains("IR")) {
			return "IR";
		}

		// Then gets rid of any other nuber which are only the id of the train
		// or bus and that we don't need to display.
		while (stationName.matches(".*(1).*")) {
			String[] s = stationName.split("1");
			stationName = s[0];
		}
		while (stationName.matches(".*(2).*")) {
			String[] s = stationName.split("2");
			stationName = s[0];
		}
		while (stationName.matches(".*(3).*")) {
			String[] s = stationName.split("3");
			stationName = s[0];
		}
		while (stationName.matches(".*(4).*")) {
			String[] s = stationName.split("4");
			stationName = s[0];
		}
		while (stationName.matches(".*(5).*")) {
			String[] s = stationName.split("5");
			stationName = s[0];
		}
		while (stationName.matches(".*(6).*")) {
			String[] s = stationName.split("6");
			stationName = s[0];
		}
		while (stationName.matches(".*(7).*")) {
			String[] s = stationName.split("7");
			stationName = s[0];
		}
		while (stationName.matches(".*(8).*")) {
			String[] s = stationName.split("8");
			stationName = s[0];
		}
		while (stationName.matches(".*(9).*")) {
			String[] s = stationName.split("9");
			stationName = s[0];
		}

		if (stationName.startsWith("I")) {
			stationName = stationName.substring(1);
		}

		return stationName;
	}

}