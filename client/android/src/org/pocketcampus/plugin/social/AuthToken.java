package org.pocketcampus.plugin.social;

import java.io.Serializable;

public class AuthToken implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = -2627881588989875394L;
	/**
	 * 
	 */
	private final Username username_;
	private final SessionId sessionId_;
	
	public AuthToken(Username username, SessionId sessionId) {
		username_ = username;
		sessionId_ = sessionId;
	}
	
	public Username getUsername() {
		return username_;
	}
	
	public SessionId getSessionId() {
		return sessionId_;
	}
}
