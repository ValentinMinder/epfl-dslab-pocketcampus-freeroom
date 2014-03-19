package org.pocketcampus.plugin.freeroom.android;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.pocketcampus.android.platform.sdk.core.IView;
import org.pocketcampus.android.platform.sdk.core.PluginModel;
import org.pocketcampus.plugin.freeroom.android.iface.IFreeRoomModel;
import org.pocketcampus.plugin.freeroom.android.iface.IFreeRoomView;
import org.pocketcampus.plugin.freeroom.shared.ActualOccupation;
import org.pocketcampus.plugin.freeroom.shared.FRRoom;
import org.pocketcampus.plugin.freeroom.shared.Occupancy;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Color;

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
	 * Keys to persistent storage
	 */
	private final String FAVORITES_ROOMS_KEY = "FAVORITES_ROOMS_KEY";

	public final int COLOR_CHECK_OCCUPANCY_DEFAULT = Color.WHITE;
	public final int COLOR_CHECK_OCCUPANCY_FREE = Color.GREEN;
	public final int COLOR_CHECK_OCCUPANCY_OCCUPIED = Color.RED;
	public final int COLOR_CHECK_OCCUPANCY_ATLEASTONCE = Color.YELLOW;

	/**
	 * 
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

	private Context context;

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
		this.context = context;
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

	/**
	 * Sets the occupancy result for all the rooms and notifies the listeners.
	 * 
	 * @param list
	 */
	public void setOccupancyResults(List<Occupancy> list) {
		mListCheckedOccupancyRoom = new ArrayList<Occupancy>(list);
		mListeners.occupancyResultUpdated();
	}

	/**
	 * Gets the list of room checked against occupancy.
	 * 
	 * @return
	 */
	public List<Occupancy> getListCheckedOccupancyRoom() {
		return mListCheckedOccupancyRoom;
	}

	private Occupancy getOccupancy(int mGroupPosition) {
		if (mListCheckedOccupancyRoom != null
				&& mGroupPosition < mListCheckedOccupancyRoom.size()) {
			return mListCheckedOccupancyRoom.get(mGroupPosition);
		}
		// default
		return null;
	}

	private ActualOccupation getActualOccupation(int mGroupPosition,
			int mChildPosition) {

		Occupancy mOccupancy = getOccupancy(mGroupPosition);
		if (mOccupancy != null) {
			List<ActualOccupation> mActualOccupationList = mOccupancy
					.getOccupancy();
			if (mChildPosition < mActualOccupationList.size()) {
				ActualOccupation mActualOccupation = mActualOccupationList
						.get(mChildPosition);
				return mActualOccupation;
			}
		}

		// default
		return null;
	}

	public int getColorOfCheckOccupancyRoom(int mGroupPosition,
			int mChildPosition) {
		ActualOccupation mActualOccupation = getActualOccupation(
				mGroupPosition, mChildPosition);
		if (mActualOccupation != null) {
			if (mActualOccupation.isAvailable()) {
				return COLOR_CHECK_OCCUPANCY_FREE;
			} else {
				return COLOR_CHECK_OCCUPANCY_OCCUPIED;
			}
		}
		// default
		return COLOR_CHECK_OCCUPANCY_DEFAULT;
	}

	public boolean isCheckOccupancyLineClickable(int mGroupPosition,
			int mChildPosition) {
		ActualOccupation mActualOccupation = getActualOccupation(
				mGroupPosition, mChildPosition);
		if (mActualOccupation != null) {
			if (mActualOccupation.isAvailable()) {
				return true;
			} else {
				return false;
			}
		}

		// default
		return false;
	}

	public int getColorOfCheckOccupancyRoom(int mGroupPosition) {
		Occupancy mOccupancy = getOccupancy(mGroupPosition);
		List<ActualOccupation> mListActualOccupations = mOccupancy
				.getOccupancy();
		boolean atLeastOneFree = false;
		boolean atLeastOneOccupied = false;
		// TODO: optimization!
		// Stores the information once when the results are updated
		// and avoid multiple going through the whole list!
		for (ActualOccupation mActualOccupation : mListActualOccupations) {
			boolean isAvailable = mActualOccupation.isAvailable();
			if (isAvailable) {
				atLeastOneFree = true;
			} else {
				atLeastOneOccupied = true;
			}
		}

		if (atLeastOneFree) {
			if (atLeastOneOccupied) {
				return COLOR_CHECK_OCCUPANCY_ATLEASTONCE;
			} else {
				return COLOR_CHECK_OCCUPANCY_FREE;
			}
		} else {
			if (atLeastOneOccupied) {
				return COLOR_CHECK_OCCUPANCY_OCCUPIED;
			} else {
				// default
				return COLOR_CHECK_OCCUPANCY_DEFAULT;
			}
		}

	}

	public void setFavoriteRoom(String roomname) {
		SharedPreferences preferences = context.getSharedPreferences(
				FAVORITES_ROOMS_KEY, Context.MODE_PRIVATE);
		SharedPreferences.Editor editor = preferences.edit();
		String roomCorrected = roomname.replaceAll("\\s", "");
		editor.putBoolean(roomCorrected, true);
		editor.commit();
	}

	public void removeFavoriteRoom(String roomname) {
		SharedPreferences preferences = context.getSharedPreferences(
				FAVORITES_ROOMS_KEY, Context.MODE_PRIVATE);
		SharedPreferences.Editor editor = preferences.edit();
		String roomCorrected = roomname.replaceAll("\\s", "");
		editor.remove(roomCorrected);
		editor.commit();
	}

	public boolean isFavoriteRoom(String roomname) {
		SharedPreferences preferences = context.getSharedPreferences(
				FAVORITES_ROOMS_KEY, Context.MODE_PRIVATE);
		String roomCorrected = roomname.replaceAll("\\s", "");
		return preferences.getBoolean(roomCorrected, false);
	}

	public Map<String, Boolean> getFavorites() {
		// TODO doesn't work
		SharedPreferences preferences = context.getSharedPreferences(
				FAVORITES_ROOMS_KEY, Context.MODE_PRIVATE);
		Map<String, Boolean> map = (Map<String, Boolean>) preferences.getAll();
		return map;
	}
}
