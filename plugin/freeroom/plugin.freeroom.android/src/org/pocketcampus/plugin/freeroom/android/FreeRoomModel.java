package org.pocketcampus.plugin.freeroom.android;


import java.util.List;
import java.util.Map;
import java.util.Set;

import org.pocketcampus.android.platform.sdk.core.IView;
import org.pocketcampus.android.platform.sdk.core.PluginModel;
import org.pocketcampus.plugin.freeroom.android.iface.IFreeRoomModel;
import org.pocketcampus.plugin.freeroom.android.iface.IFreeRoomView;
import org.pocketcampus.plugin.freeroom.shared.FRRoom;
import org.pocketcampus.plugin.freeroom.shared.Occupancy;

import android.content.Context;
import android.util.Log;

/**
 * FreeRoomModel - The Model that stores the data of this plugin.
 * 
 * This is the Model associated with the FreeRoom plugin.
 * It stores the data required for the correct functioning of the plugin.
 * Some data is persistent. e.g.freeroomCookie.
 * Other data are temporary.
 * 
 * @author Amer <amer.chamseddine@epfl.ch>
 *
 */
public class FreeRoomModel extends PluginModel implements IFreeRoomModel {
	
	/**
	 * Reference to the Views that need to be notified when the stored data changes.
	 */
	IFreeRoomView mListeners = (IFreeRoomView) getListeners();
	
	/**List of <code>FRRoom</code>'s obtained from the freeroom query**/
	private Set<FRRoom> mFreeRoomResult;
	/**List of suggestions for the check occupancy search view*/
	private List<FRRoom> mAutoCompleteSuggestions;
	// TODO: not used NOW
	/**Used to specify the displayed <code>FRRoom</code> in the results view*/
	private FRRoom mSelectedFRRoom;
	/**Ordered list of <code>FRRoom</code>'s displayed in the check occupancy*/
	private List<FRRoom> mListCheckedOccupancyRoom;
	/**Map of <code>FRRoom</code>'s to their respective occupancy*/
	private Map<FRRoom, Occupancy> mMapOccupancy;
	
	/**
	 * Constructor with reference to the context.
	 * 
	 * We need the context to be able to instantiate
	 * the SharedPreferences object in order to use
	 * persistent storage.
	 * 
	 * @param context is the Application Context.
	 */
	public FreeRoomModel(Context context) {
		
	}
	
	/**
	 * Returns the Type of the Views associated with this plugin.
	 */
	@Override
	protected Class<? extends IView> getViewInterface() {
		return IFreeRoomView.class;
	}

	/**
	 * Returns the registered listeners to by notified.
	 */
	public IFreeRoomView getListenersToNotify() {
		return mListeners;
	}
	
	/**
	 * Getter for the results of the request
	 * @return Set of FRRoom
	 */
	@Override
	public Set<FRRoom> getFreeRoomResults() {
		return mFreeRoomResult;
	}
	
	/**
	 * Setter for the results of a freeroom request
	 * @param results the result for the specific freeroom search
	 */
	@Override
	public void setFreeRoomResults(Set<FRRoom> results){
		mFreeRoomResult = results;
		Log.v("Freeroom","result set via the model");
		mListeners.freeRoomResultsUpdated();
		
	}

	/**
	 * Gets the currently selected <code>FRRoom</code>
	 */
	public FRRoom getSelectedFRRoom() {
		return mSelectedFRRoom;
	}
	
	/**
	 * Sets which <code>FRRoom</code> is currently displayed.
	 */
	public void selectFRRoom(FRRoom choosen_one){
		mSelectedFRRoom = choosen_one;
	}

	/**
	 * Sets the suggestions to the autocomplete and notifies the listeners.
	 * @param listFRRoom
	 */
	@Override
	public void setAutoCompleteResults(List<FRRoom> listFRRoom) {
		mAutoCompleteSuggestions = listFRRoom;
		mListeners.autoCompletedUpdated();
	}


	/**
	 * Gets the suggestions for the autocomplete
	 */
	@Override
	public List<FRRoom> getAutocompleteSuggestions() {
		return mAutoCompleteSuggestions;
	}

	/**
	 * Sets the occupancy result for all the rooms and notifies the listeners.
	 * @param listFRRoom
	 */
	public void setOccupancyResults(List<FRRoom> list,
			Map<FRRoom, Occupancy> map) {
		mListCheckedOccupancyRoom = list;
		mMapOccupancy = map;
		mListeners.occupancyResultUpdated();
	}
	
	/**
	 * Gets the list of room checked against occupancy.
	 * @return
	 */
	public List<FRRoom> getListCheckedOccupancyRoom() {
		return mListCheckedOccupancyRoom;
	}
	
	/**
	 * Gets the map that represent occupancy for all the rooms.
	 * @return
	 */
	public Map<FRRoom, Occupancy> getMapOccupancy() {
		return mMapOccupancy;
	}
	
}
