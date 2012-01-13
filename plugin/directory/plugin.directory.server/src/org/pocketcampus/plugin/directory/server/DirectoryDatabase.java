package org.pocketcampus.plugin.directory.server;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

import org.pocketcampus.platform.sdk.server.database.ConnectionManager;
import org.pocketcampus.platform.sdk.server.database.handlers.exceptions.ServerException;

/**
 * 
 * @author Pascal <pascal.scheiben@gmail.com>
 * @author Florian <florian.laurent@gmail.com>
 */
public class DirectoryDatabase {	
	/** url of the database */
	private static final String DB_URL = "jdbc:mysql://pocketcampus.epfl.ch:3306/pocketcampus";
	/** username of the database*/
	private static final String DB_USERNAME = "pocketcampus";
	/** password of the database*/
	private static final String DB_PASSWORD = "pHEcNhrKAZMS5Hdp";
	
	/** Name of the table containing the first names*/
	private static final String TABLE_FIRSTNAME = "directory_firstname";
	/** Name of the table containing the last names*/
	private static final String TABLE_LASTNAME = "directory_lastname";
	
	/** Name of the field containing the first names */
	private static final String LASTNAME_TITLE = "lastname";
	/** Name of the field containing the last names */
	private static final String FIRSTNAME_TITLE = "firstname";
	
	/** Database connection manager*/
	private ConnectionManager connectionManager_;
	
	/** 
	 * Constructor
	 * 
	 */
	public DirectoryDatabase() {
		try {
			this.connectionManager_ = new ConnectionManager(DB_URL, DB_USERNAME, DB_PASSWORD);
		} catch (ServerException e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * Get all the last name contained in the database
	 * @return List of last names
	 */
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
	
	/**
	 * Get all the first names contained in the database
	 * @return List of first names
	 */
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
