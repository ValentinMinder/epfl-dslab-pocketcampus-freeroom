package org.pocketcampus.plugin.map.server.database.handlers.exceptions;

import org.pocketcampus.plugin.map.server.ServerException;
import org.pocketcampus.plugin.map.server.logging.Logger;

import java.sql.SQLException;

/**
 * Handler for <b>{@code SQLException}</b>.<br />
 * This class is meant to provide an advanced SQLException handler. The default implementation only
 * logs any SQLException and converts it into a ServerException.
 *
 * @param <T>
 */
public class SQLExceptionHandler<T> implements IExceptionHandler<T> {
	/**
	 * Override this method to implement a specialized handler. If the specialized handler
	 * cannot handle the given SQLException, it should throw it.<br />
	 * The default implementation handles nothing and throws directly the received exception
	 * @param e the exception thrown by the DB engine
	 * @return nothing (always throws an exception)
	 * @throws SQLException the exception given as argument
	 */
	public T specializedHandler(SQLException e) throws SQLException, ServerException {
		throw e;
	}
	
	/**
	 * The default handler will be called to handle an SQLException. It will first try to call
	 * <code>handler</code>, and if it cannot handle the exception, the exception will be logged
	 * and converted into a <code>ServerException</code>
	 * @param e the exception to handle
	 * @return
	 * @throws ServerException
	 */
	public final T handle(SQLException e) throws ServerException {
		try {
			return specializedHandler(e);
		} catch (SQLException ex) {
			Logger.log(ex, "Database Error : Unable to execute MySQL Request");
			throw new ServerException("Database Error : Unable to execute an operation", ex);
		}
	}
}