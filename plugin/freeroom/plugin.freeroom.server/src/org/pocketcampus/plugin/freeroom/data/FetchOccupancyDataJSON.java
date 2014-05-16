package org.pocketcampus.plugin.freeroom.data;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URISyntaxException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.Scanner;
import java.util.logging.Level;

import microsoft.exchange.webservices.data.LogicalOperator;

import org.apache.http.HttpException;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.pocketcampus.platform.sdk.server.database.ConnectionManager;
import org.pocketcampus.platform.sdk.server.database.handlers.exceptions.ServerException;
import org.pocketcampus.plugin.freeroom.server.FreeRoomServiceImpl;
import org.pocketcampus.plugin.freeroom.server.FreeRoomServiceImpl.OCCUPANCY_TYPE;
import org.pocketcampus.plugin.freeroom.shared.FRPeriod;
import org.pocketcampus.plugin.freeroom.shared.utils.FRTimes;

/**
	Fetch, insert, update rooms and/or occupancies from the ISA webservice
 * 
 * @author FreeRoom Project Team - Julien WEBER <julien.weber@epfl.ch> and
 *         Valentin MINDER <valentin.minder@epfl.ch>
 * 
 */
public class FetchOccupancyDataJSON {
	private final String URL_DATA = "https://isatest.epfl.ch/services/timetable/reservations/";

	private final String KEY_ALIAS = "name";
	private final String KEY_ALIAS_WITHOUT_SPACE = "code";
	private final String KEY_DOORCODE = "EPDLCode";
	private final String KEY_UID = "sourceId";
	private final String KEY_CAPACITY = "capacity";
	private final String KEY_CAPACITY_EXTRA = "extraCapacity";
	private final String KEY_DINCAT = "typeDIN";

	private final String KEY_OCCUPANCY = "meetingType";
	private final String KEY_OCCUPANCY_START = "startDateTime";
	private final String KEY_OCCUPANCY_LENGTH = "duration";
	private final String KEY_OCCUPANCY_ROOMS = "rooms";

	private ConnectionManager connMgr = null;
	private String DB_URL;
	private String DB_USER;
	private String DB_PASSWORD;

	private FreeRoomServiceImpl server = null;

	public FetchOccupancyDataJSON(String db_url, String username,
			String passwd, FreeRoomServiceImpl server) {
		try {
			connMgr = new ConnectionManager(db_url, username, passwd);
			DB_URL = db_url;
			DB_USER = username;
			DB_PASSWORD = passwd;
			this.server = server;
		} catch (ServerException e) {
			e.printStackTrace();
		}

	}

	/**
	 * Fetch and insert in the database the occupancy for the given timestamp
	 * 
	 * @param timestamp
	 *            At what time to fetch
	 */
	public void fetchAndInsert(long timestamp) {
		// String json = readFromFile("src" + File.separator + "freeroomjson");
		extractJSONAndInsert(fetch(timestamp), false);

	}

	/**
	 * Fetch and insert in the database the occupancy during a certain period
	 * 
	 * @param from
	 *            The start of the period
	 * @param to
	 *            The end of the period
	 */
	public void fetchAndInsert(long from, long to) {
		if (to < from) {
			return;
		}
		extractJSONAndInsert(fetchFromTo(from, to), false);

	}

	/**
	 * Fetch and insert in the database the rooms occupied during a given
	 * period.
	 * 
	 * @param from
	 *            The start of the period
	 * @param to
	 *            The end of the period
	 */
	public void fetchAndInsertRoomsList(long from, long to) {
		extractJSONAndInsert(fetchFromTo(from, to), true);
	}

	/**
	 * Parse the json page and insert the data contained in the database
	 * 
	 * @param jsonSource
	 *            The JSON to parse
	 * @param updateRooms
	 *            true if this method should also update/insert rooms, false
	 *            otherwise
	 */
	private void extractJSONAndInsert(String jsonSource, boolean updateRooms) {
		try {
			JSONArray sourceArray = new JSONArray(jsonSource);
			int lengthSourceArray = sourceArray.length();
			int countRoom = 0;

			for (int i = 0; i < lengthSourceArray; ++i) {
				JSONArray subArray = sourceArray.getJSONArray(i);
				int subArrayLength = subArray.length();

				if (subArrayLength == 2) {
					JSONObject room = subArray.getJSONObject(0);
					JSONArray occupancy = subArray.getJSONArray(1);

					String uid = extractAndInsertRoom(room, updateRooms);
					if (uid != null) {
						countRoom++;
						//extractAndInsertOccupancies(occupancy, uid);
					}
				}

			}
		} catch (JSONException e) {
			e.printStackTrace();
		}
	}

