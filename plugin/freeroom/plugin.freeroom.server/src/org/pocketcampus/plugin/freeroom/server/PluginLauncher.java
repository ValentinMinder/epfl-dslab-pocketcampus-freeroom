package org.pocketcampus.plugin.freeroom.server;

import java.util.ArrayList;

import org.pocketcampus.platform.launcher.server.Processor;
import org.pocketcampus.platform.launcher.server.ServerBase;
import org.pocketcampus.plugin.freeroom.server.FreeRoomServiceImpl;
import org.pocketcampus.plugin.freeroom.shared.FreeRoomService;


public class PluginLauncher {
	
	public static class PocketCampusServer extends ServerBase {
		protected ArrayList<Processor> getServiceProcessors() {
			ArrayList<Processor> processors = new ArrayList<Processor>();
			processors.add(new Processor(new FreeRoomService.Processor<FreeRoomServiceImpl>(new FreeRoomServiceImpl()), "freeroom"));
			return processors;
		}
	}
	
	public static void main(String[] args) throws Exception {
		ServerBase server = new PocketCampusServer();
		server.start();
	}
	
}
