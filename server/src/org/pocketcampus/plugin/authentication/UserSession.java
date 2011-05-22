package org.pocketcampus.plugin.authentication;

import java.util.Calendar;
import java.util.Date;

public class UserSession {
	private final String username_;
	private final String password_;
	private final String sessionId_;
	private Date timestamp_;
	
	public UserSession(String username, String password, String sessionId) {
		username_ = username;
		password_ = password;
		sessionId_ = sessionId;
		timestamp_ = Calendar.getInstance().getTime();
	}
	
	public void updateTimestamp() {
		timestamp_ = Calendar.getInstance().getTime();
	}

	public String getUsername() {
		return username_;
	}

	public String getPassword() {
		return password_;
	}

	public String getSessionId() {
		return sessionId_;
	}
	
	public Date getTimestamp() {
		return timestamp_;
	}
}
