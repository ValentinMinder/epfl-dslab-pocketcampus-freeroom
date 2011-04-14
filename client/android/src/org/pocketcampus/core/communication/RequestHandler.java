package org.pocketcampus.core.communication;

import org.pocketcampus.core.plugin.PluginInfo;

public class RequestHandler {
	private PluginInfo pluginInfo_;

	// EMULATOR URL
	//private String serverUrl_ = "http://10.0.0.2:8080/pocketcampus-server/";

	// AWS SERVER URL
//	private String serverUrl_ = "http://epflserv2-g8pjde3mf8.elasticbeanstalk.com/";

	// FLORIAN LOCAL SERVER URL
	private String serverUrl_ = "http://128.178.195.35:8080/pocketcampus-server/";

	// JOHAN LOCAL SERVER URL
	//private String serverUrl_ = "http://192.168.1.46:8080/pocketcampus-server/";

	// JOHAN DEBUG STATIC SERVER
	//private String serverUrl_ = "http://jleuleu.neqo.org/temp/";


	public RequestHandler(PluginInfo pluginInfo) {
		pluginInfo_ = pluginInfo;
	}

	public void execute(ServerRequest req, String command, RequestParameters... params) {
		req.setPluginInfo(pluginInfo_);
		req.setServerUrl(serverUrl_);
		req.setCommand(command);
		req.execute(params);
	}

	public void execute(ImageRequest req, String... params) {
		req.execute(params);
	}

	public String getRequestUrl(RequestParameters req, String command) {
		return serverUrl_ + pluginInfo_.getId() + "/" + command;
	}
}






