//// Copyright (c) 2006- Facebook
//// Distributed under the Thrift Software License
////
//// See accompanying file LICENSE or visit the Thrift site at:
//// http://developers.facebook.com/thrift/
//
//package org.pocketcampus.platform.sdk.server;
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
//import org.apache.thrift.transport.TServerTransport;
//import org.apache.thrift.transport.TTransport;
//import org.apache.thrift.transport.TTransportException;
//import org.apache.thrift.transport.TTransportFactory;
//
///**
// * Simple singlethreaded multiplexing server for testing.
// *
// * @author Mark Slee <mcslee@facebook.com>
// */
//public class TSimpleMultiplexServer extends TMultiplexServer {
//
//	private boolean stopped_ = false;
//
//	public TSimpleMultiplexServer(TServerTransport serverTransport) {
//		super(serverTransport);
//	}
//
//	public TSimpleMultiplexServer(TServerTransport serverTransport,
//			TTransportFactory transportFactory,
//			TProtocolFactory protocolFactory) {
//		super(serverTransport, transportFactory, protocolFactory);
//	}
//
//	public TSimpleMultiplexServer(TServerTransport serverTransport,
//			TTransportFactory inputTransportFactory,
//			TTransportFactory outputTransportFactory,
//			TProtocolFactory inputProtocolFactory,
//			TProtocolFactory outputProtocolFactory) {
//		super(serverTransport, inputTransportFactory, outputTransportFactory, inputProtocolFactory, outputProtocolFactory);
//	}
//
//	public void serve() {
//		stopped_ = false;
//		try {
//			serverTransport_.listen();
//		} catch (TTransportException ttx) {
//			ttx.printStackTrace();
//			return;
//		}
//
//		while (!stopped_) {
//			TTransport transport = null;
//			TTransport inputTransport = null;
//			TTransport outputTransport = null;
//			try {
//				transport = serverTransport_.accept();
//				if (transport != null) {
//					System.out.println("Connection accepted");
//					inputTransport = inputTransportFactory_.getTransport(transport);
//					outputTransport = outputTransportFactory_.getTransport(transport);
//
//					TProtocol inputProtocol = inputProtocolFactory_.getProtocol(inputTransport);
//					TProtocol outputProtocol = outputProtocolFactory_.getProtocol(outputTransport);
//
//					boolean functionIndicatesStopping = false;
//					do {
//						TMessage serviceNameMsg = inputProtocol.readMessageBegin();
//
//						if (serviceNameMsg.type == TMessageType.SERVICE_SELECTION) {
//							TProcessorFactory processorFactory_ = processors_.get(serviceNameMsg.name);
//							if (processorFactory_ == null) {
//								skipFunction(inputProtocol);
//								TApplicationException x = new TApplicationException(TApplicationException.UNKNOWN_METHOD,
//										"Invalid service name: '" + serviceNameMsg.name + "'");
//								outputProtocol.writeMessageBegin(new TMessage(serviceNameMsg.name, TMessageType.EXCEPTION,
//										serviceNameMsg.seqid));
//								x.write(outputProtocol);
//								outputProtocol.writeMessageEnd();
//								outputProtocol.getTransport().flush();
//							} else {
//								TProcessor processor = processorFactory_.getProcessor(transport);
//								functionIndicatesStopping = !processor.process(inputProtocol, outputProtocol);
//							}
//
//							inputProtocol.readMessageEnd();
//						} else {
//							// FIXME: don't know what to expect inside this message.
//							// What to do now?
//							TApplicationException x = new TApplicationException(TApplicationException.INVALID_MESSAGE_TYPE,
//									"Expected service selection (" + TMessageType.SERVICE_SELECTION + "), but got: "
//									+ serviceNameMsg.toString());
//							outputProtocol.writeMessageBegin(new TMessage(serviceNameMsg.name, TMessageType.EXCEPTION,
//									serviceNameMsg.seqid));
//							x.write(outputProtocol);
//							outputProtocol.writeMessageEnd();
//							outputProtocol.getTransport().flush();
//						}
//					} while (!functionIndicatesStopping);
//				}
//			} catch (TTransportException ttx) {
//				// Client died, just move on
//			} catch (TException tx) {
//				System.out.println("Connection closed because of exception: " + tx);
//				if (!stopped_) {
//					tx.printStackTrace();
//				}
//			} catch (Exception x) {
//				System.out.println("Connection closed because of exception: " + x);
//				if (!stopped_) {
//					x.printStackTrace();
//				}
//			}
//
//			if (inputTransport != null) {
//				inputTransport.close();
//			}
//
//			if (outputTransport != null) {
//				outputTransport.close();
//			}
//		}
//	}
//
//	private void skipFunction(TProtocol iprot) throws TException {
//		iprot.readMessageBegin();
//		TProtocolUtil.skip(iprot, TType.STRUCT);
//		iprot.readMessageEnd();
//	}
//
//	public void stop() {
//		stopped_ = true;
//		serverTransport_.interrupt();
//	}
//}