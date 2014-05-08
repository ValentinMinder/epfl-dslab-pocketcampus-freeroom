package org.pocketcampus.plugin.freeroom.android.views;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
import java.util.Date;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import org.pocketcampus.android.platform.sdk.core.PluginController;
import org.pocketcampus.android.platform.sdk.tracker.Tracker;
import org.pocketcampus.android.platform.sdk.ui.element.InputBarElement;
import org.pocketcampus.android.platform.sdk.ui.element.OnKeyPressedListener;
import org.pocketcampus.android.platform.sdk.ui.layout.StandardTitledLayout;
import org.pocketcampus.android.platform.sdk.ui.list.LabeledListViewElement;
import org.pocketcampus.plugin.freeroom.R;
import org.pocketcampus.plugin.freeroom.android.FreeRoomAbstractView;
import org.pocketcampus.plugin.freeroom.android.FreeRoomController;
import org.pocketcampus.plugin.freeroom.android.FreeRoomModel;
import org.pocketcampus.plugin.freeroom.android.adapter.ActualOccupationArrayAdapter;
import org.pocketcampus.plugin.freeroom.android.adapter.ExpandableListViewAdapter;
import org.pocketcampus.plugin.freeroom.android.adapter.ExpandableListViewFavoriteAdapter;
import org.pocketcampus.plugin.freeroom.android.adapter.FRRoomSuggestionArrayAdapter;
import org.pocketcampus.plugin.freeroom.android.iface.IFreeRoomView;
import org.pocketcampus.plugin.freeroom.android.utils.FRRequestDetails;
import org.pocketcampus.plugin.freeroom.android.utils.SetArrayList;
import org.pocketcampus.plugin.freeroom.shared.ActualOccupation;
import org.pocketcampus.plugin.freeroom.shared.AutoCompleteRequest;
import org.pocketcampus.plugin.freeroom.shared.FRPeriod;
import org.pocketcampus.plugin.freeroom.shared.FRRequest;
import org.pocketcampus.plugin.freeroom.shared.FRRoom;
import org.pocketcampus.plugin.freeroom.shared.ImWorkingRequest;
import org.pocketcampus.plugin.freeroom.shared.Occupancy;
import org.pocketcampus.plugin.freeroom.shared.WorkingOccupancy;
import org.pocketcampus.plugin.freeroom.shared.utils.FRTimes;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.app.TimePickerDialog.OnTimeSetListener;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnShowListener;
import android.content.Intent;
import android.graphics.Rect;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.view.WindowManager;
import android.view.WindowManager.LayoutParams;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ExpandableListView;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RadioButton;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.TextView.OnEditorActionListener;
import android.widget.TimePicker;
import android.widget.Toast;

import com.markupartist.android.widget.ActionBar.Action;

/**
 * <code>FreeRoomHomeView</code> is the main <code>View</code>, it's the entry
 * of the plugin. It displays the availabilities for the search given, and for
 * your favorites NOW at the start.
 * <p>
 * All others views are supposed to be dialog windows, therefore it's always
 * visible.
 * 
 * @author FreeRoom Project Team (2014/05)
 * @author Julien WEBER <julien.weber@epfl.ch>
 * @author Valentin MINDER <valentin.minder@epfl.ch>
 */
