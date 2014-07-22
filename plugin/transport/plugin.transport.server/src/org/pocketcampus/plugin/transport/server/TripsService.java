package org.pocketcampus.plugin.transport.server;

import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;

import org.joda.time.DateTime;
import org.joda.time.LocalDate;
import org.joda.time.Period;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;
import org.joda.time.format.PeriodFormatter;
import org.joda.time.format.PeriodFormatterBuilder;
import org.pocketcampus.platform.server.HttpClient;
import org.pocketcampus.platform.server.XElement;
import org.pocketcampus.plugin.transport.shared.*;

public final class TripsService {
	// API properties
	private static final String API_URL = "http://fahrplan.sbb.ch/bin/extxml.exe";
	private static final Charset API_CHARSET = Charset.forName("iso-8859-1");

	private static final String REQUEST_CONTAINER = "ConReq";
	private static final String REQUEST_START_CONTAINER = "Start";
	private static final String REQUEST_STATION_ELEMENT = "Station";
	private static final String REQUEST_FILTER_ELEMENT = "Prod";
	private static final String REQUEST_END_CONTAINER = "Dest";
	private static final String REQUEST_DATETIME_ELEMENT = "ReqT";
	private static final String REQUEST_FLAGS_ELEMENT = "RFlags";

	private static final String REQUEST_STATION_ID_ATTRIBUTE = "externalId";
	private static final String REQUEST_FILTER_ATTRIBUTE = "prod";
	private static final String REQUEST_DATE_ATTRIBUTE = "date";
	private static final String REQUEST_TIME_ATTRIBUTE = "time";
	private static final String REQUEST_FLAG_PAST_RESULTS_COUNT_ATTRIBUTE = "b";
	private static final String REQUEST_FLAG_FUTURE_RESULTS_COUNT_ATTRIBUTE = "f";

	private static final DateTimeFormatter REQUEST_DATE_FORMAT = DateTimeFormat.forPattern("yyyyMMdd");
	private static final DateTimeFormatter REQUEST_TIME_FORMAT = DateTimeFormat.forPattern("hh:mm");

	private static final String REQUEST_FILTER_ALL = "1111111111"; // Undocumented. Each 'bit' enables/disables a mode of transport.
	private static final int REQUEST_PAST_RESULTS_COUNT = 0; // 0-6
	private static final int REQUEST_FUTURE_RESULTS_COUNT = 6; // 0-6

	private static final String RESPONSE_CONTAINER = "ConRes";
	private static final String RESPONSE_TRIPS_ELEMENT = "ConnectionList";
	private static final String RESPONSE_TRIP_ELEMENT = "Connection";
	private static final String RESPONSE_TRIP_CONNECTIONS_ELEMENT = "ConSectionList";
	private static final String RESPONSE_TRIP_CONNECTION_ELEMENT = "ConSection";
	private static final String RESPONSE_TRIP_CONNECTION_DEPARTURE_ELEMENT = "Departure";
	private static final String RESPONSE_TRIP_CONNECTION_ARRIVAL_ELEMENT = "Arrival";
	private static final String RESPONSE_TRIP_CONNECTION_WALK_ELEMENT_1 = "Walk";
	private static final String RESPONSE_TRIP_CONNECTION_WALK_ELEMENT_2 = "Transfer";
	private static final String RESPONSE_TRIP_CONNECTION_WALK_ELEMENT_3 = "GisRoute";
	private static final String RESPONSE_TRIP_CONNECTION_WALK_DURATION_ELEMENT = "Duration";
	private static final String RESPONSE_TRIP_CONNECTION_WALK_DURATION_TIME_ATTRIBUTE = "Time";
	private static final String RESPONSE_TRIP_PROPERTIES_CONTAINER = "Journey";
	private static final String RESPONSE_TRIP_PROPERTIES_ELEMENT = "JourneyAttributeList";

	private static final String RESPONSE_STOP_ELEMENT = "BasicStop";
	private static final String RESPONSE_STOP_STATION_ELEMENT = "Station";
	private static final String RESPONSE_STOP_DEPARTURE_FLAGS_ELEMENT = "Dep";
	private static final String RESPONSE_STOP_ARRIVAL_FLAGS_ELEMENT = "Arr";
	private static final String RESPONSE_STOP_FLAGS_TIME_ELEMENT = "Time";
	private static final String RESPONSE_STOP_FLAGS_PLATFORM_CONTAINER = "Platform";
	private static final String RESPONSE_STOP_FLAGS_PLATFORM_ELEMENT = "Text";

