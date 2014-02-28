package org.pocketcampus.plugin.freeroom.server;

import static org.pocketcampus.platform.launcher.server.PCServerConfig.PC_SRV_CONFIG;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashSet;
import java.util.Set;

import org.apache.thrift.TException;
import org.pocketcampus.platform.sdk.server.database.ConnectionManager;
import org.pocketcampus.platform.sdk.server.database.handlers.exceptions.ServerException;
import org.pocketcampus.plugin.freeroom.shared.FreeRoomService;
import org.pocketcampus.plugin.freeroom.shared.FRPeriodOfTime;
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
	public Set<FRRoom> getFreeRoomsFromTime(FRPeriodOfTime period)
			throws TException {
		if (period.getStartHour() < 8 || period.getEndHour() > 19) {
			return null;
		}
		if (period.getStartHour() >= period.getEndHour()) {
			return null;
		}
		
		//Create a string formatted as ?, ? ... ? for the query
		
		String queryStatement = "";
		for (int i = period.getStartHour(); i < period.getEndHour(); ++i) {
			queryStatement += "?";
			if (i < period.getEndHour() - 1) {
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
			query.setInt(1, period.getDay().getValue());
			for (int i = 1; i <= (period.getEndHour() - period.getStartHour()) ; ++i) {
				query.setInt(i + 1, i + period.getStartHour() - 1);
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
