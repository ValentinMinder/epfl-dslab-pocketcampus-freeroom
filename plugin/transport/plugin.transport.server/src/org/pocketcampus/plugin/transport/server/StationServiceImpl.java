package org.pocketcampus.plugin.transport.server;

import java.io.IOException;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;

import org.pocketcampus.platform.server.HttpClient;
import org.pocketcampus.platform.server.XElement;
import org.pocketcampus.plugin.transport.shared.*;

/**
 * Implementation of StationService using the SBB's API (HAFAS).
 * 
 * @author Solal Pirelli <solal@pocketcampus.org>
 */
public final class StationServiceImpl implements StationService {
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

	public StationServiceImpl(HttpClient client, String token) {
		this.client = client;
		this.token = token;
	}

	// TODO remove this
	public static void main(String... args) throws Exception {
		List<TransportStation> stations = new StationServiceImpl(new org.pocketcampus.platform.server.HttpClientImpl(),
				"YJpyuPISerpXNNRTo50fNMP0yVu7L6IMuOaBgS0Xz89l3f6I3WhAjnto4kS9oz1")
		.findStations("Bourdonette");

		for (TransportStation station : stations) {
			System.out.println(station.toString());
		}
	}

	/** Gets the station with the specified name, or null if no such station exists. */
	public TransportStation getStation(final String name) throws IOException {
		List<TransportStation> result = findStations(name, 1);

		if (result.size() == 0) {
			return null;
		}

		if (result.get(0).getName().equals(name)) {
			return result.get(0);
		}

		return null;
	}

	/** Searches for stations by name using the specified query. */
	public List<TransportStation> findStations(final String query) throws IOException {
		return findStations(query, REQUEST_MAX_RESULTS);
	}

	
	/** Searches for stations by name using the specified query with the specified maximum number of results. */
	private List<TransportStation> findStations(final String query, final int maxResultsCount) throws IOException {
		XElement request = buildRequest(token, query, maxResultsCount);
		String responseXml = client.post(API_URL, request.toBytes(API_CHARSET), API_CHARSET);
		return parseResponse(responseXml);
	}

	/** Builds the request XML. */
	private XElement buildRequest(final String token, final String query, final int maxResultsCount) {
		XElement root = HafasUtil.createRequestRoot(token);

		XElement container = root.addChild(REQUEST_CONTAINER)
				.setAttribute(REQUEST_CONTAINER_ID_ATTRIBUTE, REQUEST_ID)
				.setAttribute(REQUEST_CONTAINER_MAX_RESULTS_ATTRIBUTE, Integer.toString(maxResultsCount));

		container.addChild(REQUEST_ELEMENT)
				.setAttribute(REQUEST_TYPE_ATTRIBUTE, REQUEST_TYPE)
				.setAttribute(REQUEST_QUERY_ATTRIBUTE, query);

		return root;
	}

	/** Parses the response XML. */
	private static List<TransportStation> parseResponse(final String responseXml) {
		XElement responseElem = XElement.parse(responseXml);

		List<TransportStation> result = new ArrayList<TransportStation>();

		for (XElement stationElem : responseElem.child(RESPONSE_CONTAINER).children(RESPONSE_STATION_ELEMENT)) {
			result.add(HafasUtil.parseStation(stationElem));
		}

		return result;
	}
}