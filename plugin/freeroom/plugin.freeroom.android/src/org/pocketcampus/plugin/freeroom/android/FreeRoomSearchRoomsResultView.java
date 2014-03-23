package org.pocketcampus.plugin.freeroom.android;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.TreeMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.pocketcampus.android.platform.sdk.core.PluginController;
import org.pocketcampus.android.platform.sdk.tracker.Tracker;
import org.pocketcampus.android.platform.sdk.ui.layout.StandardTitledLayout;
import org.pocketcampus.plugin.freeroom.R;
import org.pocketcampus.plugin.freeroom.android.adapter.ExpandableListViewFavoriteAdapter;
import org.pocketcampus.plugin.freeroom.android.iface.IFreeRoomView;
import org.pocketcampus.plugin.freeroom.shared.FRRoom;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ExpandableListView;
import android.widget.ExpandableListView.OnChildClickListener;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.Toast;

/**
 * View displaying the Results of the FreeRoom feature.
 * <p>
 * 
 * @author FreeRoom Project Team - Julien WEBER <julien.weber@epfl.ch> and
 *         Valentin MINDER <valentin.minder@epfl.ch>
 * 
 */
public class FreeRoomSearchRoomsResultView extends FreeRoomAbstractView
		implements IFreeRoomView {

	private FreeRoomController mController;
	private FreeRoomModel mModel;

	private StandardTitledLayout mLayout;
	private LinearLayout subLayout;

	private Button resetButton;

	private ListView mList;
	private ExpandableListView mExpList;
	private TreeMap<String, List<FRRoom>> sortedRooms;
	private ArrayList<String> buildings;

	@Override
	protected Class<? extends PluginController> getMainControllerClass() {
		return FreeRoomController.class;
	}

	@Override
	protected void onDisplay(Bundle savedInstanceState,
			PluginController controller) {
		// Tracker
		Tracker.getInstance().trackPageView("freeroom/search/viewresult");

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
		mLayout.setTitle(getString(R.string.freeroom_title_FRresult));

		initializeResultView();

		// launch the actual search AFTER launching completely the UI
		mController.searchFreeRoom(this);
	}

	private void initializeResultView() {
		// resetButton = new Button(this);
		// resetButton.setEnabled(false);
		// resetButton.setText(R.string.freeroom_resetbutton);
		// resetButton.setOnClickListener(new OnClickListener() {
		//
		// @Override
		// public void onClick(View v) {
		// System.out.println("reset!");
		// // TODO action reset/ return
		// }
		// });
		// subLayout.addView(resetButton);

		mExpList = new ExpandableListView(this);
		buildings = new ArrayList<String>();
		sortedRooms = new TreeMap<String, List<FRRoom>>();

		final ExpandableListViewFavoriteAdapter adapter = new ExpandableListViewFavoriteAdapter(
				this, buildings, sortedRooms, mModel);
		mExpList.setAdapter(adapter);

		// adding the listeners
		mExpList.setOnChildClickListener(new OnChildClickListener() {

			@Override
			public boolean onChildClick(ExpandableListView parent, View v,
					int groupPosition, int childPosition, long id) {

				String room = (String) adapter.getChild(groupPosition,
						childPosition);
				String regexpMatch = "([A-Z0-9\\s]+)*";
				Pattern mPattern = Pattern.compile(regexpMatch);
				Matcher matcher = mPattern.matcher(room);

				if (matcher.matches()) {
					room = matcher.group(0).replaceAll("\\s", "");
				}

				System.out.println("selected " + room);

				Uri mUri = Uri
						.parse("pocketcampus://map.plugin.pocketcampus.org/search");
				Uri.Builder mbuild = mUri.buildUpon().appendQueryParameter("q",
						room);
				Intent i = new Intent(Intent.ACTION_VIEW, mbuild.build());
				startActivity(i);

				return true;
			}
		});

		subLayout.addView(mExpList);
	}

	@Override
	public void freeRoomResultsUpdated() {
		// mAdapter.clear();
		// mListValues.clear();
		// mAdapter.notifyDataSetChanged();
		Set<FRRoom> res = mModel.getFreeRoomResults();
		TreeMap<String, List<FRRoom>> map = mModel.getFreeRoomResultsFilteredByBuildings();
		List<String> listBuildings = mModel.getFreeRoomResultsBuildings();

		buildings.clear();
		buildings.addAll(listBuildings);

		sortedRooms.clear();
		sortedRooms.putAll(map);

		if (res.isEmpty()) {
			Toast.makeText(getApplicationContext(),
					getString(R.string.freeroom_no_room_available),
					Toast.LENGTH_LONG).show();
		} else {
			mExpList.expandGroup(0);
		}

		Log.v(this.getClass().toString(), "data_updated in FreeRoomResultView");
		// mAdapter.notifyDataSetChanged();
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
