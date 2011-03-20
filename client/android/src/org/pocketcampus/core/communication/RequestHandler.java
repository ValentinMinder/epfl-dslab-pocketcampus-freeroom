package org.pocketcampus.core.communication;

import org.pocketcampus.core.plugin.PluginInfo;

public class RequestHandler {
	private PluginInfo pluginInfo_;
	//private String serverUrl_ = "http://10.0.2.2:8080/pocketcampus-server/";
	private String serverUrl_ = "http://192.168.1.48:8080/pocketcampus-server/";
	
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






