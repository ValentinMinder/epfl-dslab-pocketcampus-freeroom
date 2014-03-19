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
import org.pocketcampus.plugin.freeroom.shared.FRRoom;
import org.pocketcampus.plugin.freeroom.shared.Occupancy;

import android.graphics.Color;
import android.os.Bundle;
import android.view.ContextMenu;
import android.view.ContextMenu.ContextMenuInfo;
import android.view.View;
import android.view.View.OnCreateContextMenuListener;
import android.widget.ExpandableListView;
import android.widget.ExpandableListView.OnChildClickListener;
import android.widget.ExpandableListView.OnGroupClickListener;
import android.widget.ExpandableListView.OnGroupExpandListener;
import android.widget.LinearLayout;
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
		mTitle.setBackgroundColor(Color.LTGRAY);
		String review = getString(R.string.freeroom_please_wait);
		mTitle.setText(review);

		subLayout.addView(mTitle);

		mExpList = new ExpandableListView(this);
		mFRRoomList = new ArrayList<String>();
		mActualOccupancyTreeMap = new TreeMap<String, List<String>>();

		mAdapter = new ExpandableSimpleListViewAdapter(this, mFRRoomList,
				mActualOccupancyTreeMap);
		mExpList.setAdapter(mAdapter);
		mExpList.setOnCreateContextMenuListener(new OnCreateContextMenuListener() {
			
			@Override
			public void onCreateContextMenu(ContextMenu menu, View v,
					ContextMenuInfo menuInfo) {
				mExpList.getChildAt(0).setBackgroundColor(Color.YELLOW);
				
			}
		});
		mExpList.setOnGroupExpandListener(new OnGroupExpandListener() {
			
			@Override
			public void onGroupExpand(int groupPosition) {
				View v = mExpList.getChildAt(groupPosition);
				if (v != null) {
					v.setBackgroundColor(Color.CYAN);
				}
				
			}
		});
		mExpList.setOnGroupClickListener(new OnGroupClickListener() {
			
			@Override
			public boolean onGroupClick(ExpandableListView parent, View v,
					int groupPosition, long id) {
				v.setBackgroundColor(Color.DKGRAY);
				return false;
			}
		});
		mExpList.setOnChildClickListener(new OnChildClickListener() {

			@Override
			public boolean onChildClick(ExpandableListView parent, View v,
					int groupPosition, int childPosition, long id) {

				v.setBackgroundColor(Color.BLUE);

				return false;
			}
		});

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
		SimpleDateFormat day_month = new SimpleDateFormat("EEEE MMMM dd");
		SimpleDateFormat hour_min = new SimpleDateFormat("HH:mm");

		mExpList.postInvalidate();
		mFRRoomList.clear();
		mActualOccupancyTreeMap.clear();
		mTitle.setBackgroundColor(Color.RED);

		List<Occupancy> list = mModel.getListCheckedOccupancyRoom();

		String mFullPeriodAsString = "";
		String mListRoom = getString(R.string.freeroom_check_occupancy_result_checking_rooms)
				+ " ";
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

				mFullPeriodAsString += getString(R.string.freeroom_check_occupancy_result_onthe)
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

				mActualOccupationAsString += " :";
				if (mActualOccupation.isAvailable()) {
					mActualOccupationAsString += " "
							+ getString(R.string.freeroom_check_occupancy_result_free);
					mActualOccupationAsString += " "
							+ mActualOccupation.getOccupationType().toString();
				} else {
					mActualOccupationAsString += " "
							+ getString(R.string.freeroom_check_occupancy_result_occupied)
							+ " "
							+ getString(R.string.freeroom_check_occupancy_result_by)
							+ " " + mActualOccupation.getOccupationType();
				}

				mListActualOccupationAsString.add(mActualOccupationAsString);
			}
			mActualOccupancyTreeMap.put(mRoomAsString,
					mListActualOccupationAsString);
		}

		String review = getString(R.string.freeroom_no_results_sorry);
		if (atLeastOneRoom) {
			review = mListRoom.substring(0, mListRoom.length() - 2);
			// -2:avoiding the last ", "
			review += "\n";
			review += mFullPeriodAsString;
			mTitle.setBackgroundColor(Color.LTGRAY);
		}
		mTitle.setText(review);

		// if there is only one room, we expand the first group
		if (mFRRoomList.size() == 1) {
			mExpList.expandGroup(0);
			mExpList.getExpandableListAdapter().getGroupView(0, false, null, null);
			View v = mExpList.getExpandableListAdapter().getChildView(0, 0, false, null, null);
			
			if (v != null) {
				System.out.println("jajaj");
				// here it's not null but changing the color has no effect
				v.setBackgroundColor(Color.GREEN);
				v.refreshDrawableState();
				v.requestLayout();
				//v.getParent().requestLayout();
			}
		}
		// this 
		// mExpList.setBackgroundColor(Color.BLUE);
	}
	
	public void changeThatColor () {
		// we acnnot access elements while the list is not constructed sucessfully
		mExpList.getChildAt(0).setBackgroundColor(Color.RED);
	}
}
