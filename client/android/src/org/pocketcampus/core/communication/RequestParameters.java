package org.pocketcampus.core.communication;

import java.net.URLEncoder;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map.Entry;

public class RequestParameters {
	private HashMap<String, String> parameters_ = new HashMap<String, String>();
	
	public void addParameter(String key, String value) {
		parameters_.put(key, value);
	}
	
	@Override
	public String toString() {
		String text = "?";
		Iterator<Entry<String, String>> it = parameters_.entrySet().iterator();
		
		while (it.hasNext()) {
			Entry<String, String> param = (Entry<String, String>) it.next();
			text += URLEncoder.encode(param.getKey()) + "=" + URLEncoder.encode(param.getValue()) + "&";
		}
		
		return text;
	}
	
}
