package org.pocketcampus.platform.shared.utils;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;

/**
 * Builds data for requests.
 */
public class PostDataBuilder {
	private final StringBuilder builder = new StringBuilder();

	public PostDataBuilder addParam(String key, String val) {
		if (builder.length() != 0) {
			builder.append('&');
		}

		try {
			builder.append(URLEncoder.encode(key, "UTF-8"));
			builder.append('=');
			builder.append(URLEncoder.encode(val, "UTF-8"));
		} catch (UnsupportedEncodingException e) {
			// Not happening. Ever.
		}
		
		return this;
	}

	public String toString() {
		return builder.toString();
	}
}