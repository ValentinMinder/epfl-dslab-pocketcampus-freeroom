package org.pocketcampus.plugin.freeroom.android.iface;

import org.pocketcampus.plugin.freeroom.shared.FreeRoomRequest;

/**
 * IFreeRoomController
 * 
 * Interface for the Controller of the FreeRoom plugin.
 * It is empty as we have only one Controller in this plugin.
 * 
 * @author Amer <amer.chamseddine@epfl.ch>
 *
 */
public interface IFreeRoomController {

	void search(IFreeRoomView view, FreeRoomRequest request);
}