	private static final String TRIP_PROPERTY_CONTAINER = "JourneyAttribute";
	private static final String TRIP_PROPERTY_ELEMENT = "Attribute";
	private static final String TRIP_PROPERTY_NAME_ATTRIBUTE = "type";
	private static final String TRIP_PROPERTY_VALUE_CONTAINER = "AttributeVariant";
	private static final String TRIP_PROPERTY_VALUE_CONTAINER_TYPE_ATTRIBUTE = "type";
	private static final String TRIP_PROPERTY_VALUE_CONTAINER_TYPE_NORMAL = "NORMAL";
	private static final String TRIP_PROPERTY_VALUE_ELEMENT = "Text";

	private static final String TRIP_PROPERTY_LINE_NAME = "CATEGORY";
	private static final String TRIP_PROPERTY_LINE_NUMBER = "LINE";
	private static final String TRIP_EMPTY_LINE_PLACEHOLDER = "???";

	private static final PeriodFormatter RESPONSE_DURATION_FORMAT =
			new PeriodFormatterBuilder()
					.appendDays().appendSuffix("d")
					.appendHours().appendSuffix(":")
					.appendMinutes().appendSuffix(":")
					.appendSeconds()
					.toFormatter();

	private final HttpClient client;
	private final String token;
	
	// TODO remove this
	public static void main(String... args) throws Exception {
		LocationService locServ=new LocationService(new org.pocketcampus.platform.server.HttpClientImpl(),
				"YJpyuPISerpXNNRTo50fNMP0yVu7L6IMuOaBgS0Xz89l3f6I3WhAjnto4kS9oz1");
		
		TransportStation epfl = locServ.getStation("Ecublens VD, EPFL");
		TransportStation flon = locServ.getStation("Lausanne-Flon");
		
		List<TransportTrip> trips = new TripsService(new org.pocketcampus.platform.server.HttpClientImpl(),
				"YJpyuPISerpXNNRTo50fNMP0yVu7L6IMuOaBgS0Xz89l3f6I3WhAjnto4kS9oz1").getConnections(epfl, flon, DateTime.now());

		for (TransportTrip trip : trips) {
			System.out.println(trip.toString());
		}
	}

	public TripsService(HttpClient client, String token) {
		this.client = client;
		this.token = token;
	}

