package org.pocketcampus.shared.plugin.authentication;

public class AuthToken {
	public final static int SESSION_ID_SIZE = 128;
	
	private final String sciper_;
	private final String sessionId_;
	
	public AuthToken(String sciper, String sessionId) {
		this.sciper_ = sciper;
		this.sessionId_ = sessionId;
	}
	
	public String getSciper() {
		return sciper_;
	}
	
	public String getSessionId() {
		return sessionId_;
	}
}
