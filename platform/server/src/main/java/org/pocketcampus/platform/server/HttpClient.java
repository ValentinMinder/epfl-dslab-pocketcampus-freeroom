package org.pocketcampus.platform.server;

import java.io.IOException;
import java.nio.charset.Charset;
import java.util.HashMap;
import java.util.Map;

/**
 * Simple HTTP client abstraction.
 *
 * @author Solal Pirelli <solal.pirelli@epfl.ch>
 */
public interface HttpClient {
    default String get(String url, Charset charset) throws IOException {
        return get(url, new HashMap<>(), charset);
    }

    String get(String url, Map<String, String> headers, Charset charset) throws IOException;

    String post(String url, byte[] body, Charset charset) throws IOException;
}