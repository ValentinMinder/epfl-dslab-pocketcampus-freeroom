package org.pocketcampus.platform.launcher.server;

import java.util.ArrayList;

import org.pocketcampus.plugin.authentication.server.AuthenticationServiceImpl;
import org.pocketcampus.plugin.authentication.shared.AuthenticationService;


public class ServerLauncher {
	
	public static class PocketCampusServer extends ServerBase {
		protected ArrayList<Processor> getServiceProcessors() {
			ArrayList<Processor> processors = new ArrayList<Processor>();
			processors.add(new Processor(new AuthenticationService.Processor<AuthenticationServiceImpl>(new AuthenticationServiceImpl()), "authentication"));
			return processors;
		}
	}
	
	public static void main(String[] args) throws Exception {
		ServerBase server = new PocketCampusServer();
		server.start();
	}
	
}
