package org.pocketcampus.plugin.freeroom.android;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashSet;
import java.util.List;
import java.util.Map;

import org.pocketcampus.android.platform.sdk.core.PluginController;
import org.pocketcampus.android.platform.sdk.tracker.Tracker;
import org.pocketcampus.plugin.freeroom.R;
import org.pocketcampus.plugin.freeroom.android.adapter.ExpandableListViewFavoriteAdapter;
import org.pocketcampus.plugin.freeroom.android.iface.IFreeRoomView;
import org.pocketcampus.plugin.freeroom.android.layout.FreeRoomTabLayout;
import org.pocketcampus.plugin.freeroom.shared.FRRoom;
import org.pocketcampus.plugin.freeroom.shared.FreeRoomRequest;
import org.pocketcampus.plugin.freeroom.shared.Occupancy;
import org.pocketcampus.plugin.freeroom.shared.OccupancyRequest;
import org.pocketcampus.plugin.freeroom.shared.utils.FRTimes;

import android.os.Bundle;
import android.widget.ExpandableListView;
import android.widget.LinearLayout;
import android.widget.TextView;

/**
 * HomeView is the entry of the plugin, it displays user favorites that are free
 * <p>
 * 
 * @author FreeRoom Project Team - Julien WEBER <julien.weber@epfl.ch> and
 *         Valentin MINDER <valentin.minder@epfl.ch>
 * 
 */
public class FreeRoomHomeView extends FreeRoomAbstractView implements
		IFreeRoomView {

	private FreeRoomController mController;
	private FreeRoomModel mModel;

	private FreeRoomTabLayout mLayout;
	private LinearLayout subLayout;

	private ExpandableListView mExpView;
	private ExpandableListViewFavoriteAdapter mAdapter;
	
	private TextView noRooms;

	private ArrayList<String> buildings;
	private Map<String, List<FRRoom>> rooms;

	@Override
	protected Class<? extends PluginController> getMainControllerClass() {
		return FreeRoomController.class;
	}

	@Override
	protected void onDisplay(Bundle savedInstanceState,
			PluginController controller) {
		// Tracker
		Tracker.getInstance().trackPageView("freeroom");

		// Get and cast the controller and model
		mController = (FreeRoomController) controller;
		mModel = (FreeRoomModel) controller.getModel();

		// Setup the layout
		mLayout = new FreeRoomTabLayout(this, this);
		subLayout = new LinearLayout(this);
		subLayout.setOrientation(LinearLayout.VERTICAL);
		
		mLayout.addFillerView(subLayout);
		// The ActionBar is added automatically when you call setContentView
		setContentView(mLayout);
		// mLayout.setTitle(getString(R.string.freeroom_title_main_title));
		mLayout.hideTitle();

		initializeView();
	}

	/**
	 * This is called when the Activity is resumed.
	 * 
	 * If the user presses back on the Authentication window, This Activity is
	 * resumed but we do not have the freeroomCookie. In this case we close the
	 * Activity.
	 */
	@Override
	protected void onResume() {
		super.onResume();
		
		if (subLayout != null) {
			clearData();
			subLayout.removeAllViews();
			initializeView();
		}
		
		/*
		 * if(mModel != null && mModel.getFreeRoomCookie() == null) { // Resumed
		 * and lot logged in? go back finish(); }
		 */
	}

	public void initializeView() {
		// create the UI elements
		mExpView = new ExpandableListView(this);
		subLayout.addView(mExpView);

		// create the request for the server
		List<String> uidsFavorites = new ArrayList<String>(
				mModel.getAllRoomMapFavorites().keySet());
		Calendar calendar = Calendar.getInstance();
		FreeRoomRequest req = FRTimes.convert(
				calendar.get(Calendar.DAY_OF_WEEK),
				calendar.get(Calendar.HOUR_OF_DAY),
				calendar.get(Calendar.HOUR_OF_DAY) + 1);
		OccupancyRequest mOccRequest = new OccupancyRequest(uidsFavorites, FRTimes.getNextValidPeriod());

		// and send the request
		mController.prepareCheckOccupancy(mOccRequest);
		mController.checkOccupancy(this);
	}

	@Override
	public void freeRoomResultsUpdated() {
		// we do nothing here
	}

	@Override
	public void autoCompletedUpdated() {
		// we do nothing here
	}

	private void clearData() {
		if (rooms != null) {
			rooms.clear();
		}

		if (buildings != null) {
			buildings.clear();
		}
	}

	@Override
	public void occupancyResultUpdated() {
		List<Occupancy> results = mModel.getListCheckedOccupancyRoom();
		HashSet<FRRoom> roomsFreeFromModel = new HashSet<FRRoom>();

		// for each occupancy we need to check if the room is entirely free, if
		// not we do not display it
		for (Occupancy mOcc : results) {
			if (!mOcc.isIsAtLeastOccupiedOnce()) {
				roomsFreeFromModel.add(mOcc.getRoom());
			}
		}

		// Sort the rooms we want to display by buildings
		clearData();
		rooms = mModel.sortFRRoomsByBuildingsAndFavorites(roomsFreeFromModel,
				false);
		buildings = new ArrayList<String>(rooms.keySet());
		
		if (buildings.size() == 0) {
			//there is not free room, display a nice message to the user
			noRooms = new TextView(this);
			noRooms.setText(getString(R.string.freeroom_no_favorites_freeroom));
			subLayout.addView(noRooms);
		}

		// and finally create the adapter and display!
		mAdapter = new ExpandableListViewFavoriteAdapter(this, buildings,
				rooms, mModel);
		mExpView.setAdapter(mAdapter);

		// expanding all the groups to view immediate information!
		for (int i = 0; i < mExpView.getCount(); i++) {
			mExpView.expandGroup(i);
		}
	}
}
