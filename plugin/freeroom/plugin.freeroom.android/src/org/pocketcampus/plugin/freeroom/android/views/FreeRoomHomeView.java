package org.pocketcampus.plugin.freeroom.android.views;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Collection;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;

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
import org.pocketcampus.plugin.freeroom.android.FreeRoomModel.HomeBehaviourRoom;
import org.pocketcampus.plugin.freeroom.android.FreeRoomModel.HomeBehaviourTime;
import org.pocketcampus.plugin.freeroom.android.FreeRoomModel.TimeLanguage;
import org.pocketcampus.plugin.freeroom.android.adapter.ActualOccupationArrayAdapter;
import org.pocketcampus.plugin.freeroom.android.adapter.ExpandableListViewAdapter;
import org.pocketcampus.plugin.freeroom.android.adapter.ExpandableListViewFavoriteAdapter;
import org.pocketcampus.plugin.freeroom.android.adapter.FRRoomSuggestionArrayAdapter;
import org.pocketcampus.plugin.freeroom.android.adapter.PreviousRequestArrayAdapter;
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
import org.pocketcampus.plugin.freeroom.shared.RegisterUser;
import org.pocketcampus.plugin.freeroom.shared.WorkingOccupancy;
import org.pocketcampus.plugin.freeroom.shared.utils.FRStruct;
import org.pocketcampus.plugin.freeroom.shared.utils.FRTimes;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.app.TimePickerDialog.OnTimeSetListener;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnDismissListener;
import android.content.DialogInterface.OnShowListener;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Rect;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings.Secure;
import android.text.format.DateFormat;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnLongClickListener;
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
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.TextView.OnEditorActionListener;
import android.widget.TimePicker;

