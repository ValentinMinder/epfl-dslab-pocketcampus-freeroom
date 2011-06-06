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
	
	/**
	 * Returns a HTTP GET Query string, preceded by '?' and with each parameter separated by
	 * '&'. The string is properly encoded for URL specifications.
	 */
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
	
	public HashMap<String, String> getParameters() {
		return parameters_;
	}
}
