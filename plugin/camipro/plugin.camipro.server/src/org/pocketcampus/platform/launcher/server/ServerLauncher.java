package org.pocketcampus.platform.launcher.server;

import java.util.ArrayList;

import org.pocketcampus.plugin.camipro.server.CamiproServiceImpl;
import org.pocketcampus.plugin.camipro.shared.CamiproService;


public class ServerLauncher {
	
	public static class PocketCampusServer extends ServerBase {
		protected ArrayList<Processor> getServiceProcessors() {
			ArrayList<Processor> processors = new ArrayList<Processor>();
			processors.add(new Processor(new CamiproService.Processor<CamiproServiceImpl>(new CamiproServiceImpl()), "camipro"));
			return processors;
		}
	}
	
	public static void main(String[] args) throws Exception {
		ServerBase server = new PocketCampusServer();
		server.start();
	}
	
}
