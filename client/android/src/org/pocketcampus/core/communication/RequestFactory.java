package org.pocketcampus.core.communication;

import org.pocketcampus.core.plugin.Id;

public class RequestFactory {
	private Id pluginId_;
	
	public RequestFactory(Id pluginId) {
		pluginId_ = pluginId;
	}
	
	public Request newRequest() {
		return new Request(pluginId_);
	}
}
