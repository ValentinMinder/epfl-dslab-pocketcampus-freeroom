//package org.pocketcampus.platform.sdk.server;
//
//import java.io.IOException;
//import java.io.InputStream;
//import java.io.OutputStream;
//import java.util.ArrayList;
//import java.util.Collection;
//import java.util.HashMap;
//import java.util.Map;
//
//import javax.servlet.ServletException;
//import javax.servlet.http.HttpServlet;
//import javax.servlet.http.HttpServletRequest;
//import javax.servlet.http.HttpServletResponse;
//
//import org.apache.thrift.TApplicationException;
//import org.apache.thrift.TException;
//import org.apache.thrift.TProcessor;
//import org.apache.thrift.TProcessorFactory;
//import org.apache.thrift.protocol.TMessage;
//import org.apache.thrift.protocol.TMessageType;
//import org.apache.thrift.protocol.TProtocol;
//import org.apache.thrift.protocol.TProtocolFactory;
//import org.apache.thrift.protocol.TProtocolUtil;
//import org.apache.thrift.protocol.TType;
//import org.apache.thrift.transport.TIOStreamTransport;
//import org.apache.thrift.transport.TTransport;
//
///**
// * Servlet implementation class ThriftServer
// */
//public class TServletMultiplexed extends HttpServlet {
//
//	private static final long serialVersionUID = -6279138132567830805L;
//
//	protected Map<String, TProcessorFactory> processors_ = new HashMap<String, TProcessorFactory>();
//
//	private final TProtocolFactory inProtocolFactory;
//
//	private final TProtocolFactory outProtocolFactory;
//
//	private final Collection<Map.Entry<String, String>> customHeaders;
//
//	/**
//	 * @see HttpServlet#HttpServlet()
//	 */
//	public TServletMultiplexed(TProtocolFactory inProtocolFactory, TProtocolFactory outProtocolFactory) {
//		super();
//		this.inProtocolFactory = inProtocolFactory;
//		this.outProtocolFactory = outProtocolFactory;
//		this.customHeaders = new ArrayList<Map.Entry<String, String>>();
//	}
//
//	/**
//	 * @see HttpServlet#HttpServlet()
//	 */
//	public TServletMultiplexed(TProtocolFactory protocolFactory) {
//		this(protocolFactory, protocolFactory);
//	}
//
//	public void register(String serviceName, TProcessor processor) {
//		processors_.put(serviceName, new TProcessorFactory(processor));
//	}
//
//	public void register(String serviceName, TProcessorFactory processorFactory) {
//		processors_.put(serviceName, processorFactory);
//	}
//
//	/**
//	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse
//	 *      response)
//	 */
//	@Override
//	protected void doPost(HttpServletRequest request, HttpServletResponse response)
//	throws ServletException, IOException {
//		
//		TTransport inTransport = null;
//		TTransport outTransport = null;
//
//		try {
//			response.setContentType("application/x-thrift");
//
//			if (null != this.customHeaders) {
//				for (Map.Entry<String, String> header : this.customHeaders) {
//					response.addHeader(header.getKey(), header.getValue());
//				}
//			}
//			InputStream in = request.getInputStream();
//			OutputStream out = response.getOutputStream();
//
//			TTransport transport = new TIOStreamTransport(in, out);
//			inTransport = transport;
//			outTransport = transport;
//			
//			TProtocol inProtocol = inProtocolFactory.getProtocol(inTransport);
//			TProtocol outProtocol = outProtocolFactory.getProtocol(outTransport);
//
//			TMessage serviceNameMsg = inProtocol.readMessageBegin();
//
//			if (serviceNameMsg.type == TMessageType.SERVICE_SELECTION) {
//				TProcessorFactory processorFactory_ = processors_.get(serviceNameMsg.name);
//				
//				if (processorFactory_ != null) {
//					// Ok, send the result
//					System.out.println("Service '"+serviceNameMsg.name+"' found.");
//					
//					TProcessor processor = processorFactory_.getProcessor(transport);
//					processor.process(inProtocol, outProtocol);
//					out.flush();
//					
//				} else {
//					// Error: invalid service name
//					skipFunction(inProtocol);
//					TApplicationException x = new TApplicationException(TApplicationException.UNKNOWN_METHOD,
//							"Invalid service name: '" + serviceNameMsg.name + "'");
//					outProtocol.writeMessageBegin(new TMessage(serviceNameMsg.name, TMessageType.EXCEPTION,
//							serviceNameMsg.seqid));
//					x.write(outProtocol);
//					outProtocol.writeMessageEnd();
//					outProtocol.getTransport().flush();
//				}
//
//				inProtocol.readMessageEnd();
//				
//			} else {
//				// Error: no service selection specified.
//				TApplicationException x = new TApplicationException(TApplicationException.INVALID_MESSAGE_TYPE,
//						"Expected service selection (" + TMessageType.SERVICE_SELECTION + "), but got: "
//						+ serviceNameMsg.toString());
//				outProtocol.writeMessageBegin(new TMessage(serviceNameMsg.name, TMessageType.EXCEPTION, serviceNameMsg.seqid));
//				x.write(outProtocol);
//				outProtocol.writeMessageEnd();
//				outProtocol.getTransport().flush();
//			}
//
//		} catch (TException te) {
//			throw new ServletException(te);
//		}
//
//
//
//		//		TTransport inTransport = null;
//		//		TTransport outTransport = null;
//		//
//		//		try {
//		//			response.setContentType("application/x-thrift");
//		//
//		//			if (null != this.customHeaders) {
//		//				for (Map.Entry<String, String> header : this.customHeaders) {
//		//					response.addHeader(header.getKey(), header.getValue());
//		//				}
//		//			}
//		//			InputStream in = request.getInputStream();
//		//			OutputStream out = response.getOutputStream();
//		//
//		//			TTransport transport = new TIOStreamTransport(in, out);
//		//			inTransport = transport;
//		//			outTransport = transport;
//		//
//		//			TProtocol inProtocol = inProtocolFactory.getProtocol(inTransport);
//		//			TProtocol outProtocol = outProtocolFactory.getProtocol(outTransport);
//		//
//		//			processor.process(inProtocol, outProtocol);
//		//			out.flush();
//		//		} catch (TException te) {
//		//			throw new ServletException(te);
//		//		}
//	}
//
//	private void skipFunction(TProtocol iprot) throws TException {
//		iprot.readMessageBegin();
//		TProtocolUtil.skip(iprot, TType.STRUCT);
//		iprot.readMessageEnd();
//	}
//
//	/**
//	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse
//	 *      response)
//	 */
//	protected void doGet(HttpServletRequest request, HttpServletResponse response)
//	throws ServletException, IOException {
//		doPost(request, response);
//	}
//
//	public void addCustomHeader(final String key, final String value) {
//		this.customHeaders.add(new Map.Entry<String, String>() {
//			public String getKey() {
//				return key;
//			}
//
//			public String getValue() {
//				return value;
//			}
//
//			public String setValue(String value) {
//				return null;
//			}
//		});
//	}
//
//	public void setCustomHeaders(Collection<Map.Entry<String, String>> headers) {
//		this.customHeaders.clear();
//		this.customHeaders.addAll(headers);
//	}
//}
