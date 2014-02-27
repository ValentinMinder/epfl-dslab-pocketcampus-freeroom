package org.pocketcampus.plugin.freeroom.server.tests;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;

import junit.framework.Assert;
import org.apache.commons.io.IOUtils;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;


public class TestFindFreeRooms {
	final String DB_USERNAME = "root";
	final String DB_PASSWORD = "root";
	final String DB_URL = "jdbc:mysql://localhost/?allowMultiQueries=true";
	
	public void createDBTest() {
		Connection conn = null;
		PreparedStatement pstmt = null;
		try {
			conn = DriverManager
					.getConnection(DB_URL, DB_USERNAME, DB_PASSWORD);
			
			PreparedStatement stmt = conn.prepareStatement("CREATE DATABASE IF NOT EXISTS pocketcampustest");
			stmt.execute();
			conn.setCatalog("pocketcampustest");
			
			File dbFile = new File("src/org/pocketcampus/plugin/freeroom/server/tests/testdb.sql");

			String query = IOUtils.toString(new FileReader(dbFile));
			pstmt = conn.prepareStatement(query);
			pstmt.execute();
			
			// TODO: check that the database and two tables are successfully created ?
		} catch (SQLException e) {
			Assert.fail("There was an SQL Exception \n " + e);
		} catch (FileNotFoundException e) {
			Assert.fail("Thee test file for the database was not found \n " + e);
		} catch (IOException e) {
			Assert.fail("There was another IO Exception \n " + e);
		}

	}
	
	public void removeDBTest() {
		Connection conn = null;
		try {
			conn = DriverManager
					.getConnection(DB_URL, DB_USERNAME, DB_PASSWORD);
			
			PreparedStatement stmt = conn.prepareStatement("DROP DATABASE pocketcampustest");
			stmt.execute();
			
			// TODO : check that the database is successfully deleted ?
		} catch (SQLException e) {
			Assert.fail("There was an SQL Exception \n " + e);
		} 
	}

	@Before
	public void setUp() {
		createDBTest();
	}
	
	@After
	public void finalize() {
		removeDBTest();
	}
	
	@Test
	public void testPopulateDB() {
		// TODO: populate the database with fake values and extract expected results according to the values.
	}
}
