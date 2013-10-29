package org.pocketcampus.plugin.food.server;

import java.nio.charset.Charset;

public interface HttpClient {
    String getString(String url, Charset charset) throws Exception;
}