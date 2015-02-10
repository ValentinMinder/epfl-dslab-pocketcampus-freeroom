package org.pocketcampus.plugin.transport.server;

import org.apache.commons.lang.WordUtils;
import org.joda.time.DateTime;
import org.joda.time.LocalDate;
import org.joda.time.Period;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;
import org.joda.time.format.PeriodFormatter;
import org.joda.time.format.PeriodFormatterBuilder;
import org.pocketcampus.platform.server.HttpClient;
import org.pocketcampus.platform.server.XElement;
import org.pocketcampus.plugin.transport.shared.TransportConnection;
import org.pocketcampus.plugin.transport.shared.TransportLine;
import org.pocketcampus.plugin.transport.shared.TransportStation;
import org.pocketcampus.plugin.transport.shared.TransportTrip;

import java.io.IOException;
import java.nio.charset.Charset;
import java.util.*;

/**
 * Implementation of TripsService using the SBB's API (HAFAS).
 * 
 * @author Solal Pirelli <solal@pocketcampus.org>
 */
public final class TripsServiceImpl implements TripsService {
	// API properties
	private static final String API_URL = "http://fahrplan.sbb.ch/bin/extxml.exe";
	private static final Charset API_CHARSET = Charset.forName("iso-8859-1");

	// Request element names
	private static final String REQUEST_CONTAINER = "ConReq";
	private static final String REQUEST_START_CONTAINER = "Start";
	private static final String REQUEST_STATION_ELEMENT = "Station";
	private static final String REQUEST_FILTER_ELEMENT = "Prod";
	private static final String REQUEST_END_CONTAINER = "Dest";
	private static final String REQUEST_DATETIME_ELEMENT = "ReqT";
	private static final String REQUEST_FLAGS_ELEMENT = "RFlags";

	// Attribute of the request elements
	private static final String REQUEST_STATION_ID_ATTRIBUTE = "externalId";
	private static final String REQUEST_FILTER_ATTRIBUTE = "prod";
	private static final String REQUEST_DATE_ATTRIBUTE = "date";
	private static final String REQUEST_TIME_ATTRIBUTE = "time";
	private static final String REQUEST_FLAG_PAST_RESULTS_COUNT_ATTRIBUTE = "b";
	private static final String REQUEST_FLAG_FUTURE_RESULTS_COUNT_ATTRIBUTE = "f";

	// Date/time formats for the request
	private static final DateTimeFormatter REQUEST_DATE_FORMAT = DateTimeFormat.forPattern("yyyyMMdd");
	private static final DateTimeFormatter REQUEST_TIME_FORMAT = DateTimeFormat.forPattern("HH:mm");

	// Values for the request
	private static final String REQUEST_FILTER_ALL = "1111111111111111"; // Undocumented. Each 'bit' enables/disables a mode of transport.
	private static final int REQUEST_PAST_RESULTS_COUNT = 0; // 0-6
	private static final int REQUEST_FUTURE_RESULTS_COUNT = 6; // 0-6

	// Response element names
	private static final String RESPONSE_CONTAINER = "ConRes";
	private static final String RESPONSE_ERROR_ELEMENT = "Err";
	private static final String RESPONSE_TRIPS_ELEMENT = "ConnectionList";
	private static final String RESPONSE_TRIP_ELEMENT = "Connection";
	private static final String RESPONSE_TRIP_DATE_CONTAINER_ELEMENT = "Overview";
	private static final String RESPONSE_TRIP_DATE_ELEMENT = "Date";
	private static final String RESPONSE_TRIP_CONNECTIONS_ELEMENT = "ConSectionList";
	private static final String RESPONSE_TRIP_CONNECTION_ELEMENT = "ConSection";
	private static final String RESPONSE_TRIP_CONNECTION_DEPARTURE_ELEMENT = "Departure";
	private static final String RESPONSE_TRIP_CONNECTION_ARRIVAL_ELEMENT = "Arrival";
	private static final String[] RESPONSE_TRIP_CONNECTION_WALK_ELEMENTS = { "Walk", "Transfer", "GisRoute" };
	private static final String RESPONSE_TRIP_CONNECTION_WALK_DURATION_ELEMENT = "Duration";
	private static final String RESPONSE_TRIP_CONNECTION_WALK_DURATION_TIME_ATTRIBUTE = "Time";
	private static final String RESPONSE_TRIP_PROPERTIES_CONTAINER = "Journey";
	private static final String RESPONSE_TRIP_PROPERTIES_ELEMENT = "JourneyAttributeList";

