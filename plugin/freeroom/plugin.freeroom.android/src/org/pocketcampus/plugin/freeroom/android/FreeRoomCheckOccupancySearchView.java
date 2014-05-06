package org.pocketcampus.plugin.freeroom.android;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
import java.util.Date;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.pocketcampus.android.platform.sdk.core.PluginController;
import org.pocketcampus.android.platform.sdk.tracker.Tracker;
import org.pocketcampus.android.platform.sdk.ui.element.InputBarElement;
import org.pocketcampus.android.platform.sdk.ui.element.OnKeyPressedListener;
import org.pocketcampus.android.platform.sdk.ui.layout.StandardTitledDoubleLayout;
import org.pocketcampus.android.platform.sdk.ui.list.LabeledListViewElement;
import org.pocketcampus.plugin.freeroom.R;
import org.pocketcampus.plugin.freeroom.android.iface.IFreeRoomView;
import org.pocketcampus.plugin.freeroom.android.layout.FreeRoomTabLayout;
import org.pocketcampus.plugin.freeroom.shared.AutoCompleteRequest;
import org.pocketcampus.plugin.freeroom.shared.FRPeriod;
import org.pocketcampus.plugin.freeroom.shared.FRRoom;
import org.pocketcampus.plugin.freeroom.shared.FRRoomType;
import org.pocketcampus.plugin.freeroom.shared.OccupancyRequest;
import org.pocketcampus.plugin.freeroom.shared.utils.FRTimes;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.app.TimePickerDialog.OnTimeSetListener;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.inputmethod.EditorInfo;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.TextView.OnEditorActionListener;
import android.widget.TimePicker;
import android.widget.Toast;

/**
 * View displaying the SearchQuery of the CheckOccupancy feature.
 * <p>
 * 
 * @author FreeRoom Project Team - Julien WEBER <julien.weber@epfl.ch> and
 *         Valentin MINDER <valentin.minder@epfl.ch>
 * 
 */
