package org.pocketcampus.plugin.freeroom.android.views;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
import java.util.Date;
import java.util.Iterator;
import java.util.List;

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
import org.pocketcampus.plugin.freeroom.android.utils.FRTimesClient;
import org.pocketcampus.plugin.freeroom.android.utils.FRUtilsClient;
import org.pocketcampus.plugin.freeroom.android.utils.OrderMapListFew;
import org.pocketcampus.plugin.freeroom.android.utils.SetArrayList;
import org.pocketcampus.plugin.freeroom.shared.ActualOccupation;
import org.pocketcampus.plugin.freeroom.shared.AutoCompleteRequest;
import org.pocketcampus.plugin.freeroom.shared.FRPeriod;
import org.pocketcampus.plugin.freeroom.shared.FRRequest;
import org.pocketcampus.plugin.freeroom.shared.FRRoom;
import org.pocketcampus.plugin.freeroom.shared.ImWorkingRequest;
import org.pocketcampus.plugin.freeroom.shared.Occupancy;
import org.pocketcampus.plugin.freeroom.shared.WorkingOccupancy;
import org.pocketcampus.plugin.freeroom.shared.utils.FRStruct;
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
import android.net.Uri;
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
 * <p>
 * <code>AlertDialog</code> that exists are the following:
 * <p>
 * INFO ROOM display detailed information and occupancy about a room, and give
 * the ability to share the location.
 * <p>
 * SEARCH enables the user to enter a customized search, and retrieves
 * previously entered searches.
 * <p>
 * FAVORITES show the current favorites, with the possibility to remove them one
 * by one. Adding is done by the ADD ROOM dialog.
 * <p>
 * ADD ROOM gives the possibility to construct an user-defined list of selected
 * rooms, with auto-complete capabilities. It can be user to add favorites or a
 * custom search.
 * <p>
 * SHARE give the possibility to share the location with friends through a share
 * Intent. The server will also be notified, such that approximately occupancies
 * continues to be as accurate as possible.
 * <p>
 * 
 * @author FreeRoom Project Team (2014/05)
 * @author Julien WEBER <julien.weber@epfl.ch>
 * @author Valentin MINDER <valentin.minder@epfl.ch>
 */
