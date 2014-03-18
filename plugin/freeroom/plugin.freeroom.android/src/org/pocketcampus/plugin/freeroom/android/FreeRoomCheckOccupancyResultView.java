package org.pocketcampus.plugin.freeroom.android;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.TreeMap;

import org.pocketcampus.android.platform.sdk.core.PluginController;
import org.pocketcampus.android.platform.sdk.tracker.Tracker;
import org.pocketcampus.android.platform.sdk.ui.layout.StandardTitledLayout;
import org.pocketcampus.plugin.freeroom.R;
import org.pocketcampus.plugin.freeroom.android.iface.IFreeRoomView;
import org.pocketcampus.plugin.freeroom.android.utils.ExpandableSimpleListViewAdapter;
import org.pocketcampus.plugin.freeroom.shared.ActualOccupation;
import org.pocketcampus.plugin.freeroom.shared.FRPeriod;
import org.pocketcampus.plugin.freeroom.shared.FRRoom;
import org.pocketcampus.plugin.freeroom.shared.Occupancy;

import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.widget.ArrayAdapter;
import android.widget.ExpandableListView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

/**
 * View displaying the Results of the CheckOccupancy feature.
 * <p>
 * 
 * @author FreeFroom Project Team - Julien WEBER <julien.weber@epfl.ch> and
 *         Valentin MINDER <valentin.minder@epfl.ch>
 * 
 */
public class FreeRoomCheckOccupancyResultView extends FreeRoomAbstractView
		implements IFreeRoomView {

	private FreeRoomController mController;
	private FreeRoomModel mModel;

	private StandardTitledLayout mLayout;
	private LinearLayout subLayout;

	private TextView mTitle;

	private ExpandableListView mExpList;
	private TreeMap<String, List<String>> mActualOccupancyTreeMap;
	private ArrayList<String> mFRRoomList;

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

		initializeCheckOccupancyResultView();

		// launch the actual search AFTER launching completely the UI
		mController.checkOccupancy(this);
	}

	private void initializeCheckOccupancyResultView() {
		subLayout = new LinearLayout(this);
		subLayout.setOrientation(LinearLayout.VERTICAL);

		mTitle = new TextView(this);

		subLayout.addView(mTitle);

		mExpList = new ExpandableListView(this);
		mFRRoomList = new ArrayList<String>();
		mActualOccupancyTreeMap = new TreeMap<String, List<String>>();

		mAdapter = new ExpandableSimpleListViewAdapter(this, mFRRoomList,
				mActualOccupancyTreeMap);
		mExpList.setAdapter(mAdapter);

		subLayout.addView(mExpList);

		mLayout.addFillerView(subLayout);
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
		SimpleDateFormat day_month = new SimpleDateFormat("MMM dd");
		SimpleDateFormat hour_min = new SimpleDateFormat("HH:mm");

		mFRRoomList.clear();
		mActualOccupancyTreeMap.clear();
		Log.v("fr.checkresult", "listerner called - start updating!!");
		List<Occupancy> list = mModel.getListCheckedOccupancyRoom();

		String mFullPeriodAsString = "";
		String mListRoom = "Checking result for rooms: ";
		boolean atLeastOneRoom = false;

		for (Occupancy occupation : list) {
			atLeastOneRoom = true;
			FRRoom mFRRoom = occupation.getRoom();
			List<ActualOccupation> mListActualOccupation = occupation
					.getOccupancy();
			String mRoomAsString = mFRRoom.getBuilding() + mFRRoom.getNumber();
			mFRRoomList.add(mRoomAsString);

			mListRoom += mRoomAsString + ", ";

			ArrayList<String> mListActualOccupationAsString = new ArrayList<String>(
					mListActualOccupation.size());

			if (mFullPeriodAsString.equals("")
					&& !mListActualOccupation.isEmpty()) {
				Date startDate = new Date(mListActualOccupation.get(0)
						.getPeriod().getTimeStampStart());
				Date endDate = new Date(mListActualOccupation
						.get(mListActualOccupation.size() - 1).getPeriod()
						.getTimeStampEnd());

				mFullPeriodAsString += " "
						+ getString(R.string.freeroom_check_occupancy_result_onthe)
						+ " ";
				mFullPeriodAsString += day_month.format(startDate);
				mFullPeriodAsString += " "
						+ getString(R.string.freeroom_check_occupancy_result_from)
						+ " ";
				mFullPeriodAsString += hour_min.format(startDate);
				mFullPeriodAsString += " "
						+ getString(R.string.freeroom_check_occupancy_result_to)
						+ " ";
				mFullPeriodAsString += hour_min.format(endDate);
			}

			for (ActualOccupation mActualOccupation : mListActualOccupation) {
				String mActualOccupationAsString = "";

				Date startDate = new Date(mActualOccupation.getPeriod()
						.getTimeStampStart());
				Date endDate = new Date(mActualOccupation.getPeriod()
						.getTimeStampEnd());

				mActualOccupationAsString += hour_min.format(startDate);
				mActualOccupationAsString += " "
						+ getString(R.string.freeroom_check_occupancy_result_to)
						+ " ";
				mActualOccupationAsString += hour_min.format(endDate);

				mActualOccupationAsString += " : ";
				if (mActualOccupation.isAvailable()) {
					mActualOccupationAsString += "FREE";
				} else {
					mActualOccupationAsString += "OCCUPIED by "
							+ mActualOccupation.getOccupationType();
				}

				mListActualOccupationAsString.add(mActualOccupationAsString);
			}
			mActualOccupancyTreeMap.put(mRoomAsString,
					mListActualOccupationAsString);
		}

		String review = "No results to display, sorry!";
		if (atLeastOneRoom) {
			review = mListRoom.substring(0, mListRoom.length() - 2);
			// -2:avoiding the last ", "
			review += "\n";
			review += mFullPeriodAsString;
		}
		mTitle.setText(review);
		
		for (int i = 0 ; i < mFRRoomList.size(); i++) {
			
		}
		

		// if there is only one room, we expand the first group
		if (mFRRoomList.size() == 1) {
			mExpList.expandGroup(0);
		}
				
		try {
			Thread.sleep(1000);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
