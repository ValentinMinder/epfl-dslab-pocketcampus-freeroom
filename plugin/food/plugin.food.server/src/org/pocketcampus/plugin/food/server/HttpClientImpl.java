package org.pocketcampus.plugin.food.server;

import java.io.*;
import java.net.*;
import java.nio.ByteBuffer;
import java.nio.charset.Charset;

public final class HttpClientImpl implements HttpClient {
	private static final int BUFFER_SIZE = 512;

	// TODO: There has to be a much better way to do this.
	
	@Override
	public String getString(String url, Charset charset)
			throws Exception {
		URL u = new URL(url);

		InputStream in = null;
		try {
			in = u.openStream();
			ByteArrayOutputStream outStream = new ByteArrayOutputStream(BUFFER_SIZE);
			int bytesRead = 0;
			byte[] buffer = new byte[BUFFER_SIZE];
			while ((bytesRead = in.read(buffer, 0, buffer.length)) != -1) {
				outStream.write(buffer, 0, bytesRead);
			}

			return new String(charset.decode(ByteBuffer.wrap(outStream.toByteArray())).array());
		} finally {
			if (in != null) {
				in.close();
			}
		}
	}
}