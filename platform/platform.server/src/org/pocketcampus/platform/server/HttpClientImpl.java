package org.pocketcampus.platform.server;

import java.io.IOException;
import java.io.OutputStreamWriter;
import java.net.URL;
import java.net.URLConnection;
import java.nio.charset.Charset;

import org.pocketcampus.platform.shared.utils.StringUtils;

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
	public String post(String url, String body, Charset charset) throws IOException {
		URLConnection conn = new URL(url).openConnection();
		conn.setDoOutput(true);

		OutputStreamWriter writer = null;

		try {
			writer = new OutputStreamWriter(conn.getOutputStream());
			writer.write(body);
			writer.flush();
			return StringUtils.fromStream(conn.getInputStream(), charset.name());
		} finally {
			if (writer != null) {
				writer.close();
			}
		}
	}
}