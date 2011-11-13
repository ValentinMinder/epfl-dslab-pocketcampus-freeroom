package org.pocketcampus.authentication.server;

import java.util.HashMap;
import java.util.UUID;

public class TequilaSessions {
	
	static HashMap<String, TequilaSession> mSessionMap = new HashMap<String, TequilaSession>();
	
	public static String newSession(TequilaSession teqSess) {
		String udid = UUID.randomUUID().toString();
		mSessionMap.put(udid, teqSess);
		return udid;
	}

	public static TequilaSession getSession(String udid) {
		return mSessionMap.get(udid);
	}

	public static void deleteSession(String udid) {
		if(mSessionMap.containsKey(udid)) {
			mSessionMap.remove(udid);
		}
	}

}
