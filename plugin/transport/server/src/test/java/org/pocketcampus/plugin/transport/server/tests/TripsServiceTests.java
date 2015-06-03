package org.pocketcampus.plugin.transport.server.tests;

import org.joda.time.DateTime;
import org.junit.Test;
import org.pocketcampus.platform.server.HttpClient;
import org.pocketcampus.platform.shared.utils.StringUtils;
import org.pocketcampus.plugin.transport.server.TripsService;
import org.pocketcampus.plugin.transport.server.TripsServiceImpl;
import org.pocketcampus.plugin.transport.shared.TransportConnection;
import org.pocketcampus.plugin.transport.shared.TransportStation;
import org.pocketcampus.plugin.transport.shared.TransportTrip;

import javax.xml.XMLConstants;
import javax.xml.transform.Source;
import javax.xml.transform.stream.StreamSource;
import javax.xml.validation.SchemaFactory;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.nio.charset.Charset;
import java.util.List;
import java.util.Map;

import static org.junit.Assert.*;

public final class TripsServiceTests {
	@Test
	public void requestValidatesAgainstXsd() throws Exception {
		TestHttpClient client = new TestHttpClient("TripsReplyEpflFlon.xml");
		TripsService service = new TripsServiceImpl(client, "token");

		TransportStation from = new TransportStation().setName("X").setId(0);
		TransportStation to = new TransportStation().setName("Y").setId(1);
		DateTime now = DateTime.now();

		service.getTrips(from, to, now);

		Source schemaSource = new StreamSource(new StationServiceTests().getClass().getResourceAsStream("hafasXMLInterface.xsd"));
		Source requestSource = new StreamSource(new ByteArrayInputStream(client.lastSentBody));
		// this call throws if the request does not validate against the HAFAS XSD
		SchemaFactory.newInstance(XMLConstants.W3C_XML_SCHEMA_NS_URI).newSchema(schemaSource).newValidator().validate(requestSource);
	}

	// test the global parsing, as well as metro lines
	@Test
	public void metroTripEpflToFlon() throws IOException {
		TestHttpClient client = new TestHttpClient("TripsReplyEpflFlon.xml");
		TripsService service = new TripsServiceImpl(client, "token");

		TransportStation from = new TransportStation(8501214, 46522197, 6566143, "EPFL");
		TransportStation to = new TransportStation(8501181, 46520795, 6630344, "Lausanne-Flon");
		DateTime now = DateTime.now();

		List<TransportTrip> trips = service.getTrips(from, to, now);

		assertEquals("There should be 6 trips.",
				6, trips.size());

		TransportTrip trip = trips.get(0);

		assertEquals("There should be 2 parts in the first trip.",
				2, trip.getPartsSize());

		TransportStation dep = trip.getParts().get(0).getDeparture();

		assertEquals("The departure ID should be parsed correctly.",
				8501214, dep.getId());
		assertEquals("The departure name should be parsed correctly.",
				"EPFL", dep.getName());
		assertEquals("The departure latitude should be parsed correctly.",
				46522197, dep.getLatitude());
		assertEquals("The departure longitude should be parsed correctly.",
				6566143, dep.getLongitude());

		assertEquals("The first part's departure time should be parsed correctly (in the first trip).",
				new DateTime(2014, 07, 25, 12, 53, 00, 00).getMillis(), trip.getParts().get(0).getDepartureTime());
		assertEquals("The first trip's departure time should be the same as its first connection's.",
				trip.getParts().get(0).getDepartureTime(), trip.getDepartureTime());

		assertEquals("The first part's arrival time should be parsed correctly (in the first trip).",
				new DateTime(2014, 07, 25, 12, 56, 00, 00).getMillis(), trip.getParts().get(0).getArrivalTime());
		assertEquals("The first trip's arrival time should be the same as its last connection's.",
				trip.getParts().get(1).getArrivalTime(), trip.getArrivalTime());

		assertEquals("The first part's line should be parsed and converted correctly.",
				"M1", trip.getParts().get(0).getLine().getName());

		assertEquals("The first trip's departure should be the requested departure.",
				from, trip.getFrom());
		assertEquals("The first trip's arrival should be the requested arrival.",
				to, trip.getTo());

		assertNotSame("The first and second trips' IDs should be different.",
				trips.get(0).getId(), trips.get(1).getId());
	}

