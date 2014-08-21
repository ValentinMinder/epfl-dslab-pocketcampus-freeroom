package org.pocketcampus.platform.server.database;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class ConnectionManager {
	private String url;
	private String user;
	private String password;
	private Connection connection;

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

	public void connect() throws SQLException {
		this.connection = DriverManager.getConnection(url, user, password);
	}

	public void disconnect() throws SQLException {
		this.connection.close();
		this.connection = null;
	}

	public Connection getConnection() throws SQLException {
		if (this.connection == null || !this.connection.isValid(1)) {
			connect();
		}
		return this.connection;
	}
}