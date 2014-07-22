package org.pocketcampus.plugin.transport.server;

import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import org.pocketcampus.platform.server.XElement;
import org.pocketcampus.plugin.transport.shared.TransportStation;

// The SBB HAFAS API's schema is available at http://fahrplan.sbb.ch/xsd/hafasXMLInterface.xsd

public final class HafasUtil {
	// Root element name
	private static final String ROOT_NAME = "ReqC";

	// Attributes of the root element
	private static final String ROOT_VERSION_ATTRIBUTE = "ver";
	private static final String ROOT_SOURCE_ATTRIBUTE = "prod";
	private static final String ROOT_LANGUAGE_ATTRIBUTE = "lang";
	private static final String ROOT_TOKEN_ATTRIBUTE = "accessId";

	// Properties of the CFF's HAFAS API
	private static final String HAFAS_VERSION = "3.2.3";
	private static final String HAFAS_LANGUAGE = "EN";
	private static final String HAFAS_NAME = "hafas";

	// Attributes of station elements
	private static final String STATION_ID_ATTRIBUTE = "externalStationNr";
	private static final String STATION_NAME_ATTRIBUTE = "name";
	private static final String STATION_LONGITUDE_ATTRIBUTE = "x";
	private static final String STATION_LATITUDE_ATTRIBUTE = "y";

	// EPFL-centered special names for common stations, and an inverted version
	private static final Map<String, String> SPECIAL_NAMES = new HashMap<String, String>();
	private static final Map<String, String> SPECIAL_NAMES_INVERTED = new HashMap<String, String>();

	static {
		// The entire M1 line goes into SPECIAL_NAMES for convenience
		
		// Lausanne-Flon is already a short name
		SPECIAL_NAMES.put("Lausanne, Vigie", "Vigie");
		SPECIAL_NAMES.put("Lausanne, Montelly", "Montelly");
		SPECIAL_NAMES.put("Lausanne, Malley", "Malley");
		SPECIAL_NAMES.put("Lansanne, Bourdonnette", "Bourdonnette");
		SPECIAL_NAMES.put("Chavannes-p.-R., UNIL-Dorigny", "UNIL-Dorigny");
		SPECIAL_NAMES.put("Chavannes-p.-R., UNIL-Mouline", "UNIL-Mouline");
		SPECIAL_NAMES.put("Ecublens VD, UNIL-Sorge", "UNIL-Sorge");
		SPECIAL_NAMES.put("Ecublens VD, EPFL", "EPFL");
		SPECIAL_NAMES.put("Ecublens VD, Bassenges", "Bassenges");
		SPECIAL_NAMES.put("Ecublens VD, Cerisaie", "Cerisaie");
		SPECIAL_NAMES.put("Chavannes-près-Renens, Crochy", "Crochy");
		SPECIAL_NAMES.put("Ecublens VD, Epenex", "Epenex");
		SPECIAL_NAMES.put("Renens VD, gare", "Renens gare");

		for (final Entry<String, String> entry : SPECIAL_NAMES.entrySet()) {
			SPECIAL_NAMES_INVERTED.put(entry.getValue(), entry.getKey());
		}
	}

	/** Gets the root for requests to HAFAS. */
	public static XElement createRequestRoot(final String token) {
		return XElement.create(ROOT_NAME)
				.setAttribute(ROOT_VERSION_ATTRIBUTE, HAFAS_VERSION)
				.setAttribute(ROOT_LANGUAGE_ATTRIBUTE, HAFAS_LANGUAGE)
				.setAttribute(ROOT_SOURCE_ATTRIBUTE, HAFAS_NAME)
				.setAttribute(ROOT_TOKEN_ATTRIBUTE, token);
	}

	/** Parses a TransportStation from an XElement. */
	public static TransportStation parseStation(final XElement stationElem) {
		final int id = Integer.parseInt(stationElem.attribute(STATION_ID_ATTRIBUTE));
		final int latitude = Integer.parseInt(stationElem.attribute(STATION_LATITUDE_ATTRIBUTE));
		final int longitude = Integer.parseInt(stationElem.attribute(STATION_LONGITUDE_ATTRIBUTE));
		String name = stationElem.attribute(STATION_NAME_ATTRIBUTE);

		if (SPECIAL_NAMES.containsKey(name)) {
			name = SPECIAL_NAMES.get(name);
		}

		return new TransportStation(id, latitude, longitude, name);
	}

	/** Gets the full name of a station from name that may be a short name. */
	public static String getFullStationName(final String shortName) {
		if (SPECIAL_NAMES_INVERTED.containsKey(shortName)) {
			return SPECIAL_NAMES_INVERTED.get(shortName);
		}
		return shortName;
	}
}