	// HAFAS stop element names (in the response)
	private static final String STOP_ELEMENT = "BasicStop";
	private static final String STOP_STATION_ELEMENT = "Station";
	private static final String STOP_DEPARTURE_FLAGS_ELEMENT = "Dep";
	private static final String STOP_ARRIVAL_FLAGS_ELEMENT = "Arr";
	private static final String STOP_FLAGS_TIME_ELEMENT = "Time";
	private static final String STOP_FLAGS_PLATFORM_CONTAINER = "Platform";
	private static final String STOP_FLAGS_PLATFORM_ELEMENT = "Text";

	// HAFAS trip property element and attributes names (in the response)
	private static final String TRIP_PROPERTY_CONTAINER = "JourneyAttribute";
	private static final String TRIP_PROPERTY_ELEMENT = "Attribute";
	private static final String TRIP_PROPERTY_NAME_ATTRIBUTE = "type";
	private static final String TRIP_PROPERTY_VALUE_CONTAINER = "AttributeVariant";
	private static final String TRIP_PROPERTY_VALUE_CONTAINER_TYPE_ATTRIBUTE = "type";
	private static final String TRIP_PROPERTY_VALUE_CONTAINER_TYPE_NORMAL = "NORMAL";
	private static final String TRIP_PROPERTY_VALUE_ELEMENT = "Text";

	// Values to find in the trip properties
	private static final String TRIP_PROPERTY_LINE_NAME = "CATEGORY";
	private static final String TRIP_PROPERTY_LINE_NUMBER = "LINE";

	// Special line values
	private static final Set<String> LINE_NAMES_WITH_NUMBERS = new HashSet<>(Arrays.asList("M", "S"));

	// Placeholder if a line name cannot be found
	private static final String EMPTY_LINE_PLACEHOLDER = "???";

	// Date/time formats in the response
	private static final DateTimeFormatter RESPONSE_DATE_FORMAT = DateTimeFormat.forPattern("yyyyMMdd");
	private static final PeriodFormatter RESPONSE_PERIOD_FORMAT =
			new PeriodFormatterBuilder()
					.appendDays().appendSuffix("d")
					.appendHours().appendSuffix(":")
					.appendMinutes().appendSuffix(":")
					.appendSeconds()
					.toFormatter();

	private final HttpClient client;
	private final String token;

	public TripsServiceImpl(HttpClient client, String token) {
		this.client = client;
		this.token = token;
	}

	/** Gets trips from the specified station, to the specified station, at the specified date and time. */
	public List<TransportTrip> getTrips(final TransportStation start, final TransportStation end,
			final DateTime datetime) throws IOException {
		final XElement request = buildRequest(token, start, end, datetime);
		final String responseXml = client.post(API_URL, request.toBytes(API_CHARSET), API_CHARSET);
		return parseResponse(responseXml);
	}

	/** Builds the request XML. */
	private static XElement buildRequest(final String token, final TransportStation start, final TransportStation end,
			final DateTime tripDeparture) {
		final XElement root = HafasUtil.createRequestRoot(token);

		final XElement container = root.addChild(REQUEST_CONTAINER);

		final XElement startElem = container.addChild(REQUEST_START_CONTAINER);

		startElem.addChild(REQUEST_STATION_ELEMENT)
				.setAttribute(REQUEST_STATION_ID_ATTRIBUTE, Integer.toString(start.getId()));

		startElem.addChild(REQUEST_FILTER_ELEMENT)
				.setAttribute(REQUEST_FILTER_ATTRIBUTE, REQUEST_FILTER_ALL);

		container.addChild(REQUEST_END_CONTAINER)
				.addChild(REQUEST_STATION_ELEMENT)
				.setAttribute(REQUEST_STATION_ID_ATTRIBUTE, Integer.toString(end.getId()));

		container.addChild(REQUEST_DATETIME_ELEMENT)
				.setAttribute(REQUEST_DATE_ATTRIBUTE, REQUEST_DATE_FORMAT.print(tripDeparture))
				.setAttribute(REQUEST_TIME_ATTRIBUTE, REQUEST_TIME_FORMAT.print(tripDeparture));

		container
				.addChild(REQUEST_FLAGS_ELEMENT)
				.setAttribute(REQUEST_FLAG_PAST_RESULTS_COUNT_ATTRIBUTE, Integer.toString(REQUEST_PAST_RESULTS_COUNT))
				.setAttribute(REQUEST_FLAG_FUTURE_RESULTS_COUNT_ATTRIBUTE,
						Integer.toString(REQUEST_FUTURE_RESULTS_COUNT));

		return root;
	}

