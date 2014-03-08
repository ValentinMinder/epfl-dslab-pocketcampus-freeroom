package org.pocketcampus.plugin.freeroom.android.iface;

import java.util.List;
import java.util.Set;

import org.pocketcampus.plugin.freeroom.shared.FRRoom;
import org.pocketcampus.plugin.freeroom.shared.Occupancy;

/**
 * IFreeRoomModel
 * 
 * Interface for the Model of the FreeRoom plugin.
 * It is empty as we have only one Model in this plugin.
 * 
 * @author Amer <amer.chamseddine@epfl.ch>
 *
 */
public interface IFreeRoomModel {

	void setFreeRoomResults(Set<FRRoom> results);
	
	Set<FRRoom> getFreeRoomResults();

	void setAutoCompleteResults(List<FRRoom> listFRRoom);

	List<FRRoom> getAutocompleteSuggestions();

	void setOccupancyResults(List<Occupancy> list);

	List<Occupancy> getListCheckedOccupancyRoom();

}