	/**
	 * Parse the given JSON representing a room and eventually update/insert it
	 * into the database, depending of the argument updateRooms.
	 * 
	 * @param room
	 *            The JSON describing the room
	 * @param updateRooms
	 *            true if you want to update/insert the room in the database,
	 *            false if you just want to get the UID
	 * @return The unique id (UID) of the room
	 */
	private String extractAndInsertRoom(JSONObject room, boolean updateRooms) {
		if (room == null) {
			return null;
		}

		try {
			String uid = null;
			if (room.has(KEY_UID)) {
				uid = room.getString(KEY_UID);
			} else {
				return null;
			}

			if (!updateRooms) {
				return uid;
			}

			// first fetch and insert the room from the other webservice
			FetchRoomsDetails frd = new FetchRoomsDetails(DB_URL, DB_USER,
					DB_PASSWORD);
			if (!frd.fetchRoomDetailInDB(uid)) {
				return null;
			}

			// from this webservice
			Connection conn = null;

			conn = connMgr.getConnection();

			String reqCapacity = "UPDATE `fr-roomslist` SET capacity = ? WHERE uid = ? AND capacity = 0";
			String reqAlias = "UPDATE `fr-roomslist` SET alias = ? WHERE uid = ? AND alias IS NULL";
			PreparedStatement queryCapacity;
			PreparedStatement queryAlias;

			// first update the capacity
			queryCapacity = conn.prepareStatement(reqCapacity);

			int capacity = 0;
			if (room.has(KEY_CAPACITY)) {
				capacity = Integer.parseInt(room.getString(KEY_CAPACITY));
			}
			if (capacity == 0 && room.has(KEY_CAPACITY_EXTRA)) {
				capacity = Integer.parseInt(room.getString(KEY_CAPACITY_EXTRA));
			}

			queryCapacity.setInt(1, capacity);
			queryCapacity.setString(2, uid);
			queryCapacity.executeUpdate();

			// then update the alias
			queryAlias = conn.prepareStatement(reqAlias);

			String alias = null;
			if (room.has(KEY_ALIAS)) {
				JSONObject aliasObject = room.getJSONObject(KEY_ALIAS);
				if (aliasObject.has("fr")) {
					alias = aliasObject.getString("fr");
				}
			}

			if (alias == null && room.has(KEY_ALIAS_WITHOUT_SPACE)) {
				alias = room.getString(KEY_ALIAS_WITHOUT_SPACE);
			}

			if (alias != null) {
				queryAlias.setString(1, alias);
				queryAlias.setString(2, uid);
				queryAlias.executeUpdate();
			}
			// if we are there, it means the fetch of the rooms details
			// succeeded
			return uid;
		} catch (SQLException | JSONException e) {
			e.printStackTrace();
			return null;
		}

	}

	/**
	 * Parse and insert occupancies contained in the argument into the database
	 * @param array The array containing the occupancies for the given room
	 * @param uid The unique ID (UID) representing the rooms of the occupancies
	 * @return The number of occupancies extracted and inserted into the database
	 */
	private int extractAndInsertOccupancies(JSONArray array, String uid) {
		if (array == null || uid == null) {
			return 0;
		}

		if (array.length() == 0) {
			return 0;
		}
		try {
			int nbOccupancy = array.length();
			int count = 0;
			for (int i = 0; i < nbOccupancy; ++i) {
				JSONObject occupancy = array.getJSONObject(i);
				long tsStart = 0;
				long tsEnd = 0;
				if (occupancy.has(KEY_OCCUPANCY_START)) {
					tsStart = Long.parseLong(occupancy
							.getString(KEY_OCCUPANCY_START));

					if (occupancy.has(KEY_OCCUPANCY_LENGTH)) {
						int length = Integer.parseInt(occupancy
								.getString(KEY_OCCUPANCY_LENGTH));
						tsEnd = tsStart + length * FRTimes.ONE_MIN_IN_MS;
					}
				}

				if (tsStart != 0 && tsEnd != 0 && tsStart < tsEnd) {
					FRPeriod period = new FRPeriod(tsStart, tsEnd, false);
					if (server.insertOccupancy(period, OCCUPANCY_TYPE.ROOM,
							uid, null)) {
						count++;
					}
				}
			}
			return count;
		} catch (JSONException e) {
			e.printStackTrace();
			return 0;
		}
	}

	private String readFromFile(String name) {
		try {
			Scanner sc = new Scanner(new File(name));
			StringBuffer json = new StringBuffer();
			while (sc.hasNextLine()) {
				json.append(sc.nextLine());
			}

			return json.toString();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
			return null;
		}
	}

	/**
	 * Fetch JSON page from ISA webservice during a given period
	 * @param from The start of the period
	 * @param to The end of the period
	 * @return The JSON page fetched
	 */
	private String fetchFromTo(long from, long to) {
		String timestampStringFrom = FRTimes.convertTimeStampInString(from);
		String timestampStringTo = FRTimes.convertTimeStampInString(to);
		return fetch(URL_DATA + timestampStringFrom + "/to/"
				+ timestampStringTo);
	}

	/**
	 * Fetch JSON page from ISA webservice for a given day
	 * @param timestamp The day to fetch
	 * @return The JSON page fetched
	 */
	private String fetch(long timestamp) {
		String timestampString = FRTimes.convertTimeStampInString(timestamp);
		return fetch(URL_DATA + timestampString);

	}

	/**
	 * Fetch a JSON page at a given URL
	 * @param URL The URL to fetch
	 * @return The JSON page located at the given URL, null if none
	 */
	private String fetch(String URL) {
		server.log(Level.INFO, "Starting fetching data from ISA webservice for URL " + URL);
		DefaultHttpClient client = new DefaultHttpClient();
		HttpGet request;
		try {
			request = new HttpGet(URL);
			request.addHeader("Accept", "application/json");

			HttpResponse response = client.execute(request);

			if (response.getStatusLine().getStatusCode() == HttpURLConnection.HTTP_OK) {

				BufferedReader reader = new BufferedReader(
						new InputStreamReader(response.getEntity().getContent()));

				StringBuffer jsonBuffer = new StringBuffer();
				String line = "";
				while ((line = reader.readLine()) != null) {
					jsonBuffer.append(line);
				}
				server.log(Level.INFO, "Successfully fetched data from ISA webservice");
				return jsonBuffer.toString();
			} else {
				server.log(Level.WARNING, "Error while fetching from ISA webservice, status " + response.getStatusLine().getStatusCode());
			}
		} catch (URISyntaxException e) {
			e.printStackTrace();
		} catch (HttpException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return null;
	}
}
