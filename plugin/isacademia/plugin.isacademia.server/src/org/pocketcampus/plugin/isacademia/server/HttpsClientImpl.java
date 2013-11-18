package org.pocketcampus.plugin.isacademia.server;

import java.security.*;
import java.util.List;
import java.util.Scanner;
import java.io.IOException;
import java.io.InputStream;
import java.net.Socket;
import java.net.UnknownHostException;
import java.nio.charset.Charset;
import javax.net.ssl.SSLSocket;

import org.apache.http.conn.ClientConnectionManager;
import org.apache.http.conn.ssl.SSLSocketFactory;
import org.apache.http.conn.ssl.TrustSelfSignedStrategy;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.conn.scheme.Scheme;
import org.apache.http.conn.scheme.SchemeRegistry;
import org.apache.http.cookie.Cookie;
import org.apache.http.impl.client.AbstractHttpClient;
import org.apache.http.impl.client.BasicCookieStore;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.impl.conn.SingleClientConnManager;
import org.apache.http.params.HttpParams;

/**
 * Implementation of HttpsClient.
 * 
 * @author Solal Pirelli <solal.pirelli@epfl.ch>
 */
public class HttpsClientImpl implements HttpsClient {
	private static final SchemeRegistry SCHEME_REGISTRY = new SchemeRegistry();

	static {
		// This is required because the ISA server is... not exactly standards-compliant or up-to-date.
		System.setProperty("jsse.enableSNIExtension", "false");

		try {
			SCHEME_REGISTRY.register(new Scheme("https", 443, new InsecureSocketFactory()));
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	@Override
	public String getString(String url, Charset charset, List<Cookie> cookies) throws Exception {
		ClientConnectionManager cm = new SingleClientConnManager(SCHEME_REGISTRY);
		AbstractHttpClient client = new DefaultHttpClient(cm);

		BasicCookieStore cookieStore = new BasicCookieStore();
		for (Cookie cookie : cookies) {
			cookieStore.addCookie(cookie);
		}
		client.setCookieStore(cookieStore);

		HttpGet get = new HttpGet(url);

		HttpResponse response = client.execute(get);
		return read(response.getEntity().getContent(), charset);
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

	private static class InsecureSocketFactory extends SSLSocketFactory {
		public InsecureSocketFactory() throws NoSuchAlgorithmException, KeyManagementException, KeyStoreException, UnrecoverableKeyException {
			super(new TrustSelfSignedStrategy());
		}

		@Override
		@Deprecated
		public Socket createSocket() throws IOException {
			return processSocket(super.createSocket());
		}

		@Override
		public Socket createSocket(HttpParams params) throws IOException {
			return processSocket(super.createSocket(params));
		}

		@Override
		@Deprecated
		public Socket createSocket(Socket socket, String host, int port, boolean autoClose) throws IOException, UnknownHostException {
			return processSocket(super.createSocket(socket, host, port, autoClose));
		}

		private static Socket processSocket(Socket socket) {
			// The second cipher suite is not really a cipher suite
			// it just tells Java not to send the SSL renegotiation_info extension
			// that the IS-Academia server doesn't like (and by "doesn't like" I mean "closes the connection if it receives it")
			((SSLSocket) socket).setEnabledCipherSuites(new String[] { "SSL_RSA_WITH_RC4_128_SHA", "TLS_EMPTY_RENEGOTIATION_INFO_SCSV" });
			((SSLSocket) socket).setEnabledProtocols(new String[] { "SSLv3" });
			return socket;
		}
	}
}