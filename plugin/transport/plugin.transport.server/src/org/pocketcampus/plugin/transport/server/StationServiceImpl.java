package org.pocketcampus.plugin.transport.server;

import org.pocketcampus.platform.server.HttpClient;
import org.pocketcampus.platform.server.XElement;
import org.pocketcampus.plugin.transport.shared.TransportGeoPoint;
import org.pocketcampus.plugin.transport.shared.TransportStation;

import java.io.IOException;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

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
	private static final String REQUEST_QUERY_WILDCARD_SUFFIX = "*"; // Required, otherwise the results are messed up

	// Attributes of the response elements
	private static final String RESPONSE_CONTAINER = "LocValRes";
	private static final String RESPONSE_ERROR_ELEMENT = "Err";
	private static final String RESPONSE_STATION_ELEMENT = "Station";

	private final HttpClient client;
	private final String token;

	public StationServiceImpl(final HttpClient client, final String token) {
		this.client = client;
		this.token = token;
	}

	/** Gets the station with the specified name, or null if no such station exists. */
	public TransportStation getStation(final String name) throws IOException {
		// TODO: Remove this and the method itself once we remove the deprecated stuff
		final String query = HafasUtil.getFullStationName(name);

		final List<TransportStation> result = findStations(query, 1, null);

		if (result.size() == 0) {
			return null;
		}

		if (result.get(0).getName().equals(name)) {
			return result.get(0);
		}

		return null;
	}

	/** Searches for stations by name using the specified query, optionally ordering them by their proximity to a point. */
	public List<TransportStation> findStations(final String query, final TransportGeoPoint location) throws IOException {
		return findStations(query, REQUEST_MAX_RESULTS, location);
	}

	/** Searches for stations by name using the specified query with the specified maximum number of results. */
	private List<TransportStation> findStations(final String query, final int maxResultsCount, final TransportGeoPoint location) throws IOException {
		final XElement request = buildRequest(token, query, maxResultsCount);
		final String responseXml = client.post(API_URL, request.toBytes(API_CHARSET), API_CHARSET);
		final List<TransportStation> stations = parseResponse(responseXml);

		if (location != null) {
			Collections.sort(stations, new Comparator<TransportStation>() {
				@Override
				public int compare(TransportStation station1, TransportStation station2) {
					final double dist1 = distanceBetween(
							station1.getLatitude() / 1000000.0, station1.getLongitude() / 1000000.0,
							location.getLatitude(), location.getLongitude());
					final double dist2 = distanceBetween(
							station2.getLatitude() / 1000000.0, station2.getLongitude() / 1000000.0,
							location.getLatitude(), location.getLongitude());

					return Double.compare(dist1, dist2);
				}
			});
		}

		return stations;
	}

	/** Builds the request XML. */
	private XElement buildRequest(final String token, final String query, final int maxResultsCount) {
		final XElement root = HafasUtil.createRequestRoot(token);

		final XElement container = root.addChild(REQUEST_CONTAINER)
				.setAttribute(REQUEST_CONTAINER_ID_ATTRIBUTE, REQUEST_ID)
				.setAttribute(REQUEST_CONTAINER_MAX_RESULTS_ATTRIBUTE, Integer.toString(maxResultsCount));

		container.addChild(REQUEST_ELEMENT)
				.setAttribute(REQUEST_TYPE_ATTRIBUTE, REQUEST_TYPE)
				.setAttribute(REQUEST_QUERY_ATTRIBUTE, query + REQUEST_QUERY_WILDCARD_SUFFIX);

		return root;
	}

	/** Parses the response XML. */
	private static List<TransportStation> parseResponse(final String responseXml) {
		final XElement responseElem = XElement.parse(responseXml);

		// haven't seen it in the wild, but the XSD allows it
		if (responseElem.child(RESPONSE_ERROR_ELEMENT) != null) {
			return new ArrayList<TransportStation>();
		}

		final XElement containerElem = responseElem.child(RESPONSE_CONTAINER);
		if (containerElem.child(RESPONSE_ERROR_ELEMENT) != null) {
			return new ArrayList<TransportStation>();
		}

		final List<TransportStation> result = new ArrayList<TransportStation>();
		for (XElement stationElem : containerElem.children(RESPONSE_STATION_ELEMENT)) {
			result.add(HafasUtil.parseStation(stationElem));
		}

		return result;
	}

	/** Approximates the distance between two lat/lon points on Earth. */
	private static double distanceBetween(final double lat1, final double lon1, final double lat2, final double lon2) {
		return Math.sqrt(Math.pow(lat2 - lat1, 2) + Math.pow(lon2 - lon1, 2));
	}
}