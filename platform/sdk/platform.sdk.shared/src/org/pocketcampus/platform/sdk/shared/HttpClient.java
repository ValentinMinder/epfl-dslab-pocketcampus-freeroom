package org.pocketcampus.platform.sdk.shared;

import java.nio.charset.Charset;

/**
 * Simple HTTP client abstraction.
 * 
 * @author Solal Pirelli <solal.pirelli@epfl.ch>
 */
public interface HttpClient {
    String getString(String url, Charset charset) throws Exception;
}