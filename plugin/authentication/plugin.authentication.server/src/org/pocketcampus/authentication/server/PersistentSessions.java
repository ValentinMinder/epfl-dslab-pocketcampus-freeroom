package org.pocketcampus.authentication.server;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.pocketcampus.plugin.authentication.shared.SessionId;

public class PersistentSessions {
	
	private Map<String, List<SessionId>> sessMap;
	
	PersistentSessions() {
		sessMap = Collections.synchronizedMap(new HashMap<String, List<SessionId>>());
	}

	public void addSessionIdToRefresh(SessionId sess) {
		
	}
	
	public void loadFromFile() {
		
	}
	
	public void writeToFile() {
		
	}
	
}
