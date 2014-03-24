package org.pocketcampus.plugin.freeroom.android;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;

import org.pocketcampus.android.platform.sdk.core.PluginController;
import org.pocketcampus.android.platform.sdk.tracker.Tracker;
import org.pocketcampus.android.platform.sdk.ui.layout.StandardTitledLayout;
import org.pocketcampus.plugin.freeroom.R;
import org.pocketcampus.plugin.freeroom.android.adapter.ExpandableSimpleListViewAdapter;
import org.pocketcampus.plugin.freeroom.android.iface.IFreeRoomView;

import android.os.Bundle;
import android.widget.ExpandableListView;
import android.widget.LinearLayout;
import android.widget.TextView;

public class FreeRoomManageFavoritesView extends FreeRoomAbstractView implements
		IFreeRoomView {

	private FreeRoomController mController;
	private FreeRoomModel mModel;

	private StandardTitledLayout mLayout;
	private LinearLayout subLayout;

	private TextView mTitle;

	private ExpandableListView mExpList;
	private ExpandableSimpleListViewAdapter mAdapter;

	@Override
	protected Class<? extends PluginController> getMainControllerClass() {
		return FreeRoomController.class;
	}

	@Override
	protected void onDisplay(Bundle savedInstanceState,
			PluginController controller) {
		// Tracker
		Tracker.getInstance().trackPageView("freeroom/checkoccupancy/result");

		// Get and cast the controller and model
		mController = (FreeRoomController) controller;
		mModel = (FreeRoomModel) controller.getModel();

		// Setup the layout
		mLayout = new StandardTitledLayout(this);

		// The ActionBar is added automatically when you call setContentView
		setContentView(mLayout);
		mLayout.setTitle(getString(R.string.freeroom_title_occupancy_result));

		// launch the actual search AFTER launching completely the UI
		mController.checkOccupancy(this);
	}

	@Override
	public void freeRoomResultsUpdated() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void autoCompletedUpdated() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void occupancyResultUpdated() {
		// TODO Auto-generated method stub
		
	}
}
