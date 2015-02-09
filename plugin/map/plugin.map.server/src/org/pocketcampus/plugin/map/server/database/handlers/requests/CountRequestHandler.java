package org.pocketcampus.plugin.map.server.database.handlers.requests;

import org.pocketcampus.plugin.map.server.ServerException;
import org.pocketcampus.plugin.map.server.database.IConnectionManager;

import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * Implementation of QueryRequestHandler which implements the {@code processResult} method
 * to deal with "{@code SELECT COUNT(*) AS `virtname` FROM ...}" SQL statements.
 */
public abstract class CountRequestHandler extends QueryRequestHandler<Integer> {
	private final String countColName;
	
	/**
	 * Prepare an SQL query which is intended to contain a {@code COUNT(*)} statement
	 * associated to a virtual column.
	 * @param sqlRequest the SQL query
	 * @param countColName the SQL column or virtual column's name associated with the SQL
	 * COUNT function. Ex.: "{@code SELECT COUNT(*) AS `colname` FROM ...}", 'colname' is the
	 * virtual column associated with the COUNT function
	 * @throws ServerException 
	 */
	public CountRequestHandler(String sqlRequest, String countColName) throws ServerException {
		super(sqlRequest);
		this.countColName = countColName;
	}
	
	//TEMP
	@Deprecated
	public CountRequestHandler(String sqlRequest, IConnectionManager dbManager, String countColName) throws ServerException {
		super(sqlRequest, dbManager);
		this.countColName = countColName;
	}
	
	/**
	 * Returns the value in the first row for the column specified in the constructor's
	 * argument <i>countColName</i>.
	 */
	public Integer processResult(ResultSet result) throws SQLException, ServerException {
		int count = Helpers.getCount(this.countColName, result);
		return count;
	}
}