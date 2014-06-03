package org.pocketcampus.plugin.freeroom.android.views;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Collection;
import java.util.Date;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Set;

import org.pocketcampus.android.platform.sdk.core.PluginController;
import org.pocketcampus.android.platform.sdk.tracker.Tracker;
import org.pocketcampus.android.platform.sdk.ui.element.InputBarElement;
import org.pocketcampus.android.platform.sdk.ui.element.OnKeyPressedListener;
import org.pocketcampus.android.platform.sdk.ui.layout.StandardTitledLayout;
import org.pocketcampus.plugin.freeroom.R;
import org.pocketcampus.plugin.freeroom.android.FreeRoomAbstractView;
import org.pocketcampus.plugin.freeroom.android.FreeRoomController;
import org.pocketcampus.plugin.freeroom.android.FreeRoomModel;
import org.pocketcampus.plugin.freeroom.android.FreeRoomModel.ColorBlindMode;
import org.pocketcampus.plugin.freeroom.android.FreeRoomModel.HomeBehaviourRoom;
import org.pocketcampus.plugin.freeroom.android.FreeRoomModel.HomeBehaviourTime;
import org.pocketcampus.plugin.freeroom.android.FreeRoomModel.TimeLanguage;
import org.pocketcampus.plugin.freeroom.android.adapter.ActualOccupationArrayAdapter;
import org.pocketcampus.plugin.freeroom.android.adapter.ExpandableListViewAdapter;
import org.pocketcampus.plugin.freeroom.android.adapter.ExpandableListViewFavoriteAdapter;
import org.pocketcampus.plugin.freeroom.android.adapter.FRRoomRemoveArrayAdapter;
import org.pocketcampus.plugin.freeroom.android.adapter.FRRoomSuggestionArrayAdapter;
import org.pocketcampus.plugin.freeroom.android.adapter.MessageFrequencyArrayAdapter;
import org.pocketcampus.plugin.freeroom.android.adapter.PreviousRequestArrayAdapter;
import org.pocketcampus.plugin.freeroom.android.iface.IFreeRoomView;
import org.pocketcampus.plugin.freeroom.android.utils.FRRequestDetails;
import org.pocketcampus.plugin.freeroom.android.utils.FRTimesClient;
import org.pocketcampus.plugin.freeroom.android.utils.FRUtilsClient;
import org.pocketcampus.plugin.freeroom.android.utils.OrderMapListFew;
import org.pocketcampus.plugin.freeroom.android.utils.SetArrayList;
import org.pocketcampus.plugin.freeroom.shared.ActualOccupation;
import org.pocketcampus.plugin.freeroom.shared.AutoCompleteRequest;
import org.pocketcampus.plugin.freeroom.shared.Constants;
import org.pocketcampus.plugin.freeroom.shared.FRPeriod;
import org.pocketcampus.plugin.freeroom.shared.FRRequest;
import org.pocketcampus.plugin.freeroom.shared.FRRoom;
import org.pocketcampus.plugin.freeroom.shared.ImWorkingRequest;
import org.pocketcampus.plugin.freeroom.shared.MessageFrequency;
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
import android.view.View.OnTouchListener;
import android.view.ViewGroup;
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
import android.widget.ExpandableListView.OnGroupCollapseListener;
import android.widget.ExpandableListView.OnGroupExpandListener;
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
	 * View that holds the ADDFavorites dialog content, defined in xml in layout
	 * folder.
	 */
	private View mAddFavoritesView;
	/**
	 * AlertDialog that holds the ADDFavorites dialog.
	 */
	private AlertDialog mAddFavoritesDialog;

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
	 * View that holds the EDITROOM dialog content, defined in xml in layout
	 * folder.
	 */
	private View mEditRoomView;
	/**
	 * AlertDialog that holds the EDITROOM dialog.
	 */
	private AlertDialog mEditRoomDialog;
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
	/**
	 * View that holds the ImWorking dialog content, defined in xml in layout
	 * folder.
	 */
	private View mImWorkingView;
	/**
	 * Dialog that holds the ImWorking Dialog.
	 */
	private AlertDialog mImWorkingDialog;

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
	 * Button to come back to up of search page.
	 */
	private Button goUpHomeButton;
	/**
	 * Text for "Previous request"
	 */
	private String textTitlePrevious = "mock text";
	/**
	 * Array adapter for previous FRRequest.
	 */
	private ArrayAdapter<FRRequestDetails> mPrevRequestAdapter;

	/* UI ELEMENTS FOR DIALOGS - FAVORITES */

	/**
	 * TextView for autocomplete status for adding favorites.
	 */
	private TextView tvAutcompletStatusFav;

	/* UI ELEMENTS FOR DIALOGS - ADDROOM */

	/**
	 * Adpater for selected room.
	 */
	private ArrayAdapter<FRRoom> selectedRoomArrayAdapter;

	/**
	 * TextView for autocomplete status for adding room to search.
	 */
	private TextView tvAutcompletStatusRoom;

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
	 * Time summary in "working there" dialog.
	 */
	private TextView workingTimeSummary;
	/**
	 * Disclaimer/please wait in "working there" dialog.
	 */
	private TextView workingDisclaimer;
	/**
	 * List of displayed working message.
	 */
	private List<MessageFrequency> workingMessageList;
	/**
	 * Adpater for message and their frequency.
	 */
	private ArrayAdapter<MessageFrequency> workingMessageAdapter;

	/* ACTIONS FOR THE ACTION BAR */
	/**
	 * Action to open the beta registration.
	 * <p>
	 * TODO: beta only (may change to about menu?)
	 */
	private Action betaRegister = new Action() {
		public void performAction(View view) {
			if (mModel.getRegisteredUser()) {
				validateRegistration();
			}
			mWelcomeDialog.show();
		}

		public int getDrawable() {
			return R.drawable.ic_action_about;
		}
	};
	/**
	 * Action to open the overflow actions.
	 * <p>
	 * ALL the actions are in overflow, even if already visible.
	 */
	private Action overflow = new Action() {
		public void performAction(View view) {
			// open the legacy options menu: deprecated on new phones.
			// openOptionsMenu() ;
			// show the compat popup menu.
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
			mSearchDialog.show();
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
	 * Please not that it replays the SAME request if it's not outdated, it wont
	 * generate a new default request!
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
		// on portrait devices, we have both overflow and menu with same
		// options.
		// on landscape devices, non is available.
		if (!isLandscapeTabletMode()) {
			// Inflate the menu items, the same as in overflow action bar
			MenuInflater inflater = getMenuInflater();
			inflater.inflate(R.menu.main_activity_actions, menu);
		}
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
	 * http://occupancy.epfl.ch/content format <br>
	 * with content being the room requested (exact match).
	 * <p>
	 * scheme://freeroom.plugin.pocketcampus.org/service?key=Value <br>
	 * 
	 * with scheme could be http, pocketcampus or "" (empty string) <br>
	 * service could be <br>
	 * show, with key "id" and value the unique UID (exact match). <br>
	 * search, with key "name" and value the start of the name (autocomplete,
	 * not exact match)<br>
	 * match, with key "name" and value the exactly matched name (room or
	 * building, but for rooms prefer uids)<br>
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

						/*
						 * using freeroom links. accepted formats are
						 * 
						 * pocketcampus ://freeroom.plugin.pocketcampus.org/show
						 * ?id=UID where UID is the EPFL id (with OR without the
						 * leading 1201XX)
						 * 
						 * pocketcampus://freeroom.plugin.pocketcampus.org/search
						 * ?name=NAM where NAM is the leading characters of the
						 * room (please note there is a search for NAM* on
						 * server-side so a search for "CO1" will also give
						 * "CO123", and a search for "BC" will also give
						 * "BCH4375", if this room is available)
						 * 
						 * pocketcampus://freeroom.plugin.pocketcampus.org/match
						 * ?name=NAME where NAME is the exact name of the toom
						 * OR the building (if want all BC rooms without BCH)
						 */
						else if ((intentScheme.equalsIgnoreCase("pocketcampus")
								|| intentScheme.equalsIgnoreCase("http") || intentScheme
									.equalsIgnoreCase(""))
								&& intentUriHost
										.equalsIgnoreCase("freeroom.plugin.pocketcampus.org")) {

							if ("/show".equals(intentUriPath)
									&& intentUri.getQueryParameter("id") != null) {
								String uid = intentUri.getQueryParameter("id");
								// removing the epfl "room type" identifier, to
								// keep only the relevant id.
								if (uid.startsWith("1201")) {
									uid = uid.substring(4);
									while (uid.startsWith("0")) {
										uid = uid.substring(1);
									}
								}
								errorIntentHandled = searchByUriPrepareArguments(uid);
							} else if ("/search".equals(intentUriPath)
									&& intentUri.getQueryParameter("name") != null) {
								// the completion is added THERE (%) because the
								// autocomplete method is set to "exactmatch"
								errorIntentHandled = searchByUriPrepareArguments(intentUri
										.getQueryParameter("name") + "%");
							} else if ("/match".equals(intentUriPath)
									&& intentUri.getQueryParameter("name") != null) {
								errorIntentHandled = searchByUriPrepareArguments(intentUri
										.getQueryParameter("name"));
							}
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
		// display an error message when the intent/uri handling lead to a
		// problem.
		showErrorDialog(getString(R.string.freeroom_urisearch_error_basis)
				+ "\n" + errorMessage + "\n"
				+ getString(R.string.freeroom_urisearch_error_end));
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
			if (req == null || req.isOutDated(timeOut)) {
				initDefaultRequest(false);
			} else {
				u.logV("existing request will be reused");
			}
			refresh();
			u.logV("Successful start in default mode: wait for server response.");
		} else {
			// CANT LOG using utils because null after a while.
			Log.e("defaultmainstart",
					"Controller or Model not defined: cannot start default mode.");
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
		if (constraint.length() < Constants.MIN_AUTOCOMPL_LENGTH) {
			return getString(R.string.freeroom_urisearch_error_AutoComplete_error)
					+ " "
					+ getString(R.string.freeroom_urisearch_error_AutoComplete_precond);
		} else {
			mSearchByUriTriggered = true;
			// if the URI is triggered, we want to give access to the room,
			// event if the user might no have right to see the room.
			AutoCompleteRequest req = new AutoCompleteRequest(constraint,
					Math.max(mModel.getGroupAccess(), Integer.MAX_VALUE));
			// set to exact match (if you want autocompletion, please add a "%"
			// to your constraint)
			req.setExactString(true);
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
			// we dont warn if there is more than 1 result
			// please use carefully
			// /show?id=ID, /search?name=NAM and /match?name?NAME

			// search for the rest of the day.
			FRPeriod period = FRTimes.getNextValidPeriodTillEndOfDay();
			FRRequestDetails request = null;
			List<String> uidList = new ArrayList<String>();
			Set<FRRoom> uidNonFav = new HashSet<FRRoom>();

			// maybe find a simpler and more efficient way ?
			// it's not really relevant as these collection are usually
			// relatively small (limited by autocomplete limit on server side)
			Iterator<FRRoom> iter = collection.iterator();
			while (iter.hasNext()) {
				FRRoom room = iter.next();
				uidList.add(room.getUid());
				uidNonFav.add(room);
			}
			request = new FRRequestDetails(period, false, uidList, false,
					false, true, uidNonFav, mModel.getGroupAccess());
			mModel.setFRRequestDetails(request, !empty);
			mPrevRequestAdapter.notifyDataSetChanged();
			refresh();
		}
	}

	/* MAIN ACTIVITY - INITIALIZATION */

	@Override
	public void initializeView() {

		constructKonamiCode();
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
		mExpListView.setOnGroupExpandListener(new OnGroupExpandListener() {

			@Override
			public void onGroupExpand(int groupPosition) {
				// when we expand a group, it gets the focus (highlighted)
				mExpListAdapter.setGroupFocus(groupPosition);
			}
		});
		mExpListView.setOnGroupCollapseListener(new OnGroupCollapseListener() {

			@Override
			public void onGroupCollapse(int groupPosition) {
				// when we collapse a group, no group has focused (highlight)
				mExpListAdapter.setGroupFocus(-1);
			}
		});
		// replay the onTouchEvent on the List to home View.
		mExpListView.setOnTouchListener(new OnTouchListener() {

			@Override
			public boolean onTouch(View v, MotionEvent event) {
				onTouchEvent(event);
				return false;
			}
		});

		// search action is always there, on phones AND tablet modes
		addActionToActionBar(search);
		// on tablet, put all the actions, without the overflow.
		if (isLandscapeTabletMode()) {
			addActionToActionBar(editFavorites);
			addActionToActionBar(settings);
			addActionToActionBar(refresh);
			// TODO: beta only
			addActionToActionBar(betaRegister);
		} else {
			// TODO: beta only
			addActionToActionBar(betaRegister);
			// on phones, put all the other actions in the action overflow.
			addActionToActionBar(overflow);
		}

		initInfoDialog();
		initSearchDialog();
		initFavoritesDialog();
		initAddFavoritesDialog();
		initAddRoomDialog();
		initEditRoomDialog();
		initShareDialog();
		initWarningDialog();
		initErrorDialog();
		initParamDialog();
		initImWorkingDialog();

		// TODO: beta only
		initWelcomeDialog();
		if (!mModel.getRegisteredUser()) {
			mWelcomeDialog.show();
		} else {
			validateRegistration();
		}
	}

	/**
	 * Inits the dialog to diplay the information about a room.
	 */
	private void initInfoDialog() {
		// Instantiate an AlertDialog.Builder with its constructor
		AlertDialog.Builder builder = new AlertDialog.Builder(this);
		builder.setIcon(R.drawable.ic_action_view_as_list);
		// erased when calling the intended method to show the details
		builder.setTitle("Mock title");
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

		ListView lv = (ListView) mInfoRoomView
				.findViewById(R.id.freeroom_layout_dialog_info_roomOccupancy);

		ViewGroup header = (ViewGroup) mLayoutInflater.inflate(
				R.layout.freeroom_layout_dialog_info_header, lv, false);
		lv.addHeaderView(header, null, false);
		ViewGroup footer = (ViewGroup) mLayoutInflater.inflate(
				R.layout.freeroom_layout_dialog_info_footer, lv, false);
		lv.addFooterView(footer, null, false);

		mInfoRoomDialog.setView(mInfoRoomView);

		mInfoRoomDialog.setOnShowListener(new OnShowListener() {
			@Override
			public void onShow(DialogInterface dialog) {
				// Tracker
				Tracker.getInstance().trackPageView("freeroom/details");
			}
		});
	}

	/**
	 * Inits the dialog to show what people are doing.
	 */
	private void initImWorkingDialog() {
		// Instantiate an AlertDialog.Builder with its constructor
		AlertDialog.Builder builder = new AlertDialog.Builder(this);
		builder.setIcon(R.drawable.ic_action_view_as_list);
		// erased when calling the intended method to show the details
		builder.setTitle(getString(R.string.freeroom_whoIsWorking_title));

		// Get the AlertDialog from create()
		mImWorkingDialog = builder.create();

		// redefine paramaters to dim screen when displayed
		WindowManager.LayoutParams lp = mImWorkingDialog.getWindow()
				.getAttributes();
		lp.dimAmount = 0.60f;
		// these doesn't work
		lp.width = LayoutParams.FILL_PARENT;
		lp.height = LayoutParams.WRAP_CONTENT;
		mImWorkingDialog.getWindow().addFlags(
				WindowManager.LayoutParams.FLAG_WATCH_OUTSIDE_TOUCH);
		mImWorkingDialog.getWindow().addFlags(
				WindowManager.LayoutParams.FLAG_DIM_BEHIND);
		mImWorkingDialog.getWindow().setAttributes(lp);

		mImWorkingView = mLayoutInflater.inflate(
				R.layout.freeroom_layout_dialog_working, null);

		// these work perfectly
		// mImWorkingView.setMinimumWidth((int) (activityWidth * 0.9f));
		// mImWorkingView.setMinimumHeight((int) (activityHeight * 0.8f));

		mImWorkingDialog.setView(mImWorkingView);

		mImWorkingDialog.setOnShowListener(new OnShowListener() {
			@Override
			public void onShow(DialogInterface dialog) {
				workingDisclaimer.setText(R.string.freeroom_whoIsWorking_wait);
				// Tracker
				Tracker.getInstance().trackPageView("freeroom/workingthere");
			}
		});

		ListView lv = (ListView) mImWorkingView
				.findViewById(R.id.freeroom_layout_dialog_working_time_list);

		ViewGroup header = (ViewGroup) mLayoutInflater.inflate(
				R.layout.freeroom_layout_dialog_working_header, lv, false);
		lv.addHeaderView(header, null, false);
		ViewGroup footer = (ViewGroup) mLayoutInflater.inflate(
				R.layout.freeroom_layout_dialog_working_footer, lv, false);
		lv.addFooterView(footer, null, false);

		workingTimeSummary = (TextView) mImWorkingView
				.findViewById(R.id.freeroom_layout_dialog_working_time);
		workingDisclaimer = (TextView) mImWorkingView
				.findViewById(R.id.freeroom_layout_dialog_working_disclaimer);

		workingMessageList = mModel.getListMessageFrequency();
		workingMessageAdapter = new MessageFrequencyArrayAdapter<MessageFrequency>(
				this, getApplicationContext(),
				R.layout.freeroom_layout_message,
				R.id.freeroom_layout_message_text, workingMessageList);
		lv.setAdapter(workingMessageAdapter);

		mImWorkingDialog.setOnDismissListener(new OnDismissListener() {

			@Override
			public void onDismiss(DialogInterface dialog) {
				workingMessageList.clear();
				workingMessageAdapter.notifyDataSetInvalidated();
			}
		});
	}

	/**
	 * Display the working dialog with the given room and period.
	 * 
	 * @param room
	 *            room displayed
	 * @param period
	 *            period displayed
	 */
	public void displayWorkingDialog(FRRoom room, FRPeriod period) {
		mImWorkingDialog.show();
		mImWorkingDialog.setTitle(FRUtilsClient.formatRoom(room));
		workingTimeSummary.setText(times.formatFullDateFullTimePeriod(period));
	}

	@Override
	public void workingMessageUpdated() {
		workingDisclaimer
				.setText(getString(R.string.freeroom_whoIsWorking_disclaimer));
		workingMessageAdapter.notifyDataSetChanged();
	}

	/**
	 * Stores (non-permanent!) if it's the first time the user want to dismiss
	 * the welcome dialog without being registered.
	 */
	private boolean firstTimeWelcomeWithOutRegistered = true;

	/**
	 * Inits the welcome dialog
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
					if (firstTimeWelcomeWithOutRegistered) {
						firstTimeWelcomeWithOutRegistered = false;
						mWelcomeDialog.show();
						showErrorDialog(getString(R.string.freeroom_welcome_error));
					} else {
						finish();
					}
				}
			}
		});

		mWelcomeDialog.setOnShowListener(new OnShowListener() {
			@Override
			public void onShow(DialogInterface dialog) {
				// Tracker
				Tracker.getInstance().trackPageView("freeroom/welcome");
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
				if (email.equals(fct_prefix + "noregister")) {
					registerUserBeta
							.setText(getString(R.string.freeroom_welcome_submitting));
					registerUserBeta.setEnabled(false);
					dismissSoftKeyBoard(arg0);
					mModel.setRegisteredUser(true);
					validateRegistration();
				} else if (validEmail(email)) {
					RegisterUser req = new RegisterUser(email, getConfig(false));
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
	public void errorRegister(boolean transmissionError) {

		// TODO beta-test only.
		Button registerUserBeta = (Button) mWelcomeView
				.findViewById(R.id.freeroom_layout_dialog_welcome_register);
		registerUserBeta.setEnabled(true);
		registerUserBeta.setText(getString(R.string.freeroom_welcome_register));
		String string = getString(R.string.freeroom_welcome_validate_network_error);
		if (!transmissionError) {
			string = getString(R.string.freeroom_welcome_validate_reject);
			showErrorDialog(string);
		}
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
				updateFavoritesSummary();
				// Tracker
				Tracker.getInstance().trackPageView("freeroom/favorites");
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
				mAddFavoritesDialog.show();
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
				.getFavorites().keySetOrdered(), mModel.getFavorites(), mModel,
				this);
		lv.setAdapter(mFavoritesAdapter);
		mFavoritesAdapter.notifyDataSetChanged();
	}

	/**
	 * Updates the favorites summary after something has changed.
	 * <p>
	 * Display the number of favorites, or a small message if no favorites.
	 */
	public void updateFavoritesSummary() {
		TextView favoritesSummaryTextView = (TextView) mFavoritesView
				.findViewById(R.id.freeroom_layout_dialog_fav_status);
		int count = mFavoritesAdapter.getGroupCount();
		String text = "";
		if (count == 0) {
			text = getString(R.string.freeroom_dialog_fav_status_no);
		} else {
			text = getString(R.string.freeroom_dialog_fav_status_fav);
			int total = 0;
			for (int i = 0; i < count; i++) {
				total += mFavoritesAdapter.getChildrenCount(i);
			}
			text += getResources().getQuantityString(
					R.plurals.freeroom_results_room_header, total, total);
		}
		favoritesSummaryTextView.setText(text);
	}

	private void initAddFavoritesDialog() {
		// Instantiate an AlertDialog.Builder with its constructor
		AlertDialog.Builder builder = new AlertDialog.Builder(this);
		builder.setTitle(getString(R.string.freeroom_dialog_add_favorites_title));
		builder.setIcon(R.drawable.ic_action_new);

		// Get the AlertDialog from create()
		mAddFavoritesDialog = builder.create();

		// redefine paramaters to dim screen when displayed
		WindowManager.LayoutParams lp = mAddFavoritesDialog.getWindow()
				.getAttributes();
		lp.dimAmount = 0.60f;
		// these doesn't work
		lp.width = LayoutParams.FILL_PARENT;
		lp.height = LayoutParams.WRAP_CONTENT;
		mAddFavoritesDialog.getWindow().addFlags(
				WindowManager.LayoutParams.FLAG_WATCH_OUTSIDE_TOUCH);
		mAddFavoritesDialog.getWindow().addFlags(
				WindowManager.LayoutParams.FLAG_DIM_BEHIND);
		mAddFavoritesDialog.getWindow().setAttributes(lp);

		mAddFavoritesView = mLayoutInflater.inflate(
				R.layout.freeroom_layout_dialog_add_favorites_room, null);
		// these work perfectly
		mAddFavoritesView.setMinimumWidth((int) (activityWidth * 0.9f));

		mAddFavoritesDialog.setView(mAddFavoritesView);

		tvAutcompletStatusFav = (TextView) mAddFavoritesView
				.findViewById(R.id.freeroom_layout_dialog_add_room_status);

		mAddFavoritesDialog.setOnShowListener(new OnShowListener() {
			@Override
			public void onShow(DialogInterface dialog) {
				// Tracker
				Tracker.getInstance().trackPageView("freeroom/favorites/add");
			}
		});

		mAddFavoritesDialog.setOnDismissListener(new OnDismissListener() {

			@Override
			public void onDismiss(DialogInterface dialog) {
				mFavoritesAdapter.notifyDataSetChanged();

				// WARNING: BUG FIX
				// when the favorites are modified (removed) and the parent
				// group is opened, this cause a NullPointerException on the
				// main favorites window.
				ExpandableListView lv = (ExpandableListView) mFavoritesView
						.findViewById(R.id.freeroom_layout_dialog_fav_list);
				for (int i = mFavoritesAdapter.getGroupCount() - 1; i >= 0; i--) {
					lv.collapseGroup(i);
				}
				updateFavoritesSummary();
				autoCompleteCancel();
				mAutoCompleteAddRoomInputBarElement.setInputText("");
				mAutoCompleteAddFavoritesInputBarElement.setInputText("");
			}
		});

		mAutoCompleteAddFavoritesArrayListFRRoom = new ArrayList<FRRoom>(50);

		UIConstructAddFavoritesInputBar();
		LinearLayout ll = (LinearLayout) mAddFavoritesView
				.findViewById(R.id.freeroom_layout_dialog_add_favorites_layout_main);
		ll.addView(mAutoCompleteAddFavoritesInputBarElement);
		createAddFavoritesSuggestionsList();
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

		mAddRoomDialog.setView(mAddRoomView);

		tvAutcompletStatusRoom = (TextView) mAddRoomView
				.findViewById(R.id.freeroom_layout_dialog_add_room_status);

		mAddRoomDialog.setOnShowListener(new OnShowListener() {
			@Override
			public void onShow(DialogInterface dialog) {
				// Tracker
				Tracker.getInstance().trackPageView("freeroom/search/add");
			}
		});

		mAddRoomDialog.setOnDismissListener(new OnDismissListener() {
			@Override
			public void onDismiss(DialogInterface dialog) {
				autoCompleteCancel();
				mSummarySelectedRoomsTextViewSearchMenu.setText(u
						.getSummaryTextFromCollection(selectedRooms));
				searchButton.setEnabled(auditSubmit() == 0);
				mAutoCompleteAddRoomInputBarElement.setInputText("");
				mAutoCompleteAddFavoritesInputBarElement.setInputText("");

				dismissSoftKeyBoard(mAddRoomView);
				mEditRoomDialog.dismiss();
				mAddRoomDialog.dismiss();
			}
		});

		Button bt_done = (Button) mAddRoomView
				.findViewById(R.id.freeroom_layout_dialog_add_room_done);
		bt_done.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				dismissSoftKeyBoard(v);
				mEditRoomDialog.dismiss();
				mAddRoomDialog.dismiss();
			}
		});

		Button bt_edit = (Button) mAddRoomView
				.findViewById(R.id.freeroom_layout_dialog_add_room_edit);
		bt_edit.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				dismissSoftKeyBoard(v);
				// we dont dismiss, we hide, such that the text is kept
				// if it's dismissed by other method, the text will be reset
				mAddRoomDialog.hide();
				mEditRoomDialog.show();
			}
		});

		UIConstructAddRoomInputBar();
		LinearLayout ll = (LinearLayout) mAddRoomView
				.findViewById(R.id.freeroom_layout_dialog_add_layout_main);
		ll.addView(mAutoCompleteAddRoomInputBarElement);
		createAddRoomSuggestionsList();
	}

	private ListView selectedListView;

	private void initEditRoomDialog() {
		// Instantiate an AlertDialog.Builder with its constructor
		AlertDialog.Builder builder = new AlertDialog.Builder(this);
		builder.setTitle(getString(R.string.freeroom_dialog_edit_room_title));
		builder.setIcon(R.drawable.ic_action_edit_light);

		// Get the AlertDialog from create()
		mEditRoomDialog = builder.create();

		// redefine paramaters to dim screen when displayed
		WindowManager.LayoutParams lp = mEditRoomDialog.getWindow()
				.getAttributes();
		lp.dimAmount = 0.60f;
		// these doesn't work
		lp.width = LayoutParams.FILL_PARENT;
		lp.height = LayoutParams.WRAP_CONTENT;
		mEditRoomDialog.getWindow().addFlags(
				WindowManager.LayoutParams.FLAG_WATCH_OUTSIDE_TOUCH);
		mEditRoomDialog.getWindow().addFlags(
				WindowManager.LayoutParams.FLAG_DIM_BEHIND);
		mEditRoomDialog.getWindow().setAttributes(lp);

		mEditRoomView = mLayoutInflater.inflate(
				R.layout.freeroom_layout_dialog_edit_room, null);
		// these work perfectly
		mEditRoomView.setMinimumWidth((int) (activityWidth * 0.9f));
		mEditRoomView.setMinimumHeight((int) (activityHeight * 0.8f));

		mEditRoomDialog.setView(mEditRoomView);

		selectedListView = (ListView) mEditRoomView
				.findViewById(R.id.freeroom_layout_dialog_edit_room_list);
		selectedRoomArrayAdapter = new FRRoomRemoveArrayAdapter<FRRoom>(this,
				getApplicationContext(), R.layout.freeroom_layout_room_edit,
				R.id.freeroom_layout_selected_text, selectedRooms);
		selectedListView.setAdapter(selectedRoomArrayAdapter);
		selectedListView.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
					long arg3) {
				selectedRoomArrayAdapter.remove(selectedRoomArrayAdapter
						.getItem(arg2));
			}
		});
		selectedListView.refreshDrawableState();

		mEditRoomDialog.setOnDismissListener(new OnDismissListener() {
			@Override
			public void onDismiss(DialogInterface arg0) {
				dismissSoftKeyBoard(mEditRoomView);
				mEditRoomDialog.dismiss();
				mAddRoomDialog.dismiss();
			}
		});

		mEditRoomDialog.setOnShowListener(new OnShowListener() {
			@Override
			public void onShow(DialogInterface dialog) {
				selectedRoomArrayAdapter.notifyDataSetChanged();
				// Tracker
				Tracker.getInstance().trackPageView("freeroom/search/edit");
			}
		});

		Button bt_done = (Button) mEditRoomView
				.findViewById(R.id.freeroom_layout_dialog_edit_room_done);
		bt_done.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				dismissSoftKeyBoard(v);
				mEditRoomDialog.dismiss();
				mAddRoomDialog.dismiss();
			}
		});

		Button bt_more = (Button) mEditRoomView
				.findViewById(R.id.freeroom_layout_dialog_edit_room_add);
		bt_more.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// we DONT dismiss, only hide! (no trigger or dismisslistener)
				mEditRoomDialog.hide();
				mAddRoomDialog.show();
			}
		});
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
				// Tracker
				Tracker.getInstance().trackPageView("freeroom/share");
			}
		});

		mShareDialog.setOnDismissListener(new OnDismissListener() {

			@Override
			public void onDismiss(DialogInterface dialog) {
				// we dont remove the text, as the user may want to share again
				// the same text!
			}
		});

		mShareDialogTextViewSummarySharing = (TextView) mShareView
				.findViewById(R.id.freeroom_layout_dialog_share_textBasic);

		mShareDialogEditTextMessageWorking = (EditText) mShareView
				.findViewById(R.id.freeroom_layout_dialog_share_text_edit);

		mShareDialog.setView(mShareView);

		mShareDialog.show();
		mShareDialog.hide();
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
		// layout : this is NOT displayed for now!
		// other's activity are only displayed in the given popup, not there.
		ArrayList<String> suggest = new ArrayList<String>();
		// 1st result is the "title"
		suggest.add(getString(R.string.freeroom_share_others_activity));

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

		mSearchDialog.setOnShowListener(new OnShowListener() {
			@Override
			public void onShow(DialogInterface dialog) {
				searchButton.setEnabled(auditSubmit() == 0);
				initPreviousTitle();
				reset();
				// Tracker
				Tracker.getInstance().trackPageView("freeroom/search");
			}
		});

		// this is necessary o/w buttons don't exists!
		mSearchDialog.hide();
		mSearchDialog.show();
		mSearchDialog.dismiss();
		resetButton = mSearchDialog.getButton(DialogInterface.BUTTON_NEUTRAL);
		searchButton = mSearchDialog.getButton(DialogInterface.BUTTON_POSITIVE);

		// display the previous searches
		mSearchPreviousListView = (ListView) mSearchView
				.findViewById(R.id.freeroom_layout_dialog_search_prev_search_list);
		mPrevRequestAdapter = new PreviousRequestArrayAdapter<FRRequestDetails>(
				this, this, R.layout.freeroom_layout_list_prev_req,
				R.id.freeroom_layout_prev_req_text, mModel.getPreviousRequest());

		ViewGroup header = (ViewGroup) mLayoutInflater.inflate(
				R.layout.freeroom_layout_search_header,
				mSearchPreviousListView, false);
		mSearchPreviousListView.addHeaderView(header, null, false);
		ViewGroup footer = (ViewGroup) mLayoutInflater.inflate(
				R.layout.freeroom_layout_search_footer,
				mSearchPreviousListView, false);
		mSearchPreviousListView.addFooterView(footer, null, false);
		mSearchPreviousListView.setAdapter(mPrevRequestAdapter);

		textTitlePrevious = getString(R.string.freeroom_search_previous_search);
		prevSearchTitle = (TextView) mSearchView
				.findViewById(R.id.freeroom_layout_dialog_search_prev_search_title);
		// go home: useful for very long lists to go back up.
		goUpHomeButton = (Button) mSearchView
				.findViewById(R.id.freeroom_layout_dialog_search_footer_home);
		goUpHomeButton.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				mSearchPreviousListView.smoothScrollToPosition(0);
			}
		});

		mSummarySelectedRoomsTextViewSearchMenu = (TextView) mSearchDialog
				.findViewById(R.id.freeroom_layout_dialog_search_text_summary);
		// the view will be removed or the text changed, no worry
		mSummarySelectedRoomsTextViewSearchMenu
				.setText(getString(R.string.freeroom_add_rooms_empty));

		initSearch();
	}

	/**
	 * Inits the title for previous request, with empty value if none, with
	 * "show" if not displayed, or "prev request" otherwise.
	 */
	private void initPreviousTitle() {
		if (mModel.getPreviousRequest().isEmpty()) {
			prevSearchTitle.setText("");
			prevSearchTitle.setVisibility(View.GONE);
			goUpHomeButton.setVisibility(View.GONE);
		} else {
			prevSearchTitle.setText(textTitlePrevious);
			prevSearchTitle.setVisibility(View.VISIBLE);
			goUpHomeButton.setVisibility(View.VISIBLE);
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
			// request we will be stored
			// it will actually come at the first place and be deleted from it's
			// original place
			prepareSearchQuery(true);
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
					build.append(u.getSummaryTextFromCollection(
							req.getUidNonFav(), "", max));
				}
			}
		}
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
			mSearchDialog.show();
		}
	}

	/**
	 * When a Selected Room item is clicked on "remove".
	 * 
	 * @param position
	 *            position of the item to remove
	 */
	public void onRemoveRoomClickListener(int position) {
		// from time to time cause an issue, this gets black hole in the list.
		// fixed by setting the whole line clickable to remove the line
		selectedRoomArrayAdapter.remove(selectedRoomArrayAdapter
				.getItem(position));
		selectedListView.refreshDrawableState();
	}

	/**
	 * Fill the search menus with the selected previous request.
	 * 
	 * @param position
	 *            position of the item to use to refill.
	 */
	public boolean onFillRequestClickListeners(int position) {
		mSearchPreviousListView.smoothScrollToPosition(0);
		FRRequestDetails req = mModel.getPreviousRequest().get(position);
		if (req != null) {
			reset();
			fillSearchDialog(req);
			return true;
		}
		return false;
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

		mWarningDialog.setOnDismissListener(new OnDismissListener() {

			@Override
			public void onDismiss(DialogInterface dialog) {
				updateFavoritesSummary();
				mFavoritesAdapter.notifyDataSetChanged();
				// Tracker
				Tracker.getInstance().trackPageView("freeroom/warning");
			}
		});
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

		mErrorDialog.setOnShowListener(new OnShowListener() {
			@Override
			public void onShow(DialogInterface dialog) {
				// Tracker
				Tracker.getInstance().trackPageView("freeroom/error");
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
		builder.setTitle(getString(R.string.freeroom_settings_title));
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

		mParamDialog.setOnShowListener(new OnShowListener() {
			@Override
			public void onShow(DialogInterface dialog) {
				// Tracker
				Tracker.getInstance().trackPageView("freeroom/settings");
			}
		});
	}

	private void initParamDialogData() {
		mParamDialogRefreshTimeFormatExample();
		mParamDialogRefreshColorBlindExamples();

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
		// this is implemented, this was thought as useless.
		// case LASTREQUEST:
		// id = R.id.freeroom_layout_param_home_last;
		// break;
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

		ColorBlindMode colorBlindMode = mModel.getColorBlindMode();
		switch (colorBlindMode) {
		case DEFAULT:
			id = R.id.freeroom_layout_param_color_default;
			break;
		case DOTS_DISCOLORED:
			id = R.id.freeroom_layout_param_color_default;
			break;
		case DOTS_SYMBOL:
			id = R.id.freeroom_layout_param_color_symbolic;
			break;
		case DOTS_SYMBOL_LINEFULL:
			id = R.id.freeroom_layout_param_color_symbolic_lines;
			break;
		case DOTS_SYMBOL_LINEFULL_DISCOLORED:
			id = R.id.freeroom_layout_param_color_symbolic_lines_disc;
			break;
		default:
			id = R.id.freeroom_layout_param_color_default;
			break;
		}
		rd = (RadioButton) mParamView.findViewById(id);
		rd.setChecked(true);

		boolean advanced = false;
		switch (colorBlindMode) {
		case DEFAULT:
		case DOTS_DISCOLORED:
			advanced = false;
			break;
		case DOTS_SYMBOL:
		case DOTS_SYMBOL_LINEFULL:
		case DOTS_SYMBOL_LINEFULL_DISCOLORED:
			advanced = true;
			break;
		default:
			advanced = false;
			break;
		}

		CheckBox advancedCheckBox = (CheckBox) mParamView
				.findViewById(R.id.freeroom_layout_dialog_param_colorblind_advanced);
		advancedCheckBox.setChecked(advanced);
		onColorBlindAdvancedChecked(advancedCheckBox);

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

	/**
	 * This checks the konami code moves.
	 */
	public boolean onTouchEvent(MotionEvent event) {
		checkKonamiCode(event);
		return true;
	}

	/**
	 * Overrides the legacy <code>onKeyDown</code> method in order to override
	 * some hardware button implementation.
	 * 
	 * @param keyCode
	 *            keycode as specified by overridden method
	 * @param event
	 *            event as specified by overridden method
	 * @return boolean value as specified by overridden method
	 */
	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		// overrides search button for devices who are equipped with such
		// hardware button, and launch automatically the search popup.
		if (keyCode == KeyEvent.KEYCODE_SEARCH) {
			mSearchDialog.show();
			return true;
		}

		// overrides clear button for devices who are equipped with such
		// hardware button, and clear all the search introduced (reset)
		if (keyCode == KeyEvent.KEYCODE_CLEAR) {
			reset();
			return true;
		}

		// overrides enter button for devices who are equipped with such
		// hardware button, and launch a search if valid.
		if (keyCode == KeyEvent.KEYCODE_ENTER) {
			if (mSearchDialog.isShowing() && !mAddRoomDialog.isShowing()
					&& !mEditRoomDialog.isShowing() && (auditSubmit() == 0)) {
				prepareSearchQuery(true);
				return true;
			}
		}

		// overrides envelope button for devices who are equipped with such
		// hardware button, and share the location if the detailled info popup
		// is displayed and available for the whole period.
		if (keyCode == KeyEvent.KEYCODE_ENVELOPE) {
			if (mInfoRoomDialog.isShowing()) {
				Occupancy mOccupancy = mModel.getDisplayedOccupancy();
				if (mOccupancy != null && mOccupancy.isIsAtLeastFreeOnce()
						&& !mOccupancy.isIsAtLeastOccupiedOnce()) {
					Button shareButton = mInfoRoomDialog
							.getButton(AlertDialog.BUTTON_POSITIVE);
					if (shareButton != null && shareButton.isEnabled()) {
						shareButton.performClick();
						return true;
					}
				}
			}
		}

		// this overrides the menu button, which is present on most phone BUT
		// not all (deprecated). We should never think this is always available.
		if (keyCode == KeyEvent.KEYCODE_MENU) {
			// this does nothging special and let the legacy menu to be shown.
			// it's implemented now to display the same as the overflow button,
			// but in different UI and shapes.
		}

		// Overrides the back button, which is present on all devices.
		if (keyCode == KeyEvent.KEYCODE_BACK) {
			// this is not used so far, as all the popup are automatically
			// closed by the back button.
		}

		// all other keys are handled automatically or not handled.
		return super.onKeyDown(keyCode, event);
	}

	@Override
	public void anyError() {
		setTitle();
		String errorMessage = getString(R.string.freeroom_home_error_sorry);
		setTextSummary(errorMessage);
		autoCompleteUpdateMessage(errorMessage);
		workingDisclaimer.setText(errorMessage);
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
		super.setTitle(getString(R.string.freeroom_title_main_title));
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
				return new FRRequestDetails(period, onlyFree, array, false,
						true, false, null, mModel.getGroupAccess());
			} else {
				u.logV("no favorites in model: going for any free room");
				room = HomeBehaviourRoom.ANYFREEROOM;
			}
		}

		if (room.equals(HomeBehaviourRoom.LASTREQUEST)) {
			// this feature has been disabled => going from default
			u.logD("last request is not operational now");
			room = HomeBehaviourRoom.ANYFREEROOM;
		}

		if (!room.equals(HomeBehaviourRoom.ANYFREEROOM)) {
			u.logE("unknown room behavior: ");
			u.logE(room.name());
			u.logE("going for any free room");
		}
		// any free room behavior
		return new FRRequestDetails(period, true, new ArrayList<String>(1),
				true, false, false, null, mModel.getGroupAccess());
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
			// popup with no results message
			subtitle = getString(R.string.freeroom_home_error_no_results);
			showErrorDialog(subtitle);
		} else {
			FRRequest request = mModel.getFRRequestDetails();

			String title = "";
			if (request.isOnlyFreeRooms()) {
				title = getString(R.string.freeroom_home_info_free_rooms);
			} else {
				title = getString(R.string.freeroom_home_info_rooms);
			}
			FRPeriod period = mModel.getOverAllTreatedPeriod();
			setTitle(title + times.formatTimeSummaryTitle(period));
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
	 * Expands all the groups if there are no more than 3 groups AND not more
	 * than 7 results.
	 * <p>
	 * These constants are defined ONLY there <br>
	 * 
	 * @param ev
	 *            expandable list view
	 * @param ad
	 *            expandable list view adapter
	 */
	public void updateCollapse(ExpandableListView ev,
			ExpandableListViewAdapter<Occupancy> ad) {
		int maxChildrenToExpand = 7;
		int maxGroupToExpand = 3;
		if (ad.getGroupCount() <= maxGroupToExpand
				&& ad.getChildrenTotalCount() <= maxChildrenToExpand) {
			// we expand in reverse order for performance reason!
			// expand all may caus
			for (int i = ad.getGroupCount() - 1; i >= 0; i--) {
				ev.expandGroup(i);
			}
		}
		ad.setGroupFocus(-1);
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
			mInfoRoomDialog.setTitle(FRUtilsClient.formatRoom(mRoom));

			TextView periodTextView = (TextView) mInfoRoomView
					.findViewById(R.id.freeroom_layout_dialog_info_period);
			if (mOccupancy.isSetTreatedPeriod()
					&& mOccupancy.getTreatedPeriod() != null) {
				periodTextView.setText(times
						.formatFullDateFullTimePeriod(mOccupancy
								.getTreatedPeriod()));
			} else {
				// error coming from server ??
				periodTextView.setText("period");
				System.err
						.println("Unknown or undefined period coming from server");
			}

			// people image to replay worst case occupancy and direct share with
			// server
			ImageView peopleImageView = (ImageView) mInfoRoomView
					.findViewById(R.id.freeroom_layout_dialog_info_people);
			peopleImageView.setImageResource(mModel
					.getImageFromRatioOccupation(mOccupancy
							.getRatioWorstCaseProbableOccupancy()));
			peopleImageView.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View arg0) {
					directShareWithServer(mOccupancy.getTreatedPeriod(), mRoom);
				}
			});

			ImageView map = (ImageView) mInfoRoomView
					.findViewById(R.id.freeroom_layout_dialog_info_map);
			map.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {
					try {
						// TODO: when map plugin accepts UID and not only room
						// name, change by uid instead of doorcode!
						Uri mUri = Uri
								.parse("pocketcampus://map.plugin.pocketcampus.org/search");
						Uri.Builder mbuild = mUri.buildUpon()
								.appendQueryParameter("q", mRoom.getDoorCode());
						Intent i = new Intent(Intent.ACTION_VIEW, mbuild
								.build());
						i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
						startActivity(i);
					} catch (Exception e) {
						e.printStackTrace();
						System.err.println("Map plugin not installed ?!?");
						showErrorDialog(getString(R.string.freeroom_error_map_plugin_missing));
					}
				}
			});

			ImageView shareImageView = (ImageView) mInfoRoomView
					.findViewById(R.id.freeroom_layout_dialog_info_share);
			setShareClickListener(shareImageView, this, mOccupancy);

			Button shareButton = mInfoRoomDialog
					.getButton(AlertDialog.BUTTON_POSITIVE);
			shareButton.setEnabled(mOccupancy.isIsAtLeastFreeOnce()
					&& !mOccupancy.isIsAtLeastOccupiedOnce());
			setShareClickListener(shareButton, this, mOccupancy);

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

	/**
	 * DONT CALL IT! Call {@link #reset()} instead.
	 * 
	 * @param request
	 */
	private void fillSearchDialog(final FRRequestDetails request) {
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
		mOptionalLineLinearLayoutWrapperFirst.setVisibility(View.GONE);
		if (enabled) {
			mOptionalLineLinearLayoutWrapperFirst.setVisibility(View.VISIBLE);
		}
		mOptionalLineLinearLayoutWrapperSecond.setVisibility(View.GONE);
		if (request.isUser()) {
			mOptionalLineLinearLayoutWrapperSecond.setVisibility(View.VISIBLE);
			selectedRooms.addAll(request.getUidNonFav());
			mSummarySelectedRoomsTextViewSearchMenu.setText(u
					.getSummaryTextFromCollection(selectedRooms));
		}
		updateDateTimePickersAndButtons();

		// MUST be the last action: after all field are set, check if the
		// request is valid
		searchButton.setEnabled(auditSubmit() == 0);
	}

	/**
	 * Sends directly a sharing with the server, without going thru the dialog
	 * and/or asking for a message or a confirmation.
	 * 
	 * @param mPeriod
	 *            the room (location) to share
	 * @param mRoom
	 *            the period (of time) to share
	 */
	public void directShareWithServer(FRPeriod mPeriod, FRRoom mRoom) {
		share(mPeriod, mRoom, false, "");
	}

	private void share(FRPeriod mPeriod, FRRoom mRoom, boolean withFriends,
			String toShare) {
		WorkingOccupancy work = new WorkingOccupancy(mPeriod, mRoom);
		CheckBox mShareDialogCheckBoxShareMessageServer = (CheckBox) mShareDialog
				.findViewById(R.id.freeroom_layout_dialog_share_checkbox_server);
		if (mShareDialogCheckBoxShareMessageServer != null
				&& mShareDialogCheckBoxShareMessageServer.isChecked()
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
		u.logV("sharing:" + sharing);
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

	private ListView mAutoCompleteAddRoomListView;
	private List<FRRoom> mAutoCompleteAddRoomArrayListFRRoom;

	/** The input bar to make the search */
	private InputBarElement mAutoCompleteAddRoomInputBarElement;
	/** Adapter for the <code>mListView</code> */
	private FRRoomSuggestionArrayAdapter<FRRoom> mAddRoomAdapter;

	/**
	 * FAVORITES
	 */

	private ListView mAutoCompleteAddFavoritesListView;
	private List<FRRoom> mAutoCompleteAddFavoritesArrayListFRRoom;

	/** The input bar to make the search */
	private InputBarElement mAutoCompleteAddFavoritesInputBarElement;
	/** Adapter for the <code>mListView</code> */
	private FRRoomSuggestionArrayAdapter<FRRoom> mAddFavoritesAdapter;

	private DatePickerDialog mDatePickerDialog;
	private TimePickerDialog mTimePickerStartDialog;
	private TimePickerDialog mTimePickerEndDialog;

	private Button showDatePicker;
	private Button showStartTimePicker;
	private Button showEndTimePicker;
	private Button showStartTimePickerShort;
	private Button showEndTimePickerShort;

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
	private ImageButton downToStartHourButton;
	private ImageButton downStartHourButton;
	private ImageButton upStartHourButton;
	private ImageButton downEndHourButton;
	private ImageButton upEndHourButton;
	private ImageButton upToEndHourButton;

	/**
	 * Stores if the "up to end" button has been trigged.
	 * <p>
	 * If yes, the endHour don't follow anymore the startHour when you change
	 * it. It will be disabled when you change manually the endHour to a value
	 * under the maximal hour.
	 */
	private boolean upToEndSelected = false;

	private TextView mSummarySelectedRoomsTextViewSearchMenu;

	private int yearSelected = -1;
	private int monthSelected = -1;
	private int dayOfMonthSelected = -1;
	private int startHourSelected = -1;
	private int startMinSelected = -1;
	private int endHourSelected = -1;
	private int endMinSelected = -1;

	private LinearLayout mOptionalLineLinearLayoutWrapperFirst;
	private LinearLayout mOptionalLineLinearLayoutWrapperSecond;

	private void initSearch() {

		mOptionalLineLinearLayoutWrapperFirst = (LinearLayout) mSearchDialog
				.findViewById(R.id.freeroom_layout_dialog_search_opt_line_wrapper_1st);
		mOptionalLineLinearLayoutWrapperSecond = (LinearLayout) mSearchView
				.findViewById(R.id.freeroom_layout_dialog_search_opt_line_wrapper_2nd);

		selectedRooms = new SetArrayList<FRRoom>();

		// createSuggestionsList();
		// addAllFavsToAutoComplete();
		mAutoCompleteAddRoomArrayListFRRoom = new ArrayList<FRRoom>(10);
		resetTimes();

		UIConstructPickers();

		UIConstructButton();

		// UIConstructInputBar();

		reset();
	}

	private void UIConstructPickers() {
		// First allow the user to select a date, but don't display the date
		// button if the user don't have the right to use it.
		showDatePicker = (Button) mSearchView
				.findViewById(R.id.freeroom_layout_dialog_search_date);
		if (!mModel.getAdvancedTime()) {
			showDatePicker.setVisibility(View.GONE);
		}

		mDatePickerDialog = new DatePickerDialog(this,
				new DatePickerDialog.OnDateSetListener() {

					@Override
					public void onDateSet(DatePicker view, int nYear,
							int nMonthOfYear, int nDayOfMonth) {
						yearSelected = nYear;
						monthSelected = nMonthOfYear;
						dayOfMonthSelected = nDayOfMonth;
						updateDateTimePickersAndButtons();
					}
				}, yearSelected, monthSelected, dayOfMonthSelected);

		// the click listener is always there, even if the button is not
		// visible.
		showDatePicker.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				mDatePickerDialog.show();
			}
		});

		// Then the starting time of the period
		showStartTimePicker = (Button) mSearchView
				.findViewById(R.id.freeroom_layout_dialog_search_hour_start);
		showStartTimePickerShort = (Button) mSearchView
				.findViewById(R.id.freeroom_layout_dialog_search_hour_start_short);
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
						updateDateTimePickersAndButtons();

					}
				}, startHourSelected, startMinSelected, true);

		OnClickListener ocl_start = new OnClickListener() {

			@Override
			public void onClick(View v) {
				mTimePickerStartDialog.show();
			}
		};
		showStartTimePicker.setOnClickListener(ocl_start);
		showStartTimePickerShort.setOnClickListener(ocl_start);

		// Then the ending time of the period
		showEndTimePicker = (Button) mSearchView
				.findViewById(R.id.freeroom_layout_dialog_search_hour_end);
		showEndTimePickerShort = (Button) mSearchView
				.findViewById(R.id.freeroom_layout_dialog_search_hour_end_short);
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
								&& (endMinSelected - startMinSelected) <= FRTimes.MIN_MINUTE_INTERVAL) {
							endMinSelected = startMinSelected
									+ FRTimes.MIN_MINUTE_INTERVAL;
							if (endMinSelected >= 60) {
								endMinSelected = 0;
								endHourSelected += 1;
								startMinSelected = 60 - FRTimes.MIN_MINUTE_INTERVAL;
							}
						}

						if (endHourSelected >= FRTimes.LAST_HOUR_CHECK) {
							endHourSelected = FRTimes.LAST_HOUR_CHECK;
							endMinSelected = 0;
						}
						if (endHourSelected != FRTimes.LAST_HOUR_CHECK) {
							upToEndSelected = false;
							upToEndHourButton.setEnabled(!upToEndSelected);
						}
						updateDateTimePickersAndButtons();
					}
				}, endHourSelected, endMinSelected, true);

		OnClickListener ocl_end = new OnClickListener() {

			@Override
			public void onClick(View v) {
				mTimePickerEndDialog.show();
			}
		};
		showEndTimePicker.setOnClickListener(ocl_end);
		showEndTimePickerShort.setOnClickListener(ocl_end);
	}

	/**
	 * 
	 * @param itsTheEnd
	 *            true if the request is already send, avoid to recheck the
	 *            validity.
	 */
	private void resetUserDefined(boolean itsTheEnd) {
		selectedRooms.clear();

		mSummarySelectedRoomsTextViewSearchMenu.setText(u
				.getSummaryTextFromCollection(selectedRooms));
		mAutoCompleteAddRoomInputBarElement.setInputText("");

		if (!itsTheEnd) {
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
					mOptionalLineLinearLayoutWrapperFirst
							.setVisibility(View.VISIBLE);

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
					mOptionalLineLinearLayoutWrapperSecond
							.setVisibility(View.VISIBLE);

					userDefButton.setChecked(true);
					mAddRoomDialog.show();
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
					mOptionalLineLinearLayoutWrapperFirst
							.setVisibility(View.GONE);
					mOptionalLineLinearLayoutWrapperSecond
							.setVisibility(View.GONE);
				}
				specButton.setChecked(false);
				resetUserDefined(false);

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
						resetUserDefined(false);
					}
					userDefButton.setChecked(true);

					anyButton.setChecked(false);
					specButton.setChecked(true);
					freeButton.setChecked(false);

					mOptionalLineLinearLayoutWrapperSecond
							.setVisibility(View.VISIBLE);

					mSummarySelectedRoomsTextViewSearchMenu.setText(u
							.getSummaryTextFromCollection(selectedRooms));
					mAddRoomDialog.show();
					searchButton.setEnabled(false);
				} else {
					resetUserDefined(false);
					mOptionalLineLinearLayoutWrapperSecond
							.setVisibility(View.GONE);
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
			}
		});

		downToStartHourButton = (ImageButton) mSearchView
				.findViewById(R.id.freeroom_layout_dialog_search_hour_start_tostart);
		downToStartHourButton.setEnabled(true);
		downToStartHourButton.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				startMinSelected = 0;
				int shift = startHourSelected - FRTimes.FIRST_HOUR_CHECK;
				startHourSelected = FRTimes.FIRST_HOUR_CHECK;
				if (!upToEndSelected && shift > 0) {
					endHourSelected -= shift;
				}
				updateDateTimePickersAndButtons();
			}
		});

		downStartHourButton = (ImageButton) mSearchView
				.findViewById(R.id.freeroom_layout_dialog_search_hour_start_minus);
		downStartHourButton.setEnabled(true);
		downStartHourButton.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				if (startHourSelected >= FRTimes.FIRST_HOUR_CHECK - 1) {
					startHourSelected -= 1;
					if (!upToEndSelected) {
						endHourSelected -= 1;
					}
				}
				updateDateTimePickersAndButtons();
			}
		});

		upStartHourButton = (ImageButton) mSearchView
				.findViewById(R.id.freeroom_layout_dialog_search_hour_start_plus);
		upStartHourButton.setEnabled(true);
		upStartHourButton.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				if (startHourSelected <= FRTimes.LAST_HOUR_CHECK - 2) {
					startHourSelected += 1;
					if (!upToEndSelected) {
						endHourSelected = Math.min(endHourSelected + 1,
								FRTimes.LAST_HOUR_CHECK);
					}
				}
				updateDateTimePickersAndButtons();
			}
		});

		downEndHourButton = (ImageButton) mSearchView
				.findViewById(R.id.freeroom_layout_dialog_search_hour_end_minus);
		downEndHourButton.setEnabled(true);
		downEndHourButton.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				if (endHourSelected >= FRTimes.FIRST_HOUR_CHECK + 2) {
					endHourSelected -= 1;
					if (startHourSelected >= endHourSelected) {
						startHourSelected -= 1;
					}
				}
				upToEndSelected = false;
				updateDateTimePickersAndButtons();
			}
		});

		upEndHourButton = (ImageButton) mSearchView
				.findViewById(R.id.freeroom_layout_dialog_search_hour_end_plus);
		upEndHourButton.setEnabled(true);
		upEndHourButton.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				if (endHourSelected <= FRTimes.LAST_HOUR_CHECK - 1) {
					endHourSelected += 1;
				}
				if (endHourSelected == FRTimes.LAST_HOUR_CHECK) {
					endMinSelected = 0;
				}
				upToEndSelected = false;
				updateDateTimePickersAndButtons();
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
				upToEndSelected = true;
				updateDateTimePickersAndButtons();
			}
		});

		userDefAddButton = (Button) mSearchView
				.findViewById(R.id.freeroom_layout_dialog_search_user_add);
		userDefAddButton.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				mAddRoomDialog.show();
			}
		});

		userDefEditButton = (Button) mSearchView
				.findViewById(R.id.freeroom_layout_dialog_search_user_edit);
		userDefEditButton.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				mEditRoomDialog.show();
			}
		});

		userDefResetButton = (Button) mSearchView
				.findViewById(R.id.freeroom_layout_dialog_search_user_reset);
		userDefResetButton.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				resetUserDefined(false);
			}
		});

		// for landscape device, mainly tablet, some layout are programmatically
		// changed to horizontal values, and weighted more logically.
		// XML IS ALWAYS DESIGNED FOR PHONES, as it's probably more than 97% of
		// users. tablets are changing their layout here.
		LinearLayout header_1st = (LinearLayout) mSearchView
				.findViewById(R.id.freeroom_layout_dialog_search_upper_first);
		if (isLandscapeTabletMode()) {
			LinearLayout header_main = (LinearLayout) mSearchView
					.findViewById(R.id.freeroom_layout_dialog_search_upper_main);
			header_main.setOrientation(LinearLayout.HORIZONTAL);

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

			// radio group and chexbox group made horizontal
			// all 5 buttons made fillparent verticallly.
			RadioGroup rg = (RadioGroup) mSearchView
					.findViewById(R.id.freeroom_layout_dialog_search_any_vs_spec);
			rg.setOrientation(RadioGroup.HORIZONTAL);
			RadioGroup.LayoutParams q = new RadioGroup.LayoutParams(
					LayoutParams.FILL_PARENT, LayoutParams.WRAP_CONTENT);
			rg.setLayoutParams(q);

			anyButton.setHeight(LayoutParams.FILL_PARENT);
			specButton.setHeight(LayoutParams.FILL_PARENT);

			LinearLayout mLinearLayout = (LinearLayout) mSearchDialog
					.findViewById(R.id.freeroom_layout_dialog_search_opt_line_wrapper_1st);
			mLinearLayout.setOrientation(LinearLayout.HORIZONTAL);

			freeButton.setHeight(LayoutParams.FILL_PARENT);
			favButton.setHeight(LayoutParams.FILL_PARENT);
			userDefButton.setHeight(LayoutParams.FILL_PARENT);

			// Layouts to change the horizontal weight
			// They have ONLY ONE CHILD: Children cannot have weight
			LinearLayout anyButtonLayout = (LinearLayout) mSearchDialog
					.findViewById(R.id.freeroom_layout_dialog_search_any_layout);
			LinearLayout specButtonLayout = (LinearLayout) mSearchDialog
					.findViewById(R.id.freeroom_layout_dialog_search_spec_layout);
			LinearLayout favButtonLayout = (LinearLayout) mSearchDialog
					.findViewById(R.id.freeroom_layout_dialog_search_fav_layout);
			LinearLayout userDefButtonLayout = (LinearLayout) mSearchDialog
					.findViewById(R.id.freeroom_layout_dialog_search_user_layout);
			LinearLayout freeButtonLayout = (LinearLayout) mSearchDialog
					.findViewById(R.id.freeroom_layout_dialog_search_non_free_layout);

			anyButtonLayout.setLayoutParams(p);
			specButtonLayout.setLayoutParams(p);
			favButtonLayout.setLayoutParams(p);
			userDefButtonLayout.setLayoutParams(p);
			freeButtonLayout.setLayoutParams(p);
		}

		if (mModel.getAdvancedTime()) {
			header_1st.setVisibility(View.VISIBLE);
		} else {
			header_1st.setVisibility(View.GONE);
		}
	}

	/**
	 * Minimal width in pixel to trigger landscape mode according to
	 * {@link #isLandscapeTabletMode()}.
	 */
	private int minWidthForLandscapeMode = 480;

	/**
	 * Check if the height is smaller than the width of the displayed screen.
	 * <p>
	 * As the plugin is NOT sensible to landscape mode, this will ONLY occur on
	 * tablets.
	 * <p>
	 * Please note some phones are also wider than higher, even if their are
	 * very small. To avoid the "landscape tablet mode" to be triggered, we
	 * check than the witdh is wider than the {@link #minWidthForLandscapeMode}
	 * constant in pixels, which is not really perfect, but not an issue as
	 * these phones are low-pixels densities and most recent tablets are medium
	 * or high-density.
	 * 
	 * @return true if landscape mode should be activated.
	 */
	private boolean isLandscapeTabletMode() {
		return (activityHeight < activityWidth)
				&& (activityWidth > minWidthForLandscapeMode);
	}

	private void UIConstructAddRoomInputBar() {
		final IFreeRoomView view = this;

		mAutoCompleteAddRoomInputBarElement = new InputBarElement(
				this,
				null,
				getString(R.string.freeroom_check_occupancy_search_inputbarhint));
		mAutoCompleteAddRoomInputBarElement
				.setImeOptions(EditorInfo.IME_ACTION_SEARCH);

		// click on magnify glass on the keyboard
		mAutoCompleteAddRoomInputBarElement
				.setOnEditorActionListener(new OnEditorActionListener() {
					@Override
					public boolean onEditorAction(TextView v, int actionId,
							KeyEvent event) {
						if (actionId == EditorInfo.IME_ACTION_SEARCH) {
							String query = mAutoCompleteAddRoomInputBarElement
									.getInputText();
							validAutoCompleteQuery(query, v);
						}

						return true;
					}
				});

		// click on BUTTON magnify glass on the inputbar
		mAutoCompleteAddRoomInputBarElement
				.setOnButtonClickListener(new OnClickListener() {
					@Override
					public void onClick(View v) {
						String query = mAutoCompleteAddRoomInputBarElement
								.getInputText();
						validAutoCompleteQuery(query, v);
					}
				});

		mAddRoomAdapter = new FRRoomSuggestionArrayAdapter<FRRoom>(
				getApplicationContext(),
				R.layout.freeroom_layout_list_room_add_room,
				R.id.freeroom_layout_list_room_add_room,
				mAutoCompleteAddRoomArrayListFRRoom, mModel, false);

		mAutoCompleteAddRoomInputBarElement
				.setOnKeyPressedListener(new OnKeyPressedListener() {
					@Override
					public void onKeyPressed(String text) {
						mAutoCompleteAddRoomListView
								.setAdapter(mAddRoomAdapter);

						if (!u.validQuery(text)) {
							mAutoCompleteAddRoomInputBarElement
									.setButtonText(null);
							autoCompleteCancel();
						} else {
							mAutoCompleteAddRoomInputBarElement
									.setButtonText("");
							// remove this if you don't want
							// automatic autocomplete
							// without pressing the button
							AutoCompleteRequest request = new AutoCompleteRequest(
									text, mModel.getGroupAccess());
							mController.autoCompleteBuilding(view, request);
						}
					}
				});
	}

	private void validAutoCompleteQuery(String query, View v) {
		if (u.validQuery(query)) {
			dismissSoftKeyBoard(v);
			AutoCompleteRequest request = new AutoCompleteRequest(query,
					mModel.getGroupAccess());
			mController.autoCompleteBuilding(this, request);
		} else {
			autoCompleteCancel();
		}
		activateDebug(query);
	}

	private void UIConstructAddFavoritesInputBar() {
		final IFreeRoomView view = this;

		mAutoCompleteAddFavoritesInputBarElement = new InputBarElement(
				this,
				null,
				getString(R.string.freeroom_check_occupancy_search_inputbarhint));
		mAutoCompleteAddFavoritesInputBarElement
				.setImeOptions(EditorInfo.IME_ACTION_SEARCH);

		// click on magnify glass on the keyboard
		mAutoCompleteAddFavoritesInputBarElement
				.setOnEditorActionListener(new OnEditorActionListener() {
					@Override
					public boolean onEditorAction(TextView v, int actionId,
							KeyEvent event) {
						if (actionId == EditorInfo.IME_ACTION_SEARCH) {
							String query = mAutoCompleteAddRoomInputBarElement
									.getInputText();
							validAutoCompleteQuery(query, v);
						}

						return true;
					}
				});

		// click on BUTTON magnify glass on the inputbar
		mAutoCompleteAddFavoritesInputBarElement
				.setOnButtonClickListener(new OnClickListener() {
					@Override
					public void onClick(View v) {
						String query = mAutoCompleteAddRoomInputBarElement
								.getInputText();
						validAutoCompleteQuery(query, v);
					}
				});

		mAddFavoritesAdapter = new FRRoomSuggestionArrayAdapter<FRRoom>(
				getApplicationContext(),
				R.layout.freeroom_layout_list_room_add_fav,
				R.id.freeroom_layout_list_room_add_fav,
				mAutoCompleteAddFavoritesArrayListFRRoom, mModel, true);

		mAutoCompleteAddFavoritesInputBarElement
				.setOnKeyPressedListener(new OnKeyPressedListener() {
					@Override
					public void onKeyPressed(String text) {
						mAutoCompleteAddFavoritesListView
								.setAdapter(mAddFavoritesAdapter);

						if (!u.validQuery(text)) {
							mAutoCompleteAddFavoritesInputBarElement
									.setButtonText(null);
							autoCompleteCancel();
						} else {
							mAutoCompleteAddFavoritesInputBarElement
									.setButtonText("");
							// remove this if you don't want
							// automatic autocomplete
							// without pressing the button
							AutoCompleteRequest request = new AutoCompleteRequest(
									text, mModel.getGroupAccess());
							mController.autoCompleteBuilding(view, request);
						}
					}
				});
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
	private void createAddRoomSuggestionsList() {
		mAutoCompleteAddRoomListView = new ListView(this);
		mAutoCompleteAddRoomInputBarElement
				.addView(mAutoCompleteAddRoomListView);

		mAutoCompleteAddRoomListView
				.setOnItemClickListener(new OnItemClickListener() {
					@Override
					public void onItemClick(AdapterView<?> adapter, View view,
							int pos, long id) {
						// when an item is clicked, the keyboard is dimissed
						dismissSoftKeyBoard(view);
						FRRoom room = mAutoCompleteAddRoomArrayListFRRoom
								.get(pos);
						addRoomToCheck(room);
						searchButton.setEnabled(auditSubmit() == 0);

						// WE DONT REMOVE the text in the input bar
						// INTENTIONNALLY: user may want to select multiple
						// rooms in the same building

						// refresh the autocomplete, such that selected
						// rooms are not displayed anymore
						autoCompletedUpdated();

					}
				});
		mAutoCompleteAddRoomListView.setAdapter(mAddRoomAdapter);
	}

	/**
	 * Initialize the autocomplete suggestion list
	 */
	private void createAddFavoritesSuggestionsList() {
		mAutoCompleteAddFavoritesListView = new ListView(this);
		mAutoCompleteAddFavoritesInputBarElement
				.addView(mAutoCompleteAddFavoritesListView);

		mAutoCompleteAddFavoritesListView
				.setOnItemClickListener(new OnItemClickListener() {
					@Override
					public void onItemClick(AdapterView<?> adapter, View view,
							int pos, long id) {
						// when an item is clicked, the keyboard is dimissed
						dismissSoftKeyBoard(view);
						FRRoom room = mAutoCompleteAddFavoritesArrayListFRRoom
								.get(pos);
						if (mModel.isFavorite(room)) {
							mModel.removeFavorite(room);
						} else {
							mModel.addFavorite(room);
						}

						// WE DONT REMOVE the text in the input bar
						// INTENTIONNALLY: user may want to select multiple
						// rooms in the same building
						autoCompletedUpdated();
					}
				});
		mAutoCompleteAddFavoritesListView.setAdapter(mAddFavoritesAdapter);
	}

	private void addRoomToCheck(FRRoom room) {
		// we only add if it already contains the room
		if (!selectedRooms.contains(room)) {
			selectedRooms.add(room);
			mSummarySelectedRoomsTextViewSearchMenu.setText(u
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
		// nextValid is today according to nextValidPeriod definition.
		Calendar nextValid = Calendar.getInstance();
		nextValid.setTimeInMillis(FRTimes.getNextValidPeriod()
				.getTimeStampStart());
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

	private void reset() {
		searchButton.setEnabled(false);

		// reset the list of selected rooms
		selectedRooms.clear();
		// TODO: mSummarySelectedRoomsTextView
		// .setText(getString(R.string.freeroom_check_occupancy_search_text_no_selected_rooms));

		mAutoCompleteAddRoomArrayListFRRoom.clear();

		resetTimes();

		anyButton.setChecked(true);
		mOptionalLineLinearLayoutWrapperFirst.setVisibility(View.GONE);
		mOptionalLineLinearLayoutWrapperSecond.setVisibility(View.GONE);

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
		fillSearchDialog(validRequest(false));
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
		enabledButtons();
		searchButton.setEnabled(auditSubmit() == 0);
	}

	private void enabledButtons() {
		upStartHourButton
				.setEnabled(startHourSelected <= FRTimes.LAST_HOUR_CHECK - 2);
		downStartHourButton
				.setEnabled(startHourSelected > FRTimes.FIRST_HOUR_CHECK);

		upEndHourButton.setEnabled(endHourSelected < FRTimes.LAST_HOUR_CHECK);
		downEndHourButton
				.setEnabled(endHourSelected >= FRTimes.FIRST_HOUR_CHECK + 2);
		upToEndHourButton.setEnabled(!upToEndSelected);
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
		showStartTimePickerShort.setText(times.formatTime(prepareFRFrPeriod()
				.getTimeStampStart(), true));
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
		showEndTimePickerShort.setText(times.formatTime(prepareFRFrPeriod()
				.getTimeStampEnd(), true));
		showEndTimePicker
				.setText(times.generateTimeSummaryWithPrefix(
						getString(R.string.freeroom_selectendHour), true, times
								.formatTime(prepareFRFrPeriod()
										.getTimeStampEnd(), false)));
		if (endHourSelected >= FRTimes.LAST_HOUR_CHECK
				|| (endHourSelected == FRTimes.LAST_HOUR_CHECK - 1 && endMinSelected != 0)) {
			upEndHourButton.setEnabled(false);
		} else {
			upEndHourButton.setEnabled(true);
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
		Set<FRRoom> userDef = new HashSet<FRRoom>(selectedRooms.size());
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
		FRRequestDetails details = new FRRequestDetails(period,
				freeButton.isChecked(), mUIDList, any, fav, user, userDef,
				mModel.getGroupAccess());
		mModel.setFRRequestDetails(details, save);
		mPrevRequestAdapter.notifyDataSetChanged();
		refresh();
		mSearchDialog.dismiss();

		resetUserDefined(true); // cleans the selectedRooms of userDefined
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
			return "ERROR: date not set";
		}
		if (startHourSelected == -1 || endHourSelected == -1
				|| startMinSelected == -1 || endMinSelected == -1) {
			return "ERROR: time not set";
		}

		// IF SET, we use the shared method checking the prepared period
		FRPeriod period = prepareFRFrPeriod();
		String errorsTime = FRTimes.validCalendarsString(period);
		boolean isValid = errorsTime.equals("") ? true : false;
		TextView tv = (TextView) mSearchView
				.findViewById(R.id.freeroom_layout_dialog_search_time_summary);
		if (isValid) {
			// time summary ?
			char limit = isLandscapeTabletMode() ? ' ' : '\n';
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
				return getString(
						R.string.freeroom_search_invalid_time_advanced,
						FRTimes.FIRST_HOUR_CHECK, FRTimes.LAST_HOUR_CHECK,
						FRTimes.MIN_MINUTE_INTERVAL,
						FRTimes.MAXIMAL_WEEKS_IN_PAST,
						FRTimes.MAXIMAL_WEEKS_IN_FUTURE);
			} else {
				return getString(R.string.freeroom_search_invalid_time_basic,
						FRTimes.FIRST_HOUR_CHECK, FRTimes.LAST_HOUR_CHECK,
						FRTimes.MIN_MINUTE_INTERVAL);
			}
		}
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
			tv.setText(getString(R.string.freeroom_search_invalid_request)
					+ error);
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

	private String auditSubmitString() {
		String ret = "";
		if (selectedRooms == null
				|| (!anyButton.isChecked() && userDefButton.isChecked() && selectedRooms
						.isEmpty())) {
			ret += getString(R.string.freeroom_search_check_empty_select);
		}

		if (anyButton.isChecked()
				&& (favButton.isChecked() || userDefButton.isChecked())) {
			ret += getString(R.string.freeroom_search_check_any_incompat);
		}
		if (!anyButton.isChecked() && !favButton.isChecked()
				&& !userDefButton.isChecked()) {
			ret += getString(R.string.freeroom_search_check_at_least);
		}
		boolean isFavEmpty = mModel.getFavorites().isEmpty();
		if (favButton.isChecked() && isFavEmpty) {
			if (!userDefButton.isChecked()) {
				ret += getString(R.string.freeroom_search_check_empty_fav);
			}
		}
		// we dont allow query all the room, including non-free
		if (anyButton.isChecked() && !freeButton.isChecked()) {
			ret += getString(R.string.freeroom_search_check_any_must_be_free);
		}
		return ret + auditTimesString();
	}

	@Override
	public void autoCompleteLaunch() {
		tvAutcompletStatusFav
				.setText(getString(R.string.freeroom_dialog_add_autocomplete_updating));
		tvAutcompletStatusRoom
				.setText(getString(R.string.freeroom_dialog_add_autocomplete_updating));
	}

	/**
	 * To be called when autocomplete is not lauchable and ask the user to type
	 * in.
	 */
	public void autoCompleteCancel() {
		mAutoCompleteAddFavoritesArrayListFRRoom.clear();
		mAddFavoritesAdapter.notifyDataSetInvalidated();
		mAutoCompleteAddRoomArrayListFRRoom.clear();
		mAddRoomAdapter.notifyDataSetInvalidated();

		tvAutcompletStatusFav
				.setText(getString(R.string.freeroom_dialog_add_autocomplete_typein));
		tvAutcompletStatusRoom
				.setText(getString(R.string.freeroom_dialog_add_autocomplete_typein));
	}

	/**
	 * Update the text message in autocomplete status text view
	 * (updating/up-to-date/error/...)
	 * 
	 * @param text
	 *            the new message to display.
	 */
	private void autoCompleteUpdateMessage(CharSequence text) {
		tvAutcompletStatusFav.setText(text);
		tvAutcompletStatusRoom.setText(text);
	}

	@Override
	public void autoCompletedUpdated() {
		mAddRoomAdapter.notifyDataSetInvalidated();
		mAddFavoritesAdapter.notifyDataSetInvalidated();
		mAutoCompleteAddRoomArrayListFRRoom.clear();
		mAutoCompleteAddFavoritesArrayListFRRoom.clear();
		boolean emptyResult = (mModel.getAutoComplete().values().size() == 0);
		if (emptyResult) {
			autoCompleteUpdateMessage(getString(R.string.freeroom_dialog_add_autocomplete_noresult));
		} else {
			autoCompleteUpdateMessage(getString(R.string.freeroom_dialog_add_autocomplete_uptodate));
		}

		// FIXME: adapt to use the new version of autocomplete mapped by
		// building
		Iterator<List<FRRoom>> iter = mModel.getAutoComplete().values()
				.iterator();
		while (iter.hasNext()) {
			List<FRRoom> list = iter.next();
			Iterator<FRRoom> iterroom = list.iterator();
			while (iterroom.hasNext()) {
				FRRoom room = iterroom.next();
				// rooms that are already selected are not displayed...
				if (!selectedRooms.contains(room)) {
					mAutoCompleteAddRoomArrayListFRRoom.add(room);
				}
				mAutoCompleteAddFavoritesArrayListFRRoom.add(room);
			}
		}

		/*
		 * If there was a non-empty result but all rooms got rejected, we
		 * display "no more" instead of "up-to-date". Not useful for favorites
		 * as no room is rejected.
		 */
		if (!emptyResult) {
			if (mAutoCompleteAddRoomArrayListFRRoom.isEmpty()) {
				tvAutcompletStatusRoom
						.setText(getString(R.string.freeroom_dialog_add_autocomplete_nomore));
			}
			if (mAutoCompleteAddFavoritesArrayListFRRoom.isEmpty()) {
				tvAutcompletStatusFav
						.setText(getString(R.string.freeroom_dialog_add_autocomplete_nomore));
			}
		}

		if (mSearchByUriTriggered) {
			searchByUriMakeRequest(mAutoCompleteAddRoomArrayListFRRoom);
		}

		mAddRoomAdapter.notifyDataSetChanged();
		mAddFavoritesAdapter.notifyDataSetChanged();
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
	 * Updates the colors example for the accurate {@link ColorBlindMode}.
	 */
	private void mParamDialogRefreshColorBlindExamples() {
		onColorBlindModeChangedUpdateBasicClick();

		FRPeriod period = new FRPeriod(System.currentTimeMillis(),
				System.currentTimeMillis() + FRTimes.ONE_HOUR_IN_MS, false);
		FRRoom room = new FRRoom("mock", "1234");
		List<ActualOccupation> occupancy = new ArrayList<ActualOccupation>(1);
		List<Occupancy> occupancies = new ArrayList<Occupancy>(4);
		Occupancy free = new Occupancy(room, occupancy, false, true, period);
		Occupancy part = new Occupancy(room, occupancy, true, true, period);
		Occupancy occupied = new Occupancy(room, occupancy, true, false, period);
		Occupancy error = new Occupancy(room, occupancy, false, false, period);
		occupancies.add(free);
		occupancies.add(part);
		occupancies.add(occupied);
		occupancies.add(error);
		List<TextView> textViews = new ArrayList<TextView>(4);
		mModel.getColoredDotDrawable(free);
		TextView free_text = (TextView) mParamView
				.findViewById(R.id.freeroom_layout_dialog_param_advanced_color_ex_free);
		TextView part_text = (TextView) mParamView
				.findViewById(R.id.freeroom_layout_dialog_param_advanced_color_ex_part);
		TextView occupied_text = (TextView) mParamView
				.findViewById(R.id.freeroom_layout_dialog_param_advanced_color_ex_occ);
		TextView error_text = (TextView) mParamView
				.findViewById(R.id.freeroom_layout_dialog_param_advanced_color_ex_err);
		textViews.add(free_text);
		textViews.add(part_text);
		textViews.add(occupied_text);
		textViews.add(error_text);
		for (int i = 0; i < 4; i++) {
			TextView tv = textViews.get(i);
			Occupancy occ = occupancies.get(i);
			tv.setBackgroundColor(mModel.getColorLine(occ));
			tv.setCompoundDrawablesWithIntrinsicBounds(
					mModel.getColoredDotDrawable(occ), 0, 0, 0);
		}
	}

	/**
	 * Updates the basic selection according to the change of the advanced
	 * selected {@link ColorBlindMode}.
	 */
	private void onColorBlindModeChangedUpdateBasicClick() {
		CheckBox basicCheckBox = (CheckBox) mParamView
				.findViewById(R.id.freeroom_layout_dialog_param_colorblind_basic);
		ColorBlindMode current = mModel.getColorBlindMode();
		if (current.equals(ColorBlindMode.DOTS_DISCOLORED)
				|| current
						.equals(ColorBlindMode.DOTS_SYMBOL_LINEFULL_DISCOLORED)) {
			basicCheckBox.setChecked(true);
		} else {
			basicCheckBox.setChecked(false);
		}
	}

	/**
	 * Listener to change some model parameter/settings.
	 * 
	 * @param v
	 *            the checkbox for basic color-blind mode.
	 */
	public void onColorBlindBasicChecked(View v) {
		int id;
		if (((CheckBox) v).isChecked()) {
			id = R.id.freeroom_layout_param_color_dots_disc;
		} else {
			id = R.id.freeroom_layout_param_color_default;
		}
		((RadioButton) mParamView.findViewById(id)).performClick();
		mParamDialogRefreshColorBlindExamples();
	}

	/**
	 * Listener to change some model parameter/settings.
	 * 
	 * @param v
	 *            the checkbox to choose advanced mode.
	 */
	public void onColorBlindAdvancedChecked(View v) {
		CheckBox advancedCheckBox = (CheckBox) v;
		CheckBox basicCheckBox = (CheckBox) mParamView
				.findViewById(R.id.freeroom_layout_dialog_param_colorblind_basic);
		LinearLayout advancedColor = (LinearLayout) mParamView
				.findViewById(R.id.freeroom_layout_dialog_param_advanced_color);
		if (advancedCheckBox.isChecked()) {
			advancedColor.setVisibility(View.VISIBLE);
			basicCheckBox.setVisibility(View.GONE);
		} else {
			advancedColor.setVisibility(View.GONE);
			basicCheckBox.setVisibility(View.VISIBLE);
			onColorBlindModeChangedUpdateBasicClick();
			onColorBlindBasicChecked(basicCheckBox);
		}
		mParamDialogRefreshColorBlindExamples();
	}

	/**
	 * Listener to change some model parameter/settings.
	 * 
	 * @param v
	 *            caller view (not used)
	 */
	public void onColorBlindModeSetDefault(View v) {
		mModel.setColorBlindMode(ColorBlindMode.DEFAULT);
		mParamDialogRefreshColorBlindExamples();
	}

	/**
	 * Listener to change some model parameter/settings.
	 * 
	 * @param v
	 *            caller view (not used)
	 */
	public void onColorBlindModeSetDotsDiscolored(View v) {
		mModel.setColorBlindMode(ColorBlindMode.DOTS_DISCOLORED);
		mParamDialogRefreshColorBlindExamples();
	}

	/**
	 * Listener to change some model parameter/settings.
	 * 
	 * @param v
	 *            caller view (not used)
	 */
	public void onColorBlindModeSetSymbolic(View v) {
		mModel.setColorBlindMode(ColorBlindMode.DOTS_SYMBOL);
		mParamDialogRefreshColorBlindExamples();
	}

	/**
	 * Listener to change some model parameter/settings.
	 * 
	 * @param v
	 *            caller view (not used)
	 */
	public void onColorBlindModeSetSymbolicLines(View v) {
		mModel.setColorBlindMode(ColorBlindMode.DOTS_SYMBOL_LINEFULL);
		mParamDialogRefreshColorBlindExamples();
	}

	/**
	 * Listener to change some model parameter/settings.
	 * 
	 * @param v
	 *            caller view (not used)
	 */
	public void onColorBlindModeSetSymbolicLinesDiscolored(View v) {
		mModel.setColorBlindMode(ColorBlindMode.DOTS_SYMBOL_LINEFULL_DISCOLORED);
		mParamDialogRefreshColorBlindExamples();
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
		boolean moreDetails = false;

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
		config.append("Android version: " + Build.VERSION.RELEASE);
		config.append(" (SDK " + Build.VERSION.SDK + ")" + s);

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
		long nowAsLong = mModel.getRegisteredTime();
		Date nowAsDate = new Date(nowAsLong);
		config.append("Local time/format: " + nowAsDate.toLocaleString() + s);

		if (!forUser) {
			config.append("*** TIME ***" + s);
			config.append("Default time: " + nowAsDate.toString() + s);
			config.append("GMT time: " + nowAsDate.toGMTString() + s);

			// test android formatting
			config.append("Time/milliseconds: " + nowAsLong + s);
			config.append("Is 24h format: " + DateFormat.is24HourFormat(this)
					+ s);
			java.text.DateFormat df = android.text.format.DateFormat
					.getTimeFormat(this);
			String time = df.format(nowAsDate);
			java.text.DateFormat dd = android.text.format.DateFormat
					.getDateFormat(this);
			String date = dd.format(nowAsDate);
			config.append("Android formatting: " + date + " // " + time + s);
			char[] mmddyyyy = DateFormat.getDateFormatOrder(this);
			config.append("DD/MM/YYYY format: " + Arrays.toString(mmddyyyy) + s);

			// test our own times methods
			config.append("times.formatTime(false): "
					+ times.formatTime(nowAsLong, false) + s);
			config.append("times.formatTime(true): "
					+ times.formatTime(nowAsLong, true) + s);

			// one week shift
			nowAsLong += FRTimes.ONE_WEEK_IN_MS;
			Calendar selected = Calendar.getInstance();
			FRPeriod period = new FRPeriod(nowAsLong, nowAsLong + 2
					* FRTimes.ONE_HOUR_IN_MS, false);
			selected.setTimeInMillis(nowAsLong);
			config.append("times.formatFullDate(): "
					+ times.formatFullDate(selected) + s);
			config.append("times.formatFullDateFullTimePeriod: "
					+ times.formatFullDateFullTimePeriod(period) + s);
		}

		if (!forUser && moreDetails) {
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
		}
		if (!forUser && moreDetails) {

			// hardware informations
			config.append("*** Other hardware informations ***" + s);
			config.append("Version.Codename: " + Build.VERSION.CODENAME + s);
			config.append("Version.Incremental: " + Build.VERSION.INCREMENTAL
					+ s);
			config.append("Version.SDK_int: " + Build.VERSION.SDK_INT + s);
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

	/**
	 * TODO: Beta only
	 */
	@Override
	public void networkErrorHappened() {
		showErrorDialog(getString(R.string.freeroom_connection_error_happened)
				+ "\n" + getString(R.string.freeroom_error_please_try_again));
	}

	/**
	 * TODO: Beta only
	 */
	@Override
	public void freeRoomServerBadRequest() {
		showErrorDialog(getString(R.string.freeroom_error_bad_request) + "\n"
				+ getString(R.string.freeroom_error_please_report));
	}

	/**
	 * TODO: Beta only
	 */
	@Override
	public void freeRoomServersInternalError() {
		showErrorDialog(getString(R.string.freeroom_error_internal_error)
				+ "\n" + getString(R.string.freeroom_error_please_report));
	}

	/**
	 * TODO: Beta only
	 */
	@Override
	public void freeRoomServersUnknownError() {
		showErrorDialog(getString(R.string.freeroom_error_unknown_error) + "\n"
				+ getString(R.string.freeroom_error_please_report));
	}

	/**
	 * Enum to describe Moves taken into account in Konami Code.
	 * <p>
	 * None represent the null action, if the trigger was not long enough.
	 * 
	 */
	private enum KonamiMove {
		NONE, UP, DOWN, LEFT, RIGHT;
	}

	/**
	 * Construct the list of ordered KonamiMove to enter.
	 */
	private void constructKonamiCode() {
		konamiMoveList = new ArrayList<KonamiMove>();
		konamiMoveList.add(KonamiMove.UP);
		konamiMoveList.add(KonamiMove.UP);
		konamiMoveList.add(KonamiMove.DOWN);
		konamiMoveList.add(KonamiMove.DOWN);
		konamiMoveList.add(KonamiMove.LEFT);
		konamiMoveList.add(KonamiMove.RIGHT);
		konamiMoveList.add(KonamiMove.LEFT);
		konamiMoveList.add(KonamiMove.RIGHT);
	}

	/**
	 * Stores the list of ordered KonamiMove.
	 */
	private List<KonamiMove> konamiMoveList;
	/**
	 * The minimal coordinate change to trigger a {@link KonamiMove}.
	 */
	private int konamiCodeMinChangeCoord = 50;
	/**
	 * The maximal time elapsed between two move to trigger a {@link KonamiMove}
	 */
	private long konamiCodeMaxTime = 2000;

	/**
	 * Stores the index to where the KonamiCode list has been triggered.
	 * <p>
	 * 0 means no trigger no so far, or trigger reset <br>
	 * 1-6 means trigger is ongoing <br>
	 * 7 means konami is triggered <br>
	 * 8 means konami has been triggered <br>
	 */
	private int konamiCodeCurrentIndex = 0;
	/**
	 * Stores the X-Coordinate of the start of the move
	 */
	private float konamiCodePrevX = 0;
	/**
	 * Stores the Y-Coordinate of the start of the move
	 */
	private float konamiCodePrevY = 0;

	/**
	 * Stores the time of end the last triggered {@link KonamiMove}.
	 */
	private long konamiCodeLastTimeTriggered = System.currentTimeMillis();

	/**
	 * Checks an event given by the System to detect a {@link KonamiMove}.
	 * <p>
	 * All moves on the screen starts by a {@link MotionEvent#ACTION_DOWN} event
	 * when the screen is pressed, followed a long list of
	 * {@link MotionEvent#ACTION_MOVE} when the user moves his finger and ends
	 * by a {@link MotionEvent#ACTION_UP} event when the user release the
	 * screen.
	 * <p>
	 * To detect a {@link KonamiMove}, we are interested in the difference of
	 * coordinates between the start {@link MotionEvent#ACTION_DOWN} and the end
	 * {@link MotionEvent#ACTION_UP} events. If the coordinates change more than
	 * {@link FreeRoomHomeView#konamiCodeMinChangeCoord}, then a
	 * {@link KonamiMove} will happen! If it's the next one in the
	 * {@link FreeRoomHomeView#konamiMoveList} according to
	 * {@link FreeRoomHomeView#konamiCodeCurrentIndex}, then the index is
	 * incremented. If not, the index is reset to the start (0).
	 * 
	 * @param event
	 *            a motion event to check.
	 */
	private void checkKonamiCode(MotionEvent event) {
		if (konamiCodeCurrentIndex >= 8) {
			return;
		} else if (event.getAction() == MotionEvent.ACTION_DOWN) {
			// action down/start: if too much time elapsed, reset
			if (konamiCodeCurrentIndex != 0) {
				long elapsed = System.currentTimeMillis()
						- konamiCodeLastTimeTriggered;
				if (elapsed > konamiCodeMaxTime) {
					System.out.println("rst time");
					konamiCodeCurrentIndex = 0;
				}
			}
			// action down/start: we store the start coordinates
			konamiCodePrevX = event.getX();
			konamiCodePrevY = event.getY();
			return;
		} else if (event.getAction() == MotionEvent.ACTION_UP) {
			// action up/end: check the coordinate changes
			float diffX = event.getX() - konamiCodePrevX;
			float diffY = event.getY() - konamiCodePrevY;
			float absX = Math.abs(diffX);
			float absY = Math.abs(diffY);
			KonamiMove move = KonamiMove.NONE;
			System.out.println(event.getX() + "/" + event.getY());
			System.out.println(konamiCodePrevX + "/" + konamiCodePrevY);
			System.out.println(diffX + "/" + diffY);
			if (absX < konamiCodeMinChangeCoord
					&& absY < konamiCodeMinChangeCoord) {
				System.out.println("no change sign");

				// if changes are not significant: NONE move.
			} else if (absY > absX) {
				// if more change on Y axe: up/down event.
				if (diffY > 0) {
					move = KonamiMove.DOWN;
				} else if (diffY < 0) {
					move = KonamiMove.UP;
				}
			} else {
				// if more change on X axe: left/right event.
				if (diffX > 0) {
					move = KonamiMove.RIGHT;
				} else if (diffX < 0) {
					move = KonamiMove.LEFT;
				}
			}

			System.out.println(move);
			if (move.equals(KonamiMove.NONE)) {
				// none event: reset
				konamiCodeCurrentIndex = 0;
				return;
			} else if (move.equals(konamiMoveList.get(konamiCodeCurrentIndex))) {
				// next event in the row: sucess
				// update index and time of last success
				konamiCodeLastTimeTriggered = System.currentTimeMillis();
				konamiCodeCurrentIndex++;
				if (konamiCodeCurrentIndex == 8) {
					konamiActivateKeyBoard();
					konamiCodeCurrentIndex++;
				}
				return;
			} else {
				konamiCodeCurrentIndex = 0;
				return;
			}
		}
	}

	/**
	 * Display the input edittext and keyboard to type "ba" and complete konami
	 * code.
	 */
	private void konamiActivateKeyBoard() {
		final LinearLayout konamiLayout = (LinearLayout) this
				.findViewById(R.id.freeroom_layout_home_konami);
		konamiLayout.setVisibility(View.VISIBLE);

		final EditText konamiEditText = (EditText) this
				.findViewById(R.id.freeroom_layout_home_konami_text);
		InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
		imm.showSoftInput(konamiEditText, InputMethodManager.SHOW_IMPLICIT);

		final Button konamiConfirm = (Button) this
				.findViewById(R.id.freeroom_layout_home_konami_confirm);
		konamiEditText.setOnEditorActionListener(new OnEditorActionListener() {

			@Override
			public boolean onEditorAction(TextView v, int actionId,
					KeyEvent event) {
				if (actionId == EditorInfo.IME_ACTION_GO) {
					konamiConfirm.performClick();
					return true;
				}
				return false;
			}
		});
		konamiConfirm.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				String text = konamiEditText.getText().toString();
				activateDebug(text);
				if (text.equalsIgnoreCase("ba")) {
					activateKonamiCode();
				}
				konamiEditText.setText("");
				dismissSoftKeyBoard(v);
				konamiLayout.setVisibility(View.GONE);
			}
		});
		konamiConfirm.setOnLongClickListener(new OnLongClickListener() {

			@Override
			public boolean onLongClick(View v) {
				String text = konamiEditText.getText().toString();
				activateDebug(text);
				if (text.equalsIgnoreCase("ba")) {
					activateKonamiCode();
				}
				return false;
			}
		});

	}

	/**
	 * Konami code as string in English.
	 */
	private final static String KonamiCodeEnglish = "UUDDLRLRBA";
	/**
	 * Konami code as string in French.
	 */
	private final static String KonamiCodeFrench = "hhbbgdgdBA";

	/**
	 * Prefix for all debug hidden functions.
	 */
	private final static String fct_prefix = "debug@jwvm:";
	private final static String fct_chgdate_on = "changedate=on";
	private final static String fct_chgdate_off = "changedate=off";
	private final static String fct_chggrp_on = "changegrp=on";
	private final static String fct_chggrp_off = "changegrp=off";

	/**
	 * Activates hidden functionnalities for debug puposes.
	 */
	private void activateDebug(String query) {
		if (query.equalsIgnoreCase(KonamiCodeEnglish)
				|| query.equalsIgnoreCase(KonamiCodeFrench)) {
			activateKonamiCode();
		}
		if (query.matches("[Dd][Aa][Tt][Ee]")) {
			mModel.setAdvancedTime(!mModel.getAdvancedTime());
			initSearchDialog();
			showErrorDialog("Change date switched");
		}
		if (query.matches("[Dd][Ee][Bb][Uu][Gg]")
				&& !query.startsWith(fct_prefix)) {
			boolean advanced = mModel.getAdvancedTime();
			mModel.setAdvancedTime(!advanced);
			initSearchDialog();
			if (advanced) {
				mModel.setGroupAccess();
				showErrorDialog("Debug mode deactivated!");
			} else {
				mModel.setGroupAccess(Integer.MAX_VALUE);
				showErrorDialog("Debug mode activated! Try with great care! :p");
			}
		}
		if (!query.startsWith(fct_prefix)) {
			return;
		} else if (query.equalsIgnoreCase(fct_prefix + fct_chgdate_on)) {
			mModel.setAdvancedTime(true);
			showErrorDialog("Change date activated");
			initSearchDialog();
		} else if (query.equalsIgnoreCase(fct_prefix + fct_chgdate_off)) {
			mModel.setAdvancedTime(false);
			showErrorDialog("Change date disabled");
			initSearchDialog();
		} else if (query.equalsIgnoreCase(fct_prefix + fct_chggrp_on)) {
			mModel.setGroupAccess(Integer.MAX_VALUE);
			showErrorDialog("Change group access activated");
		} else if (query.equalsIgnoreCase(fct_prefix + fct_chggrp_off)) {
			mModel.setGroupAccess();
			showErrorDialog("Change group access disabled");
		}
	}

	/**
	 * Activates and display the konami code.
	 * <p>
	 * This popup is NEVER closable. The user who cheats has to close the
	 * application by the application manager! :p
	 * <p>
	 * Never trust cheaters! :D
	 */
	private void activateKonamiCode() {
		ImageView konami = new ImageView(this);
		konami.setImageResource(R.drawable.konami);
		mErrorDialog.setView(konami);

		showErrorDialog("KONAMI CODE IS CHEATING! NOW FIND THE WAY OUT!"
				+ "\nIf you find or want other hidden functions, "
				+ "please contact us at freeroom.epfl@gmail.com :p");

		mErrorDialog.setOnDismissListener(new OnDismissListener() {
			@Override
			public void onDismiss(DialogInterface dialog) {
				mErrorDialog.show();
			}
		});
	}
}