public class FreeRoomHomeView extends FreeRoomAbstractView implements
		IFreeRoomView {

	/**
	 * FreeRoom controller in MVC scheme.
	 */
	private FreeRoomController mController;
	/**
	 * FreeRoom model in MVC scheme.
	 */
	private FreeRoomModel mModel;

	/**
	 * Titled layout that holds the title and the main layout.
	 */
	private StandardTitledLayout titleLayout;
	/**
	 * Main layout that hold all UI components.
	 */
	private LinearLayout mainLayout;

	/**
	 * TextView to display a short message about what is currently displayed.
	 */
	private TextView mTextView;
	/**
	 * ExpandableListView to display the results of occupancies building by
	 * building.
	 */
	private ExpandableListView mExpListView;

	/**
	 * Adapter for the results (to display the occupancies).
	 */
	private ExpandableListViewAdapter<Occupancy> mExpListAdapter;

	/**
	 * View that holds the INFO dialog content, defined in xml in layout folder.
	 */
	private View mInfoRoomView;
	/**
	 * AlertDialog that holds the INFO dialog.
	 */
	private AlertDialog mInfoRoomDialog;

	/**
	 * View that holds the SEARCH dialog content, defined in xml in layout
	 * folder.
	 */
	private View mSearchView;

	/**
	 * ListView that holds previous searches.
	 */
	private ListView searchPreviousListView;

	/**
	 * Dialog that holds the SEARCH Dialog.
	 */
	private AlertDialog searchDialog;

	/**
	 * View that holds the FAVORITES dialog content, defined in xml in layout
	 * folder.
	 */
	private View mFavoritesView;
	/**
	 * AlertDialog that holds the FAVORITES dialog.
	 */
	private AlertDialog mFavoritesDialog;

	/**
	 * View that holds the ADDROOM dialog content, defined in xml in layout
	 * folder.
	 */
	private View mAddRoomView;

	/**
	 * AlertDialog that holds the ADDROOM dialog.
	 */
	private AlertDialog mAddRoomDialog;

	/**
	 * View that holds the SHARE dialog content, defined in xml in layout folder.
	 */
	private View mShareView;
	/**
	 * Dialog that holds the SHARE Dialog.
	 */
	private AlertDialog mShareDialog;

	private int activityWidth;
	private int activityHeight;

	private LayoutInflater mLayoutInflater;

	/**
	 * Action to perform a customized search.
	 */
	private Action search = new Action() {
		public void performAction(View view) {
			fillSearchDialog();
			searchDialog.show();
		}

		public int getDrawable() {
			return R.drawable.magnify2x06;
		}
	};

	/**
	 * Action to edit the user's favorites.
	 */
	private Action editFavorites = new Action() {
		public void performAction(View view) {
			mAdapterFav.notifyDataSetChanged();
			mFavoritesDialog.show();
		}

		public int getDrawable() {
			return R.drawable.star2x28;
		}
	};

	/**
	 * Action to refresh the view (it sends the same stored request again).
	 * <p>
	 * TODO: useful? useless ? delete !
	 */
	private Action refresh = new Action() {
		public void performAction(View view) {
			refresh();
		}

		public int getDrawable() {
			return R.drawable.refresh2x01;
		}
	};

	@Override
	protected Class<? extends PluginController> getMainControllerClass() {
		return FreeRoomController.class;
	}

	@Override
	protected void onDisplay(Bundle savedInstanceState,
			PluginController controller) {
		// Tracker
		Tracker.getInstance().trackPageView("freeroom");

		// Get and cast the controller and model
		mController = (FreeRoomController) controller;
		mModel = (FreeRoomModel) controller.getModel();

		// Setup the layout

		LayoutInflater layoutInflater = (LayoutInflater) getBaseContext()
				.getSystemService(LAYOUT_INFLATER_SERVICE);
		titleLayout = (StandardTitledLayout) layoutInflater.inflate(
				R.layout.freeroom_layout_home, null);
		mainLayout = (LinearLayout) titleLayout
				.findViewById(R.id.freeroom_layout_home_main_layout);
		// The ActionBar is added automatically when you call setContentView
		setContentView(titleLayout);
		titleLayout.setTitle(getString(R.string.freeroom_title_main_title));

		mExpListView = (ExpandableListView) titleLayout
				.findViewById(R.id.freeroom_layout_home_list);
		mTextView = (TextView) titleLayout
				.findViewById(R.id.freeroom_layout_home_text_summary);
		setTextSummary(getString(R.string.freeroom_home_init_please_wait));
		initializeView();

		titleLayout.removeView(mainLayout);
		titleLayout.addFillerView(mainLayout);

		initDefaultRequest();
		refresh();

		// TODO: NOT the right call to handle the intent
		handleIntent(getIntent());
	}

	/**
	 * This is called when the Activity is resumed.
	 * 
	 * If the user presses back on the Authentication window, This Activity is
	 * resumed but we do not have the freeroomCookie. In this case we close the
	 * Activity.
	 */
	@Override
	protected void onResume() {
		super.onResume();
		/*
		 * if(mModel != null && mModel.getFreeRoomCookie() == null) { // Resumed
		 * and lot logged in? go back finish(); }
		 */

		if (mController != null) {
			mController.sendFRRequest(this);
		}
	}

	/**
	 * Handles an intent for a search coming from outside.
	 * <p>
	 * // TODO: handles occupancy.epfl.ch + pockecampus://
	 * 
	 * @param intent
	 *            the intent to handle
	 */
	private void handleSearchIntent(Intent intent) {
		// TODO: if search launched by other plugin.
	}

	@Override
	public void initializeView() {
		mLayoutInflater = this.getLayoutInflater();

		// retrieve display dimensions
		Rect displayRectangle = new Rect();
		Window window = this.getWindow();
		window.getDecorView().getWindowVisibleDisplayFrame(displayRectangle);
		activityWidth = displayRectangle.width();
		activityHeight = displayRectangle.height();

		mExpListAdapter = new ExpandableListViewAdapter<Occupancy>(
				getApplicationContext(), mModel.getOccupancyResults(),
				mController, this);
		mExpListView.setAdapter(mExpListAdapter);
		addActionToActionBar(refresh);
		addActionToActionBar(editFavorites);
		addActionToActionBar(search);
		initInfoDialog();
		initSearchDialog();
		initFavoritesDialog();
		initAddRoomDialog();
		initShareDialog();
	}

	/**
	 * Inits the dialog to diplay the information about a room.
	 */
	private void initInfoDialog() {
		// Instantiate an AlertDialog.Builder with its constructor
		AlertDialog.Builder builder = new AlertDialog.Builder(this);
		builder.setTitle("room name");
		builder.setIcon(R.drawable.details_white_50);
		builder.setPositiveButton(
				getString(R.string.freeroom_dialog_info_share), null);
		builder.setNegativeButton(
				getString(R.string.freeroom_dialog_fav_close), null);

		// Get the AlertDialog from create()
		mInfoRoomDialog = builder.create();

		// redefine paramaters to dim screen when displayed
		WindowManager.LayoutParams lp = mInfoRoomDialog.getWindow().getAttributes();
		lp.dimAmount = 0.60f;
		// these doesn't work
		lp.width = LayoutParams.FILL_PARENT;
		lp.height = LayoutParams.WRAP_CONTENT;
		mInfoRoomDialog.getWindow().addFlags(
				WindowManager.LayoutParams.FLAG_WATCH_OUTSIDE_TOUCH);
		mInfoRoomDialog.getWindow().addFlags(
				WindowManager.LayoutParams.FLAG_DIM_BEHIND);
		mInfoRoomDialog.getWindow().setAttributes(lp);

		mInfoRoomView = mLayoutInflater.inflate(
				R.layout.freeroom_layout_dialog_info, null);

		// these work perfectly
		mInfoRoomView.setMinimumWidth((int) (activityWidth * 0.9f));
		mInfoRoomView.setMinimumHeight((int) (activityHeight * 0.8f));

		mInfoRoomDialog.setView(mInfoRoomView);
		mInfoRoomDialog.setOnShowListener(new OnShowListener() {

			@Override
			public void onShow(DialogInterface dialog) {

			}
		});
	}

	/**
	 * Inits the dialog to diplay the favorites.
	 */
	private ArrayList<String> buildings;
	private Map<String, List<FRRoom>> rooms;
	private ExpandableListViewFavoriteAdapter mAdapterFav;

	private void initFavoritesDialog() {
		// Instantiate an AlertDialog.Builder with its constructor
		AlertDialog.Builder builder = new AlertDialog.Builder(this);
		builder.setTitle(getString(R.string.freeroom_dialog_fav_title));
		builder.setIcon(R.drawable.star2x28);
		builder.setPositiveButton(getString(R.string.freeroom_dialog_fav_add),
				null);
		builder.setNegativeButton(
				getString(R.string.freeroom_dialog_fav_close), null);

		// Get the AlertDialog from create()
		mFavoritesDialog = builder.create();

		// redefine paramaters to dim screen when displayed
		WindowManager.LayoutParams lp = mFavoritesDialog.getWindow()
				.getAttributes();
		lp.dimAmount = 0.60f;
		// these doesn't work
		lp.width = LayoutParams.FILL_PARENT;
		lp.height = LayoutParams.WRAP_CONTENT;
		mFavoritesDialog.getWindow().addFlags(
				WindowManager.LayoutParams.FLAG_WATCH_OUTSIDE_TOUCH);
		mFavoritesDialog.getWindow().addFlags(
				WindowManager.LayoutParams.FLAG_DIM_BEHIND);
		mFavoritesDialog.getWindow().setAttributes(lp);

		mFavoritesView = mLayoutInflater.inflate(
				R.layout.freeroom_layout_dialog_fav, null);

		// these work perfectly
		mFavoritesView.setMinimumWidth((int) (activityWidth * 0.9f));
		mFavoritesView.setMinimumHeight((int) (activityHeight * 0.8f));

		mFavoritesDialog.setView(mFavoritesView);
		mFavoritesDialog.setOnShowListener(new OnShowListener() {

			@Override
			public void onShow(DialogInterface dialog) {

			}
		});

		mFavoritesDialog.hide();
		mFavoritesDialog.show();
		mFavoritesDialog.dismiss();

		Button tv = mFavoritesDialog.getButton(DialogInterface.BUTTON_POSITIVE);
		tv.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				if (!mAddRoomDialog.isShowing()) {
					showAddRoomDialog(true);
				}
			}
		});

		ExpandableListView lv = (ExpandableListView) mFavoritesView
				.findViewById(R.id.freeroom_layout_dialog_fav_list);

		// TODO: THIS IS AWWWWWWWFUUUUULLL
		// PLEASE STORE FRROOM OBJECTS, NOT THESE UIDS
		Map<String, String> allFavorites = mModel.getAllRoomMapFavorites();
		HashSet<FRRoom> favoritesAsFRRoom = new HashSet<FRRoom>();
		for (Entry<String, String> e : allFavorites.entrySet()) {
			// Favorites beeing stored as uid -> doorCode
			favoritesAsFRRoom.add(new FRRoom(e.getValue(), e.getKey()));
		}
		rooms = mModel.sortFRRoomsByBuildingsAndFavorites(favoritesAsFRRoom,
				false);
		buildings = new ArrayList<String>(rooms.keySet());

		mAdapterFav = new ExpandableListViewFavoriteAdapter(this, buildings,
				rooms, mModel);
		lv.setAdapter(mAdapterFav);
		mAdapterFav.notifyDataSetChanged();
	}

	// true: fav // false: user-def search
	private boolean calling = true;

	private void showAddRoomDialog(boolean calling) {
		mAddRoomDialog.show();
		// TODO: reset the data ? the text input, the selected room ?
		this.calling = calling;
	}

	private void treatEndAddRoomDialog() {
		if (calling) {
			Iterator<FRRoom> iter = selectedRooms.iterator();
			while (iter.hasNext()) {
				FRRoom mRoom = iter.next();
				mModel.addRoomFavorites(mRoom.getUid(), mRoom.getDoorCode());
			}
			resetUserDefined();
		} else {
			// we do nothing: reset will be done at search time
			mSummarySelectedRoomsTextViewSearchMenu
					.setText(getSummaryTextFromCollection(selectedRooms));
		}
	}

	private void initAddRoomDialog() {
		// Instantiate an AlertDialog.Builder with its constructor
		AlertDialog.Builder builder = new AlertDialog.Builder(this);

		// Get the AlertDialog from create()
		mAddRoomDialog = builder.create();

		// redefine paramaters to dim screen when displayed
		WindowManager.LayoutParams lp = mAddRoomDialog.getWindow()
				.getAttributes();
		lp.dimAmount = 0.60f;
		// these doesn't work
		lp.width = LayoutParams.FILL_PARENT;
		lp.height = LayoutParams.WRAP_CONTENT;
		mAddRoomDialog.getWindow().addFlags(
				WindowManager.LayoutParams.FLAG_WATCH_OUTSIDE_TOUCH);
		mAddRoomDialog.getWindow().addFlags(
				WindowManager.LayoutParams.FLAG_DIM_BEHIND);
		mAddRoomDialog.getWindow().setAttributes(lp);

		mAddRoomView = mLayoutInflater.inflate(
				R.layout.freeroom_layout_dialog_add_room, null);
		// these work perfectly
		mAddRoomView.setMinimumWidth((int) (activityWidth * 0.9f));
		// mAddRoomView.setMinimumHeight((int) (activityHeight * 0.8f));

		mAddRoomDialog.setView(mAddRoomView);
		mAddRoomDialog.setOnShowListener(new OnShowListener() {

			@Override
			public void onShow(DialogInterface dialog) {
				// searchButton.setEnabled(auditSubmit() == 0);
			}
		});

		Button bt_done = (Button) mAddRoomView
				.findViewById(R.id.freeroom_layout_dialog_add_room_done);
		bt_done.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				mAddRoomDialog.dismiss();
				treatEndAddRoomDialog();
			}
		});

		mSummarySelectedRoomsTextView = (TextView) mAddRoomView
				.findViewById(R.id.freeroom_layout_dialog_add_room_summary);

		UIConstructInputBar();
		LinearLayout ll = (LinearLayout) mAddRoomView
				.findViewById(R.id.freeroom_layout_dialog_add_layout_main);
		ll.addView(mAutoCompleteSuggestionInputBarElement);
		createSuggestionsList();

	}

	private void initShareDialog() {
		// Instantiate an AlertDialog.Builder with its constructor
		AlertDialog.Builder builder = new AlertDialog.Builder(this);
		builder.setTitle(getString(R.string.freeroom_dialog_share_title));
		builder.setPositiveButton(
				getString(R.string.freeroom_dialog_share_button_friends), null);
		builder.setNegativeButton(getString(R.string.freeroom_search_cancel),
				null);
		builder.setNeutralButton(
				getString(R.string.freeroom_dialog_share_button_server), null);
		builder.setIcon(R.drawable.share_white_50);

		// Get the AlertDialog from create()
		mShareDialog = builder.create();

		// redefine paramaters to dim screen when displayed
		WindowManager.LayoutParams lp = mShareDialog.getWindow()
				.getAttributes();
		lp.dimAmount = 0.60f;
		// these doesn't work
		lp.width = LayoutParams.FILL_PARENT;
		lp.height = LayoutParams.FILL_PARENT;
		mShareDialog.getWindow().addFlags(
				WindowManager.LayoutParams.FLAG_WATCH_OUTSIDE_TOUCH);
		mShareDialog.getWindow().addFlags(
				WindowManager.LayoutParams.FLAG_DIM_BEHIND);
		mShareDialog.getWindow().setAttributes(lp);

		mShareView = mLayoutInflater.inflate(
				R.layout.freeroom_layout_dialog_share, null);
		// these work perfectly
		mShareView.setMinimumWidth((int) (activityWidth * 0.95f));
		// mShareView.setMinimumHeight((int) (activityHeight * 0.8f));
		mShareDialog.setOnShowListener(new OnShowListener() {

			@Override
			public void onShow(DialogInterface dialog) {
				dismissSoftKeyBoard(mShareView);
			}
		});

		mShareDialog.setView(mShareView);

	}

	public void displayShareDialog(final FRPeriod mPeriod, final FRRoom mRoom) {
		mShareDialog.hide();
		mShareDialog.show();

		final TextView tv = (TextView) mShareView
				.findViewById(R.id.freeroom_layout_dialog_share_textBasic);
		tv.setText(wantToShare(mPeriod, mRoom, ""));

		final EditText ed = (EditText) mShareView
				.findViewById(R.id.freeroom_layout_dialog_share_text_edit);
		ed.setOnEditorActionListener(new OnEditorActionListener() {

			@Override
			public boolean onEditorAction(TextView arg0, int arg1, KeyEvent arg2) {
				tv.setText(wantToShare(mPeriod, mRoom, ed.getText().toString()));
				dismissSoftKeyBoard(arg0);
				return true;
			}
		});

		Button shareWithServer = mShareDialog
				.getButton(DialogInterface.BUTTON_NEUTRAL);
		shareWithServer.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				share(mPeriod, mRoom, false, ed.getText().toString());
				mShareDialog.dismiss();
			}
		});

		Button shareWithFriends = mShareDialog
				.getButton(DialogInterface.BUTTON_POSITIVE);
		shareWithFriends.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				share(mPeriod, mRoom, true, ed.getText().toString());
				mShareDialog.dismiss();
			}
		});

		Spinner spinner = (Spinner) mShareView
				.findViewById(R.id.freeroom_layout_dialog_share_spinner_course);
		// Create an ArrayAdapter using the string array and a default spinner
		// layout
		ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(
				this, R.array.planets_array,
				android.R.layout.simple_spinner_item);
		// Specify the layout to use when the list of choices appears
		adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		// Apply the adapter to the spinner
		spinner.setAdapter(adapter);

		spinner.setOnItemSelectedListener(new OnItemSelectedListener() {

			@Override
			public void onItemSelected(AdapterView<?> arg0, View arg1,
					int arg2, long arg3) {
				// TODO do something with that
				System.out.println("selected" + arg0.getItemAtPosition(arg2));
			}

			@Override
			public void onNothingSelected(AdapterView<?> arg0) {
				// TODO find out how is this relevant
				System.out.println("nothing selected!");
			}

		});

		// it's automatically in center of screen!
		mShareDialog.show();
	}

	/**
	 * Dismiss the keyboard associated with the view.
	 * 
	 * @param v
	 */
	private void dismissSoftKeyBoard(View v) {
		InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
		imm.hideSoftInputFromWindow(v.getWindowToken(), 0);
	}

	/**
	 * Inits the dialog to diplay the information about a room.
	 */
	private void initSearchDialog() {
		// Instantiate an AlertDialog.Builder with its constructor
		AlertDialog.Builder builder = new AlertDialog.Builder(this);

		// Various setter methods to set the searchDialog characteristics

		builder.setTitle(getString(R.string.freeroom_search_title));
		builder.setPositiveButton(getString(R.string.freeroom_search_search),
				null);
		builder.setNegativeButton(getString(R.string.freeroom_search_cancel),
				null);
		builder.setNeutralButton(getString(R.string.freeroom_search_reset),
				null);
		builder.setIcon(R.drawable.magnify2x06);

		// Get the AlertDialog from create()

		searchDialog = builder.create();

		// redefine paramaters to dim screen when displayed
		WindowManager.LayoutParams lp = searchDialog.getWindow()
				.getAttributes();
		lp.dimAmount = 0.60f;
		// these doesn't work
		lp.width = LayoutParams.FILL_PARENT;
		lp.height = LayoutParams.FILL_PARENT;
		searchDialog.getWindow().addFlags(
				WindowManager.LayoutParams.FLAG_WATCH_OUTSIDE_TOUCH);
		searchDialog.getWindow().addFlags(
				WindowManager.LayoutParams.FLAG_DIM_BEHIND);
		searchDialog.getWindow().setAttributes(lp);

		mSearchView = mLayoutInflater.inflate(
				R.layout.freeroom_layout_dialog_search, null);
		// these work perfectly
		mSearchView.setMinimumWidth((int) (activityWidth * 0.9f));
		mSearchView.setMinimumHeight((int) (activityHeight * 0.8f));

		searchDialog.setView(mSearchView);
		searchDialog.setOnShowListener(new OnShowListener() {

			@Override
			public void onShow(DialogInterface dialog) {
				searchButton.setEnabled(auditSubmit() == 0);
			}
		});
		// this is necessary o/w buttons don't exists!
		searchDialog.hide();
		searchDialog.show();
		searchDialog.dismiss();
		resetButton = searchDialog.getButton(DialogInterface.BUTTON_NEUTRAL);
		searchButton = searchDialog.getButton(DialogInterface.BUTTON_POSITIVE);

		mSummarySelectedRoomsTextViewSearchMenu = (TextView) mSearchView
				.findViewById(R.id.freeroom_layout_dialog_search_text_summary);
		// the view will be removed or the text changed, no worry
		mSummarySelectedRoomsTextViewSearchMenu.setText("empty");

		searchPreviousListView = (ListView) mSearchView
				.findViewById(R.id.freeroom_layout_dialog_search_prev_search);
		// TODO: previous search
		// searchPreviousListView.setAdapter();

		initSearch();
	}

	public boolean onTouchEvent(MotionEvent event) {

		if (event.getAction() == MotionEvent.ACTION_OUTSIDE) {
			System.out
					.println("TOuch outside the dialog ******************** ");
			searchDialog.dismiss();
		}
		return false;
	}

	/**
	 * Overrides the legacy <code>onKeyDown</code> method in order to close the
	 * dialog if one was opened.
	 * TODO: test if really needed.
	 * 
	 * @param keyCode
	 * @param event
	 * @return
	 */
	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {

		// Override back button
		if (keyCode == KeyEvent.KEYCODE_BACK) {
			boolean flag = false;
			if (mInfoRoomDialog.isShowing()) {
				mInfoRoomDialog.dismiss();
				flag = true;
			}
			if (searchDialog.isShowing()) {
				searchDialog.dismiss();
				flag = true;
			}
			if (mFavoritesDialog.isShowing()) {
				mFavoritesDialog.dismiss();
				flag = true;
			}
			selectedRooms.clear();
			if (flag) {
				resetUserDefined();
				return true;
			}
		}
		return super.onKeyDown(keyCode, event);
	}

	@Override
	public void anyError() {
		setTextSummary(getString(R.string.freeroom_home_error_sorry));
	}

	/**
	 * Sets the summary text box to the specified text.
	 * 
	 * @param text
	 *            the new summary to be displayed.
	 */
	private void setTextSummary(String text) {
		mTextView.setText(text);
	}

	/**
	 * Constructs the default request (check all the favorites for the next
	 * valid period) and sets it in the model for future use. You may call
	 * <code>refresh</code> in order to send it to the server.
	 */
	private void initDefaultRequest() {
		mModel.setFRRequestDetails(validRequest());
	}

	private FRRequestDetails validRequest() {
		Set<String> set = mModel.getAllRoomMapFavorites().keySet();
		FRRequestDetails details = null;
		if (set.isEmpty()) {
			// NO FAV = check all free rooms
			details = new FRRequestDetails(FRTimes.getNextValidPeriod(), true,
					new ArrayList<String>(1), true, false, false,
					new SetArrayList<FRRoom>());
		} else {
			// FAV: check occupancy of ALL favs
			ArrayList<String> array = new ArrayList<String>(set.size());
			array.addAll(set);
			details = new FRRequestDetails(FRTimes.getNextValidPeriod(), false,
					array, false, true, false, new SetArrayList<FRRoom>());
		}
		return details;
	}

	/**
	 * Asks the controller to send again the request which was already set in
	 * the model.
	 */
	private void refresh() {
		setTextSummary(getString(R.string.freeroom_home_please_wait));
		mController.sendFRRequest(this);
	}

	@Override
	public void freeRoomResultsUpdated() {
		// we do nothing here
	}

	@Override
	public void occupancyResultUpdated() {
		// we do nothing here
	}

	@Override
	public void occupancyResultsUpdated() {
		StringBuilder build = new StringBuilder(50);
		if (mModel.getOccupancyResults().isEmpty()) {
			build.append(getString(R.string.freeroom_home_error_no_results));
		} else {
			FRRequest request = mModel.getFRRequestDetails();

			if (request.isOnlyFreeRooms()) {
				build.append(getString(R.string.freeroom_home_info_free_rooms));
			} else {
				build.append(getString(R.string.freeroom_home_info_rooms));
			}
			FRPeriod period = request.getPeriod();
			build.append(generateFullTimeSummary(period));
		}

		setTextSummary(build.toString());
		mExpListAdapter.notifyDataSetChanged();
		updateCollapse(mExpListView, mExpListAdapter);
	}

	/**
	 * Expands all the groups if there are no more than 4 groups or not more
	 * than 10 results.
	 * <p>
	 * TODO defines these consts somewhere else
	 * 
	 * @param ev
	 */
	public void updateCollapse(ExpandableListView ev,
			ExpandableListViewAdapter<Occupancy> ad) {
		System.out.println("check: " + ad.getGroupCount() + "/"
				+ ad.getChildrenTotalCount()); // TODO delete
		if (ad.getGroupCount() <= 4 || ad.getChildrenTotalCount() <= 10) {
			System.out.println("i wanted to expand");
			// TODO: this cause troubles in performance when first launch
			for (int i = 0; i < ad.getGroupCount(); i++) {
				ev.expandGroup(i);
			}
		}
	}

	/**
	 * Generates a string summary of a given period of time.
	 * <p>
	 * eg: "Wednesday Apr 24 from 9am to 12pm"
	 * 
	 * @param period
	 *            the period of time
	 * @return a string summary of a given period of time.
	 */
	private String generateFullTimeSummary(FRPeriod period) {
		StringBuilder build = new StringBuilder(100);
		Date endDate = new Date(period.getTimeStampEnd());
		Date startDate = new Date(period.getTimeStampStart());
		SimpleDateFormat day_month = new SimpleDateFormat(
				getString(R.string.freeroom_pattern_day_format));
		SimpleDateFormat hour_min = new SimpleDateFormat(
				getString(R.string.freeroom_pattern_hour_format));

		build.append(" ");
		// TODO: if date is today, use "today" instead of specifying date
		build.append(getString(R.string.freeroom_check_occupancy_result_onthe));
		build.append(" ");
		build.append(day_month.format(startDate));
		build.append(" ");
		build.append(getString(R.string.freeroom_check_occupancy_result_from));
		build.append(" ");
		build.append(hour_min.format(startDate));
		build.append(" ");
		build.append(getString(R.string.freeroom_check_occupancy_result_to));
		build.append(" ");
		build.append(hour_min.format(endDate));
		return build.toString();
	}

	/**
	 * Generates a short string summary of a given period of time.
	 * <p>
	 * eg: "9:00\n12:00pm"
	 * 
	 * @param period
	 *            the period of time
	 * @return a string summary of a given period of time.
	 */
	private String generateShortTimeSummary(FRPeriod period) {
		StringBuilder build = new StringBuilder(100);
		Date endDate = new Date(period.getTimeStampEnd());
		Date startDate = new Date(period.getTimeStampStart());
		SimpleDateFormat hour_min = new SimpleDateFormat(
				getString(R.string.freeroom_pattern_hour_format));

		build.append(hour_min.format(startDate));
		build.append(" - ");
		build.append(hour_min.format(endDate));
		return build.toString();
	}

	/**
	 * Put a onClickListener on an imageView in order to share the location and
	 * time when clicking share, if available.
	 * 
	 * @param shareImageView
	 *            the view on which to put the listener
	 * @param homeView
	 *            reference to the home view
	 * @param mOccupancy
	 *            the holder of data for location and time
	 */
	public void setShareClickListener(View shareImageView,
			final FreeRoomHomeView homeView, final Occupancy mOccupancy) {

		if (!mOccupancy.isIsAtLeastOccupiedOnce()
				&& mOccupancy.isIsAtLeastFreeOnce()) {
			shareImageView.setClickable(true);
			shareImageView.setEnabled(true);
			if (shareImageView instanceof ImageView) {
				((ImageView) shareImageView).setImageResource(R.drawable.share);
			}
			shareImageView.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View v) {
					homeView.displayShareDialog(
							getMaxPeriodFromList(mOccupancy.getOccupancy()),
							mOccupancy.getRoom());
				}
			});
		} else {
			shareImageView.setClickable(false);
			shareImageView.setEnabled(false);

			if (shareImageView instanceof ImageView) {
				((ImageView) shareImageView)
						.setImageResource(R.drawable.share_disabled);
			}
		}
	}

	/**
	 * Finds the whole period covered by a list of contiguous and ordered of
	 * occupancies.
	 * 
	 * @param listOccupations
	 *            the list of occupancies.
	 * @return the period covered by the list
	 */
	private FRPeriod getMaxPeriodFromList(List<ActualOccupation> listOccupations) {
		long tss = listOccupations.get(0).getPeriod().getTimeStampStart();
		long tse = listOccupations.get(listOccupations.size() - 1).getPeriod()
				.getTimeStampEnd();
		return new FRPeriod(tss, tse, false);
	}

	/**
	 * Display the dialog that provides more info about the occupation of the
	 * selected room.
	 */
	public void displayInfoDialog() {
		final Occupancy mOccupancy = mModel.getDisplayedOccupancy();
		if (mOccupancy != null) {
			mInfoRoomDialog.hide();
			mInfoRoomDialog.show();

			final FRRoom mRoom = mOccupancy.getRoom();
			String text = mRoom.getDoorCode();
			if (mRoom.isSetDoorCodeAlias()) {
				// alias is displayed IN PLACE of the official name
				// the official name can be found in bottom of dialog
				text = mRoom.getDoorCodeAlias();
			}
			mInfoRoomDialog.setTitle(text);

			TextView periodTextView = (TextView) mInfoRoomView
					.findViewById(R.id.freeroom_layout_dialog_info_period);
			periodTextView
					.setText(generateFullTimeSummary(getMaxPeriodFromList(mOccupancy
							.getOccupancy())));

			ImageView iv = (ImageView) mInfoRoomView
					.findViewById(R.id.freeroom_layout_dialog_info_share);
			setShareClickListener(iv, this, mOccupancy);

			Button share = mInfoRoomDialog.getButton(AlertDialog.BUTTON_POSITIVE);
			share.setEnabled(mOccupancy.isIsAtLeastFreeOnce()
					&& !mOccupancy.isIsAtLeastOccupiedOnce());
			setShareClickListener(share, this, mOccupancy);

			ListView roomOccupancyListView = (ListView) mInfoRoomView
					.findViewById(R.id.freeroom_layout_dialog_info_roomOccupancy);
			roomOccupancyListView
					.setAdapter(new ActualOccupationArrayAdapter<ActualOccupation>(
							getApplicationContext(), mOccupancy.getOccupancy(),
							mController, this));

			TextView detailsTextView = (TextView) mInfoRoomView
					.findViewById(R.id.freeroom_layout_dialog_info_details);
			detailsTextView.setText(getInfoFRRoom(mOccupancy.getRoom()));
			mInfoRoomDialog.show();
		}
	}

	private void fillSearchDialog() {
		final FRRequestDetails request = mModel.getFRRequestDetails();
		if (request != null) {
			fillSearchDialog(request);
		}
	}

	private void fillSearchDialog(final FRRequestDetails request) {
		resetTimes(request.getPeriod());
		anyButton.setChecked(request.isAny());
		specButton.setChecked(!request.isAny());
		favButton.setChecked(request.isFav());
		userDefButton.setChecked(request.isUser());
		freeButton.setChecked(request.isOnlyFreeRooms());
		searchButton.setEnabled(auditSubmit() == 0);
		boolean enabled = !request.isAny();
		favButton.setEnabled(enabled);
		userDefButton.setEnabled(enabled);
		freeButton.setEnabled(enabled);
		mOptionalLineLinearLayoutContainer
				.removeView(mOptionalLineLinearLayoutWrapper);
		if (enabled) {
			mOptionalLineLinearLayoutContainer
					.addView(mOptionalLineLinearLayoutWrapper);
		}
		mOptionalLineLinearLayoutWrapper
				.removeView(mOptionalLineLinearLayoutWrapperIn);
		if (request.isUser()) {
			mOptionalLineLinearLayoutWrapper
					.addView(mOptionalLineLinearLayoutWrapperIn);
			selectedRooms.addAll(request.getUidNonFav());
			mSummarySelectedRoomsTextViewSearchMenu
					.setText(getSummaryTextFromCollection(selectedRooms));
		}
	}

	public String wantToShare(FRPeriod mPeriod, FRRoom mRoom, String toShare) {
		// TODO: in case of "now" request (nextPeriodValid is now), just put
		// "i am, now, " instead of
		// time
		StringBuilder textBuilder = new StringBuilder(100);
		textBuilder.append(getString(R.string.freeroom_share_iwillbe) + " ");
		textBuilder.append(getString(R.string.freeroom_share_in_room) + " ");
		if (mRoom.isSetDoorCodeAlias()) {
			textBuilder.append(mRoom.getDoorCodeAlias() + " ("
					+ mRoom.getDoorCode() + ") ");
		} else {
			textBuilder.append(mRoom.getDoorCode() + " ");
		}
		// TODO: which period to use ?
		// in case of specified in request, we should use the personalized
		// period
		textBuilder.append(generateFullTimeSummary(mPeriod) + ". ");
		if (toShare.length() == 0) {
			textBuilder.append(getString(R.string.freeroom_share_please_come));
		} else {
			textBuilder.append(toShare);
		}
		return textBuilder.toString();
	}

	private void share(FRPeriod mPeriod, FRRoom mRoom, boolean withFriends,
			String toShare) {
		WorkingOccupancy work = new WorkingOccupancy(mPeriod, mRoom);
		ImWorkingRequest request = new ImWorkingRequest(work,
				mModel.getAnonymID());
		mController.prepareImWorking(request);
		mController.ImWorking(this);
		if (withFriends) {
			shareWithFriends(mPeriod, mRoom, toShare);
		}
	}

	/**
	 * Construct the Intent to share the location and time with friends. The
	 * same information is shared with the server at the same time
	 * 
	 * @param mPeriod
	 *            time period
	 * @param mRoom
	 *            location
	 */
	private void shareWithFriends(FRPeriod mPeriod, FRRoom mRoom, String toShare) {
		String sharing = wantToShare(mPeriod, mRoom, toShare);
		sharing += " \n" + getString(R.string.freeroom_share_ref_pocket);
		Log.v(this.getClass().getName() + "-share", "sharing:" + sharing);
		Intent sendIntent = new Intent();
		sendIntent.setAction(Intent.ACTION_SEND);
		sendIntent.putExtra(Intent.EXTRA_TEXT, sharing);
		// this ensure that our handler, that is handling home-made text types,
		// will also work. But our won't work outside the app, which is needed,
		// because it waits for this special type.
		sendIntent.setType("text/*");
		startActivity(Intent.createChooser(sendIntent,
				getString(R.string.freeroom_share_title)));
	}

	/**
	 * Converts a FRRoom to a String of only major properties, in order to
	 * display them. It includes name (with alias), type, capacity, surface and
	 * UID.
	 * <p>
	 * TODO: this method may be changed
	 * 
	 * @param mFrRoom
	 * @return
	 */
	private String getInfoFRRoom(FRRoom mFrRoom) {
		StringBuilder builder = new StringBuilder(50);
		if (mFrRoom.isSetDoorCode()) {
			if (mFrRoom.isSetDoorCodeAlias()) {
				builder.append(mFrRoom.getDoorCode() + " (alias: "
						+ mFrRoom.getDoorCodeAlias() + ")");
			} else {
				builder.append(mFrRoom.getDoorCode());
			}
		}
		if (mFrRoom.isSetTypeFR() || mFrRoom.isSetTypeEN()) {
			builder.append(" / " + getString(R.string.freeroom_dialog_info_type)
					+ ": ");
			if (mFrRoom.isSetTypeFR()) {
				builder.append(mFrRoom.getTypeFR());
			}
			if (mFrRoom.isSetTypeFR() && mFrRoom.isSetTypeEN()) {
				builder.append(" / ");
			}
			if (mFrRoom.isSetTypeFR()) {
				builder.append(mFrRoom.getTypeEN());
			}
		}
		if (mFrRoom.isSetCapacity()) {
			builder.append(" / "
					+ getString(R.string.freeroom_dialog_info_capacity) + ": "
					+ mFrRoom.getCapacity() + " "
					+ getString(R.string.freeroom_dialog_info_places));
		}
		if (mFrRoom.isSetSurface()) {
			builder.append(" / "
					+ getString(R.string.freeroom_dialog_info_surface) + ": "
					+ mFrRoom.getSurface() + " "
					+ getString(R.string.freeroom_dialog_info_sqm));
		}
		// TODO: for production, remove UID (it's useful for debugging for the
		// moment)
		if (mFrRoom.isSetUid()) {
			// uniq UID must be 1201XXUID, with XX filled with 0 such that
			// it has 10 digit
			// the prefix "1201" indiquates that it's a EPFL room (not a phone,
			// a computer)
			String communUID = "1201";
			String roomUID = mFrRoom.getUid();
			for (int i = roomUID.length() + 1; i <= 6; i++) {
				communUID += "0";
			}
			communUID += roomUID;
			builder.append(" / "
					+ getString(R.string.freeroom_dialog_info_uniqID) + ": "
					+ communUID);
		}
		return builder.toString();
	}

	// ** REUSED FROM SCRATCH FROM FreeRoomSearchView ** //
	private SetArrayList<FRRoom> selectedRooms;

	private ListView mAutoCompleteSuggestionListView;
	private List<FRRoom> mAutoCompleteSuggestionArrayListFRRoom;

	/** The input bar to make the search */
	private InputBarElement mAutoCompleteSuggestionInputBarElement;
	/** Adapter for the <code>mListView</code> */
	private FRRoomSuggestionArrayAdapter<FRRoom> mAdapter;

	private DatePickerDialog mDatePickerDialog;
	private TimePickerDialog mTimePickerStartDialog;
	private TimePickerDialog mTimePickerEndDialog;

	private Button showDatePicker;
	private Button showStartTimePicker;
	private Button showEndTimePicker;

	private RadioButton specButton;
	private RadioButton anyButton;
	private CheckBox favButton;
	private CheckBox userDefButton;
	/**
	 * TRUE: "only free rooms" FALSE: "allow non-free rooms"
	 */
	private CheckBox freeButton;

	private Button searchButton;
	private Button resetButton;

	private Button userDefEditButton;
	private Button userDefResetButton;
	private ImageButton addHourButton;
	private ImageButton upToEndHourButton;

	/**
	 * Stores if the "up to end" button has been trigged.
	 * <p>
	 * If yes, the endHour don't follow anymore the startHour when you change
	 * it. It will be disabled when you change manually the endHour to a value
	 * under the maximal hour.
	 */
	private boolean upToEndSelected = false;

	private TextView mSummarySelectedRoomsTextView;
	private TextView mSummarySelectedRoomsTextViewSearchMenu;

	private int yearSelected = -1;
	private int monthSelected = -1;
	private int dayOfMonthSelected = -1;
	private int startHourSelected = -1;
	private int startMinSelected = -1;
	private int endHourSelected = -1;
	private int endMinSelected = -1;

	private SimpleDateFormat dateFormat;
	private SimpleDateFormat timeFormat;
	private LinearLayout mOptionalLineLinearLayoutWrapper;
	private LinearLayout mOptionalLineLinearLayoutWrapperIn;
	private LinearLayout mOptionalLineLinearLayoutContainer;

	private void initSearch() {

		mOptionalLineLinearLayoutWrapper = (LinearLayout) searchDialog
				.findViewById(R.id.freeroom_layout_dialog_search_opt_line_wrapper);
		mOptionalLineLinearLayoutContainer = (LinearLayout) searchDialog
				.findViewById(R.id.freeroom_layout_dialog_search_opt_line_container);
		mOptionalLineLinearLayoutWrapperIn = (LinearLayout) mSearchView
				.findViewById(R.id.freeroom_layout_dialog_search_opt_line_wrapper_in);

		selectedRooms = new SetArrayList<FRRoom>();
		formatters();

		// createSuggestionsList();
		// addAllFavsToAutoComplete();
		mAutoCompleteSuggestionArrayListFRRoom = new ArrayList<FRRoom>(10);
		resetTimes();

		UIConstructPickers();

		UIConstructButton();

		// UIConstructInputBar();

		reset();
	}

	private void formatters() {
		dateFormat = new SimpleDateFormat(
				getString(R.string.freeroom_pattern_day_format));
		timeFormat = new SimpleDateFormat(
				getString(R.string.freeroom_pattern_hour_format));
	}

	private void UIConstructPickers() {
		// First allow the user to select a date
		showDatePicker = (Button) mSearchView
				.findViewById(R.id.freeroom_layout_dialog_search_date);
		mDatePickerDialog = new DatePickerDialog(this,
				new DatePickerDialog.OnDateSetListener() {

					@Override
					public void onDateSet(DatePicker view, int nYear,
							int nMonthOfYear, int nDayOfMonth) {
						yearSelected = nYear;
						monthSelected = nMonthOfYear;
						dayOfMonthSelected = nDayOfMonth;
						updateDatePickerAndButton();
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
		showStartTimePicker = (Button) mSearchView
				.findViewById(R.id.freeroom_layout_dialog_search_hour_start);
		mTimePickerStartDialog = new TimePickerDialog(this,
				new OnTimeSetListener() {

					@Override
					public void onTimeSet(TimePicker view, int nHourOfDay,
							int nMinute) {
						int previous = startHourSelected;
						startHourSelected = nHourOfDay;
						startMinSelected = nMinute;
						if (startHourSelected < FRTimes.FIRST_HOUR_CHECK) {
							startHourSelected = FRTimes.FIRST_HOUR_CHECK;
							startMinSelected = 0;
						}
						if (startHourSelected >= FRTimes.LAST_HOUR_CHECK) {
							startHourSelected = FRTimes.LAST_HOUR_CHECK - 1;
							startMinSelected = 0;
						}
						if (startHourSelected != -1 && !upToEndSelected) {
							int shift = startHourSelected - previous;
							int newEndHour = endHourSelected + shift;
							if (newEndHour > FRTimes.LAST_HOUR_CHECK) {
								newEndHour = FRTimes.LAST_HOUR_CHECK;
							}
							if (newEndHour < FRTimes.FIRST_HOUR_CHECK) {
								newEndHour = FRTimes.FIRST_HOUR_CHECK + 1;
							}
							endHourSelected = newEndHour;
							if (endHourSelected == FRTimes.LAST_HOUR_CHECK) {
								endMinSelected = 0;
							}
							updateEndTimePickerAndButton();
						}
						updateStartTimePickerAndButton();
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
		showEndTimePicker = (Button) mSearchView
				.findViewById(R.id.freeroom_layout_dialog_search_hour_end);
		mTimePickerEndDialog = new TimePickerDialog(this,
				new OnTimeSetListener() {

					@Override
					public void onTimeSet(TimePicker view, int nHourOfDay,
							int nMinute) {
						endHourSelected = nHourOfDay;
						endMinSelected = nMinute;
						if (endHourSelected < startHourSelected) {
							endHourSelected = startHourSelected + 1;
						}
						if (endHourSelected < FRTimes.FIRST_HOUR_CHECK) {
							endHourSelected = FRTimes.FIRST_HOUR_CHECK + 1;
							endMinSelected = 0;
						}
						if (endHourSelected == FRTimes.FIRST_HOUR_CHECK
								&& endMinSelected <= FRTimes.MIN_MINUTE_INTERVAL) {
							endMinSelected = FRTimes.MIN_MINUTE_INTERVAL;
							// TODO: if start is not 8h00 (eg 8h10 dont work)
						}

						if (endHourSelected >= FRTimes.LAST_HOUR_CHECK) {
							endHourSelected = FRTimes.LAST_HOUR_CHECK;
							endMinSelected = 0;
						}
						if (endHourSelected != FRTimes.LAST_HOUR_CHECK) {
							upToEndSelected = false;
							upToEndHourButton.setEnabled(!upToEndSelected);
						}
						updateEndTimePickerAndButton();
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

	private void resetUserDefined() {
		// TODO: add/remove
		// mGlobalSubLayout.removeView(mAutoCompleteSuggestionInputBarElement);
		// mGlobalSubLayout.removeView(mSummarySelectedRoomsTextView);
		mOptionalLineLinearLayoutWrapper
				.removeView(mOptionalLineLinearLayoutWrapperIn);
		selectedRooms.clear();
		userDefButton.setChecked(false);
		mSummarySelectedRoomsTextView
				.setText(getSummaryTextFromCollection(selectedRooms));
		mSummarySelectedRoomsTextViewSearchMenu
				.setText(getSummaryTextFromCollection(selectedRooms));
		mAutoCompleteSuggestionInputBarElement.setInputText("");
	}

	private void UIConstructButton() {
		specButton = (RadioButton) mSearchView
				.findViewById(R.id.freeroom_layout_dialog_search_spec);
		specButton.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				if (specButton.isChecked()) {
					mOptionalLineLinearLayoutContainer
							.removeView(mOptionalLineLinearLayoutWrapper);
					mOptionalLineLinearLayoutContainer
							.addView(mOptionalLineLinearLayoutWrapper);
				}
				specButton.setChecked(true);
				anyButton.setChecked(false);
				anyButton.setEnabled(true);
				specButton.setChecked(true);
				freeButton.setChecked(false);

				boolean enabled = true;
				favButton.setEnabled(enabled);
				userDefButton.setEnabled(enabled);
				freeButton.setEnabled(enabled);

				// TODO: is this great ? this guarantees that search is always
				// available, but requires two steps to remove the fav (ass
				// user-def, remove fav)
				favButton.setChecked(true);

				searchButton.setEnabled(auditSubmit() == 0);
			}
		});

		anyButton = (RadioButton) mSearchView
				.findViewById(R.id.freeroom_layout_dialog_search_any);
		anyButton.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				if (anyButton.isChecked()) {
					mOptionalLineLinearLayoutContainer
							.removeView(mOptionalLineLinearLayoutWrapper);
				}
				specButton.setChecked(false);
				resetUserDefined();

				boolean enabled = false;
				favButton.setEnabled(enabled);
				userDefButton.setEnabled(enabled);
				freeButton.setEnabled(enabled);

				favButton.setChecked(false);
				userDefButton.setChecked(false);
				freeButton.setChecked(true);
				anyButton.setChecked(true);
				searchButton.setEnabled(auditSubmit() == 0);
			}
		});

		favButton = (CheckBox) mSearchView
				.findViewById(R.id.freeroom_layout_dialog_search_fav);
		favButton.setEnabled(true);
		favButton.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				if (!userDefButton.isChecked()) {
					favButton.setChecked(true);
				}
				anyButton.setChecked(false);
				specButton.setChecked(true);
				searchButton.setEnabled(auditSubmit() == 0);
			}
		});

		userDefButton = (CheckBox) mSearchView
				.findViewById(R.id.freeroom_layout_dialog_search_user);
		userDefButton.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				if (userDefButton.isChecked()) {
					anyButton.setChecked(false);
					specButton.setChecked(true);
					freeButton.setChecked(false);
					// TODO: init and use the data.
					mOptionalLineLinearLayoutWrapper
							.removeView(mOptionalLineLinearLayoutWrapperIn);
					mOptionalLineLinearLayoutWrapper
							.addView(mOptionalLineLinearLayoutWrapperIn);
					mSummarySelectedRoomsTextViewSearchMenu
							.setText(getSummaryTextFromCollection(selectedRooms));
					showAddRoomDialog(false);
				} else if (!favButton.isChecked()) {
					userDefButton.setChecked(true);
					anyButton.setChecked(false);
					specButton.setChecked(true);
					freeButton.setChecked(false);
				} else {
					resetUserDefined();
				}
				searchButton.setEnabled(auditSubmit() == 0);
			}
		});

		freeButton = (CheckBox) mSearchView
				.findViewById(R.id.freeroom_layout_dialog_search_non_free);
		freeButton.setEnabled(true);
		freeButton.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				if (!freeButton.isChecked()) {
					anyButton.setChecked(false);
					specButton.setChecked(true);
				}
				searchButton.setEnabled(auditSubmit() == 0);
			}
		});

		searchButton.setEnabled(auditSubmit() == 0);
		searchButton.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				prepareSearchQuery();
			}
		});

		resetButton.setEnabled(true);
		resetButton.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				reset();
				// addAllFavsToAutoComplete();
				// we reset the input bar...
				// TODO
				// mAutoCompleteSuggestionInputBarElement.setInputText("");
			}
		});

		addHourButton = (ImageButton) mSearchView
				.findViewById(R.id.freeroom_layout_dialog_search_hour_end_plus);
		addHourButton.setEnabled(true);
		addHourButton.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				if (endHourSelected <= 18) {
					endHourSelected += 1;
					updateEndTimePickerAndButton();
					mTimePickerEndDialog.updateTime(endHourSelected,
							endMinSelected);
				}
				if (endHourSelected >= 19) {
					addHourButton.setEnabled(false);
				}
			}
		});

		upToEndHourButton = (ImageButton) mSearchView
				.findViewById(R.id.freeroom_layout_dialog_search_hour_end_toend);
		upToEndHourButton.setEnabled(true);
		upToEndHourButton.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				endHourSelected = FRTimes.LAST_HOUR_CHECK;
				endMinSelected = 0;
				updateEndTimePickerAndButton();
				mTimePickerEndDialog
						.updateTime(endHourSelected, endMinSelected);
				upToEndSelected = true;
				upToEndHourButton.setEnabled(!upToEndSelected);
				searchButton.setEnabled(auditSubmit() == 0);
			}
		});

		// on vertical screens, choose fav and choose user-def are vertically
		// aligned
		// on horizontal screens, there are horizontally aligned.
		if (activityHeight > activityWidth) {
			LinearLayout mLinearLayout = (LinearLayout) searchDialog
					.findViewById(R.id.freeroom_layout_dialog_search_opt_line_semi);
			mLinearLayout.setOrientation(LinearLayout.VERTICAL);
		}

		userDefEditButton = (Button) mSearchView
				.findViewById(R.id.freeroom_layout_dialog_search_user_edit);
		userDefEditButton.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				showAddRoomDialog(false);
			}
		});
		userDefResetButton = (Button) mSearchView
				.findViewById(R.id.freeroom_layout_dialog_search_user_reset);
		userDefResetButton.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				resetUserDefined();
			}
		});
	}

	// TODO: the InputBar is not used so far
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
						if (query.length() >= 2) {
							AutoCompleteRequest request = new AutoCompleteRequest(
									query);
							mController.autoCompleteBuilding(view, request);
						}
					}
				});

		mAdapter = new FRRoomSuggestionArrayAdapter<FRRoom>(
				getApplicationContext(),
				mAutoCompleteSuggestionArrayListFRRoom, mModel);

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
							if (text.length() >= 2) {
								AutoCompleteRequest request = new AutoCompleteRequest(
										text);
								mController.autoCompleteBuilding(view, request);
							}
						}
					}
				});
	}

	private void addAllFavsToAutoComplete() {
		Map<String, String> map = mModel.getAllRoomMapFavorites();

		mAutoCompleteSuggestionArrayListFRRoom.clear();

		Iterator<String> iter = map.keySet().iterator();
		while (iter.hasNext()) {
			String uid = iter.next();
			String doorCode = map.get(uid);
			FRRoom room = new FRRoom(doorCode, uid);
			if (!selectedRooms.contains(room)) {
				mAutoCompleteSuggestionArrayListFRRoom.add(room);
			}
		}

		mAdapter.notifyDataSetChanged();
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
		mAutoCompleteSuggestionListView.setAdapter(mAdapter);
	}

	private void addRoomToCheck(FRRoom room) {
		// we only add if it already contains the room
		if (!selectedRooms.contains(room)) {
			selectedRooms.add(room);
			mSummarySelectedRoomsTextView
					.setText(getSummaryTextFromCollection(selectedRooms));

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
		int MAX = 100;
		if (buffer.length() > MAX) {
			buffer.setLength(MAX);
			buffer.append("...");
		}
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
		resetTimes(mFrPeriod);
	}

	private void resetTimes(FRPeriod mFrPeriod) {
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

	private void reset() {
		searchButton.setEnabled(false);

		// // reset the list of selected rooms
		selectedRooms.clear();
		// mSummarySelectedRoomsTextView
		// .setText(getString(R.string.freeroom_check_occupancy_search_text_no_selected_rooms));
		//
		mAutoCompleteSuggestionArrayListFRRoom.clear();

		resetTimes();

		anyButton.setChecked(true);
		mOptionalLineLinearLayoutContainer
				.removeView(mOptionalLineLinearLayoutWrapper);
		specButton.setChecked(false);
		favButton.setChecked(false);
		userDefButton.setChecked(false);
		// resetUserDefined(); TODO
		freeButton.setChecked(true);
		// verify the submit
		searchButton.setEnabled(auditSubmit() == 0);

		boolean enabled = false;
		favButton.setEnabled(enabled);
		userDefButton.setEnabled(enabled);
		freeButton.setEnabled(enabled);
		// show the buttons
		updateDateTimePickersAndButtons();
	}

	/**
	 * Updates ALL the date and time <code>PickerDialog</code> and related
	 * <code>Button</code>.
	 * 
	 * <p>
	 * It updates the <code>Button</code> to summarize the date/time selected
	 * according to your language preferences. The <code>PickerDialog</code> is
	 * also updated: it's useful if the date/time has changed from somewhere
	 * else, the <code>PickerDialog</code> will reopen with the new value.
	 */
	private void updateDateTimePickersAndButtons() {
		updateDatePickerAndButton();
		updateStartTimePickerAndButton();
		updateEndTimePickerAndButton();
	}

	/**
	 * Updates the date <code>PickerDialog</code> and related
	 * <code>Button</code>.
	 * 
	 * <p>
	 * It updates the <code>Button</code> to summarize the date selected
	 * according to your language preferences. The <code>PickerDialog</code> is
	 * also updated: it's useful if the date has changed from somewhere else,
	 * the <code>PickerDialog</code> will reopen with the new value.
	 * 
	 * <p>
	 * Instead of the usual format "Wed 24 May", the date is summarize to
	 * "today", "yesterday", "tomorrow" when relevant.
	 */
	private void updateDatePickerAndButton() {
		// creating selected time
		Calendar selected = Calendar.getInstance();
		selected.setTimeInMillis(prepareFRFrPeriod().getTimeStampStart());
		// creating now time reference
		Calendar now = Calendar.getInstance();
		// creating tomorrow time reference
		Calendar tomorrow = Calendar.getInstance();
		tomorrow.roll(Calendar.DAY_OF_MONTH, true);
		// creating yesterday time reference
		Calendar yesterday = Calendar.getInstance();
		yesterday.roll(Calendar.DAY_OF_MONTH, false);

		if (FRTimes.compareCalendars(now, selected)) {
			showDatePicker.setText(getString(R.string.freeroom_search_today));
		} else if (FRTimes.compareCalendars(tomorrow, selected)) {
			showDatePicker
					.setText(getString(R.string.freeroom_search_tomorrow));
		} else if (FRTimes.compareCalendars(yesterday, selected)) {
			showDatePicker
					.setText(getString(R.string.freeroom_search_yesterday));
		} else {
			showDatePicker.setText(dateFormat.format(selected.getTime()));
		}

		mDatePickerDialog.updateDate(yearSelected, monthSelected,
				dayOfMonthSelected);
	}

	/**
	 * Updates the START time <code>PickerDialog</code> and related
	 * <code>Button</code>.
	 * 
	 * <p>
	 * It updates the <code>Button</code> to summarize the START time selected
	 * according to your language preferences. The <code>PickerDialog</code> is
	 * also updated: it's useful if the START time has changed from somewhere
	 * else, the <code>PickerDialog</code> will reopen with the new value.
	 */
	private void updateStartTimePickerAndButton() {
		showStartTimePicker
				.setText(getString(R.string.freeroom_check_occupancy_search_start)
						+ " "
						+ timeFormat.format(new Date(prepareFRFrPeriod()
								.getTimeStampStart())));
		mTimePickerStartDialog.updateTime(startHourSelected, startMinSelected);
	}

	/**
	 * Updates the END time <code>PickerDialog</code> and related
	 * <code>Button</code>.
	 * 
	 * <p>
	 * It updates the <code>Button</code> to summarize the END time selected
	 * according to your language preferences. The <code>PickerDialog</code> is
	 * also updated: it's useful if the END time has changed from somewhere
	 * else, the <code>PickerDialog</code> will reopen with the new value.
	 */
	private void updateEndTimePickerAndButton() {
		showEndTimePicker
				.setText(getString(R.string.freeroom_check_occupancy_search_end)
						+ " "
						+ timeFormat.format(new Date(prepareFRFrPeriod()
								.getTimeStampEnd())));
		if (endHourSelected >= FRTimes.LAST_HOUR_CHECK
				|| (endHourSelected == FRTimes.LAST_HOUR_CHECK - 1 && endMinSelected != 0)) {
			addHourButton.setEnabled(false);
		} else {
			addHourButton.setEnabled(true);
		}
		mTimePickerEndDialog.updateTime(endHourSelected, endMinSelected);
	}

	/**
	 * Construct the <code>FRPeriod</code> object asscociated with the current
	 * selected times.
	 * 
	 * @return
	 */
	private FRPeriod prepareFRFrPeriod() {
		Calendar start = Calendar.getInstance();
		start.set(yearSelected, monthSelected, dayOfMonthSelected,
				startHourSelected, startMinSelected, 0);
		start.set(Calendar.MILLISECOND, 0);

		Calendar end = Calendar.getInstance();
		end.set(yearSelected, monthSelected, dayOfMonthSelected,
				endHourSelected, endMinSelected, 0);
		end.set(Calendar.MILLISECOND, 0);

		// constructs the request
		return new FRPeriod(start.getTimeInMillis(), end.getTimeInMillis(),
				false);
	}

	/**
	 * Prepare the actual query to send and set it in the controller
	 */
	private void prepareSearchQuery() {
		FRPeriod period = prepareFRFrPeriod();

		List<String> mUIDList = new ArrayList<String>(selectedRooms.size());

		if (favButton.isChecked()) {
			mUIDList.addAll(mModel.getAllRoomMapFavorites().keySet());
		}
		if (userDefButton.isChecked()) {
			Iterator<FRRoom> iter = selectedRooms.iterator();
			while (iter.hasNext()) {
				FRRoom room = iter.next();
				mUIDList.add(room.getUid());
			}
		}

		boolean any = anyButton.isChecked();
		boolean fav = favButton.isChecked();
		boolean user = userDefButton.isChecked();
		FRRequestDetails details = new FRRequestDetails(period,
				freeButton.isChecked(), mUIDList, any, fav, user, selectedRooms);
		mModel.setFRRequestDetails(details);
		mController.sendFRRequest(this);
		searchDialog.dismiss();

		resetUserDefined(); // cleans the selectedRooms of userDefined
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
		if (selectedRooms == null
				|| (!anyButton.isChecked() && !favButton.isChecked()
						&& userDefButton.isChecked() && selectedRooms.isEmpty())) {
			return 1;
		}

		if (anyButton.isChecked()
				&& (favButton.isChecked() || userDefButton.isChecked())) {
			return 1;
		}
		if (!anyButton.isChecked() && !favButton.isChecked()
				&& !userDefButton.isChecked()) {
			return 1;
		}
		Set<String> set = mModel.getAllRoomMapFavorites().keySet();
		if (favButton.isChecked()
				&& set.isEmpty()
				&& (!userDefButton.isChecked() || (userDefButton.isChecked() && selectedRooms
						.isEmpty()))) {
			return 1;
		}
		// we dont allow query all the room, including non-free
		if (anyButton.isChecked() && !freeButton.isChecked()) {
			return 1;
		}
		return auditTimes();
	}

	@Override
	public void autoCompletedUpdated() {
		mAdapter.notifyDataSetInvalidated();
		mAutoCompleteSuggestionArrayListFRRoom.clear();

		// TODO: adapt to use the new version of autocomplete mapped by building
		Iterator<List<FRRoom>> iter = mModel.getAutoComplete().values()
				.iterator();
		System.out.println(mModel.getAutoComplete().values().size());
		while (iter.hasNext()) {
			List<FRRoom> list = iter.next();
			System.out.println(list.size());
			Iterator<FRRoom> iterroom = list.iterator();
			while (iterroom.hasNext()) {
				FRRoom room = iterroom.next();
				// rooms that are already selected are not displayed...
				if (!selectedRooms.contains(room)) {
					mAutoCompleteSuggestionArrayListFRRoom.add(room);
				}
			}
		}

		mAdapter.notifyDataSetChanged();
	}
}