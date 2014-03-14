package org.pocketcampus.plugin.freeroom.android;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import org.pocketcampus.android.platform.sdk.core.PluginController;
import org.pocketcampus.android.platform.sdk.tracker.Tracker;
import org.pocketcampus.android.platform.sdk.ui.element.InputBarElement;
import org.pocketcampus.android.platform.sdk.ui.element.OnKeyPressedListener;
import org.pocketcampus.android.platform.sdk.ui.layout.StandardTitledDoubleLayout;
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
import android.view.Gravity;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.inputmethod.EditorInfo;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.DatePicker;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.TimePicker;

/**
 * View displaying the SearchQuery of the CheckOccupancy feature.
 * <p>
 * 
 * @author FreeFroom Project Team - Julien WEBER <julien.weber@epfl.ch> and
 *         Valentin MINDER <valentin.minder@epfl.ch>
 * 
 */
public class FRCheckOccupancySearchView extends FreeRoomAbstractView implements
		IFreeRoomView {

	private FreeRoomController mController;
	private FreeRoomModel mModel;

	private StandardTitledDoubleLayout mLayout;
	private LinearLayout subLayout;

	private ArrayList<FRRoom> roomsToCheck;

	private ListView mListView;
	private List<FRRoom> listFR;

	/** The input bar to make the search */
	private InputBarElement mInputBar;
	/** Adapter for the <code>mListView</code> */
	private ArrayAdapter<String> mAdapter;

	private DatePicker dp;
	private TimePicker tp_s;
	private TimePicker tp_e;

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
		mLayout = new StandardTitledDoubleLayout(this);

		mLayout.setTitle(getString(R.string.freeroom_title_occupancy_search));
		subLayout = new LinearLayout(this);
		subLayout.setOrientation(LinearLayout.HORIZONTAL);
		mLayout.addFirstLayoutFillerView(subLayout);

		dp = new DatePicker(this);
		TextView tv = new TextView(this);
		tv.setText("from");
		tv.setGravity(Gravity.CENTER_VERTICAL);
		tp_s = new TimePicker(this);
		TextView tv2 = new TextView(this);
		tv2.setText("to");
		tv2.setGravity(Gravity.CENTER);
		tp_e = new TimePicker(this);
		subLayout.addView(dp);
		subLayout.addView(tv);
		subLayout.addView(tp_s);
		subLayout.addView(tv2);
		subLayout.addView(tp_e);

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

		mLayout.addSecondLayoutFillerView(mInputBar);

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

				// getting the time from the pickers;
				int day = dp.getDayOfMonth();
				int month = dp.getMonth();
				int year = dp.getYear();
				int h_s = tp_s.getCurrentHour();
				int m_s = tp_s.getCurrentMinute();
				int h_e = tp_e.getCurrentHour();
				int m_e = tp_e.getCurrentMinute();
				System.out.println(year + "/" + month + "/" + day + "/" + h_s
						+ "/" + m_s);
				Calendar start = Calendar.getInstance();
				System.out.println(start.getTimeInMillis());
				start.set(year, month, day, h_s, m_s, 0);
				Calendar end = Calendar.getInstance();
				end.set(year, month, day, h_e, m_e, 0);
				System.out.println(start.getTimeInMillis());

				// constructs the request
				FRPeriod period = new FRPeriod(start.getTimeInMillis(), end
						.getTimeInMillis(), false);
				List<FRRoom> listFRRoom = new ArrayList<FRRoom>();
				listFRRoom.add(room);
				OccupancyRequest request = new OccupancyRequest(listFRRoom,
						period);

				// starting the result UI before sending the request
				Intent i = new Intent(FRCheckOccupancySearchView.this,
						FRCheckOccupancyResultView.class);
				FRCheckOccupancySearchView.this.startActivity(i);

				// finally sending the request to the controller
				mController.checkOccupancy(FRCheckOccupancySearchView.this,
						request);

			}
		});

	}

	@Override
	public void freeRoomResultsUpdated() {
		// we do nothing here
	}

	@Override
	public void autoCompletedUpdated() {
		mAdapter.notifyDataSetInvalidated();
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
		Log.v("fr-check-search", "listener to occupancyResultUpdated called");
	}
}
