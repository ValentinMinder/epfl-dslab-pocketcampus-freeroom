package org.pocketcampus.plugin.freeroom.server;

import org.pocketcampus.platform.launcher.server.PocketCampusServer;

/**
 * ServerLauncher launches the actual PocketCampus server.
 * 
 * @author FreeRoom Project Team (2014/05)
 * @author Julien WEBER <julien.weber@epfl.ch>
 * @author Valentin MINDER <valentin.minder@epfl.ch>
 * 
 */

public class ServerLauncher {

	public static void main(String[] args) throws Exception {
		new PocketCampusServer().start(args.length > 0 ? args[0] : null);
	}

}
