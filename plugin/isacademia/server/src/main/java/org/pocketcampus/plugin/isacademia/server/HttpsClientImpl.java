package org.pocketcampus.plugin.isacademia.server;

import org.pocketcampus.platform.shared.utils.StringUtils;

import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLSocket;
import javax.net.ssl.SSLSocketFactory;
import java.io.IOException;
import java.net.InetAddress;
import java.net.Socket;
import java.net.URL;
import java.nio.charset.Charset;

/**
 * Implementation of HttpsClient.
 * Forces the protocol to be TLSv1, since ISA doesn't support anything better.
 * 
 * @author Solal Pirelli <solal@pocketcampus.org>
 */
public class HttpsClientImpl implements HttpsClient {
	@Override
	public String get(String url, Charset charset) throws IOException {
		HttpsURLConnection conn =(HttpsURLConnection) new URL(url).openConnection();
		conn.setSSLSocketFactory(InsecureSocketFactory.INSTANCE);
		return StringUtils.fromStream(conn.getInputStream(), charset.name());
	}

	private static class InsecureSocketFactory extends SSLSocketFactory {
		private final SSLSocketFactory _default = (SSLSocketFactory) SSLSocketFactory.getDefault();
		
		public static final SSLSocketFactory INSTANCE = new InsecureSocketFactory();

		@Override
		public String[] getDefaultCipherSuites() {
			return _default.getDefaultCipherSuites();
		}

		@Override
		public String[] getSupportedCipherSuites() {
			return _default.getSupportedCipherSuites();
		}

		@Override
		public Socket createSocket(String host, int port) throws IOException {
			return processSocket(_default.createSocket(host, port));
		}

		@Override
		public Socket createSocket(InetAddress host, int port) throws IOException {
			return processSocket(_default.createSocket(host, port));
		}

		@Override
		public Socket createSocket(String host, int port, InetAddress localHost, int localPort) throws IOException {
			return processSocket(_default.createSocket(host, port, localHost, localPort));
		}
		
		@Override
		public Socket createSocket(InetAddress address, int port, InetAddress localAddress, int localPort) throws IOException {
			return processSocket(_default.createSocket(address, port, localAddress, localPort));
		}
		
		@Override
		public Socket createSocket(Socket s, String host, int port, boolean autoClose) throws IOException {
			return processSocket(_default.createSocket(s,host,port,autoClose));
		}
		

		private static Socket processSocket(Socket socket) {
			((SSLSocket) socket).setEnabledProtocols(new String[] { "TLSv1" });
			return socket;
		}
	}
}