	/** Parses the response XML. */
	private static List<TransportTrip> parseResponse(final String responseXml) {
		final XElement responseElem = XElement.parse(responseXml);
		if (responseElem.child(RESPONSE_ERROR_ELEMENT) != null) {
			return new ArrayList<>();
		}

		final XElement containerElem = responseElem.child(RESPONSE_CONTAINER);
		// haven't seen it in the wild, but the XSD allows it
		if (containerElem.child(RESPONSE_ERROR_ELEMENT) != null) {
			return new ArrayList<>();
		}

		final List<TransportTrip> trips = new ArrayList<>();
		int id = 0;

		for (final XElement tripElem : containerElem.child(RESPONSE_TRIPS_ELEMENT).children(RESPONSE_TRIP_ELEMENT)) {
			trips.add(parseTrip(tripElem, id));
			id++;
		}

		return trips;
	}

	/** Parses a TransportTrip from the specified XElement. */
	private static TransportTrip parseTrip(final XElement tripElem, final int id) {
		final List<TransportConnection> connections = new ArrayList<>();

		final XElement dateElem = tripElem.child(RESPONSE_TRIP_DATE_CONTAINER_ELEMENT)
				.child(RESPONSE_TRIP_DATE_ELEMENT);
		final LocalDate connectionDate = RESPONSE_DATE_FORMAT.parseLocalDate(dateElem.text());

		final XElement connectionsListElem = tripElem.child(RESPONSE_TRIP_CONNECTIONS_ELEMENT);
		for (final XElement connectionElem : connectionsListElem.children(RESPONSE_TRIP_CONNECTION_ELEMENT)) {
			connections.add(parseConnection(connectionElem, connectionDate));
		}

		final TransportStation start = connections.get(0).getDeparture();
		final TransportStation end = connections.get(connections.size() - 1).getArrival();

		final long departureTime = connections.get(0).getDepartureTime();
		final long arrivalTime = connections.get(connections.size() - 1).getArrivalTime();

		return new TransportTrip(Integer.toString(id), departureTime, arrivalTime, start, end, connections);
	}

	/** Parses a TransportConnection from the specified XElement. */
	private static TransportConnection parseConnection(final XElement connectionElem, final LocalDate connectionDate) {
		final XElement departureElem = connectionElem.child(RESPONSE_TRIP_CONNECTION_DEPARTURE_ELEMENT);
		final HafasTransportStop departureStop = parseStop(departureElem, true, connectionDate);

		final XElement arrivalElem = connectionElem.child(RESPONSE_TRIP_CONNECTION_ARRIVAL_ELEMENT);
		final HafasTransportStop arrivalStop = parseStop(arrivalElem, false, connectionDate);

		final XElement walkElement = getWalkElement(connectionElem);
		final boolean isWalk = walkElement != null;

		final TransportConnection connection = new TransportConnection(departureStop.station, arrivalStop.station,
				isWalk);

		connection.setDepartureTime(departureStop.datetime.getMillis());
		if (departureStop.platform != null) {
			connection.setDeparturePosition(departureStop.platform);
		}

		connection.setArrivalTime(arrivalStop.datetime.getMillis());
		if (arrivalStop.platform != null) {
			connection.setArrivalPosition(arrivalStop.platform);
		}

		if (isWalk) {
			final XElement timeElement = walkElement.child(RESPONSE_TRIP_CONNECTION_WALK_DURATION_ELEMENT);
			final Period walkTime = RESPONSE_PERIOD_FORMAT.parsePeriod(timeElement.child(
					RESPONSE_TRIP_CONNECTION_WALK_DURATION_TIME_ATTRIBUTE).text());

			connection.setFootDuration(walkTime.toStandardMinutes().getMinutes());
		} else {
			final XElement propertiesElem = connectionElem.child(RESPONSE_TRIP_PROPERTIES_CONTAINER).child(
					RESPONSE_TRIP_PROPERTIES_ELEMENT);
			final String lineName = getConnectionProperty(propertiesElem, TRIP_PROPERTY_LINE_NAME);
			final String lineNumber = getConnectionProperty(propertiesElem, TRIP_PROPERTY_LINE_NUMBER);
			final String fullLineName = getFullLineName(lineName, lineNumber);

			connection.setLine(new TransportLine(fullLineName, new ArrayList<String>()));
		}

		return connection;
	}

