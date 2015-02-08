package org.pocketcampus.plugin.freeroom.data;

import org.pocketcampus.plugin.freeroom.server.FreeRoomServiceImpl;

/**
 * Used to rebuild the list of rooms in the database, should not be used too
 * often as it queries the ISA server for a big period of time with all the
 * reservations.
 * 
 * @author Julien WEBER (julien.weber@epfl.ch)
 * 
 */
public class RebuildDB implements Runnable {
	
	private String DB_URL;
	private String DB_USER;
	private String DB_PASSWORD;
	private FreeRoomServiceImpl server;
	private long tsStart;
	private long tsEnd;

	public RebuildDB(String db_url, String username, String passwd, FreeRoomServiceImpl server, long tsStartSemester, long tsEndSemester) {
		DB_URL = db_url;
		DB_USER = username;
		DB_PASSWORD = passwd;
		this.server = server;
		this.tsStart = tsStartSemester;
		this.tsEnd = tsEndSemester;
	}

	@Override
	public void run() {
		server.cleanOldData(null);
		server.cleanRoomsList();
		FetchOccupancyDataJSON fodJSON = new FetchOccupancyDataJSON(DB_URL,
				DB_USER, DB_PASSWORD, server);
		fodJSON.fetchAndInsertRoomsList(tsStart, tsEnd);
		
		//ExchangeLoading loadEWA = new ExchangeLoading(DB_URL, DB_USER, DB_PASSWORD);
		//loadEWA.loadExchangeData();
	}

}
