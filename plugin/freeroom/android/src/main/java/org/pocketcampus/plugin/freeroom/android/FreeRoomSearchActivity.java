package org.pocketcampus.plugin.freeroom.android;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.Timer;
import java.util.TimerTask;

import org.pocketcampus.platform.android.core.PluginController;
import org.pocketcampus.platform.android.core.PluginView;
import org.pocketcampus.platform.android.ui.element.InputBarElement;
import org.pocketcampus.platform.android.ui.element.OnKeyPressedListener;
import org.pocketcampus.plugin.freeroom.R;
import org.pocketcampus.plugin.freeroom.android.FreeRoomModel.TimePickersPref;
import org.pocketcampus.plugin.freeroom.android.adapter.FRRoomRemoveArrayAdapter;
import org.pocketcampus.plugin.freeroom.android.adapter.FRRoomSuggestionArrayAdapter;
import org.pocketcampus.plugin.freeroom.android.adapter.PreviousRequestArrayAdapter;
import org.pocketcampus.plugin.freeroom.android.iface.IFreeRoomView;
import org.pocketcampus.plugin.freeroom.android.utils.FRRequestDetails;
import org.pocketcampus.plugin.freeroom.android.utils.FRTimesClient;
import org.pocketcampus.plugin.freeroom.android.utils.FRUtilsClient;
import org.pocketcampus.plugin.freeroom.android.utils.SetArrayList;
import org.pocketcampus.plugin.freeroom.shared.Constants;
import org.pocketcampus.plugin.freeroom.shared.FRAutoCompleteRequest;
import org.pocketcampus.plugin.freeroom.shared.FRPeriod;
import org.pocketcampus.plugin.freeroom.shared.FRRoom;
import org.pocketcampus.plugin.freeroom.shared.utils.FRTimes;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.app.TimePickerDialog.OnTimeSetListener;
import android.content.DialogInterface;
import android.content.DialogInterface.OnDismissListener;
import android.content.DialogInterface.OnShowListener;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.View.OnClickListener;
import android.view.WindowManager.LayoutParams;
import android.view.inputmethod.EditorInfo;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.DatePicker;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.TextView.OnEditorActionListener;

public class FreeRoomSearchActivity extends FreeRoomAutocompleteActivity {

	// ADD SEARCH ROOM

	/**
	 * {@link #addSearchRoom}: View that holds the {@link #addSearchRoom} dialog
	 * content, defined in xml in layout folder.
	 */
	private View addSearchRoomView;
	/**
	 * {@link #addSearchRoom}: AlertDialog that holds the {@link #addSearchRoom}
	 * dialog.
	 */
	private AlertDialog addSearchRoom;

	/**
	 * {@link #addSearchRoom}: Adpater for selected room.
	 */
	private ArrayAdapter<FRRoom> addSearchRoomSelectedRoomArrayAdapter;

	/**
	 * {@link #addSearchRoom}: TextView for autocomplete status for adding room
	 * to search.
	 */
	private TextView addSearchRoomAutoCompleteStatusTextView;

	/**
	 * {@link #addSearchRoom}: the set of already selected room.
	 */
	private SetArrayList<FRRoom> addSearchRoomSelectedRooms;

	/**
	 * {@link #addSearchRoom}: list of displayed room in autocomplete
	 */
	private List<FRRoom> addSearchRoomAutoCompleteArrayListFRRoom;

	/**
	 * {@link #addSearchRoom}: The input bar to make the search
	 */
	private InputBarElement addSearchRoomAutoCompleteInputBarElement;
	/**
	 * {@link #addSearchRoom}: Adapter for the <code>mListView</code>
	 */
	private FRRoomSuggestionArrayAdapter<FRRoom> addSearchRoomSuggestionAdapter;
	/**
	 * {@link #addSearchRoom}: the listview of autocomplete suggestion
	 */
	private ListView addSearchRoomAutoCompleteListView;

	/**
	 * {@link #search}: ListView that holds previous searches.
	 */
	private ListView searchPreviousSearchesListView;
	/**
	 * {@link #search}: TextView to write "previous searches" +show/hide
	 */
	private TextView searchPreviousRequestTitleTextView;

	/**
	 * {@link #search}: Text for "Previous request"
	 */
	private String searchPreviousRequestTitleString = "mock text";
	/**
	 * {@link #search}: Array adapter for previous FRRequest.
	 */
	private ArrayAdapter<FRRequestDetails> searchPreviousRequestAdapter;

	/**
	 * {@link #search}: Date and time pickers dialog.
	 */
	private DatePickerDialog searchTimeDatePickerDialog;
	private TimePickerDialog searchTimePickerStartDialog;
	private TimePickerDialog searchTimePickerEndDialog;

	/**
	 * {@link #search}: button to show the date/time pickers dialog.
	 */
	private Button searchTimeDatePicker;
	private Button searchTimeStartPicker;
	private Button searchTimeEndPicker;
	private Button searchTimeStartShortPicker;
	private Button searchTimeEndShortPicker;

	/**
	 * {@link #search}: button for specific searches param.
	 */
	private RadioButton searchParamSpecificButton;
	private RadioButton searchParamAnyFreeRoomButton;
	private CheckBox searchParamSelectFavoritesButton;
	private CheckBox searchParamSelectUserDefButton;
	/**
	 * {@link #search}: TRUE: "only free rooms" FALSE: "allow non-free rooms"
	 */
	private CheckBox searchParamOnlyFreeRoomsButton;

	/**
	 * {@link #search}: finals button (launch)
	 */
	private Button searchLaunchValidateButton;
	private Button searchLaunchResetButton;

	/**
	 * {@link #search}: edit/add/reset user-defined list of rooms.
	 */
	private Button searchParamChangeUserDefEditButton;
	private Button searchParamChangeUserDefAddButton;
	private Button searchParamChangeUserDefResetButton;

	/**
	 * {@link #search}: buttons for advanced time edition (arrows).
	 */
	private ImageButton searchTimeAdvDownToStartHourButton;
	private ImageButton searchTimeAdvDownStartHourButton;
	private ImageButton searchTimeAdvUpStartHourButton;
	private ImageButton searchTimeAdvDownEndHourButton;
	private ImageButton searchTimeAdvUpEndHourButton;
	private ImageButton searchTimeAdvUpToEndHourButton;

	/**
	 * {@link #search}: Stores if the {@link #searchTimeAdvUpToEndHourButton}
	 * button has been trigged.
	 * <p>
	 * If yes, the endHour don't follow anymore the startHour when you change
	 * it. It will be disabled when you change manually the endHour to a value
	 * under the maximal hour.
	 */
	private boolean searchTimeAdvUpToEndSelected = false;

	/**
	 * {@link #search}: text summary of selected rooms.
	 */
	private TextView searchParamSelectedRoomsTextViewSearchMenu;

	private int yearSelected = -1;
	private int monthSelected = -1;
	private int dayOfMonthSelected = -1;
	private int startHourSelected = -1;
	private int startMinSelected = -1;
	private int endHourSelected = -1;
	private int endMinSelected = -1;

	/**
	 * {@link #search}: Optionnal line for favorites / user-defindes / only free
	 * rooms checkboxes.
	 */
	private LinearLayout searchSelectOptionalLineLinearLayoutWrapperFirst;
	/**
	 * {@link #search}: Optionnal line for add/edit/reset user-defined
	 * selection.
	 */
	private LinearLayout searchSelectOptionalLineLinearLayoutWrapperSecond;

	/**
	 * {@link #search}: Inits the {@link #search} to diplay the information
	 * about a room.
	 */

	/**
	 * COMMON: Reference to times utility method for client-side.
	 */
	private FRTimesClient times;

