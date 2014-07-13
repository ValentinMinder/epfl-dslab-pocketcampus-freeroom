package org.pocketcampus.platform.server.launcher;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.LinkedList;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.thrift.TProcessor;
import org.apache.thrift.protocol.TBinaryProtocol;
import org.apache.thrift.protocol.TJSONProtocol;
import org.apache.thrift.protocol.TProtocolFactory;
import org.eclipse.jetty.http.ssl.SslContextFactory;
import org.eclipse.jetty.server.Connector;
import org.eclipse.jetty.server.Handler;
import org.eclipse.jetty.server.NCSARequestLog;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.server.handler.ContextHandlerCollection;
import org.eclipse.jetty.server.handler.HandlerCollection;
import org.eclipse.jetty.server.handler.RequestLogHandler;
import org.eclipse.jetty.server.nio.SelectChannelConnector;
import org.eclipse.jetty.server.ssl.SslSelectChannelConnector;
import org.eclipse.jetty.servlet.ServletContextHandler;
import org.eclipse.jetty.servlet.ServletHolder;

import static org.pocketcampus.platform.server.launcher.PCServerConfig.PC_SRV_CONFIG;

public abstract class ServerBase {
	
	private static TProtocolFactory binProtocolFactory = new TBinaryProtocol.Factory();
	private static TProtocolFactory jsonProtocolFactory = new TJSONProtocol.Factory();
	
	public void start(String config) throws Exception {
		initializeConfig(config);
		
		Server server = new Server();
		
        LinkedList<Connector> conn_list = new LinkedList<Connector>();

        SelectChannelConnector connector0 = new SelectChannelConnector();
        connector0.setPort(PC_SRV_CONFIG.getInteger("LISTEN_ON_PORT"));
        connector0.setMaxIdleTime(30000);
        connector0.setRequestHeaderSize(8192);
        conn_list.add(connector0);

        /*SelectChannelConnector connector1 = new SelectChannelConnector();
        connector1.setHost("127.0.0.1");
        connector1.setPort(7070);
        connector1.setThreadPool(new QueuedThreadPool(20));
        connector1.setName("admin");
        conn_list.add(connector1);*/

        
        if(PC_SRV_CONFIG.getInteger("SSL_LISTEN_ON_PORT") != 0) {
	        SslSelectChannelConnector ssl_connector = new SslSelectChannelConnector();
	        ssl_connector.setPort(PC_SRV_CONFIG.getInteger("SSL_LISTEN_ON_PORT"));
	        SslContextFactory cf = ssl_connector.getSslContextFactory();
	        cf.setKeyStore(PC_SRV_CONFIG.getString("SSL_KEYSTORE"));
	        cf.setKeyStorePassword(PC_SRV_CONFIG.getString("SSL_KEYSTORE_PASS"));
	        cf.setKeyManagerPassword(PC_SRV_CONFIG.getString("SSL_KEYMGR_PASS"));
	        conn_list.add(ssl_connector);
        }

        server.setConnectors((Connector[]) conn_list.toArray(new Connector[conn_list.size()]));
        
		ServletContextHandler context = new ServletContextHandler(ServletContextHandler.SESSIONS);
		context.setContextPath("/v3r1");
		
		String locale = "en_US";
		String encoding = "UTF-8";
		context.addLocaleEncoding(locale, encoding);
		
		try {
			String thisIp = InetAddress.getLocalHost().getHostAddress();
			System.out.println("Local address: " + thisIp);
		} catch (UnknownHostException e) {
			e.printStackTrace();
		}
		
		ArrayList<Processor> processors = getServiceProcessors();

		for(Processor processor : processors) {
			TProcessor thriftProcessor = processor.getThriftProcessor();
			TrackingThriftServlet binThriftServlet = new TrackingThriftServlet(thriftProcessor, binProtocolFactory);
			TrackingThriftServlet jsonThriftServlet = new TrackingThriftServlet(thriftProcessor, jsonProtocolFactory);
			context.addServlet(new ServletHolder(binThriftServlet), "/" + processor.getServiceName());
			context.addServlet(new ServletHolder(jsonThriftServlet), "/json-" + processor.getServiceName());
			if(processor.getRawProcessor() != null) {
				context.addServlet(new ServletHolder(processor.getRawProcessor()), "/raw-" + processor.getServiceName());
			}
		}
		
		// add dummy servlet to ping to check if server is up
		context.addServlet(new ServletHolder(new HttpServlet() {
			private static final long serialVersionUID = 2812085352871299189L;
			protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
				    OutputStream out = response.getOutputStream();
				    out.write("OK".getBytes());
				    out.flush();
				  }
		}), "/ping");
		
		NCSARequestLog requestLog = new NCSARequestLog(PC_SRV_CONFIG.getString("JETTY_LOGFILES_PATH") + "/jetty-yyyy_mm_dd.request.log");
		requestLog.setRetainDays(90);
		requestLog.setAppend(true);
		requestLog.setExtended(false);
		requestLog.setLogTimeZone("GMT");
		RequestLogHandler requestLogHandler = new RequestLogHandler();
		requestLogHandler.setRequestLog(requestLog);
		
		HandlerCollection handlers = new HandlerCollection();
		ContextHandlerCollection contexts = new ContextHandlerCollection();
		contexts.setHandlers(new Handler[]{context});
		handlers.setHandlers(new Handler[]{contexts, requestLogHandler});
		server.setHandler(handlers);

		server.start();
		server.join();
	}
	
	private void initializeConfig(String config) {
		
		try {
			
			/**
			* First load internal config.
			*   This should be exhaustive/comprehensive
			*   meaning all config params should be assigned a value here.
			*/
			PC_SRV_CONFIG.load(this.getClass().getResourceAsStream("pocketcampus-server.config"));
			
		} catch (IOException e) {
			e.printStackTrace();
		}
		try {
			
			/**
			* Then override with config file in absolute path.
			*   All meaningful params should be overridden
			*   here.
			*/
			String configFile = "/etc/pocketcampus-server.config";
			if(new File(configFile).exists()) {
				PC_SRV_CONFIG.load(new FileInputStream(configFile));
			}
			
		} catch (IOException e) {
			e.printStackTrace();
		}
		try {
			
			/**
			* Finally override with config file given as arg.
			*   This is mainly used for running multiple instances 
			*   of the server on the same machine.
			*/
			if(config != null) {
				PC_SRV_CONFIG.load(new FileInputStream(config));
			}
			
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	protected abstract ArrayList<Processor> getServiceProcessors();
	
}