public class FreeRoomHomeView extends FreeRoomAbstractView implements
		IFreeRoomView {

	/* MVC STRUCTURE */
	/**
	 * FreeRoom controller in MVC scheme.
	 */
	private FreeRoomController mController;
	/**
	 * FreeRoom model in MVC scheme.
	 */
	private FreeRoomModel mModel;
	/**
	 * Reference to times utility method for client-side.
	 */
	private FRTimesClient times;
	/**
	 * Reference to other utility method for client-side.
	 */
	private FRUtilsClient u;

	/* COMMON SHARED VALUES */
	/**
	 * Width of the main Activity.
	 */
	private int activityWidth;
	/**
	 * Height of the main Activity.
	 */
	private int activityHeight;
	/**
	 * Common LayoutInflater for all Layout inflated from xml.
	 */
	private LayoutInflater mLayoutInflater;

	/* UI OF MAIN ACTIVITY */
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

	/* VIEW/DIALOGS FOR ALL ALERTDIALOG */
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
	 * Dialog that holds the SEARCH Dialog.
	 */
	private AlertDialog mSearchDialog;
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
	 * View that holds the SHARE dialog content, defined in xml in layout
	 * folder.
	 */
	private View mShareView;
	/**
	 * Dialog that holds the SHARE Dialog.
	 */
	private AlertDialog mShareDialog;
	/**
	 * Dialog that holds the WARNING Dialog.
	 */
	private AlertDialog mWarningDialog;

	/* UI ELEMENTS FOR ALL DIALOGS */
	/* UI ELEMENTS FOR DIALOGS - INFO ROOM */

	/* UI ELEMENTS FOR DIALOGS - SEARCH */
	/**
	 * ListView that holds previous searches.
	 */
	private ListView mSearchPreviousListView;
	/**
	 * TextView to write "previous searches" +show/hide
	 */
	private TextView prevSearchTitle;
	/**
	 * Layout of the first part, the search input (without the second part,
	 * previous searches).
	 */
	private LinearLayout searchDialogUpperLinearLayout;
	/**
	 * Main layout of the search dialog.
	 */
	private LinearLayout searchDialogMainLinearLayout;
	/**
	 * Stores the height available
	 */
	private int searchDialogMainLayoutHeightAvailable = 0;
	/**
	 * Stores if the screen is too small
	 */
	private boolean searchDialogHasHeightExtenstionProblem = false;
	/**
	 * Ratio of dialog that should be occupied with searches input when NOT
	 * displaying previous request.
	 */
	private double searchDialogNonExtended = 0.90;
	/**
	 * Ratio of dialog that should be occupied with searches input when
	 * displaying previous request.
	 */
	private double searchDialogExtended = 0.70;
	/**
	 * Stores if the previous search has been hidden (the rest is more
	 * extended).
	 */
	private boolean searchDialogExtendMoreTriggered = false;

	/* UI ELEMENTS FOR DIALOGS - FAVORITES */

	/* UI ELEMENTS FOR DIALOGS - ADDROOM */

	/* UI ELEMENTS FOR DIALOGS - SHARE */

	/* OTHER UTILS */
	/**
	 * Enum to have types and store the last caller of the "Add" dialog.
	 * 
	 */
	private enum AddRoomCaller {
		FAVORITES, SEARCH;
	}

	/**
	 * Stores the last caller of the ADDROOM dialog.
	 */
	private AddRoomCaller lastCaller = null;

	/* ACTIONS FOR THE ACTION BAR */
	/**
	 * Action to perform a customized search, by showing the search dialog.
	 */
	private Action search = new Action() {
		public void performAction(View view) {
			displaySearchDialog();
		}

		public int getDrawable() {
			return R.drawable.magnify2x06;
		}
	};

	/**
	 * Action to edit the user's favorites, by showing the favorites dialog.
	 */
	private Action editFavorites = new Action() {
		public void performAction(View view) {
			mFavoritesAdapter.notifyDataSetChanged();
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

	/* MAIN ACTIVITY - OVERRIDEN METHODS */

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
		times = new FRTimesClient(this);
		u = new FRUtilsClient(this);

		// Setup the layout
		mLayoutInflater = this.getLayoutInflater();
		titleLayout = new StandardTitledLayout(this);
		mainLayout = (LinearLayout) mLayoutInflater.inflate(
				R.layout.freeroom_layout_home, null);
		// The ActionBar is added automatically when you call setContentView
		setContentView(titleLayout);
		titleLayout.setTitle(getString(R.string.freeroom_title_main_title));

		mExpListView = (ExpandableListView) mainLayout
				.findViewById(R.id.freeroom_layout_home_list);
		mTextView = (TextView) mainLayout
				.findViewById(R.id.freeroom_layout_home_text_summary);
		setTextSummary(getString(R.string.freeroom_home_init_please_wait));
		initializeView();

		// add the main layout to the pocketcampus titled layout.
		titleLayout.addFillerView(mainLayout);
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
	}

	/**
	 * Handles an intent for coming from outside, eg. for specific search.
	 * <p>
	 * If it's not a specific intended Intent, it will simply construct the
	 * default request and refresh it. As specified in SDK javadoc, this method
	 * is called AFTER the onCreate and onResume methods, so it's used to
	 * initiate the default behavior at (re)launch.
	 * <p>
	 * It supports:
	 * <p>
	 * http://occupancy.epfl.ch/content format, with content being the
	 * autocomplete requet.
	 * <p>
	 * TODO: pockecampus://
	 * 
	 * @param intent
	 *            the intent to handle
	 */
	@Override
	protected void handleIntent(Intent intent) {
		u.logV("Starting the app: handling an Intent");
		// Intent and/or action seems to be null in some cases... so we just
		// skip these cases and launch the default settings.
		if (intent != null && intent.getAction() != null) {
			if (intent.getAction().equalsIgnoreCase(
					"android.intent.action.MAIN")) {
				u.logV("starting MAIN and default mode");
				defaultMainStart();
			} else if (intent.getAction().equalsIgnoreCase(
					"android.intent.action.VIEW")) {
				u.logV("starting the app and handling simple VIEW intent");
				String errorIntentHandled = ""
						+ getString(R.string.freeroom_urisearch_error_URINotUnderstood)
						+ " "
						+ getString(R.string.freeroom_urisearch_error_URINotSupported);
				String intentScheme = intent.getScheme();
				Uri intentUri = intent.getData();
				if (intentScheme != null && intentUri != null) {
					String intentUriHost = intentUri.getHost();
					String intentUriPath = intentUri.getPath();
					if (intentUriHost != null && intentUriPath != null) {
						String intentUriPathQuery = FRStruct
								.removeFirstCharSafely(intentUriPath);
						// using standard epfl http page
						if (intentScheme.equalsIgnoreCase("http")
								&& intentUriHost
										.equalsIgnoreCase("occupancy.epfl.ch")) {
							u.logV("Found an EPFL http://occupancy.epfl.ch/room URI");
							u.logV("With room query: \"" + intentUriPathQuery
									+ "\"");
							errorIntentHandled = searchByUriPrepareArguments(intentUriPathQuery);
						}

						// using freeroom links.
						else if (intentScheme.equalsIgnoreCase("pocketcampus")
								&& intentUriHost
										.equalsIgnoreCase("freeroom.plugin.pocketcampus.org")) {
							u.logV("Found a pocketcampus://freeroom.plugin.pocketcampus.org/data URI");
							u.logV("With room query: \"" + intentUriPathQuery
									+ "\"");
							// TODO: do something.
							errorIntentHandled = "URI NOR supported right now!";
						} else {
							u.logE("Unknow URI: \"" + intentUri + "\"");
						}
					}
				}
				if (errorIntentHandled.length() != 0) {
					onErrorHandleIntent(errorIntentHandled);
				}
			} else {
				u.logE("ERROR: Found an unhandled action: \""
						+ intent.getAction() + "\"");
				u.logE("Starting the app in default mode anyway");
				defaultMainStart();
			}
		} else {
			if (intent == null) {
				u.logE("ERROR: Found a null Intent !!!");
			} else if (intent.getAction() == null) {
				u.logE("ERROR: Found a null Action !!!");
				u.logE("This issue may appear by launching the app from the pocketcampus dashboard");
			}

			u.logE("Starting the app in default mode anyway");
			defaultMainStart();
		}
	}

	/**
	 * In case of error while handling an Intent, display a message to the user,
	 * and then launch a default request (without taking the favorites into
	 * account).
	 * 
	 * @param errorMessage
	 *            the error message to display.
	 */
	private void onErrorHandleIntent(String errorMessage) {
		// TODO: display a popup
		u.logE(getString(R.string.freeroom_urisearch_error_basis));
		u.logE(errorMessage);
		u.logE(getString(R.string.freeroom_urisearch_error_end));
		if (mController != null && mModel != null) {
			initDefaultRequest(false);
			refresh();
		}
	}

	/**
	 * Constructs the default request and refreshes it.
	 */
	private void defaultMainStart() {
		u.logV("Starting in default mode.");
		if (mController != null && mModel != null) {
			initDefaultRequest(true);
			refresh();
			u.logV("Successful start in default mode: wait for server response.");
		} else {
			u.logE("Controller or Model not defined: cannot start default mode.");
		}
	}

	/**
	 * Stores if a search by URI has been initiated recently, in order for
	 * auto-complete to automatically launch a new search if triggered, using
	 * <code>searchByUriMakeRequest</code>
	 */
	private boolean mSearchByUriTriggered = false;

	/**
	 * Initiates a search by URI with the given constraint as the argument for
	 * the auto-complete.
	 * 
	 * <p>
	 * If the requirement for autocomplete are not met, it will simply start a
	 * standard request (any free room now). If the autocomplete gave relevant
	 * results, it will search the availabilities of these rooms now. If
	 * autocomplete gave no results, it will also search for any free room now.
	 * 
	 * @param constraint
	 *            argument for the auto-complete to search for rooms.
	 * @return an empty String if successful, an error message if
	 */
	private String searchByUriPrepareArguments(String constraint) {
		// TODO: constraint in common!!
		if (constraint.length() < 2) {
			return getString(R.string.freeroom_urisearch_error_AutoComplete_error)
					+ " "
					+ getString(R.string.freeroom_urisearch_error_AutoComplete_precond);
		} else {
			mSearchByUriTriggered = true;
			// TODO: group
			AutoCompleteRequest req = new AutoCompleteRequest(constraint, 1);
			mController.autoCompleteBuilding(this, req);
			return "";
		}
	}

	/**
	 * Make a FRRequest with the FRRoom given in argument, for the rest of the
	 * day. If the argument is empty, it will display free room now.
	 * 
	 * @param collection
	 *            collection of FRRoom to make a new search on.
	 */
	private void searchByUriMakeRequest(Collection<FRRoom> collection) {
		mSearchByUriTriggered = false;

		boolean empty = collection.isEmpty();
		if (empty) {
			// if nothing matched the search, we notify the user.
			onErrorHandleIntent(getString(R.string.freeroom_urisearch_error_AutoComplete_error)
					+ " "
					+ getString(R.string.freeroom_urisearch_error_AutoComplete_noMatch));
		} else {
			// TODO: warn if there is more than 1 result ? Val. think no.

			// search for the rest of the day.
			FRPeriod period = FRTimes.getNextValidPeriodTillEndOfDay();
			FRRequestDetails request = null;
			List<String> uidList = new ArrayList<String>();
			SetArrayList<FRRoom> uidNonFav = new SetArrayList<FRRoom>();
			// TODO: find a simpler and more efficient way ?
			Iterator<FRRoom> iter = collection.iterator();
			while (iter.hasNext()) {
				FRRoom room = iter.next();
				uidList.add(room.getUid());
				uidNonFav.add(room);
			}
			// TODO: usergroup
			request = new FRRequestDetails(period, false, uidList, false,
					false, true, uidNonFav, 1);
			mModel.setFRRequestDetails(request, !empty);
			refresh();
		}
	}

	/* MAIN ACTIVITY - INITIALIZATION */

	@Override
	public void initializeView() {
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
		initWarningDialog();
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
		WindowManager.LayoutParams lp = mInfoRoomDialog.getWindow()
				.getAttributes();
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
	private ExpandableListViewFavoriteAdapter mFavoritesAdapter;

	private void initFavoritesDialog() {
		// Instantiate an AlertDialog.Builder with its constructor
		AlertDialog.Builder builder = new AlertDialog.Builder(this);
		builder.setTitle(getString(R.string.freeroom_dialog_fav_title));
		builder.setIcon(R.drawable.star2x28);
		builder.setPositiveButton(getString(R.string.freeroom_dialog_fav_add),
				null);
		builder.setNegativeButton(
				getString(R.string.freeroom_dialog_fav_close), null);
		builder.setNeutralButton(getString(R.string.freeroom_dialog_fav_reset),
				null);

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
				mFavoritesAdapter.notifyDataSetChanged();
			}
		});

		mFavoritesDialog.hide();
		mFavoritesDialog.show();
		mFavoritesDialog.dismiss();

		mFavoritesDialog
				.setOnDismissListener(new AlertDialog.OnDismissListener() {

					@Override
					public void onDismiss(DialogInterface dialog) {
						// sends a new request with the new favorites
						initDefaultRequest(true);
						refresh();
					}
				});

		Button tv = mFavoritesDialog.getButton(DialogInterface.BUTTON_POSITIVE);
		tv.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				if (!mAddRoomDialog.isShowing()) {
					displayAddRoomDialog(AddRoomCaller.FAVORITES);
				}
			}
		});

		Button bt = mFavoritesDialog.getButton(DialogInterface.BUTTON_NEUTRAL);
		bt.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				mWarningDialog.show();
			}
		});

		ExpandableListView lv = (ExpandableListView) mFavoritesView
				.findViewById(R.id.freeroom_layout_dialog_fav_list);
		mFavoritesAdapter = new ExpandableListViewFavoriteAdapter(this, mModel
				.getFavorites().keySetOrdered(), mModel.getFavorites(), mModel);
		lv.setAdapter(mFavoritesAdapter);
		mFavoritesAdapter.notifyDataSetChanged();
	}

	private void displayAddRoomDialog(AddRoomCaller calling) {
		mAddRoomDialog.show();
		// TODO: reset the data ? the text input, the selected room ?
		lastCaller = calling;
	}

	private void dimissAddRoomDialog() {
		if (lastCaller.equals(AddRoomCaller.FAVORITES)) {
			Iterator<FRRoom> iter = selectedRooms.iterator();
			while (iter.hasNext()) {
				FRRoom mRoom = iter.next();
				// TODO: add all in batch!
				mModel.addFavorite(mRoom);
			}
			mFavoritesAdapter.notifyDataSetChanged();
			resetUserDefined();
		} else if (lastCaller.equals(AddRoomCaller.SEARCH)) {
			// we do nothing: reset will be done at search time
			mSummarySelectedRoomsTextViewSearchMenu.setText(u
					.getSummaryTextFromCollection(selectedRooms));
		}
	}

	private void initAddRoomDialog() {
		// Instantiate an AlertDialog.Builder with its constructor
		AlertDialog.Builder builder = new AlertDialog.Builder(this);
		builder.setTitle(getString(R.string.freeroom_dialog_add_room_title));
		builder.setIcon(R.drawable.ic_dialog_adding);

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
				dismissSoftKeyBoard(v);
				mAddRoomDialog.dismiss();
				dimissAddRoomDialog();
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
		tv.setText(u.wantToShare(mPeriod, mRoom, ""));

		final EditText ed = (EditText) mShareView
				.findViewById(R.id.freeroom_layout_dialog_share_text_edit);
		ed.setOnEditorActionListener(new OnEditorActionListener() {

			@Override
			public boolean onEditorAction(TextView arg0, int arg1, KeyEvent arg2) {
				tv.setText(u.wantToShare(mPeriod, mRoom, ed.getText()
						.toString()));
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
		// Apply the mFavoritesAdapter to the spinner
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

		mSearchDialog = builder.create();

		// redefine paramaters to dim screen when displayed
		WindowManager.LayoutParams lp = mSearchDialog.getWindow()
				.getAttributes();
		lp.dimAmount = 0.60f;
		// these doesn't work
		lp.width = LayoutParams.FILL_PARENT;
		lp.height = LayoutParams.FILL_PARENT;
		mSearchDialog.getWindow().addFlags(
				WindowManager.LayoutParams.FLAG_WATCH_OUTSIDE_TOUCH);
		mSearchDialog.getWindow().addFlags(
				WindowManager.LayoutParams.FLAG_DIM_BEHIND);
		mSearchDialog.getWindow().setAttributes(lp);

		mSearchView = mLayoutInflater.inflate(
				R.layout.freeroom_layout_dialog_search, null);
		// these work perfectly
		mSearchView.setMinimumWidth((int) (activityWidth * 0.9f));
		mSearchView.setMinimumHeight((int) (activityHeight * 0.8f));

		mSearchDialog.setView(mSearchView);
		final String textTitlePrevious = getString(R.string.freeroom_search_previous_search);
		prevSearchTitle = (TextView) mSearchView
				.findViewById(R.id.freeroom_layout_dialog_search_prev_search_title);
		prevSearchTitle.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				searchDialogMissSpaceExtendChangeState(searchDialogExtendMoreTriggered);
				if (searchDialogHasHeightExtenstionProblem) {
					if (searchDialogExtendMoreTriggered) {
						prevSearchTitle
								.setText(textTitlePrevious
										+ ": "
										+ getString(R.string.freeroom_search_previous_hide));
					} else {
						prevSearchTitle
								.setText(textTitlePrevious
										+ ": "
										+ getString(R.string.freeroom_search_previous_show));
					}
				}
				searchDialogExtendMoreTriggered = !searchDialogExtendMoreTriggered;
			}
		});

		mSearchDialog.setOnShowListener(new OnShowListener() {

			@Override
			public void onShow(DialogInterface dialog) {
				searchButton.setEnabled(auditSubmit() == 0);
				if (mModel.getPreviousRequest().isEmpty()) {
					prevSearchTitle.setText("");
				} else if (searchDialogHasHeightExtenstionProblem) {
					prevSearchTitle
							.setText(textTitlePrevious
									+ ": "
									+ getString(R.string.freeroom_search_previous_show));
				} else {
					prevSearchTitle.setText(textTitlePrevious);
				}
			}
		});
		// this is necessary o/w buttons don't exists!
		mSearchDialog.hide();
		mSearchDialog.show();
		mSearchDialog.dismiss();
		resetButton = mSearchDialog.getButton(DialogInterface.BUTTON_NEUTRAL);
		searchButton = mSearchDialog.getButton(DialogInterface.BUTTON_POSITIVE);

		mSummarySelectedRoomsTextViewSearchMenu = (TextView) mSearchView
				.findViewById(R.id.freeroom_layout_dialog_search_text_summary);
		// the view will be removed or the text changed, no worry
		mSummarySelectedRoomsTextViewSearchMenu.setText("empty");

		// display the previous searches
		mSearchPreviousListView = (ListView) mSearchView
				.findViewById(R.id.freeroom_layout_dialog_search_prev_search_list);
		ArrayAdapter<FRRequestDetails> adapter = new ArrayAdapter<FRRequestDetails>(
				this, R.layout.sdk_list_entry, R.id.sdk_list_entry_text,
				mModel.getPreviousRequest());
		mSearchPreviousListView.setAdapter(adapter);
		mSearchPreviousListView
				.setOnItemClickListener(new OnItemClickListener() {

					@Override
					public void onItemClick(AdapterView<?> arg0, View arg1,
							int arg2, long arg3) {
						FRRequestDetails req = mModel.getPreviousRequest().get(
								arg2);
						if (req != null) {
							fillSearchDialog(req);
						}

						searchDialogExtendMoreTriggered = true;
						if (searchDialogHasHeightExtenstionProblem) {
							prevSearchTitle
									.setText(textTitlePrevious
											+ ": "
											+ getString(R.string.freeroom_search_previous_show));
						}
						searchDialogMissSpaceExtendChangeState(false);
					}
				});

		searchDialogUpperLinearLayout = (LinearLayout) mSearchDialog
				.findViewById(R.id.freeroom_layout_dialog_search_scroll_main);
		searchDialogMainLinearLayout = (LinearLayout) mSearchDialog
				.findViewById(R.id.freeroom_layout_dialog_search_main);

		initSearch();
	}

	/**
	 * Finds if the available height in the search popup is not enough to store
	 * the layout, in order the minimize it's size.
	 * <p>
	 * TODO: this method has an issue and never work on 1st time! The mock
	 * filling seems not to be working regarding to the mesurements.
	 * 
	 * @return true if no problem
	 */
	private boolean findIfHeightProblem() {
		// TODO: seems useless regarding the issue
		searchDialogUpperLinearLayout
				.setLayoutParams(new LinearLayout.LayoutParams(
						LinearLayout.LayoutParams.FILL_PARENT, 1700));
		searchDialogUpperLinearLayout
				.setLayoutParams(new LinearLayout.LayoutParams(
						LinearLayout.LayoutParams.FILL_PARENT,
						LinearLayout.LayoutParams.WRAP_CONTENT));
		FRRequestDetails request = validRequest(true);
		request.setAny(false);
		request.setFav(true);
		request.setUser(true);
		SetArrayList<FRRoom> set = new SetArrayList<FRRoom>(100);
		List<String> uidList = new ArrayList<String>(100);
		for (int i = 0; i < 100; i++) {
			set.add(new FRRoom("BC898989", i + "123"));
			uidList.add(i + "123");
		}
		request.setUidNonFav(set);
		request.setUidList(uidList);
		fillSearchDialog(request);
		mSearchDialog.show();
		// TODO: seems useless regarding the issue
		searchDialogUpperLinearLayout.refreshDrawableState();
		searchDialogUpperLinearLayout.getDrawableState();
		searchDialogMainLayoutHeightAvailable = searchDialogMainLinearLayout
				.getMeasuredHeight();
		boolean toreturn = (searchDialogUpperLinearLayout.getMeasuredHeight() > (searchDialogExtended * searchDialogMainLayoutHeightAvailable));
		fillSearchDialog();
		return toreturn;
	}

	/**
	 * Display the search dialog and checks the compatibility of the UI and make
	 * some change if necessary.
	 */
	private void displaySearchDialog() {
		if (findIfHeightProblem()) {
			searchDialogHasHeightExtenstionProblem = true;
		}
		fillSearchDialog();
		if (!mModel.getPreviousRequest().isEmpty()) {
			searchDialogMissSpaceExtendChangeState(false);
		} else {
			searchDialogUpperLinearLayout
					.setLayoutParams(new LinearLayout.LayoutParams(
							LinearLayout.LayoutParams.FILL_PARENT,
							LinearLayout.LayoutParams.WRAP_CONTENT));
		}
		mSearchDialog.show();
	}

	/**
	 * Modify the height of the upper layout of the search dialog in order to
	 * show/hide the previous requests.
	 * 
	 * @param lessExtend
	 */
	private void searchDialogMissSpaceExtendChangeState(boolean lessExtend) {
		if (searchDialogHasHeightExtenstionProblem) {
			int height = (int) (searchDialogNonExtended * searchDialogMainLayoutHeightAvailable);

			if (lessExtend) {
				height = (int) (searchDialogExtended * searchDialogMainLayoutHeightAvailable);
			}
			searchDialogUpperLinearLayout
					.setLayoutParams(new LinearLayout.LayoutParams(
							LinearLayout.LayoutParams.FILL_PARENT, height));
			searchDialogUpperLinearLayout.refreshDrawableState();
		}
	}

	private void initWarningDialog() {
		AlertDialog.Builder builder = new AlertDialog.Builder(this);
		builder.setTitle(getString(R.string.freeroom_dialog_warn_title));
		builder.setMessage(getString(R.string.freeroom_dialog_warn_delete_fav_text));
		builder.setIcon(R.drawable.warning_white_50);
		builder.setPositiveButton(
				getString(R.string.freeroom_dialog_warn_confirm),
				new AlertDialog.OnClickListener() {

					@Override
					public void onClick(DialogInterface dialog, int which) {
						mModel.resetFavorites();
						mFavoritesAdapter.notifyDataSetChanged();
					}
				});
		builder.setNegativeButton(
				getString(R.string.freeroom_dialog_warn_cancel), null);

		// Get the AlertDialog from create()
		mWarningDialog = builder.create();

		// redefine paramaters to dim screen when displayed
		WindowManager.LayoutParams lp = mWarningDialog.getWindow()
				.getAttributes();
		lp.dimAmount = 0.60f;
		// these doesn't work
		lp.width = LayoutParams.WRAP_CONTENT;
		lp.height = LayoutParams.WRAP_CONTENT;
		mWarningDialog.getWindow().addFlags(
				WindowManager.LayoutParams.FLAG_WATCH_OUTSIDE_TOUCH);
		mWarningDialog.getWindow().addFlags(
				WindowManager.LayoutParams.FLAG_DIM_BEHIND);
		mWarningDialog.getWindow().setAttributes(lp);
	}

	public boolean onTouchEvent(MotionEvent event) {

		if (event.getAction() == MotionEvent.ACTION_OUTSIDE) {
			System.out
					.println("TOuch outside the dialog ******************** ");
			mSearchDialog.dismiss();
		}
		return false;
	}

	/**
	 * Overrides the legacy <code>onKeyDown</code> method in order to close the
	 * dialog if one was opened. TODO: test if really needed.
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
			if (mSearchDialog.isShowing()) {
				mSearchDialog.dismiss();
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
	 * <p>
	 * It doesn't need to start by a space, the text view already contains an
	 * appropriate padding.
	 * 
	 * @param text
	 *            the new summary to be displayed.
	 */
	private void setTextSummary(String text) {
		mTextView.setText(text);
	}

	/**
	 * Constructs the default request and sets it in the model for future use.
	 * You may call <code>refresh</code> in order to actually send it to the
	 * server.
	 * 
	 * @param useFavorites
	 *            if the constructor of the request should consider the
	 *            favorites or not
	 */
	private void initDefaultRequest(boolean useFavorites) {
		mModel.setFRRequestDetails(validRequest(useFavorites), false);
	}

	/**
	 * Construct a valid and default request. If useFavorites is true, it will
	 * check all the favorites for the next valid period, otherwise or if there
	 * are not.
	 * 
	 * @param useFavorites
	 *            if it should consider the favorites or not
	 * @return a valid and default request, based or nor on the favorites.
	 */
	private FRRequestDetails validRequest(boolean useFavorites) {
		OrderMapListFew<String, List<FRRoom>, FRRoom> set = mModel
				.getFavorites();
		FRRequestDetails details = null;
		// if there are no favorites or we dont want to use them.
		if (set.isEmpty() || !useFavorites) {
			// NO FAV = check all free rooms
			// TODO change group accordingly, set to 1 by default and for
			// testing purpose

			details = new FRRequestDetails(FRTimes.getNextValidPeriod(), true,
					new ArrayList<String>(1), true, false, false,
					new SetArrayList<FRRoom>(), 1);
		} else {
			// FAV: check occupancy of ALL favs
			ArrayList<String> array = new ArrayList<String>(set.size());

			addAllFavoriteToCollection(array, AddCollectionCaller.SEARCH, true);

			// TODO change group accordingly, set to 1 by default and for
			// testing purpose

			details = new FRRequestDetails(FRTimes.getNextValidPeriod(), false,
					array, false, true, false, new SetArrayList<FRRoom>(), 1);
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
			FRPeriod period = mModel.getOverAllTreatedPeriod();
			build.append(times.generateFullTimeSummary(period));

			// if the info dialog is opened, we update the CORRECT occupancy
			// with the new data.
			if (mInfoRoomDialog.isShowing()) {
				FRRoom room = mModel.getDisplayedOccupancy().getRoom();
				List<?> list = mModel.getOccupancyResults().get(
						mModel.getKey(room));
				Iterator<?> iter = list.iterator();
				label: while (iter.hasNext()) {
					Object o = iter.next();
					if (o instanceof Occupancy) {
						if (((Occupancy) o).getRoom().getUid()
								.equals(room.getUid())) {
							mModel.setDisplayedOccupancy((Occupancy) o);
							// doesn't work
							mInfoActualOccupationAdapter.notifyDataSetChanged();
							// works!
							displayInfoDialog();
							break label;
						}
					}
				}
			}

			// if there's only one result, we display immediately the info
			// dialog, to get the detailed information (very useful in context
			// of QR codes)
			if (mModel.getOccupancyResults().size() == 1) {
				List<?> list = mModel.getOccupancyResults().get(0);
				if (list != null && list.size() == 1) {
					Object elem = list.get(0);
					if (elem != null && elem instanceof Occupancy) {
						mModel.setDisplayedOccupancy((Occupancy) elem);
						displayInfoDialog();
					}
				}
			}
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
					homeView.displayShareDialog(mOccupancy.getTreatedPeriod(),
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

	private ActualOccupationArrayAdapter<ActualOccupation> mInfoActualOccupationAdapter;

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
			if (mOccupancy.isSetTreatedPeriod()
					&& mOccupancy.getTreatedPeriod() != null) {
				periodTextView.setText(times.generateFullTimeSummary(mOccupancy
						.getTreatedPeriod()));
			} else {
				// TODO: error coming from server ??
				periodTextView.setText("Error: cannot display period");
			}

			ImageView iv = (ImageView) mInfoRoomView
					.findViewById(R.id.freeroom_layout_dialog_info_share);
			setShareClickListener(iv, this, mOccupancy);

			Button share = mInfoRoomDialog
					.getButton(AlertDialog.BUTTON_POSITIVE);
			share.setEnabled(mOccupancy.isIsAtLeastFreeOnce()
					&& !mOccupancy.isIsAtLeastOccupiedOnce());
			setShareClickListener(share, this, mOccupancy);

			ListView roomOccupancyListView = (ListView) mInfoRoomView
					.findViewById(R.id.freeroom_layout_dialog_info_roomOccupancy);
			mInfoActualOccupationAdapter = new ActualOccupationArrayAdapter<ActualOccupation>(
					getApplicationContext(), mOccupancy.getOccupancy(),
					mController, this);
			roomOccupancyListView.setAdapter(mInfoActualOccupationAdapter);

			TextView detailsTextView = (TextView) mInfoRoomView
					.findViewById(R.id.freeroom_layout_dialog_info_details);
			detailsTextView.setText(u.getInfoFRRoom(mOccupancy.getRoom()));
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
		reset();
		resetTimes(request.getPeriod());
		anyButton.setChecked(request.isAny());
		specButton.setChecked(!request.isAny());
		favButton.setChecked(request.isFav());
		userDefButton.setChecked(request.isUser());
		freeButton.setChecked(request.isOnlyFreeRooms());
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
			mSummarySelectedRoomsTextViewSearchMenu.setText(u
					.getSummaryTextFromCollection(selectedRooms));
		}
		updateDateTimePickersAndButtons();

		// MUST be the last action: after all field are set, check if the
		// request is valid
		searchButton.setEnabled(auditSubmit() == 0);
	}

	private void share(FRPeriod mPeriod, FRRoom mRoom, boolean withFriends,
			String toShare) {
		WorkingOccupancy work = new WorkingOccupancy(mPeriod, mRoom);
		ImWorkingRequest request = new ImWorkingRequest(work,
				mModel.getAnonymID());
		mController.prepareImWorking(request);
		mModel.setOnlyServer(!withFriends);
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
		String sharing = u.wantToShare(mPeriod, mRoom, toShare);
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
				getString(R.string.freeroom_share_intent_title)));
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
	private Button userDefAddButton;
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

		mOptionalLineLinearLayoutWrapper = (LinearLayout) mSearchDialog
				.findViewById(R.id.freeroom_layout_dialog_search_opt_line_wrapper);
		mOptionalLineLinearLayoutContainer = (LinearLayout) mSearchDialog
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
				getString(R.string.freeroom_pattern_day_format_default));
		timeFormat = new SimpleDateFormat(
				getString(R.string.freeroom_pattern_hour_format_default));
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
		mSummarySelectedRoomsTextView.setText(u
				.getSummaryTextFromCollection(selectedRooms));
		mSummarySelectedRoomsTextViewSearchMenu.setText(u
				.getSummaryTextFromCollection(selectedRooms));
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
					mSummarySelectedRoomsTextViewSearchMenu.setText(u
							.getSummaryTextFromCollection(selectedRooms));
					displayAddRoomDialog(AddRoomCaller.SEARCH);
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

		userDefAddButton = (Button) mSearchView
				.findViewById(R.id.freeroom_layout_dialog_search_user_add);
		userDefAddButton.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				displayAddRoomDialog(AddRoomCaller.SEARCH);
			}
		});

		userDefEditButton = (Button) mSearchView
				.findViewById(R.id.freeroom_layout_dialog_search_user_edit);
		userDefEditButton.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO: display something else! Edit, not adding!!!
				displayAddRoomDialog(AddRoomCaller.SEARCH);
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

		// on vertical screens, choose fav and choose user-def are vertically
		// aligned
		// on horizontal screens, there are horizontally aligned.
		if (activityHeight > activityWidth) {
			LinearLayout mLinearLayout = (LinearLayout) mSearchDialog
					.findViewById(R.id.freeroom_layout_dialog_search_opt_line_semi);
			mLinearLayout.setOrientation(LinearLayout.VERTICAL);
			if (activityWidth <= 480) {
				LinearLayout header_main = (LinearLayout) mSearchView
						.findViewById(R.id.freeroom_layout_dialog_search_upper_main);
				header_main.setOrientation(LinearLayout.VERTICAL);
				LinearLayout header_1st = (LinearLayout) mSearchView
						.findViewById(R.id.freeroom_layout_dialog_search_upper_first);
				LinearLayout header_2nd = (LinearLayout) mSearchView
						.findViewById(R.id.freeroom_layout_dialog_search_upper_second);
				header_1st.getLayoutParams().height = LayoutParams.WRAP_CONTENT;
				header_1st.getLayoutParams().width = LayoutParams.FILL_PARENT;
				header_2nd.getLayoutParams().height = LayoutParams.WRAP_CONTENT;
				header_2nd.getLayoutParams().width = LayoutParams.FILL_PARENT;
			}
		}
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

		// click on magnify glass on the keyboard
		mAutoCompleteSuggestionInputBarElement
				.setOnEditorActionListener(new OnEditorActionListener() {
					@Override
					public boolean onEditorAction(TextView v, int actionId,
							KeyEvent event) {
						if (actionId == EditorInfo.IME_ACTION_SEARCH) {
							String query = mAutoCompleteSuggestionInputBarElement
									.getInputText();
							dismissSoftKeyBoard(v);
							AutoCompleteRequest request = new AutoCompleteRequest(
									query, 1);
							mController.autoCompleteBuilding(view, request);
						}

						return true;
					}
				});

		// click on BUTTON magnify glass on the inputbar
		mAutoCompleteSuggestionInputBarElement
				.setOnButtonClickListener(new OnClickListener() {
					@Override
					public void onClick(View v) {
						String query = mAutoCompleteSuggestionInputBarElement
								.getInputText();
						if (query.length() >= 2) {
							dismissSoftKeyBoard(v);
							// TODO change group accordingly, set to 1 by
							// default and for testing purpose
							AutoCompleteRequest request = new AutoCompleteRequest(
									query, 1);
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
								// TODO change group accordingly, set to 1 by
								// default and for testing purpose
								// remove this if you don't want auto-complete
								// without pressing the button
								AutoCompleteRequest request = new AutoCompleteRequest(
										text, 1);
								mController.autoCompleteBuilding(view, request);
							}
						}
					}
				});
	}

	private void addAllFavsToAutoComplete() {
		mAutoCompleteSuggestionArrayListFRRoom.clear();
		addAllFavoriteToCollection(mAutoCompleteSuggestionArrayListFRRoom,
				AddCollectionCaller.ADDALLFAV, false);

		mAdapter.notifyDataSetChanged();
	}

	private enum AddCollectionCaller {
		ADDALLFAV, SEARCH;
	}

	/**
	 * Add all the favorites FRRoom to the collection. Caller is needed in order
	 * to have special condition depending on the caller. The collection will be
	 * cleared prior to any adding.
	 * 
	 * @param collection
	 *            collection in which you want the favorites to be added.
	 * @param caller
	 *            identification of the caller, to provide conditions.
	 * @param addOnlyUID
	 *            true to add UID, false to add fully FRRoom object.
	 */
	private void addAllFavoriteToCollection(Collection collection,
			AddCollectionCaller caller, boolean addOnlyUID) {
		collection.clear();
		OrderMapListFew<String, List<FRRoom>, FRRoom> set = mModel
				.getFavorites();
		Iterator<String> iter = set.keySetOrdered().iterator();
		while (iter.hasNext()) {
			String key = iter.next();
			Iterator<FRRoom> iter2 = set.get(key).iterator();
			label: while (iter2.hasNext()) {
				FRRoom mRoom = iter2.next();
				// condition of adding depending on the caller
				if (caller.equals(AddCollectionCaller.ADDALLFAV)) {
					if (selectedRooms.contains(mRoom)) {
						break label;
					}
				}
				if (addOnlyUID) {
					collection.add(mRoom.getUid());
				} else {
					collection.add(mRoom);
				}
			}
		}
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
			mSummarySelectedRoomsTextView.setText(u
					.getSummaryTextFromCollection(selectedRooms));

		} else {
			Log.e(this.getClass().toString(),
					"room cannot be added: already added");
		}
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

		upToEndHourButton.setEnabled(true);
		upToEndSelected = false;

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
	 */
	private void updateDatePickerAndButton() {
		// creating selected time
		Calendar selected = Calendar.getInstance();
		selected.setTimeInMillis(prepareFRFrPeriod().getTimeStampStart());
		showDatePicker.setText(times.getDateText(selected, dateFormat));

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
		showStartTimePicker.setText(generateTime(
				getString(R.string.freeroom_selectstartHour),
				prepareFRFrPeriod().getTimeStampStart()));
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
		showEndTimePicker.setText(generateTime(
				getString(R.string.freeroom_selectendHour), prepareFRFrPeriod()
						.getTimeStampEnd()));
		if (endHourSelected >= FRTimes.LAST_HOUR_CHECK
				|| (endHourSelected == FRTimes.LAST_HOUR_CHECK - 1 && endMinSelected != 0)) {
			addHourButton.setEnabled(false);
		} else {
			addHourButton.setEnabled(true);
		}
		mTimePickerEndDialog.updateTime(endHourSelected, endMinSelected);
	}

	/**
	 * Generates the start and end time summary. On small screens, specific
	 * start and end are not written.
	 * 
	 * @param prefix
	 *            eg. "start"
	 * @param time
	 *            in milliseconds, time to display
	 * @return a formatted time with an optional prefix.
	 */
	private String generateTime(String prefix, long time) {
		String returned = "";
		if (activityWidth < 480) {
			returned = timeFormat.format(new Date(time));
		} else {
			returned = prefix + " " + timeFormat.format(new Date(time));
		}
		return returned;
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
			addAllFavoriteToCollection(mUIDList, AddCollectionCaller.SEARCH,
					true);
		}
		SetArrayList<FRRoom> userDef = new SetArrayList<FRRoom>(
				selectedRooms.size());
		if (userDefButton.isChecked()) {
			Iterator<FRRoom> iter = selectedRooms.iterator();
			while (iter.hasNext()) {
				FRRoom room = iter.next();
				userDef.add(room);
				mUIDList.add(room.getUid());
			}
		}

		boolean any = anyButton.isChecked();
		boolean fav = favButton.isChecked();
		boolean user = userDefButton.isChecked();
		// TODO change group accordingly, set to 1 by default and for testing
		// purpose
		FRRequestDetails details = new FRRequestDetails(period,
				freeButton.isChecked(), mUIDList, any, fav, user, userDef, 1);
		mModel.setFRRequestDetails(details, true);
		refresh();
		mSearchDialog.dismiss();

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
		boolean isFavEmpty = mModel.getFavorites().isEmpty();
		if (favButton.isChecked()
				&& isFavEmpty
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
		// TODO: syso
		System.out.println(mModel.getAutoComplete().values().size());
		while (iter.hasNext()) {
			List<FRRoom> list = iter.next();
			// TODO: syso
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

		if (mSearchByUriTriggered) {
			searchByUriMakeRequest(mAutoCompleteSuggestionArrayListFRRoom);
		}

		mAdapter.notifyDataSetChanged();
	}

	@Override
	public void refreshOccupancies() {
		refresh();
	}

}