	@Override
	protected void onDisplay(Bundle savedInstanceState, PluginController controller) {
		super.onDisplay(savedInstanceState, controller);
		setContentView(R.layout.freeroom_layout_search);
		times = mModel.getFRTimesClient(this);
		initializeView();
		initAddSearchRoomDialog();
		initEditSearchRoomDialog();
		searchLaunchValidateButton.setEnabled(searchAuditSubmit() == 0);
		searchPreviousRequestInitTitle();
		searchResetMain();
		trackEvent("Search", null);
	}

	@Override
	protected void autoCompleteUpdatedClear() {
		addSearchRoomSuggestionAdapter.notifyDataSetInvalidated();
		addSearchRoomAutoCompleteArrayListFRRoom.clear();
	}

	/**
	 * MVC METHOD/AUTOCOMPLETE: Override
	 * {@link IFreeRoomView#autoCompleteLaunch()} and notify an autocomplete
	 * request have been launched, and that the user should way until it's
	 * completed.
	 */
	@Override
	public void autoCompleteLaunch() {
		addSearchRoomAutoCompleteStatusTextView.setText(getString(R.string.freeroom_dialog_add_autocomplete_updating));
	}

	/**
	 * AUTOCOMPLETE: To be called when autocomplete is not lauchable and ask the
	 * user to type in.
	 */
	public void autoCompleteCancel() {
		addSearchRoomAutoCompleteArrayListFRRoom.clear();
		addSearchRoomSuggestionAdapter.notifyDataSetInvalidated();
		addSearchRoomAutoCompleteStatusTextView.setText(getString(R.string.freeroom_dialog_add_autocomplete_typein));
	}

	/**
	 * {@link #search}: This method check if the client is allowed to submit a
	 * request to the server.
	 *
	 * @return 0 if there is no error and the client can send the request,
	 *         something else otherwise.
	 */
	private int searchAuditSubmit() {
		String error = searchAuditSubmitString();
		TextView tv = (TextView) findViewById(R.id.freeroom_layout_dialog_search_validation);
		if (!error.equals("")) {
			// print errors in textView
			tv.setText(getString(R.string.freeroom_search_invalid_request) + error);
			tv.setTextColor(Color.WHITE);
			tv.setBackgroundColor(Color.BLACK);
			tv.setVisibility(View.VISIBLE);
			return 1;
		} else {
			tv.setTextColor(Color.LTGRAY);
			tv.setBackgroundColor(Color.WHITE);
			// valid request
			tv.setText(getString(R.string.freeroom_search_valid_request));
			// tv.setVisibility(View.GONE);
			return 0;
		}
	}

	/**
	 * {@link #search}: DONT CALL THIS.
	 * <p>
	 * Prefer {@link #searchAuditSubmit()} !
	 *
	 * @return the error text, or "" if no error occurs.
	 */
	private String searchAuditSubmitString() {
		String ret = "";
		if (addSearchRoomSelectedRooms == null
				|| (!searchParamAnyFreeRoomButton.isChecked() && searchParamSelectUserDefButton.isChecked() && addSearchRoomSelectedRooms
						.isEmpty())) {
			ret += getString(R.string.freeroom_search_check_empty_select);
		}

		if (searchParamAnyFreeRoomButton.isChecked()
				&& (searchParamSelectFavoritesButton.isChecked() || searchParamSelectUserDefButton.isChecked())) {
			ret += getString(R.string.freeroom_search_check_any_incompat);
		}
		if (!searchParamAnyFreeRoomButton.isChecked() && !searchParamSelectFavoritesButton.isChecked()
				&& !searchParamSelectUserDefButton.isChecked()) {
			ret += getString(R.string.freeroom_search_check_at_least);
		}
		boolean isFavEmpty = mModel.getFavorites().isEmpty();
		if (searchParamSelectFavoritesButton.isChecked() && isFavEmpty) {
			if (!searchParamSelectUserDefButton.isChecked()) {
				ret += getString(R.string.freeroom_search_check_empty_fav);
			}
		}
		// we dont allow query all the room, including non-free
		if (searchParamAnyFreeRoomButton.isChecked() && !searchParamOnlyFreeRoomsButton.isChecked()) {
			ret += getString(R.string.freeroom_search_check_any_must_be_free);
		}
		return ret + searchAuditTimeString();
	}

	/**
	 * {@link #search}: Inits the title for previous request, with empty value
	 * if none, with "show" if not displayed, or "prev request" otherwise.
	 */
	private void searchPreviousRequestInitTitle() {
		if (mModel.getPreviousRequest().isEmpty()) {
			searchPreviousRequestTitleTextView.setText("");
			searchPreviousRequestTitleTextView.setVisibility(View.GONE);
		} else {
			searchPreviousRequestTitleTextView.setText(searchPreviousRequestTitleString);
			searchPreviousRequestTitleTextView.setVisibility(View.VISIBLE);
		}
	}

	/**
	 * {@link #search}: Check that the times set are valid, according to the
	 * shared definition.
	 *
	 * @return the errors
	 */
	private String searchAuditTimeString() {
		// NOT EVEN SET, we don't bother checking
		if (yearSelected == -1 || monthSelected == -1 || dayOfMonthSelected == -1) {
			return "ERROR: date not set";
		}
		if (startHourSelected == -1 || endHourSelected == -1 || startMinSelected == -1 || endMinSelected == -1) {
			return "ERROR: time not set";
		}

		// IF SET, we use the shared method checking the prepared period
		FRPeriod period = searchLaunchPreparePeriod();
		String errorsTime = FRTimes.validCalendarsString(period);
		boolean isValid = errorsTime.equals("") ? true : false;
		TextView tv = (TextView) findViewById(R.id.freeroom_layout_dialog_search_time_summary);
		if (isValid) {
			// time summary ?
			char limit = '\n';
			tv.setText(getString(R.string.freeroom_search_time_summary) + limit
					+ times.formatFullDateFullTimePeriod(period));
			tv.setVisibility(View.VISIBLE);
			return errorsTime;
		} else {
			tv.setVisibility(View.GONE);
			tv.setText("");
			// display generic time errors, depending on the rights of the user.
			boolean advancedTime = true;
			if (advancedTime) {
				return getString(R.string.freeroom_search_invalid_time_advanced, Constants.FIRST_HOUR_CHECK,
						Constants.LAST_HOUR_CHECK, Constants.MIN_MINUTE_INTERVAL, Constants.MAXIMAL_WEEKS_IN_PAST,
						Constants.MAXIMAL_WEEKS_IN_FUTURE);
			} else {
				return getString(R.string.freeroom_search_invalid_time_basic, Constants.FIRST_HOUR_CHECK,
						Constants.LAST_HOUR_CHECK, Constants.MIN_MINUTE_INTERVAL);
			}
		}
	}

	/**
	 * {@link #search}: RESET THE SEARCH COMPLETELY (SET TO DEFAULT VALUES)
	 */
	private void searchResetMain() {
		searchLaunchValidateButton.setEnabled(false);

		// reset the list of selected rooms
		addSearchRoomSelectedRooms.clear();
		// TODO: mSummarySelectedRoomsTextView
		//
		// .setText(getString(R.string.freeroom_check_occupancy_search_text_no_selected_rooms));

		addSearchRoomAutoCompleteArrayListFRRoom.clear();

		searchResetTimes();

		searchParamAnyFreeRoomButton.setChecked(true);
		searchSelectOptionalLineLinearLayoutWrapperFirst.setVisibility(View.GONE);
		searchSelectOptionalLineLinearLayoutWrapperSecond.setVisibility(View.GONE);

		searchParamSpecificButton.setChecked(false);
		searchParamSelectFavoritesButton.setChecked(false);
		searchParamSelectUserDefButton.setChecked(false);

		// resetUserDefined(); TODO

		searchParamOnlyFreeRoomsButton.setChecked(true);
		// verify the submit
		searchLaunchValidateButton.setEnabled(searchAuditSubmit() == 0);

		searchTimeAdvUpToEndHourButton.setEnabled(true);
		searchTimeAdvUpToEndSelected = false;

		boolean enabled = false;
		searchParamSelectFavoritesButton.setEnabled(enabled);
		searchParamSelectUserDefButton.setEnabled(enabled);
		searchParamOnlyFreeRoomsButton.setEnabled(enabled);
		// show the buttons
		searchTimeUpdateAllPickersAndButtons();
		searchFillWithRequest(homeValidRequest(false));
	}

