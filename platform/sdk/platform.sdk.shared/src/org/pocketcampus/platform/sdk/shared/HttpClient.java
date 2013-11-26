package org.pocketcampus.platform.sdk.shared;

import java.nio.charset.Charset;

public interface HttpClient {
    String getString(String url, Charset charset) throws Exception;
}