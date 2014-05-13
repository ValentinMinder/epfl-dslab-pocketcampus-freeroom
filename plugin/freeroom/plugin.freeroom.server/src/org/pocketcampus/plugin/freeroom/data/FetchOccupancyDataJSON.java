package org.pocketcampus.plugin.freeroom.data;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.Scanner;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.pocketcampus.platform.sdk.server.database.ConnectionManager;
import org.pocketcampus.platform.sdk.server.database.handlers.exceptions.ServerException;
import org.pocketcampus.plugin.freeroom.server.FreeRoomServiceImpl;

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

	private FreeRoomServiceImpl server = null;

	public FetchOccupancyDataJSON(String db_url, String username, String passwd,
			FreeRoomServiceImpl server) {
		try {
			connMgr = new ConnectionManager(db_url, username, passwd);
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
			
			for (int i = 0; i < lengthSourceArray; ++i) {
				JSONArray subArray = sourceArray.getJSONArray(i);
				int subArrayLength = subArray.length();
				
				if (subArrayLength == 2) {
					JSONObject room = subArray.getJSONObject(0);
					JSONArray occupancy = subArray.getJSONArray(1);
					
					extractAndInsertRoom(room);
					extractAndInsertOccupancies(occupancy);
				}
				
			}
		} catch (JSONException e) {
			e.printStackTrace();
		}
	}
	
	private void extractAndInsertRoom(JSONObject room) {
		if (room == null) {
			return;
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
}
