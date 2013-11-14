package org.pocketcampus.plugin.isacademia.server;

import java.nio.charset.Charset;
import java.util.Map;

/**
 * Simple HTTP client with support for cookies.
 * 
 * @author Solal Pirelli <solal.pirelli@epfl.ch>
 */
public interface HttpsClient {
	String getString(String url, Charset charset, Map<String, String> cookies) throws Exception;
}