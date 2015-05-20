package org.pocketcampus.platform.server.database;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class ConnectionManager {
	private static Connection connection;

	private String url;
	private String user;
	private String password;

	public ConnectionManager(String url, String user, String password) {
		this.url = url;
		this.user = user;
		this.password = password;

		// Register the JDBC driver for MySQL.
		// TODO why do it this way? can't we just instantiate it?
		try {
			Class.forName("com.mysql.jdbc.Driver");
		} catch (ClassNotFoundException e) {
			throw new RuntimeException(e);
		}
	}

	private void connect() throws SQLException {
		connection = DriverManager.getConnection(url, user, password);
	}

	public Connection getConnection() throws SQLException {
		if (connection == null || !connection.isValid(3)) {
			connect();
		}
		return connection;
	}
}