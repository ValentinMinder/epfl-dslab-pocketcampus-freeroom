package org.pocketcampus.platform.server;

import java.io.IOException;
import java.nio.charset.Charset;

/**
 * Simple HTTP client abstraction.
 * 
 * @author Solal Pirelli <solal.pirelli@epfl.ch>
 */
public interface HttpClient {
    String get(String url, Charset charset) throws IOException;
    String post(String url, byte[] body, Charset charset) throws IOException;
}