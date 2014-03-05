package org.pocketcampus.plugin.freeroom.android;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.pocketcampus.android.platform.sdk.core.PluginController;
import org.pocketcampus.android.platform.sdk.tracker.Tracker;
import org.pocketcampus.android.platform.sdk.ui.layout.StandardTitledLayout;
import org.pocketcampus.plugin.freeroom.R;
import org.pocketcampus.plugin.freeroom.android.iface.IFreeRoomView;
import org.pocketcampus.plugin.freeroom.android.utils.Converter;
import org.pocketcampus.plugin.freeroom.android.utils.ExpandableSimpleListViewAdapter;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ExpandableListView;
import android.widget.ExpandableListView.OnChildClickListener;
import android.widget.LinearLayout;
import android.widget.Toast;

public class FreeRoomSearchView extends FreeRoomAbstractView implements IFreeRoomView {

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
		mLayout.hideTitle();

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
					mController.searchFreeRoom(view, Converter.convert(intday, startHour, endHour));
					
					//TODO action
					Intent i = new Intent(FreeRoomSearchView.this, FreeRoomResultView.class);
					FreeRoomSearchView.this.startActivity(i);
				}
			}
		});
		
		
		//Creating and initializing the ExpandableListView 
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
		for (int i = 9; i <= 19; ++i) {
			endHourList.add(i + ":00");
		}

		listData.put(listHeader.get(0), daysList);
		listData.put(listHeader.get(1), startHourList);
		listData.put(listHeader.get(2), endHourList);

		ExpandableSimpleListViewAdapter listAdapter = new ExpandableSimpleListViewAdapter(this,
				listHeader, listData);

		searchParams.setAdapter(listAdapter);
		searchParams.setOnChildClickListener(new OnChildClickListener() {
			
			@Override
			public boolean onChildClick(ExpandableListView parent, View v,
					int groupPosition, int childPosition, long id) {
				//TODO this is the first version, need to highlight what has been clicked
				//TODO adjust values of the list in real time (i.e if user select 9am as start, don't show 9am for endHour)
				if (groupPosition == 0) {
					intday = childPosition;
				} else if (groupPosition == 1) {
					startHour = childPosition + 8;
				} else {
					endHour = childPosition + 9;
				}
				
				if (auditSearchButton() == 0) {
					Toast.makeText(
							getApplicationContext(),
							"You selected day " + intday + " from " + startHour + " to " + endHour,
							Toast.LENGTH_SHORT).show();
					searchButton.setEnabled(true);
					return true;
				} else {
					searchButton.setEnabled(false);
				}
				
				return false;
			}
		});
		
		//Finally adding the different component to the activity
		subLayout.addView(searchParams);
		subLayout.addView(searchButton);
		
	}
	
	private int auditSearchButton() {
		//TODO adapt when the code will be more advanced.
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


}
