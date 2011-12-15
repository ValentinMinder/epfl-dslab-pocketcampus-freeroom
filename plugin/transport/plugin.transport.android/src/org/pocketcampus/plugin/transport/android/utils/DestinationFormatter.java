package org.pocketcampus.plugin.transport.android.utils;

import java.util.HashMap;

import org.pocketcampus.plugin.transport.shared.TransportStation;

/**
 * This class formats some destination names that we know are not named nicely.
 * We can add some whenever needed.
 * 
 * @author Florian <florian.laurent@epfl.ch>
 * @author Oriane <oriane.rodriguez@epfl.ch>
 */
public class DestinationFormatter {
	private static HashMap<String, String> niceNames_;

	/**
	 * Initialize the hash map containing the bad names and corresponding nice
	 * names.
	 */
	private static void inititializeList() {
		if (niceNames_ != null) {
			return;			
		}
		niceNames_ = new HashMap<String, String>();
		niceNames_.put("Ecublens VD, EPFL", "EPFL");
		niceNames_.put("Lausanne, Vigie", "Vigie");
	}

	/**
	 * A way to call the <code>getNiceName(String locationName)</code> method
	 * with a Location.
	 * 
	 * @param location
	 *            The Location for which we want a nice name
	 * @return the result of the <code>getNiceName(String locationName)</code>
	 *         method called with the location's name.
	 */
	public static String getNiceName(TransportStation location) {
		return getNiceName(location.name);
	}

	/**
	 * Returns the nice name of the location if it was in the hash map, doesn't
	 * change anything otherwise.
	 * 
	 * @param locationName
	 *            The Location for which we want a nice name
	 * @return locationName formatted
	 */
	public static String getNiceName(String locationName) {
		inititializeList();
		if (niceNames_.containsKey(locationName)) {
			return niceNames_.get(locationName);
		}
		return locationName;
	}
}