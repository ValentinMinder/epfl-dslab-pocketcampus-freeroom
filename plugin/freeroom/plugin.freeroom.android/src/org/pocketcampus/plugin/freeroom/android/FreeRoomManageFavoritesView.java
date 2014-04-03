package org.pocketcampus.plugin.freeroom.android;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.pocketcampus.android.platform.sdk.core.PluginController;
import org.pocketcampus.android.platform.sdk.tracker.Tracker;
import org.pocketcampus.android.platform.sdk.ui.layout.StandardTitledLayout;
import org.pocketcampus.plugin.freeroom.R;
import org.pocketcampus.plugin.freeroom.android.adapter.ExpandableListViewFavoriteAdapter;
import org.pocketcampus.plugin.freeroom.android.iface.IFreeRoomView;
import org.pocketcampus.plugin.freeroom.shared.FRRoom;

import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ExpandableListView;
import android.widget.LinearLayout;
import android.widget.TextView;

public class FreeRoomManageFavoritesView extends FreeRoomAbstractView implements
		IFreeRoomView {

	private FreeRoomController mController;
	private FreeRoomModel mModel;

	private StandardTitledLayout mLayout;
	private LinearLayout subLayout;

	private TextView mTitle;

	private ExpandableListView mExpList;
	private ExpandableListViewFavoriteAdapter mAdapter;

	private ArrayList<String> buildings;
	private Map<String, List<FRRoom>> rooms;

	private Button resetAllFavorites;

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
		subLayout = new LinearLayout(this);
		subLayout.setOrientation(LinearLayout.VERTICAL);
		mLayout.addFillerView(subLayout);

		// The ActionBar is added automatically when you call setContentView
		setContentView(mLayout);
//		mLayout.setTitle(getString(R.string.freeroom_title_manage_favorites));
		mLayout.hideTitle();
		initializeView();
	}

	private void resetView() {
		subLayout.removeAllViews();

		if (rooms != null) {
			rooms.clear();
		}

		if (buildings != null) {
			buildings.clear();
		}
	}

	public void initializeView() {
		resetView();
		resetAllFavorites = new Button(this);
		resetAllFavorites.setText(getString(R.string.freeroom_resetbutton));
		resetAllFavorites.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				if (mModel.removeAllRoomsFavorites()) {
					initializeView();
				}
			}
		});
		subLayout.addView(resetAllFavorites);

		mExpList = new ExpandableListView(this);

		Map<String, String> allFavorites = mModel.getAllRoomMapFavorites();
		HashSet<FRRoom> favoritesAsFRRoom = new HashSet<FRRoom>();

		for (Entry<String, String> e : allFavorites.entrySet()) {
			// Favorites beeing stored as uid -> doorCode
			favoritesAsFRRoom.add(new FRRoom(e.getValue(), e.getKey()));
		}

		rooms = mModel.sortFRRoomsByBuildingsAndFavorites(favoritesAsFRRoom,
				false);
		buildings = new ArrayList<String>(rooms.keySet());

		mAdapter = new ExpandableListViewFavoriteAdapter(this, buildings,
				rooms, mModel);
		mExpList.setAdapter(mAdapter);

		subLayout.addView(mExpList);
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
