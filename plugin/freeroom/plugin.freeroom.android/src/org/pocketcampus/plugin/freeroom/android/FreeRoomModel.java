package org.pocketcampus.plugin.freeroom.android;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.pocketcampus.android.platform.sdk.core.IView;
import org.pocketcampus.android.platform.sdk.core.PluginModel;
import org.pocketcampus.plugin.freeroom.android.iface.IFreeRoomModel;
import org.pocketcampus.plugin.freeroom.android.iface.IFreeRoomView;
import org.pocketcampus.plugin.freeroom.shared.ActualOccupation;
import org.pocketcampus.plugin.freeroom.shared.FRRoom;
import org.pocketcampus.plugin.freeroom.shared.Occupancy;

import android.content.Context;
import android.util.Log;

/**
 * FreeRoomModel - The Model that stores the data of this plugin.
 * <p>
 * This is the Model associated with the FreeRoom plugin. It stores the data
 * required for the correct functioning of the plugin. Some data is persistent
 * (none at the moment!) Other data are temporary.
 * <p>
 * 
 * @author FreeFroom Project Team - Julien WEBER <julien.weber@epfl.ch> and
 *         Valentin MINDER <valentin.minder@epfl.ch>
 * 
 */
public class FreeRoomModel extends PluginModel implements IFreeRoomModel {

	/**
	 * Reference to the Views that need to be notified when the stored data
	 * changes.
	 */
	IFreeRoomView mListeners = (IFreeRoomView) getListeners();

	/** List of <code>FRRoom</code>'s obtained from the freeroom query **/
	private Set<FRRoom> mFreeRoomResult = new HashSet<FRRoom>();
	/** List of suggestions for the check occupancy search view */
	private List<FRRoom> mAutoCompleteSuggestions = new ArrayList<FRRoom>();
	// TODO: not used NOW
	/** Used to specify the displayed <code>FRRoom</code> in the results view */
	private FRRoom mSelectedFRRoom;
	/**
	 * Ordered list of <code>Occupancy</code>'s displayed in the check occupancy
	 */
	private List<Occupancy> mListCheckedOccupancyRoom = new ArrayList<Occupancy>();

	/**
	 * Constructor with reference to the context.
	 * 
	 * We need the context to be able to instantiate the SharedPreferences
	 * object in order to use persistent storage.
	 * 
	 * @param context
	 *            is the Application Context.
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
	 * 
	 * @return Set of FRRoom
	 */
	public Set<FRRoom> getFreeRoomResults() {
		return mFreeRoomResult;
	}

	/**
	 * Setter for the results of a freeroom request
	 * 
	 * @param results
	 *            the result for the specific freeroom search
	 */
	public void setFreeRoomResults(Set<FRRoom> results) {
		mFreeRoomResult = results;
		Log.v("Freeroom", "result set via the model");
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
	public void selectFRRoom(FRRoom choosen_one) {
		mSelectedFRRoom = choosen_one;
	}

	/**
	 * Sets the suggestions to the autocomplete and notifies the listeners.
	 * 
	 * @param listFRRoom
	 */
	public void setAutoCompleteResults(List<FRRoom> listFRRoom) {
		mAutoCompleteSuggestions = listFRRoom;
		mListeners.autoCompletedUpdated();
	}

	/**
	 * Gets the suggestions for the autocomplete
	 */
	public List<FRRoom> getAutocompleteSuggestions() {
		return mAutoCompleteSuggestions;
	}

	private List<ActualOccupation> a = new ArrayList<ActualOccupation>();

	/**
	 * Sets the occupancy result for all the rooms and notifies the listeners.
	 * 
	 * @param list
	 */
	public void setOccupancyResults(List<Occupancy> list) {
		Log.v("fr.model-set", list.size() + "/"
				+ list.get(0).getOccupancySize());
		a = new ArrayList<ActualOccupation>(list.get(0).getOccupancy());
		mListCheckedOccupancyRoom = new ArrayList<Occupancy>(list);
		Log.v("fr.model-set", "listeners should be called");
		// TODO: it seems NOT working from time to time! WHY???
		// TOFIX : dont call ALL the needed listeners !!! WHY?!?
		mListeners.occupancyResultUpdated();
	}

	/**
	 * Gets the list of room checked against occupancy.
	 * 
	 * @return
	 */
	public List<Occupancy> getListCheckedOccupancyRoom() {
		Log.v("fr.model-get", mListCheckedOccupancyRoom.size() + "/"
				+ mListCheckedOccupancyRoom.get(0).getOccupancySize());
		return mListCheckedOccupancyRoom;
	}

	public List<ActualOccupation> getListActualOccupationForONERoom() {
		Log.v("fr.model-getONE",
				mListCheckedOccupancyRoom.size() + "/"
						+ mListCheckedOccupancyRoom.get(0).getOccupancySize()
						+ "/" + a.size());
		return a;
	}
}
