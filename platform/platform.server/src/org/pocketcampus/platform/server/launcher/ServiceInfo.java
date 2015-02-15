package org.pocketcampus.platform.server.launcher;

import org.apache.thrift.TProcessor;
import org.pocketcampus.platform.server.StateChecker;

import javax.servlet.http.HttpServlet;

public final class ServiceInfo {
	public final String name;
	public final TProcessor thriftProcessor;
	public final HttpServlet rawProcessor;
	public final StateChecker stateChecker;

	public ServiceInfo(final String name, final TProcessor thriftProcessor, final HttpServlet rawProcessor, StateChecker stateChecker) {
		this.name = name;
		this.thriftProcessor = thriftProcessor;
		this.rawProcessor = rawProcessor;
		this.stateChecker = stateChecker;
	}
}