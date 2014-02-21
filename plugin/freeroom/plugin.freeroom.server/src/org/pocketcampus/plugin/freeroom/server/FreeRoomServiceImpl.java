package org.pocketcampus.plugin.freeroom.server;

import org.pocketcampus.plugin.freeroom.shared.FreeRoomService;

/**
 * FreeRoomServiceImpl
 * 
 * The implementation of the server side of the FreeRoom Plugin.
 * 
 * It fetches the user's FreeRoom data from the FreeRoom servers.
 * 
 * @author Amer <amer.chamseddine@epfl.ch>
 *
 */
public class FreeRoomServiceImpl implements FreeRoomService.Iface {
	
	public FreeRoomServiceImpl() {
		System.out.println("Starting FreeRoom plugin server ...");
	}
	
}
