package org.pocketcampus.plugin.isacademia.server;

import java.util.Map;
import java.util.Map.Entry;
import java.util.Scanner;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.Charset;
import java.net.*;
import javax.net.ssl.*;

/**
 * Implementation of HttpsClient.
 * 
 * @author Solal Pirelli <solal.pirelli@epfl.ch>
 */
public class HttpsClientImpl implements HttpsClient {
	@Override
	public String getString(String url, Charset charset, Map<String, String> cookies) throws Exception {
		HttpsURLConnection conn = (HttpsURLConnection) new URL(url).openConnection();
		conn.setSSLSocketFactory(getSSLSocketFactory("SSLv2Hello","SSLv3"));
		conn.setRequestProperty("Cookie", getCookieString(cookies));
		conn.connect();

		return read(conn.getInputStream(), charset);
	}

	private static SSLSocketFactory getSSLSocketFactory(final String... protocols) {
		final SSLSocketFactory factory = (SSLSocketFactory) SSLSocketFactory.getDefault();

		return new SSLSocketFactory() {
			@Override
			public Socket createSocket(InetAddress address, int port, InetAddress localAddress, int localPort) throws IOException {
				return processSocket(factory.createSocket(address, port, localAddress, localPort), protocols);
			}

			@Override
			public Socket createSocket(String host, int port, InetAddress localHost, int localPort) throws IOException, UnknownHostException {
				return processSocket(factory.createSocket(host, port, localHost, localPort), protocols);
			}

			@Override
			public Socket createSocket(InetAddress host, int port) throws IOException {
				return processSocket(factory.createSocket(host, port), protocols);
			}

			@Override
			public Socket createSocket(String host, int port) throws IOException, UnknownHostException {
				return processSocket(factory.createSocket(host, port), protocols);
			}

			@Override
			public String[] getSupportedCipherSuites() {
				return factory.getSupportedCipherSuites();
			}

			@Override
			public String[] getDefaultCipherSuites() {
				return factory.getDefaultCipherSuites();
			}

			@Override
			public Socket createSocket(Socket s, String host, int port, boolean autoClose) throws IOException {
				return processSocket(factory.createSocket(s, host, port, autoClose), protocols);
			}
		};
	}

	private static Socket processSocket(Socket socket, String[] protocols) {
		((SSLSocket) socket).setEnabledProtocols(protocols);
		return socket;
	}

	private static String getCookieString(Map<String, String> cookies) {
		if (cookies == null) {
			return "";
		}

		String cookie = "";
		for (Entry<String, String> entry : cookies.entrySet()) {
			cookie += entry.getKey() + "=" + entry.getValue() + "; ";
		}
		return cookie;
	}

	private static String read(InputStream stream, Charset charset) {
		Scanner scanner = null;
		try {
			scanner = new Scanner(stream, charset.name());
			scanner.useDelimiter("\\A"); // \A = "beginning of input boundary"
			return scanner.hasNext() ? scanner.next() : "";
		} finally {
			if (scanner != null) {
				scanner.close();
			}
		}
	}
}