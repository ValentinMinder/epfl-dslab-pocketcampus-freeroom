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
import org.pocketcampus.plugin.freeroom.android.iface.IFreeRoomView;
import org.pocketcampus.plugin.freeroom.android.utils.ExpandableSimpleListViewAdapter;
import org.pocketcampus.plugin.freeroom.shared.FRRoom;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
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
 * @author FreeFroom Project Team - Julien WEBER <julien.weber@epfl.ch> and
 *         Valentin MINDER <valentin.minder@epfl.ch>
 * 
 */
public class FreeRoomResultView extends FreeRoomAbstractView implements
		IFreeRoomView {

	private FreeRoomController mController;
	private FreeRoomModel mModel;

	private StandardTitledLayout mLayout;
	private LinearLayout subLayout;

	private Button resetButton;

	private ListView mList;
	private ExpandableListView mExpList;
	private TreeMap<String, List<String>> sortedRooms;
	private ArrayList<String> buildings;

	private ArrayList<String> mListValues;
	private ArrayAdapter<String> mAdapter;

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
		sortedRooms = new TreeMap<String, List<String>>();

		final ExpandableSimpleListViewAdapter adapter = new ExpandableSimpleListViewAdapter(
				this, buildings, sortedRooms);
		mExpList.setAdapter(adapter);

		// adding the listeners
		mExpList.setOnChildClickListener(new OnChildClickListener() {
			
			@Override
			public boolean onChildClick(ExpandableListView parent, View v,
					int groupPosition, int childPosition, long id) {
				
				String room = (String) adapter.getChild(groupPosition, childPosition);
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
		
		mExpList.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {

			@Override
			public boolean onItemLongClick(AdapterView<?> arg0, View arg1,
					int arg2, long arg3) {
				int group = ExpandableListView.getPackedPositionGroup(arg3);
				int child = ExpandableListView.getPackedPositionChild(arg3);

				if (group == -1 || child == -1) {
					return false;
				}

				String room = (String) adapter.getChild(group, child);
				String regexpMatch = "([A-Z0-9\\s]+)*";
				Pattern mPattern = Pattern.compile(regexpMatch);
				Matcher matcher = mPattern.matcher(room);

				if (matcher.find()) {
					room = matcher.group(0).replaceAll("\\s", "");
					if (mModel.isFavoriteRoom(room)) {
						mModel.removeFavoriteRoom(room);
						sortedRooms.get(buildings.get(group)).set(child, room);
					} else {
						mModel.setFavoriteRoom(room);
						sortedRooms.get(buildings.get(group)).set(child,
								room + "\u2713");
					}
					adapter.notifyDataSetChanged();
				}

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
		buildings.clear();
		sortedRooms.clear();
		// keep a structure organized as building -> list of rooms in the
		// building
		for (FRRoom frRoom : res) {
			String roomDisplay = frRoom.getBuilding() + frRoom.getNumber();

			List<String> roomsNumbers = sortedRooms.get(frRoom.getBuilding());
			if (roomsNumbers == null) {
				buildings.add(frRoom.getBuilding());
				roomsNumbers = new ArrayList<String>();
				sortedRooms.put(frRoom.getBuilding(), roomsNumbers);
			}

			if (mModel.isFavoriteRoom(roomDisplay)) {
				// indicate a favorite room
				roomsNumbers.add(roomDisplay + " \u2713");
			} else {
				roomsNumbers.add(roomDisplay);
			}
			//

			// mListValues.add(roomDisplay);
		}

		if (res.isEmpty()) {
			Toast.makeText(getApplicationContext(),
					getString(R.string.freeroom_no_room_available),
					Toast.LENGTH_LONG).show();
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
		Log.v("fr-freeroom-result", "listener to occupancyResultUpdated called");
	}

}
