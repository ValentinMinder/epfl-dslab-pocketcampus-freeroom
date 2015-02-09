package org.pocketcampus.plugin.map.server.database.handlers.exceptions;

import org.pocketcampus.plugin.map.server.ServerException;

import java.sql.SQLException;

/**
 * A class implements {@code IExceptionHandler} interface to indicate that it can handle
 * subtypes of SQLException.
 * @param <E> the type of the object returned when the SQLException is handled by the
 * specilized handler
 */
public interface IExceptionHandler<E> {
	/**
	 * Override this method to implement a specialized handler. If the specialized handler
	 * cannot handle the given SQLException, it should throw it.
	 * @param e a SQLException thrown by the DB Engine
	 * @return the value which replaces the exception
	 * @throws SQLException if it cannot be handled (i.e. not of a handled type)
	 * @throws ServerException if the exception was handled but is not recoverable
	 */
	public E specializedHandler(SQLException e) throws SQLException, ServerException;
}
