package org.pocketcampus.plugin.map.server.database.handlers.requests;

import org.pocketcampus.plugin.map.server.ServerException;
import org.pocketcampus.plugin.map.server.logging.Logger;

import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * Various useful methods used by the request handlers
 */
public class Helpers {
	/**
	 * Close a ResultSet and handles properly exceptions that may occur
	 * @param result
	 */
	public static synchronized void closeResultSet(ResultSet result) {
		try {
			if (result != null && !result.isClosed())
				result.close();
		} catch (SQLException e) {
			Logger.log(e, "ResultSet not properly closed");
		}
	}
	
	/**
	 * Returns an integer held by the first row in the given ResultSet.<br />
	 * Useful to get the result of a request which looks like<br />
	 * <code>SELECT COUNT(*) FROM table ...</code><br />
	 * @param colName name of the column which holds the COUNT(*)'s result
	 * @param result
	 * @return the value returned by COUNT(*)
	 * @throws ServerException 
	 */
	public static int getCount(String colName, ResultSet result) throws ServerException {
		if (result == null)	// Should never happen, defined in PreparedUpdate.execute*()
			throw new IllegalStateException("null result");
		
		try {
			if (result.first()) {
				return result.getInt(colName);
			}
			else {
				throw new IllegalArgumentException("The given ResultSet doesn't contain any result");
			}
		} catch (SQLException e) {
			Logger.log(e, "Unable to handle the ResultSet");
			throw new ServerException("Database Error : An error occured while processing the request", e);
		} finally {
			closeResultSet(result);
		}
	}
}
