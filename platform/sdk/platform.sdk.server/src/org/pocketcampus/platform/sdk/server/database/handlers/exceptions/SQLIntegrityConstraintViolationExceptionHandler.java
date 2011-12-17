package org.pocketcampus.platform.sdk.server.database.handlers.exceptions;

import java.sql.SQLException;
import java.sql.SQLIntegrityConstraintViolationException;

/**
 * Handler for <b>{@code SQLIntegrityConstraintViolationException}</b>.<br />
 * This class provides a way to avoid SQLIntegrityConstraintViolationException and instead
 * return something useful without the need for the caller to manage further exceptions.<br />
 * Since {@code SQLIntegrityConstraintViolationException}s are typically thrown on DML and
 * DDL statements (i.e. UPDATE requests, which return an integer) this handler returns an
 * integer.
 */
public class SQLIntegrityConstraintViolationExceptionHandler extends SQLExceptionHandler<Integer>
		implements IExceptionHandler<Integer> {
	
	/**
	 * The value that will be returned if a {@code SQLIntegrityConstraintViolationException}
	 * is thrown, defined at construct time.
	 */
	private final int defaultReturnValue;
	
	/**
	 * Creates a new Exception Handler specialized in
	 * {@code SQLIntegrityConstraintViolationException}, which are thrown when the database
	 * cannot complete a request due to integrity constraints (typically duplicate unique
	 * keys).
	 * @param defaultReturnValue the value that will be returned if such an exception is
	 * thrown
	 */
	public SQLIntegrityConstraintViolationExceptionHandler(int defaultReturnValue) {
		super();
		this.defaultReturnValue = defaultReturnValue;
	}
	
	
	/* (non-Javadoc)
	 * @see org.pocketcampus.server.database.DatabaseManager.SQLExceptionHandler#handler(java.sql.SQLException)
	 */
	@Override
	public Integer specializedHandler(SQLException e) throws SQLException {
		if (e instanceof SQLIntegrityConstraintViolationException) {
			return this.defaultReturnValue;
		}
		else {
			throw e;
		}
	}
}