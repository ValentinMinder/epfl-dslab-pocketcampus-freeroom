package org.pocketcampus.platform.launcher.server;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.util.ArrayList;
import org.apache.thrift.TProcessor;
import org.apache.thrift.protocol.TBinaryProtocol;
import org.apache.thrift.protocol.TProtocolFactory;
import org.apache.thrift.server.TServlet;
import org.eclipse.jetty.server.Handler;
import org.eclipse.jetty.server.NCSARequestLog;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.server.handler.ContextHandlerCollection;
import org.eclipse.jetty.server.handler.HandlerCollection;
import org.eclipse.jetty.server.handler.RequestLogHandler;
import org.eclipse.jetty.servlet.ServletContextHandler;
import org.eclipse.jetty.servlet.ServletHolder;


public abstract class ServerBase {
	
	/****
	 * DO NOT EDIT THE PORT NUMBER HERE
	 * INSTEAD CREATE A CONFIG FILE IN THE CURRENT DIRECTORY pocketcampus-server.config
	 * A SAMPLE OF THE FILE IS IN THIS PROJECT'S ROOT DIRECTORY
	 */
	
	public static int LISTEN_ON_PORT = 443;
	
	private static TProtocolFactory protocolFactory = new TBinaryProtocol.Factory();
	
	public void start() throws Exception {
		Server server = new Server(LISTEN_ON_PORT);
		
		ServletContextHandler context = new ServletContextHandler(ServletContextHandler.SESSIONS);
		context.setContextPath("/v3r1");
		
		String locale = "en_US";
		String encoding = "UTF-8";
		context.addLocaleEncoding(locale, encoding);
		
		ArrayList<Processor> processors = getServiceProcessors();

		for(Processor processor : processors) {
			TProcessor thriftProcessor = processor.getThriftProcessor();
			TServlet thriftServlet = new TServlet(thriftProcessor, protocolFactory);
			context.addServlet(new ServletHolder(thriftServlet), "/"+processor.getServiceName());
		}
		
		NCSARequestLog requestLog = new NCSARequestLog("./jetty-yyyy_mm_dd.request.log");
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
	
	protected abstract ArrayList<Processor> getServiceProcessors();
	
	static {
		try {
			String configFile = "pocketcampus-server.config";
			if(new File(configFile).exists()) {
				FileReader fr = new FileReader(configFile);
				BufferedReader br = new BufferedReader(fr);
				String line;
				while((line = br.readLine()) != null) {
					String[] param = line.trim().split("=");
					if(param.length == 2) {
						if("LISTEN_ON_PORT".equals(param[0]))
							LISTEN_ON_PORT = Integer.parseInt(param[1]);
					}
				}
			} else {
				FileWriter fw = new FileWriter(configFile, false);
				fw.write("LISTEN_ON_PORT=" + LISTEN_ON_PORT + "\n");
				fw.close();
			}
		} catch (Exception e) {
			System.err.println("grrrrrrrrr Exception while running static code!?!?");
			e.printStackTrace();
		}
	}
	
}
