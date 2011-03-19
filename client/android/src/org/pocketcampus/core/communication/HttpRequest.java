package org.pocketcampus.core.communication;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.nio.charset.Charset;

/**
 * Handles synchronous HTTP request.
 * TODO implement timeout
 * @author Florian
 *
 */
public class HttpRequest {
	private URL url_;
	
	private static final int BUFFER_SIZE = 2000;
	
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
		int charRead;
		char[] inputBuffer = new char[BUFFER_SIZE]; 
		String str = "";
		
		URLConnection connection = url_.openConnection();
		InputStream content = connection.getInputStream();
		InputStreamReader reader = new InputStreamReader(content, Charset.forName("UTF-8"));
		         
		while ((charRead = reader.read(inputBuffer))>0) {                    
			String readString = String.copyValueOf(inputBuffer, 0, charRead);                    
			str += readString;
			inputBuffer = new char[BUFFER_SIZE];
		}
		
		content.close();
		return str;
	}
}
