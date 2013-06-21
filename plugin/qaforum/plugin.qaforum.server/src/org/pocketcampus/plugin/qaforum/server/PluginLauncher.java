package org.pocketcampus.plugin.qaforum.server;

import java.util.ArrayList;

import org.pocketcampus.platform.launcher.server.Processor;
import org.pocketcampus.platform.launcher.server.ServerBase;
import org.pocketcampus.plugin.qaforum.server.QAforumServiceImpl;
import org.pocketcampus.plugin.qaforum.shared.QAforumService;


public class PluginLauncher {
	
	public static class PocketCampusServer extends ServerBase {
		protected ArrayList<Processor> getServiceProcessors() {
			ArrayList<Processor> processors = new ArrayList<Processor>();
			processors.add(new Processor(new QAforumService.Processor<QAforumServiceImpl>(new QAforumServiceImpl()), "qaforum"));
			return processors;
		}
	}
	
	public static void main(String[] args) throws Exception {
		ServerBase server = new PocketCampusServer();
		server.start();
	}
	
}
