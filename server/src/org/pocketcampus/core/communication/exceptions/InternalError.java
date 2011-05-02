package org.pocketcampus.core.communication.exceptions;

/**
 * Indicates a serious problem at the Communication Service level. It is generally due to
 * a programming error.
 */
public class InternalError extends Error {
	/**
	 * 
	 */
	private static final long serialVersionUID = 7021311405597818529L;

	/**
	 * Constructs a new error with <code>null</code> as its detail message.
     * The cause is not initialized, and may subsequently be initialized by a
     * call to {@link initCause}.
	 */
	public InternalError() {
		super();
	}
	
	/**
	 * Constructs a new error with the specified cause and a detail
     * message of <tt>(cause==null ? null : cause.toString())</tt> (which
     * typically contains the class and detail message of <tt>cause</tt>).
     * This constructor is useful for errors that are little more than
     * wrappers for other throwables.
     *
     * @param  cause the cause (which is saved for later retrieval by the
     *         {@link #getCause()} method).  (A <tt>null</tt> value is
     *         permitted, and indicates that the cause is nonexistent or
     *         unknown.)
	 */
	public InternalError(Throwable throwable) {
		super(throwable);
	}
}
