package org.pocketcampus.core.communication;

import org.pocketcampus.core.plugin.PluginInfo;

public class RequestHandler {
	private PluginInfo pluginInfo_;
	
	// EMULATOR URL
	//private String serverUrl_ = "http://10.0.0.2:8080/pocketcampus-server/";
	
	// AWS SERVER URL
	private String serverUrl_ = "http://pcepfl.elasticbeanstalk.com/";
	
	public RequestHandler(PluginInfo pluginInfo) {
		pluginInfo_ = pluginInfo;
	}

	public void execute(ServerRequest req, RequestParameters... params) {
		req.setPluginInfo(pluginInfo_);
		req.setServerUrl(serverUrl_);
		req.execute(params);
	}
	
	public void execute(ImageRequest req, String... params) {
		req.execute(params);
	}
	
	public String getRequestUrl(RequestParameters req) {
		return serverUrl_ + pluginInfo_.getId() + req.toString();
	}
}






