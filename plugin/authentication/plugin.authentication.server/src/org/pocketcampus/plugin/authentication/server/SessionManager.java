package org.pocketcampus.plugin.authentication.server;

public interface SessionManager {
	String insert(String gaspar, String sciper);
	String getGaspar(String sessionId);
	String getSciper(String sessionId);
}