	public List<TransportTrip> getConnections(TransportStation start, TransportStation end, DateTime datetime) throws Exception {
		XElement root = HafasUtil.createRequestRoot(token);

		XElement container = root.addElement(REQUEST_CONTAINER);

		XElement startElem = container.addElement(REQUEST_START_CONTAINER);

		startElem.addElement(REQUEST_STATION_ELEMENT)
				.setAttribute(REQUEST_STATION_ID_ATTRIBUTE, Integer.toString(start.getId()));

		startElem.addElement(REQUEST_FILTER_ELEMENT)
				.setAttribute(REQUEST_FILTER_ATTRIBUTE, REQUEST_FILTER_ALL);

		XElement endElem = container.addElement(REQUEST_END_CONTAINER);

		endElem.addElement(REQUEST_STATION_ELEMENT)
				.setAttribute(REQUEST_STATION_ID_ATTRIBUTE, Integer.toString(end.getId()));

		container.addElement(REQUEST_DATETIME_ELEMENT)
				.setAttribute(REQUEST_DATE_ATTRIBUTE, REQUEST_DATE_FORMAT.print(datetime))
				.setAttribute(REQUEST_TIME_ATTRIBUTE, REQUEST_TIME_FORMAT.print(datetime));

		container.addElement(REQUEST_FLAGS_ELEMENT)
				.setAttribute(REQUEST_FLAG_PAST_RESULTS_COUNT_ATTRIBUTE, Integer.toString(REQUEST_PAST_RESULTS_COUNT))
				.setAttribute(REQUEST_FLAG_FUTURE_RESULTS_COUNT_ATTRIBUTE, Integer.toString(REQUEST_FUTURE_RESULTS_COUNT));

		String requestXml = root.toString(API_CHARSET);
		String responseXml = client.post(API_URL, requestXml, API_CHARSET);
		XElement responseElem = XElement.parse(responseXml);

		XElement tripsListElem = responseElem.child(RESPONSE_CONTAINER).child(RESPONSE_TRIPS_ELEMENT);

		List<TransportTrip> trips = new ArrayList<TransportTrip>();
		int id = 0;

		for (XElement tripElem : tripsListElem.children(RESPONSE_TRIP_ELEMENT)) {
			List<TransportConnection> connections = new ArrayList<TransportConnection>();

			XElement connectionsListElem = tripElem.child(RESPONSE_TRIP_CONNECTIONS_ELEMENT);
			for (XElement connectionElem : connectionsListElem.children(RESPONSE_TRIP_CONNECTION_ELEMENT)) {
				XElement departureElem = connectionElem.child(RESPONSE_TRIP_CONNECTION_DEPARTURE_ELEMENT);
				HafasTransportStop departureStop = parseStop(departureElem, true, datetime.toLocalDate());

				XElement arrivalElem = connectionElem.child(RESPONSE_TRIP_CONNECTION_ARRIVAL_ELEMENT);
				HafasTransportStop arrivalStop = parseStop(arrivalElem, false, datetime.toLocalDate());

				XElement walkElement1 = connectionElem.child(RESPONSE_TRIP_CONNECTION_WALK_ELEMENT_1);
				XElement walkElement2 = connectionElem.child(RESPONSE_TRIP_CONNECTION_WALK_ELEMENT_2);
				XElement walkElement3 = connectionElem.child(RESPONSE_TRIP_CONNECTION_WALK_ELEMENT_3);
				XElement walkElement = walkElement1 == null ? walkElement2 == null ? walkElement3 : walkElement2 : walkElement1;
				if (walkElement == null) {
					TransportConnection connection = new TransportConnection(departureStop.station, arrivalStop.station, false);

					connection.setDepartureTime(departureStop.datetime.getMillis());
					if (departureStop.platform != null) {
						connection.setDeparturePosition(departureStop.platform);
					}

					connection.setArrivalTime(arrivalStop.datetime.getMillis());
					if (arrivalStop.platform != null) {
						connection.setArrivalPosition(arrivalStop.platform);
					}

					XElement propertiesElem = connectionElem.child(RESPONSE_TRIP_PROPERTIES_CONTAINER).child(RESPONSE_TRIP_PROPERTIES_ELEMENT);
					String lineName = getConnectionProperty(propertiesElem, TRIP_PROPERTY_LINE_NAME);
					String lineNumber = getConnectionProperty(propertiesElem, TRIP_PROPERTY_LINE_NUMBER);
					String fullLineName = lineName == null && lineNumber == null ? TRIP_EMPTY_LINE_PLACEHOLDER : lineName + " " + lineNumber;

					connection.setLine(new TransportLine(fullLineName, new ArrayList<String>()));

					connections.add(connection);
				} else {
					TransportConnection connection = new TransportConnection(departureStop.station, arrivalStop.station, true);

					XElement timeElement = walkElement.child(RESPONSE_TRIP_CONNECTION_WALK_DURATION_ELEMENT);
					Period walkTime = RESPONSE_DURATION_FORMAT.parsePeriod(timeElement.childText(RESPONSE_TRIP_CONNECTION_WALK_DURATION_TIME_ATTRIBUTE));

					connection.setFootDuration(walkTime.toStandardMinutes().getMinutes());

					connections.add(connection);
				}
			}

			long departureTime = connections.get(0).getDepartureTime();
			long arrivalTime = connections.get(connections.size() - 1).getArrivalTime();

			trips.add(new TransportTrip(Integer.toString(id), departureTime, arrivalTime, start, end, connections));
			id++;
		}

		return trips;
	}

	private static HafasTransportStop parseStop(final XElement stopElem, final boolean isDeparture, final LocalDate dayStart) {
		final XElement container = stopElem.child(RESPONSE_STOP_ELEMENT);
		final XElement flagsElem = container.child(isDeparture ? RESPONSE_STOP_DEPARTURE_FLAGS_ELEMENT : RESPONSE_STOP_ARRIVAL_FLAGS_ELEMENT);

		final TransportStation station = HafasUtil.parseStation(container.child(RESPONSE_STOP_STATION_ELEMENT));
		final Period fromDayStart = RESPONSE_DURATION_FORMAT.parsePeriod(flagsElem.childText(RESPONSE_STOP_FLAGS_TIME_ELEMENT));
		String platform = flagsElem.child(RESPONSE_STOP_FLAGS_PLATFORM_CONTAINER).childText(RESPONSE_STOP_FLAGS_PLATFORM_ELEMENT);

		if (platform.length() == 0) {
			platform = null;
		}

		final DateTime datetime = dayStart.toDateTimeAtStartOfDay().plus(fromDayStart);

		return new HafasTransportStop(station, datetime, platform);
	}

	private static String getConnectionProperty(final XElement propertyListElem, final String name) {
		for (XElement containerElem : propertyListElem.children(TRIP_PROPERTY_CONTAINER)) {
			XElement propertyElem = containerElem.child(TRIP_PROPERTY_ELEMENT);

			if (propertyElem.attribute(TRIP_PROPERTY_NAME_ATTRIBUTE).equals(name)) {
				for (XElement valueElem : propertyElem.children(TRIP_PROPERTY_VALUE_CONTAINER)) {
					if (valueElem.attribute(TRIP_PROPERTY_VALUE_CONTAINER_TYPE_ATTRIBUTE).equals(TRIP_PROPERTY_VALUE_CONTAINER_TYPE_NORMAL)) {
						return valueElem.childText(TRIP_PROPERTY_VALUE_ELEMENT);
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