package org.pocketcampus.core.communication.status;

/**
 * Status codes indicate the state of the communication. They are heavily inspired
 * from HTTP statuses. 
 *
 * PCP Statuses are meant solely for the communication layer. A module cannot use
 * these codes in a normal execution. E.g. if a module wants to mimic the 304 Not
 * Modified HTTP Status code for cache management, it should define its own way to
 * internally communicate that information (i.e. put that information in its
 * response's payload).
 * 
 * [1xx] Informational
 * [2xx] PCP Error
 * [3xx] Link Error
 * [4xx] Dispatch Error
 * [5xx] Module Error
 */
public enum Status {
	/* Informational */
	
	/**
	 * The request has succeeded. The packet's payload contains the response
	 * of the requested action.
	 */
	OK(101),
	
	/**
	 * The request has succeeded. The packet is only informational, and the
	 * response will arrive later.
	 */
	RECEIVED(102),
	
	
	/* PCP Error */
	
	/**
	 * The request could not be understood by the server due to malformed syntax.
	 */
	BAD_REQUEST(201),
	
	
	/**
	 * The PCP version used for the request is not handled by the server.
	 */
	PCP_VERSION_NOT_HANDLED(202),
	
	
	/* Link Error */
	
	/**
	 * 
	 */
	SESSION_EXPIRED(301),
	
	
	/* Dispatch Error */
	
	/**
	 * The requested module was not found or is not accessible
	 */
	MODULE_NOT_FOUND(401),
	
	/**
	 * The requested module is available, however it can not handle its
	 * requested interface version.
	 */
	MODULE_VERSION_NOT_HANDLED(402),
	
	/**
	 * The requested module was found, but not the requested action (or it is not
	 * accessible)
	 */
	ACTION_NOT_FOUND(403),
	
	
	/* Module Error */
	
	/**
	 * An unrecoverable error occurred while processing the request.
	 */
	UNRECOVERABLE_MODULE_ERROR(501),
	
	/**
	 * The module took too much time to process the request, the processing was aborted.
	 */
	MODULE_TIMEOUT(501);
	
	
	
	private int code;
	
	private Status(int code) {
		this.code = code;
	}
	
	public int getCode() {
		return this.code;
	}
	
	public String toString() {
		String message = this.name().replace("_", " ");
		return this.getCode() + " " + message;
	}
}
