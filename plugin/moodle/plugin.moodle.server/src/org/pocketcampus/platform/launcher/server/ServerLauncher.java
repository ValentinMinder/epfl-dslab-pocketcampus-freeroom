package org.pocketcampus.platform.launcher.server;

import java.util.ArrayList;

import org.pocketcampus.plugin.moodle.server.MoodleServiceImpl;
import org.pocketcampus.plugin.moodle.shared.MoodleService;


public class ServerLauncher {
	
	public static class PocketCampusServer extends ServerBase {
		protected ArrayList<Processor> getServiceProcessors() {
			ArrayList<Processor> processors = new ArrayList<Processor>();
			processors.add(new Processor(new MoodleService.Processor<MoodleServiceImpl>(new MoodleServiceImpl()), "moodle"));
			return processors;
		}
	}
	
	public static void main(String[] args) throws Exception {
		ServerBase server = new PocketCampusServer();
		server.start();
	}
	
}
