package org.pocketcampus.platform.server;

import org.pocketcampus.platform.shared.utils.StringUtils;

import java.io.IOException;
import java.net.URL;
import java.net.URLConnection;
import java.nio.charset.Charset;

/**
 * Implementation of HttpClient.
 * 
 * @author Solal Pirelli <solal.pirelli@epfl.ch>
 */
public final class HttpClientImpl implements HttpClient {
	@Override
	public String get(String url, Charset charset)
			throws IOException {
		return StringUtils.fromStream(new URL(url).openStream(), charset.name());
	}

	@Override
	public String post(String url, byte[] body, Charset charset) throws IOException {
		URLConnection conn = new URL(url).openConnection();
		conn.setDoOutput(true);
		conn.getOutputStream().write(body);
		return StringUtils.fromStream(conn.getInputStream(), charset.name());
	}
}