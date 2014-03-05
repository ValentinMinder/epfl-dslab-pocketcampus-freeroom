package org.pocketcampus.plugin.freeroom.server;

import static org.pocketcampus.platform.launcher.server.PCServerConfig.PC_SRV_CONFIG;

import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Calendar;
import java.util.HashSet;
import java.util.Set;

import org.apache.thrift.TException;
import org.pocketcampus.platform.sdk.server.database.ConnectionManager;
import org.pocketcampus.platform.sdk.server.database.handlers.exceptions.ServerException;
import org.pocketcampus.plugin.freeroom.shared.FRFreeRoomRequestFromTime;
import org.pocketcampus.plugin.freeroom.shared.FRFreeRoomResponseFromTime;
import org.pocketcampus.plugin.freeroom.shared.FRPeriod;
import org.pocketcampus.plugin.freeroom.shared.FRTimeStamp;
import org.pocketcampus.plugin.freeroom.shared.FreeRoomService;
import org.pocketcampus.plugin.freeroom.shared.FRRoom;

/**
 * FreeRoomServiceImpl
 * 
 * The implementation of the server side of the FreeRoom Plugin.
 * 
 * It fetches the user's FreeRoom data from the FreeRoom servers.
 * 
 * @author Amer <amer.chamseddine@epfl.ch>
 *
 */
public class FreeRoomServiceImpl implements FreeRoomService.Iface {
	private ConnectionManager connMgr;
	
	public FreeRoomServiceImpl() {
		System.out.println("Starting FreeRoom plugin server ...");
		try {
			connMgr = new ConnectionManager(PC_SRV_CONFIG.getString("DB_URL"),
					PC_SRV_CONFIG.getString("DB_USERNAME"), PC_SRV_CONFIG.getString("DB_PASSWORD"));

		} catch (ServerException e) {
			e.printStackTrace();
		}
	}
	
	//for test purposes ONLY
	public FreeRoomServiceImpl(ConnectionManager conn) {
		System.out.println("Starting TEST FreeRoom plugin server ...");
		connMgr = conn;
	}

	@Override
	public FRFreeRoomResponseFromTime getFreeRoomFromTime(
			FRFreeRoomRequestFromTime request) throws TException {

		FRPeriod period = request.getPeriod();
		FRTimeStamp ts_start = period.getTimeStampStart();
		FRTimeStamp ts_end = period.getTimeStampEnd();
		boolean recurrent = period.isRecurrent();
		
		if (!recurrent) {
			FRFreeRoomResponseFromTime rep = new FRFreeRoomResponseFromTime();
			rep.setRooms(getFreeRoom(ts_start, ts_end));
//			rep.setRooms(new HashSet<FRRoom>());
			rep.setRoomsIsSet(true);
			return rep;
		} else {
			// TODO: support recurrent request
			throw new TException("reccurent request not implemented yet");
			//return null;
		}
	}
	
	private Set<FRRoom> getFreeRoom (FRTimeStamp start, FRTimeStamp end) throws TException {
		Calendar startDate = Calendar.getInstance();
		startDate.setFirstDayOfWeek(Calendar.MONDAY);
		startDate.setTimeInMillis((long) start.getTimeSeconds() *1000);
		Calendar endDate = Calendar.getInstance();
		endDate.setFirstDayOfWeek(Calendar.SUNDAY);
		endDate.setTimeInMillis((long) end.getTimeSeconds() *1000);
	
//		if (startDate.compareTo(endDate) <= 0) {
//			throw new TException("Start date must be before end date");
//		}
		
		// depends from the structure of database, need to change probably!
		// doesn't support overnight searches, only MON-SUN 8am-7pm
		int day = startDate.get(Calendar.DAY_OF_WEEK) - 1;
		int starthour = startDate.get(Calendar.HOUR_OF_DAY);
		int endhour = endDate.get(Calendar.HOUR_OF_DAY);
		//TODO: check the day : seems to be false (tuesday = 3 ?!?)
		System.out.println("Day: " + day + "/ from hour " + starthour + "/ to hour" + endhour);
		
		// All this was copied from previous method!
		if (starthour < 8 || endhour > 19) {
			throw new TException("unsupported timestamps: outside boundaries");
		}
		if (starthour >= endhour) {
			throw new TException("unsupported timestamps: same timestamps");
		}
		
		//Create a string formatted as ?, ? ... ? for the query
		
		String queryStatement = "";
		for (int i = starthour; i < endhour; ++i) {
			queryStatement += "?";
			if (i < endhour - 1) {
				queryStatement += ",";
			}
		}
				
		HashSet<FRRoom> freerooms = new HashSet<FRRoom>();
		try {
			Connection connectBDD = connMgr.getConnection();
			PreparedStatement query = connectBDD.prepareStatement("SELECT rl.building, rl.room_number " +
					"FROM roomslist rl " +
					"WHERE rl.rid NOT IN " +
					"(SELECT ro2.rid FROM roomsoccupancy ro2 WHERE ro2.day_number = ? AND ro2.startHour IN (" + queryStatement + "))");
		
			//filling the query with values
			query.setInt(1, day);
			for (int i = 1; i <= (endhour - starthour) ; ++i) {
				query.setInt(i + 1, i + starthour - 1);
			}
			
			ResultSet resultQuery = query.executeQuery();
			while (resultQuery.next()) {
				String building = resultQuery.getString("building");
				int room_number = resultQuery.getInt("room_number");
				FRRoom r = new FRRoom();
				r.setBuilding(building);
				r.setNumber(room_number + "");
				freerooms.add(r);
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		
		return freerooms;
	}
	
}
