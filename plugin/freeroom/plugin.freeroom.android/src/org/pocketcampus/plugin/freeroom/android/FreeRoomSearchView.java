package org.pocketcampus.plugin.freeroom.android;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;

import org.pocketcampus.android.platform.sdk.core.PluginController;
import org.pocketcampus.android.platform.sdk.tracker.Tracker;
import org.pocketcampus.android.platform.sdk.ui.layout.StandardTitledLayout;
import org.pocketcampus.plugin.freeroom.R;
import org.pocketcampus.plugin.freeroom.android.iface.IFreeRoomView;
import org.pocketcampus.plugin.freeroom.android.utils.ExpandableSimpleListViewAdapter;
import org.pocketcampus.plugin.freeroom.shared.FRDay;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ExpandableListView;
import android.widget.ExpandableListView.OnChildClickListener;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

public class FreeRoomSearchView extends FreeRoomAbstractView implements
		IFreeRoomView {

	private FreeRoomController mController;
	private FreeRoomModel mModel;

	private StandardTitledLayout mLayout;
	private LinearLayout subLayout;

	private ExpandableListView searchParams;
	private Button searchButton;

	private int startHour = -1;
	private int endHour = -1;
	private int intday = -1;
	private boolean advancedSearch = false;

	// values for the ExpandableListView
	private ArrayList<String> listHeader = new ArrayList<String>();
	private HashMap<String, List<String>> listData = new HashMap<String, List<String>>();

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
		subLayout = new LinearLayout(this);
		subLayout.setOrientation(LinearLayout.VERTICAL);

		mLayout.addFillerView(subLayout);

		// The ActionBar is added automatically when you call setContentView
		setContentView(mLayout);
		mLayout.setTitle(getString(R.string.freeroom_title_FRsearch));

		initializeSearchView();

	}

	private void initializeSearchView() {
		// Creating and initializing button
		searchButton = new Button(this);
		searchButton.setEnabled(false);
		searchButton.setText(R.string.freeroom_searchbutton);
		final IFreeRoomView view = this;
		searchButton.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				if (auditSearchButton() == 0) {
					mController
							.searchFreeRoom(
									view,
									org.pocketcampus.plugin.freeroom.android.utils.Converter
											.convert(intday, startHour, endHour));
					Intent i = new Intent(FreeRoomSearchView.this,
							FreeRoomResultView.class);
					FreeRoomSearchView.this.startActivity(i);
				}
			}
		});

		// Creating and initializing the ExpandableListView
		searchParams = new ExpandableListView(this);

		listHeader.add(getString(R.string.freeroom_selectday));
		listHeader.add(getString(R.string.freeroom_selectstartHour));
		listHeader.add(getString(R.string.freeroom_selectendHour));

		List<String> daysList = new ArrayList<String>();
		// TODO: clean this list, get another way of doing that...
		daysList.add("Monday");
		daysList.add("Tuesday");
		daysList.add("Wednesday");
		daysList.add("Thursday");
		daysList.add("Friday");

		final List<String> startHourList = new ArrayList<String>();
		for (int i = 8; i <= 18; ++i) {
			startHourList.add(i + ":00");
		}

		// TODO display only possible end hour (i.e if user selects 9 for start
		// hour, display from 10)
		final List<String> endHourList = new ArrayList<String>();
		for (int i = 9; i <= 19; ++i) {
			endHourList.add(i + ":00");
		}

		listData.put(listHeader.get(0), daysList);
		listData.put(listHeader.get(1), startHourList);
		listData.put(listHeader.get(2), endHourList);

		final ExpandableSimpleListViewAdapter listAdapter = new ExpandableSimpleListViewAdapter(
				this, listHeader, listData);

		searchParams.setAdapter(listAdapter);
		searchParams.expandGroup(0);
		searchParams.setOnChildClickListener(new OnChildClickListener() {

			@Override
			public boolean onChildClick(ExpandableListView parent, View v,
					int groupPosition, int childPosition, long id) {
				// TODO this is the first version, need to highlight what has
				// been clicked
				// TODO adjust values of the list in real time (i.e if user
				// select 9am as start, don't show 9am for endHour)
				int tmpStartHour = childPosition + 8;
				int tmpEndHour;
				if (startHour != -1) {
					tmpEndHour = startHour + childPosition + 1;
				} else {
					tmpEndHour = childPosition + 9;
				}
				
				if (groupPosition == 0) {
					intday = (((childPosition + 1) % 7) + 1); // monday = 0 in
																// UI//monday =2
																// in calendar
					listAdapter.updateHeader(groupPosition,
							getString(R.string.freeroom_selectday)
									+ " ("
									+ FRDay.findByValue(childPosition)
											.toString() + ")");
					searchParams.collapseGroup(0);
					searchParams.expandGroup(1);
				} else if (groupPosition == 1
						&& (endHour == -1 || endHour > tmpStartHour)) {
					startHour = tmpStartHour;
					listAdapter.updateHeader(groupPosition,
							getString(R.string.freeroom_selectstartHour) + " ("
									+ startHour + ":00)");
					searchParams.collapseGroup(1);
					if (endHour == -1) {
						searchParams.expandGroup(2);
					}

					// adjusting values of the end hours
					endHourList.clear();
					for (int i = startHour + 1; i <= 19; ++i) {
						endHourList.add(i + ":00");
					}
				} else if (groupPosition == 2
						&& (startHour == -1 || tmpEndHour > startHour)) {
					endHour = tmpEndHour;
					listAdapter.updateHeader(groupPosition,
							getString(R.string.freeroom_selectendHour) + " ("
									+ endHour + ":00)");
					searchParams.collapseGroup(2);

					// adjusting values of the start hours
					startHourList.clear();
					for (int i = 8; i < endHour; ++i) {
						startHourList.add(i + ":00");
					}
				}

				listAdapter.notifyDataSetChanged();

				if (auditSearchButton() == 0) {
					Toast.makeText(
							getApplicationContext(),
							"You selected day " + intday + " from " + startHour
									+ " to " + endHour, Toast.LENGTH_SHORT)
							.show();
					searchButton.setEnabled(true);
					return true;
				} else {
					searchButton.setEnabled(false);
				}

				return false;
			}
		});

		// Finally adding the different component to the activity
		subLayout.addView(searchParams);
		subLayout.addView(searchButton);

	}

	private int auditSearchButton() {
		// TODO adapt when the code will be more advanced.
		int error = 0;
		if (startHour == -1 || endHour == -1 || intday == -1) {
			error++;
		}

		if (startHour >= endHour) {
			error++;
		}
		return error;
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
		// we do nothing here
	}

}
