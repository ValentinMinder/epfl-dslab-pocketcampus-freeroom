package org.pocketcampus.platform.launcher.server;
import java.util.ArrayList;

import org.apache.thrift.TProcessor;
import org.apache.thrift.protocol.TBinaryProtocol;
import org.apache.thrift.protocol.TProtocolFactory;
import org.apache.thrift.server.TServlet;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.servlet.ServletContextHandler;
import org.eclipse.jetty.servlet.ServletHolder;


public abstract class ServerBase {
	private static TProtocolFactory protocolFactory = new TBinaryProtocol.Factory();
	
	public void start() throws Exception {
		Server server = new Server(8080);

		ServletContextHandler context = new ServletContextHandler(ServletContextHandler.SESSIONS);
		context.setContextPath("/");
		server.setHandler(context);

		ArrayList<Processor> processors = getServiceProcessors();

		for(Processor processor : processors) {
			TProcessor thriftProcessor = processor.getThriftProcessor();
			TServlet thriftServlet = new TServlet(thriftProcessor, protocolFactory);
			context.addServlet(new ServletHolder(thriftServlet), "/"+processor.getServiceName());
		}
		
		server.start();
		server.join();
	}
	
	protected abstract ArrayList<Processor> getServiceProcessors();
}
