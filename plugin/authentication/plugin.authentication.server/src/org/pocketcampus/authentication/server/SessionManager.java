package org.pocketcampus.authentication.server;

import java.util.HashMap;
import java.util.UUID;

import org.pocketcampus.plugin.authentication.shared.SessionToken;

public class SessionManager {
	HashMap<SessionToken, Session> mSessionMap = new HashMap<SessionToken, Session>();
	
	public SessionToken openSession(String username) {
		String id = UUID.randomUUID().toString();
		SessionToken sessionToken = new SessionToken(id); 
		mSessionMap.put(sessionToken, new Session());
		
		return sessionToken;
	}

	public boolean checkSession(SessionToken token) {
		return mSessionMap.containsKey(token);
	}

	public boolean closeSession(SessionToken token) {
		if(checkSession(token)) {
			mSessionMap.remove(token);
			return true;
		}
		
		return false;
	}

}
