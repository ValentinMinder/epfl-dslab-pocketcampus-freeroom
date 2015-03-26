package org.pocketcampus.plugin.events.server;

import org.pocketcampus.platform.server.launcher.PocketCampusServer;

public class ServerLauncher {
	public static void main(String[] args) throws Exception {
		new PocketCampusServer().start(args.length > 0 ? args[0] : null);
	}
}