	/**
	 * Gets an XElement representing a walk section in the specified XElement representing a connection, or null if no
	 * such element is present.
	 */
	private static XElement getWalkElement(final XElement connectionElem) {
		// We consider any "implicit" element, i.e. "go to station XYZ in 42 minutes", as a walk, even though it might
		// be a very long walk.
		for (final String elementName : RESPONSE_TRIP_CONNECTION_WALK_ELEMENTS) {
			final XElement walkElement = connectionElem.child(elementName);
			if (walkElement != null) {
				return walkElement;
			}
		}
		return null;
	}

	/** Gets a full line name from the specified line name and number. */
	private static String getFullLineName(final String lineName, final String lineNumber) {
		// Should never happen, but we can't rule it out...
		if (lineName == null) {
			return EMPTY_LINE_PLACEHOLDER;
		}
		// Trains don't have line numbers
		if (lineNumber == null || lineNumber.equals("")) {
			return lineName;
		}
		// Special case for some lines (e.g. metro) whose number follows immediately, e.g. "M1" rather than "M 1".
		if (LINE_NAMES_WITH_NUMBERS.contains(lineName)) {
			return lineName + lineNumber;
		}
		// Normal case, e.g. "Bus 1"
		return WordUtils.capitalizeFully(lineName) + " " + lineNumber;
	}

	/** Parses a Stop from an XElement. */
	private static HafasTransportStop parseStop(final XElement stopElem, final boolean isDeparture,
			final LocalDate connectionDate) {
		final XElement container = stopElem.child(STOP_ELEMENT);
		final XElement flagsElem = container.child(isDeparture ? STOP_DEPARTURE_FLAGS_ELEMENT
				: STOP_ARRIVAL_FLAGS_ELEMENT);

		final TransportStation station = HafasUtil.parseStation(container.child(STOP_STATION_ELEMENT));

		String platform = flagsElem.child(STOP_FLAGS_PLATFORM_CONTAINER).child(STOP_FLAGS_PLATFORM_ELEMENT).text();
		if (platform.length() == 0) {
			platform = null;
		}

		final Period fromDayStart = RESPONSE_PERIOD_FORMAT.parsePeriod(flagsElem.child(STOP_FLAGS_TIME_ELEMENT).text());

		// Don't use toDateTimeAtStartOfDay().plus(fromDayStart), it will bug on DST changes
		// since some periods of time either exist twice (00:00 + 13h00 == 12:00) or don't exist (00:00 + 13h00 ==
		// 14:00)
		final DateTime datetime = new DateTime(
				connectionDate.getYear(), connectionDate.getMonthOfYear(), connectionDate.getDayOfMonth(),
				fromDayStart.getHours(), fromDayStart.getMinutes(), fromDayStart.getSeconds())
				.plusDays(fromDayStart.getDays());

		return new HafasTransportStop(station, datetime, platform);
	}

	/** Gets a connection property from the property list XElement. */
	private static String getConnectionProperty(final XElement propertyListElem, final String name) {
		for (final XElement containerElem : propertyListElem.children(TRIP_PROPERTY_CONTAINER)) {
			final XElement propertyElem = containerElem.child(TRIP_PROPERTY_ELEMENT);

			if (propertyElem.attribute(TRIP_PROPERTY_NAME_ATTRIBUTE).equals(name)) {
				for (final XElement valueElem : propertyElem.children(TRIP_PROPERTY_VALUE_CONTAINER)) {
					if (valueElem.attribute(TRIP_PROPERTY_VALUE_CONTAINER_TYPE_ATTRIBUTE).equals(
							TRIP_PROPERTY_VALUE_CONTAINER_TYPE_NORMAL)) {
						final XElement textElem = valueElem.child(TRIP_PROPERTY_VALUE_ELEMENT);
						return textElem == null ? "" : textElem.text();
					}
				}
			}
		}

		return null;
	}

	// Helper class to group some stuff together
	private static final class HafasTransportStop {
		public final TransportStation station;
		public final DateTime datetime;
		public final String platform;

		public HafasTransportStop(TransportStation station, DateTime datetime, String platform) {
			this.station = station;
			this.datetime = datetime;
			this.platform = platform;
		}
	}
}