	// Bug #205: test connections late at night when the returned connections are the next day
	@Test
	public void lateConnections() throws IOException {
		TestHttpClient client = new TestHttpClient("TripsReplyEpflZurichLateAtNight.xml");
		TripsService service = new TripsServiceImpl(client, "token");

		TransportStation from = new TransportStation(8501214, 46522197, 6566143, "Ecublens VD, EPFL");
		TransportStation to = new TransportStation(8503000, 47378177, 8540192, "Zürich HB");
		DateTime now = DateTime.now();

		TransportTrip trip = service.getTrips(from, to, now).get(0);

		assertEquals("The trip's departure time should be correctly parsed.",
				new DateTime(2014, 9, 25, 05, 25, 00).getMillis(), trip.getDepartureTime());
		assertEquals("The trip's arrival time should be correctly parsed.",
				new DateTime(2014, 9, 25, 07, 56, 00).getMillis(), trip.getArrivalTime());
	}

	// test foot parts and no-number lines
	@Test
	public void trainTripLausanneToParis() throws IOException {
		TestHttpClient client = new TestHttpClient("TripsReplyLausanneParis.xml");
		TripsService service = new TripsServiceImpl(client, "token");

		TransportStation from = new TransportStation(8592050, 46517594, 6629670, "Lausanne, gare");
		TransportStation to = new TransportStation(8768600, 48843724, 2375947, "Paris-Gare de Lyon");
		DateTime now = DateTime.now();

		TransportTrip trip = service.getTrips(from, to, now).get(0);
		TransportConnection walk = trip.getParts().get(0);

		// NOTE: This is kind of wrong since it makes no sense to "walk from Lausanne, gare to Lausanne" to take a train, but HAFAS says so
		assertTrue("The first trip's first part should be a foot path.",
				walk.isFoot());
		assertEquals("The foot duration of the first trip's first part should be 4 minutes.",
				4, walk.getFootDuration());
		assertEquals("The first trip's first part's departure time should be parsed correctly.",
				new DateTime(2014, 07, 25, 06, 20, 00, 00).getMillis(), walk.getDepartureTime());
		assertEquals("The first trip's first part's arrival time should be parsed correctly.",
				new DateTime(2014, 07, 25, 06, 24, 00, 00).getMillis(), walk.getArrivalTime());

		assertEquals("The first trip's second part's line should be parsed and converted correctly.",
				"TGV", trip.getParts().get(1).getLine().getName());
	}

	// test bus lines with numbers
	@Test
	public void busTripGareToStFrancois() throws IOException {
		TestHttpClient client = new TestHttpClient("TripsReplyGareStFrancois.xml");
		TripsService service = new TripsServiceImpl(client, "token");

		TransportStation from = new TransportStation(8592050, 46517594, 6629670, "Lausanne, gare");
		TransportStation to = new TransportStation(8579254, 46519365, 6633481, "Lausanne, St-François");
		DateTime now = DateTime.now();

		TransportTrip trip = service.getTrips(from, to, now).get(1);

		assertEquals("The second trip's first part's line should be parsed and converted correctly.",
				"Bus 9", trip.getParts().get(0).getLine().getName());
	}

	// test errors
	@Test
	public void error() throws IOException {
		TestHttpClient client = new TestHttpClient("TripsReplyError.xml");
		TripsService service = new TripsServiceImpl(client, "token");

		TransportStation from = new TransportStation(8501214, 46522197, 6566143, "EPFL");
		TransportStation to = new TransportStation(8501181, 46520795, 6630344, "Lausanne-Flon");
		DateTime now = DateTime.now();

		List<TransportTrip> trips = service.getTrips(from, to, now);

		assertEquals("There should be no trips in case of an error.",
				0, trips.size());
	}

	// test time parsing on days where the DST changes
	@Test
	public void dstChange() throws IOException {
		TestHttpClient client = new TestHttpClient("TripsReplyEpflFlonOnDstChange.xml");
		TripsService service = new TripsServiceImpl(client, "token");

		TransportStation from = new TransportStation(8501214, 46522197, 6566143, "EPFL");
		TransportStation to = new TransportStation(8501181, 46520795, 6630344, "Lausanne-Flon");
		DateTime now = new DateTime(2014, 10, 26, 13, 56, 00);

		TransportTrip trip = service.getTrips(from, to, now).get(0);

		assertEquals("The first trip's departure time should be correct.",
				new DateTime(2014, 10, 26, 13, 59, 00).getMillis(), trip.getDepartureTime());
	}

	private static final class TestHttpClient implements HttpClient {
		private final String returnValue;

		public byte[] lastSentBody;

		public TestHttpClient(String returnFile) {
			returnValue = getFileContents(returnFile);
		}

		@Override
		public String get(String url, Map<String,String> headers, Charset charset) throws IOException {
			throw new RuntimeException("get(String, Charset) should not be called.");
		}

		@Override
		public String post(String url, byte[] body, Charset charset) throws IOException {
			lastSentBody = body;
			return returnValue;
		}

		private static String getFileContents(String name) {
			return StringUtils.fromStream(new StationServiceTests().getClass().getResourceAsStream(name), "UTF-8");
		}
	}
}