package org.pocketcampus.plugin.freeroom.android;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
import java.util.Date;
import java.util.HashSet;
import java.util.Iterator;
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
 * @author FreeFroom Project Team - Julien WEBER <julien.weber@epfl.ch> and
 *         Valentin MINDER <valentin.minder@epfl.ch>
 * 
 */
public class FreeRoomCheckOccupancySearchView extends FreeRoomAbstractView
		implements IFreeRoomView {

	private FreeRoomController mController;
	private FreeRoomModel mModel;

	private StandardTitledDoubleLayout mLayout;
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

	// TODO: defines that const elsewhere
	private final int firstHourCheckable = 8;
	private final int lastHourCheckable = 19;
	private final int minMinutesIntervalToCheck = 5;

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

		initializeCheckOccupancySearchView();
		createSuggestionsList();
	}

	private void initializeCheckOccupancySearchView() {
		init();
		resetTimes();

		// Setup the layout
		mLayout = new StandardTitledDoubleLayout(this);

		mLayout.setTitle(getString(R.string.freeroom_title_occupancy_search));
		mSubLayoutUpperData = new LinearLayout(this);
		mSubLayoutUpperData.setOrientation(LinearLayout.VERTICAL);
		mSubSubTimePickersLayout = new LinearLayout(this);
		mSubSubTimePickersLayout.setOrientation(LinearLayout.HORIZONTAL);
		mLayout.addFirstLayoutFillerView(mSubLayoutUpperData);

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

		mLayout.addSecondLayoutFillerView(mAutoCompleteSuggestionInputBarElement);

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
						AutoCompleteRequest request = new AutoCompleteRequest(
								query);
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
							// TODO: add the favorites to the listview!!!
							mModel.getFavorites();
						} else {
							mAutoCompleteSuggestionInputBarElement
									.setButtonText("");
							AutoCompleteRequest request = new AutoCompleteRequest(
									text);
							mController.autoCompleteBuilding(view, request);
						}
					}
				});
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
						autoCompletedUpdated();

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

	int testCounter = -1;

	/**
	 * Reset the year, month, day, hour_start, minute_start, hour_end,
	 * minute_end to their initial values. DONT forget to update the date/time
	 * pickers afterwards.
	 */
	private void resetTimes() {
		// reset the time to the present time
		Calendar mCalendar = Calendar.getInstance();

		boolean testActivated = false;
		if (testActivated) {
			switch (testCounter) {
			case 0:
				// thursday, early morning
				mCalendar.set(2014, 02, 20, 01, 00);
				break;
			case 1:
				// thursday, during day, usual
				mCalendar.set(2014, 02, 20, 17, 59);
				break;
			case 2:
				// thursday, late afternoon
				mCalendar.set(2014, 02, 20, 18, 23);
				break;
			case 3:
				// thursday, after delay
				mCalendar.set(2014, 02, 20, 18, 56);
				break;
			case 4:
				// thursday, after delay
				mCalendar.set(2014, 02, 20, 19, 00);
				break;
			case 5:
				// friday, late afternoon
				mCalendar.set(2014, 02, 21, 18, 23);
				break;
			case 6:
				// friday, after delay
				// TODO: dont work
				mCalendar.set(2014, 02, 21, 18, 56);
				break;
			case 7:
				// friday, after delay
				// TODO: dont work
				mCalendar.set(2014, 02, 21, 19, 00);
				break;
			case 8:
				// saturday, early morning
				mCalendar.set(2014, 02, 22, 00, 00);
				break;
			case 9:
				// saturday, early morning
				mCalendar.set(2014, 02, 22, 00, 21);
				break;
			case 10:
				// saturday, during day, usual
				// TODO: dont work
				mCalendar.set(2014, 02, 22, 17, 59);
				break;
			case 11:
				// saturday, late afternoon
				// TODO: dont work
				mCalendar.set(2014, 02, 22, 18, 00);
				break;
			case 12:
				// saturday, after delay
				// TODO: dont work
				mCalendar.set(2014, 02, 22, 18, 55);
				break;
			case 13:
				// saturday, after delay
				// TODO: dont work
				mCalendar.set(2014, 02, 22, 19, 00);
				break;
			case 14:
				// saturday, early morning
				mCalendar.set(2014, 02, 23, 00, 00);
				break;
			case 15:
				// saturday, during day, usual
				// TODO: dont work
				mCalendar.set(2014, 02, 23, 17, 59);
				break;
			case 16:
				// saturday, late afternoon
				// TODO: dont work
				mCalendar.set(2014, 02, 23, 18, 00);
				break;
			case 17:
				// saturday, after delay
				// TODO: dont work
				mCalendar.set(2014, 02, 23, 18, 55);
				break;
			case 18:
				// saturday, after delay
				// TODO: dont work
				mCalendar.set(2014, 02, 23, 19, 00);
				break;
			default:
				mCalendar.setTimeInMillis(System.currentTimeMillis());
				break;
			}
			System.out.println("Test Counter:" + testCounter);
			testCounter++;
		}

		// values for test only (weekends, nights, ...)
		int temp_now_day_week = mCalendar.get(Calendar.DAY_OF_WEEK);
		int temp_now_hour = mCalendar.get(Calendar.HOUR_OF_DAY);
		int temp_now_min = mCalendar.get(Calendar.MINUTE);

		// starting Friday 18h55, and during all the weekend, we shift to Mon
		// between 0am and 1am. It will be reshifted to Mon 8h-9h after.
		int hourShift = 0;
		if (temp_now_day_week == Calendar.SUNDAY) {
			hourShift = 24 - temp_now_hour + 1;
		}
		if (temp_now_day_week == Calendar.SATURDAY) {
			hourShift = 24 - temp_now_hour + 1;
			hourShift += 24;
		}
		if ((temp_now_day_week == Calendar.FRIDAY)
				&& checkHourMinIsEveningAndShifted(temp_now_hour, temp_now_min)) {
			hourShift = 24 - temp_now_hour + 1;
			hourShift += 2 * 24;
		}
		mCalendar.setTimeInMillis(mCalendar.getTimeInMillis() + hourShift
				* 3600 * 1000);

		// actualize with the enventually shifted Calendar
		temp_now_hour = mCalendar.get(Calendar.HOUR_OF_DAY);
		temp_now_min = mCalendar.get(Calendar.MINUTE);

		yearSelected = mCalendar.get(Calendar.YEAR);
		monthSelected = mCalendar.get(Calendar.MONTH);
		dayOfMonthSelected = mCalendar.get(Calendar.DAY_OF_MONTH);

		// default set: next hour to next+1 hour, all minutes to 0
		// Works perfectly Mon-Fri 8h-18h.
		// weekend are handled before to be on next Monday.
		startHourSelected = mCalendar.get(Calendar.HOUR_OF_DAY) + 1;
		startMinSelected = 0;
		endHourSelected = startHourSelected + 1;
		endMinSelected = 0;

		// during the evening (starting 18h55), we shift to the next morning
		// during the night (before 8h), we shift to the first hour of morning
		// (8h-9h).
		// during late afternoon (18h-18h55), we default set from now to 19h00.
		if (temp_now_hour < firstHourCheckable) {
			startHourSelected = firstHourCheckable;
			endHourSelected = firstHourCheckable + 1;
		} else if (checkHourMinIsEveningAndShifted(temp_now_hour, temp_now_min)) {
			yearSelected = mCalendar.get(Calendar.YEAR);
			monthSelected = mCalendar.get(Calendar.MONTH);
			dayOfMonthSelected = mCalendar.get(Calendar.DAY_OF_MONTH);
			System.out.println(yearSelected + "" + monthSelected + ""
					+ dayOfMonthSelected);
			mCalendar
					.setTimeInMillis(mCalendar.getTimeInMillis() + 24 * 3600 * 1000);
			System.out.println(yearSelected + "" + monthSelected + ""
					+ dayOfMonthSelected);
			yearSelected = mCalendar.get(Calendar.YEAR);
			monthSelected = mCalendar.get(Calendar.MONTH);
			dayOfMonthSelected = mCalendar.get(Calendar.DAY_OF_MONTH);
			startHourSelected = firstHourCheckable;
			endHourSelected = firstHourCheckable + 1;
		} else if (temp_now_hour == lastHourCheckable - 1) {
			startHourSelected = temp_now_hour;
			startMinSelected = temp_now_min;
			endHourSelected = temp_now_hour + 1;
		}
	}

	/**
	 * Checks if the given hour and minutes is evening. Request in evening are
	 * shifted to next morning.
	 * 
	 * Should return true if time is greater or equal to 19h00, up to 24h00.
	 * Moreover, it returns true a few minutes before 19h00, because we need at
	 * least a few minutes to check
	 * 
	 * @param hour
	 * @param min
	 * @return
	 */
	private boolean checkHourMinIsEveningAndShifted(int hour, int min) {
		return (hour >= lastHourCheckable)
				|| (hour == (lastHourCheckable - 1) && min >= (60 - minMinutesIntervalToCheck));
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

		// TODO: set the inputbar text empty and display favorites!
	}

	private void updatePickersButtons() {
		updateShowDatePicker();
		updateShowStartTimePicker();
		updateShowEndTimePicker();
	}

	private void updateShowDatePicker() {
		showDatePicker
				.setText(getString(R.string.freeroom_check_occupancy_search_date)
						+ " : "
						+ dateFormat.format(new Date(yearSelected,
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

	private void prepareSearchQuery() {
		Calendar start = Calendar.getInstance();
		start.clear();
		start.set(yearSelected, monthSelected, dayOfMonthSelected,
				startHourSelected, startMinSelected, 0);

		Calendar end = Calendar.getInstance();
		end.clear();
		end.set(yearSelected, monthSelected, dayOfMonthSelected,
				endHourSelected, endMinSelected, 0);

		// constructs the request
		FRPeriod period = new FRPeriod(start.getTimeInMillis(),
				end.getTimeInMillis(), false);
		
		// mSelectedRoomsToQueryArrayList
		List<String> mUIDList = new ArrayList<String>(mSelectedRoomsToQueryArrayList.size());
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

	private int auditTimes() {
		int error = 0;
		if (yearSelected == -1 || monthSelected == -1
				|| dayOfMonthSelected == -1) {
			error++;
		}

		if (startHourSelected == -1 || endHourSelected == -1
				|| startMinSelected == -1 || endMinSelected == -1) {
			error++;
		}

		if (startHourSelected == endHourSelected) {
			// there must be at least 5 minutes
			if (endMinSelected <= startMinSelected + 5) {
				error++;
			}
		} else if (startHourSelected > endHourSelected) {
			error++;
		} else if (startHourSelected + 1 == endHourSelected) {
			// there must be at least 5 minutes
			if (endMinSelected + 60 - startMinSelected < 5) {
				error++;
			}
		}

		if (startHourSelected <= 7) {
			error++;
		}
		if (endHourSelected > 19
				|| (endHourSelected == 19 && endMinSelected != 0)) {
			error++;
		}

		Calendar mCalendar = Calendar.getInstance();
		mCalendar.clear();
		mCalendar.set(yearSelected, monthSelected, dayOfMonthSelected);
		int day = mCalendar.get(Calendar.DAY_OF_WEEK);

		// day should also be between Monday-Friday
		if (day < 2 || day > 6) {
			error++;
		}
		if (error != 0) {
			Toast.makeText(
					getApplicationContext(),
					"Please review the time, should be between Mo-Fr 8am-7pm.\n"
							+ "The end should also be after the start, and at least 5 minutes.",
					Toast.LENGTH_LONG).show();
		}
		return error;
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

		Iterator<FRRoom> iter = mModel.getAutocompleteSuggestions().iterator();
		while (iter.hasNext()) {
			FRRoom room = iter.next();

			// rooms that are already selected are not displayed...
			if (!mSelectedRoomsToQueryHashSet.contains(room)) {
				String result = "";
				result += room.getDoorCode();
				int capacity = room.getCapacity();
				FRRoomType t = room.getType();
				if (capacity > 0 && t != null) {
					result += "(";
					result += "Type: " + t + ";";
					result += "Capacity: " + capacity + " places";
					result += ")";
				}
				result += "";
				mAutoCompleteSuggestionArrayListFRRoom.add(room);
				mAutoCompleteSuggestionArrayListString.add(result);
			}
		}

		mAdapter.notifyDataSetChanged();
		mAutoCompleteSuggestionListView.setAdapter(mAdapter);
		mAutoCompleteSuggestionListView.invalidate();
	}

	@Override
	public void occupancyResultUpdated() {
		// we do nothing here
	}
}
