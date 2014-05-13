package org.pocketcampus.plugin.freeroom.data;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.StringReader;
import java.net.HttpURLConnection;
import java.net.URISyntaxException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Types;
import java.util.Calendar;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.apache.http.HttpException;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONException;
import org.pocketcampus.platform.sdk.server.database.ConnectionManager;
import org.pocketcampus.platform.sdk.server.database.handlers.exceptions.ServerException;
import org.pocketcampus.plugin.freeroom.server.FreeRoomServiceImpl;
import org.pocketcampus.plugin.freeroom.server.FreeRoomServiceImpl.OCCUPANCY_TYPE;
import org.pocketcampus.plugin.freeroom.server.utils.Utils;
import org.pocketcampus.plugin.freeroom.shared.FRPeriod;
import org.pocketcampus.plugin.freeroom.shared.FRRoom;
import org.pocketcampus.plugin.freeroom.shared.utils.FRTimes;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

public class FetchOccupancyData {
	private final String URL_DATA = "https://isatest.epfl.ch/services/timetable/reservations/";
	private StringBuffer xml = new StringBuffer();
	private Document documentXML = null;

	private final String KEY_CLASSROOM = "classroom";

	private final String KEY_ALIAS = "name";
	private final String KEY_ALIAS_WITHOUT_SPACE = "code";
	private final String KEY_DOORCODE = "code-epfl";
	private final String KEY_UID = "SOURCE-ID";
	private final String KEY_CAPACITY = "capacity";
	private final String KEY_CAPACITY_EXTRA = "capacity-extra";
	private final String KEY_TYPE = "room-type";
	private final String KEY_DINCAT = "din-type";
	private final String KEY_BUILDING = "building";

	private final String KEY_LIST_OCCUPANCY = "reservations";
	private final String KEY_SINGLE_OCCUPANCY = "reservation";
	private final String KEY_OCCUPANCY_DATE = "date";
	private final String KEY_OCCUPANCY_START = "start-time";
	private final String KEY_OCCUPANCY_END = "end-time";

	private ConnectionManager connMgr = null;

	private FreeRoomServiceImpl server = null;

	public FetchOccupancyData(String db_url, String username, String passwd,
			FreeRoomServiceImpl server) {
		try {
			connMgr = new ConnectionManager(db_url, username, passwd);
			this.server = server;
		} catch (ServerException e) {
			e.printStackTrace();
		}

	}

	public void fetchAndInsert(long timestamp) {
		fetch(timestamp);
		extractDocument();
		traverseDocument();
	}

	private void traverseDocument() {
		if (documentXML == null) {
			return;
		}

		// first get root element
		Element root = documentXML.getDocumentElement();
		NodeList allNodes = root.getElementsByTagName(KEY_CLASSROOM);
		int sizeNodes = allNodes.getLength();

		for (int i = 0; i < sizeNodes; ++i) {
			Node currentNode = allNodes.item(i);

			if (currentNode.getNodeType() == Node.ELEMENT_NODE) {
				Element element = (Element) currentNode;
				String uid = updateRoomAliasCapacity(element);

				if (uid != null) {
					int occupancies = extractAndInsertOccupancy(uid, element);
					System.out.println(occupancies + " inserted for room "
							+ uid);
				}
			}

		}

	}

