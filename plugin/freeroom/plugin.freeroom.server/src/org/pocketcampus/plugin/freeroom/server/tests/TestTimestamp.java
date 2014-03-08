package org.pocketcampus.plugin.freeroom.server.tests;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Calendar;

import junit.framework.Assert;

import org.apache.commons.io.IOUtils;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.pocketcampus.plugin.freeroom.android.utils.Converter;
import org.pocketcampus.plugin.freeroom.shared.FRRoom;
import org.pocketcampus.plugin.freeroom.shared.FreeRoomRequest;

public class TestTimestamp {
	final static String DB_USERNAME = "root";
	final static String DB_PASSWORD = "root";
	final static String DBMS_URL = "jdbc:mysql://localhost/?allowMultiQueries=true";
	final static String DB_URL = "jdbc:mysql://localhost/pocketcampustest?allowMultiQueries=true";

	private Connection conn = null;

	// @Test
	// public void test() {
	// try {
	// conn = DriverManager.getConnection(DBMS_URL, DB_USERNAME,
	// DB_PASSWORD);
	// conn.setCatalog("pocketcampustest");
	// long now = System.currentTimeMillis();
	// ArrayList<FRRoom> rooms = new ArrayList<>();
	// rooms.add(new FRRoom("CO", "1"));
	// OccupancyRequest req = new OccupancyRequest(rooms, new FRPeriod(
	// now, now + 3600 * 1000 * 4, false));
	// OccupancyReply occReply = (new FreeRoomServiceImpl(
	// new ConnectionManager(DB_URL, DB_USERNAME, DB_PASSWORD)))
	// .checkTheOccupancy(req);
	// System.out.println(occReply.getOccupancyOfRoomsSize());
	// for (Occupancy occ : occReply.getOccupancyOfRooms()) {
	// for (ActualOccupation aocc : occ.getOccupancy()) {
	// System.out.println(aocc);
	// }
	// }
	//
	// } catch (SQLException e) {
	// Assert.fail("There was an SQL Exception \n " + e);
	// } catch (TException e) {
	// // TODO Auto-generated catch block
	// e.printStackTrace();
	// } catch (ServerException e) {
	// // TODO Auto-generated catch block
	// e.printStackTrace();
	// }
	//
	// }

	public static void createDBTest() {
		Connection conn = null;
		PreparedStatement pstmt = null;
		try {
			conn = DriverManager.getConnection(DBMS_URL, DB_USERNAME,
					DB_PASSWORD);

			PreparedStatement stmt = conn
					.prepareStatement("CREATE DATABASE IF NOT EXISTS pocketcampustest");
			stmt.execute();
			conn.setCatalog("pocketcampustest");

			File dbFile = new File(
					"src/org/pocketcampus/plugin/freeroom/server/tests/testdb-create.sql");

			String query = IOUtils.toString(new FileReader(dbFile));
			pstmt = conn.prepareStatement(query);
			pstmt.execute();
			pstmt.close();
			// TODO: check that the database and two tables are successfully
			// created ?
		} catch (SQLException e) {
			Assert.fail("There was an SQL Exception \n " + e);
		} catch (FileNotFoundException e) {
			Assert.fail("Thee test file for the database was not found \n " + e);
		} catch (IOException e) {
			Assert.fail("There was another IO Exception \n " + e);
		}

	}

	public static void removeDBTest() {
		Connection conn = null;
		try {
			conn = DriverManager.getConnection(DBMS_URL, DB_USERNAME,
					DB_PASSWORD);

			PreparedStatement stmt = conn
					.prepareStatement("DROP DATABASE pocketcampustest");
			stmt.execute();
			stmt.close();
			// TODO : check that the database is successfully deleted ?
		} catch (SQLException e) {
			Assert.fail("There was an SQL Exception \n " + e);
		}
	}

	@BeforeClass
	public static void setUpBeforeClass() {
		createDBTest();
		populate();
	}

	@AfterClass
	public static void tearDownAfterClass() {
		// TODO: tests should remove their databases and tables, comment it if
		// you want to see them in SQL
		 removeDBTest();
	}

	@Before
	public void setUp() {
		try {
			conn = DriverManager.getConnection(DBMS_URL, DB_USERNAME,
					DB_PASSWORD);
			conn.setCatalog("pocketcampustest");
		} catch (SQLException e) {
			Assert.fail("There was an SQL Exception \n " + e);
		}
	}

