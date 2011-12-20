package org.pocketcampus.platform.sdk.server.database;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

import org.pocketcampus.platform.sdk.server.database.handlers.exceptions.ServerException;

public class ConnectionManager implements IConnectionManager {
	private String url;
	private String user;
	private String password;
	private Connection connection;
	
	public ConnectionManager() throws ServerException {
//		this("jdbc:mysql://ec2-46-51-131-245.eu-west-1.compute.amazonaws.com:3306/pocketbuddy", "pocketbuddy", "");
		throw new RuntimeException("NON! Mauvais constructeur, tête de linotte.");
	}
	
	public ConnectionManager(String url, String user, String password) throws ServerException {
		this.url = url;		// Ex. for DB 'pocketbuddy' : "jdbc:mysql://localhost:3306/pocketbuddy"
		this.user = user;
		this.password = password;
		
		// Register the JDBC driver for MySQL.
		try {
			Class.forName("com.mysql.jdbc.Driver");
		} catch (ClassNotFoundException e) {
			throw new ServerException("MySQL JDBC Driver not found", e);
		}
	}

	@Override
	public void connect() throws SQLException {
		this.connection = DriverManager.getConnection(url, user, password);
	}

	@Override
	public void disconnect() throws SQLException {
		this.connection.close();
		this.connection = null;
	}

	@Override
	public Connection getConnection() throws SQLException {
		if (this.connection == null || !this.connection.isValid(30000)) {
			connect();
		}
		return this.connection;
	}
}
