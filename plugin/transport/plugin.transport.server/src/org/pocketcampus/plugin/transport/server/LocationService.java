package org.pocketcampus.plugin.transport.server;

import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;

import org.pocketcampus.platform.server.HttpClient;
import org.pocketcampus.platform.server.XElement;
import org.pocketcampus.plugin.transport.shared.TransportStation;

public final class LocationService {
	// For reference, the SBB API's schema is available at http://fahrplan.sbb.ch/xsd/hafasXMLInterface.xsd

	// API-related constants
	private static final String API_URL = "http://fahrplan.sbb.ch/bin/query.exe/dn";
	private static final Charset API_CHARSET = Charset.forName("ISO-8859-1");
	private static final String API_VERSION = "3.2.3";
	private static final String API_LANGUAGE = "EN";
	private static final String API_NAME = "hafas";

	// Root element name
	private static final String ROOT_NAME = "ReqC";

	// Attributes of the root element
	private static final String ROOT_VERSION_ATTRIBUTE = "ver";
	private static final String ROOT_SOURCE_ATTRIBUTE = "prod";
	private static final String ROOT_LANGUAGE_ATTRIBUTE = "lang";
	private static final String ROOT_TOKEN_ATTRIBUTE = "accessId";

	// Request element names
	private static final String REQUEST_CONTAINER_NAME = "LocValReq";
	private static final String REQUEST_CHILD_NAME = "ReqLoc";

	// Attributes of the request elements
	private static final String REQUEST_ID_ATTRIBUTE = "id";
	private static final String REQUEST_MAX_RESULTS_ATTRIBUTE = "maxNr";
	private static final String REQUEST_CHILD_TYPE_ATTRIBUTE = "type";
	private static final String REQUEST_CHILD_QUERY_ATTRIBUTE = "match";

	// Values for the request
	private static final String REQUEST_ID = "_"; // The ID must not be empty, but we're not using multiple requests so it can be anything
	private static final String REQUEST_MAX_RESULTS = "5"; // Between 1-50
	private static final String REQUEST_TYPE = "ST"; // Search for stations only

	// Attributes of the response elements
	private static final String RESPONSE_CONTAINER = "LocValRes";
	private static final String RESPONSE_STATION_ELEMENT = "Station";
	private static final String RESPONSE_STATION_ID_ATTRIBUTE = "externalStationNr";
	private static final String RESPONSE_STATION_NAME_ATTRIBUTE = "name";
	private static final String RESPONSE_STATION_LONGITUDE_ATTRIBUTE = "x";
	private static final String RESPONSE_STATION_LATITUDE_ATTRIBUTE = "y";

	private final HttpClient client;
	private final String token;

	public LocationService(HttpClient client, String token) {
		this.client = client;
		this.token = token;
	}

	// TODO remove this
	public static void main(String... args) throws Exception {
		List<TransportStation> stations = new LocationService(new org.pocketcampus.platform.server.HttpClientImpl(),
				"YJpyuPISerpXNNRTo50fNMP0yVu7L6IMuOaBgS0Xz89l3f6I3WhAjnto4kS9oz1").getMatchingLocations("Ecublens VD");

		for (TransportStation station : stations) {
			System.out.println(station.toString());
		}
	}

	public List<TransportStation> getMatchingLocations(final String query) throws Exception {
		XElement root = createRequestRoot(token);

		XElement container = root.addElement(REQUEST_CONTAINER_NAME)
				.setAttribute(REQUEST_ID_ATTRIBUTE, REQUEST_ID)
				.setAttribute(REQUEST_MAX_RESULTS_ATTRIBUTE, REQUEST_MAX_RESULTS);

		container.addElement(REQUEST_CHILD_NAME)
				.setAttribute(REQUEST_CHILD_TYPE_ATTRIBUTE, REQUEST_TYPE)
				.setAttribute(REQUEST_CHILD_QUERY_ATTRIBUTE, query);

		String requestXml = root.toString(API_CHARSET);
		String responseXml = client.post(API_URL, requestXml, API_CHARSET);
		XElement responseElem = XElement.parse(responseXml);

		List<TransportStation> result = new ArrayList<TransportStation>();
		
		for (XElement stationElem : responseElem.child(RESPONSE_CONTAINER).children(RESPONSE_STATION_ELEMENT)) {
			int id = Integer.parseInt(stationElem.attribute(RESPONSE_STATION_ID_ATTRIBUTE));
			int latitude = Integer.parseInt(stationElem.attribute(RESPONSE_STATION_LATITUDE_ATTRIBUTE));
			int longitude = Integer.parseInt(stationElem.attribute(RESPONSE_STATION_LONGITUDE_ATTRIBUTE));
			String name = stationElem.attribute(RESPONSE_STATION_NAME_ATTRIBUTE);

			result.add(new TransportStation(id, latitude, longitude, name));
		}

		return result;
	}

	private static XElement createRequestRoot(final String token) {
		return XElement.create(ROOT_NAME)
				.setAttribute(ROOT_VERSION_ATTRIBUTE, API_VERSION)
				.setAttribute(ROOT_LANGUAGE_ATTRIBUTE, API_LANGUAGE)
				.setAttribute(ROOT_SOURCE_ATTRIBUTE, API_NAME)
				.setAttribute(ROOT_TOKEN_ATTRIBUTE, token);
	}
}