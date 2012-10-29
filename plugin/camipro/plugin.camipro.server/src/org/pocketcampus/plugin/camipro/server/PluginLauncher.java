package org.pocketcampus.plugin.camipro.server;

import java.util.ArrayList;

import org.pocketcampus.platform.launcher.server.Processor;
import org.pocketcampus.platform.launcher.server.ServerBase;
import org.pocketcampus.plugin.camipro.shared.CamiproService;


public class PluginLauncher {
	
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
