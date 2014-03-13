package org.pocketcampus.plugin.freeroom.android;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.pocketcampus.android.platform.sdk.core.PluginController;
import org.pocketcampus.android.platform.sdk.tracker.Tracker;
import org.pocketcampus.android.platform.sdk.ui.layout.StandardTitledLayout;
import org.pocketcampus.plugin.freeroom.R;
import org.pocketcampus.plugin.freeroom.android.iface.IFreeRoomView;
import org.pocketcampus.plugin.freeroom.shared.ActualOccupation;
import org.pocketcampus.plugin.freeroom.shared.Occupancy;

import android.os.Bundle;
import android.util.Log;
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
		Log.v("fr.checkresult", "listerner called - start updating");
		List<Occupancy> list = mModel.getListCheckedOccupancyRoom();
		List<ActualOccupation> listA = new ArrayList<ActualOccupation>();
		
		Occupancy firstRoom = null;
		if (!list.isEmpty()) {
			firstRoom = list.get(0);
			Log.v("fr.check-updating", firstRoom.getOccupancySize() + "");
			listA.addAll(firstRoom.getOccupancy());
		}
		
		if (listA.isEmpty()) {
			System.out.println("empty! getting from way 2");
			listA = new ArrayList<ActualOccupation>(mModel.getListActualOccupationForONERoom());
		}

		// TODO: only support one room! treat others room.. do expandable view
		// for each room
		ArrayList<String> listS = new ArrayList<String>();

		SimpleDateFormat day_month = new SimpleDateFormat("MMM dd");
		SimpleDateFormat hour_min = new SimpleDateFormat("HH:mm");
		
		//TODO: put that in an appropriate title, not in the list!!!
		String review = "No results to display, sorry!";
		if (firstRoom != null) {
			review = "Results for room " + firstRoom.getRoom();
			if (!listA.isEmpty()) {
				Date s = new Date(listA.get(0).getPeriod().getTimeStampStart());
				Date e = new Date(listA.get(listA.size() - 1).getPeriod().getTimeStampEnd());
				String p = "";
				p += getString(R.string.freeroom_check_occupancy_search_onthe)
						+ " ";
				p += day_month.format(s);
				p += " " + getString(R.string.freeroom_check_occupancy_search_from)
						+ " ";
				p += hour_min.format(s);
				p += " " + getString(R.string.freeroom_check_occupancy_search_to)
						+ " ";
				p += hour_min.format(e);
				review += "\n" + p;
			}
			listS.add(review);
		}
		
		for (ActualOccupation actual : listA) {
			Date d = new Date(actual.getPeriod().getTimeStampStart());
			Date e = new Date(actual.getPeriod().getTimeStampEnd());
			
			String p = "";
			p += getString(R.string.freeroom_check_occupancy_search_onthe)
					+ " ";
			p += day_month.format(d);
			p += " " + getString(R.string.freeroom_check_occupancy_search_from)
					+ " ";
			p += hour_min.format(d);
			p += " " + getString(R.string.freeroom_check_occupancy_search_to)
					+ " ";
			p += hour_min.format(e);
			
			p += " : ";
			p += actual.isAvailable() ? "FREE" : "OCCUPIED by "
					+ actual.getOccupationType();
			listS.add(p);
		}
		mAdapter = new ArrayAdapter<String>(getApplicationContext(),
				android.R.layout.simple_dropdown_item_1line,
				android.R.id.text1, listS);

		mListView.setAdapter(mAdapter);
	}
}
