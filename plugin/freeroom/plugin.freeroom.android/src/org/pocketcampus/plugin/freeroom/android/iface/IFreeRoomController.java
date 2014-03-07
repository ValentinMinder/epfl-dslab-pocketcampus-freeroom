package org.pocketcampus.plugin.freeroom.android.iface;

import org.pocketcampus.plugin.freeroom.shared.AutoCompleteReply;
import org.pocketcampus.plugin.freeroom.shared.AutoCompleteRequest;
import org.pocketcampus.plugin.freeroom.shared.FreeRoomReply;
import org.pocketcampus.plugin.freeroom.shared.FreeRoomRequest;
import org.pocketcampus.plugin.freeroom.shared.OccupancyReply;
import org.pocketcampus.plugin.freeroom.shared.OccupancyRequest;

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

	void searchFreeRoom(IFreeRoomView view, FreeRoomRequest request);

	void setFreeRoomResults(FreeRoomReply rep);
	
	void autoCompleteBuilding(IFreeRoomView view, AutoCompleteRequest request);

	void setAutoCompleteResults(AutoCompleteReply result);
	
	void checkOccupancy(IFreeRoomView view, OccupancyRequest request);

	void setCheckOccupancyResults(OccupancyReply result);
}
