package org.pocketcampus.plugin.freeroom.android;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import org.pocketcampus.android.platform.sdk.core.PluginController;
import org.pocketcampus.android.platform.sdk.tracker.Tracker;
import org.pocketcampus.android.platform.sdk.ui.layout.StandardTitledLayout;
import org.pocketcampus.plugin.freeroom.R;
import org.pocketcampus.plugin.freeroom.android.iface.IFreeRoomView;
import org.pocketcampus.plugin.freeroom.shared.ActualOccupation;
import org.pocketcampus.plugin.freeroom.shared.Occupancy;

import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.ListView;

public class FRCheckOccupancyResultView extends FreeRoomAbstractView implements
		IFreeRoomView {

	private FreeRoomController mController;
	private FreeRoomModel mModel;

	private StandardTitledLayout mLayout;

	private ListView mListView;
	private ArrayAdapter<String> mAdapter;

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

		initializeCheckOccupancyResultView();
	}

	private void initializeCheckOccupancyResultView() {
		/*
		 * TODO maybe have a greater idea how to display the result!
		 */
		mListView = new ListView(this);
		mLayout.addFillerView(mListView);
	}

	@Override
	public void freeRoomResultsUpdated() {
		// we do nothing here
	}

	@Override
	public void autoCompletedUpdated() {
		// we do nothing here
	}

	@Override
	public void occupancyResultUpdated() {
		List<Occupancy> list = mModel.getListCheckedOccupancyRoom();
		List<ActualOccupation> listA = new ArrayList<ActualOccupation>();
		if (!list.isEmpty()) {
			Occupancy firstRoom = list.get(0);
			listA = firstRoom.getOccupancy();
		}

		// TODO: only support one room! treat others room.. do expandable view
		// for each room
		ArrayList<String> listS = new ArrayList<String>();
		mAdapter = new ArrayAdapter<String>(getApplicationContext(),
				android.R.layout.simple_dropdown_item_1line,
				android.R.id.text1, listS);

		mListView.setAdapter(mAdapter);
		Calendar cal = Calendar.getInstance();
		for (ActualOccupation actual : listA) {
			String p = "";
			cal.setTimeInMillis(actual.getPeriod().getTimeStampStart());
			p += cal.get(Calendar.DAY_OF_MONTH);
			p += "/" + cal.get(Calendar.MONTH);
			p += " " + getString(R.string.freeroom_check_occupancy_search_from)
					+ " ";
			p += cal.get(Calendar.HOUR_OF_DAY);
			p += ":";
			p += cal.get(Calendar.MINUTE);
			p += " " + getString(R.string.freeroom_check_occupancy_search_to)
					+ " ";
			cal.setTimeInMillis(actual.getPeriod().getTimeStampEnd());
			p += cal.get(Calendar.HOUR_OF_DAY);
			p += ":";
			p += cal.get(Calendar.MINUTE);
			p += " : ";
			p += actual.isAvailable() ? "FREE" : "OCCUPIED by "
					+ actual.getOccupationType();
			listS.add(p);
		}
		mAdapter.notifyDataSetChanged();
	}
}
