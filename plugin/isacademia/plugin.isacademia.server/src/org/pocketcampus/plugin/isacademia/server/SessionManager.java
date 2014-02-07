package org.pocketcampus.plugin.isacademia.server;

import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

/**
 * SessionManager to manage ISA sessions.
 * 
 * @author Amer Chamseddine <amer.chamseddine@epfl.ch>
 */
public class SessionManager {

	private final static long SESSION_VALIDITY = 3600000; // 1 hour

	private static class User {
		private String gaspar;
		private String sciper;
		private long lastAccess;
		public User(String gaspar, String sciper) {
			this.gaspar = gaspar;
			this.sciper = sciper;
			touch();
		}
		public String getGaspar() {
			return gaspar;
		}
		public String getSciper() {
			return sciper;
		}
		public void touch() {
			lastAccess = System.currentTimeMillis();
		}
	}
	
	private Map<String, User> sessions;
	
	public SessionManager() {
		sessions = new ConcurrentHashMap<String, User>();
	}
	
	public String insert(String gaspar, String sciper) {
		String id = UUID.randomUUID().toString();
		sessions.put(id, new User(gaspar, sciper));
		return id;
	}
	
	public String getGaspar(String sessionId) {
		User u = sessions.get(sessionId);
		if(u == null)
			return null;
		u.touch();
		return u.getGaspar();
	}
	
	public String getSciper(String sessionId) {
		User u = sessions.get(sessionId);
		if(u == null)
			return null;
		u.touch();
		return u.getSciper();
	}
	
	public Runnable getCleaner() {
		return new Runnable() {
			public void run() {
				while(true) {
					try {
						Thread.sleep(60000);
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
					int cleaned = 0,  stored = 0;
					for(Iterator<Entry<String, User>> ri = sessions.entrySet().iterator(); ri.hasNext();) {
						Entry<String, User> re = ri.next();
						if(re.getValue().lastAccess < System.currentTimeMillis() - SESSION_VALIDITY) {
							ri.remove();
							cleaned++;
						} else {
							stored++;
						}
					}
					System.out.println("[isacademia] cleaned up " + cleaned + " sessions; " + stored + " in memory.");
				}
			}
		};
	}
	
}
