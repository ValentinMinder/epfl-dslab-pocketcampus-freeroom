package org.pocketcampus.platform.shared.utils;

import java.net.HttpURLConnection;
import java.net.URL;

/**
 * Network utilities.
 * 
 * @author Solal Pirelli <solal@pocketcampus.org>
 */
public class NetworkUtils {
	/** Blocking. Ensures the specified URL corresponds to an image. */
	public static boolean checkUrlImage(final String url) {
		try {
			final HttpURLConnection connection = (HttpURLConnection) new URL(url).openConnection();

			return connection.getResponseCode() == 200
					&& connection.getContentType() != null
					&& connection.getContentType().contains("image");
		} catch (Exception e) {
			return false;
		}
	}
}