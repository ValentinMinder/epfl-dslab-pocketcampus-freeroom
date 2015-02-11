package org.pocketcampus.plugin.transport.server.tests;

import org.junit.Test;
import org.pocketcampus.platform.server.HttpClient;
import org.pocketcampus.platform.shared.utils.StringUtils;
import org.pocketcampus.plugin.transport.server.StationService;
import org.pocketcampus.plugin.transport.server.StationServiceImpl;
import org.pocketcampus.plugin.transport.shared.TransportGeoPoint;
import org.pocketcampus.plugin.transport.shared.TransportStation;

import javax.xml.XMLConstants;
import javax.xml.transform.Source;
import javax.xml.transform.stream.StreamSource;
import javax.xml.validation.SchemaFactory;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.nio.charset.Charset;
import java.util.List;

import static org.junit.Assert.assertEquals;

public final class StationServiceTests {
	@Test
	public void requestValidatesAgainstXsd() throws Exception {
		TestHttpClient client = new TestHttpClient("StationsReplyLausanne.xml");
		StationService service = new StationServiceImpl(client, "token");

		service.findStations("Lausanne,", null);

		Source schemaSource = new StreamSource(new StationServiceTests().getClass().getResourceAsStream("hafasXMLInterface.xsd"));
		Source requestSource = new StreamSource(new ByteArrayInputStream(client.lastSentBody));
		// this call throws if the request does not validate against the HAFAS XSD
		SchemaFactory.newInstance(XMLConstants.W3C_XML_SCHEMA_NS_URI).newSchema(schemaSource).newValidator().validate(requestSource);
	}

	@Test
	public void stationsAreFetched() throws IOException {
		TestHttpClient client = new TestHttpClient("StationsReplyLausanne.xml");
		StationService service = new StationServiceImpl(client, "token");

		List<TransportStation> stations = service.findStations("Lausanne,", null);

		assertEquals("There should be 5 stations.",
				5, stations.size());
		assertEquals("The first station's ID should be parsed correctly.",
				8501120, stations.get(0).getId());
		assertEquals("The first station's name should be parsed correctly.",
				"Lausanne", stations.get(0).getName());
		assertEquals("The first station's latitude should be parsed correctly.",
				46516776, stations.get(0).getLatitude());
		assertEquals("The first station's longitude should be parsed correctly.",
				6629095, stations.get(0).getLongitude());
	}
	
	@Test
	public void stationsAreOrderedWhenLocationIsPresent() throws IOException{
		TestHttpClient client = new TestHttpClient("StationsReplyLausanne.xml");
		StationService service = new StationServiceImpl(client, "token");

		List<TransportStation> stations = service.findStations("Lausanne,", new TransportGeoPoint(46.521244, 6.640673));
		
		assertEquals("The stations should be properly ordered. (#1)",
				"Lausanne, Ours", stations.get(0).getName());
		assertEquals("The stations should be properly ordered. (#2)",
				"Lausanne, CHUV", stations.get(1).getName());
		assertEquals("The stations should be properly ordered. (#3)",
				"Lausanne, gare", stations.get(2).getName());
		assertEquals("The stations should be properly ordered. (#4)",
				"Lausanne", stations.get(3).getName());
		assertEquals("The stations should be properly ordered. (#5)",
				"Lausanne, Sallaz", stations.get(4).getName());
	}

	@Test
	public void errorMeansNoStations() throws IOException {
		TestHttpClient client = new TestHttpClient("StationsReplyError.xml");
		StationService service = new StationServiceImpl(client, "token");

		List<TransportStation> stations = service.findStations("", null);

		assertEquals("There should be no stations.",
				0, stations.size());
	}

	private static final class TestHttpClient implements HttpClient {
		private final String returnValue;

		public byte[] lastSentBody;

		public TestHttpClient(String returnFile) {
			returnValue = getFileContents(returnFile);
		}

		@Override
		public String get(String url, Charset charset) throws IOException {
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