	@After
	public void tearDown() {
		try {
			conn.close();
		} catch (SQLException e) {
			Assert.fail("There was an SQL Exception \n " + e);
		}
	}

	public static void populate() {
		Connection conn = null;
		try {
			conn = DriverManager.getConnection(DB_URL, DB_USERNAME,
					DB_PASSWORD);
		} catch (SQLException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
//			return;
		}
		ArrayList<FRRoom> rooms = new ArrayList<>();
		rooms.add(new FRRoom("CO", "1"));
		rooms.add(new FRRoom("CO", "2"));
		rooms.add(new FRRoom("CO", "3"));
		rooms.add(new FRRoom("CM", "1"));
		rooms.add(new FRRoom("CM", "2"));
		rooms.add(new FRRoom("CM", "3"));

		ArrayList<ArrayList<FreeRoomRequest>> globalAL = new ArrayList<>();
		ArrayList<FreeRoomRequest> requestco1 = new ArrayList<>();
		ArrayList<FreeRoomRequest> requestco2 = new ArrayList<>();
		ArrayList<FreeRoomRequest> requestco3 = new ArrayList<>();
		ArrayList<FreeRoomRequest> requestcm1 = new ArrayList<>();
		ArrayList<FreeRoomRequest> requestcm2 = new ArrayList<>();
		ArrayList<FreeRoomRequest> requestcm3 = new ArrayList<>();

		globalAL.add(requestco1);
		globalAL.add(requestco2);
		globalAL.add(requestco3);
		globalAL.add(requestcm1);
		globalAL.add(requestcm2);
		globalAL.add(requestcm3);

		// CO1 occupancies
		requestco1.add(Converter.convert(Calendar.MONDAY, 8, 9));
		requestco1.add(Converter.convert(Calendar.THURSDAY, 12, 13));
		// CO2
		requestco2.add(Converter.convert(Calendar.MONDAY, 11, 12));
		requestco2.add(Converter.convert(Calendar.WEDNESDAY, 8, 12));
		// CO3
		requestco3.add(Converter.convert(Calendar.MONDAY, 15, 16));
		requestco3.add(Converter.convert(Calendar.TUESDAY, 15, 16));
		requestco3.add(Converter.convert(Calendar.THURSDAY, 10, 11));
		// CM1
		requestcm1.add(Converter.convert(Calendar.MONDAY, 8, 9));
		requestcm1.add(Converter.convert(Calendar.MONDAY, 10, 12));
		requestcm1.add(Converter.convert(Calendar.WEDNESDAY, 8, 9));
		// CM2
		requestcm2.add(Converter.convert(Calendar.MONDAY, 15, 16));
		requestcm2.add(Converter.convert(Calendar.TUESDAY, 12, 14));
		// CM3
		requestcm3.add(Converter.convert(Calendar.MONDAY, 15, 16));
		requestcm3.add(Converter.convert(Calendar.WEDNESDAY, 10, 11));

		// insert the rooms
		for (FRRoom r : rooms) {
			String req = "INSERT INTO roomslist(building, room_number, type, capacity) VALUES(?, ?, ?, ?)";
			PreparedStatement query;
			try {
				query = conn.prepareStatement(req);

				// filling the query with values
				query.setString(1, r.getBuilding());
				query.setInt(2, Integer.parseInt(r.getNumber()));
				query.setString(3, "AUDITORIUM");
				query.setInt(4, 100);

				query.executeUpdate();
			} catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		int i = 0;
		for (ArrayList<FreeRoomRequest> r : globalAL) {
			i++;
			for (FreeRoomRequest frreq : r) {
				String req = "INSERT INTO roomsoccupancy (rid, timestampStart, timestampEnd) VALUES(?, ?, ?)";
				PreparedStatement query;
				try {
					query = conn.prepareStatement(req);

					// filling the query with values
					query.setInt(1, i);
					query.setLong(2, frreq.getPeriod().getTimeStampStart());
					query.setLong(3, frreq.getPeriod().getTimeStampEnd());

					query.executeUpdate();
				} catch (SQLException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}

		}
	}
	
	@Test
	public void testOccupancy() {
		
	}
}
