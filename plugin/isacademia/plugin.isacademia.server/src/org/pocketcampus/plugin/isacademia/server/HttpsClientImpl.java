package org.pocketcampus.plugin.isacademia.server;

import java.util.List;
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
	private static final String COOKIE_SENT_HEADER = "Cookie";
	private static final String COOKIE_RECEIVED_HEADER = "Set-Cookie";
	private static final String REDIRECT_URL_HEADER = "Location";
	private static final int MAX_REDIRECTS = Integer.parseInt(System.getProperty("http.maxRedirects"));

	static {
		// This is required because the ISA server is... not exactly standards-compliant or up-to-date.
		System.setProperty("jsse.enableSNIExtension", "false");
	}

	@Override
	public String getString(String url, Charset charset, Map<String, String> cookies) throws Exception {
		return getStringPrivate(url, charset, cookies, 0);
	}

	private String getStringPrivate(String url, Charset charset, Map<String, String> cookies, int count) throws Exception {
		if (count >= MAX_REDIRECTS) {
			throw new Exception("Too many redirects.");
		}

		HttpsURLConnection conn = (HttpsURLConnection) new URL(url).openConnection();
		conn.setSSLSocketFactory(getSSLv3SocketFactory());
		conn.setRequestProperty(COOKIE_SENT_HEADER, getCookieString(cookies));
		conn.setInstanceFollowRedirects(false);
		conn.connect();

		int code = conn.getResponseCode();
		if (code == HttpURLConnection.HTTP_MOVED_PERM || code == HttpURLConnection.HTTP_MOVED_TEMP || code == HttpURLConnection.HTTP_SEE_OTHER) {
			for (Entry<String, List<String>> cookieEntry : conn.getHeaderFields().entrySet()) {
				String key = cookieEntry.getKey();
				if (key != null && key.equals(COOKIE_RECEIVED_HEADER)) {
					// format is
					// <key>=<value>; <other info>
					for (String value : cookieEntry.getValue()) {
						String[] parts = value.split(";")[0].split("=");
						cookies.put(parts[0].trim(), parts[1].trim());
					}
				}
			}

			return getStringPrivate(conn.getHeaderField(REDIRECT_URL_HEADER), charset, cookies, count + 1);
		}

		return read(conn.getInputStream(), charset);
	}

	private static SSLSocketFactory getSSLv3SocketFactory() {
		final SSLSocketFactory factory = (SSLSocketFactory) SSLSocketFactory.getDefault();

		return new SSLSocketFactory() {
			@Override
			public Socket createSocket(InetAddress address, int port, InetAddress localAddress, int localPort) throws IOException {
				return processSocket(factory.createSocket(address, port, localAddress, localPort));
			}

			@Override
			public Socket createSocket(String host, int port, InetAddress localHost, int localPort) throws IOException, UnknownHostException {
				return processSocket(factory.createSocket(host, port, localHost, localPort));
			}

			@Override
			public Socket createSocket(InetAddress host, int port) throws IOException {
				return processSocket(factory.createSocket(host, port));
			}

			@Override
			public Socket createSocket(String host, int port) throws IOException, UnknownHostException {
				return processSocket(factory.createSocket(host, port));
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
				return processSocket(factory.createSocket(s, host, port, autoClose));
			}
		};
	}

	private static Socket processSocket(Socket socket) {
		// The second cipher suite is not really a cipher suite
		// it just tells Java not to send the SSL renegotiation_info extension
		// that the IS-Academia server doesn't like (and by "doesn't like" I mean "closes the connection if it receives it")
		((SSLSocket) socket).setEnabledCipherSuites(new String[] { "SSL_RSA_WITH_RC4_128_SHA", "TLS_EMPTY_RENEGOTIATION_INFO_SCSV" });
		((SSLSocket) socket).setEnabledProtocols(new String[] { "SSLv3" });
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