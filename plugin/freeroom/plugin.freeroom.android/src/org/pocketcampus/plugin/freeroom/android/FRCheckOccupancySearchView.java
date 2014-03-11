package org.pocketcampus.plugin.freeroom.android;

import java.util.ArrayList;
import java.util.List;

import org.pocketcampus.android.platform.sdk.core.PluginController;
import org.pocketcampus.android.platform.sdk.tracker.Tracker;
import org.pocketcampus.android.platform.sdk.ui.element.InputBarElement;
import org.pocketcampus.android.platform.sdk.ui.layout.StandardTitledLayout;
import org.pocketcampus.plugin.freeroom.R;
import org.pocketcampus.plugin.freeroom.android.iface.IFreeRoomView;
import org.pocketcampus.plugin.freeroom.shared.FRRoom;

import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.ListView;

public class FRCheckOccupancySearchView extends FreeRoomAbstractView implements
		IFreeRoomView {

	private FreeRoomController mController;
	private FreeRoomModel mModel;

	private StandardTitledLayout mLayout;

	private ArrayList<FRRoom> roomsToCheck;

	private ListView mListView;
	private List<FRRoom> mListAutoRoom;

	/** The input bar to make the search */
	private InputBarElement mInputBar;
	/** Adapter for the <code>mListView</code> */
	private ArrayAdapter<String> mAdapter;

	@Override
	protected Class<? extends PluginController> getMainControllerClass() {
		return FreeRoomController.class;
	}

	@Override
	protected void onDisplay(Bundle savedInstanceState,
			PluginController controller) {
		// Tracker
		Tracker.getInstance().trackPageView("freeroom/checkoccupancy/search");

		// Get and cast the controller and model
		mController = (FreeRoomController) controller;
		mModel = (FreeRoomModel) controller.getModel();

		// Setup the layout
		mLayout = new StandardTitledLayout(this);

		// The ActionBar is added automatically when you call setContentView
		setContentView(mLayout);
		mLayout.setTitle(getString(R.string.freeroom_title_occupancy_search));

		initializeCheckOccupancySearchView();
	}

	private void initializeCheckOccupancySearchView() {
		/*
		 * TODO : add a list of already selected rooms, an input bar for
		 * selecting more rooms, a suggestion clickable list
		 */
	}

	@Override
	public void freeRoomResultsUpdated() {
		// we do nothing here
	}

	@Override
	public void autoCompletedUpdated() {
		mListAutoRoom = mModel.getAutocompleteSuggestions();
	}

	@Override
	public void occupancyResultUpdated() {
		// we do nothing here
	}
}
