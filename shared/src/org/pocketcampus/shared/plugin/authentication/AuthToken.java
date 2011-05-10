package org.pocketcampus.shared.plugin.authentication;

public class AuthToken {
	public final static int SESSION_ID_SIZE = 128;
	
	private final String username_;
	private final String sessionId_;
	
	public AuthToken(String username, String sessionId) {
		this.username_ = username;
		this.sessionId_ = sessionId;
	}
	
	public String getUsername() {
		return username_;
	}
	
	public String getSessionId() {
		return sessionId_;
	}
}
