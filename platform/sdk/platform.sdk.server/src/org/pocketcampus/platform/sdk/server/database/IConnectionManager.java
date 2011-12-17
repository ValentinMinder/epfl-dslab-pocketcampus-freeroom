package org.pocketcampus.platform.sdk.server.database;

import java.sql.Connection;
import java.sql.SQLException;

import org.pocketcampus.platform.sdk.server.database.handlers.exceptions.ServerException;



public interface IConnectionManager {
	/**
	 * Attempts to establish a connection to the database.
	 * @throws SQLException if a database access error occurs
	 */
	public void connect() throws SQLException;
	
	/**
	 * Disconnects immediately from the database instead of waiting for the automatic
	 * disconnection.
	 * @throws SQLException if a database access error occurs
	 */
	public void disconnect() throws SQLException;
	
	/**
	 * Returns the {@code Connection} object associated with the current database connection.
	 * @return the current {@code Connection} object
	 * @throws SQLException if a database access error occurs
	 */
	public Connection getConnection() throws ServerException, SQLException;
}