	private int extractAndInsertOccupancy(String uid, Element element) {
		if (!Utils.checkValidUID(uid)) {
			return 0;
		}

		int count = 0;
		NodeList listOcc = element.getElementsByTagName(KEY_SINGLE_OCCUPANCY);
		int nbOcc = listOcc.getLength();
		// for each occupancy
		for (int i = 0; i < nbOcc; ++i) {
			Node currentOcc = listOcc.item(i);

			if (currentOcc.getNodeType() == Node.ELEMENT_NODE) {
				Element currentElementOcc = (Element) currentOcc;

				NodeList nl = currentElementOcc
						.getElementsByTagName(KEY_OCCUPANCY_DATE);
				String date = null;
				String start = null;
				String end = null;

				if (nl.getLength() > 0) {
					date = nl.item(0).getTextContent();
				}

				nl = currentElementOcc
						.getElementsByTagName(KEY_OCCUPANCY_START);
				if (nl.getLength() > 0) {
					start = nl.item(0).getTextContent();
				}

				nl = currentElementOcc.getElementsByTagName(KEY_OCCUPANCY_END);
				if (nl.getLength() > 0) {
					end = nl.item(0).getTextContent();
				}

				if (date != null && start != null && end != null) {
					long tsStart = FRTimes.getTimestamp(date, start);
					long tsEnd = FRTimes.getTimestamp(date, end);
					FRPeriod period = new FRPeriod(tsStart, tsEnd, false);
					FRRoom room = new FRRoom("fakeroom", uid);
					if (tsStart != 0 && tsEnd != 0) {
						count = server.insertOccupancy(period,
								OCCUPANCY_TYPE.ROOM, room, null) ? count + 1
								: count;
					}
				}
			}
		}
		return count;
	}

	private String updateRoomAliasCapacity(Element element) {
		Connection conn = null;
		try {
			conn = connMgr.getConnection();
		} catch (SQLException e1) {
			e1.printStackTrace();
			return null;
		}

		String req = "UPDATE `fr-roomslist` SET "
				+ "alias = ?, capacity = ? WHERE uid = ? AND (capacity IS NULL OR alias IS NULL)";
		PreparedStatement query;
		String uid = null;
		String alias = null;
		int capacity = 0;
		try {

			query = conn.prepareStatement(req);
			NodeList nl = element.getElementsByTagName(KEY_UID);
			if (nl.getLength() > 0 && !nl.item(0).getTextContent().isEmpty()) {
				uid = nl.item(0).getTextContent();
				if (!Utils.checkValidUID(uid)) {
					return null;
				}
			} else {
				return null;
			}

			nl = element.getElementsByTagName(KEY_ALIAS);
			if (nl.getLength() > 0) {
				alias = nl.item(0).getTextContent();
			} else {
				return null;
			}

			nl = element.getElementsByTagName(KEY_CAPACITY);
			if (nl.getLength() > 0) {
				capacity = Integer.parseInt(nl.item(0).getTextContent());
			} else {
				return null;
			}

			query.setString(1, alias);
			query.setInt(2, capacity);
			query.setString(3, uid);

			query.executeUpdate();
		} catch (SQLException e) {
			e.printStackTrace();
			return null;
		}
		return uid;
	}

	private void extractDocument() {
		if (xml.length() == 0) {
			return;
		}

		DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
		DocumentBuilder constructor;
		try {
			constructor = factory.newDocumentBuilder();
			InputSource is = new InputSource();
			is.setCharacterStream(new StringReader(xml.toString()));
			documentXML = constructor.parse(is);
			System.out.println("Page successfully imported");
		} catch (ParserConfigurationException e) {
			e.printStackTrace();
		} catch (SAXException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}

	}

	private void fetch(long timestamp) {
		String timestampString = FRTimes.convertTimeStampInString(timestamp);
		System.out.println("Start fetching for ... " + timestampString);
		DefaultHttpClient client = new DefaultHttpClient();
		HttpGet request;
		try {
			request = new HttpGet(URL_DATA + timestampString);
			request.addHeader("Accept", "application/xml");

			HttpResponse response = client.execute(request);

			if (response.getStatusLine().getStatusCode() == HttpURLConnection.HTTP_OK) {

				BufferedReader reader = new BufferedReader(
						new InputStreamReader(response.getEntity().getContent()));

				String line = "";
				while ((line = reader.readLine()) != null) {
					xml.append(line);
				}
				System.out.println("Successfully fetched from server");
			} else {
				System.err.println("Error while fetching ,status  "
						+ response.getStatusLine().getStatusCode());
			}
		} catch (URISyntaxException e) {
			e.printStackTrace();
		} catch (HttpException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}

	}
}
