package org.pocketcampus.plugin.authentication;

import java.util.Collection;
import java.util.HashMap;
import java.util.concurrent.locks.ReentrantLock;

public class AuthenticationSessions {
	private static final int SESSION_ID_SIZE = 128;
	
	private static HashMap<String, String> sessions_;
	private static ReentrantLock sessionsLock_;
	
	public static Collection<String> ss() {
		return sessions_.values();
	}
	
	public static String newSession(String username) {
		if(username == null)
			throw new IllegalArgumentException();
		if(!valid())
			init();
		
		String sessionId = null;
		
		sessionsLock_.lock();
		do {
			sessionId = generateId();
		} while(sessions_.containsValue(sessionId));
		
		sessions_.put(username, sessionId);
		
		sessionsLock_.unlock();
		
		return sessionId;
	}
	
	
	public static boolean authenticateSession(String username, String sessionId) {
		if(username == null || sessionId == null)
			throw new IllegalArgumentException();
		if(!valid())
			init();
		
		boolean authed = false;
		
		sessionsLock_.lock();
		
		if(sessions_.containsKey(username) && sessions_.get(username).equals(sessionId))
			authed = true;
		
		sessionsLock_.unlock();
		
		return authed;
	}
	
	public static String getSession(String username) {
		if(username == null)
			throw new IllegalArgumentException();
		if(!valid())
			init();
		
		String session = null;
		
		sessionsLock_.lock();
		
		if(sessions_.containsKey(username)) {
			session = sessions_.get(username);
		}
		
		sessionsLock_.unlock();
		
		return session;
	}
	
	public static boolean freeSession(String username) {
		if(username == null)
			throw new IllegalArgumentException();
		if(!valid())
			init();
		
		boolean status = false;
		
		sessionsLock_.lock();
		
		if(sessions_.containsKey(username)) {
			sessions_.remove(username);
			status = true;
		}
		
		sessionsLock_.unlock();
		
		return status;
	}
	
	private static String generateId() {
		String id = null;	
		
		id = "";
		while(id.length() < SESSION_ID_SIZE) {
			id+=""+((int)(10*Math.random()));
		}
		
		return id;
	}
	
	private static boolean valid() {
		return (sessions_ != null && sessionsLock_ != null);
	}
	
	private static void init() {
		sessions_ = new HashMap<String, String>();
		sessionsLock_ = new ReentrantLock();
	}
}
