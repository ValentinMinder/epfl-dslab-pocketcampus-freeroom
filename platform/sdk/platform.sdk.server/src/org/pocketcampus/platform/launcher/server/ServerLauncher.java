package org.pocketcampus.platform.launcher.server;

public class ServerLauncher {
	
	public static void main(String[] args) throws Exception {
		new PocketCampusServer().start(args.length > 0 ? args[0] : null);
	}
	
}
