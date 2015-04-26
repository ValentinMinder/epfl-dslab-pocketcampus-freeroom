package org.pocketcampus.plugin.transport.android.utils;

import java.util.HashMap;

import org.pocketcampus.plugin.transport.shared.TransportStation;

/**
 * Formats some stations names that we know are not named nicely. We can add any
 * station whenever needed.
 * 
 * @author Florian <florian.laurent@epfl.ch>
 * @author Oriane <oriane.rodriguez@epfl.ch>
 */
public class DestinationFormatter {
	private static HashMap<String, String> mNiceNames;

	/**
	 * Initializes the <code>HashMap</code> containing the bad names and
	 * corresponding nice names.
	 */
	private static void inititializeList() {
		if (mNiceNames != null) {
			return;
		}
		mNiceNames = new HashMap<String, String>();
		mNiceNames.put("Ecublens VD, EPFL", "EPFL");
		mNiceNames.put("Lausanne, Vigie", "Vigie");
	}

	/**
	 * A way to call the <code>getNiceName</code> method with a station.
	 * 
	 * @param station
	 *            The station for which we want the nice name.
	 * @return The result of the <code>getNiceName</code> method called with the
	 *         station's name.
	 */
	public static String getNiceName(TransportStation station) {
		return getNiceName(station.getName());
	}

	/**
	 * Returns the nice name of the station if it was in the list, doesn't
	 * change anything otherwise.
	 * 
	 * @param stationName
	 *            The station for which we want a nice name.
	 * @return stationName formatted.
	 */
	public static String getNiceName(String stationName) {
		inititializeList();
		if (mNiceNames.containsKey(stationName)) {
			return mNiceNames.get(stationName);
		}
		return stationName;
	}
}