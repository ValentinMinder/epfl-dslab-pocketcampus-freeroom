package org.pocketcampus.plugin.transport.server;

import org.pocketcampus.platform.server.XElement;
import org.pocketcampus.plugin.transport.shared.TransportStation;

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
	

	/** Gets the root for requests to HAFAS. */
	public static XElement createRequestRoot(final String token) {
		return XElement.create(ROOT_NAME)
				.setAttribute(ROOT_VERSION_ATTRIBUTE, HAFAS_VERSION)
				.setAttribute(ROOT_LANGUAGE_ATTRIBUTE, HAFAS_LANGUAGE)
				.setAttribute(ROOT_SOURCE_ATTRIBUTE, HAFAS_NAME)
				.setAttribute(ROOT_TOKEN_ATTRIBUTE, token);
	}
	
	public static TransportStation parseStation(final XElement stationElem){
		int id = Integer.parseInt(stationElem.attribute(STATION_ID_ATTRIBUTE));
		int latitude = Integer.parseInt(stationElem.attribute(STATION_LATITUDE_ATTRIBUTE));
		int longitude = Integer.parseInt(stationElem.attribute(STATION_LONGITUDE_ATTRIBUTE));
		String name = stationElem.attribute(STATION_NAME_ATTRIBUTE);

		return new TransportStation(id, latitude, longitude, name);
	}
}