	@Override
	protected String screenName() {
		return "/freeroom/search";
	}

	/**
	 * {@link #search}: Updates ALL the date and time <code>PickerDialog</code>
	 * and related <code>Button</code>.
	 *
	 * <p>
	 * It updates the <code>Button</code> to summarize the date/time selected
	 * according to your language preferences. The <code>PickerDialog</code> is
	 * also updated: it's useful if the date/time has changed from somewhere
	 * else, the <code>PickerDialog</code> will reopen with the new value.
	 */
	private void searchTimeUpdateAllPickersAndButtons() {
		searchTimeUpdateDatePickerAndButton();
		searchTimeUpdateStartTimePickerAndButton();
		searchTimeUpdateEndTimePickerAndButton();
		searchTimeUpdateEnabledButtons();
		searchLaunchValidateButton.setEnabled(searchAuditSubmit() == 0);
	}

	/**
	 * {@link #search}: checks if advanced time edition button should be
	 * disabled or not.
	 */
	private void searchTimeUpdateEnabledButtons() {
		searchTimeAdvUpStartHourButton.setEnabled(startHourSelected <= Constants.LAST_HOUR_CHECK - 2);
		searchTimeAdvDownStartHourButton.setEnabled(startHourSelected > Constants.FIRST_HOUR_CHECK);

		searchTimeAdvUpEndHourButton.setEnabled(endHourSelected < Constants.LAST_HOUR_CHECK);
		searchTimeAdvDownEndHourButton.setEnabled(endHourSelected >= Constants.FIRST_HOUR_CHECK + 2);
		searchTimeAdvUpToEndHourButton.setEnabled(!searchTimeAdvUpToEndSelected);
	}

	/**
	 * {@link #search}: Updates the date <code>PickerDialog</code> and related
	 * <code>Button</code>.
	 *
	 * <p>
	 * It updates the <code>Button</code> to summarize the date selected
	 * according to your language preferences. The <code>PickerDialog</code> is
	 * also updated: it's useful if the date has changed from somewhere else,
	 * the <code>PickerDialog</code> will reopen with the new value.
	 *
	 */
	private void searchTimeUpdateDatePickerAndButton() {
		// creating selected time
		Calendar selected = Calendar.getInstance();
		selected.setTimeInMillis(searchLaunchPreparePeriod().getTimeStampStart());
		searchTimeDatePicker.setText(times.formatFullDate(selected));

		searchTimeDatePickerDialog.updateDate(yearSelected, monthSelected, dayOfMonthSelected);
	}

	/**
	 * {@link #search}: Updates the START time <code>PickerDialog</code> and
	 * related <code>Button</code>.
	 *
	 * <p>
	 * It updates the <code>Button</code> to summarize the START time selected
	 * according to your language preferences. The <code>PickerDialog</code> is
	 * also updated: it's useful if the START time has changed from somewhere
	 * else, the <code>PickerDialog</code> will reopen with the new value.
	 */
	private void searchTimeUpdateStartTimePickerAndButton() {
		searchTimeStartShortPicker.setText(times.formatTime(searchLaunchPreparePeriod().getTimeStampStart(), true));
		searchTimeStartPicker.setText(times.generateTimeSummaryWithPrefix(getString(R.string.freeroom_selectstartHour),
				true, times.formatTime(searchLaunchPreparePeriod().getTimeStampStart(), false)));
		searchTimePickerStartDialog.updateTime(startHourSelected, startMinSelected);
	}

	/**
	 * {@link #search}: Updates the END time <code>PickerDialog</code> and
	 * related <code>Button</code>.
	 *
	 * <p>
	 * It updates the <code>Button</code> to summarize the END time selected
	 * according to your language preferences. The <code>PickerDialog</code> is
	 * also updated: it's useful if the END time has changed from somewhere
	 * else, the <code>PickerDialog</code> will reopen with the new value.
	 */
	private void searchTimeUpdateEndTimePickerAndButton() {
		searchTimeEndShortPicker.setText(times.formatTime(searchLaunchPreparePeriod().getTimeStampEnd(), true));
		searchTimeEndPicker.setText(times.generateTimeSummaryWithPrefix(getString(R.string.freeroom_selectendHour),
				true, times.formatTime(searchLaunchPreparePeriod().getTimeStampEnd(), false)));
		if (endHourSelected >= Constants.LAST_HOUR_CHECK
				|| (endHourSelected == Constants.LAST_HOUR_CHECK - 1 && endMinSelected != 0)) {
			searchTimeAdvUpEndHourButton.setEnabled(false);
		} else {
			searchTimeAdvUpEndHourButton.setEnabled(true);
		}
		searchTimePickerEndDialog.updateTime(endHourSelected, endMinSelected);
	}

	/**
	 * {@link #addSearchRoom}: init the {@link #addSearchRoom} to add rooms to
	 * search for.
	 */
	private void initAddSearchRoomDialog() {
		// Instantiate an AlertDialog.Builder with its constructor
		AlertDialog.Builder builder = new AlertDialog.Builder(this);
		builder.setTitle(getString(R.string.freeroom_dialog_add_room_title));
		builder.setIcon(R.drawable.freeroom_ic_action_new);

		// Get the AlertDialog from create()
		addSearchRoom = builder.create();

		// redefine paramaters to dim screen when displayed
		WindowManager.LayoutParams lp = addSearchRoom.getWindow().getAttributes();
		lp.dimAmount = 0.60f;
		// these doesn't work
		lp.width = LayoutParams.FILL_PARENT;
		lp.height = LayoutParams.WRAP_CONTENT;
		addSearchRoom.getWindow().addFlags(WindowManager.LayoutParams.FLAG_WATCH_OUTSIDE_TOUCH);
		addSearchRoom.getWindow().addFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND);
		addSearchRoom.getWindow().setAttributes(lp);

		addSearchRoomView = getLayoutInflater().inflate(R.layout.freeroom_layout_dialog_add_room, null);

		addSearchRoom.setView(addSearchRoomView);

		addSearchRoomAutoCompleteStatusTextView = (TextView) addSearchRoomView
				.findViewById(R.id.freeroom_layout_dialog_add_room_status);

		addSearchRoom.setOnShowListener(new OnShowListener() {
			@Override
			public void onShow(DialogInterface dialog) {
				trackEvent("AddSearch", null);
			}
		});

		addSearchRoom.setOnDismissListener(new OnDismissListener() {
			@Override
			public void onDismiss(DialogInterface dialog) {
				autoCompleteCancel();
				searchParamSelectedRoomsTextViewSearchMenu.setText(u
						.getSummaryTextFromCollection(addSearchRoomSelectedRooms));
				searchLaunchValidateButton.setEnabled(searchAuditSubmit() == 0);
				addSearchRoomAutoCompleteInputBarElement.setInputText("");
				// addFavoritesAutoCompleteInputBarElement.setInputText("");

				commonDismissSoftKeyBoard(addSearchRoomView);
				editSearchRoom.dismiss();
				addSearchRoom.dismiss();
			}
		});

