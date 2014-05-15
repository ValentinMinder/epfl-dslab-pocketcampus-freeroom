package org.pocketcampus.plugin.freeroom.data;

import java.util.Calendar;

import org.pocketcampus.plugin.freeroom.server.FreeRoomServiceImpl;
import org.pocketcampus.plugin.freeroom.server.exchange.ExchangeServiceImpl;
import org.pocketcampus.plugin.freeroom.shared.utils.FRTimes;

/**
 * Runnable used to be launched periodically to fetch and update data in the
 * database
 * 
 * @author FreeRoom Project Team - Julien WEBER <julien.weber@epfl.ch> and
 *         Valentin MINDER <valentin.minder@epfl.ch>
 * 
 */
public class PeriodicallyUpdate implements Runnable {

	private String DB_URL;
	private String DB_USER;
	private String DB_PASSWORD;
	private FreeRoomServiceImpl server;

	public PeriodicallyUpdate(String db_url, String username, String passwd,
			FreeRoomServiceImpl server) {
		DB_URL = db_url;
		DB_USER = username;
		DB_PASSWORD = passwd;
		this.server = server;
	}

	@Override
	public void run() {
		FetchOccupancyDataJSON fodj = new FetchOccupancyDataJSON(DB_URL,
				DB_USER, DB_PASSWORD, server);
		Calendar mCalendar = Calendar.getInstance();
		long yesterday = mCalendar.getTimeInMillis() - FRTimes.ONE_DAY_IN_MS;
		long tomorrow = mCalendar.getTimeInMillis() + FRTimes.ONE_DAY_IN_MS;
		fodj.fetchAndInsert(yesterday, tomorrow);
		
		ExchangeServiceImpl exchange = new ExchangeServiceImpl(DB_URL, DB_USER, DB_PASSWORD, server);
		exchange.updateEWAOccupancyFromTo(yesterday, tomorrow);
	}

}
