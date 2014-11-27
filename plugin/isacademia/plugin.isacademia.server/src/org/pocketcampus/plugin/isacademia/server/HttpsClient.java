package org.pocketcampus.plugin.isacademia.server;

import java.io.IOException;
import java.nio.charset.Charset;

/**
 * Simple HTTPS client.
 * 
 * @author Solal Pirelli <solal@pocketcampus.org>
 */
public interface HttpsClient {
	String get(String url, Charset charset) throws IOException;
}