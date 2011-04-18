package org.pocketcampus.core.communication;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;

/**
 * Handles synchronous HTTP request.
 * TODO implement timeout
 * @author Florian
 *
 */
public class HttpRequest {
	private URL url_;
	
	protected HttpRequest(String urlString) {
		try {
			url_ = new URL(urlString);
		} catch (MalformedURLException e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * Legacy v1 code, handle with care.
	 * @return
	 * @throws Exception
	 */
	public String getContent() throws Exception {
		
		BufferedReader breader = new BufferedReader(new InputStreamReader(url_.openStream(), "UTF-8"));
		String s;
		StringBuilder str = new StringBuilder();
		while((s = breader.readLine()) != null) {
			str.append(s);
		}
		breader.close();
		return str.toString();
	}
}
