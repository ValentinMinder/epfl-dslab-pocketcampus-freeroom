package org.pocketcampus.core.database.handlers.requests;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

import org.pocketcampus.core.database.ConnectionManager;
import org.pocketcampus.core.database.IConnectionManager;
import org.pocketcampus.core.database.handlers.exceptions.SQLExceptionHandler;
import org.pocketcampus.core.exception.ServerException;
import org.pocketcampus.core.logging.Logger;

/**
 * Provides a generic structure to simplify database requests and exceptions management.
 *
 * @param <R> the type of the result returned by a query to the database
 * @param <O> the type of the returned object
 */
public abstract class RequestHandler<R, O> {
	
	private IConnectionManager dbManager;
	private Connection dbConnection;
	private boolean defaultAutoCommitValue;
	
	/**
	 * Implement this method in order to prepare a statement
	 * @param stmt
	 * @throws SQLException
	 */
	public abstract void prepareStatement(PreparedStatement stmt) throws SQLException;
	
	/**
	 * Implement this method in order to process what is returned by a call to <code>executeStatement</code>
	 * @param <O>
	 * @param result
	 * @return
	 */
	protected abstract O resultProcessor(R result) throws ServerException;
	
	/**
	 * This method will take care of executing the prepared statement and returning
	 * an object of appropried type
	 * @param stmt
	 * @return
	 * @throws SQLException
	 */
	protected abstract R executeStatement(PreparedStatement stmt) throws SQLException;
	
	/**
	 * SQLException handler
	 */
	private SQLExceptionHandler<O> exceptionHandler;
	
	/**
	 * Stores the SQL Request
	 */
	private String request;
	
	/**
	 * Prepare an SQL statement.
	 * @param sqlRequest the request that will be used by <i>prepareStatement</i>, where
	 * sensitive strings (e.g. user inputs) are replaced by question marks.<br />
	 * Ex : <code>SELECT `name` FROM `fruits` WHERE `category` = ?</code><br />
	 * See <code>PreparedStatement</code>'s documentation for more details.
	 * @throws ServerException 
	 */
	public RequestHandler(String sqlRequest) throws ServerException {
		this(sqlRequest, new SQLExceptionHandler<O>());
	}
	
	/**
	 * Prepare an SQL statement.<br />
	 * This constructor provides an additional parameter that offer the possibility to manage exceptions that might be thrown during
	 * the process in a fine-grained way.
	 * @param sqlRequest the request that will be used by <i>prepareStatement</i>, where
	 * sensitive strings (e.g. user inputs) are replaced by question marks.<br />
	 * Ex : <code>SELECT `name` FROM `fruits` WHERE `category` = ?</code><br />
	 * See <code>PreparedStatement</code>'s documentation for more details.
	 * @param exceptionHandler the handler that will be used in case of a thrown SQLException during the process
	 * @throws ServerException 
	 */
	protected RequestHandler(String sqlRequest, SQLExceptionHandler<O> exceptionHandler) throws ServerException {
		this.request = sqlRequest;
		this.exceptionHandler = exceptionHandler;
		this.dbManager = new ConnectionManager();
	}
	
	/**
	 * Execute the prepared statement
	 * @return
	 * @throws ServerException
	 */
	public O execute() throws ServerException {
		PreparedStatement stmt = null;
		
		try {
			connect();
			
			stmt =
				dbConnection.prepareStatement(this.request);
			prepareStatement(stmt);
			
			R result = executeStatement(stmt);
			dbConnection.commit();
			
			return resultProcessor(result);
			
		} catch (SQLException e) {
			return this.exceptionHandler.handle(e);
		} finally {
			try {
				if (stmt != null && !stmt.isClosed())
					stmt.close();
				disconnect();
			} catch (SQLException e) {
				Logger.log(e, "Connection to DB not properly closed");
			}
		}
	}
	
	
	/**
	 * Attempts to establish a connection to the database, and sets the AutoCommit mode to
	 * false
	 * @throws SQLException
	 * @throws ServerException
	 */
	private void connect() throws SQLException, ServerException {
		dbManager.connect();
		dbConnection = dbManager.getConnection();
		defaultAutoCommitValue = dbConnection.getAutoCommit();
		dbConnection.setAutoCommit(false);
		
		/* FIXME
		 * Si la connexion (Connection) est partagée entre les instances, ou qu'une classe
		 * utilise un DB handler dans une méthode, et pas de handler dans une autre, que
		 * l'autre méthode utilise autoCommit à true et que pour le handler ce doit être à
		 * false, si les deux méthodes sont appelées en même temps => CLASH sur
		 * les autoCommit
		 * 
		 * http://www.google.com/search?hl=fr&client=opera&hs=hrr&rls=fr&q=servlet+static+variable+context&aq=f&aqi=&aql=&oq=
		 */
	}
	
	/**
	 * 
	 * @throws SQLException
	 * @throws ServerException
	 */
	private void disconnect() throws SQLException, ServerException {
		dbConnection.setAutoCommit(defaultAutoCommitValue);
		dbManager.disconnect();
		dbConnection = null;
	}
}