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
		if(stationName.contains("BBus")){
			return stationName.replace("BBus", "Bus ");
		}

		return stationName;
	}

}