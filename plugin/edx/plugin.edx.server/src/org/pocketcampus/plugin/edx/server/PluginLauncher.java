package org.pocketcampus.plugin.edx.server;

import java.util.ArrayList;

import org.pocketcampus.platform.launcher.server.Processor;
import org.pocketcampus.platform.launcher.server.ServerBase;
import org.pocketcampus.plugin.edx.server.EdXServiceImpl;
import org.pocketcampus.plugin.edx.shared.EdXService;


public class PluginLauncher {
	
	public static class PocketCampusServer extends ServerBase {
		protected ArrayList<Processor> getServiceProcessors() {
			ArrayList<Processor> processors = new ArrayList<Processor>();
			processors.add(new Processor(new EdXService.Processor<EdXServiceImpl>(new EdXServiceImpl()), "edx"));
			return processors;
		}
	}
	
	public static void main(String[] args) throws Exception {
		ServerBase server = new PocketCampusServer();
		server.start();
	}
	
}
