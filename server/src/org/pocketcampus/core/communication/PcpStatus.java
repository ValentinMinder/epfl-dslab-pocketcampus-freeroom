package org.pocketcampus.core.communication;

/**
 * <p>Status codes indicate the state of the communication. They are heavily inspired
 * from HTTP statuses.</p> 
 *
 * <p>PCP Statuses are meant solely for the communication layer. A module cannot use
 * these codes in a normal execution. E.g. if a module wants to mimic the 304 Not
 * Modified HTTP Status code for cache management, it should define its own way to
 * internally communicate that information (i.e. put that information in its
 * response's payload).</p>
 * 
 * <ul>
 * 	<li>[1xx] Informational</li>
 * 	<li>[2xx] PCP Error</li>
 * 	<li>[3xx] Link Error</li>
 * 	<li>[4xx] Dispatch Error</li>
 * 	<li>[5xx] Module Error</li>
 * </ul>
 */
public enum PcpStatus {
	/* Informational */
	
	/**
	 * [101] The request has succeeded. The packet's payload contains the response.
	 */
	OK(101, "The request has succeeded. The packet's payload contains the response."),
	
	/**
	 * [102] The request has succeeded. The packet is informational and the response
	 * will arrive later.
	 */
	RECEIVED(102, "The request has succeeded. The packet is informational and the" +
			" response will arrive later."),
	
	
	/* PCP Error */
	
	/**
	 * [201] The request could not be understood by the server due to malformed syntax.
	 */
	BAD_REQUEST(201, "The request could not be understood by the server due to" +
			" malformed syntax."),
			
	/**
	 * [202] The PCP version used for the request is not handled by the server.
	 */
	PCP_VERSION_NOT_HANDLED(202, "The PCP version used for the request is not handled" +
			" by the server."),
	
	
	/* Link Error */
	
	/**
	 * [301] PCP session expired.
	 */
	SESSION_EXPIRED(301, "PCP session expired."),
	
	
	/* Dispatch Error */
	
	/**
	 * [401] The requested module was not found or is not usable.
	 */
	MODULE_NOT_FOUND(401, "The requested module was not found or is not usable."),
	
	/**
	 * [402] The requested module is available but cannot handle its requested interface
	 * version.
	 */
	MODULE_VERSION_NOT_HANDLED(402, "The requested module is available but cannot" +
			" handle its requested interface version."),
			
	/**
	 * [403] The requested module was found, but not the requested action (or it is
	 * not accessible).
	 */
	ACTION_NOT_FOUND(403, "The requested module was found, but not the requested action" +
			" (or it is not accessible)."),
	
	
	/* Module Error */
			
	/**
	 * [501] An unrecoverable error occurred while processing the request.
	 */
	UNRECOVERABLE_MODULE_ERROR(501, "An unrecoverable error occurred while processing" +
			" the request."),
	
	/**
	 * [502] The module took too much time to process the request, hence it was aborted.
	 */
	MODULE_TIMEOUT(502, "The module took too much time to process the request, hence it" +
			" was aborted.");
	
	
	
	private int code_;
	private String explanation_;
	
	private PcpStatus(int code, String explanation) {
		this.code_ = code;
		this.explanation_ = explanation;
	}
	
	/**
	 * @return the status code of this PCP Status
	 */
	public int getCode() {
		return this.code_;
	}
	
	/**
	 * @return a human-readable explanation of this PCP Status
	 */
	public String getExplanation() {
		return this.explanation_;
	}
	
	/**
	 * @return a PCP-compliant representation of this Status
	 */
	public String toString() {
		String name = this.name().replace("_", " ");
		return this.getCode() + " " + name;
	}
}
