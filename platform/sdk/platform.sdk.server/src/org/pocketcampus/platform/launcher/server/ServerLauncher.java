package org.pocketcampus.platform.launcher.server;

import java.util.ArrayList;


public class ServerLauncher {
	
	public static class PocketCampusServer extends ServerBase {
		protected ArrayList<Processor> getServiceProcessors() {
			return new ArrayList<Processor>();
		}
	}
	
	public static void main(String[] args) throws Exception {
		ServerBase server = new PocketCampusServer();
		server.start();
	}
	
}