public class FreeRoomCheckOccupancySearchView extends FreeRoomAbstractView
		implements IFreeRoomView {

	private FreeRoomController mController;
	private FreeRoomModel mModel;

	private FreeRoomTabLayout mLayout;
	private LinearLayout mGlobalSubLayout;
	private LinearLayout mSubLayoutUpperData;
	private LinearLayout mSubSubTimePickersLayout;

	private ArrayList<FRRoom> mSelectedRoomsToQueryArrayList;
	/**
	 * This must contain exactly the same as
	 * <code>mSelectedRoomsToQueryArrayList</code> ONLY used for performance
	 * (checking if an element exist is quicker).
	 * 
	 * TODO: use LinkedHashSet instead!
	 */
	private HashSet<FRRoom> mSelectedRoomsToQueryHashSet;

	private ListView mAutoCompleteSuggestionListView;
	private List<FRRoom> mAutoCompleteSuggestionArrayListFRRoom;

	/** The input bar to make the search */
	private InputBarElement mAutoCompleteSuggestionInputBarElement;
	/** Adapter for the <code>mListView</code> */
	private ArrayAdapter<String> mAdapter;

	private DatePickerDialog mDatePickerDialog;
	private TimePickerDialog mTimePickerStartDialog;
	private TimePickerDialog mTimePickerEndDialog;

	private Button showDatePicker;
	private Button showStartTimePicker;
	private Button showEndTimePicker;

	private Button searchButton;
	private Button resetButton;

	private TextView mSummarySelectedRoomsTextView;

	private ArrayList<String> mAutoCompleteSuggestionArrayListString;

	private int yearSelected = -1;
	private int monthSelected = -1;
	private int dayOfMonthSelected = -1;
	private int startHourSelected = -1;
	private int startMinSelected = -1;
	private int endHourSelected = -1;
	private int endMinSelected = -1;

	private SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yy");
	private SimpleDateFormat timeFormat = new SimpleDateFormat("HH:mm");

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

		mSelectedRoomsToQueryArrayList = new ArrayList<FRRoom>(10);	
		
		initializeView();
		createSuggestionsList();
		addAllFavsToAutoComplete();
	}

	public void initializeView() {
		init();
		resetTimes();

		// Setup the layout
		mLayout = new FreeRoomTabLayout(this, this);
		mGlobalSubLayout = new LinearLayout(this);
		mGlobalSubLayout.setOrientation(LinearLayout.VERTICAL);
		
//		mLayout.setTitle(getString(R.string.freeroom_title_occupancy_search));
		mLayout.hideTitle();

		mSubLayoutUpperData = new LinearLayout(this);
		mSubLayoutUpperData.setOrientation(LinearLayout.VERTICAL);
		mSubSubTimePickersLayout = new LinearLayout(this);
		mSubSubTimePickersLayout.setOrientation(LinearLayout.HORIZONTAL);
		mGlobalSubLayout.addView(mSubLayoutUpperData);

		UIConstructPickers();

		mSubSubTimePickersLayout.addView(showDatePicker);
		mSubSubTimePickersLayout.addView(showStartTimePicker);
		mSubSubTimePickersLayout.addView(showEndTimePicker);

		UIConstructButton();

		mSubSubTimePickersLayout.addView(searchButton);
		mSubSubTimePickersLayout.addView(resetButton);

		mSubLayoutUpperData.addView(mSubSubTimePickersLayout);

		mSummarySelectedRoomsTextView = new TextView(this);
		mSubLayoutUpperData.addView(mSummarySelectedRoomsTextView);

		UIConstructInputBar();

		mGlobalSubLayout.addView(mAutoCompleteSuggestionInputBarElement);
		
		mLayout.addFillerView(mGlobalSubLayout);

		// The ActionBar is added automatically when you call setContentView

		reset();
		setContentView(mLayout);
	}

	private void UIConstructPickers() {
		// First allow the user to select a date
		showDatePicker = new Button(this);
		mDatePickerDialog = new DatePickerDialog(this,
				new DatePickerDialog.OnDateSetListener() {

					@Override
					public void onDateSet(DatePicker view, int nYear,
							int nMonthOfYear, int nDayOfMonth) {
						yearSelected = nYear;
						monthSelected = nMonthOfYear;
						dayOfMonthSelected = nDayOfMonth;
						updateShowDatePicker();
						searchButton.setEnabled(auditSubmit() == 0);

					}
				}, yearSelected, monthSelected, dayOfMonthSelected);

		showDatePicker.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				mDatePickerDialog.show();
			}
		});

		// Then the starting time of the period
		showStartTimePicker = new Button(this);
		mTimePickerStartDialog = new TimePickerDialog(this,
				new OnTimeSetListener() {

					@Override
					public void onTimeSet(TimePicker view, int nHourOfDay,
							int nMinute) {
						startHourSelected = nHourOfDay;
						startMinSelected = nMinute;
						updateShowStartTimePicker();
						searchButton.setEnabled(auditSubmit() == 0);

					}
				}, startHourSelected, startMinSelected, true);

		showStartTimePicker.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				mTimePickerStartDialog.show();

			}
		});

		// Then the ending time of the period
		showEndTimePicker = new Button(this);
		mTimePickerEndDialog = new TimePickerDialog(this,
				new OnTimeSetListener() {

					@Override
					public void onTimeSet(TimePicker view, int nHourOfDay,
							int nMinute) {
						endHourSelected = nHourOfDay;
						endMinSelected = nMinute;
						updateShowEndTimePicker();
						searchButton.setEnabled(auditSubmit() == 0);

					}
				}, endHourSelected, endMinSelected, true);

		showEndTimePicker.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				mTimePickerEndDialog.show();

			}
		});
	}

	private void UIConstructButton() {
		searchButton = new Button(this);
		searchButton.setEnabled(false);
		searchButton.setText(R.string.freeroom_searchbutton);
		searchButton.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				prepareSearchQuery();
			}
		});

		resetButton = new Button(this);
		resetButton.setEnabled(true);
		resetButton.setText(R.string.freeroom_resetbutton);
		resetButton.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				reset();
				addAllFavsToAutoComplete();
				// we reset the input bar...
				mAutoCompleteSuggestionInputBarElement.setInputText("");
			}
		});
	}

	private void UIConstructInputBar() {
		final IFreeRoomView view = this;

		mAutoCompleteSuggestionInputBarElement = new InputBarElement(
				this,
				null,
				getString(R.string.freeroom_check_occupancy_search_inputbarhint));
		mAutoCompleteSuggestionInputBarElement
				.setImeOptions(EditorInfo.IME_ACTION_SEARCH);

		mAutoCompleteSuggestionInputBarElement
				.setOnEditorActionListener(new OnEditorActionListener() {
					@Override
					public boolean onEditorAction(TextView v, int actionId,
							KeyEvent event) {
						if (actionId == EditorInfo.IME_ACTION_SEARCH) {
							String query = mAutoCompleteSuggestionInputBarElement
									.getInputText();
							Log.v(this.getClass().toString(),
									"we do nothing here... with query: "
											+ query);
						}

						return true;
					}
				});
		mAutoCompleteSuggestionInputBarElement
				.setOnButtonClickListener(new OnClickListener() {
					@Override
					public void onClick(View v) {
						String query = mAutoCompleteSuggestionInputBarElement
								.getInputText();
						//TODO change group accordingly, set to 1 by default and for testing purpose
						AutoCompleteRequest request = new AutoCompleteRequest(
								query, 1);
						mController.autoCompleteBuilding(view, request);
					}
				});

		mAdapter = new ArrayAdapter<String>(getApplicationContext(),
				R.layout.sdk_list_entry, R.id.sdk_list_entry_text,
				mAutoCompleteSuggestionArrayListString);

		mAutoCompleteSuggestionInputBarElement
				.setOnKeyPressedListener(new OnKeyPressedListener() {
					@Override
					public void onKeyPressed(String text) {
						mAutoCompleteSuggestionListView.setAdapter(mAdapter);

						if (mAutoCompleteSuggestionInputBarElement
								.getInputText().length() == 0) {
							mAutoCompleteSuggestionInputBarElement
									.setButtonText(null);
							mAutoCompleteSuggestionListView.invalidate();
							addAllFavsToAutoComplete();
						} else {
							mAutoCompleteSuggestionInputBarElement
									.setButtonText("");
							//TODO change group accordingly, set to 1 by default and for testing purpose
							AutoCompleteRequest request = new AutoCompleteRequest(
									text, 1);
							mController.autoCompleteBuilding(view, request);
						}
					}
				});
	}
	
	private void addAllFavsToAutoComplete() {
		Map<String, String> map = mModel.getAllRoomMapFavorites();
		
		mAutoCompleteSuggestionArrayListFRRoom.clear();
		mAutoCompleteSuggestionArrayListString.clear();

		Iterator<String> iter = map.keySet().iterator();
		while (iter.hasNext()) {
			String uid = iter.next();
			String doorCode = map.get(uid);
			FRRoom room = new FRRoom(doorCode, uid);
			if (!mSelectedRoomsToQueryArrayList.contains(room)) {
				mAutoCompleteSuggestionArrayListFRRoom.add(room);
				mAutoCompleteSuggestionArrayListString.add(doorCode);
			}
		}

		mAdapter.notifyDataSetChanged();
		mAutoCompleteSuggestionListView.setAdapter(mAdapter);
	}

	/**
	 * Initialize the autocomplete suggestion list
	 */
	private void createSuggestionsList() {
		mAutoCompleteSuggestionListView = new LabeledListViewElement(this);
		mAutoCompleteSuggestionInputBarElement
				.addView(mAutoCompleteSuggestionListView);

		mAutoCompleteSuggestionListView
				.setOnItemClickListener(new OnItemClickListener() {
					@Override
					public void onItemClick(AdapterView<?> adapter, View view,
							int pos, long id) {

						FRRoom room = mAutoCompleteSuggestionArrayListFRRoom
								.get(pos);
						addRoomToCheck(room);
						searchButton.setEnabled(auditSubmit() == 0);
						// refresh the autocomplete, such that selected rooms
						// are not displayed
						if (mAutoCompleteSuggestionInputBarElement
								.getInputText().length() == 0) {
							addAllFavsToAutoComplete();
						} else {
							autoCompletedUpdated();
						}

						// WE DONT REMOVE the text in the input bar
						// INTENTIONNALLY: user may want to select multiple
						// rooms in the same building
					}
				});
	}

	private void addRoomToCheck(FRRoom room) {
		// we only add if it already contains the room
		if (!mSelectedRoomsToQueryHashSet.contains(room)) {
			mSelectedRoomsToQueryArrayList.add(room);
			mSelectedRoomsToQueryHashSet.add(room);
			mSummarySelectedRoomsTextView
					.setText(getSummaryTextFromCollection(mSelectedRoomsToQueryArrayList));

		} else {
			Log.e(this.getClass().toString(),
					"room cannot be added: already added");
		}
	}

	private String getSummaryTextFromCollection(Collection<FRRoom> collec) {
		Iterator<FRRoom> iter = collec.iterator();
		StringBuffer buffer = new StringBuffer(collec.size() * 5);
		FRRoom room = null;
		buffer.append(getString(R.string.freeroom_check_occupancy_search_text_selected_rooms)
				+ " ");
		boolean empty = true;
		while (iter.hasNext()) {
			empty = false;
			room = iter.next();
			buffer.append(room.getDoorCode() + ", ");
		}
		buffer.setLength(buffer.length() - 2);
		String result = "";
		if (empty) {
			result = getString(R.string.freeroom_check_occupancy_search_text_no_selected_rooms);
		} else {
			result = buffer.toString();
		}
		return result;
	}

	/**
	 * Reset the year, month, day, hour_start, minute_start, hour_end,
	 * minute_end to their initial values. DONT forget to update the date/time
	 * pickers afterwards.
	 */
	private void resetTimes() {
		FRPeriod mFrPeriod = FRTimes.getNextValidPeriod();

		Calendar mCalendar = Calendar.getInstance();
		mCalendar.setTimeInMillis(mFrPeriod.getTimeStampStart());
		yearSelected = mCalendar.get(Calendar.YEAR);
		monthSelected = mCalendar.get(Calendar.MONTH);
		dayOfMonthSelected = mCalendar.get(Calendar.DAY_OF_MONTH);
		startHourSelected = mCalendar.get(Calendar.HOUR_OF_DAY);
		startMinSelected = mCalendar.get(Calendar.MINUTE);
		mCalendar.setTimeInMillis(mFrPeriod.getTimeStampEnd());
		endHourSelected = mCalendar.get(Calendar.HOUR_OF_DAY);
		endMinSelected = mCalendar.get(Calendar.MINUTE);
	}

	private void updateDateTimePickers() {
		// reset the time pickers
		mDatePickerDialog.updateDate(yearSelected, monthSelected,
				dayOfMonthSelected);
		mTimePickerStartDialog.updateTime(startHourSelected, startMinSelected);
		mTimePickerEndDialog.updateTime(endHourSelected, endMinSelected);
	}

	private void init() {
		mSelectedRoomsToQueryArrayList = new ArrayList<FRRoom>(10);
		mSelectedRoomsToQueryHashSet = new HashSet<FRRoom>(10);
		mAutoCompleteSuggestionArrayListFRRoom = new ArrayList<FRRoom>(10);
		mAutoCompleteSuggestionArrayListString = new ArrayList<String>(10);
	}

	private void reset() {
		searchButton.setEnabled(false);

		// reset the list of selected rooms
		mSelectedRoomsToQueryArrayList.clear();
		mSelectedRoomsToQueryHashSet.clear();
		mSummarySelectedRoomsTextView
				.setText(getString(R.string.freeroom_check_occupancy_search_text_no_selected_rooms));

		mAutoCompleteSuggestionArrayListFRRoom.clear();
		mAutoCompleteSuggestionArrayListString.clear();

		resetTimes();
		updateDateTimePickers();

		// show the buttons
		updatePickersButtons();
	}

	private void updatePickersButtons() {
		updateShowDatePicker();
		updateShowStartTimePicker();
		updateShowEndTimePicker();
	}

	private void updateShowDatePicker() {
		showDatePicker
				.setText(dateFormat.format(new Date(yearSelected,
								monthSelected, dayOfMonthSelected)));
	}

	private void updateShowStartTimePicker() {
		showStartTimePicker
				.setText(getString(R.string.freeroom_check_occupancy_search_start)
						+ " : "
						+ timeFormat.format(new Date(yearSelected,
								monthSelected, dayOfMonthSelected,
								startHourSelected, startMinSelected)));
	}

	private void updateShowEndTimePicker() {
		showEndTimePicker
				.setText(getString(R.string.freeroom_check_occupancy_search_end)
						+ " : "
						+ timeFormat.format(new Date(yearSelected,
								monthSelected, dayOfMonthSelected,
								endHourSelected, endMinSelected)));
	}

	/**
	 * Construct the <code>FRPeriod</code> object asscociated with the current
	 * selected times.
	 * 
	 * @return
	 */
	private FRPeriod prepareFRFrPeriod() {
		Calendar start = Calendar.getInstance();
		start.clear();
		start.set(yearSelected, monthSelected, dayOfMonthSelected,
				startHourSelected, startMinSelected, 0);

		Calendar end = Calendar.getInstance();
		end.clear();
		end.set(yearSelected, monthSelected, dayOfMonthSelected,
				endHourSelected, endMinSelected, 0);

		// constructs the request
		return new FRPeriod(start.getTimeInMillis(), end.getTimeInMillis(),
				false);
	}

	/**
	 * Prepare the actual query to send and set it in the controller
	 */
	private void prepareSearchQuery() {
		FRPeriod period = prepareFRFrPeriod();

		// mSelectedRoomsToQueryArrayList
		List<String> mUIDList = new ArrayList<String>(
				mSelectedRoomsToQueryArrayList.size());
		Iterator<FRRoom> iter = mSelectedRoomsToQueryArrayList.iterator();
		while (iter.hasNext()) {
			FRRoom room = iter.next();
			mUIDList.add(room.getUid());
		}
		OccupancyRequest request = new OccupancyRequest(mUIDList, period);

		// starting the result UI before sending the request
		Intent i = new Intent(FreeRoomCheckOccupancySearchView.this,
				FreeRoomCheckOccupancyResultView.class);
		FreeRoomCheckOccupancySearchView.this.startActivity(i);

		// finally sending the request to the controller
		mController.prepareCheckOccupancy(request);
	}

	/**
	 * Check that the times set are valid, according to the shared definition.
	 * 
	 * @return 0 if times are valids, positive integer otherwise
	 */
	private int auditTimes() {
		// NOT EVEN SET, we don't bother checking
		if (yearSelected == -1 || monthSelected == -1
				|| dayOfMonthSelected == -1) {
			return 1;
		}
		if (startHourSelected == -1 || endHourSelected == -1
				|| startMinSelected == -1 || endMinSelected == -1) {
			return 1;
		}

		// IF SET, we use the shared method checking the prepared period

		String errors = FRTimes.validCalendarsString(prepareFRFrPeriod());
		if (errors.equals("")) {
			return 0;
		}

		Toast.makeText(
				getApplicationContext(),
				"Please review the time, should be between Mo-Fr 8am-7pm.\n"
						+ "The end should also be after the start, and at least 5 minutes.",
				Toast.LENGTH_LONG).show();
		Toast.makeText(getApplicationContext(),
				"Errors remaining: \n" + errors, Toast.LENGTH_LONG).show();
		return 1;
	}

	/**
	 * This method check if the client is allowed to submit a request to the
	 * server.
	 * 
	 * @return 0 if there is no error and the client can send the request,
	 *         something else otherwise.
	 */
	private int auditSubmit() {
		int error = auditTimes();

		if (mSelectedRoomsToQueryArrayList == null
				|| mSelectedRoomsToQueryArrayList.isEmpty()) {
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
		mAdapter.notifyDataSetInvalidated();
		mAutoCompleteSuggestionArrayListFRRoom.clear();
		mAutoCompleteSuggestionArrayListString.clear();

//		Iterator<FRRoom> iter = mModel.getAutocompleteSuggestions().iterator();
//		while (iter.hasNext()) {
//			FRRoom room = iter.next();
//
//			// rooms that are already selected are not displayed...
//			if (!mSelectedRoomsToQueryHashSet.contains(room)) {
//				String result = "";
//				result += room.getDoorCode();
//				int capacity = room.getCapacity();
//				FRRoomType t = room.getType();
//				if (capacity > 0 && t != null) {
//					result += "(";
//					result += "Type: " + t + ";";
//					result += "Capacity: " + capacity + " places";
//					result += ")";
//				}
//				result += "";
//				mAutoCompleteSuggestionArrayListFRRoom.add(room);
//				mAutoCompleteSuggestionArrayListString.add(result);
//			}
//		}

		mAdapter.notifyDataSetChanged();
		mAutoCompleteSuggestionListView.setAdapter(mAdapter);
		mAutoCompleteSuggestionListView.invalidate();
	}

	@Override
	public void occupancyResultUpdated() {
		// we do nothing here
	}

	@Override
	public void occupancyResultsUpdated() {
		// TODO Auto-generated method stub
		
	}
}
