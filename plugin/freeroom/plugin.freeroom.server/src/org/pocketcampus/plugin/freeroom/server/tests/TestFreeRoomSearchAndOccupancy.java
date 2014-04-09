package org.pocketcampus.plugin.freeroom.server.tests;

import static org.junit.Assert.assertTrue;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Map;
import java.util.Set;

import junit.framework.Assert;

import org.apache.commons.io.IOUtils;
import org.apache.thrift.TException;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.pocketcampus.platform.sdk.server.database.ConnectionManager;
import org.pocketcampus.platform.sdk.server.database.handlers.exceptions.ServerException;
import org.pocketcampus.plugin.freeroom.server.FreeRoomServiceImpl;
import org.pocketcampus.plugin.freeroom.server.FreeRoomServiceImplOld;
import org.pocketcampus.plugin.freeroom.server.utils.Utils;
import org.pocketcampus.plugin.freeroom.shared.ActualOccupation;
import org.pocketcampus.plugin.freeroom.shared.FRPeriod;
import org.pocketcampus.plugin.freeroom.shared.FRReply;
import org.pocketcampus.plugin.freeroom.shared.FRRequest;
import org.pocketcampus.plugin.freeroom.shared.FRRoom;
import org.pocketcampus.plugin.freeroom.shared.FreeRoomReply;
import org.pocketcampus.plugin.freeroom.shared.FreeRoomRequest;
import org.pocketcampus.plugin.freeroom.shared.Occupancy;
import org.pocketcampus.plugin.freeroom.shared.OccupancyReply;
import org.pocketcampus.plugin.freeroom.shared.OccupancyRequest;
import org.pocketcampus.plugin.freeroom.shared.utils.FRStruct;
import org.pocketcampus.plugin.freeroom.shared.utils.FRTimes;

/**
 * TESTS - Check Occupancy feature.
 * 
 * @author FreeRoom Project Team - Julien WEBER <julien.weber@epfl.ch> and
 *         Valentin MINDER <valentin.minder@epfl.ch>
 * 
 */

public class TestFreeRoomSearchAndOccupancy {
	final static String DB_USERNAME = "root";
	final static String DB_PASSWORD = "root";
	final static String DBMS_URL = "jdbc:mysql://localhost/?allowMultiQueries=true";
	final static String DB_URL = "jdbc:mysql://localhost/pocketcampustest?allowMultiQueries=true";
	final static String DB_NOTTEST_URL = "jdbc:mysql://localhost/pocketcampus?allowMultiQueries=true";

