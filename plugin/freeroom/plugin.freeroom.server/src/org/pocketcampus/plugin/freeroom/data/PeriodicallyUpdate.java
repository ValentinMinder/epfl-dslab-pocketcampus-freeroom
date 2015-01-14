package org.pocketcampus.plugin.freeroom.data;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.Calendar;
import java.util.logging.Level;

import org.pocketcampus.plugin.freeroom.server.FreeRoomServiceImpl;
import org.pocketcampus.plugin.freeroom.server.exchange.ExchangeServiceImpl;
import org.pocketcampus.plugin.freeroom.shared.Constants;
import org.pocketcampus.plugin.freeroom.shared.utils.FRTimes;

/**
 * Runnable used to be launched periodically to fetch and update data in the
 * database
 * 
 * @author FreeRoom Project Team (2014/05)
 * @author Julien WEBER <julien.weber@epfl.ch>
 * @author Valentin MINDER <valentin.minder@epfl.ch>
 * 
 */
public class PeriodicallyUpdate implements Runnable {

	private String DB_URL;
	private String DB_USER;
	private String DB_PASSWORD;
	private FreeRoomServiceImpl server;
	private AutoUpdate updater;
	private Connection conn;

	public PeriodicallyUpdate(String db_url, String username, String passwd,
			FreeRoomServiceImpl server, AutoUpdate updater) {
		DB_URL = db_url;
		DB_USER = username;
		DB_PASSWORD = passwd;
		this.server = server;
		this.updater = updater;
	}

	/**
	 * Assume connUpdate's autocommit is set to false.
	 */
	public PeriodicallyUpdate(FreeRoomServiceImpl freeRoomServiceImpl,
			AutoUpdate updater, Connection connUpdate) {
		this.server = freeRoomServiceImpl;
		this.updater = updater;
		this.conn = connUpdate;
	}

	@Override
	public void run() {
		// The clean part, and setAutoCommit(false) is done just before
		// inserting, the later the better. See the fetchAndInsert(start,end)
		// below
		
		server.log(Level.INFO, "Starting update of data from ISA");
		FetchOccupancyDataJSON fodj = new FetchOccupancyDataJSON(server, conn);
		Calendar mCalendar = Calendar.getInstance();
		long now = mCalendar.getTimeInMillis();
		long start = now;
		// end is two weeks after what is authorized by clients.
		// just in case the ISA server goes down for a while, or we cannot fetch
		// data for any other reason, we have data for a longer period.
		long end = now + (Constants.MAXIMAL_WEEKS_IN_FUTURE + 2)
				* FRTimes.ONE_WEEK_IN_MS;
		fodj.fetchAndInsert(start, end);

		// server.log(Level.INFO, "Starting update of data from Exchange EWA");
		// ExchangeServiceImpl exchange = new ExchangeServiceImpl(server, conn);
		// exchange.updateEWAOccupancyFromTo(start, end);
		try {
			this.conn.commit();
			server.log(Level.INFO, "Finished updating data for FreeRoom");
		} catch (SQLException e) {
			server.log(Level.SEVERE, "Cannot commit update change");
			e.printStackTrace();
		}

	}

}
