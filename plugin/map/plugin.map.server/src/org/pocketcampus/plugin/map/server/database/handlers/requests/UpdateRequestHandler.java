package org.pocketcampus.plugin.map.server.database.handlers.requests;

import org.pocketcampus.plugin.map.server.ServerException;
import org.pocketcampus.plugin.map.server.database.IConnectionManager;
import org.pocketcampus.plugin.map.server.database.handlers.exceptions.SQLExceptionHandler;

import java.sql.PreparedStatement;
import java.sql.SQLException;

/**
 * Implementation of RequestHandler specialized in SQL DML and SQL DDL statements,
 * which return an integer.
 *
 */
public abstract class UpdateRequestHandler extends RequestHandler<Integer, Integer> {
	abstract public void prepareStatement(PreparedStatement stmt) throws SQLException;
	
	/**
	 * Prepare an SQL statement, which must be an SQL Data Manipulation Language (DML) statement,
	 * such as <code>INSERT</code>, <code>UPDATE</code> or <code>DELETE</code>; or an SQL statement that returns nothing,
	 * such as a DDL statement. 
	 * @param sqlRequest the request that will be used by <i>prepareStatement</i>, where
	 * sensitive strings (e.g. user inputs) are replaced by question marks.<br />
	 * Ex : <code>INSERT INTO `fruits` (`name`) VALUES (?)</code><br />
	 * See <code>PreparedStatement</code>'s documentation for more details.
	 * @throws ServerException 
	 */
	public UpdateRequestHandler(String sqlRequest) throws ServerException {
		super(sqlRequest);
	}
	
	//Temp
	@Deprecated
	public UpdateRequestHandler(String sqlRequest, IConnectionManager dbManager) throws ServerException {
		super(sqlRequest, new SQLExceptionHandler<Integer>(), dbManager);
	}
	
	
	/**
	 * Prepare an SQL statement, which must be an SQL Data Manipulation Language (DML) statement,
	 * such as <code>INSERT</code>, <code>UPDATE</code> or <code>DELETE</code>; or an SQL statement that returns nothing,
	 * such as a DDL statement.<br />
	 * This constructor provides an additional parameter that offer the possibility to manage exceptions that might be thrown during
	 * the process in a fine-grained way.
	 * @param sqlRequest the request that will be used by <i>prepareStatement</i>, where
	 * sensitive strings (e.g. user inputs) are replaced by question marks.<br />
	 * Ex : <code>INSERT INTO `fruits` (`name`) VALUES (?)</code><br />
	 * See <code>PreparedStatement</code>'s documentation for more details.
	 * @param exceptionHandler the handler that will be used in case of a thrown SQLException during the process
	 * @throws ServerException 
	 */
	public UpdateRequestHandler(String sqlRequest, SQLExceptionHandler<Integer> exceptionHandler) throws ServerException {
		super(sqlRequest, exceptionHandler);
	}
	
	//Temp
	@Deprecated
	public UpdateRequestHandler(String sqlRequest, SQLExceptionHandler<Integer> exceptionHandler, IConnectionManager dbManager) throws ServerException {
		super(sqlRequest, exceptionHandler, dbManager);
	}
	
	/* (non-Javadoc)
	 * @see org.pocketcampus.core.database.handlers.requests.RequestHandler#executeStatement(java.sql.PreparedStatement)
	 */
	@Override
	protected Integer executeStatement(PreparedStatement stmt)
			throws SQLException {
		return stmt.executeUpdate();
	}
	
	/* (non-Javadoc)
	 * @see org.pocketcampus.core.database.handlers.requests.RequestHandler#resultProcessor(java.lang.Object)
	 */
	@Override
	protected Integer resultProcessor(Integer result) {
		return result;
	}
}