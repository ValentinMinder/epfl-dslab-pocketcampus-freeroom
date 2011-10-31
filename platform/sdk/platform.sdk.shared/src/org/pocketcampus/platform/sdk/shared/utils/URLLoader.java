package org.pocketcampus.platform.sdk.shared.utils;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.net.URLConnection;

public class URLLoader {

	/**
	 * Get data from the web
	 * @param url URL to get data from
	 * @return
	 * @throws IOException
	 */
	public static String getSource(String url) throws IOException {
		URL page = new URL(url);
		
		// Get data
		InputStream is = page.openConnection().getInputStream();
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		int b = is.read();
		while(b != -1) {
			baos.write(b);
			b = is.read();
		}
		
		return baos.toString();
	}
	
	/**
	 * Get data from the web using basic HTTP authentication 
	 * 
	 * @param url URL to get data from
	 * @param username 
	 * @param password
	 * @return
	 * @throws IOException
	 */
	public static String getSource(String url, String username, String password) throws IOException {
		URL page = new URL(url);
		URLConnection conn = page.openConnection();
		
		// Set the credentials
		String auth = new String(username + ":" + password);
		auth = Base64.encodeBytes(auth.getBytes());
		conn.setRequestProperty ("Authorization", "Basic " + auth);
		
		// Get data
		InputStream is = conn.getInputStream();
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		int b = is.read();
		while(b != -1) {
			baos.write(b);
			b = is.read();
		}
		
		return baos.toString();
	}
}
