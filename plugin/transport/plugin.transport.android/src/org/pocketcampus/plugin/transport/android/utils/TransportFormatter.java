package org.pocketcampus.plugin.transport.android.utils;

import java.util.HashMap;

public class TransportFormatter {
	static HashMap<String, String> niceNames_;
	
	private static void initialize() {
		niceNames_ = new HashMap<String, String>();
		niceNames_.put("UMetm1", "M1");
		niceNames_.put("UMetm2", "M2");
	}
	
	public static String getNiceName(String lineName) {
		if(niceNames_ == null) {
			initialize();
		}
		
		if(niceNames_.containsKey(lineName)) {
			return niceNames_.get(lineName);
		}
		
		return lineName;
	}

}