import com.markupartist.android.widget.ActionBar.Action;
import com.taig.pmc.PopupMenuCompat;

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
	 * Dialog that holds the WARNING Dialog (with two button: confirm/cancel).
	 */
	private AlertDialog mWarningDialog;
	/**
	 * Dialog that holds the ERROR Dialog (with one button: dismiss)
	 */
	private AlertDialog mErrorDialog;
	/**
	 * View that holds the PARAM dialog content, defined in xml in layout
	 * folder.
	 */
	private View mParamView;
	/**
	 * Dialog that holds the PARAM Dialog.
	 */
	private AlertDialog mParamDialog;
	/**
	 * View that holds the WELCOME dialog content, defined in xml in layout
	 * folder.
	 * <p>
	 * TODO: beta-only
	 */
	private View mWelcomeView;
	/**
	 * Dialog that holds the WELCOME Dialog.
	 * <p>
	 * TODO: beta-only
	 */
	private AlertDialog mWelcomeDialog;

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
	 * Authorize the change of date for search dialog.
	 */
	private boolean changeDateAuthorized = false;
	/**
	 * Stores the height available
	 */
	private int searchDialogMainLayoutHeightAvailable = 0;
	/**
	 * Stores if the screen is too small
	 */
	private boolean searchDialogHasHeightExtenstionProblem = true;
	/**
	 * Ratio of dialog that should be occupied with searches input when NOT
	 * displaying previous request.
	 */
	private double searchDialogNonExtended = 0.90;
	/**
	 * Ratio of dialog that should be occupied with searches input when
	 * displaying previous request.
	 */
	private double searchDialogExtended = 0.10;
	/**
	 * Stores if the previous search has been hidden (the rest is more
	 * extended).
	 */
	private boolean searchDialogExtendMoreTriggered = false;
	/**
	 * Text for "Previous request"
	 */
	private String textTitlePrevious = "mock text";
	/**
	 * Array adapter for previous FRRequest.
	 */
	private ArrayAdapter<FRRequestDetails> mPrevRequestAdapter;

	/* UI ELEMENTS FOR DIALOGS - FAVORITES */

	/* UI ELEMENTS FOR DIALOGS - ADDROOM */

	/* UI ELEMENTS FOR DIALOGS - SHARE */

	/**
	 * TextView summarizing the share intent/text/information that will be sent
	 * to friend/server.
	 */
	private TextView mShareDialogTextViewSummarySharing;
	/**
	 * EditText to share the activity/work the user is doing.
	 */
	private EditText mShareDialogEditTextMessageWorking;

	/* OTHER UTILS */
	/**
	 * Enum to have types and store the last caller of the "Add" dialog.
	 * 
	 */
	private enum AddRoomCaller {
		FAVORITES, SEARCH, UNDEF;
	}

	/**
	 * Stores the last caller of the ADDROOM dialog.
	 */
	private AddRoomCaller lastCaller = AddRoomCaller.UNDEF;

	/* ACTIONS FOR THE ACTION BAR */
	/**
	 * Action to open the overflow actions.
	 * <p>
	 * ALL the actions are in overflow, even if already visible.
	 */
	private Action overflow = new Action() {
		public void performAction(View view) {
			showPopupMenuCompat(view);
		}

		public int getDrawable() {
			return R.drawable.ic_action_overflow;
		}
	};
	/**
	 * Action to open the settings.
	 * <p>
	 * Only added conditionally. Otherwise, go through menu or overflow action.
	 */
	private Action settings = new Action() {
		public void performAction(View view) {
			mParamDialog.show();
		}

		public int getDrawable() {
			return R.drawable.ic_action_settings;
		}
	};
	/**
	 * Action to perform a customized search, by showing the search dialog.
	 */
	private Action search = new Action() {
		public void performAction(View view) {
			displaySearchDialog();
		}

		public int getDrawable() {
			return R.drawable.ic_action_search;
		}
	};

	/**
	 * Action to edit the user's favorites, by showing the favorites dialog.
	 * <p>
	 * Only added conditionally. Otherwise, go through menu or overflow action.
	 */
	private Action editFavorites = new Action() {
		public void performAction(View view) {
			mFavoritesAdapter.notifyDataSetChanged();
			mFavoritesDialog.show();
		}

		public int getDrawable() {
			return R.drawable.ic_action_important;
		}
	};

	/**
	 * Action to refresh the data (it sends the same stored request again if not
	 * outdated, or generates a new request).
	 * <p>
	 * TODO: useful? useless ? delete !
	 * <p>
	 * Only added conditionally. Otherwise, go through menu or overflow action.
	 */
	private Action refresh = new Action() {
		public void performAction(View view) {
			defaultMainStart();
		}

		public int getDrawable() {
			return R.drawable.ic_action_refresh;
		}
	};

	/* MAIN ACTIVITY - OVERRIDEN METHODS */

	@Override
	protected Class<? extends PluginController> getMainControllerClass() {
		return FreeRoomController.class;
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu items for use in the action bar
		MenuInflater inflater = getMenuInflater();
		inflater.inflate(R.menu.main_activity_actions, menu);
		return super.onCreateOptionsMenu(menu);
	}

	public void showPopupMenuCompat(View v) {
		PopupMenuCompat menu = PopupMenuCompat.newInstance(this, v);
		menu.inflate(R.menu.main_activity_actions);
		menu.setOnMenuItemClickListener(new PopupMenuCompat.OnMenuItemClickListener() {
			@Override
			public boolean onMenuItemClick(MenuItem item) {
				return onOptionsItemSelected(item);
			}
		});

		menu.show();
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle presses on the action bar items
		switch (item.getItemId()) {
		case R.id.freeroom_action_search:
			mSearchDialog.show();
			return true;
		case R.id.freeroom_action_favorites:
			mFavoritesDialog.show();
			return true;
		case R.id.freeroom_action_refresh:
			// refresh if no timeout, otherwise new default request.
			defaultMainStart();
			return true;
		case R.id.freeroom_action_settings:
			mParamDialog.show();
			return true;
		case R.id.freeroom_action_help:
			// TODO: do something
			return true;
		case R.id.freeroom_action_about:
			// TODO: do something
			return true;
		default:
			return super.onOptionsItemSelected(item);
		}
	}

	@Override
	protected void onDisplay(Bundle savedInstanceState,
			PluginController controller) {
		// Tracker
		Tracker.getInstance().trackPageView("freeroom");

		// Get and cast the controller and model
		mController = (FreeRoomController) controller;
		mModel = (FreeRoomModel) controller.getModel();
		times = mModel.getFRTimesClient(this);
		u = new FRUtilsClient(this);

		// Setup the layout
		mLayoutInflater = this.getLayoutInflater();
		titleLayout = new StandardTitledLayout(this);
		mainLayout = (LinearLayout) mLayoutInflater.inflate(
				R.layout.freeroom_layout_home, null);
		// The ActionBar is added automatically when you call setContentView
		setContentView(titleLayout);
		setTitle();

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
		// on resuming, u may have gone null, go direct start.
		if (u == null) {
			defaultMainStart();
			return;
		}
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
	 * <p>
	 * If a previous request exists and it's not outdated, it wont construct a
	 * new request but use this one instead.
	 */
	private void defaultMainStart() {
		if (mController != null && mModel != null) {
			u.logV("Starting in default mode.");
			FRRequestDetails req = mModel.getFRRequestDetails();
			// if no previous request or it's outdated
			long timeOut = mModel.getMinutesRequestTimeOut()
					* FRTimes.ONE_MIN_IN_MS;
			// TODO: remove this line.
			timeOut = FRTimes.ONE_SEC_IN_MS * 15;
			if (req == null || req.isOutDated(timeOut)) {
				initDefaultRequest(false);
			} else {
				u.logV("existing request will be reused");
			}
			refresh();
			u.logV("Successful start in default mode: wait for server response.");
		} else {
			System.err
					.println("Controller or Model not defined: cannot start default mode.");
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
			mPrevRequestAdapter.notifyDataSetChanged();
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

		// search action is always there.
		addActionToActionBar(search);
		// on tablet, put all the actions, without the overflow.
		if (isLandscape()) {
			addActionToActionBar(editFavorites);
			addActionToActionBar(settings);
			addActionToActionBar(refresh);
		} else {
			// on phones, put all the other actions in the action overflow.
			addActionToActionBar(overflow);
		}

		initInfoDialog();
		initSearchDialog();
		initFavoritesDialog();
		initAddRoomDialog();
		initShareDialog();
		initWarningDialog();
		initErrorDialog();
		initParamDialog();
		initWelcomeDialog();
		if (!mModel.getRegisteredUser()) {
			mWelcomeDialog.show();
		}
	}

	/**
	 * Inits the dialog to diplay the information about a room.
	 */
	private void initInfoDialog() {
		// Instantiate an AlertDialog.Builder with its constructor
		AlertDialog.Builder builder = new AlertDialog.Builder(this);
		builder.setTitle("Mock text");
		builder.setIcon(R.drawable.ic_action_view_as_list);
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
				// TODO: tracker:
			}
		});
	}

	/**
	 * Inits the dialog to diplay the information about a room.
	 * <p>
	 * TODO: beta-only
	 */
	private void initWelcomeDialog() {
		// Instantiate an AlertDialog.Builder with its constructor
		AlertDialog.Builder builder = new AlertDialog.Builder(this);
		builder.setTitle(getString(R.string.freeroom_welcome_title));
		builder.setIcon(R.drawable.ic_action_about);
		builder.setNeutralButton(getString(R.string.freeroom_welcome_dismiss),
				null);

		// Get the AlertDialog from create()
		mWelcomeDialog = builder.create();

		// redefine paramaters to dim screen when displayed
		WindowManager.LayoutParams lp = mWelcomeDialog.getWindow()
				.getAttributes();
		lp.dimAmount = 0.60f;
		// these doesn't work
		lp.width = LayoutParams.FILL_PARENT;
		lp.height = LayoutParams.WRAP_CONTENT;
		mWelcomeDialog.getWindow().addFlags(
				WindowManager.LayoutParams.FLAG_WATCH_OUTSIDE_TOUCH);
		mWelcomeDialog.getWindow().addFlags(
				WindowManager.LayoutParams.FLAG_DIM_BEHIND);
		mWelcomeDialog.getWindow().setAttributes(lp);

		mWelcomeView = mLayoutInflater.inflate(
				R.layout.freeroom_layout_dialog_welcome, null);

		// these work perfectly
		mWelcomeView.setMinimumWidth((int) (activityWidth * 0.9f));
		mWelcomeView.setMinimumHeight((int) (activityHeight * 0.8f));

		mWelcomeDialog.setView(mWelcomeView);
		mWelcomeDialog.setOnDismissListener(new OnDismissListener() {

			@Override
			public void onDismiss(DialogInterface arg0) {
				if (!mModel.getRegisteredUser()) {
					mWelcomeDialog.show();
					showErrorDialog(getString(R.string.freeroom_welcome_error));
				}
			}
		});

		TextView configText = (TextView) mWelcomeView
				.findViewById(R.id.freeroom_layout_dialog_welcome_config);
		configText.setText(getConfig(true));

		final EditText emailText = (EditText) mWelcomeView
				.findViewById(R.id.freeroom_layout_dialog_welcome_email);
		final Button registerUserBeta = (Button) mWelcomeView
				.findViewById(R.id.freeroom_layout_dialog_welcome_register);

		final IFreeRoomView view = this;
		registerUserBeta.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				String email = emailText.getText().toString();
				if (validEmail(email)) {
					RegisterUser req = new RegisterUser(email, mModel
							.getAnonymID(), getConfig(false));
					mController.sendRegisterUser(req, view);
					registerUserBeta
							.setText(getString(R.string.freeroom_welcome_submitting));
					registerUserBeta.setEnabled(false);
					dismissSoftKeyBoard(arg0);
				}
			}
		});
	}

	/**
	 * Tries to validate an email. If error, display an error message in a
	 * dialog.
	 * <p>
	 * TODO: beta-only
	 * 
	 * @param email
	 *            the email to test
	 * @return true if email is well-formed.
	 */
	private boolean validEmail(String email) {
		if (u.validEmail(email)) {
			return true;
		} else {
			showErrorDialog(getString(R.string.freeroom_welcome_invalid_mail));
			return false;
		}
	}

	/**
	 * To be called when the server validates the registration, to change the
	 * display of the welcome popup.
	 * <p>
	 * TODO: beta-only
	 */
	private void validateRegistration() {
		LinearLayout before = (LinearLayout) mWelcomeView
				.findViewById(R.id.freeroom_layout_dialog_welcome_before);
		LinearLayout after = (LinearLayout) mWelcomeView
				.findViewById(R.id.freeroom_layout_dialog_welcome_after);
		after.setVisibility(LinearLayout.VISIBLE);
		before.setVisibility(LinearLayout.GONE);
	}

	@Override
	public void errorRegister(String string) {
		// TODO beta-test only.
		Button registerUserBeta = (Button) mWelcomeView
				.findViewById(R.id.freeroom_layout_dialog_welcome_register);
		registerUserBeta.setEnabled(true);
		showErrorDialog(string);
	}

	@Override
	public void validateRegister() {
		// TODO beta-test only.
		validateRegistration();
	}

	/**
	 * Inits the dialog to diplay the favorites.
	 */
	private ExpandableListViewFavoriteAdapter mFavoritesAdapter;

	private void initFavoritesDialog() {
		// Instantiate an AlertDialog.Builder with its constructor
		AlertDialog.Builder builder = new AlertDialog.Builder(this);
		builder.setTitle(getString(R.string.freeroom_dialog_fav_title));
		builder.setIcon(R.drawable.ic_action_important);
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
		builder.setIcon(R.drawable.ic_action_new);

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

		mAddRoomDialog.setOnDismissListener(new OnDismissListener() {

			@Override
			public void onDismiss(DialogInterface dialog) {
				if (AddRoomCaller.SEARCH.equals(lastCaller)) {
					searchButton.setEnabled(auditSubmit() == 0);
				}
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
		builder.setIcon(R.drawable.ic_action_share);

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

		mShareDialog.setOnDismissListener(new OnDismissListener() {

			@Override
			public void onDismiss(DialogInterface dialog) {
				// TODO remove text + empty spinner
			}
		});

		mShareDialogTextViewSummarySharing = (TextView) mShareView
				.findViewById(R.id.freeroom_layout_dialog_share_textBasic);

		mShareDialogEditTextMessageWorking = (EditText) mShareView
				.findViewById(R.id.freeroom_layout_dialog_share_text_edit);

		mShareDialog.setView(mShareView);

	}

	/**
	 * Notify when the EditText (Message-Working) is updated to generate the
	 * summary and dimiss the keyboard.
	 * 
	 * @param mPeriod
	 *            period selected.
	 * @param mRoom
	 *            room selected.
	 */
	private void shareDialogEditTextMessageWorkingUpdated(
			final FRPeriod mPeriod, final FRRoom mRoom) {
		String text = mShareDialogEditTextMessageWorking.getText().toString();
		if (text == null || text.length() == 0) {
			text = "...";
		}
		mShareDialogTextViewSummarySharing.setText(u.wantToShare(mPeriod,
				mRoom, text));
		dismissSoftKeyBoard(mShareDialogTextViewSummarySharing);
	}

	public void displayShareDialog(final FRPeriod mPeriod, final FRRoom mRoom) {

		mShareDialog.hide();
		mShareDialog.show();

		mShareDialogTextViewSummarySharing.setText(u.wantToShare(mPeriod,
				mRoom, "..."));

		mShareDialogEditTextMessageWorking
				.setOnEditorActionListener(new OnEditorActionListener() {

					@Override
					public boolean onEditorAction(TextView arg0, int arg1,
							KeyEvent arg2) {
						shareDialogEditTextMessageWorkingUpdated(mPeriod, mRoom);
						return true;
					}
				});

		Button shareWithServer = mShareDialog
				.getButton(DialogInterface.BUTTON_NEUTRAL);
		shareWithServer.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				share(mPeriod, mRoom, false, mShareDialogEditTextMessageWorking
						.getText().toString());
				mShareDialog.dismiss();
			}
		});

		Button shareWithFriends = mShareDialog
				.getButton(DialogInterface.BUTTON_POSITIVE);
		shareWithFriends.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				share(mPeriod, mRoom, true, mShareDialogEditTextMessageWorking
						.getText().toString());
				mShareDialog.dismiss();
			}
		});

		final Spinner spinner = (Spinner) mShareView
				.findViewById(R.id.freeroom_layout_dialog_share_spinner_course);
		// Create an ArrayAdapter using the string array and a default spinner
		// layout
		// TODO: get that from the occupancy data... ++ string
		ArrayList<String> suggest = new ArrayList<String>();
		// 1st result is the "title"
		suggest.add("others' activity");
		// TODO: get this from the selected occupancy...
		suggest.add("SwEng");
		suggest.add("NetSec");

		ArrayAdapter<String> adapter = new ArrayAdapter<String>(this,
				android.R.layout.simple_spinner_item, suggest);
		// Specify the layout to use when the list of choices appears
		adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		// Apply the mFavoritesAdapter to the spinner
		spinner.setAdapter(adapter);

		spinner.setOnItemSelectedListener(new OnItemSelectedListener() {

			@Override
			public void onItemSelected(AdapterView<?> arg0, View arg1,
					int arg2, long arg3) {
				// item 0 is the title
				if (arg2 != 0) {
					mShareDialogEditTextMessageWorking.setText(arg0
							.getItemAtPosition(arg2).toString());
				}
				shareDialogEditTextMessageWorkingUpdated(mPeriod, mRoom);
				spinner.setSelection(0);
			}

			@Override
			public void onNothingSelected(AdapterView<?> arg0) {
				// do nothing.
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
		builder.setIcon(R.drawable.ic_action_search);

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
		textTitlePrevious = getString(R.string.freeroom_search_previous_search);
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
				initPreviousTitle();
				fillSearchDialog();
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
		mPrevRequestAdapter = new PreviousRequestArrayAdapter<FRRequestDetails>(
				this, this, R.layout.freeroom_layout_list_prev_req,
				R.id.freeroom_layout_prev_req_text, mModel.getPreviousRequest());
		mSearchPreviousListView.setAdapter(mPrevRequestAdapter);
		mSearchPreviousListView
				.setOnItemClickListener(new OnItemClickListener() {

					@Override
					public void onItemClick(AdapterView<?> arg0, View arg1,
							int arg2, long arg3) {
						onFillRequestClickListeners(arg2);
					}
				});

		searchDialogUpperLinearLayout = (LinearLayout) mSearchDialog
				.findViewById(R.id.freeroom_layout_dialog_search_scroll_main);

		initSearch();
	}

	/**
	 * Inits the title for previous request, with empty value if none, with
	 * "show" if not displayed, of "prev request otherwise.
	 */
	private void initPreviousTitle() {
		if (mModel.getPreviousRequest().isEmpty()) {
			prevSearchTitle.setText("");
		} else if (searchDialogHasHeightExtenstionProblem) {
			prevSearchTitle.setText(textTitlePrevious + ": "
					+ getString(R.string.freeroom_search_previous_show));
		} else {
			prevSearchTitle.setText(textTitlePrevious);
		}
	}

	/**
	 * When a Previous Request item is clicked on "play".
	 * 
	 * @param position
	 *            position of the item to replay
	 */
	public void onPlayRequestClickListener(int position) {
		if (onFillRequestClickListeners(position)) {
			// request wont be stored
			prepareSearchQuery(false);
		}
	}

	/**
	 * Generates a String summary of a FRRequestDetails with compatible time and
	 * collections.
	 * 
	 * @param req
	 *            the request to summarize
	 * @return summary of the request
	 */
	public String FRRequestToString(FRRequestDetails req) {
		StringBuilder build = new StringBuilder(100);
		build.append(times.formatTimePeriod(req.getPeriod(), true, false));
		build.append(" ");
		int max = 50;

		if (req.isAny()) {
			build.append(getString(R.string.freeroom_search_any));
		} else {
			build.append(getString(R.string.freeroom_search_spec));
			build.append(": ");
			if (req.isFav()) {
				build.append(getString(R.string.freeroom_search_favorites));
				build.append("; ");
			}
			if (req.isOnlyFreeRooms()) {
				build.append(getString(R.string.freeroom_search_only_free));
				build.append("; ");
			}
			if (req.isUser()) {
				build.append(getString(R.string.freeroom_search_userdef));
				build.append(u.getSummaryTextFromCollection(req.getUidNonFav(),
						"", max));
			}
		}
		build.setLength(max);
		return build.toString();
	}

	/**
	 * When a Previous Request item is clicked on "remove".
	 * 
	 * @param position
	 *            position of the item to remove
	 */
	public void onRemoveRequestClickListener(int position) {
		mModel.removeRequest(position);
		mPrevRequestAdapter.notifyDataSetChanged();
		if (mModel.getPreviousRequest().isEmpty()) {
			initPreviousTitle();
			displaySearchDialog();
		}
	}

	/**
	 * Fill the search menus with the selected previous request.
	 * 
	 * @param position
	 *            position of the item to use to refill.
	 */
	private boolean onFillRequestClickListeners(int position) {
		searchDialogExtendMoreTriggered = true;
		if (searchDialogHasHeightExtenstionProblem) {
			prevSearchTitle.setText(textTitlePrevious + ": "
					+ getString(R.string.freeroom_search_previous_show));
		}
		searchDialogMissSpaceExtendChangeState(false);
		FRRequestDetails req = mModel.getPreviousRequest().get(position);
		if (req != null) {
			fillSearchDialog(req);
			return true;
		}
		return false;
	}

	/**
	 * Display the search dialog and checks the compatibility of the UI and make
	 * some change if necessary.
	 */
	private void displaySearchDialog() {
		if (!mModel.getPreviousRequest().isEmpty()) {
			searchDialogMainLayoutHeightAvailable = Math.max(
					mSearchView.getMeasuredHeight(), mSearchView.getHeight());
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
		builder.setIcon(R.drawable.ic_action_warning);
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

	private void initErrorDialog() {
		AlertDialog.Builder builder = new AlertDialog.Builder(this);
		builder.setTitle(getString(R.string.freeroom_dialog_error_title));
		builder.setIcon(R.drawable.ic_action_error);
		builder.setNeutralButton(R.string.freeroom_dialog_error_dismiss, null);

		// Get the AlertDialog from create()
		mErrorDialog = builder.create();

		// redefine paramaters to dim screen when displayed
		WindowManager.LayoutParams lp = mErrorDialog.getWindow()
				.getAttributes();
		lp.dimAmount = 0.60f;
		// these doesn't work
		lp.width = LayoutParams.WRAP_CONTENT;
		lp.height = LayoutParams.WRAP_CONTENT;
		mErrorDialog.getWindow().addFlags(
				WindowManager.LayoutParams.FLAG_WATCH_OUTSIDE_TOUCH);
		mErrorDialog.getWindow().addFlags(
				WindowManager.LayoutParams.FLAG_DIM_BEHIND);
		mErrorDialog.getWindow().setAttributes(lp);

		// reset the message when dismiss
		// (to avoid showing with previous message!)
		mErrorDialog.setOnDismissListener(new OnDismissListener() {

			@Override
			public void onDismiss(DialogInterface dialog) {
				mErrorDialog.setMessage("");

			}
		});
	}

	/**
	 * Show the error dialog with the given message.
	 * 
	 * @param text
	 *            message to display.
	 */
	private void showErrorDialog(String text) {
		// error dialog may be null at init time!
		if (mErrorDialog != null) {
			mErrorDialog.setMessage(text);
			mErrorDialog.show();
		}
	}

	/**
	 * Inits the dialog to display the parameters.
	 */
	private void initParamDialog() {
		// Instantiate an AlertDialog.Builder with its constructor
		AlertDialog.Builder builder = new AlertDialog.Builder(this);
		builder.setTitle("Settings"); // TODO: string
		builder.setIcon(R.drawable.ic_action_settings);

		// Get the AlertDialog from create()
		mParamDialog = builder.create();

		// redefine paramaters to dim screen when displayed
		WindowManager.LayoutParams lp = mParamDialog.getWindow()
				.getAttributes();
		lp.dimAmount = 0.60f;
		// these doesn't work
		lp.width = LayoutParams.FILL_PARENT;
		lp.height = LayoutParams.WRAP_CONTENT;
		mParamDialog.getWindow().addFlags(
				WindowManager.LayoutParams.FLAG_WATCH_OUTSIDE_TOUCH);
		mParamDialog.getWindow().addFlags(
				WindowManager.LayoutParams.FLAG_DIM_BEHIND);
		mParamDialog.getWindow().setAttributes(lp);

		mParamView = mLayoutInflater.inflate(
				R.layout.freeroom_layout_dialog_param, null);

		// these work perfectly
		mParamView.setMinimumWidth((int) (activityWidth * 0.95f));

		mParamDialog.setView(mParamView);

		// fill with the real value from model!
		initParamDialogData();
		// when dismissing, make a new search with (new) default value
		mParamDialog.setOnDismissListener(new OnDismissListener() {

			@Override
			public void onDismiss(DialogInterface dialog) {
				initDefaultRequest(false);
				refresh();
			}
		});
	}

	private void initParamDialogData() {
		mParamDialogRefreshTimeFormatExample();

		int id;
		RadioButton rd;

		HomeBehaviourRoom room = mModel.getHomeBehaviourRoom();
		switch (room) {
		case ANYFREEROOM:
			id = R.id.freeroom_layout_param_home_any;
			break;
		case FAVORITES:
			id = R.id.freeroom_layout_param_home_fav;
			break;
		case FAVORITES_ONLY_FREE:
			id = R.id.freeroom_layout_param_home_favfree;
			break;
		case LASTREQUEST:
			id = R.id.freeroom_layout_param_home_last;
			break;
		default:
			id = R.id.freeroom_layout_param_home_fav;
			break;
		}
		rd = (RadioButton) mParamView.findViewById(id);
		rd.setChecked(true);

		HomeBehaviourTime time = mModel.getHomeBehaviourTime();
		switch (time) {
		case CURRENT_TIME:
			id = R.id.freeroom_layout_param_home_time_current;
			break;
		case UP_TO_END_OF_DAY:
			id = R.id.freeroom_layout_param_home_time_end;
			break;
		case WHOLE_DAY:
			id = R.id.freeroom_layout_param_home_time_whole;
			break;
		default:
			id = R.id.freeroom_layout_param_home_time_current;
			break;
		}
		rd = (RadioButton) mParamView.findViewById(id);
		rd.setChecked(true);

		TimeLanguage tl = mModel.getTimeLanguage();
		switch (tl) {
		case DEFAULT:
			id = R.id.freeroom_layout_param_time_language_def;
			break;
		case ENGLISH:
			id = R.id.freeroom_layout_param_time_language_en;
			break;
		case FRENCH:
			id = R.id.freeroom_layout_param_time_language_fr;
			break;
		default:
			id = R.id.freeroom_layout_param_time_language_def;
			break;
		}
		rd = (RadioButton) mParamView.findViewById(id);
		rd.setChecked(true);
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
		setTitle();
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
	 * Sets the title to the default value.
	 * <p>
	 * same effect as <br>
	 * <code>String default = getString(R.string.freeroom_title_main_title)</code>
	 * <br>
	 * <code>titleLayout.setTitle(defaultTitle);</code>
	 * 
	 */
	private void setTitle() {
		setTitle(getString(R.string.freeroom_title_main_title));
	}

	/**
	 * Sets the title to the given value.
	 * <p>
	 * same effect as <br>
	 * <code>titleLayout.setTitle(titleValue);</code>
	 * 
	 * @param titleValue
	 *            the new title
	 * 
	 */
	private void setTitle(String titleValue) {
		titleLayout.setTitle(titleValue);
	}

	/**
	 * Constructs the default request and sets it in the model for future use.
	 * You may call <code>refresh</code> in order to actually send it to the
	 * server.
	 * 
	 * @param forceUseFavorites
	 *            if the constructor of the request should consider the
	 *            favorites or not
	 */
	private void initDefaultRequest(boolean forceUseFavorites) {
		u.logV("generating and setting a new default request");
		mModel.setFRRequestDetails(validRequest(forceUseFavorites), false);
		mPrevRequestAdapter.notifyDataSetChanged();
	}

	/**
	 * Construct a valid and default request. If useFavorites is true, it will
	 * check all the favorites for the next valid period, otherwise or if there
	 * are not.
	 * 
	 * @param forceUseFavorites
	 *            if it should consider the favorites or not
	 * @return a valid and default request, based or nor on the favorites.
	 */
	private FRRequestDetails validRequest(boolean forceUseFavorites) {
		OrderMapListFew<String, List<FRRoom>, FRRoom> set = mModel
				.getFavorites();

		// we choose the period according to settings in model.
		FRPeriod period = null;
		HomeBehaviourTime time = mModel.getHomeBehaviourTime();
		if (time.equals(HomeBehaviourTime.CURRENT_TIME)) {
			period = FRTimes.getNextValidPeriod();
		} else if (time.equals(HomeBehaviourTime.UP_TO_END_OF_DAY)) {
			period = FRTimes.getNextValidPeriodTillEndOfDay();
		} else if (time.equals(HomeBehaviourTime.WHOLE_DAY)) {
			period = FRTimes.getNextValidPeriodWholeDay();
		} else {
			u.logE("unknown time behavior: ");
			u.logE(time.name());
			u.logE("going for default value");
			period = FRTimes.getNextValidPeriod();
		}
		System.out.println(period);
		System.out.println(FRTimes.validCalendarsString(period));

		// we choose the request according to the model settings
		HomeBehaviourRoom room = mModel.getHomeBehaviourRoom();
		// if there are favorites and we want: to force their usage, despite of
		// model settings, or the model ask for favorites.
		if (forceUseFavorites || room.equals(HomeBehaviourRoom.FAVORITES)
				|| room.equals(HomeBehaviourRoom.FAVORITES_ONLY_FREE)) {
			if (!set.isEmpty()) {

				// FAV: check occupancy of ALL favs
				ArrayList<String> array = new ArrayList<String>(set.size());

				addAllFavoriteToCollection(array, AddCollectionCaller.SEARCH,
						true);

				// if we want only free favorites.
				boolean onlyFree = room
						.equals(HomeBehaviourRoom.FAVORITES_ONLY_FREE);
				// TODO change group accordingly, set to 1 by default and for
				// testing purpose
				return new FRRequestDetails(period, onlyFree, array, false,
						true, false, new SetArrayList<FRRoom>(), 1);
			} else {
				u.logV("no favorites in model: going for any free room");
				room = HomeBehaviourRoom.ANYFREEROOM;
			}
		}

		if (room.equals(HomeBehaviourRoom.LASTREQUEST)) {
			// TODO
			u.logD("last request is not operational now");
			room = HomeBehaviourRoom.ANYFREEROOM;
		}

		if (!room.equals(HomeBehaviourRoom.ANYFREEROOM)) {
			u.logE("unknown room behavior: ");
			u.logE(room.name());
			u.logE("going for any free room");
		}
		// any free room behavior
		// TODO change group accordingly, set to 1 by default and for
		// testing purpose
		return new FRRequestDetails(period, true, new ArrayList<String>(1),
				true, false, false, new SetArrayList<FRRoom>(), 1);
	}

	/**
	 * Asks the controller to send again the request which was already set in
	 * the model.
	 * <p>
	 * Don't call it before setting a request in the model!
	 */
	private void refresh() {
		setTextSummary(getString(R.string.freeroom_home_please_wait));
		setTitle();
		// cleans the previous results
		mModel.getOccupancyResults().clear();
		mExpListAdapter.notifyDataSetChanged();
		mController.sendFRRequest(this);
	}

	@Override
	public void occupancyResultsUpdated() {
		setTitle();
		String subtitle = "";
		if (mModel.getOccupancyResults().isEmpty()) {
			// TODO: popup with no results message ?
			subtitle = getString(R.string.freeroom_home_error_no_results);
		} else {
			FRRequest request = mModel.getFRRequestDetails();

			String title = "";
			if (request.isOnlyFreeRooms()) {
				title = getString(R.string.freeroom_home_info_free_rooms);
			} else {
				title = getString(R.string.freeroom_home_info_rooms);
			}
			FRPeriod period = mModel.getOverAllTreatedPeriod();
			setTitle(title + times.formatTimePeriod(period, false, false));
			subtitle = times.formatFullDateFullTimePeriod(period);

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

		setTextSummary(subtitle);
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
				((ImageView) shareImageView)
						.setImageResource(R.drawable.ic_action_share_enabled);
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
						.setImageResource(R.drawable.ic_action_share_disabled);
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
				periodTextView.setText(times
						.formatFullDateFullTimePeriod(mOccupancy
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
					getApplicationContext(), mOccupancy, mController, this);
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
				.removeView(mOptionalLineLinearLayoutWrapperFirst);
		if (enabled) {
			mOptionalLineLinearLayoutContainer
					.addView(mOptionalLineLinearLayoutWrapperFirst);
		}
		mOptionalLineLinearLayoutContainer
				.removeView(mOptionalLineLinearLayoutWrapperSecond);
		if (request.isUser()) {
			mOptionalLineLinearLayoutContainer
					.addView(mOptionalLineLinearLayoutWrapperSecond);
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
		CheckBox mShareDialogCheckBoxShareMessageServer = (CheckBox) mShareDialog
				.findViewById(R.id.freeroom_layout_dialog_share_checkbox_server);
		if (mShareDialogCheckBoxShareMessageServer.isChecked()
				&& toShare != null && toShare != "") {
			work.setMessage(toShare);
		}
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

	private LinearLayout mOptionalLineLinearLayoutContainer;
	private LinearLayout mOptionalLineLinearLayoutWrapperFirst;
	private LinearLayout mOptionalLineLinearLayoutWrapperSecond;

	private void initSearch() {

		mOptionalLineLinearLayoutContainer = (LinearLayout) mSearchDialog
				.findViewById(R.id.freeroom_layout_dialog_search_opt_line_container);
		mOptionalLineLinearLayoutWrapperFirst = (LinearLayout) mSearchDialog
				.findViewById(R.id.freeroom_layout_dialog_search_opt_line_wrapper_1st);
		mOptionalLineLinearLayoutWrapperSecond = (LinearLayout) mSearchView
				.findViewById(R.id.freeroom_layout_dialog_search_opt_line_wrapper_2nd);

		selectedRooms = new SetArrayList<FRRoom>();

		// createSuggestionsList();
		// addAllFavsToAutoComplete();
		mAutoCompleteSuggestionArrayListFRRoom = new ArrayList<FRRoom>(10);
		resetTimes();

		UIConstructPickers();

		UIConstructButton();

		// UIConstructInputBar();

		reset();
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
				// TODO do that correctly according to group change.
				if (changeDateAuthorized) {
					mDatePickerDialog.show();
				} else {
					changeDateAuthorized = true;
					showErrorDialog("Changing the current date has been disabled FOR STUDENTS. Blocking this feature was requested by official EPFL Services.");
				}
			}
		});

		showDatePicker.setOnLongClickListener(new OnLongClickListener() {

			@Override
			public boolean onLongClick(View v) {
				mDatePickerDialog.show();
				return true;
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
		mOptionalLineLinearLayoutContainer
				.removeView(mOptionalLineLinearLayoutWrapperSecond);
		selectedRooms.clear();

		mSummarySelectedRoomsTextView.setText(u
				.getSummaryTextFromCollection(selectedRooms));
		mSummarySelectedRoomsTextViewSearchMenu.setText(u
				.getSummaryTextFromCollection(selectedRooms));
		mAutoCompleteSuggestionInputBarElement.setInputText("");

		if (mModel.getFavorites().isEmpty() && !favButton.isChecked()) {
			userDefButton.setChecked(true);
			mOptionalLineLinearLayoutContainer
					.addView(mOptionalLineLinearLayoutWrapperSecond);
			displayAddRoomDialog(AddRoomCaller.SEARCH);
			searchButton.setEnabled(false);
		} else {
			userDefButton.setChecked(false);
			favButton.setChecked(true);
			searchButton.setEnabled(auditSubmit() == 0);
		}
	}

	private void UIConstructButton() {
		specButton = (RadioButton) mSearchView
				.findViewById(R.id.freeroom_layout_dialog_search_spec);
		specButton.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				if (specButton.isChecked()) {
					mOptionalLineLinearLayoutContainer
							.removeView(mOptionalLineLinearLayoutWrapperFirst);
					mOptionalLineLinearLayoutContainer
							.addView(mOptionalLineLinearLayoutWrapperFirst);
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

				// if you don't have favs, ask you to enter some rooms
				// if you have favs, auto-select it, ... but it requires two
				// steps to remove the fav (add user-def, remove fav)
				if (mModel.getFavorites().isEmpty()) {
					mOptionalLineLinearLayoutContainer
							.removeView(mOptionalLineLinearLayoutWrapperSecond);
					mOptionalLineLinearLayoutContainer
							.addView(mOptionalLineLinearLayoutWrapperSecond);
					userDefButton.setChecked(true);
					displayAddRoomDialog(AddRoomCaller.SEARCH);
					// as it's user-defined, we dont check for search button
					// enabled now
					searchButton.setEnabled(false);
				} else {
					favButton.setChecked(true);
					searchButton.setEnabled(auditSubmit() == 0);
				}
			}
		});

		anyButton = (RadioButton) mSearchView
				.findViewById(R.id.freeroom_layout_dialog_search_any);
		anyButton.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				if (anyButton.isChecked()) {
					mOptionalLineLinearLayoutContainer
							.removeView(mOptionalLineLinearLayoutWrapperSecond);
					mOptionalLineLinearLayoutContainer
							.removeView(mOptionalLineLinearLayoutWrapperFirst);
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
				if (userDefButton.isChecked() || !favButton.isChecked()) {
					if (userDefButton.isChecked()) {
						resetUserDefined();
					}
					userDefButton.setChecked(true);

					anyButton.setChecked(false);
					specButton.setChecked(true);
					freeButton.setChecked(false);

					mOptionalLineLinearLayoutContainer
							.removeView(mOptionalLineLinearLayoutWrapperSecond);
					mOptionalLineLinearLayoutContainer
							.addView(mOptionalLineLinearLayoutWrapperSecond);
					mSummarySelectedRoomsTextViewSearchMenu.setText(u
							.getSummaryTextFromCollection(selectedRooms));
					displayAddRoomDialog(AddRoomCaller.SEARCH);
					searchButton.setEnabled(false);
				} else {
					resetUserDefined();
					searchButton.setEnabled(auditSubmit() == 0);
				}
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
				prepareSearchQuery(true);
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

		// for landscape device, mainly tablet, some layout are programatically
		// changed to horizontal values.
		if (isLandscape()) {
			LinearLayout header_main = (LinearLayout) mSearchView
					.findViewById(R.id.freeroom_layout_dialog_search_upper_main);
			header_main.setOrientation(LinearLayout.HORIZONTAL);
			LinearLayout header_1st = (LinearLayout) mSearchView
					.findViewById(R.id.freeroom_layout_dialog_search_upper_first);
			LinearLayout header_2nd = (LinearLayout) mSearchView
					.findViewById(R.id.freeroom_layout_dialog_search_upper_second);
			LinearLayout header_3rd = (LinearLayout) mSearchView
					.findViewById(R.id.freeroom_layout_dialog_search_upper_third);
			LinearLayout.LayoutParams p = new LinearLayout.LayoutParams(0,
					LinearLayout.LayoutParams.FILL_PARENT);
			p.weight = 1;

			header_1st.setLayoutParams(p);
			header_2nd.setLayoutParams(p);
			header_3rd.setLayoutParams(p);

			RadioGroup rg = (RadioGroup) mSearchView
					.findViewById(R.id.freeroom_layout_dialog_search_any_vs_spec);
			rg.setOrientation(RadioGroup.HORIZONTAL);
			RadioGroup.LayoutParams q = new RadioGroup.LayoutParams(
					LayoutParams.FILL_PARENT, LayoutParams.WRAP_CONTENT);
			rg.setLayoutParams(q);

			// int widthPopup = ???
			// cannot get the width of the popup correctly
			// so item will just be one after the other, no rule
			// and no regularization of the layout

			anyButton.setHeight(LayoutParams.FILL_PARENT);
			// anyButton.setWidth(widthPopup / 2);
			specButton.setHeight(LayoutParams.FILL_PARENT);
			// specButton.setWidth(widthPopup / 2);

			LinearLayout mLinearLayout = (LinearLayout) mSearchDialog
					.findViewById(R.id.freeroom_layout_dialog_search_opt_line_wrapper_1st);
			mLinearLayout.setOrientation(LinearLayout.HORIZONTAL);

			freeButton.setHeight(LayoutParams.FILL_PARENT);
			// freeButton.setWidth(widthPopup / 3);
			favButton.setHeight(LayoutParams.FILL_PARENT);
			// favButton.setWidth(widthPopup / 3);
			userDefButton.setHeight(LayoutParams.FILL_PARENT);
			// userDefButton.setWidth(widthPopup / 3);

			searchDialogHasHeightExtenstionProblem = false;
		}
	}

	/**
	 * Check if the height is smaller than the width of the displayed screen.
	 * <p>
	 * As the plugin is NOT sensible to landscape mode, this will ONLY occur on
	 * tablets.
	 * 
	 * @return true if landscape.
	 */
	private boolean isLandscape() {
		return (activityHeight < activityWidth);
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
		// TODO do that correctly according to group change.
		changeDateAuthorized = false;

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
				.removeView(mOptionalLineLinearLayoutWrapperSecond);
		mOptionalLineLinearLayoutContainer
				.removeView(mOptionalLineLinearLayoutWrapperFirst);

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
		showDatePicker.setText(times.formatFullDate(selected));

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
		showStartTimePicker.setText(times.generateTimeSummaryWithPrefix(
				getString(R.string.freeroom_selectstartHour), true, times
						.formatTime(prepareFRFrPeriod().getTimeStampStart(),
								false)));
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
				.setText(times.generateTimeSummaryWithPrefix(
						getString(R.string.freeroom_selectendHour), true, times
								.formatTime(prepareFRFrPeriod()
										.getTimeStampEnd(), false)));
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
	private void prepareSearchQuery(boolean save) {
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
		mModel.setFRRequestDetails(details, save);
		mPrevRequestAdapter.notifyDataSetChanged();
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
		return auditTimesString().equals("") ? 0 : 1;
	}

	/**
	 * Check that the times set are valid, according to the shared definition.
	 * 
	 * @return the errors
	 */
	private String auditTimesString() {
		// NOT EVEN SET, we don't bother checking
		if (yearSelected == -1 || monthSelected == -1
				|| dayOfMonthSelected == -1) {
			return "error time";
		}
		if (startHourSelected == -1 || endHourSelected == -1
				|| startMinSelected == -1 || endMinSelected == -1) {
			return "error time";
		}

		// IF SET, we use the shared method checking the prepared period
		return FRTimes.validCalendarsString(prepareFRFrPeriod());
	}

	/**
	 * This method check if the client is allowed to submit a request to the
	 * server.
	 * 
	 * @return 0 if there is no error and the client can send the request,
	 *         something else otherwise.
	 */
	private int auditSubmit() {
		String error = auditSubmitString();
		TextView tv = (TextView) mSearchView
				.findViewById(R.id.freeroom_layout_dialog_search_validation);
		if (!error.equals("")) {
			// print errors in textView
			tv.setText("Request is invalid.\n" + error);
			tv.setTextColor(Color.WHITE);
			tv.setBackgroundColor(Color.BLACK);
			tv.setVisibility(View.VISIBLE);
			return 1;
		} else {
			tv.setTextColor(Color.BLACK);
			tv.setBackgroundColor(Color.WHITE);
			// no text, not displayed
			tv.setText("");
			tv.setVisibility(View.GONE);
			return 0;
		}
	}

	private String auditSubmitString() {
		String ret = "";
		if (selectedRooms == null
				|| (!anyButton.isChecked() && !favButton.isChecked()
						&& userDefButton.isChecked() && selectedRooms.isEmpty())) {
			ret += "\nSelected rooms are empty, please add some room or select other options.";
		}

		if (anyButton.isChecked()
				&& (favButton.isChecked() || userDefButton.isChecked())) {
			ret += "\nAny free room is incompatbile with favorites and specified room.";
		}
		if (!anyButton.isChecked() && !favButton.isChecked()
				&& !userDefButton.isChecked()) {
			ret += "\nAt least one of any free room, favorites or specified room must be chosen.";
		}
		boolean isFavEmpty = mModel.getFavorites().isEmpty();
		if (favButton.isChecked() && isFavEmpty) {
			if (!userDefButton.isChecked()) {
				ret += "\nYou have no favorites: favorites button don't work, you can't select only this.";
			} else if (userDefButton.isChecked() && selectedRooms.isEmpty()) {
				ret += "\nYou have no favorites: favorites button don't work, you can't select only this.";
				ret += "\nSelected rooms are empty, please add some room or select other options.";
			}
		}
		// we dont allow query all the room, including non-free
		if (anyButton.isChecked() && !freeButton.isChecked()) {
			ret += "\nAny free room must check only free room and is incompatible with non-free rooms.";
		}
		return ret + auditTimesString();
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

	// ******* SETTINGS/PARAMETERS *****///

	/**
	 * Listener to change some model parameter/settings.
	 * 
	 * @param v
	 *            caller view (not used)
	 */
	public void onHomeBehaviourRoomSetFavorites(View v) {
		mModel.setHomeBehaviourRoom(HomeBehaviourRoom.FAVORITES);
	}

	/**
	 * Listener to change some model parameter/settings.
	 * 
	 * @param v
	 *            caller view (not used)
	 */
	public void onHomeBehaviourRoomSetFavoritesFree(View v) {
		mModel.setHomeBehaviourRoom(HomeBehaviourRoom.FAVORITES_ONLY_FREE);
	}

	/**
	 * Listener to change some model parameter/settings.
	 * 
	 * @param v
	 *            caller view (not used)
	 */
	public void onHomeBehaviourRoomSetAnyFreeRoom(View v) {
		mModel.setHomeBehaviourRoom(HomeBehaviourRoom.ANYFREEROOM);
	}

	/**
	 * Listener to change some model parameter/settings.
	 * 
	 * @param v
	 *            caller view (not used)
	 */
	public void onHomeBehaviourRoomSetLastRequest(View v) {
		mModel.setHomeBehaviourRoom(HomeBehaviourRoom.LASTREQUEST);
	}

	/**
	 * Listener to change some model parameter/settings.
	 * 
	 * @param v
	 *            caller view (not used)
	 */
	public void onHomeBehaviourTimeSetCurrent(View v) {
		mModel.setHomeBehaviourTime(HomeBehaviourTime.CURRENT_TIME);
	}

	/**
	 * Listener to change some model parameter/settings.
	 * 
	 * @param v
	 *            caller view (not used)
	 */
	public void onHomeBehaviourTimeSetEndOfDay(View v) {
		mModel.setHomeBehaviourTime(HomeBehaviourTime.UP_TO_END_OF_DAY);
	}

	/**
	 * Listener to change some model parameter/settings.
	 * 
	 * @param v
	 *            caller view (not used)
	 */
	public void onHomeBehaviourTimeSetWholeDay(View v) {
		mModel.setHomeBehaviourTime(HomeBehaviourTime.WHOLE_DAY);
	}

	/**
	 * Listener to change some model parameter/settings.
	 * 
	 * @param v
	 *            caller view (not used)
	 */
	public void onFormattingSetDefault(View v) {
		mModel.setTimeLanguage(TimeLanguage.DEFAULT);
		mParamDialogRefreshTimeFormatExample();
	}

	/**
	 * Listener to change some model parameter/settings.
	 * 
	 * @param v
	 *            caller view (not used)
	 */
	public void onFormattingSetEnglish(View v) {
		mModel.setTimeLanguage(TimeLanguage.ENGLISH);
		mParamDialogRefreshTimeFormatExample();
	}

	/**
	 * Listener to change some model parameter/settings.
	 * 
	 * @param v
	 *            caller view (not used)
	 */
	public void onFormattingSetFrench(View v) {
		mModel.setTimeLanguage(TimeLanguage.FRENCH);
		mParamDialogRefreshTimeFormatExample();
	}

	/**
	 * Refreshes the example text of formatting times.
	 * <p>
	 * Used in param dialog to show the impact of a particular formatting.
	 */
	private void mParamDialogRefreshTimeFormatExample() {
		times = mModel.getFRTimesClient(this);
		TextView tv = (TextView) mParamView
				.findViewById(R.id.freeroom_layout_dialog_param_time_language_example);
		long now = System.currentTimeMillis() + FRTimes.ONE_WEEK_IN_MS;
		Calendar selected = Calendar.getInstance();
		selected.setTimeInMillis(now);
		tv.setText(times.formatFullDate(selected));
	}

	// FOR BETA/DEV

	/**
	 * Return a String representation of device / build / settings.
	 * <p>
	 * It's useful for debugging remote device (beta-testers) and understand
	 * their reports.
	 * 
	 * @param forUser
	 *            true if designed for user display, false if for server
	 *            sending.
	 * @return a String representation of device / build / settings.
	 */
	public String getConfig(boolean forUser) {
		Locale locale = Locale.getDefault();
		Locale english = Locale.ENGLISH;
		// if the user locale is not english
		// we print to him to information in his language.
		boolean printUserLocale = true;
		if (locale.equals(english) || locale.equals(Locale.UK)
				|| locale.equals(Locale.US)) {
			printUserLocale = false;
		}
		if (!forUser) {
			printUserLocale = false;
		}

		StringBuilder config = new StringBuilder(500);
		String s = "\n";
		String l = "/";

		// basic device information
		config.append("*** Device information ***" + s);
		config.append("Brand: " + Build.BRAND + s);
		config.append("Model: " + Build.MODEL + s);
		if (forUser) {
			config.append("Android version: " + Build.VERSION.RELEASE);
			config.append(" (SDK " + Build.VERSION.SDK + ")" + s);
		}

		config.append("Mesured screen size: " + activityHeight + "x"
				+ activityWidth + s);

		config.append("*** Preferences ***" + s);

		// locale name
		if (forUser) {
			config.append("Language: ");
		} else {
			config.append("Locale: ");
			config.append("[" + locale.toString() + "]" + s);
		}
		config.append(locale.getDisplayName(english));
		if (printUserLocale) {
			config.append(" [" + locale.getDisplayName(locale) + "]");
		}
		config.append(s);

		if (!forUser) {
			// language
			config.append("Language: ");
			config.append(locale.getLanguage() + l);
			config.append(locale.getISO3Language() + l);
			config.append(locale.getDisplayLanguage(english));
			if (printUserLocale) {
				config.append("(" + locale.getDisplayLanguage(locale) + ")");
			}

			String var = locale.getVariant();
			if (var != null && !var.equals("")) {
				config.append(l + "Variant:" + locale.getVariant() + l);
				config.append(locale.getDisplayVariant(english));
				if (printUserLocale) {
					config.append("(" + locale.getDisplayVariant(locale) + ")");
				}
			}
			config.append(s);
			// country
			config.append("Country: ");
			config.append(locale.getCountry() + l);
			config.append(locale.getISO3Country() + l);
			config.append(locale.getDisplayCountry(english));
			if (printUserLocale) {
				config.append("(" + locale.getDisplayCountry(locale) + ")");
			}
			config.append(s);
		}

		// time
		Date nowd = new Date(System.currentTimeMillis());
		config.append("Local time: " + nowd.toLocaleString() + s);

		if (!forUser) {
			config.append("*** TIME ***" + s);
			config.append("Default time: " + nowd + s);
			config.append("GMT time: " + nowd.toGMTString() + s);
			long now = System.currentTimeMillis();

			// test android formatting
			config.append("Time/system: " + now + s);
			config.append("Is 24h format: " + DateFormat.is24HourFormat(this)
					+ s);
			java.text.DateFormat df = android.text.format.DateFormat
					.getTimeFormat(this);
			String time = df.format(nowd);
			java.text.DateFormat dd = android.text.format.DateFormat
					.getDateFormat(this);
			String date = dd.format(nowd);
			config.append("Android formatting: " + date + " // " + time + s);
			char[] mmddyyyy = DateFormat.getDateFormatOrder(this);
			config.append("DD/MM/YYYY format: " + Arrays.toString(mmddyyyy) + s);

			// test our own times methods
			config.append("times.formatTime(false): "
					+ times.formatTime(now, false) + s);
			config.append("times.formatTime(true): "
					+ times.formatTime(now, true) + s);

			// one week shift
			now += FRTimes.ONE_WEEK_IN_MS;
			Calendar selected = Calendar.getInstance();
			FRPeriod period = new FRPeriod(now, now + 2
					* FRTimes.ONE_HOUR_IN_MS, false);
			selected.setTimeInMillis(now);
			config.append("times.formatFullDate(): "
					+ times.formatFullDate(selected) + s);
			config.append("times.formatFullDateFullTimePeriod: "
					+ times.formatFullDateFullTimePeriod(period) + s);
		}

		if (!forUser) {
			config.append("*** Other settings ***" + s);

			config.append("ANDROID_ID: "
					+ Secure.getString(getContentResolver(), Secure.ANDROID_ID)
					+ s);
			config.append("Proxy: "
					+ Secure.getString(getContentResolver(), Secure.HTTP_PROXY)
					+ s);
			config.append("Input method: "
					+ Secure.getString(getContentResolver(),
							Secure.DEFAULT_INPUT_METHOD) + s);
			config.append("Wifi ON: "
					+ Secure.getString(getContentResolver(), Secure.WIFI_ON)
					+ s);
			config.append("Network pref: "
					+ Secure.getString(getContentResolver(),
							Secure.NETWORK_PREFERENCE) + s);
			config.append("Non market apps: "
					+ Secure.getString(getContentResolver(),
							Secure.INSTALL_NON_MARKET_APPS) + s);
			config.append("ADB enabled: "
					+ Secure.getString(getContentResolver(), Secure.ADB_ENABLED)
					+ s);

			// hardware informations
			config.append("*** Other hardware informations ***" + s);
			config.append("Version.Codename: " + Build.VERSION.CODENAME + s);
			config.append("Version.Incremental: " + Build.VERSION.INCREMENTAL
					+ s);
			config.append("Version.SKK_int: " + Build.VERSION.SDK_INT + s);
			config.append("Board: " + Build.BOARD + s);
			config.append("Bootloader: " + Build.BOOTLOADER + s);
			config.append("CPU1: " + Build.CPU_ABI + s);
			config.append("CPU2: " + Build.CPU_ABI2 + s);
			config.append("Device:" + Build.DEVICE + s);
			config.append("Display: " + Build.DISPLAY + s);
			config.append("Fingerprint: " + Build.FINGERPRINT + s);
			config.append("Hardware: " + Build.HARDWARE + s);
			config.append("Host: " + Build.HOST + s);
			config.append("ID: " + Build.ID + s);
			config.append("Manufacturer: " + Build.MANUFACTURER + s);
			config.append("Product: " + Build.PRODUCT + s);
			config.append("Radio: " + Build.RADIO + s);
			config.append("Tags: " + Build.TAGS + s);
			config.append("Time of build: " + Build.TIME + s);
			config.append("Type: " + Build.TYPE + s);
			config.append("User: " + Build.USER + s);
		}

		return config.toString();
	}
}