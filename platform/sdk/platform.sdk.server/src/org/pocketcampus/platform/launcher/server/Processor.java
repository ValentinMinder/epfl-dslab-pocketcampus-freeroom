package org.pocketcampus.platform.launcher.server;

import javax.servlet.http.HttpServlet;

import org.apache.thrift.TProcessor;


public class Processor {
	private TProcessor mThriftProcessor;
	private String mServiceName;
	private HttpServlet mRawProcessor;
	
	public Processor(TProcessor thriftProcessor, String serviceName) {
		mThriftProcessor = thriftProcessor;
		mServiceName = serviceName;
		mRawProcessor = null;
	}
	
	public String getServiceName() {
		return mServiceName;
	}
	
	public TProcessor getThriftProcessor() {
		return mThriftProcessor;
	}
	
	public void setRawProcessor(HttpServlet rawProcessor) {
		mRawProcessor = rawProcessor;
	}
	
	public HttpServlet getRawProcessor() {
		return mRawProcessor;
	}
}
