package org.pocketcampus.plugin.freeroom.server.tests;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;

import org.apache.commons.io.IOUtils;
import org.junit.Test;


public class TestFindFreeRooms {
	final String DB_USERNAME = "root";
	final String DB_PASSWORD = "root";
	final String DB_URL = "jdbc:mysql://localhost/?allowMultiQueries=true";
	
	public void populateDBTest() {
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
		} catch (SQLException e) {
			e.printStackTrace();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}

	}
	
	public void removeDBTest() {
		Connection conn = null;
		try {
			conn = DriverManager
					.getConnection(DB_URL, DB_USERNAME, DB_PASSWORD);
			
			PreparedStatement stmt = conn.prepareStatement("DROP DATABASE pocketcampustest");
			stmt.execute();
		} catch (SQLException e) {
			e.printStackTrace();
		} 
	}

	@Test
	public void test() {
		populateDBTest();
		
		removeDBTest();
	}

}
