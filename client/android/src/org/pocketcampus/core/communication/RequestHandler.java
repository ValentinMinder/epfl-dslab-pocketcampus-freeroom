package org.pocketcampus.core.communication;

import org.pocketcampus.core.plugin.PluginInfo;

public class RequestHandler {
	private PluginInfo pluginInfo_;

	/**
	 * PRODUCTION SERVER
	 * Plugins: Map, Food, Bikes, Transport
	 * Public DNS: http://ec2-46-51-131-245.eu-west-1.compute.amazonaws.com/
	 */
	private static final String serverUrl_ = "http://epflserv.pocketcampus.org/alpha1/";
	
	/**
	 * DEVELOPMENT SERVERS
	 */
	// EMULATOR URL
	//private String serverUrl_ = "http://10.0.0.2:8080/pocketcampus-server/";

	// ELODIE LOCAL SERVER URL
	//private String serverUrl_ = "http://128.178.240.75:8080/pocketcampus-server/";

	// FLORIAN LOCAL SERVER URL
	//private static final String serverUrl_ = "http://10.0.0.157:8080/pocketcampus-server/";
	
	// JOHAN LOCAL SERVER URL
	//private String serverUrl_ = "http://192.168.1.46:8080/pocketcampus-server/";
	//private String serverUrl_ = "http://128.178.244.121:8080/pocketcampus-server/";

	public RequestHandler(PluginInfo pluginInfo) {
		pluginInfo_ = pluginInfo;
	}
	
	public void execute(Request<?> req, String command, RequestParameters... params) {
		req.setPluginInfo(pluginInfo_);
		req.setServerUrl(serverUrl_);
		req.setCommand(command);
		req.start(params);
	}

	public String getRequestUrl(RequestParameters req, String command) {
		return serverUrl_ + pluginInfo_.getId() + "/" + command + ".do";
	}
	
	public static String getServerUrl() {
		return serverUrl_;
	}
}






