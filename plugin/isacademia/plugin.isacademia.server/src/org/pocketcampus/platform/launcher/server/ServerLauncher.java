package org.pocketcampus.platform.launcher.server;

import java.util.ArrayList;

import org.pocketcampus.plugin.isacademia.server.IsacademiaServiceImpl;
import org.pocketcampus.plugin.isacademia.shared.IsacademiaService;


public class ServerLauncher {
	
	public static class PocketCampusServer extends ServerBase {
		protected ArrayList<Processor> getServiceProcessors() {
			ArrayList<Processor> processors = new ArrayList<Processor>();
			processors.add(new Processor(new IsacademiaService.Processor<IsacademiaServiceImpl>(new IsacademiaServiceImpl()), "isacademia"));
			return processors;
		}
	}
	
	public static void main(String[] args) throws Exception {
		ServerBase server = new PocketCampusServer();
		server.start();
	}
	
}
