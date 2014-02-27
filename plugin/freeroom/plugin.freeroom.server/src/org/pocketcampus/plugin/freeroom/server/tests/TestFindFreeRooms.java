package org.pocketcampus.plugin.freeroom.server.tests;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;

import junit.framework.Assert;

import org.apache.commons.io.IOUtils;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.pocketcampus.plugin.freeroom.shared.Room;
import org.pocketcampus.plugin.freeroom.shared.RoomType;


public class TestFindFreeRooms {
	final static String DB_USERNAME = "root";
	final static String DB_PASSWORD = "root";
	final static String DB_URL = "jdbc:mysql://localhost/?allowMultiQueries=true";
	private Connection conn = null;
	
	public static void createDBTest() {
		Connection conn = null;
		PreparedStatement pstmt = null;
		try {
			conn = DriverManager
					.getConnection(DB_URL, DB_USERNAME, DB_PASSWORD);
			
			PreparedStatement stmt = conn.prepareStatement("CREATE DATABASE IF NOT EXISTS pocketcampustest");
			stmt.execute();
			conn.setCatalog("pocketcampustest");
			
			File dbFile = new File("src/org/pocketcampus/plugin/freeroom/server/tests/testdb-create.sql");

			String query = IOUtils.toString(new FileReader(dbFile));
			pstmt = conn.prepareStatement(query);
			pstmt.execute();
			pstmt.close();
			// TODO: check that the database and two tables are successfully created ?
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
			conn = DriverManager
					.getConnection(DB_URL, DB_USERNAME, DB_PASSWORD);
			
			PreparedStatement stmt = conn.prepareStatement("DROP DATABASE pocketcampustest");
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
	}
	
	@AfterClass
	public static void tearDownAfterClass() {
		removeDBTest();
	}
	
	@Before
	public void setUp() {
		try {
			conn = DriverManager
					.getConnection(DB_URL, DB_USERNAME, DB_PASSWORD);
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
	
	// TODO: populate the database with fake values and extract expected results according to the values.
	
	@Test
	public void testPopulateRooms() {
		try {
			File dbFile = new File("src/org/pocketcampus/plugin/freeroom/server/tests/testdb-rooms.sql");

			String query = IOUtils.toString(new FileReader(dbFile));
			PreparedStatement pstmt = conn.prepareStatement(query);
			pstmt.execute();
			pstmt.close();
			
			PreparedStatement stmt = conn.prepareStatement("SELECT * FROM `pocketcampustest`.`roomslist`");
			stmt.execute();
			
			ResultSet resultQuery = stmt.getResultSet();
			ArrayList<Room> freerooms = new ArrayList<Room>();
			while (resultQuery.next()) {
				String building = resultQuery.getString("building");
				int room_number = resultQuery.getInt("room_number");
				String type = resultQuery.getString("type");
				int capacity = resultQuery.getInt("capacity");
				Room r = new Room();
				r.setBuilding(building);
				r.setNumber(room_number + "");
				r.setType(RoomType.valueOf(type));
				r.setCapacity(capacity);
				freerooms.add(r);
			}
			Assert.assertEquals(6, freerooms.size());
			for (Room room : freerooms) {
				System.out.println(room.toString());
			}
		} catch (SQLException e) {
			Assert.fail("There was an SQL Exception \n " + e);
		} catch (FileNotFoundException e) {
			Assert.fail("Thee test file for the database was not found \n " + e);
		} catch (IOException e) {
			Assert.fail("There was another IO Exception \n " + e);
		}
	}
	
	@Test
	public void testPopulateOccupancy() {
		try {
			File dbFile = new File("src/org/pocketcampus/plugin/freeroom/server/tests/testdb-occupancy.sql");

			String query = IOUtils.toString(new FileReader(dbFile));
			PreparedStatement pstmt = conn.prepareStatement(query);
			pstmt.execute();
			pstmt.close();
			
			PreparedStatement stmt = conn.prepareStatement("SELECT * FROM `pocketcampustest`.`roomsoccupancy`");
			stmt.execute();
			
			ResultSet resultQuery = stmt.getResultSet();
			ArrayList<int[]> freerooms = new ArrayList<int[]>();
			while (resultQuery.next()) {
				int[] r = new int [3];
				r[0] = resultQuery.getInt("rid");
				r[1] = resultQuery.getInt("day_number");
				r[2] = resultQuery.getInt("startHour");
				freerooms.add(r);
			}
			Assert.assertEquals(6, freerooms.size());
			for (int[] occ : freerooms) {
				System.out.println(Arrays.toString(occ));
			}
		} catch (SQLException e) {
			Assert.fail("There was an SQL Exception \n " + e);
		} catch (FileNotFoundException e) {
			Assert.fail("Thee test file for the database was not found \n " + e);
		} catch (IOException e) {
			Assert.fail("There was another IO Exception \n " + e);
		}
	}
}
