package org.pocketcampus.plugin.isacademia.server;

import java.nio.charset.Charset;

/**
 * Simple HTTPS client with support for cookies.
 * 
 * @author Solal Pirelli <solal.pirelli@epfl.ch>
 */
public interface HttpsClient {
	String get(String url, Charset charset) throws Exception;
}