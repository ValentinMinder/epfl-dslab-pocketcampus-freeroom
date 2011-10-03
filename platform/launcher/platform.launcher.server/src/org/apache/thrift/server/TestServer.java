package org.apache.thrift.server;

import org.apache.thrift.TProcessor;
import org.apache.thrift.protocol.TBinaryProtocol;
import org.apache.thrift.protocol.TProtocolFactory;
import org.pocketcampus.platform.sdk.server.TServlet;
import org.pocketcampus.plugin.foo.server.TestServiceImpl;
import org.pocketcampus.plugin.test.shared.TestService;

public class TestServer extends TServlet {
	private static final long serialVersionUID = -4054552246415661363L;
	
	private static TProcessor processor = new TestService.Processor<TestServiceImpl>(new TestServiceImpl());
	private static TProtocolFactory protocolFactory = new TBinaryProtocol.Factory();

	public TestServer() {
		super(processor, protocolFactory);
	}
}