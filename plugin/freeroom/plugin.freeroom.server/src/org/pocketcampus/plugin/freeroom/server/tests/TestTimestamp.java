package org.pocketcampus.plugin.freeroom.server.tests;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.ArrayList;

import junit.framework.Assert;

import org.apache.thrift.TException;
import org.junit.Test;
import org.pocketcampus.platform.sdk.server.database.ConnectionManager;
import org.pocketcampus.platform.sdk.server.database.handlers.exceptions.ServerException;
import org.pocketcampus.plugin.freeroom.server.FreeRoomServiceImpl;
import org.pocketcampus.plugin.freeroom.shared.ActualOccupation;
import org.pocketcampus.plugin.freeroom.shared.FRPeriod;
import org.pocketcampus.plugin.freeroom.shared.FRRoom;
import org.pocketcampus.plugin.freeroom.shared.Occupancy;
import org.pocketcampus.plugin.freeroom.shared.OccupancyReply;
import org.pocketcampus.plugin.freeroom.shared.OccupancyRequest;

public class TestTimestamp {
	final static String DB_USERNAME = "root";
	final static String DB_PASSWORD = "root";
	final static String DBMS_URL = "jdbc:mysql://localhost/?allowMultiQueries=true";
	final static String DB_URL = "jdbc:mysql://localhost/pocketcampus?allowMultiQueries=true";

	private Connection conn = null;

	@Test
	public void test() {
		try {
			conn = DriverManager.getConnection(DBMS_URL, DB_USERNAME,
					DB_PASSWORD);
			conn.setCatalog("pocketcampus");
			long now = System.currentTimeMillis();
			ArrayList<FRRoom> rooms = new ArrayList<>();
			rooms.add(new FRRoom("CO", "1"));
			OccupancyRequest req = new OccupancyRequest(rooms, 
					new FRPeriod(now, now + 3600*1000*4, false));
			OccupancyReply occReply = (new FreeRoomServiceImpl(
					new ConnectionManager(DB_URL, DB_USERNAME, DB_PASSWORD))).
					checkTheOccupancy(req);
			System.out.println(occReply.getOccupancyOfRoomsSize());
			for (Occupancy occ : occReply.getOccupancyOfRooms()) {
				for (ActualOccupation aocc : occ.getOccupancy()) {
					System.out.println(aocc);
				}
			}

		} catch (SQLException e) {
			Assert.fail("There was an SQL Exception \n " + e);
		} catch (TException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (ServerException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		
	}
}
