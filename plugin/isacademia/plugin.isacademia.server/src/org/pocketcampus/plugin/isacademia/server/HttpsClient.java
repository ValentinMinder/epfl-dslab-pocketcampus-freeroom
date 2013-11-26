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
	HttpResult get(String url, Charset charset, List<Cookie> cookies) throws Exception;

	public static final class HttpResult {
		public final List<Cookie> cookies;
		public final String url;
		public final String content;

		public HttpResult(List<Cookie> cookies, String url, String content) {
			this.cookies = cookies;
			this.url = url;
			this.content = content;
		}
	}
}