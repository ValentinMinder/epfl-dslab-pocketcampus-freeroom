package org.pocketcampus.plugin.freeroom.android;

import java.util.ArrayList;
import java.util.List;

import org.pocketcampus.android.platform.sdk.core.PluginController;
import org.pocketcampus.android.platform.sdk.tracker.Tracker;
import org.pocketcampus.android.platform.sdk.ui.element.InputBarElement;
import org.pocketcampus.android.platform.sdk.ui.element.OnKeyPressedListener;
import org.pocketcampus.android.platform.sdk.ui.layout.StandardTitledLayout;
import org.pocketcampus.android.platform.sdk.ui.list.LabeledListViewElement;
import org.pocketcampus.plugin.freeroom.R;
import org.pocketcampus.plugin.freeroom.android.iface.IFreeRoomView;
import org.pocketcampus.plugin.freeroom.shared.AutoCompleteRequest;
import org.pocketcampus.plugin.freeroom.shared.FRPeriod;
import org.pocketcampus.plugin.freeroom.shared.FRRoom;
import org.pocketcampus.plugin.freeroom.shared.FRRoomType;
import org.pocketcampus.plugin.freeroom.shared.OccupancyRequest;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.inputmethod.EditorInfo;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.ListView;

public class FRCheckOccupancySearchView extends FreeRoomAbstractView implements
		IFreeRoomView {

	private FreeRoomController mController;
	private FreeRoomModel mModel;

	private StandardTitledLayout mLayout;

	private ArrayList<FRRoom> roomsToCheck;

	private ListView mListView;
	private List<FRRoom> listFR;

	/** The input bar to make the search */
	private InputBarElement mInputBar;
	/** Adapter for the <code>mListView</code> */
	private ArrayAdapter<String> mAdapter;

	@Override
	protected Class<? extends PluginController> getMainControllerClass() {
		return FreeRoomController.class;
	}

	@Override
	protected void onDisplay(Bundle savedInstanceState,
			PluginController controller) {
		// Tracker
		Tracker.getInstance().trackPageView("freeroom/checkoccupancy/search");

		// Get and cast the controller and model
		mController = (FreeRoomController) controller;
		mModel = (FreeRoomModel) controller.getModel();

		initializeCheckOccupancySearchView();
		createSuggestionsList();

	}

	private void initializeCheckOccupancySearchView() {
		/*
		 * TODO : add a list of already selected rooms, an input bar for
		 * selecting more rooms, a suggestion clickable list
		 */
		final IFreeRoomView view = this;
		// Setup the layout
		mLayout = new StandardTitledLayout(this);

		mLayout.setTitle(getString(R.string.freeroom_title_occupancy_search));

		mInputBar = new InputBarElement(
				this,
				null,
				getString(R.string.freeroom_check_occupancy_search_inputbarhint));
		mInputBar.setImeOptions(EditorInfo.IME_ACTION_SEARCH);

		// mInputBar.setOnEditorActionListener(new OnEditorActionListener() {
		// @Override
		// public boolean onEditorAction(TextView v, int actionId, KeyEvent
		// event) {
		// if(actionId == EditorInfo.IME_ACTION_SEARCH){
		// String query = mInputBar.getInputText();
		// System.out.println("qurey " + query); //TODO
		// // search(query);
		// }
		//
		// return true;
		// }
		// });
		mInputBar.setOnButtonClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				String query = mInputBar.getInputText();
				AutoCompleteRequest request = new AutoCompleteRequest(query);
				mController.autoCompleteBuilding(view, request);
			}
		});

		mAdapter = new ArrayAdapter<String>(getApplicationContext(),
				R.layout.sdk_list_entry, R.id.sdk_list_entry_text,
				new ArrayList<String>());

		mInputBar.setOnKeyPressedListener(new OnKeyPressedListener() {
			@Override
			public void onKeyPressed(String text) {

				if (mInputBar.getInputText().length() == 0) {
					mInputBar.setButtonText(null);
					mAdapter = new ArrayAdapter<String>(
							getApplicationContext(), R.layout.sdk_list_entry,
							R.id.sdk_list_entry_text, new ArrayList<String>());

					mListView.setAdapter(mAdapter);
					mListView.invalidate();

				} else {
					mInputBar.setButtonText("");
					System.out.println("text" + text);
					AutoCompleteRequest request = new AutoCompleteRequest(text);
					mController.autoCompleteBuilding(view, request);
				}
			}
		});

		mLayout.addFillerView(mInputBar);

		// The ActionBar is added automatically when you call setContentView
		setContentView(mLayout);
	}

	/**
	 * Initialize the autocomplete suggestion list
	 */
	private void createSuggestionsList() {
		mListView = new LabeledListViewElement(this);
		mInputBar.addView(mListView);

		mListView.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> adapter, View view, int pos,
					long id) {
				FRRoom room = listFR.get(pos);
				Log.v("fr_check_search", "checking against selected " + room);
				// TODO: add to list selected for mutli-query, and start the
				// search elsewhere
				// TODO: search for a specific time!
				FRPeriod period = new FRPeriod(
						System.currentTimeMillis() - 36 * 3600 * 1000, System
								.currentTimeMillis() - 34 * 3600 * 1000, false);
				List<FRRoom> listFRRoom = new ArrayList<FRRoom>();
				listFRRoom.add(room);
				OccupancyRequest request = new OccupancyRequest(listFRRoom,
						period);
				mController.checkOccupancy(FRCheckOccupancySearchView.this,
						request);
				Intent i = new Intent(FRCheckOccupancySearchView.this,
						FRCheckOccupancyResultView.class);
				FRCheckOccupancySearchView.this.startActivity(i);
			}
		});

	}

	@Override
	public void freeRoomResultsUpdated() {
		// we do nothing here
	}

	@Override
	public void autoCompletedUpdated() {
		listFR = mModel.getAutocompleteSuggestions();
		ArrayList<String> listS = new ArrayList<String>(listFR.size());
		mAdapter = new ArrayAdapter<String>(getApplicationContext(),
				R.layout.sdk_list_entry, R.id.sdk_list_entry_text, listS);
		for (FRRoom room : listFR) {
			String p = "";
			p += room.getBuilding() + " ";
			p += room.getNumber() + " ";
			int c = room.getCapacity();
			FRRoomType t = room.getType();
			if (c > 0 && t != null) {
				p += "(";
				p += "Type: " + t + ";";
				p += "Capacity: " + c + " places";
				p += ")";
			}
			p += "";
			listS.add(p);
		}
		mAdapter.notifyDataSetChanged();
		mListView.setAdapter(mAdapter);
		mListView.invalidate();
	}

	@Override
	public void occupancyResultUpdated() {
		// we do nothing here
	}
}
