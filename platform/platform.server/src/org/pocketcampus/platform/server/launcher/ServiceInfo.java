package org.pocketcampus.platform.server.launcher;

import javax.servlet.http.HttpServlet;

import org.apache.thrift.TProcessor;

public final class ServiceInfo {
	public final String name;
	public final TProcessor thriftProcessor;
	public final HttpServlet rawProcessor;

	public ServiceInfo(final String name, final TProcessor thriftProcessor, final HttpServlet rawProcessor) {
		this.name = name;
		this.thriftProcessor = thriftProcessor;
		this.rawProcessor = rawProcessor;
	}
}