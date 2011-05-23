package org.pocketcampus.plugin.authentication;

import java.util.Date;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.concurrent.locks.ReentrantLock;

import org.pocketcampus.shared.plugin.authentication.AuthToken;

public class AuthenticationSessions {
	private static final int SESSION_ID_SIZE = 128;
	private static final int SESSION_DURATION = 60 * 60 * 1000;

	private static HashMap<String, UserSession> sessions_;
	private static LinkedList<String> sessionIds_;
	private static ReentrantLock lock_;
	private static AuthenticationEncryption encrypter_;

	public static String newSession(String username, String password) {
		if(username == null && password == null)
			throw new IllegalArgumentException();
		if(!valid())
			init();
		String sessionId = null;

		lock_.lock();
		do {
			sessionId = generateId();
		} while(sessionIds_.contains(sessionId));

		try {
			String sPassword = encrypter_.encrypt(password, sessionId);
			sessions_.put(username, new UserSession(username, sPassword, sessionId));
			sessionIds_.add(sessionId);
		} catch(Exception e) {
			if(sessions_.containsKey(username)) sessions_.remove(username);
			if(sessionIds_.contains(sessionId)) sessionIds_.remove(sessionId);
			sessionId = null;
		} finally {
			lock_.unlock();
		}

		return sessionId;
	}

	public static String getPassword(String username, String sessionId) {
		if(username == null && sessionId == null)
			throw new IllegalArgumentException();
		if(!valid())
			init();

		String password = null;

		lock_.lock();
		try {
			if(sessions_.containsKey(username)) {
				password = encrypter_.decrypt(sessions_.get(username).getPassword(), sessionId);
			}
		} catch(Exception e) {
			password = null;
		} finally {
			lock_.unlock();
		}
		
		return password;
	}
	
	public static String getPassword(AuthToken token) {
		return getPassword(token.getUsername(), token.getSessionId());
	}


	public static boolean authenticateSession(String username, String sessionId) {
		if(username == null || sessionId == null)
			throw new IllegalArgumentException();
		if(!valid())
			init();

		boolean authed = false;
		
		long now = new Date().getTime();

		lock_.lock();

		if(sessions_.containsKey(username)) {
			UserSession session = sessions_.get(username);
			
			long timestamp = sessions_.get(username).getTimestamp().getTime();
			if(session.getSessionId().equals(sessionId) && (now - timestamp) < SESSION_DURATION) {
				authed = true;
				session.updateTimestamp();
			} else {
				freeSession(username);
			}
		}

		lock_.unlock();

		return authed;
	}

	public static String getSession(String username) {
		if(username == null)
			throw new IllegalArgumentException();
		if(!valid())
			init();

		String session = null;

		lock_.lock();

		if(sessions_.containsKey(username)) {
			session = sessions_.get(username).getSessionId();
		}

		lock_.unlock();

		return session;
	}

	public static boolean freeSession(String username) {
		if(username == null)
			throw new IllegalArgumentException();
		if(!valid())
			init();

		boolean status = false;

		lock_.lock();

		if(sessions_.containsKey(username)) {
			String sessionId = sessions_.get(username).getSessionId();
			sessions_.remove(username);
			if(sessionIds_.contains(sessionId)) sessionIds_.remove(sessionId);
			status = true;
		}

		lock_.unlock();

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
		return (sessions_ != null && sessionIds_ != null && lock_ != null && encrypter_ != null);
	}

	private static void init() {
		sessions_ = new HashMap<String, UserSession>();
		sessionIds_ = new LinkedList<String>();
		lock_ = new ReentrantLock();
		encrypter_ = new AuthenticationEncryption("AES");
	}
}
