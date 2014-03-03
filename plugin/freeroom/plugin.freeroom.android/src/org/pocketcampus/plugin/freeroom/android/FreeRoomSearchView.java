package org.pocketcampus.plugin.freeroom.android;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.pocketcampus.android.platform.sdk.core.PluginController;
import org.pocketcampus.android.platform.sdk.core.PluginView;
import org.pocketcampus.android.platform.sdk.tracker.Tracker;
import org.pocketcampus.android.platform.sdk.ui.layout.StandardTitledLayout;
import org.pocketcampus.plugin.freeroom.R;
import org.pocketcampus.plugin.freeroom.android.iface.IFreeRoomView;
import org.pocketcampus.plugin.freeroom.android.utils.ExpandableSimpleListViewAdapter;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.widget.BaseExpandableListAdapter;
import android.widget.ExpandableListView;
import android.widget.TextView;
import android.widget.Toast;

public class FreeRoomSearchView extends PluginView implements IFreeRoomView {

	private FreeRoomController mController;
	private FreeRoomModel mModel;

	private StandardTitledLayout mLayout;

	private ExpandableListView searchParams;
	private boolean advancedSearch = false;

	@Override
	protected Class<? extends PluginController> getMainControllerClass() {
		return FreeRoomController.class;
	}

	@Override
	protected void onDisplay(Bundle savedInstanceState,
			PluginController controller) {
		// Tracker
		Tracker.getInstance().trackPageView("freeroom/search");

		// Get and cast the controller and model
		mController = (FreeRoomController) controller;
		mModel = (FreeRoomModel) controller.getModel();

		// Setup the layout
		mLayout = new StandardTitledLayout(this);

		// The ActionBar is added automatically when you call setContentView
		setContentView(mLayout);
		mLayout.hideTitle();

		initializeSearchView();

	}

	@Override
	public void networkErrorHappened() {
		Toast.makeText(
				getApplicationContext(),
				getResources().getString(
						R.string.freeroom_connection_error_happened),
				Toast.LENGTH_SHORT).show();
	}

	private void initializeSearchView() {
		searchParams = new ExpandableListView(this);

		ArrayList<String> listHeader = new ArrayList<String>();
		HashMap<String, List<String>> listData = new HashMap<String, List<String>>();

		listHeader.add("Day");
		listHeader.add("Start");
		listHeader.add("End");

		List<String> daysList = new ArrayList<String>();
		daysList.add("Monday");
		daysList.add("Tuesday");
		daysList.add("Wednesday");
		daysList.add("Thursday");
		daysList.add("Friday");

		List<String> startHourList = new ArrayList<String>();
		for (int i = 8; i <= 18; ++i) {
			startHourList.add(i + ":00");
		}

		// TODO display only possible end hour (i.e if user selects 9 for start
		// hour, display from 10)
		List<String> endHourList = new ArrayList<String>();
		for (int i = 8; i <= 19; ++i) {
			endHourList.add(i + ":00");
		}

		listData.put(listHeader.get(0), daysList);
		listData.put(listHeader.get(1), startHourList);
		listData.put(listHeader.get(2), endHourList);

		ExpandableSimpleListViewAdapter listAdapter = new ExpandableSimpleListViewAdapter(this,
				listHeader, listData);

		searchParams.setAdapter(listAdapter);
		mLayout.addView(searchParams);

	}


}
