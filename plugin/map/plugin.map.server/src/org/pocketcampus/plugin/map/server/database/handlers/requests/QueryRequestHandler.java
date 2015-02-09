package org.pocketcampus.plugin.map.server.database.handlers.requests;

import org.pocketcampus.plugin.map.server.ServerException;
import org.pocketcampus.plugin.map.server.database.IConnectionManager;
import org.pocketcampus.plugin.map.server.database.handlers.exceptions.SQLExceptionHandler;
import org.pocketcampus.plugin.map.server.logging.Logger;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * Implementation of RequestHandler specialized in SQL statements which return
 * a ResultSet object.
 *
 * @param <T> the type of the returned object
 */
public abstract class QueryRequestHandler<T> extends RequestHandler<ResultSet, T> {
	abstract public void prepareStatement(PreparedStatement stmt) throws SQLException;
	
	/**
	 * Put in this method what is to be done on the ResultSet retrieved from the database
	 * @param result the ResultSet linked to the SQL Request
	 * @return whatever you want
	 * @throws SQLException if this exception is thrown, it will be converted to a ServerException
	 * @throws ServerException 
	 */
	abstract public T processResult(ResultSet result) throws SQLException, ServerException;
	
	/**
	 * Prepare an SQL query which will return a ResultSet object when executed. Typically a
	 * <code>SELECT</code> query.
	 * @param sqlRequest the request that will be used by <i>prepareStatement</i>, where
	 * sensitive strings (e.g. user inputs) are replaced by question marks.<br />
	 * Ex : <code>SELECT `name` FROM `fruits` WHERE `category` = ?</code><br />
	 * See <code>PreparedStatement</code>'s documentation for more details.
	 * @throws ServerException 
	 */
	public QueryRequestHandler(String sqlRequest) throws ServerException {
		super(sqlRequest);
	}
	
	//TEMP
	@Deprecated
	public QueryRequestHandler(String sqlRequest, IConnectionManager connectionManager) throws ServerException {
		super(sqlRequest, new SQLExceptionHandler<T>(), connectionManager);
	}
	
	/**
	 * Prepare an SQL query which will return a ResultSet object when executed. Typically a
	 * <code>SELECT</code> query.
	 * @param sqlRequest the request that will be used by <i>prepareStatement</i>, where
	 * sensitive strings (e.g. user inputs) are replaced by question marks.<br />
	 * Ex : <code>SELECT `name` FROM `fruits` WHERE `category` = ?</code><br />
	 * See <code>PreparedStatement</code>'s documentation for more details.
	 * @param exceptionHandler the handler that will be used in case of a thrown SQLException during the process
	 * @throws ServerException 
	 */
	public QueryRequestHandler(String sqlRequest, SQLExceptionHandler<T> exceptionHandler) throws ServerException {
		super(sqlRequest, exceptionHandler);
	}
	
	/* (non-Javadoc)
	 * @see org.pocketcampus.core.database.handlers.requests.RequestHandler#executeStatement(java.sql.PreparedStatement)
	 */
	protected ResultSet executeStatement(PreparedStatement stmt) throws SQLException {
		return stmt.executeQuery();
	}
	
	/* (non-Javadoc)
	 * @see org.pocketcampus.core.database.handlers.requests.RequestHandler#resultProcessor(java.lang.Object)
	 */
	protected T resultProcessor(ResultSet result) throws ServerException {
		try {
			return processResult(result);
		} catch (SQLException e) {
			Logger.log(e, "Database Error while processing a ResultSet");
			throw new ServerException("Database Error : An error occured during processing : " + e.getMessage(), e);
		} finally {
			Helpers.closeResultSet(result);
		}
	}
}