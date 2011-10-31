package org.pocketcampus.platform.sdk.shared.utils;

import java.net.HttpURLConnection;
import java.net.URL;

public class NetworkUtil {
	
	/**
	 * Checks if an URL returns a valid status response (200).
	 * Note that this method is blocking!
	 * @param toCheck
	 * @return
	 */
	public static boolean checkUrlStatus(String toCheck) {
		URL checkedUrl;
		try {
			checkedUrl = new URL(toCheck);
			HttpURLConnection connection =  (HttpURLConnection)  checkedUrl.openConnection(); 
			connection.setRequestMethod("GET"); 
			connection.connect(); 
			return (connection.getResponseCode()==200);

		} catch (Exception e) {
			// misc network error
		}

		return false;
	}
}
