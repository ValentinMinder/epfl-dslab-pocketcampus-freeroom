package org.pocketcampus.plugin.foo.server;

import org.apache.thrift.TException;
import org.pocketcampus.plugin.test.shared.TestService;

public class TestServiceImpl implements TestService.Iface {

	public TestServiceImpl() {
		System.out.println("Starting Test plugin server...");
	}
	
	@Override
	public int getBar() throws TException {
		System.out.println("getBar");
		return (int) (Math.random()*1000);
	}

}
