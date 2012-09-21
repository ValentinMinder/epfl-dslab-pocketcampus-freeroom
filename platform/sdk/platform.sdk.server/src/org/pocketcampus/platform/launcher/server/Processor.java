package org.pocketcampus.platform.launcher.server;

import org.apache.thrift.TProcessor;


public class Processor {
	private TProcessor mThriftProcessor;
	private String mServiceName;
	
	public Processor(TProcessor thriftProcessor, String serviceName) {
		mThriftProcessor = thriftProcessor;
		mServiceName = serviceName;
	}
	
	public String getServiceName() {
		return mServiceName;
	}
	
	public TProcessor getThriftProcessor() {
		return mThriftProcessor;
	}
}
