package org.pocketcampus.plugin.isacademia.server;

import java.nio.charset.Charset;
import java.util.List;

import org.apache.http.cookie.Cookie;

/**
 * Simple HTTPS client with support for cookies.
 * 
 * @author Solal Pirelli <solal.pirelli@epfl.ch>
 */
public interface HttpsClient {
	String getString(String url, Charset charset, List<Cookie> cookies) throws Exception;
}