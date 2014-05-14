package org.pocketcampus.plugin.freeroom.data;

import static org.pocketcampus.platform.launcher.server.PCServerConfig.PC_SRV_CONFIG;

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
import java.sql.Types;
import java.util.Scanner;

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
import org.pocketcampus.plugin.freeroom.server.utils.FetchRoomsDetails;
import org.pocketcampus.plugin.freeroom.shared.utils.FRTimes;

public class FetchOccupancyDataJSON {
	private final String URL_DATA = "https://isatest.epfl.ch/services/timetable/reservations/";

	private final String KEY_ALIAS = "name";
	private final String KEY_ALIAS_WITHOUT_SPACE = "code";
	private final String KEY_DOORCODE = "EPDLCode";
	private final String KEY_UID = "sourceId";
	private final String KEY_CAPACITY = "capacity";
	private final String KEY_CAPACITY_EXTRA = "extraCapacity";
	private final String KEY_DINCAT = "typeDIN";

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

	public void fetchAndInsert(long timestamp) {
		String json = readFromFile("src" + File.separator + "freeroomjson");
		extractJSONAndInsert(json);
	}

	private void extractJSONAndInsert(String jsonSource) {
		try {
			JSONArray sourceArray = new JSONArray(jsonSource);
			int lengthSourceArray = sourceArray.length();
			int count = 0;
			for (int i = 0; i < lengthSourceArray; ++i) {
				JSONArray subArray = sourceArray.getJSONArray(i);
				int subArrayLength = subArray.length();

				if (subArrayLength == 2) {
					JSONObject room = subArray.getJSONObject(0);
					JSONArray occupancy = subArray.getJSONArray(1);

					if (extractAndInsertRoom(room)) {
						count++;
						extractAndInsertOccupancies(occupancy);
					}
				}

			}
			System.out.println(count + " rooms inserted");
		} catch (JSONException e) {
			e.printStackTrace();
		}
	}

	private boolean extractAndInsertRoom(JSONObject room) {
		if (room == null) {
			return false;
		}

		try {
			String uid = null;
			if (room.has(KEY_UID)) {
				uid = room.getString(KEY_UID);
			} else {
				return false;
			}
			// first fetch and insert the room from the other webservice
			FetchRoomsDetails frd = new FetchRoomsDetails(DB_URL, DB_USER,
					DB_PASSWORD);
			if (!frd.fetchRoomDetailInDB(uid)) {
				return false;
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
			return true;
		} catch (SQLException | JSONException e) {
			e.printStackTrace();
			return false;
		}

	}

	private void extractAndInsertOccupancies(JSONArray array) {
		if (array == null) {
			return;
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

	private String fetch(long timestamp) {
		String timestampString = FRTimes.convertTimeStampInString(timestamp);
		System.out.println("Start fetching for ... " + timestampString);
		DefaultHttpClient client = new DefaultHttpClient();
		HttpGet request;
		try {
			request = new HttpGet(URL_DATA + timestampString);
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
				System.out.println("Successfully fetched from server");
				return jsonBuffer.toString();
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
		return null;
	}
}
