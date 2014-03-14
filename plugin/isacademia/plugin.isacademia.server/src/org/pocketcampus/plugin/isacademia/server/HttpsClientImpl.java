package org.pocketcampus.plugin.isacademia.server;

import java.security.*;
import java.io.IOException;
import java.net.Socket;
import java.net.UnknownHostException;
import java.nio.charset.Charset;
import javax.net.ssl.SSLSocket;

import org.apache.http.conn.*;
import org.apache.http.conn.ssl.*;
import org.apache.http.*;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.conn.scheme.*;
import org.apache.http.impl.client.*;
import org.apache.http.impl.conn.SingleClientConnManager;
import org.apache.http.params.HttpParams;
import org.apache.http.util.EntityUtils;

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
	public String get(String url, Charset charset) throws Exception {
		ClientConnectionManager cm = new SingleClientConnManager(SCHEME_REGISTRY);
		AbstractHttpClient client = new DefaultHttpClient(cm);

		HttpGet get = new HttpGet(url);
		HttpResponse response = client.execute(get);
		return EntityUtils.toString(response.getEntity(), charset.toString());
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