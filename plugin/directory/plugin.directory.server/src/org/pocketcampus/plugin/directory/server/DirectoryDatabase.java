package org.pocketcampus.plugin.directory.server;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

import org.pocketcampus.platform.sdk.server.database.ConnectionManager;
import org.pocketcampus.platform.sdk.server.database.handlers.exceptions.ServerException;

public class DirectoryDatabase {	
	private static final String DB_URL = "jdbc:mysql://pocketcampus.epfl.ch:3306/pocketcampus";
	private static final String DB_USERNAME = "pocketcampus";
	private static final String DB_PASSWORD = "pHEcNhrKAZMS5Hdp";
	
	private static final String TABLE_FIRSTNAME = "directory_firstname";
	private static final String TABLE_LASTNAME = "directory_lastname";
	
	private static final String LASTNAME_TITLE = "lastname";
	private static final String FIRSTNAME_TITLE = "firstname";
	
	private ConnectionManager connectionManager_;
	
	public DirectoryDatabase() {
		try {
			this.connectionManager_ = new ConnectionManager(DB_URL, DB_USERNAME, DB_PASSWORD);
		} catch (ServerException e) {
			e.printStackTrace();
		}
	}
	
	public List<String> getLastNames() {
		List<String> lastNames = new ArrayList<String>();
		
		try {
			Connection dbConnection = connectionManager_.getConnection();
			Statement statement = dbConnection.createStatement();
			ResultSet rs = statement.executeQuery("select * from " + TABLE_LASTNAME);

			while (rs.next()) {
				lastNames.add(rs.getString(LASTNAME_TITLE));
			}

			statement.close();
			connectionManager_.disconnect();
			
		} catch (SQLException e) {
			System.err.println("Error with SQL");
			e.printStackTrace();
		}
		
		return lastNames;
	}
	
	public List<String> getFirstNames() {
		List<String> firstNames = new ArrayList<String>();
		
		try {
			Connection dbConnection = connectionManager_.getConnection();
			Statement statement = dbConnection.createStatement();
			ResultSet rs = statement.executeQuery("select * from " + TABLE_FIRSTNAME);

			while (rs.next()) {
				firstNames.add(rs.getString(FIRSTNAME_TITLE));
			}

			statement.close();
			connectionManager_.disconnect();
			
		} catch (SQLException e) {
			System.err.println("Error with SQL");
			e.printStackTrace();
		}
		
		return firstNames;
	}
}
