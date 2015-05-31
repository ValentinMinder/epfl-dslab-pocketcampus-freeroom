package org.pocketcampus.platform.server.database;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class ConnectionManager {
	private static Connection connection;

	private String url;
	private String user;
	private String password;

	private boolean useOwnConnection;
	private Connection ownConnection;

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

		this.useOwnConnection=false;
	}

	public ConnectionManager(String url, String user, String password, boolean useOwnConnection) {
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

		this.useOwnConnection=useOwnConnection;
	}

	public Connection getConnection() throws SQLException {
		if(useOwnConnection){
			if (ownConnection == null || !ownConnection.isValid(3)) {
				ownConnection = DriverManager.getConnection(url, user, password);
			}
			return ownConnection;
		}else{
			if (connection == null || !connection.isValid(3)) {
				connection = DriverManager.getConnection(url, user, password);
			}
			return connection;
		}
	}
}