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
		for (int i = 8; i <= 18; ++i) {
			endHourList.add(i + ":00");
		}

		listData.put(listHeader.get(0), daysList);
		listData.put(listHeader.get(1), startHourList);
		listData.put(listHeader.get(2), endHourList);

		ExpandableListAdapter listAdapter = new ExpandableListAdapter(this,
				listHeader, listData);

		searchParams.setAdapter(listAdapter);
		mLayout.addView(searchParams);

	}

	public class ExpandableListAdapter extends BaseExpandableListAdapter {

		private Context context;
		private List<String> headers;
		private Map<String, List<String>> data;

		public ExpandableListAdapter(Context c, List<String> header,
				Map<String, List<String>> data) {
			this.context = c;
			this.headers = header;
			this.data = data;
		}

		@Override
		public Object getChild(int groupPosition, int childPosition) {
			if (groupPosition >= headers.size()) {
				return null;
			}
			List<String> groupList = data.get(headers.get(groupPosition));

			if (childPosition >= groupList.size()) {
				return null;
			}
			return data.get(headers.get(groupPosition)).get(childPosition);
		}

		@Override
		public long getChildId(int groupPosition, int childPosition) {
			return childPosition;
		}

		@Override
		public View getChildView(int groupPosition, int childPosition,
				boolean isLastChild, View convertView, ViewGroup parent) {

			if (groupPosition >= headers.size()) {
				return null;
			}

			if (convertView == null) {
				convertView = new TextView(context);
			}
			String text = (String) this.getChild(groupPosition, childPosition);

			((TextView) convertView).setText(text);
			return convertView;
		}

		@Override
		public int getChildrenCount(int groupPosition) {
			if (groupPosition >= data.size()) {
				return 0;
			}

			return data.get(headers.get(groupPosition)).size();
		}

		@Override
		public Object getGroup(int groupPosition) {
			if (groupPosition >= data.size()) {
				return null;
			}
			return data.get(headers.get(groupPosition));
		}

		@Override
		public int getGroupCount() {
			return data.size();
		}

		@Override
		public long getGroupId(int groupPosition) {
			return groupPosition;
		}

		@Override
		public View getGroupView(int groupPosition, boolean isExpanded,
				View convertView, ViewGroup parent) {
			if (groupPosition >= headers.size()) {
				return null;
			}

			if (convertView == null) {
				convertView = new TextView(context);
			}
			String text = (String) headers.get(groupPosition);
			((TextView) convertView).setText(text);

			return convertView;
		}

		@Override
		public boolean hasStableIds() {
			return false;
		}

		@Override
		public boolean isChildSelectable(int groupPosition, int childPosition) {
			return true;
		}

	}

}
