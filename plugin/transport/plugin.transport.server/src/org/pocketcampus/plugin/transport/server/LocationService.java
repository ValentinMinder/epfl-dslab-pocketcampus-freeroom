package org.pocketcampus.plugin.transport.server;

import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;

import org.pocketcampus.platform.server.HttpClient;
import org.pocketcampus.platform.server.XElement;
import org.pocketcampus.plugin.transport.shared.*;

public final class LocationService {
	// For reference, the SBB HAFAS API's schema is available at http://fahrplan.sbb.ch/xsd/hafasXMLInterface.xsd

	// API-related constants
	private static final String API_URL = "http://fahrplan.sbb.ch/bin/query.exe/dn";
	private static final Charset API_CHARSET = Charset.forName("ISO-8859-1");

	// Request element names
	private static final String REQUEST_CONTAINER = "LocValReq";
	private static final String REQUEST_ELEMENT = "ReqLoc";

	// Attributes of the request elements
	private static final String REQUEST_CONTAINER_ID_ATTRIBUTE = "id";
	private static final String REQUEST_CONTAINER_MAX_RESULTS_ATTRIBUTE = "maxNr";
	private static final String REQUEST_TYPE_ATTRIBUTE = "type";
	private static final String REQUEST_QUERY_ATTRIBUTE = "match";

	// Values for the request
	private static final String REQUEST_ID = "_"; // The ID must not be empty, but we're not using multiple requests so it can be anything
	private static final int REQUEST_MAX_RESULTS = 5; // 1-50
	private static final String REQUEST_TYPE = "ST"; // Search for stations only; other options are POI, ADR (address) and ALLTYPES (all)

	// Attributes of the response elements
	private static final String RESPONSE_CONTAINER = "LocValRes";
	private static final String RESPONSE_STATION_ELEMENT = "Station";

	private final HttpClient client;
	private final String token;

	public LocationService(HttpClient client, String token) {
		this.client = client;
		this.token = token;
	}

	// TODO remove this
	public static void main(String... args) throws Exception {
		List<TransportStation> stations = new LocationService(new org.pocketcampus.platform.server.HttpClientImpl(),
				"YJpyuPISerpXNNRTo50fNMP0yVu7L6IMuOaBgS0Xz89l3f6I3WhAjnto4kS9oz1").searchStations("Ecublens VD");

		for (TransportStation station : stations) {
			System.out.println(station.toString());
		}
	}

	public TransportStation getStation(final String name) throws Exception {
		return searchStations(name, 1).get(0);
	}

	public List<TransportStation> searchStations(final String query) throws Exception {
		return searchStations(query, REQUEST_MAX_RESULTS);
	}

	private List<TransportStation> searchStations(final String query, final int count) throws Exception {
		XElement root = HafasUtil.createRequestRoot(token);

		XElement container = root.addElement(REQUEST_CONTAINER)
				.setAttribute(REQUEST_CONTAINER_ID_ATTRIBUTE, REQUEST_ID)
				.setAttribute(REQUEST_CONTAINER_MAX_RESULTS_ATTRIBUTE, Integer.toString(count));

		container.addElement(REQUEST_ELEMENT)
				.setAttribute(REQUEST_TYPE_ATTRIBUTE, REQUEST_TYPE)
				.setAttribute(REQUEST_QUERY_ATTRIBUTE, query);

		String requestXml = root.toString(API_CHARSET);
		String responseXml = client.post(API_URL, requestXml, API_CHARSET);
		XElement responseElem = XElement.parse(responseXml);

		List<TransportStation> result = new ArrayList<TransportStation>();

		for (XElement stationElem : responseElem.child(RESPONSE_CONTAINER).children(RESPONSE_STATION_ELEMENT)) {
			result.add(HafasUtil.parseStation(stationElem));
		}

		return result;
	}
}