	final static long ONE_HOUR_MS = 3600 * 1000;
	// we allow a margin for error
	final static long MARGIN_ERROR_MS = 60 * 1000;
	private Connection conn = null;

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
		// createDBTest();
		// populate();
	}

	@AfterClass
	public static void tearDownAfterClass() {
		// TODO: tests should remove their databases and tables, comment it if
		// you want to see them in SQL
		// removeDBTest();
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
			conn = DriverManager
					.getConnection(DB_URL, DB_USERNAME, DB_PASSWORD);
		} catch (SQLException e1) {
			e1.printStackTrace();
			// return;
		}
		ArrayList<FRRoom> rooms = new ArrayList<FRRoom>();
		rooms.add(new FRRoom("001", "CO 1"));
		rooms.add(new FRRoom("002", "CO 2"));
		rooms.add(new FRRoom("003", "CO 3"));
		rooms.add(new FRRoom("011", "CM 1"));
		rooms.add(new FRRoom("012", "CM 2"));
		rooms.add(new FRRoom("013", "CM 3"));

		ArrayList<ArrayList<FreeRoomRequest>> globalAL = new ArrayList<ArrayList<FreeRoomRequest>>();
		ArrayList<FreeRoomRequest> requestco1 = new ArrayList<FreeRoomRequest>();
		ArrayList<FreeRoomRequest> requestco2 = new ArrayList<FreeRoomRequest>();
		ArrayList<FreeRoomRequest> requestco3 = new ArrayList<FreeRoomRequest>();
		ArrayList<FreeRoomRequest> requestcm1 = new ArrayList<FreeRoomRequest>();
		ArrayList<FreeRoomRequest> requestcm2 = new ArrayList<FreeRoomRequest>();
		ArrayList<FreeRoomRequest> requestcm3 = new ArrayList<FreeRoomRequest>();

		globalAL.add(requestco1);
		globalAL.add(requestco2);
		globalAL.add(requestco3);
		globalAL.add(requestcm1);
		globalAL.add(requestcm2);
		globalAL.add(requestcm3);

		// CO1 occupancies
		requestco1.add(FRTimes.convert(Calendar.MONDAY, 8, 9));
		requestco1.add(FRTimes.convert(Calendar.THURSDAY, 12, 13));
		// CO2
		requestco2.add(FRTimes.convert(Calendar.MONDAY, 11, 12));
		requestco2.add(FRTimes.convert(Calendar.WEDNESDAY, 8, 12));
		// CO3
		requestco3.add(FRTimes.convert(Calendar.MONDAY, 15, 16));
		requestco3.add(FRTimes.convert(Calendar.TUESDAY, 15, 16));
		requestco3.add(FRTimes.convert(Calendar.THURSDAY, 10, 11));
		// CM1
		requestcm1.add(FRTimes.convert(Calendar.MONDAY, 8, 9));
		requestcm1.add(FRTimes.convert(Calendar.MONDAY, 10, 12));
		requestcm1.add(FRTimes.convert(Calendar.WEDNESDAY, 8, 9));
		// CM2
		requestcm2.add(FRTimes.convert(Calendar.MONDAY, 15, 16));
		requestcm2.add(FRTimes.convert(Calendar.TUESDAY, 12, 14));
		// CM3
		requestcm3.add(FRTimes.convert(Calendar.MONDAY, 15, 16));
		requestcm3.add(FRTimes.convert(Calendar.WEDNESDAY, 10, 11));

		// insert the rooms
		for (FRRoom r : rooms) {
			String req = "INSERT INTO `fr-roomslist`(uid, doorCode, type, capacity) VALUES(?, ?, ?, ?)";
			PreparedStatement query;
			try {
				query = conn.prepareStatement(req);

				// filling the query with values
				query.setString(1, r.getUid());
				query.setInt(2, Integer.parseInt(r.getDoorCode()));
				query.setString(3, "AUDITORIUM");
				query.setInt(4, 100);

				query.executeUpdate();
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
		int i = 0;
		for (ArrayList<FreeRoomRequest> r : globalAL) {
			i++;
			for (FreeRoomRequest frreq : r) {
				String req = "INSERT INTO fr-roomsoccupancy (rid, timestampStart, timestampEnd) VALUES(?, ?, ?)";
				PreparedStatement query;
				try {
					query = conn.prepareStatement(req);

					// filling the query with values
					query.setInt(1, i);
					query.setLong(2, frreq.getPeriod().getTimeStampStart());
					query.setLong(3, frreq.getPeriod().getTimeStampEnd());

					query.executeUpdate();
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}

		}
	}

	@Test
	public void testOccupancyMonday8_12CO1() {
		try {
			FreeRoomServiceImplOld server = new FreeRoomServiceImplOld(
					new ConnectionManager(DB_URL, DB_USERNAME, DB_PASSWORD));
			ArrayList<FRRoom> roomsList = new ArrayList<FRRoom>();
			FRPeriod period = new FRPeriod();
			OccupancyRequest request = null;
			OccupancyReply reply = null;

			// test1
			roomsList.add(new FRRoom("001", "C0 1"));
			period = FRTimes.convert(Calendar.MONDAY, 8, 12).getPeriod();
			request = new OccupancyRequest(FRStruct.getListUID(roomsList),
					period);
			reply = server.checkTheOccupancy(request);

			assertTrue(reply.getOccupancyOfRoomsSize() == 1);

			Occupancy mOcc = reply.getOccupancyOfRooms().get(0);
			FRRoom room = mOcc.getRoom();
			assertTrue(room.getUid().equals(roomsList.get(0).getUid()));
			assertTrue(room.getDoorCode()
					.equals(roomsList.get(0).getDoorCode()));

			List<ActualOccupation> mAccOcc = mOcc.getOccupancy();
			assertTrue("size = " + mAccOcc.size(), mAccOcc.size() == 2);

			ActualOccupation firstOcc = mAccOcc.get(0);
			long durationFirstOcc = firstOcc.getPeriod().getTimeStampEnd()
					- firstOcc.getPeriod().getTimeStampStart();
			assertTrue("First occupancy has wrong start, end, duration = "
					+ durationFirstOcc,
					Math.abs(durationFirstOcc - ONE_HOUR_MS) < MARGIN_ERROR_MS);
			assertTrue("First occupancy is not free", !firstOcc.isAvailable());

			ActualOccupation nextOcc = mAccOcc.get(1);
			long durationNextOcc = nextOcc.getPeriod().getTimeStampEnd()
					- nextOcc.getPeriod().getTimeStampStart();
			assertTrue(
					"Next occupancy has wrong start, end, duration = "
							+ durationNextOcc,
					Math.abs(durationNextOcc - ONE_HOUR_MS * 3) < MARGIN_ERROR_MS);
			assertTrue("First occupancy is free", nextOcc.isAvailable());

		} catch (ServerException e) {
			e.printStackTrace();
			Assert.fail("There was an Exception thrown");
		} catch (TException e) {
			e.printStackTrace();
			Assert.fail("There was an Exception thrown");
		}
	}

	@Test
	public void testOccupancyMonday8_19CM1() {
		try {
			FreeRoomServiceImplOld server = new FreeRoomServiceImplOld(
					new ConnectionManager(DB_URL, DB_USERNAME, DB_PASSWORD));
			ArrayList<FRRoom> roomsList = new ArrayList<FRRoom>();
			FRPeriod period = new FRPeriod();
			OccupancyRequest request = null;
			OccupancyReply reply = null;

			// test1
			roomsList.add(new FRRoom("011", "CM 1"));
			period = FRTimes.convert(Calendar.MONDAY, 8, 19).getPeriod();
			request = new OccupancyRequest(FRStruct.getListUID(roomsList),
					period);
			reply = server.checkTheOccupancy(request);

			assertTrue(reply.getOccupancyOfRoomsSize() == 1);

			Occupancy mOcc = reply.getOccupancyOfRooms().get(0);
			FRRoom room = mOcc.getRoom();
			assertTrue(room.getUid().equals(roomsList.get(0).getUid()));
			assertTrue(room.getDoorCode()
					.equals(roomsList.get(0).getDoorCode()));

			List<ActualOccupation> mAccOcc = mOcc.getOccupancy();
			assertTrue("size = " + mAccOcc.size(), mAccOcc.size() == 4);

			ActualOccupation firstOcc = mAccOcc.get(0);
			long durationFirstOcc = firstOcc.getPeriod().getTimeStampEnd()
					- firstOcc.getPeriod().getTimeStampStart();
			assertTrue("First occupancy has wrong start, end, duration = "
					+ durationFirstOcc,
					Math.abs(durationFirstOcc - ONE_HOUR_MS) < MARGIN_ERROR_MS);
			assertTrue("First occupancy is not free", !firstOcc.isAvailable());

			ActualOccupation nextOcc = mAccOcc.get(1);
			long durationNextOcc = nextOcc.getPeriod().getTimeStampEnd()
					- nextOcc.getPeriod().getTimeStampStart();
			assertTrue("Next occupancy has wrong start, end, duration = "
					+ durationNextOcc,
					Math.abs(durationNextOcc - ONE_HOUR_MS) < MARGIN_ERROR_MS);
			assertTrue("First occupancy is free", nextOcc.isAvailable());

			nextOcc = mAccOcc.get(2);
			durationNextOcc = nextOcc.getPeriod().getTimeStampEnd()
					- nextOcc.getPeriod().getTimeStampStart();
			assertTrue(
					"Next occupancy has wrong start, end, duration = "
							+ durationNextOcc,
					Math.abs(durationNextOcc - ONE_HOUR_MS * 2) < MARGIN_ERROR_MS);
			assertTrue("First occupancy is free", !nextOcc.isAvailable());

			nextOcc = mAccOcc.get(3);
			durationNextOcc = nextOcc.getPeriod().getTimeStampEnd()
					- nextOcc.getPeriod().getTimeStampStart();
			assertTrue(
					"Next occupancy has wrong start, end, duration = "
							+ durationNextOcc,
					Math.abs(durationNextOcc - ONE_HOUR_MS * 7) < MARGIN_ERROR_MS);
			assertTrue("First occupancy is free", nextOcc.isAvailable());

		} catch (ServerException e) {
			e.printStackTrace();
			Assert.fail("There was an Exception thrown");
		} catch (TException e) {
			e.printStackTrace();
			Assert.fail("There was an Exception thrown");
		}
	}

	@Test
	public void testOccupancyTuesday8_19CM2CO3CO1() {
		try {
			FreeRoomServiceImplOld server = new FreeRoomServiceImplOld(
					new ConnectionManager(DB_URL, DB_USERNAME, DB_PASSWORD));
			ArrayList<FRRoom> roomsList = new ArrayList<FRRoom>();
			FRPeriod period = new FRPeriod();
			OccupancyRequest request = null;
			OccupancyReply reply = null;

			// test1
			roomsList.add(new FRRoom("012", "CM 2"));
			roomsList.add(new FRRoom("003", "CO 3"));
			roomsList.add(new FRRoom("001", "CO 1"));
			period = FRTimes.convert(Calendar.TUESDAY, 8, 19).getPeriod();
			request = new OccupancyRequest(FRStruct.getListUID(roomsList),
					period);
			reply = server.checkTheOccupancy(request);

			assertTrue(reply.getOccupancyOfRoomsSize() == 3);

			// first room is CM2
			Occupancy mOcc = reply.getOccupancyOfRooms().get(0);
			FRRoom room = mOcc.getRoom();
			assertTrue(room.getUid().equals(roomsList.get(0).getUid()));
			assertTrue(room.getDoorCode()
					.equals(roomsList.get(0).getDoorCode()));

			List<ActualOccupation> mAccOcc = mOcc.getOccupancy();
			assertTrue("size = " + mAccOcc.size(), mAccOcc.size() == 3);

			// second room is CO3
			mOcc = reply.getOccupancyOfRooms().get(1);
			room = mOcc.getRoom();
			assertTrue(room.getUid().equals(roomsList.get(1).getUid()));
			assertTrue(room.getDoorCode()
					.equals(roomsList.get(1).getDoorCode()));

			mAccOcc = mOcc.getOccupancy();
			assertTrue("size = " + mAccOcc.size(), mAccOcc.size() == 3);

			// last room is CO1
			mOcc = reply.getOccupancyOfRooms().get(2);
			room = mOcc.getRoom();
			assertTrue(room.getUid().equals(roomsList.get(2).getUid()));
			assertTrue(room.getDoorCode()
					.equals(roomsList.get(2).getDoorCode()));

			mAccOcc = mOcc.getOccupancy();
			assertTrue("size = " + mAccOcc.size(), mAccOcc.size() == 1);

		} catch (ServerException e) {
			e.printStackTrace();
			Assert.fail("There was an Exception thrown");
		} catch (TException e) {
			e.printStackTrace();
			Assert.fail("There was an Exception thrown");
		}
	}

	@Test
	public void testFreeRoomFriday8_19() {
		try {
			FreeRoomServiceImplOld server = new FreeRoomServiceImplOld(
					new ConnectionManager(DB_URL, DB_USERNAME, DB_PASSWORD));
			FreeRoomRequest request = null;
			FreeRoomReply reply = null;
			request = FRTimes.convert(Calendar.FRIDAY, 8, 19);

			reply = server.getFreeRoomFromTime(request);

			assertTrue("Every rooms are availables, : " + reply.getRoomsSize(),
					reply.getRoomsSize() == 6);

		} catch (ServerException e) {
			e.printStackTrace();
			Assert.fail("There was an Exception thrown");
		} catch (TException e) {
			e.printStackTrace();
			Assert.fail("There was an Exception thrown");
		}
	}

	@Test
	public void testFreeRoomMonday9_10() {
		try {
			FreeRoomServiceImplOld server = new FreeRoomServiceImplOld(
					new ConnectionManager(DB_URL, DB_USERNAME, DB_PASSWORD));
			FreeRoomRequest request = null;
			FreeRoomReply reply = null;
			request = FRTimes.convert(Calendar.MONDAY, 9, 10);

			reply = server.getFreeRoomFromTime(request);

			for (FRRoom r : reply.getRooms()) {
				System.out.println(r);
			}
			assertTrue("Every rooms are availables, : " + reply.getRoomsSize(),
					reply.getRoomsSize() == 6);

		} catch (ServerException e) {
			e.printStackTrace();
			Assert.fail("There was an Exception thrown");
		} catch (TException e) {
			e.printStackTrace();
			Assert.fail("There was an Exception thrown");
		}
	}

	@Test
	public void testFreeRoomMonday9_12() {
		try {
			FreeRoomServiceImplOld server = new FreeRoomServiceImplOld(
					new ConnectionManager(DB_URL, DB_USERNAME, DB_PASSWORD));
			FreeRoomRequest request = null;
			FreeRoomReply reply = null;
			request = FRTimes.convert(Calendar.MONDAY, 9, 12);

			reply = server.getFreeRoomFromTime(request);

			assertTrue(reply.getRoomsSize() == 4);

		} catch (ServerException e) {
			e.printStackTrace();
			Assert.fail("There was an Exception thrown");
		} catch (TException e) {
			e.printStackTrace();
			Assert.fail("There was an Exception thrown");
		}
	}

	@Test
	public void testFreeRoomMonday9_11() {
		try {
			FreeRoomServiceImplOld server = new FreeRoomServiceImplOld(
					new ConnectionManager(DB_URL, DB_USERNAME, DB_PASSWORD));
			FreeRoomRequest request = null;
			FreeRoomReply reply = null;
			request = FRTimes.convert(Calendar.MONDAY, 9, 11);

			reply = server.getFreeRoomFromTime(request);

			assertTrue(reply.getRoomsSize() == 5);
			Set<FRRoom> rooms = reply.getRooms();
			assertTrue(rooms.contains(new FRRoom("CO", "1")));
			assertTrue(rooms.contains(new FRRoom("CO", "2")));
			assertTrue(rooms.contains(new FRRoom("CO", "3")));
			assertTrue(rooms.contains(new FRRoom("CM", "2")));
			assertTrue(rooms.contains(new FRRoom("CM", "3")));

		} catch (ServerException e) {
			e.printStackTrace();
			Assert.fail("There was an Exception thrown");
		} catch (TException e) {
			e.printStackTrace();
			Assert.fail("There was an Exception thrown");
		}
	}

	@Test
	public void testFreeRoomTuesday13_16() {
		try {
			FreeRoomServiceImplOld server = new FreeRoomServiceImplOld(
					new ConnectionManager(DB_URL, DB_USERNAME, DB_PASSWORD));
			FreeRoomRequest request = null;
			FreeRoomReply reply = null;
			request = FRTimes.convert(Calendar.TUESDAY, 13, 16);

			reply = server.getFreeRoomFromTime(request);

			assertTrue(reply.getRoomsSize() == 4);
			Set<FRRoom> rooms = reply.getRooms();
			assertTrue(rooms.contains(new FRRoom("CO", "1")));
			assertTrue(rooms.contains(new FRRoom("CO", "2")));
			assertTrue(rooms.contains(new FRRoom("CM", "1")));
			assertTrue(rooms.contains(new FRRoom("CM", "3")));

		} catch (ServerException e) {
			e.printStackTrace();
			Assert.fail("There was an Exception thrown");
		} catch (TException e) {
			e.printStackTrace();
			Assert.fail("There was an Exception thrown");
		}
	}

	@Test
	public void testStartAfterEnd() {
		try {
			FreeRoomServiceImplOld server = new FreeRoomServiceImplOld(
					new ConnectionManager(DB_URL, DB_USERNAME, DB_PASSWORD));
			FreeRoomRequest request = null;
			FreeRoomReply reply = null;
			request = FRTimes.convert(Calendar.TUESDAY, 13, 16);
			FreeRoomRequest request2 = FRTimes
					.convert(Calendar.TUESDAY, 12, 14);

			request.setPeriod(new FRPeriod(request.getPeriod()
					.getTimeStampStart(), request2.getPeriod()
					.getTimeStampStart(), false));

			reply = server.getFreeRoomFromTime(request);
			assertTrue("Code is " + reply.getStatus(),
					reply.getStatus() == HttpURLConnection.HTTP_BAD_REQUEST);

		} catch (ServerException e) {
			e.printStackTrace();
			Assert.fail("There was an Exception thrown");
		} catch (TException e) {
			e.printStackTrace();
			Assert.fail("There was an Exception thrown");
		}
	}

	@Test
	public void testDayOutsideRange() {
		try {
			FreeRoomServiceImplOld server = new FreeRoomServiceImplOld(
					new ConnectionManager(DB_URL, DB_USERNAME, DB_PASSWORD));
			FreeRoomRequest request = null;
			FreeRoomReply reply = null;
			request = FRTimes.convert(Calendar.SATURDAY, 13, 16);

			reply = server.getFreeRoomFromTime(request);
			assertTrue("Code is " + reply.getStatus(),
					reply.getStatus() == HttpURLConnection.HTTP_BAD_REQUEST);

		} catch (ServerException e) {
			e.printStackTrace();
			Assert.fail("There was an Exception thrown");
		} catch (TException e) {
			e.printStackTrace();
			Assert.fail("There was an Exception thrown");
		}
	}

	@Test
	public void testStartHourOutsideRange() {
		try {
			FreeRoomServiceImplOld server = new FreeRoomServiceImplOld(
					new ConnectionManager(DB_URL, DB_USERNAME, DB_PASSWORD));
			FreeRoomRequest request = null;
			FreeRoomReply reply = null;
			request = FRTimes.convert(Calendar.MONDAY, 7, 16);

			reply = server.getFreeRoomFromTime(request);
			assertTrue("Code is " + reply.getStatus(),
					reply.getStatus() == HttpURLConnection.HTTP_BAD_REQUEST);

		} catch (ServerException e) {
			e.printStackTrace();
			Assert.fail("There was an Exception thrown");
		} catch (TException e) {
			e.printStackTrace();
			Assert.fail("There was an Exception thrown");
		}
	}

	@Test
	public void testEndHourOutsideRange() {
		try {
			FreeRoomServiceImplOld server = new FreeRoomServiceImplOld(
					new ConnectionManager(DB_URL, DB_USERNAME, DB_PASSWORD));
			FreeRoomRequest request = null;
			FreeRoomReply reply = null;
			request = FRTimes.convert(Calendar.MONDAY, 9, 20);

			FRPeriod period = request.getPeriod();
			Calendar mCalendar = Calendar.getInstance();
			mCalendar.setTimeInMillis(period.getTimeStampEnd());
			assertTrue(mCalendar.get(Calendar.HOUR_OF_DAY) == 20);
			reply = server.getFreeRoomFromTime(request);

			assertTrue(
					"Code is " + reply.getStatus() + " message = "
							+ reply.getStatusComment(),
					reply.getStatus() == HttpURLConnection.HTTP_BAD_REQUEST);

		} catch (ServerException e) {
			e.printStackTrace();
			Assert.fail("There was an Exception thrown");
		} catch (TException e) {
			e.printStackTrace();
			Assert.fail("There was an Exception thrown");
		}
	}

	@Test
	public void testRequestIsNotModifiedMonday11h30_13CM1() {
		try {
			FreeRoomServiceImplOld server = new FreeRoomServiceImplOld(
					new ConnectionManager(DB_URL, DB_USERNAME, DB_PASSWORD));
			ArrayList<FRRoom> roomsList = new ArrayList<FRRoom>();
			FRPeriod period = new FRPeriod();
			OccupancyRequest request = null;
			OccupancyReply reply = null;

			roomsList.add(new FRRoom("CM", "1"));
			period = FRTimes.convertWithMinPrecision(Calendar.MONDAY, 11, 30,
					13, 00).getPeriod();
			long queryFirstTS = period.getTimeStampStart();
			long queryLastTS = period.getTimeStampEnd();

			request = new OccupancyRequest(FRStruct.getListUID(roomsList),
					period);
			reply = server.checkTheOccupancy(request);

			assertTrue(reply.getOccupancyOfRoomsSize() == 1);

			Occupancy mOcc = reply.getOccupancyOfRooms().get(0);
			List<ActualOccupation> actOcc = mOcc.getOccupancy();
			long firstTS = actOcc.get(0).getPeriod().getTimeStampStart();
			long lastTS = actOcc.get(actOcc.size() - 1).getPeriod()
					.getTimeStampEnd();

			assertTrue(Math.abs(firstTS - queryFirstTS) < MARGIN_ERROR_MS * 5);
			assertTrue(Math.abs(lastTS - queryLastTS) < MARGIN_ERROR_MS * 5);

		} catch (ServerException e) {
			e.printStackTrace();
			Assert.fail("There was an Exception thrown");
		} catch (TException e) {
			e.printStackTrace();
			Assert.fail("There was an Exception thrown");
		}
	}

	@Test
	public void testRequestIsNotModifiedTueday10_12h30CM2() {
		try {
			FreeRoomServiceImplOld server = new FreeRoomServiceImplOld(
					new ConnectionManager(DB_URL, DB_USERNAME, DB_PASSWORD));
			ArrayList<FRRoom> roomsList = new ArrayList<FRRoom>();
			FRPeriod period = new FRPeriod();
			OccupancyRequest request = null;
			OccupancyReply reply = null;

			roomsList.add(new FRRoom("CM", "2"));
			period = FRTimes.convertWithMinPrecision(Calendar.TUESDAY, 10, 00,
					12, 30).getPeriod();
			long queryFirstTS = period.getTimeStampStart();
			long queryLastTS = period.getTimeStampEnd();

			request = new OccupancyRequest(FRStruct.getListUID(roomsList),
					period);
			reply = server.checkTheOccupancy(request);

			assertTrue(reply.getOccupancyOfRoomsSize() == 1);

			Occupancy mOcc = reply.getOccupancyOfRooms().get(0);
			List<ActualOccupation> actOcc = mOcc.getOccupancy();
			long firstTS = actOcc.get(0).getPeriod().getTimeStampStart();
			long lastTS = actOcc.get(actOcc.size() - 1).getPeriod()
					.getTimeStampEnd();

			assertTrue(Math.abs(firstTS - queryFirstTS) < MARGIN_ERROR_MS * 5);
			assertTrue("diff = " + (Math.abs(lastTS - queryLastTS))
					/ (3600 * 1000),
					Math.abs(lastTS - queryLastTS) < MARGIN_ERROR_MS * 5);

		} catch (ServerException e) {
			e.printStackTrace();
			Assert.fail("There was an Exception thrown");
		} catch (TException e) {
			e.printStackTrace();
			Assert.fail("There was an Exception thrown");
		}
	}

	@Test
	public void testRequestIsNotModifiedTueday10h14_12h30CM1() {
		try {
			FreeRoomServiceImplOld server = new FreeRoomServiceImplOld(
					new ConnectionManager(DB_URL, DB_USERNAME, DB_PASSWORD));
			ArrayList<FRRoom> roomsList = new ArrayList<FRRoom>();
			FRPeriod period = new FRPeriod();
			OccupancyRequest request = null;
			OccupancyReply reply = null;

			roomsList.add(new FRRoom("CM", "2"));
			period = FRTimes.convertWithMinPrecision(Calendar.TUESDAY, 10, 14,
					12, 30).getPeriod();
			long queryFirstTS = period.getTimeStampStart();
			long queryLastTS = period.getTimeStampEnd();

			request = new OccupancyRequest(FRStruct.getListUID(roomsList),
					period);
			reply = server.checkTheOccupancy(request);

			assertTrue(reply.getOccupancyOfRoomsSize() == 1);

			Occupancy mOcc = reply.getOccupancyOfRooms().get(0);
			List<ActualOccupation> actOcc = mOcc.getOccupancy();
			long firstTS = actOcc.get(0).getPeriod().getTimeStampStart();
			long lastTS = actOcc.get(actOcc.size() - 1).getPeriod()
					.getTimeStampEnd();

			assertTrue(Math.abs(firstTS - queryFirstTS) < MARGIN_ERROR_MS * 5);
			assertTrue("diff = " + (Math.abs(lastTS - queryLastTS))
					/ (3600 * 1000),
					Math.abs(lastTS - queryLastTS) < MARGIN_ERROR_MS * 5);

		} catch (ServerException e) {
			e.printStackTrace();
			Assert.fail("There was an Exception thrown");
		} catch (TException e) {
			e.printStackTrace();
			Assert.fail("There was an Exception thrown");
		}
	}

	@Test
	public void testNewAnyRoomRequest() {
		try {
			FreeRoomServiceImpl server = new FreeRoomServiceImpl(
					new ConnectionManager(DB_NOTTEST_URL, DB_USERNAME,
							DB_PASSWORD));

			FRRequest request = new FRRequest(
					FRTimes.convertWithMinPrecisionFRPeriod(Calendar.WEDNESDAY,
							9, 0, 15, 0), true, null);

			System.out.println(FRTimes.convertWithMinPrecisionFRPeriod(
					Calendar.WEDNESDAY, 9, 00, 15, 0).getTimeStampStart()
					% Utils.ONE_HOUR_MS);
			FRReply reply = server.getOccupancy(request);
			Map<String, List<Occupancy>> result = reply.getOccupancyOfRooms();
			SimpleDateFormat day_month = new SimpleDateFormat(
					"EEEE MMMM dd / HH:mm");
			Set<String> buildings = result.keySet();
			System.out.println(buildings.size() + " buildings");
			for (String currentBuilding : buildings) {
				List<Occupancy> occOfBuilding = result.get(currentBuilding);

				for (Occupancy mOcc : occOfBuilding) {
					System.out.println(mOcc.getRoom().getDoorCode() + " "
							+ mOcc.getRatioWorstCaseProbableOccupancy());
					List<ActualOccupation> accOcc = mOcc.getOccupancy();

					for (ActualOccupation mAccOcc : accOcc) {
						Calendar calendarStart = Calendar.getInstance();
						calendarStart.setTimeInMillis(mAccOcc.getPeriod()
								.getTimeStampStart());

						Calendar calendarEnd = Calendar.getInstance();
						calendarEnd.setTimeInMillis(mAccOcc.getPeriod()
								.getTimeStampEnd());

						System.out.println("From "
								+ day_month.format(calendarStart.getTime())
								+ " to "
								+ day_month.format(calendarEnd.getTime())
								+ " available : " + mAccOcc.isAvailable());
					}
				}
			}
		} catch (ServerException e) {
			e.printStackTrace();
			Assert.fail("There was an Exception thrown");
		} catch (TException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	@Test
	public void testNewSpecificRoomRequest() {
		try {
			FreeRoomServiceImpl server = new FreeRoomServiceImpl(
					new ConnectionManager(DB_NOTTEST_URL, DB_USERNAME,
							DB_PASSWORD));
			SimpleDateFormat day_month = new SimpleDateFormat(
					"EEEE MMMM dd / HH:mm");
			ArrayList<String> uidList = new ArrayList<>();
			uidList.add("12205");
			uidList.add("12206");
			FRRequest request = new FRRequest(
					FRTimes.convertWithMinPrecisionFRPeriod(Calendar.THURSDAY,
							8, 00, 16, 00), false, uidList);
			FRReply reply = server.getOccupancy(request);
			Map<String, List<Occupancy>> result = reply.getOccupancyOfRooms();

			Set<String> buildings = result.keySet();
			System.out.println(buildings.size() + " buildings");

			for (String currentBuilding : buildings) {
				List<Occupancy> occOfBuilding = result.get(currentBuilding);

				for (Occupancy mOcc : occOfBuilding) {
					System.out.println(mOcc.getRoom().getDoorCode() + " "
							+ mOcc.getRatioWorstCaseProbableOccupancy());
					List<ActualOccupation> accOcc = mOcc.getOccupancy();

					for (ActualOccupation mAccOcc : accOcc) {
						Calendar calendarStart = Calendar.getInstance();
						calendarStart.setTimeInMillis(mAccOcc.getPeriod()
								.getTimeStampStart());

						Calendar calendarEnd = Calendar.getInstance();
						calendarEnd.setTimeInMillis(mAccOcc.getPeriod()
								.getTimeStampEnd());

						System.out.println("From "
								+ day_month.format(calendarStart.getTime())
								+ " to "
								+ day_month.format(calendarEnd.getTime())
								+ " available : " + mAccOcc.isAvailable()
								+ " people = "
								+ mAccOcc.getProbableOccupation() + " ratio = "
								+ mAccOcc.getRatioOccupation());
					}
				}
			}
		} catch (ServerException e) {
			e.printStackTrace();
			Assert.fail("There was an Exception thrown");
		} catch (TException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}