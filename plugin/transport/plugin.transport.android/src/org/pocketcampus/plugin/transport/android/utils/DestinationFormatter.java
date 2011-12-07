package org.pocketcampus.plugin.transport.android.utils;

import java.util.HashMap;

import org.pocketcampus.plugin.transport.shared.Location;

public class DestinationFormatter {
	private static HashMap<String, String> niceNames_;

	static public String getNiceName(Location location) {
		return getNiceName(location.name);
	}

	public static String getNiceName(String locationName) {
		inititializeList();

		if (niceNames_.containsKey(locationName)) {
			return niceNames_.get(locationName);
		}

		return locationName;
	}

	private static void inititializeList() {
		if (niceNames_ != null)
			return;

		niceNames_ = new HashMap<String, String>();
		niceNames_.put("Ecublens VD, EPFL", "EPFL");
		niceNames_.put("Lausanne, Vigie", "Vigie");
	}
}