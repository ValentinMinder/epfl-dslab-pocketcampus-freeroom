package org.pocketcampus.platform.server.launcher;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.net.InetAddress;
import java.util.ArrayList;
import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.thrift.protocol.TBinaryProtocol;
import org.apache.thrift.protocol.TProtocolFactory;
import org.eclipse.jetty.http.ssl.SslContextFactory;
import org.eclipse.jetty.server.Connector;
import org.eclipse.jetty.server.Handler;
import org.eclipse.jetty.server.NCSARequestLog;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.server.handler.HandlerCollection;
import org.eclipse.jetty.server.handler.RequestLogHandler;
import org.eclipse.jetty.server.nio.SelectChannelConnector;
import org.eclipse.jetty.server.ssl.SslSelectChannelConnector;
import org.eclipse.jetty.servlet.ServletContextHandler;
import org.eclipse.jetty.servlet.ServletHolder;

public abstract class ServerBase {
	public void start(String config) throws Exception {
		System.out.println("Local address is " + InetAddress.getLocalHost().getHostAddress());

		initializeConfig(config);

		HandlerCollection handlers = new HandlerCollection();
		handlers.setHandlers(new Handler[] { getServicesHandler(), getLogHandler() });

		Server server = new Server();
		server.setConnectors(getConnectors());
		server.setHandler(handlers);
		server.start();
		server.join();
	}
	
	protected abstract List<ServiceInfo> getServices();

	private Connector[] getConnectors() {
		ArrayList<Connector> conns = new ArrayList<Connector>();

		// Default (HTTP) connector
		SelectChannelConnector connector = new SelectChannelConnector();
		connector.setPort(PocketCampusServer.CONFIG.getInteger("LISTEN_ON_PORT"));
		connector.setMaxIdleTime(30000);
		connector.setRequestHeaderSize(8192);
		conns.add(connector);

		// Debug connector on localhost
		 SelectChannelConnector debugConnector = new SelectChannelConnector();
		 debugConnector.setHost("127.0.0.1");
		 debugConnector.setPort(7070);
		 debugConnector.setThreadPool(new org.eclipse.jetty.util.thread.QueuedThreadPool(20));
		 debugConnector.setName("admin");
		 conns.add(debugConnector);

		// Secure (HTTPS) connector
		if (PocketCampusServer.CONFIG.getInteger("SSL_LISTEN_ON_PORT") != 0) {
			SslSelectChannelConnector sslConnector = new SslSelectChannelConnector();
			sslConnector.setPort(PocketCampusServer.CONFIG.getInteger("SSL_LISTEN_ON_PORT"));

			SslContextFactory cf = sslConnector.getSslContextFactory();
			cf.setKeyStore(PocketCampusServer.CONFIG.getString("SSL_KEYSTORE"));
			cf.setKeyStorePassword(PocketCampusServer.CONFIG.getString("SSL_KEYSTORE_PASS"));
			cf.setKeyManagerPassword(PocketCampusServer.CONFIG.getString("SSL_KEYMGR_PASS"));

			conns.add(sslConnector);
		}

		return (Connector[]) conns.toArray(new Connector[conns.size()]);
	}

	private Handler getServicesHandler() {
		ServletContextHandler handler = new ServletContextHandler(ServletContextHandler.SESSIONS);
		handler.setContextPath("/v3r1");
		handler.addLocaleEncoding("en_US", "UTF-8");
		handler.addServlet(getPingServlet(), "/ping");
		addProcessorServlets(handler);
		return handler;
	}

	private Handler getLogHandler() {
		NCSARequestLog log = new NCSARequestLog(PocketCampusServer.CONFIG.getString("JETTY_LOGFILES_PATH") + "/jetty-yyyy_mm_dd.request.log");
		log.setRetainDays(90);
		log.setAppend(true);
		log.setExtended(false);
		log.setLogTimeZone("GMT");
		RequestLogHandler handler = new RequestLogHandler();
		handler.setRequestLog(log);
		return handler;
	}

	private void addProcessorServlets(ServletContextHandler context) {
		TProtocolFactory binProtocolFactory = new TBinaryProtocol.Factory();
		
		for (ServiceInfo service : getServices()) {
			TrackingThriftServlet binServlet = new TrackingThriftServlet(service.thriftProcessor, binProtocolFactory);

			context.addServlet(new ServletHolder(binServlet), "/" + service.name);

			// Special case for plugins that need a "raw" (non-Thrift) servlet
			if (service.rawProcessor != null) {
				context.addServlet(new ServletHolder(service.rawProcessor), "/raw-" + service.name);
			}
		}
	}

	@SuppressWarnings("serial")
	private ServletHolder getPingServlet() {
		return new ServletHolder(new HttpServlet() {
			protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
				OutputStream out = response.getOutputStream();
				out.write("OK".getBytes());
				out.flush();
			}
		});
	}

	private void initializeConfig(String config) {
		try {
			// First load internal config.
			// All non-secret params should get their value from here.
			PocketCampusServer.CONFIG.load(this.getClass().getResourceAsStream("pocketcampus-server.config"));

			// Then override with config file in absolute path.
			// All secret (e.g. tokens) params should get their value from there.
			String configFile = "/etc/pocketcampus-server.config";
			if (new File(configFile).exists()) {
				PocketCampusServer.CONFIG.load(new FileInputStream(configFile));
			}

			// Finally override with config file given as arg.
			// This is mainly used for running multiple instances
			// of the server on the same machine.
			if (config != null) {
				PocketCampusServer.CONFIG.load(new FileInputStream(config));
			}
		} catch (IOException e) {
			throw new RuntimeException("An error occurred while loading the config", e);
		}
	}

}