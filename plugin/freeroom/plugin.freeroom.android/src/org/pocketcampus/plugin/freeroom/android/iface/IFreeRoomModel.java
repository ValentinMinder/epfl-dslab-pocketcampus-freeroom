package org.pocketcampus.plugin.freeroom.android.iface;

import java.util.Set;

import org.pocketcampus.plugin.freeroom.shared.FRRoom;

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

	Set<FRRoom> getFreeRoomResults();
}
