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
 * Forces the protocol to be TLSv1, since ISA doesn't support anything better.
 * 
 * @author Solal Pirelli <solal.pirelli@epfl.ch>
 */
public class HttpsClientImpl implements HttpsClient {
	private static final SchemeRegistry SCHEME_REGISTRY = new SchemeRegistry();

	static {
		try {
			SCHEME_REGISTRY.register(new Scheme("https", 443, new InsecureSocketFactory()));
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	@Override
	public String get(String url, Charset charset) throws IOException {
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
			((SSLSocket) socket).setEnabledProtocols(new String[] { "TLSv1" });
			return socket;
		}
	}
}