		Button bt_done = (Button) addSearchRoomView.findViewById(R.id.freeroom_layout_dialog_add_room_done);
		bt_done.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				commonDismissSoftKeyBoard(v);
				editSearchRoom.dismiss();
				addSearchRoom.dismiss();
			}
		});

		Button bt_edit = (Button) addSearchRoomView.findViewById(R.id.freeroom_layout_dialog_add_room_edit);
		bt_edit.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				commonDismissSoftKeyBoard(v);
				// we dont dismiss, we hide, such that the text is kept
				// if it's dismissed by other method, the text will be reset
				addSearchRoom.hide();
				editSearchRoom.show();
			}
		});

		addSearchRoomUIConstructInputBar();
		LinearLayout ll = (LinearLayout) addSearchRoomView.findViewById(R.id.freeroom_layout_dialog_add_layout_main);
		ll.addView(addSearchRoomAutoCompleteInputBarElement);
		addSearchRoomCreateSuggestionsList();
	}

	/**
	 * {@link #addSearchRoom}: construct the UI.
	 */
	private void addSearchRoomUIConstructInputBar() {
		addSearchRoomAutoCompleteInputBarElement = new InputBarElement(this, null,
				getString(R.string.freeroom_check_occupancy_search_inputbarhint));
		addSearchRoomAutoCompleteInputBarElement.setImeOptions(EditorInfo.IME_ACTION_SEARCH);

		// click on magnify glass on the keyboard
		addSearchRoomAutoCompleteInputBarElement.setOnEditorActionListener(new OnEditorActionListener() {
			@Override
			public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
				if (actionId == EditorInfo.IME_ACTION_SEARCH) {
					String query = addSearchRoomAutoCompleteInputBarElement.getInputText();
					autoCompleteValidateQuery(query, v);
				}

				return true;
			}
		});

		// click on BUTTON magnify glass on the inputbar
		addSearchRoomAutoCompleteInputBarElement.setOnButtonClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				String query = addSearchRoomAutoCompleteInputBarElement.getInputText();
				autoCompleteValidateQuery(query, v);
			}
		});

		addSearchRoomSuggestionAdapter = new FRRoomSuggestionArrayAdapter<FRRoom>(getApplicationContext(),
				R.layout.freeroom_layout_list_room_add_room, R.id.freeroom_layout_list_room_add_room,
				addSearchRoomAutoCompleteArrayListFRRoom, mModel, false);

		addSearchRoomAutoCompleteInputBarElement.setOnKeyPressedListener(new OnKeyPressedListener() {
			@Override
			public void onKeyPressed(String text) {
				addSearchRoomAutoCompleteListView.setAdapter(addSearchRoomSuggestionAdapter);

				cancelAutoCompleteBuildingTask();
				if (!u.validQuery(text)) {
					addSearchRoomAutoCompleteInputBarElement.setButtonText(null);
					autoCompleteCancel();
				} else {
					addSearchRoomAutoCompleteInputBarElement.setButtonText("");
					// autocomplete is scheduled if nothing happen for a
					// certain delay
					scheduleAutoCompleteBuidlingTask(text);
				}
			}
		});
	}

	/**
	 * Timer that handles autocomplete task and scheduling.
	 */
	private Timer autocompleteTimer = new Timer("AutocompleteTimer");
	/**
	 * Reference to last used autocomplete task.
	 */
	private TimerTask autocompleteTimerTask;
	/**
	 * Auto-launch of auto-complete delay in ms.
	 */
	private final long timerDelay = 600;

	/**
	 * Cancel a scheduled autocomplete task.
	 */
	private void cancelAutoCompleteBuildingTask() {
		if (autocompleteTimerTask != null) {
			autocompleteTimerTask.cancel();
		}
	}

	/**
	 * Schedule a new autocomplete task (and cancel the previous instance if
	 * exists)
	 *
	 * @param text
	 */
	private void scheduleAutoCompleteBuidlingTask(String text) {
		cancelAutoCompleteBuildingTask();
		autocompleteTimerTask = getAutoCompleteTaskFromText(text);
		autocompleteTimer.schedule(autocompleteTimerTask, timerDelay);
	}

	/**
	 * Construct a timertask for autocomplete building purposes.
	 *
	 * @param text
	 * @return
	 */
	private TimerTask getAutoCompleteTaskFromText(final String text) {
		final IFreeRoomView view = this;

		TimerTask task = new TimerTask() {
			@Override
			public void run() {
				FRAutoCompleteRequest request = new FRAutoCompleteRequest(text, mModel.getGroupAccess());
				mController.autoCompleteBuildingForScheduledCall(view, request);
			}
		};

		return task;
	}

	/**
	 * {@link #addSearchRoom}: Initialize the autocomplete suggestion list
	 */
	private void addSearchRoomCreateSuggestionsList() {
		addSearchRoomAutoCompleteListView = new ListView(this);
		addSearchRoomAutoCompleteInputBarElement.addView(addSearchRoomAutoCompleteListView);

		addSearchRoomAutoCompleteListView.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> adapter, View view, int pos, long id) {
				// when an item is clicked, the keyboard is dimissed
				commonDismissSoftKeyBoard(view);
				FRRoom room = addSearchRoomAutoCompleteArrayListFRRoom.get(pos);
				addSearchRoomAddNewRoomToCheck(room);
				searchLaunchValidateButton.setEnabled(searchAuditSubmit() == 0);

				// WE DONT REMOVE the text in the input bar
				// INTENTIONNALLY: user may want to select multiple
				// rooms in the same building

				// actionRefresh the autocomplete, such that selected
				// rooms are not displayed anymore
				autoCompleteUpdated();

			}
		});
		addSearchRoomAutoCompleteListView.setAdapter(addSearchRoomSuggestionAdapter);
	}

	/**
	 * {@link #addSearchRoom}: add a room to the search selections and updates
	 * the summary.
	 *
	 * @param room
	 *            the room to ass
	 */
	private void addSearchRoomAddNewRoomToCheck(FRRoom room) {
		// we only add if it already contains the room
		if (!addSearchRoomSelectedRooms.contains(room)) {
			addSearchRoomSelectedRooms.add(room);
			searchParamSelectedRoomsTextViewSearchMenu.setText(u
					.getSummaryTextFromCollection(addSearchRoomSelectedRooms));
		} else {
			Log.e(this.getClass().toString(), "room cannot be added: already added");
		}
	}

	// EDIT SEARCH ROOM

	/**
	 * {@link #editSearchRoom}: AlertDialog that holds the
	 * {@link #editSearchRoom} dialog.
	 */
	private AlertDialog editSearchRoom;
	/**
	 * {@link #editSearchRoom}: View that holds the {@link #editSearchRoom}
	 * dialog content, defined in xml in layout folder.
	 */
	private View editSearchRoomView;
	/**
	 * {@link #editSearchRoom}: listView for the selected room.
	 */
	private ListView editSearchRoomSelectedListView;

	/**
	 * {@link #editSearchRoom}: inits a {@link #editSearchRoom} to edit the
	 * selection of rooms.
	 */
	private void initEditSearchRoomDialog() {
		// Instantiate an AlertDialog.Builder with its constructor
		AlertDialog.Builder builder = new AlertDialog.Builder(this);
		builder.setTitle(getString(R.string.freeroom_dialog_edit_room_title));
		builder.setIcon(R.drawable.freeroom_ic_action_edit_white);

		// Get the AlertDialog from create()
		editSearchRoom = builder.create();

		// redefine paramaters to dim screen when displayed
		WindowManager.LayoutParams lp = editSearchRoom.getWindow().getAttributes();
		lp.dimAmount = 0.60f;
		// these doesn't work
		lp.width = LayoutParams.FILL_PARENT;
		lp.height = LayoutParams.WRAP_CONTENT;
		editSearchRoom.getWindow().addFlags(WindowManager.LayoutParams.FLAG_WATCH_OUTSIDE_TOUCH);
		editSearchRoom.getWindow().addFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND);
		editSearchRoom.getWindow().setAttributes(lp);

		editSearchRoomView = getLayoutInflater().inflate(R.layout.freeroom_layout_dialog_edit_room, null);
		// these work perfectly

		editSearchRoom.setView(editSearchRoomView);

		editSearchRoomSelectedListView = (ListView) editSearchRoomView
				.findViewById(R.id.freeroom_layout_dialog_edit_room_list);
		addSearchRoomSelectedRoomArrayAdapter = new FRRoomRemoveArrayAdapter<FRRoom>(getApplicationContext(),
				R.layout.freeroom_layout_room_edit, R.id.freeroom_layout_selected_text, addSearchRoomSelectedRooms);
		editSearchRoomSelectedListView.setAdapter(addSearchRoomSelectedRoomArrayAdapter);
		editSearchRoomSelectedListView.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
				addSearchRoomSelectedRoomArrayAdapter.remove(addSearchRoomSelectedRoomArrayAdapter.getItem(arg2));
			}
		});
		editSearchRoomSelectedListView.refreshDrawableState();

		editSearchRoom.setOnDismissListener(new OnDismissListener() {
			@Override
			public void onDismiss(DialogInterface arg0) {
				commonDismissSoftKeyBoard(editSearchRoomView);
				editSearchRoom.dismiss();
				addSearchRoom.dismiss();
			}
		});

		editSearchRoom.setOnShowListener(new OnShowListener() {
			@Override
			public void onShow(DialogInterface dialog) {
				addSearchRoomSelectedRoomArrayAdapter.notifyDataSetChanged();
				trackEvent("EditSearch", null);
			}
		});

		Button bt_done = (Button) editSearchRoomView.findViewById(R.id.freeroom_layout_dialog_edit_room_done);
		bt_done.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				commonDismissSoftKeyBoard(v);
				editSearchRoom.dismiss();
				addSearchRoom.dismiss();
			}
		});

		Button bt_more = (Button) editSearchRoomView.findViewById(R.id.freeroom_layout_dialog_edit_room_add);
		bt_more.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// we DONT dismiss, only hide! (no trigger or dismisslistener)
				editSearchRoom.hide();
				addSearchRoom.show();
			}
		});
	}

	/**
	 * {@link #editSearchRoom}: When a Selected Room item is clicked on
	 * "remove".
	 *
	 * @param position
	 *            position of the item to remove
	 */
	public void editSearchOnRemoveRoomClickListener(int position) {
		// from time to time cause an issue, this gets black hole in the list.
		// fixed by setting the whole line clickable to remove the line
		addSearchRoomSelectedRoomArrayAdapter.remove(addSearchRoomSelectedRoomArrayAdapter.getItem(position));
		editSearchRoomSelectedListView.refreshDrawableState();
	}

	/**
	 * {@link #search}: UI init (main).
	 */
	private void initSearchUIMain() {

		searchSelectOptionalLineLinearLayoutWrapperFirst = (LinearLayout) findViewById(R.id.freeroom_layout_dialog_search_opt_line_wrapper_1st);
		searchSelectOptionalLineLinearLayoutWrapperSecond = (LinearLayout) findViewById(R.id.freeroom_layout_dialog_search_opt_line_wrapper_2nd);

		addSearchRoomSelectedRooms = new SetArrayList<FRRoom>();

		addSearchRoomAutoCompleteArrayListFRRoom = new ArrayList<FRRoom>(10);
		searchResetTimes();

		initSearchUIConstructPickers();

		initSearchUIConstructButton();

		searchResetMain();
	}

	/**
	 * {@link #search}: UI initialization (pickers)
	 */
	private void initSearchUIConstructPickers() {
		// First allow the user to select a date, but don't display the date
		// button if the user don't have the right to use it.
		searchTimeDatePicker = (Button) findViewById(R.id.freeroom_layout_dialog_search_date);
		if (!mModel.getAdvancedTime()) {
			searchTimeDatePicker.setVisibility(View.GONE);
		}

		searchTimeDatePickerDialog = new DatePickerDialog(this, new DatePickerDialog.OnDateSetListener() {

			@Override
			public void onDateSet(DatePicker view, int nYear, int nMonthOfYear, int nDayOfMonth) {
				yearSelected = nYear;
				monthSelected = nMonthOfYear;
				dayOfMonthSelected = nDayOfMonth;
				searchTimeUpdateAllPickersAndButtons();
			}
		}, yearSelected, monthSelected, dayOfMonthSelected);

		// the click listener is always there, even if the button is not
		// visible.
		searchTimeDatePicker.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				searchTimeDatePickerDialog.show();
			}
		});

		// Then the starting time of the period
		searchTimeStartPicker = (Button) findViewById(R.id.freeroom_layout_dialog_search_hour_start);
		searchTimeStartShortPicker = (Button) findViewById(R.id.freeroom_layout_dialog_search_hour_start_short);
		searchTimePickerStartDialog = new TimePickerDialog(this, new OnTimeSetListener() {

			@Override
			public void onTimeSet(TimePicker view, int nHourOfDay, int nMinute) {
				int previous = startHourSelected;
				startHourSelected = nHourOfDay;
				startMinSelected = nMinute;
				if (startHourSelected < Constants.FIRST_HOUR_CHECK) {
					startHourSelected = Constants.FIRST_HOUR_CHECK;
					startMinSelected = 0;
				}
				if (startHourSelected >= Constants.LAST_HOUR_CHECK) {
					startHourSelected = Constants.LAST_HOUR_CHECK - 1;
					startMinSelected = 0;
				}
				if (startHourSelected != -1 && !searchTimeAdvUpToEndSelected) {
					int shift = startHourSelected - previous;
					int newEndHour = endHourSelected + shift;
					if (newEndHour > Constants.LAST_HOUR_CHECK) {
						newEndHour = Constants.LAST_HOUR_CHECK;
					}
					if (newEndHour < Constants.FIRST_HOUR_CHECK) {
						newEndHour = Constants.FIRST_HOUR_CHECK + 1;
					}
					endHourSelected = newEndHour;
					if (endHourSelected == Constants.LAST_HOUR_CHECK) {
						endMinSelected = 0;
					}
					searchTimeUpdateEndTimePickerAndButton();
				}
				searchTimeUpdateAllPickersAndButtons();

			}
		}, startHourSelected, startMinSelected, true);

		OnClickListener ocl_start = new OnClickListener() {

			@Override
			public void onClick(View v) {
				searchTimePickerStartDialog.show();
			}
		};
		searchTimeStartPicker.setOnClickListener(ocl_start);
		searchTimeStartShortPicker.setOnClickListener(ocl_start);

		// Then the ending time of the period
		searchTimeEndPicker = (Button) findViewById(R.id.freeroom_layout_dialog_search_hour_end);
		searchTimeEndShortPicker = (Button) findViewById(R.id.freeroom_layout_dialog_search_hour_end_short);
		searchTimePickerEndDialog = new TimePickerDialog(this, new OnTimeSetListener() {

			@Override
			public void onTimeSet(TimePicker view, int nHourOfDay, int nMinute) {
				endHourSelected = nHourOfDay;
				endMinSelected = nMinute;
				if (endHourSelected < startHourSelected) {
					endHourSelected = startHourSelected + 1;
				}
				if (endHourSelected < Constants.FIRST_HOUR_CHECK) {
					endHourSelected = Constants.FIRST_HOUR_CHECK + 1;
					endMinSelected = 0;
				}
				if (endHourSelected == Constants.FIRST_HOUR_CHECK
						&& (endMinSelected - startMinSelected) <= Constants.MIN_MINUTE_INTERVAL) {
					endMinSelected = startMinSelected + Constants.MIN_MINUTE_INTERVAL;
					if (endMinSelected >= 60) {
						endMinSelected = 0;
						endHourSelected += 1;
						startMinSelected = 60 - Constants.MIN_MINUTE_INTERVAL;
					}
				}

				if (endHourSelected >= Constants.LAST_HOUR_CHECK) {
					endHourSelected = Constants.LAST_HOUR_CHECK;
					endMinSelected = 0;
				}
				if (endHourSelected != Constants.LAST_HOUR_CHECK) {
					searchTimeAdvUpToEndSelected = false;
					searchTimeAdvUpToEndHourButton.setEnabled(!searchTimeAdvUpToEndSelected);
				}
				searchTimeUpdateAllPickersAndButtons();
			}
		}, endHourSelected, endMinSelected, true);

		OnClickListener ocl_end = new OnClickListener() {

			@Override
			public void onClick(View v) {
				searchTimePickerEndDialog.show();
			}
		};
		searchTimeEndPicker.setOnClickListener(ocl_end);
		searchTimeEndShortPicker.setOnClickListener(ocl_end);
	}

	/**
	 * {@link #search}: UI initialization (buttons)
	 */
	private void initSearchUIConstructButton() {
		searchParamSpecificButton = (RadioButton) findViewById(R.id.freeroom_layout_dialog_search_spec);
		searchParamSpecificButton.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				if (searchParamSpecificButton.isChecked()) {
					searchSelectOptionalLineLinearLayoutWrapperFirst.setVisibility(View.VISIBLE);

				}
				searchParamSpecificButton.setChecked(true);
				searchParamAnyFreeRoomButton.setChecked(false);
				searchParamAnyFreeRoomButton.setEnabled(true);
				searchParamSpecificButton.setChecked(true);
				searchParamOnlyFreeRoomsButton.setChecked(false);

				boolean enabled = true;
				searchParamSelectFavoritesButton.setEnabled(enabled);
				searchParamSelectUserDefButton.setEnabled(enabled);
				searchParamOnlyFreeRoomsButton.setEnabled(enabled);

				// if you don't have favs, ask you to enter some rooms
				// if you have favs, auto-select it, ... but it requires two
				// steps to remove the fav (add user-def, remove fav)
				if (mModel.getFavorites().isEmpty()) {
					searchSelectOptionalLineLinearLayoutWrapperSecond.setVisibility(View.VISIBLE);

					searchParamSelectUserDefButton.setChecked(true);
					addSearchRoom.show();
					// as it's user-defined, we dont check for search
					// button
					// enabled now
					searchLaunchValidateButton.setEnabled(false);
				} else {
					searchParamSelectFavoritesButton.setChecked(true);
					searchLaunchValidateButton.setEnabled(searchAuditSubmit() == 0);
				}
			}
		});

		searchParamAnyFreeRoomButton = (RadioButton) findViewById(R.id.freeroom_layout_dialog_search_any);
		searchParamAnyFreeRoomButton.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				if (searchParamAnyFreeRoomButton.isChecked()) {
					searchSelectOptionalLineLinearLayoutWrapperFirst.setVisibility(View.GONE);
					searchSelectOptionalLineLinearLayoutWrapperSecond.setVisibility(View.GONE);
				}
				searchParamSpecificButton.setChecked(false);
				searchSelectUserDefReset(false);

				boolean enabled = false;
				searchParamSelectFavoritesButton.setEnabled(enabled);
				searchParamSelectUserDefButton.setEnabled(enabled);
				searchParamOnlyFreeRoomsButton.setEnabled(enabled);

				searchParamSelectFavoritesButton.setChecked(false);
				searchParamSelectUserDefButton.setChecked(false);
				searchParamOnlyFreeRoomsButton.setChecked(true);
				searchParamAnyFreeRoomButton.setChecked(true);
				searchLaunchValidateButton.setEnabled(searchAuditSubmit() == 0);
			}
		});

		searchParamSelectFavoritesButton = (CheckBox) findViewById(R.id.freeroom_layout_dialog_search_fav);
		searchParamSelectFavoritesButton.setEnabled(true);
		searchParamSelectFavoritesButton.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				if (!searchParamSelectUserDefButton.isChecked()) {
					searchParamSelectFavoritesButton.setChecked(true);
				}
				searchParamAnyFreeRoomButton.setChecked(false);
				searchParamSpecificButton.setChecked(true);
				searchLaunchValidateButton.setEnabled(searchAuditSubmit() == 0);
			}
		});

		searchParamSelectUserDefButton = (CheckBox) findViewById(R.id.freeroom_layout_dialog_search_user);
		searchParamSelectUserDefButton.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				if (searchParamSelectUserDefButton.isChecked() || !searchParamSelectFavoritesButton.isChecked()) {
					if (searchParamSelectUserDefButton.isChecked()) {
						searchSelectUserDefReset(false);
					}
					searchParamSelectUserDefButton.setChecked(true);

					searchParamAnyFreeRoomButton.setChecked(false);
					searchParamSpecificButton.setChecked(true);
					searchParamOnlyFreeRoomsButton.setChecked(false);

					searchSelectOptionalLineLinearLayoutWrapperSecond.setVisibility(View.VISIBLE);

					searchParamSelectedRoomsTextViewSearchMenu.setText(u
							.getSummaryTextFromCollection(addSearchRoomSelectedRooms));
					addSearchRoom.show();
					searchLaunchValidateButton.setEnabled(false);
				} else {
					searchSelectUserDefReset(false);
					searchSelectOptionalLineLinearLayoutWrapperSecond.setVisibility(View.GONE);
					searchLaunchValidateButton.setEnabled(searchAuditSubmit() == 0);
				}
			}
		});

		searchParamOnlyFreeRoomsButton = (CheckBox) findViewById(R.id.freeroom_layout_dialog_search_non_free);
		searchParamOnlyFreeRoomsButton.setEnabled(true);
		searchParamOnlyFreeRoomsButton.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				if (!searchParamOnlyFreeRoomsButton.isChecked()) {
					searchParamAnyFreeRoomButton.setChecked(false);
					searchParamSpecificButton.setChecked(true);
				}
				searchLaunchValidateButton.setEnabled(searchAuditSubmit() == 0);
			}
		});

		searchLaunchValidateButton.setEnabled(searchAuditSubmit() == 0);
		searchLaunchValidateButton.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				searchLaunchPrepareSearchQuery(true);
			}
		});

		searchLaunchResetButton.setEnabled(true);
		searchLaunchResetButton.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				searchResetMain();
			}
		});

		searchTimeAdvDownToStartHourButton = (ImageButton) findViewById(R.id.freeroom_layout_dialog_search_hour_start_tostart);
		searchTimeAdvDownToStartHourButton.setEnabled(true);
		searchTimeAdvDownToStartHourButton.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				startMinSelected = 0;
				int shift = startHourSelected - Constants.FIRST_HOUR_CHECK;
				startHourSelected = Constants.FIRST_HOUR_CHECK;
				if (!searchTimeAdvUpToEndSelected && shift > 0) {
					endHourSelected -= shift;
				}
				searchTimeUpdateAllPickersAndButtons();
			}
		});

		searchTimeAdvDownStartHourButton = (ImageButton) findViewById(R.id.freeroom_layout_dialog_search_hour_start_minus);
		searchTimeAdvDownStartHourButton.setEnabled(true);
		searchTimeAdvDownStartHourButton.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				if (startHourSelected >= Constants.FIRST_HOUR_CHECK - 1) {
					startHourSelected -= 1;
					if (!searchTimeAdvUpToEndSelected) {
						endHourSelected -= 1;
					}
				}
				searchTimeUpdateAllPickersAndButtons();
			}
		});

		searchTimeAdvUpStartHourButton = (ImageButton) findViewById(R.id.freeroom_layout_dialog_search_hour_start_plus);
		searchTimeAdvUpStartHourButton.setEnabled(true);
		searchTimeAdvUpStartHourButton.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				if (startHourSelected <= Constants.LAST_HOUR_CHECK - 2) {
					startHourSelected += 1;
					if (!searchTimeAdvUpToEndSelected) {
						endHourSelected = Math.min(endHourSelected + 1, Constants.LAST_HOUR_CHECK);
					}
				}
				searchTimeUpdateAllPickersAndButtons();
			}
		});

		searchTimeAdvDownEndHourButton = (ImageButton) findViewById(R.id.freeroom_layout_dialog_search_hour_end_minus);
		searchTimeAdvDownEndHourButton.setEnabled(true);
		searchTimeAdvDownEndHourButton.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				if (endHourSelected >= Constants.FIRST_HOUR_CHECK + 2) {
					endHourSelected -= 1;
					if (startHourSelected >= endHourSelected) {
						startHourSelected -= 1;
					}
				}
				searchTimeAdvUpToEndSelected = false;
				searchTimeUpdateAllPickersAndButtons();
			}
		});

		searchTimeAdvUpEndHourButton = (ImageButton) findViewById(R.id.freeroom_layout_dialog_search_hour_end_plus);
		searchTimeAdvUpEndHourButton.setEnabled(true);
		searchTimeAdvUpEndHourButton.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				if (endHourSelected <= Constants.LAST_HOUR_CHECK - 1) {
					endHourSelected += 1;
				}
				if (endHourSelected == Constants.LAST_HOUR_CHECK) {
					endMinSelected = 0;
				}
				searchTimeAdvUpToEndSelected = false;
				searchTimeUpdateAllPickersAndButtons();
			}
		});

		searchTimeAdvUpToEndHourButton = (ImageButton) findViewById(R.id.freeroom_layout_dialog_search_hour_end_toend);
		searchTimeAdvUpToEndHourButton.setEnabled(true);
		searchTimeAdvUpToEndHourButton.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				endHourSelected = Constants.LAST_HOUR_CHECK;
				endMinSelected = 0;
				searchTimeAdvUpToEndSelected = true;
				searchTimeUpdateAllPickersAndButtons();
			}
		});

		searchParamChangeUserDefAddButton = (Button) findViewById(R.id.freeroom_layout_dialog_search_user_add);
		searchParamChangeUserDefAddButton.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				addSearchRoom.show();
			}
		});

		searchParamChangeUserDefEditButton = (Button) findViewById(R.id.freeroom_layout_dialog_search_user_edit);
		searchParamChangeUserDefEditButton.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				editSearchRoom.show();
			}
		});

		searchParamChangeUserDefResetButton = (Button) findViewById(R.id.freeroom_layout_dialog_search_user_reset);
		searchParamChangeUserDefResetButton.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				searchSelectUserDefReset(false);
			}
		});

		// for landscape device, mainly tablet, some layout are programmatically
		// changed to horizontal values, and weighted more logically.
		// XML IS ALWAYS DESIGNED FOR PHONES, as it's probably more than 97% of
		// users. tablets are changing their layout here.
		LinearLayout header_main = (LinearLayout) findViewById(R.id.freeroom_layout_dialog_search_upper_main);
		LinearLayout header_1st = (LinearLayout) findViewById(R.id.freeroom_layout_dialog_search_upper_first);
		LinearLayout header_2nd = (LinearLayout) findViewById(R.id.freeroom_layout_dialog_search_upper_second);
		LinearLayout header_3rd = (LinearLayout) findViewById(R.id.freeroom_layout_dialog_search_upper_third);

		if (mModel.getAdvancedTime()) {
			header_1st.setVisibility(View.VISIBLE);
		} else {
			header_1st.setVisibility(View.GONE);
		}

		TimePickersPref pickers = mModel.getTimePickersPref();
		if (pickers.equals(TimePickersPref.PICKERS)) {
			header_2nd.setVisibility(View.VISIBLE);
			header_3rd.setVisibility(View.GONE);
		} else if (pickers.equals(TimePickersPref.ARROWS)) {
			header_2nd.setVisibility(View.GONE);
			header_3rd.setVisibility(View.VISIBLE);
		} else if (pickers.equals(TimePickersPref.BOTH)) {
			header_2nd.setVisibility(View.VISIBLE);
			header_3rd.setVisibility(View.VISIBLE);
		}
	}

	/**
	 * {@link #search}: RESET the user-defined date (the list of selected
	 * rooms).
	 *
	 * @param itsTheEnd
	 *            true if the request is already send, avoid to recheck the
	 *            validity.
	 */
	private void searchSelectUserDefReset(boolean itsTheEnd) {
		addSearchRoomSelectedRooms.clear();

		searchParamSelectedRoomsTextViewSearchMenu.setText(u.getSummaryTextFromCollection(addSearchRoomSelectedRooms));
		addSearchRoomAutoCompleteInputBarElement.setInputText("");

		if (!itsTheEnd) {
			searchLaunchValidateButton.setEnabled(searchAuditSubmit() == 0);
		}
	}

	/**
	 * {@link #search}: DONT CALL IT! <br>
	 * Call {@link #searchResetMain()} instead.
	 *
	 * @param request
	 */
	private void searchFillWithRequest(final FRRequestDetails request) {
		searchResetTimes(request.getPeriod());
		searchParamAnyFreeRoomButton.setChecked(request.isAny());
		searchParamSpecificButton.setChecked(!request.isAny());
		searchParamSelectFavoritesButton.setChecked(request.isFav());
		searchParamSelectUserDefButton.setChecked(request.isUser());
		searchParamOnlyFreeRoomsButton.setChecked(request.isOnlyFreeRooms());
		boolean enabled = !request.isAny();
		searchParamSelectFavoritesButton.setEnabled(enabled);
		searchParamSelectUserDefButton.setEnabled(enabled);
		searchParamOnlyFreeRoomsButton.setEnabled(enabled);
		searchSelectOptionalLineLinearLayoutWrapperFirst.setVisibility(View.GONE);
		if (enabled) {
			searchSelectOptionalLineLinearLayoutWrapperFirst.setVisibility(View.VISIBLE);
		}
		searchSelectOptionalLineLinearLayoutWrapperSecond.setVisibility(View.GONE);
		if (request.isUser()) {
			searchSelectOptionalLineLinearLayoutWrapperSecond.setVisibility(View.VISIBLE);
			addSearchRoomSelectedRooms.addAll(request.getUidNonFav());
			searchParamSelectedRoomsTextViewSearchMenu.setText(u
					.getSummaryTextFromCollection(addSearchRoomSelectedRooms));
		}
		searchTimeUpdateAllPickersAndButtons();

		// MUST be the last action: after all field are set, check if the
		// request is valid
		searchLaunchValidateButton.setEnabled(searchAuditSubmit() == 0);
	}

	/**
	 * {@link #search}: Reset the year, month, day, hour_start, minute_start,
	 * hour_end, minute_end to their initial values.
	 * <p>
	 * DONT forget to update the date/time pickers afterwards.
	 */
	private void searchResetTimes() {
		FRPeriod mFrPeriod = FRTimes.getNextValidPeriod();
		searchResetTimes(mFrPeriod);
	}

	/**
	 * {@link #search}: RESET THE TIMES. (with the given period)
	 *
	 * @param mFrPeriod
	 *            period to set
	 */
	private void searchResetTimes(FRPeriod mFrPeriod) {
		// nextValid is today according to nextValidPeriod definition.
		Calendar nextValid = Calendar.getInstance();
		nextValid.setTimeInMillis(FRTimes.getNextValidPeriod().getTimeStampStart());
		Calendar mCalendar = Calendar.getInstance();
		mCalendar.setTimeInMillis(mFrPeriod.getTimeStampStart());
		yearSelected = nextValid.get(Calendar.YEAR);
		monthSelected = nextValid.get(Calendar.MONTH);
		dayOfMonthSelected = nextValid.get(Calendar.DAY_OF_MONTH);
		startHourSelected = mCalendar.get(Calendar.HOUR_OF_DAY);
		startMinSelected = mCalendar.get(Calendar.MINUTE);
		mCalendar.setTimeInMillis(mFrPeriod.getTimeStampEnd());
		endHourSelected = mCalendar.get(Calendar.HOUR_OF_DAY);
		endMinSelected = mCalendar.get(Calendar.MINUTE);
	}

	/**
	 * {@link #search}: Construct the <code>FRPeriod</code> object asscociated
	 * with the current selected times.
	 *
	 * @return
	 */
	private FRPeriod searchLaunchPreparePeriod() {
		Calendar start = Calendar.getInstance();
		start.set(yearSelected, monthSelected, dayOfMonthSelected, startHourSelected, startMinSelected, 0);
		start.set(Calendar.MILLISECOND, 0);

		Calendar end = Calendar.getInstance();
		end.set(yearSelected, monthSelected, dayOfMonthSelected, endHourSelected, endMinSelected, 0);
		end.set(Calendar.MILLISECOND, 0);

		// constructs the request
		return new FRPeriod(start.getTimeInMillis(), end.getTimeInMillis());
	}

	/**
	 * {@link #search}: Prepare the actual query to send and set it in the
	 * controller
	 *
	 * @param save
	 *            if the query should be saved in previous requests.
	 */
	private void searchLaunchPrepareSearchQuery(boolean save) {
		FRPeriod period = searchLaunchPreparePeriod();

		List<String> mUIDList = new ArrayList<String>(addSearchRoomSelectedRooms.size());

		if (searchParamSelectFavoritesButton.isChecked()) {
			addAllFavoriteToCollection(mUIDList, true);
		}
		Set<FRRoom> userDef = new HashSet<FRRoom>(addSearchRoomSelectedRooms.size());
		if (searchParamSelectUserDefButton.isChecked()) {
			Iterator<FRRoom> iter = addSearchRoomSelectedRooms.iterator();
			while (iter.hasNext()) {
				FRRoom room = iter.next();
				userDef.add(room);
				mUIDList.add(room.getUid());
			}
		}

		boolean any = searchParamAnyFreeRoomButton.isChecked();
		boolean fav = searchParamSelectFavoritesButton.isChecked();
		boolean user = searchParamSelectUserDefButton.isChecked();
		FRRequestDetails details = new FRRequestDetails(period, searchParamOnlyFreeRoomsButton.isChecked(), mUIDList,
				any, fav, user, userDef, mModel.getGroupAccess());
		mModel.setFRRequestDetails(details, save);
		searchPreviousRequestAdapter.notifyDataSetChanged();
		// commonReplayRefresh();
		// search.dismiss();

		searchSelectUserDefReset(true); // cleans the selectedRooms of
		// userDefined

		setResult(Activity.RESULT_OK);
		finish();
	}

	// PREVIOUS REQUEST MANAGEMENT //

	/**
	 * {@link #search}: When a Previous Request item is clicked on "play".
	 *
	 * @param position
	 *            position of the item to replay
	 */
	public void searchPreviousRequestRePlayClickListener(int position) {
		if (searchPreviousRequestReFillClickListeners(position)) {
			// request we will be stored
			// it will actually come at the first place and be deleted from it's
			// original place
			searchLaunchPrepareSearchQuery(true);
		}
	}

	/**
	 * {@link #search}: When a Previous Request item is clicked on "remove".
	 *
	 * @param position
	 *            position of the item to remove
	 */
	public void searchPreviousRequestRemoveClickListener(int position) {
		mModel.removeRequest(position);
		searchPreviousRequestAdapter.notifyDataSetChanged();
		if (mModel.getPreviousRequest().isEmpty()) {
			searchPreviousRequestInitTitle();
		}
	}

	// GENERAL SERVICE

	/**
	 * GENERAL: Generates a String summary of a FRRequestDetails with compatible
	 * time and collections.
	 *
	 * @param req
	 *            the request to summarize
	 * @return summary of the request
	 */
	public String searchPreviousFRRequestToString(FRRequestDetails req) {
		StringBuilder build = new StringBuilder(100);
		build.append(times.formatTimePeriod(req.getPeriod(), true, false));
		build.append(" ");
		// it's the max size of the collections of room. but the textview has a
		// maxlength of 2 lines anyway, so we dont really care...
		int max = 150;

		if (req.isAny()) {
			build.append(getString(R.string.freeroom_search_any));
		} else {
			if (req.isFav()) {
				build.append(getString(R.string.freeroom_search_favorites));
				build.append("; ");
			}
			if (req.isOnlyFreeRooms()) {
				build.append(getString(R.string.freeroom_search_only_free_short));
				build.append("; ");
			}
			if (req.isUser()) {
				if (req.getUidNonFav() != null) {
					build.append("[" + req.getUidNonFav().size() + "] : ");
					build.append(u.getSummaryTextFromCollection(req.getUidNonFav(), "", max));
				}
			}
		}
		return build.toString();
	}

	/**
	 * {@link #search}: Fill the search menus with the selected previous
	 * request.
	 *
	 * @param position
	 *            position of the item to use to refill.
	 */
	public boolean searchPreviousRequestReFillClickListeners(int position) {
		searchPreviousSearchesListView.smoothScrollToPosition(0);
		FRRequestDetails req = mModel.getPreviousRequest().get(position);
		if (req != null) {
			searchResetMain();
			searchFillWithRequest(req);
			return true;
		}
		return false;
	}

	//
	/**
	 * AUTOCOMPLETE: Update the text message in autocomplete status text view
	 * (updating/up-to-date/error/...)
	 * 
	 * @param text
	 *            the new message to display.
	 */
	protected void autoCompleteUpdateMessage(CharSequence text) {
		// addFavoritesAutoCompleteStatus.setText(text);
		addSearchRoomAutoCompleteStatusTextView.setText(text);
	}

	@Override
	protected void addAutocompletedRoom(FRRoom room) {
		if (!addSearchRoomSelectedRooms.contains(room)) {
			addSearchRoomAutoCompleteArrayListFRRoom.add(room);
		}
	}

	@Override
	protected void autocompleteCheckEmptyResult() {
		if (addSearchRoomAutoCompleteArrayListFRRoom.isEmpty()) {
			addSearchRoomAutoCompleteStatusTextView
					.setText(getString(R.string.freeroom_dialog_add_autocomplete_nomore));
		}
	}

	@Override
	protected void autocompleteFinished() {
		addSearchRoomSuggestionAdapter.notifyDataSetChanged();
	}

	@Override
	public void initializeView() {
		searchLaunchResetButton = (Button) findViewById(R.id.freeroom_layout_search_reset_button);
		searchLaunchValidateButton = (Button) findViewById(R.id.freeroom_layout_search_search_button);

		// display the previous searches
		searchPreviousSearchesListView = (ListView) findViewById(R.id.freeroom_layout_search_prev_search_list);
		searchPreviousRequestAdapter = new PreviousRequestArrayAdapter<FRRequestDetails>(this, this,
				R.layout.freeroom_layout_list_prev_req, R.id.freeroom_layout_prev_req_text, mModel.getPreviousRequest());

		searchPreviousSearchesListView.setAdapter(searchPreviousRequestAdapter);

		searchPreviousRequestTitleString = getString(R.string.freeroom_search_previous_search);
		searchPreviousRequestTitleTextView = (TextView) findViewById(R.id.freeroom_layout_dialog_search_prev_search_title);
		searchParamSelectedRoomsTextViewSearchMenu = (TextView) findViewById(R.id.freeroom_layout_dialog_search_text_summary);
		// the view will be removed or the text changed, no worry
		searchParamSelectedRoomsTextViewSearchMenu.setText(getString(R.string.freeroom_add_rooms_empty));

		initSearchUIMain();
	}
}
