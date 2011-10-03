//// Copyright (c) 2006- Facebook
//// Distributed under the Thrift Software License
////
//// See accompanying file LICENSE or visit the Thrift site at:
//// http://developers.facebook.com/thrift/
//
//package org.pocketcampus.platform.sdk.server;
//
//import java.util.HashMap;
//import java.util.Map;
//
//import org.apache.thrift.TProcessor;
//import org.apache.thrift.TProcessorFactory;
//import org.apache.thrift.protocol.TBinaryProtocol;
//import org.apache.thrift.protocol.TProtocolFactory;
//import org.apache.thrift.transport.TServerTransport;
//import org.apache.thrift.transport.TTransportFactory;
//
//
///**
// * Generic interface for a multiplexing Thrift server.
// *
// * @author Mark Slee <mcslee@facebook.com>
// */
//public abstract class TMultiplexServer {
//
//	/**
//	 * Core processors by name
//	 */
//	protected Map<String, TProcessorFactory> processors_ = new HashMap<String, TProcessorFactory>();
//
//	/**
//	 * Server transport
//	 */
//	protected TServerTransport serverTransport_;
//
//	/**
//	 * Input Transport Factory
//	 */
//	protected TTransportFactory inputTransportFactory_;
//
//	/**
//	 * Output Transport Factory
//	 */
//	protected TTransportFactory outputTransportFactory_;
//
//	/**
//	 * Input Protocol Factory
//	 */
//	protected TProtocolFactory inputProtocolFactory_;
//
//	/**
//	 * Output Protocol Factory
//	 */
//	protected TProtocolFactory outputProtocolFactory_;
//
//	/**
//	 * Default constructors.
//	 */
//
//	protected TMultiplexServer(TServerTransport serverTransport) {
//		this(serverTransport,
//				new TTransportFactory(),
//				new TTransportFactory(),
//				new TBinaryProtocol.Factory(),
//				new TBinaryProtocol.Factory());
//	}
//
//	protected TMultiplexServer(TServerTransport serverTransport,
//			TTransportFactory transportFactory) {
//		this(serverTransport,
//				transportFactory,
//				transportFactory,
//				new TBinaryProtocol.Factory(),
//				new TBinaryProtocol.Factory());
//	}
//
//	protected TMultiplexServer(TServerTransport serverTransport,
//			TTransportFactory transportFactory,
//			TProtocolFactory protocolFactory) {
//		this(serverTransport,
//				transportFactory,
//				transportFactory,
//				protocolFactory,
//				protocolFactory);
//	}
//
//	protected TMultiplexServer(TServerTransport serverTransport,
//			TTransportFactory inputTransportFactory,
//			TTransportFactory outputTransportFactory,
//			TProtocolFactory inputProtocolFactory,
//			TProtocolFactory outputProtocolFactory) {
//		serverTransport_ = serverTransport;
//		inputTransportFactory_ = inputTransportFactory;
//		outputTransportFactory_ = outputTransportFactory;
//		inputProtocolFactory_ = inputProtocolFactory;
//		outputProtocolFactory_ = outputProtocolFactory;
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
//	 * The run method fires up the server and gets things going.
//	 */
//	 public abstract void serve();
//
//	/**
//	 * Stop the server. This is optional on a per-implementation basis. Not
//	 * all servers are required to be cleanly stoppable.
//	 */
//	 public void stop() {}
//
//}