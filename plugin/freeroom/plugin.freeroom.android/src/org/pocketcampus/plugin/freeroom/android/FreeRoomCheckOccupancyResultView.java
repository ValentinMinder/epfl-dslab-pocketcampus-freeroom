package org.pocketcampus.plugin.freeroom.android;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;

import org.pocketcampus.android.platform.sdk.core.PluginController;
import org.pocketcampus.android.platform.sdk.tracker.Tracker;
import org.pocketcampus.android.platform.sdk.ui.layout.StandardTitledLayout;
import org.pocketcampus.plugin.freeroom.R;
import org.pocketcampus.plugin.freeroom.android.adapter.ExpandableSimpleListViewAdapter;
import org.pocketcampus.plugin.freeroom.android.iface.IFreeRoomView;
import org.pocketcampus.plugin.freeroom.android.layout.FreeRoomTabLayout;
import org.pocketcampus.plugin.freeroom.shared.ActualOccupation;
import org.pocketcampus.plugin.freeroom.shared.FRRoom;
import org.pocketcampus.plugin.freeroom.shared.ImWorkingRequest;
import org.pocketcampus.plugin.freeroom.shared.Occupancy;
import org.pocketcampus.plugin.freeroom.shared.WorkingOccupancy;

import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ExpandableListView;
import android.widget.ExpandableListView.OnChildClickListener;
import android.widget.ExpandableListView.OnGroupClickListener;
import android.widget.ExpandableListView.OnGroupCollapseListener;
import android.widget.ExpandableListView.OnGroupExpandListener;
import android.widget.LinearLayout;
import android.widget.TextView;

/**
 * View displaying the Results of the CheckOccupancy feature.
 * <p>
 * 
 * @author FreeRoom Project Team - Julien WEBER <julien.weber@epfl.ch> and
 *         Valentin MINDER <valentin.minder@epfl.ch>
 * 
 */
public class FreeRoomCheckOccupancyResultView extends FreeRoomAbstractView
		implements IFreeRoomView {

	private FreeRoomController mController;
	private FreeRoomModel mModel;

	private FreeRoomTabLayout mLayout;
	private LinearLayout subLayout;

	private TextView mTitle;

	private ExpandableListView mExpList;
	private HashMap<String, List<String>> mActualOccupancyHashMap;
	private ArrayList<String> mFRRoomList;

	private HashSet<String> mFRRoomSet;

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
		mLayout = new FreeRoomTabLayout(this, this);

		// The ActionBar is added automatically when you call setContentView
		setContentView(mLayout);
		// mLayout.setTitle(getString(R.string.freeroom_title_occupancy_result));
		mLayout.hideTitle();
		initializeView();

		// launch the actual search AFTER launching completely the UI
		mController.checkOccupancy(this);
	}

	public void initializeView() {
		subLayout = new LinearLayout(this);
		subLayout.setOrientation(LinearLayout.VERTICAL);

		mTitle = new TextView(this);
		mTitle.setBackgroundColor(Color.LTGRAY);
		String review = getString(R.string.freeroom_please_wait);
		mTitle.setText(review);

		subLayout.addView(mTitle);

		mExpList = new ExpandableListView(this);
		// TODO: use LinkedHashSet instead of two structures!!!!!
		mFRRoomList = new ArrayList<String>();
		mFRRoomSet = new HashSet<String>();
		mActualOccupancyHashMap = new HashMap<String, List<String>>();

		mAdapter = new ExpandableSimpleListViewAdapter(this, mFRRoomList,
				mActualOccupancyHashMap, mModel);
		mExpList.setAdapter(mAdapter);

		mExpList.setOnGroupCollapseListener(new OnGroupCollapseListener() {

			@Override
			public void onGroupCollapse(int groupPosition) {
				// nothing to do for now

			}
		});
		mExpList.setOnGroupExpandListener(new OnGroupExpandListener() {

			@Override
			public void onGroupExpand(int groupPosition) {
				// nothing to do for now
			}
		});
		mExpList.setOnGroupClickListener(new OnGroupClickListener() {

			@Override
			public boolean onGroupClick(ExpandableListView parent, View v,
					int groupPosition, long id) {
				// nothing to do for now
				return false;
			}
		});

		final IFreeRoomView view = this;
		mExpList.setOnChildClickListener(new OnChildClickListener() {

			@Override
			public boolean onChildClick(ExpandableListView parent, View v,
					int groupPosition, int childPosition, long id) {

				// reservation??
				// indicate that i'm going to work there!

				if (groupPosition < mFRRoomList.size()) {
					String header = mFRRoomList.get(groupPosition);
					List<String> lines = mActualOccupancyHashMap.get(header);
					if (lines != null && childPosition < lines.size()) {
						List<Occupancy> list = mModel
								.getListCheckedOccupancyRoom();
						if (groupPosition < list.size()) {
							Occupancy mOccupancy = list.get(groupPosition);
							List<ActualOccupation> mActualOccupations = mOccupancy.getOccupancy();
							if (childPosition < mActualOccupations.size()) {
								ActualOccupation mActualOccupation = mActualOccupations.get(childPosition);
								WorkingOccupancy work = new WorkingOccupancy(mActualOccupation.getPeriod(), mOccupancy.getRoom());
								ImWorkingRequest request = new ImWorkingRequest(work);
								mController.prepareImWorking(request);
								mController.ImWorking(view);
							}
						}
					}
				}
				Log.v(this.getClass().toString(), "item clicked, group:"
						+ groupPosition + "/child:" + childPosition);
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
		mActualOccupancyHashMap.clear();
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
			String mRoomAsString = mFRRoom.getDoorCode();
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

				Log.v("check-res", mFullPeriodAsString);
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
					if (mActualOccupation.isSetProbableOccupation()) {
						mActualOccupationAsString += " "
								+ "(probably occupied by at least "
								+ mActualOccupation.getProbableOccupation()
								+ " people";
						if (mFRRoom.isSetCapacity()
								&& mFRRoom.getCapacity() != 0) {
							mActualOccupationAsString += ", at most "
									+ (mFRRoom.getCapacity() - mActualOccupation
											.getProbableOccupation())
									+ " places remaining )";
						} else {
							mActualOccupationAsString += ")";
						}
					}
					mActualOccupationAsString += " Click to work here.";
				} else {
					mActualOccupationAsString += " "
							+ getString(R.string.freeroom_check_occupancy_result_occupied)
							+ " "
							+ getString(R.string.freeroom_check_occupancy_result_by)
							+ " " + mActualOccupation.getOccupationType();
				}
				Log.v("check-res", mActualOccupationAsString);
				mListActualOccupationAsString.add(mActualOccupationAsString);
			}
			if (!mFRRoomSet.contains(mRoomAsString)) {
				mFRRoomSet.add(mRoomAsString);
				mFRRoomList.add(mRoomAsString);
				mActualOccupancyHashMap.put(mRoomAsString,
						mListActualOccupationAsString);
			}
		}

		String review = getString(R.string.freeroom_no_results_sorry);
		int mListRoomLength = mListRoom.length();
		if (atLeastOneRoom && mListRoomLength >= 3) {
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
		}
	}
}
