package org.pocketcampus.plugin.freeroom.android;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import org.pocketcampus.platform.android.core.PluginController;
import org.pocketcampus.platform.android.core.PluginView;
import org.pocketcampus.plugin.freeroom.R;
import org.pocketcampus.plugin.freeroom.android.FreeRoomModel.ColorBlindMode;
import org.pocketcampus.plugin.freeroom.android.FreeRoomModel.HomeBehaviourRoom;
import org.pocketcampus.plugin.freeroom.android.FreeRoomModel.HomeBehaviourTime;
import org.pocketcampus.plugin.freeroom.android.FreeRoomModel.TimeLanguage;
import org.pocketcampus.plugin.freeroom.android.FreeRoomModel.TimePickersPref;
import org.pocketcampus.plugin.freeroom.android.adapter.ActualOccupationArrayAdapter;
import org.pocketcampus.plugin.freeroom.android.adapter.ExpandableListViewOccupancyAdapter;
import org.pocketcampus.plugin.freeroom.android.adapter.MessageFrequencyArrayAdapter;
import org.pocketcampus.plugin.freeroom.android.iface.IFreeRoomView;
import org.pocketcampus.plugin.freeroom.android.utils.FRRequestDetails;
import org.pocketcampus.plugin.freeroom.android.utils.FRTimesClient;
import org.pocketcampus.plugin.freeroom.android.utils.FRUtilsClient;
import org.pocketcampus.plugin.freeroom.shared.Constants;
import org.pocketcampus.plugin.freeroom.shared.FRAutoCompleteRequest;
import org.pocketcampus.plugin.freeroom.shared.FRImWorkingRequest;
import org.pocketcampus.plugin.freeroom.shared.FRMessageFrequency;
import org.pocketcampus.plugin.freeroom.shared.FROccupancyRequest;
import org.pocketcampus.plugin.freeroom.shared.FRPeriod;
import org.pocketcampus.plugin.freeroom.shared.FRPeriodOccupation;
import org.pocketcampus.plugin.freeroom.shared.FRRoom;
import org.pocketcampus.plugin.freeroom.shared.FRRoomOccupancy;
import org.pocketcampus.plugin.freeroom.shared.FRWorkingOccupancy;
import org.pocketcampus.plugin.freeroom.android.utils.FRStruct;
import org.pocketcampus.plugin.freeroom.shared.utils.FRTimes;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.DialogInterface.OnDismissListener;
import android.content.DialogInterface.OnShowListener;
import android.content.Intent;
import android.graphics.Rect;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.view.WindowManager.LayoutParams;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ExpandableListView;
import android.widget.ExpandableListView.OnGroupCollapseListener;
import android.widget.ExpandableListView.OnGroupExpandListener;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RadioButton;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.TextView.OnEditorActionListener;

import com.markupartist.android.widget.Action;

/**
 * <code>FreeRoomHomeView</code> is the main <code>View</code>, it's the entry
 * of the plugin. It displays the availabilities for the specified search given,
 * which can chosen or automatically done according to settings (default is free
 * room now, see {@link FreeRoomModel#getHomeBehaviourRoom()} and
 * {@link FreeRoomModel#getHomeBehaviourTime()}).
 * <p>
 * All others views are supposed to be dialog/popup windows, therefore the main
 * view is always visible on the background.
 * <p>
 * 12 <code>AlertDialog</code> that exists are the following: <br>
 * {@link #infoDetailsRoom}: display detailed information and occupancy about a
 * room, and give the ability to share the location. <br>
 * {@link #search} enables the user to enter a customized search, and retrieves
 * previously entered searches. <br>
 * {@link #favorites} show the current favorites, with the possibility to remove
 * them one by one. Adding is done by the ADD ROOM dialog. * <br>
 * {@link #addFavorites} gives the possibility to construct an user-defined list
 * of selected rooms, with auto-complete capabilities. It's used to add
 * favorites. <br>
 * {@link #addSearchRoom} gives the possibility to construct an user-defined
 * list of selected rooms, with auto-complete capabilities. It's used to add
 * rooms to a custom search. Room list could be edite through
 * {@link #editSearchRoom} <br>
 * {@link #editSearchRoom} give the possibility to edit the room you selected
 * with {@link #addSearchRoom}<br>
 * {@link #share} give the possibility to share the location with friends
 * through a share Intent. The server will also be notified, such that
 * approximately occupancies continues to be as accurate as possible. <br>
 * {@link #whoIsWorking} display who is working in this room at this current
 * time <br>
 * {@link #warning} display a warning message with "continue" and"cancel"
 * options. So far it's only used to erase favorites. <br>
 * {@link #error} display an error message with only the possibility to close
 * the popup <br>
 * {@link #welcome}: says hello and welcome to the user the first time and
 * provide information about using the app. And it says that their developper
 * are the best ;)<br>
 * {@link #settings} allow the user to change the default settings, like colors
 * for color-blind people of default home screen behavior.<br>
 * 
 * <P>
 * Class ordering and organisation: <br>
 * - general shared values, start at {@link #mController} <br>
 * - general overriden methods/constructor, start at
 * {@link #onDisplay(Bundle, PluginController)} <br>
 * - main UI initialization, start at {@link #initializeView()} <br>
 * - ActionBar actions, start at {@link #actionOverflow} <br>
 * - Menus handling, start at {@link #onCreateOptionsMenu(Menu)} <br>
 * - handling Intents and URIs, start at {@link #handleIntent(Intent)} <br>
 * - common methods, start at {@link #homeMainStartDefault()} <br>
 * - MVC View part, start at {@link #occupancyResultsUpdated()} <br>
 * - autocomplete management, shared by {@link #addSearchRoom} and
 * {@link #addFavorites}, start at {@link #autoCompleteUpdated()}
 * <p>
 * POPUP HANDLING (ordered in order of appearance) <br>
 * - infoDetail popup, start at {@link #infoDetailsRoom} <br>
 * - whoIsWorkingThere popup, start at {@link #whoIsWorking} <br>
 * - favorites popup, start at {@link #favorites} <br>
 * - share popup, start at {@link #share} <br>
 * - warning popup, start at {@link #warning} <br>
 * - error popup, start at {@link #error} <br>
 * - addFavorites popup, start at {@link #addFavorites} <br>
 * - addSearchRoom popup, start at {@link #addSearchRoom} <br>
 * - editSearchRoom popup, start at {@link #editSearchRoom} <br>
 * - managing the Search popup, start at {@link #search} <br>
 * - ... managing previous request and replay, start at
 * {@link #searchPreviousRequestInitTitle()} <br>
 * - settings popup, start at {@link #settings} <br>
 * - welcome popup, start at {@link #welcome} <br>
 * <p>
 * Other things <br>
 * - Konami codes detection, start at {@link KonamiCodeMove} <br>
 * - development and test hidden functions, start at {@link #devTestPrefix} <br>
 * 
 * @author FreeRoom Project Team (2014/05)
 * @author Julien WEBER <julien.weber@epfl.ch>
 * @author Valentin MINDER <valentin.minder@epfl.ch>
 */
public class FreeRoomHomeView extends FreeRoomAbstractView implements IFreeRoomView {

	private static final int SEARCH_ACTIVITY_REQUEST = 1234;
	/**
	 * COMMON: Reference to times utility method for client-side.
	 */
	private FRTimesClient times;

	/* COMMON SHARED VALUES */
	/**
	 * HOME: Width of the main Activity.
	 */
	private int activityWidth;
	/**
	 * HOME: Height of the main Activity.
	 */
	private int homeActivityHeight;
	/**
	 * COMMON: LayoutInflater for all Layout inflated from xml.
	 */
	private LayoutInflater commonLayoutInflater;

	/* UI OF MAIN ACTIVITY */
	/**
	 * HOME: Main layout that hold all UI components.
	 */
	private LinearLayout homeMainContentLayout;
	/**
	 * HOME: TextView to display a short message about what is currently
	 * displayed, or if an error occured.
	 */
	private TextView homeStatusTextView;
	/**
	 * HOME: ExpandableListView to display the results of occupancies building
	 * by building. See also {@link #homeResultExpListAdapter}.
	 */
	private ExpandableListView homeResultExpListView;
	/**
	 * HOME: Adapter for the results (to display the occupancies). See also
	 * {@link #homeResultExpListView}.
	 */
	private ExpandableListViewOccupancyAdapter<FRRoomOccupancy> homeResultExpListAdapter;

	/* MAIN ACTIVITY - OVERRIDEN METHODS */

	/**
	 * Overrides {@link PluginView#onDisplay(Bundle, PluginController)}, and
	 * construct most of the needed UI elements to display the main view.
	 * <p>
	 * See also: {@link #initializeView()}
	 */
	@Override
	protected void onDisplay(Bundle savedInstanceState, PluginController controller) {
		super.onDisplay(savedInstanceState, controller);
		times = mModel.getFRTimesClient(this);

		// Setup the layout
		commonLayoutInflater = this.getLayoutInflater();
		homeMainContentLayout = (LinearLayout) commonLayoutInflater.inflate(R.layout.freeroom_layout_home, null);
		// The ActionBar is added automatically when you call setContentView
		homeSetTitleDefault();

		homeResultExpListView = (ExpandableListView) homeMainContentLayout.findViewById(R.id.freeroom_layout_home_list);
		homeStatusTextView = (TextView) homeMainContentLayout.findViewById(R.id.freeroom_layout_home_text_summary);
		initializeView();

		setActionBarTitle(getString(R.string.freeroom_plugin_title));
	}

	/**
	 * Overrides {@link FreeRoomAbstractView#getMainControllerClass()}, and
	 * returns the reference to the controller class.
	 */
	@Override
	protected Class<? extends PluginController> getMainControllerClass() {
		return FreeRoomController.class;
	}

	/**
	 * Overrides {@link PluginView#onResume()}. This is called when the Activity
	 * is resumed, and simply replayed to the superclass.
	 * <p>
	 * This javadoc is unrelevant as user are not logged to use the FreeRoom
	 * plugin. If it becomes the case, please adapt this: <br>
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
	 * Overrides {@link FreeRoomAbstractView#anyError()} and please see this for
	 * javadoc.
	 * 
	 * To be called to show a generic error message.
	 */
	@Override
	public void anyError() {
		homeSetTitleError();
		String errorMessage = getString(R.string.freeroom_home_error_sorry);
		homeSetStatusTextSummary(errorMessage);
		// autoCompleteUpdateMessage(errorMessage);
		whoIsWorkingDisclaimer.setText(errorMessage);
	}

	/**
	 * Overrides {@link IFreeRoomView#refreshOccupancies()} and refreshes the
	 * current displayed results.
	 */
	@Override
	public void refreshOccupancies() {
		commonReplayRefresh();
	}

	/* MAIN ACTIVITY - INITIALIZATION */

	/**
	 * Overrides {@link IFreeRoomView#initializeView()} and construct all UI
	 * elements.
	 * <p>
	 * See also {@link #onDisplay(Bundle, PluginController)}
	 */
	@Override
	public void initializeView() {
		// retrieve display dimensions
		Rect displayRectangle = new Rect();
		Window window = this.getWindow();
		window.getDecorView().getWindowVisibleDisplayFrame(displayRectangle);
		activityWidth = displayRectangle.width();
		homeActivityHeight = displayRectangle.height();

		homeResultExpListAdapter = new ExpandableListViewOccupancyAdapter<FRRoomOccupancy>(getApplicationContext(),
				mModel.getOccupancyResults(), mController, this);
		homeResultExpListView.setAdapter(homeResultExpListAdapter);
		homeResultExpListView.setOnGroupExpandListener(new OnGroupExpandListener() {

			@Override
			public void onGroupExpand(int groupPosition) {
				// when we expand a group, it gets the focus
				// (highlighted)
				homeResultExpListAdapter.setGroupFocus(groupPosition);
			}
		});
		homeResultExpListView.setOnGroupCollapseListener(new OnGroupCollapseListener() {

			@Override
			public void onGroupCollapse(int groupPosition) {
				// when we collapse a group, no group has focused
				// (highlight)
				homeResultExpListAdapter.setGroupFocus(-1);
			}
		});
		// replay the onTouchEvent on the List to home View.
		homeResultExpListView.setOnTouchListener(new OnTouchListener() {

			@Override
			public boolean onTouch(View v, MotionEvent event) {
				onTouchEvent(event);
				return false;
			}
		});

		// init all other popup needed!
		initInfoDetailsRoomDialog();
		initShareDialog();
		initSettingsDialog();
		initWhoIsWorkingDialog();
		initWelcomeDialog();

		if (mModel.getRegisteredUserNeedUpdate()) {
			welcome.show();
			mModel.setRegisteredUserAuto();
		}
	}

	private void initActionBar() {
		removeAllActionsFromActionBar();
		// actionSearch action is always there, on phones AND tablet modes
		addActionToActionBar(actionSearch);

		addActionToActionBar(actionFavoritesEdit);
		addActionToActionBar(actionSettings);
		addActionToActionBar(actionRefresh);
		addActionToActionBar(actionAbout);
	}

	/* ACTIONS FOR THE ACTION BAR */

	/**
	 * ACTION/MENU: Action to open the settings.
	 * <p>
	 * Only added conditionally. Otherwise, go through menu or actionOverflow
	 * action.
	 */
	private Action actionSettings = new Action() {
		public void performAction(View view) {
			settings.show();
		}

		public int getDrawable() {
			return R.drawable.freeroom_ic_action_settings;
		}

		@Override
		public String getDescription() {
			// TODO Auto-generated method stub
			return null;
		}
	};
	/**
	 * ACTION/MENU: Action to perform a customized search, by showing the
	 * {@link #search} dialog.
	 */
	private Action actionSearch = new Action() {
		public void performAction(View view) {
			startActivityForResult(new Intent(FreeRoomHomeView.this, FreeRoomSearchActivity.class),
					SEARCH_ACTIVITY_REQUEST);
		}

		public int getDrawable() {
			return R.drawable.freeroom_ic_action_search;
		}

		@Override
		public String getDescription() {
			// TODO Auto-generated method stub
			return null;
		}
	};

	/**
	 * ACTION/MENU: Action to edit the user's favorites, by showing the
	 * favorites dialog.
	 * <p>
	 * Only added conditionally. Otherwise, go through menu or actionOverflow
	 * action.
	 */
	private Action actionFavoritesEdit = new Action() {
		public void performAction(View view) {
			startActivity(new Intent(FreeRoomHomeView.this, FreeRoomFavoritesActivity.class));
		}

		public int getDrawable() {
			return R.drawable.freeroom_ic_action_important;
		}

		@Override
		public String getDescription() {
			// TODO Auto-generated method stub
			return null;
		}
	};

	/**
	 * ACTION/MENU: Action to actionRefresh the data (it sends the same stored
	 * request again if not outdated, or generates a new request).
	 * <p>
	 * Please not that it replays the SAME request if it's not outdated, it wont
	 * generate a new default request!
	 * <p>
	 * Only added conditionally. Otherwise, go through menu or actionOverflow
	 * action.
	 */
	private Action actionRefresh = new Action() {
		public void performAction(View view) {
			homeMainStartDefault();
		}

		public int getDrawable() {
			return R.drawable.sdk_refresh;
		}

		@Override
		public String getDescription() {
			return getString(R.string.freeroom_refresh);
		}
	};

	/**
	 * ACTION/MENU: Action to open the about menu.
	 * <p>
	 */
	private Action actionAbout = new Action() {
		public void performAction(View view) {
			welcome.show();
		}

		public int getDrawable() {
			return R.drawable.sdk_info_white;
		}

		@Override
		public String getDescription() {
			// TODO Auto-generated method stub
			return getString(R.string.freeroom_about);
		}
	};

	// HANDLING INTENTS AND URI COMING FROM INSIDE OR OUTSIDE

	/**
	 * INTENT-HANDLE: Overrides {@link PluginView#handleIntent(Intent)} and
	 * handles an intent for coming from outside, eg. for a specific search
	 * asked by a QR code or another plugin/activity.
	 * <p>
	 * See the <code>AndroidManifest.xml</code> for more details.
	 * <p>
	 * If it's not a specific intended Intent, it will simply construct the
	 * default request and actionRefresh it. As specified in SDK javadoc, this
	 * method is called AFTER the onCreate and onResume methods, so it's used to
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
	 *            the intent to handle, as specified in overriden method.
	 */
	@Override
	protected void handleIntent(Intent intent) {
		// on resuming, u may have gone null, go direct start.
		if (u == null) {
			homeMainStartDefault();
			return;
		}
		u.logV("Starting the app: handling an Intent");
		// Intent and/or action seems to be null in some cases... so we just
		// skip these cases and launch the default settings.
		if (intent != null && intent.getAction() != null) {
			if (intent.getAction().equalsIgnoreCase("android.intent.action.MAIN")) {
				u.logV("starting MAIN and default mode");
				homeMainStartDefault();
			} else if (intent.getAction().equalsIgnoreCase("android.intent.action.VIEW")) {
				u.logV("starting the app and handling simple VIEW intent");
				String errorIntentHandled = "" + getString(R.string.freeroom_urisearch_error_URINotUnderstood) + " "
						+ getString(R.string.freeroom_urisearch_error_URINotSupported);
				String intentScheme = intent.getScheme();
				Uri intentUri = intent.getData();
				if (intentScheme != null && intentUri != null) {
					String intentUriHost = intentUri.getHost();
					String intentUriPath = intentUri.getPath();
					if (intentUriHost != null && intentUriPath != null) {
						String intentUriPathQuery = FRStruct.removeFirstCharSafely(intentUriPath);
						// using standard epfl http page
						if (intentScheme.equalsIgnoreCase("http")
								&& intentUriHost.equalsIgnoreCase("occupancy.epfl.ch")) {
							u.logV("Found an EPFL http://occupancy.epfl.ch/room URI");
							u.logV("With room query: \"" + intentUriPathQuery + "\"");
							errorIntentHandled = searchByIntentUriPrepareArguments(intentUriPathQuery);
						}

						/*
						 * using freeroom links. accepted formats are
						 * 
						 * pocketcampus ://freeroom.plugin.pocketcampus.org/show
						 * ?id=UID where UID is the EPFL id (with OR without the
						 * leading 1201XX)
						 * 
						 * pocketcampus://freeroom.plugin.pocketcampus.org/
						 * search ?name=NAM where NAM is the leading characters
						 * of the room (please note there is a search for NAM*
						 * on server-side so a search for "CO1" will also give
						 * "CO123", and a search for "BC" will also give
						 * "BCH4375", if this room is available)
						 * 
						 * pocketcampus://freeroom.plugin.pocketcampus.org/match
						 * ?name=NAME where NAME is the exact name of the toom
						 * OR the building (if want all BC rooms without BCH)
						 */
						else if ((intentScheme.equalsIgnoreCase("pocketcampus")
								|| intentScheme.equalsIgnoreCase("http") || intentScheme.equalsIgnoreCase(""))
								&& intentUriHost.equalsIgnoreCase("freeroom.plugin.pocketcampus.org")) {

							if ("/show".equals(intentUriPath) && intentUri.getQueryParameter("id") != null) {
								String uid = intentUri.getQueryParameter("id");
								// removing the epfl "room type" identifier, to
								// keep only the relevant id.
								if (uid.startsWith("1201")) {
									uid = uid.substring(4);
									while (uid.startsWith("0")) {
										uid = uid.substring(1);
									}
								}
								errorIntentHandled = searchByIntentUriPrepareArguments(uid);
							} else if ("/search".equals(intentUriPath) && intentUri.getQueryParameter("name") != null) {
								// the completion is added THERE (%) because the
								// autocomplete method is set to "exactmatch"
								errorIntentHandled = searchByIntentUriPrepareArguments(intentUri
										.getQueryParameter("name") + "%");
							} else if ("/match".equals(intentUriPath) && intentUri.getQueryParameter("name") != null) {
								errorIntentHandled = searchByIntentUriPrepareArguments(intentUri
										.getQueryParameter("name"));
							}
						} else {
							u.logE("Unknow URI: \"" + intentUri + "\"");
						}
					}
				}
				if (errorIntentHandled.length() != 0) {
					searchByIntentUriOnError(errorIntentHandled);
				}
			} else {
				u.logE("ERROR: Found an unhandled action: \"" + intent.getAction() + "\"");
				u.logE("Starting the app in default mode anyway");
				homeMainStartDefault();
			}
		} else {
			if (intent == null) {
				u.logE("ERROR: Found a null Intent !!!");
			} else if (intent.getAction() == null) {
				u.logE("ERROR: Found a null Action !!!");
				u.logE("This issue may appear by launching the app from the pocketcampus dashboard");
			}

			u.logE("Starting the app in default mode anyway");
			homeMainStartDefault();
		}
	}

	/**
	 * INTENT-HANDLE: In case of error while handling an Intent, display a
	 * message to the user, and then launch a default request (without taking
	 * the favorites into account).
	 * 
	 * @param errorMessage
	 *            the error message to display.
	 */
	private void searchByIntentUriOnError(String errorMessage) {
		// display an error message when the intent/uri handling lead to a
		// problem.
		errorDialogShowMessage(getString(R.string.freeroom_urisearch_error_basis) + "\n" + errorMessage + "\n"
				+ getString(R.string.freeroom_urisearch_error_end));
		u.logE(getString(R.string.freeroom_urisearch_error_basis));
		u.logE(errorMessage);
		u.logE(getString(R.string.freeroom_urisearch_error_end));
		if (mController != null && mModel != null) {
			homeInitDefaultRequest(false);
			commonReplayRefresh();
		}
	}

	/**
	 * INTENT-HANDLE: Stores if a search by URI has been initiated recently, in
	 * order for auto-complete to automatically launch a new search if
	 * triggered, using <code>searchByUriMakeRequest</code>
	 */
	private boolean searchByIntentUriTriggered = false;

	/**
	 * INTENT-HANDLE: Initiates a search by URI with the given constraint as the
	 * argument for the auto-complete.
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
	private String searchByIntentUriPrepareArguments(String constraint) {
		if (constraint.length() < Constants.MIN_AUTOCOMPL_LENGTH) {
			return getString(R.string.freeroom_urisearch_error_AutoComplete_error) + " "
					+ getString(R.string.freeroom_urisearch_error_AutoComplete_precond);
		} else {
			searchByIntentUriTriggered = true;
			// if the URI is triggered, we want to give access to the room,
			// event if the user might no have right to see the room.
			FRAutoCompleteRequest req = new FRAutoCompleteRequest(constraint, Math.max(mModel.getGroupAccess(),
					Integer.MAX_VALUE));
			// set to exact match (if you want autocompletion, please add a "%"
			// to your constraint)
			req.setExactString(true);
			mController.autoCompleteBuilding(this, req);
			return "";
		}
	}

	/**
	 * INTENT-HANDLE: Make a FRRequest with the FRRoom given in argument, for
	 * the rest of the day. If the argument is empty, it will display free room
	 * now.
	 * 
	 * @param collection
	 *            collection of FRRoom to make a new search on.
	 */
	private void searchByIntentUriMakeRequest(Collection<FRRoom> collection) {
		searchByIntentUriTriggered = false;

		boolean empty = collection.isEmpty();
		if (empty) {
			// if nothing matched the search, we notify the user.
			searchByIntentUriOnError(getString(R.string.freeroom_urisearch_error_AutoComplete_error) + " "
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
			request = new FRRequestDetails(period, false, uidList, false, false, true, uidNonFav,
					mModel.getGroupAccess());
			mModel.setFRRequestDetails(request, !empty);
			// searchPreviousRequestAdapter.notifyDataSetChanged();
			commonReplayRefresh();
		}
	}

	// COMMON METHODS

	/**
	 * HOME: Constructs the default request and refreshes it.
	 * <p>
	 * If a previous request exists and it's not outdated, it wont construct a
	 * new request but use this one instead.
	 */
	private void homeMainStartDefault() {
		if (mController != null && mModel != null) {
			u.logV("Starting in default mode.");
			FRRequestDetails req = mModel.getFRRequestDetails();
			// if no previous request or it's outdated
			int MIN_OUDATED = 2;
			long timeOut = MIN_OUDATED * FRTimes.ONE_MIN_IN_MS;
			if (req == null || req.isOutDated(timeOut)) {
				homeInitDefaultRequest(false);
			} else {
				u.logV("existing request will be reused");
			}
			commonReplayRefresh();
			u.logV("Successful start in default mode: wait for server response.");
		} else {
			// CANT LOG using utils because null after a while.
			Log.e("defaultmainstart", "Controller or Model not defined: cannot start default mode.");
		}
	}

	/**
	 * HOME: Constructs the default request and sets it in the model for future
	 * use. You may call <code>actionRefresh</code> in order to actually send it
	 * to the server.
	 * 
	 * @param forceUseFavorites
	 *            if the constructor of the request should consider the
	 *            favorites or not
	 */
	private void homeInitDefaultRequest(boolean forceUseFavorites) {
		u.logV("generating and setting a new default request");
		mModel.setFRRequestDetails(homeValidRequest(forceUseFavorites), false);
		// searchPreviousRequestAdapter.notifyDataSetChanged();
	}

	/**
	 * COMMON: Asks the controller to send again the request which was already
	 * set in the model.
	 * <p>
	 * Don't call it before setting a request in the model!
	 */
	private void commonReplayRefresh() {
		homeSetStatusTextSummary(getString(R.string.freeroom_home_please_wait));
		homeSetTitleUpdating();
		// cleans the previous results
		mModel.getOccupancyResults().clear();
		homeResultExpListAdapter.notifyDataSetChanged();
		mController.sendFRRequest(this);
	}

	/**
	 * HOME: Sets the summary text box to the specified text.
	 * <p>
	 * It doesn't need to start by a space, the text view already contains an
	 * appropriate padding.
	 * 
	 * @param text
	 *            the new summary to be displayed.
	 */
	private void homeSetStatusTextSummary(String text) {
		homeStatusTextView.setText(text);
	}

	/**
	 * HOME: Sets the title to the default value (FreeRoom: Home)
	 * <p>
	 * So far, this happens only at the very beginning, and immediately
	 * overridden by a refreshing.
	 */
	private void homeSetTitleDefault() {
		homeSetTitle(getString(R.string.freeroom_title_main_title));
	}

	/**
	 * HOME: Sets the title to the default error value (FreeRoom: error :/)
	 * <p>
	 * Used by transmission/network error, internal server error, bad request.
	 */
	private void homeSetTitleError() {
		homeSetTitle(getString(R.string.freeroom_title_main_title_error));
		setUnrecoverableErrorOccurred(getString(R.string.freeroom_title_main_title_error));
	}

	/**
	 * HOME: Sets the title to the default no result value (FreeRoom: no result
	 * :/)
	 * <p>
	 * Used when a successfully answer was received, but empty! (eg when no
	 * selected room is free!)
	 */
	private void homeSetTitleNoResults() {
		homeSetTitle(getString(R.string.freeroom_title_main_title_no_result));
		setUnrecoverableErrorOccurred(getString(R.string.freeroom_title_main_title_no_result));
	}

	/**
	 * HOME: Sets the title to the default udpating value (FreeRoom:
	 * updating...)
	 */
	private void homeSetTitleUpdating() {
		homeSetTitle(getString(R.string.freeroom_title_main_title_updating));
		setLoadingContentScreen();
	}

	/**
	 * HOME: Sets the title to the given value.
	 * 
	 * @param titleValue
	 *            the new title
	 */
	private void homeSetTitle(String titleValue) {
		setActionBarTitle(titleValue);
	}

	// MVC INTERFACE

	/**
	 * MVC METHOD: Override {@link IFreeRoomView#occupancyResultsUpdated()} and
	 * notify the occupancy results have been updated. It will actionRefresh the
	 * home screen results displayed accordingly.
	 */
	@Override
	public void occupancyResultsUpdated() {
		homeSetTitleUpdating();
		String subtitle = "";
		initActionBar();
		if (mModel.getOccupancyResults().isEmpty()) {
			// popup with no results message

			subtitle = getString(R.string.freeroom_home_error_no_results);
			super.setUnrecoverableErrorOccurred(subtitle);
			errorDialogShowMessage(subtitle);
			homeSetTitleNoResults();
		} else {
			setContentView(homeMainContentLayout);
			FROccupancyRequest request = mModel.getFRRequestDetails();

			String title = "";
			if (request.isOnlyFreeRooms()) {
				title = getString(R.string.freeroom_home_info_free_rooms);
			} else {
				title = getString(R.string.freeroom_home_info_rooms);
			}
			FRPeriod period = mModel.getOverAllTreatedPeriod();
			homeSetTitle(title + times.formatTimeSummaryTitle(period));
			subtitle = times.formatFullDateFullTimePeriod(period);

			// if the info dialog is opened, we update the CORRECT occupancy
			// with the new data.
			if (infoDetailsRoom.isShowing()) {
				FRRoom room = mModel.getDisplayedOccupancy().getRoom();
				List<?> list = mModel.getOccupancyResults().get(mModel.getBuildingKeyLabel(room));
				Iterator<?> iter = list.iterator();
				label: while (iter.hasNext()) {
					Object o = iter.next();
					if (o instanceof FRRoomOccupancy) {
						if (((FRRoomOccupancy) o).getRoom().getUid().equals(room.getUid())) {
							mModel.setDisplayedOccupancy((FRRoomOccupancy) o);
							// doesn't work
							infoDetailsActualOccupationAdapter.notifyDataSetChanged();
							// works!
							infoDetailsDisplayDialog();
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
					if (elem != null && elem instanceof FRRoomOccupancy) {
						mModel.setDisplayedOccupancy((FRRoomOccupancy) elem);
						infoDetailsDisplayDialog();
					}
				}
			}
		}

		homeSetStatusTextSummary(subtitle);
		homeResultExpListAdapter.notifyDataSetChanged();
		homeUpdateCollapse(homeResultExpListView, homeResultExpListAdapter);

	}

	/**
	 * MVC: Expands all the groups if there are no more than 3 groups AND not
	 * more than 7 results.
	 * <p>
	 * These constants are defined ONLY there <br>
	 * 
	 * @param ev
	 *            expandable list view
	 * @param ad
	 *            expandable list view adapter
	 */
	private void homeUpdateCollapse(ExpandableListView ev, ExpandableListViewOccupancyAdapter<FRRoomOccupancy> ad) {
		int maxChildrenToExpand = 7;
		int maxGroupToExpand = 3;
		if (ad.getGroupCount() <= maxGroupToExpand && ad.getChildrenTotalCount() <= maxChildrenToExpand) {
			// we expand in reverse order for performance reason!
			// expand all may caus
			for (int i = ad.getGroupCount() - 1; i >= 0; i--) {
				ev.expandGroup(i);
			}
		}
		ad.setGroupFocus(-1);
	}

	// INFO DETAILS

	/**
	 * {@link #infoDetailsRoom}: AlertDialog that holds the
	 * {@link #infoDetailsRoom} dialog.
	 */
	private AlertDialog infoDetailsRoom;
	/**
	 * {@link #infoDetailsRoom}: View that holds the {@link #infoDetailsRoom}
	 * dialog content, defined in xml in layout folder.
	 */
	private View infoDetailsRoomView;
	/**
	 * {@link #infoDetailsRoom}: Adapter for the ActualOccupation displayed in a
	 * ListView.
	 */
	private ActualOccupationArrayAdapter<FRPeriodOccupation> infoDetailsActualOccupationAdapter;

	/**
	 * {@link #infoDetailsRoom}: Inits the {@link #infoDetailsRoom} the
	 * information about a room and it's ActualOccupation.
	 */
	private void initInfoDetailsRoomDialog() {
		// Instantiate an AlertDialog.Builder with its constructor
		AlertDialog.Builder builder = new AlertDialog.Builder(this);
		builder.setIcon(R.drawable.freeroom_ic_action_view_as_list);
		// erased when calling the intended method to show the details
		builder.setTitle("Mock title");
		builder.setNegativeButton(getString(R.string.freeroom_dialog_fav_close), null);

		// Get the AlertDialog from create()
		infoDetailsRoom = builder.create();

		// redefine paramaters to dim screen when displayed
		WindowManager.LayoutParams lp = infoDetailsRoom.getWindow().getAttributes();
		lp.dimAmount = 0.60f;
		// these doesn't work
		lp.width = LayoutParams.FILL_PARENT;
		lp.height = LayoutParams.WRAP_CONTENT;
		infoDetailsRoom.getWindow().addFlags(WindowManager.LayoutParams.FLAG_WATCH_OUTSIDE_TOUCH);
		infoDetailsRoom.getWindow().addFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND);
		infoDetailsRoom.getWindow().setAttributes(lp);

		infoDetailsRoomView = commonLayoutInflater.inflate(R.layout.freeroom_layout_dialog_info, null);

		ListView lv = (ListView) infoDetailsRoomView.findViewById(R.id.freeroom_layout_dialog_info_roomOccupancy);

		ViewGroup header = (ViewGroup) commonLayoutInflater.inflate(R.layout.freeroom_layout_dialog_info_header, lv,
				false);
		lv.addHeaderView(header, null, false);
		ViewGroup footer = (ViewGroup) commonLayoutInflater.inflate(R.layout.freeroom_layout_dialog_info_footer, lv,
				false);
		lv.addFooterView(footer, null, false);

		infoDetailsRoom.setView(infoDetailsRoomView);

		infoDetailsRoom.setOnShowListener(new OnShowListener() {
			@Override
			public void onShow(DialogInterface dialog) {
				trackEvent("Details", null);
			}
		});
	}

	/**
	 * {@link #infoDetailsRoom}: display the dialog that provides more info
	 * about the occupation of the selected room.
	 * <p>
	 * Called by the {@link ExpandableListViewOccupancyAdapter} when clicking on
	 * a line.
	 */
	public void infoDetailsDisplayDialog() {
		final FRRoomOccupancy mOccupancy = mModel.getDisplayedOccupancy();
		if (mOccupancy != null) {
			infoDetailsRoom.hide();
			infoDetailsRoom.show();

			final FRRoom mRoom = mOccupancy.getRoom();
			infoDetailsRoom.setTitle(FRUtilsClient.formatRoom(mRoom));

			TextView periodTextView = (TextView) infoDetailsRoomView
					.findViewById(R.id.freeroom_layout_dialog_info_period);
			if (mOccupancy.isSetTreatedPeriod() && mOccupancy.getTreatedPeriod() != null) {
				periodTextView.setText(times.formatFullDateFullTimePeriod(mOccupancy.getTreatedPeriod()));
			} else {
				// error coming from server ??
				periodTextView.setText("period");
				System.err.println("Unknown or undefined period coming from server");
			}

			// people image to replay worst case occupancy and direct share with
			// server
			ImageView peopleImageView = (ImageView) infoDetailsRoomView
					.findViewById(R.id.freeroom_layout_dialog_info_people);
			peopleImageView.setImageResource(mModel.getImageFromRatioOccupation(mOccupancy
					.getRatioWorstCaseProbableOccupancy()));
			peopleImageView.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View arg0) {
					shareDirectWithServer(mOccupancy.getTreatedPeriod(), mRoom);
				}
			});

			ImageView map = (ImageView) infoDetailsRoomView.findViewById(R.id.freeroom_layout_dialog_info_map);
			map.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {
					try {
						// TODO: when map plugin accepts UID and not only room
						// name, change by uid instead of doorcode!
						Uri mUri = Uri.parse("pocketcampus://map.plugin.pocketcampus.org/search");
						Uri.Builder mbuild = mUri.buildUpon().appendQueryParameter("q", mRoom.getDoorCode());
						Intent i = new Intent(Intent.ACTION_VIEW, mbuild.build());
						i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
						startActivity(i);
					} catch (Exception e) {
						e.printStackTrace();
						System.err.println("Map plugin not installed ?!?");
						errorDialogShowMessage(getString(R.string.freeroom_error_map_plugin_missing));
					}
				}
			});

			ImageView shareImageView = (ImageView) infoDetailsRoomView
					.findViewById(R.id.freeroom_layout_dialog_info_share);
			shareSetClickListener(shareImageView, this, mOccupancy);

			Button shareButton = infoDetailsRoom.getButton(AlertDialog.BUTTON_POSITIVE);
			shareButton.setEnabled(mOccupancy.isIsFreeAtLeastOnce() && !mOccupancy.isIsOccupiedAtLeastOnce());
			shareSetClickListener(shareButton, this, mOccupancy);

			ListView roomOccupancyListView = (ListView) infoDetailsRoomView
					.findViewById(R.id.freeroom_layout_dialog_info_roomOccupancy);
			infoDetailsActualOccupationAdapter = new ActualOccupationArrayAdapter<FRPeriodOccupation>(
					getApplicationContext(), mOccupancy, mController, this);
			roomOccupancyListView.setAdapter(infoDetailsActualOccupationAdapter);

			TextView detailsTextView = (TextView) infoDetailsRoomView
					.findViewById(R.id.freeroom_layout_dialog_info_details);

			// UID and surface are display only to user who have special rights
			// (time and room access), and therefore also in debug mode.
			boolean value = mModel.getAdvancedTime() && (mModel.getGroupAccess() > mModel.DEFAULT_GROUP_ACCESS);
			detailsTextView.setText(u.getInfoFRRoom(mOccupancy.getRoom(), value, value));
			infoDetailsRoom.show();
		}
	}

	// WHO IS WORKING

	/**
	 * {@link #whoIsWorking}: Dialog that holds the ImWorking Dialog.
	 */
	private AlertDialog whoIsWorking;
	/**
	 * {@link #whoIsWorking}: View that holds the ImWorking dialog content,
	 * defined in xml in layout folder.
	 */
	private View whoIsWorkingView;
	/**
	 * {@link #whoIsWorking}: Time summary in "working there" dialog.
	 */
	private TextView whoIsWorkingTimeSummary;
	/**
	 * {@link #whoIsWorking}: Disclaimer/please wait in "working there" dialog.
	 */
	private TextView whoIsWorkingDisclaimer;
	/**
	 * {@link #whoIsWorking}: List of displayed working message.
	 */
	private List<FRMessageFrequency> whoIsWorkingMessageList;
	/**
	 * {@link #whoIsWorking}: Adpater for message and their frequency.
	 */
	private ArrayAdapter<FRMessageFrequency> whoIsWorkingMessageAdapter;

	/**
	 * {@link #whoIsWorking}: Inits the {@link #whoIsWorking} to show what
	 * people are doing.
	 */
	private void initWhoIsWorkingDialog() {
		// Instantiate an AlertDialog.Builder with its constructor
		AlertDialog.Builder builder = new AlertDialog.Builder(this);
		builder.setIcon(R.drawable.freeroom_ic_action_view_as_list);
		// erased when calling the intended method to show the details
		builder.setTitle(getString(R.string.freeroom_whoIsWorking_title));

		// Get the AlertDialog from create()
		whoIsWorking = builder.create();

		// redefine paramaters to dim screen when displayed
		WindowManager.LayoutParams lp = whoIsWorking.getWindow().getAttributes();
		lp.dimAmount = 0.60f;
		// these doesn't work
		lp.width = LayoutParams.FILL_PARENT;
		lp.height = LayoutParams.WRAP_CONTENT;
		whoIsWorking.getWindow().addFlags(WindowManager.LayoutParams.FLAG_WATCH_OUTSIDE_TOUCH);
		whoIsWorking.getWindow().addFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND);
		whoIsWorking.getWindow().setAttributes(lp);

		whoIsWorkingView = commonLayoutInflater.inflate(R.layout.freeroom_layout_dialog_working, null);

		// these work perfectly
		// whoIsWorkingView.setMinimumWidth((int) (activityWidth * 0.9f));
		// whoIsWorkingView.setMinimumHeight((int) (homeActivityHeight * 0.8f));

		whoIsWorking.setView(whoIsWorkingView);

		whoIsWorking.setOnShowListener(new OnShowListener() {
			@Override
			public void onShow(DialogInterface dialog) {
				whoIsWorkingDisclaimer.setText(R.string.freeroom_whoIsWorking_wait);
				trackEvent("WorkingThere", null);
			}
		});

		ListView lv = (ListView) whoIsWorkingView.findViewById(R.id.freeroom_layout_dialog_working_time_list);

		ViewGroup header = (ViewGroup) commonLayoutInflater.inflate(R.layout.freeroom_layout_dialog_working_header, lv,
				false);
		lv.addHeaderView(header, null, false);
		ViewGroup footer = (ViewGroup) commonLayoutInflater.inflate(R.layout.freeroom_layout_dialog_working_footer, lv,
				false);
		lv.addFooterView(footer, null, false);

		whoIsWorkingTimeSummary = (TextView) whoIsWorkingView.findViewById(R.id.freeroom_layout_dialog_working_time);
		whoIsWorkingDisclaimer = (TextView) whoIsWorkingView
				.findViewById(R.id.freeroom_layout_dialog_working_disclaimer);

		whoIsWorkingMessageList = mModel.getListMessageFrequency();
		whoIsWorkingMessageAdapter = new MessageFrequencyArrayAdapter<FRMessageFrequency>(this,
				getApplicationContext(), R.layout.freeroom_layout_message, R.id.freeroom_layout_message_text,
				whoIsWorkingMessageList);
		lv.setAdapter(whoIsWorkingMessageAdapter);

		whoIsWorking.setOnDismissListener(new OnDismissListener() {

			@Override
			public void onDismiss(DialogInterface dialog) {
				whoIsWorkingMessageList.clear();
				whoIsWorkingMessageAdapter.notifyDataSetInvalidated();
			}
		});
	}

	/**
	 * {@link #whoIsWorking}: Display the working dialog with the given room and
	 * period.
	 * 
	 * @param room
	 *            room displayed
	 * @param period
	 *            period displayed
	 */
	public void whoIsWorkingDisplayDialog(FRRoom room, FRPeriod period) {
		whoIsWorking.show();
		whoIsWorking.setTitle(FRUtilsClient.formatRoom(room));
		whoIsWorkingTimeSummary.setText(times.formatFullDateFullTimePeriod(period));
	}

	/**
	 * MVC METHOD ({@link #whoIsWorking}): Override
	 * {@link IFreeRoomView#workingMessageUpdated()} and notifies the user
	 * message have been updated. Refresh the {@link #whoIsWorking} accordingly
	 * (if it's shown).
	 */
	@Override
	public void workingMessageUpdated() {
		whoIsWorkingDisclaimer.setText(getString(R.string.freeroom_whoIsWorking_disclaimer));
		whoIsWorkingMessageAdapter.notifyDataSetChanged();
	}

	// SHARE

	/**
	 * {@link #share}: Dialog that holds the {@link #share} Dialog.
	 */
	private AlertDialog share;
	/**
	 * {@link #share}: View that holds the {@link #share} dialog content,
	 * defined in xml in layout folder.
	 */
	private View shareView;
	/**
	 * {@link #share}: TextView summarizing the share intent/text/information
	 * that will be sent to friend/server.
	 */
	private TextView shareDialogTextViewSummarySharing;
	/**
	 * {@link #share}: EditText to share the activity/work the user is doing.
	 */
	private EditText shareDialogEditTextMessageWorking;

	/**
	 * {@link #share}: Inits the {@link #share} dialog to share a message with
	 * friends and/or the server.
	 */
	private void initShareDialog() {
		// Instantiate an AlertDialog.Builder with its constructor
		AlertDialog.Builder builder = new AlertDialog.Builder(this);
		builder.setTitle(getString(R.string.freeroom_dialog_share_title));
		builder.setPositiveButton(getString(R.string.freeroom_dialog_share_button_friends), null);
		builder.setNegativeButton(getString(R.string.freeroom_search_cancel), null);
		builder.setNeutralButton(getString(R.string.freeroom_dialog_share_button_server), null);
		builder.setIcon(R.drawable.freeroom_ic_action_share);

		// Get the AlertDialog from create()
		share = builder.create();

		// redefine paramaters to dim screen when displayed
		WindowManager.LayoutParams lp = share.getWindow().getAttributes();
		lp.dimAmount = 0.60f;
		// these doesn't work
		lp.width = LayoutParams.FILL_PARENT;
		lp.height = LayoutParams.FILL_PARENT;
		share.getWindow().addFlags(WindowManager.LayoutParams.FLAG_WATCH_OUTSIDE_TOUCH);
		share.getWindow().addFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND);
		share.getWindow().setAttributes(lp);

		shareView = commonLayoutInflater.inflate(R.layout.freeroom_layout_dialog_share, null);
		// these work perfectly
		shareView.setMinimumWidth((int) (activityWidth * 0.95f));
		// shareView.setMinimumHeight((int) (homeActivityHeight * 0.8f));

		share.setOnShowListener(new OnShowListener() {
			@Override
			public void onShow(DialogInterface dialog) {
				commonDismissSoftKeyBoard(shareView);
				trackEvent("Share", null);
			}
		});

		share.setOnDismissListener(new OnDismissListener() {

			@Override
			public void onDismiss(DialogInterface dialog) {
				// we dont remove the text, as the user may want to share again
				// the same text!
			}
		});

		shareDialogTextViewSummarySharing = (TextView) shareView
				.findViewById(R.id.freeroom_layout_dialog_share_textBasic);

		shareDialogEditTextMessageWorking = (EditText) shareView
				.findViewById(R.id.freeroom_layout_dialog_share_text_edit);

		share.setView(shareView);

		share.show();
		share.hide();
	}

	/**
	 * {@link #share}: Notify when the EditText (Message-Working) is updated to
	 * generate the summary and dimiss the keyboard.
	 * 
	 * @param mPeriod
	 *            period selected.
	 * @param mRoom
	 *            room selected.
	 */
	private void shareDialogEditTextMessageWorkingUpdated(final FRPeriod mPeriod, final FRRoom mRoom) {
		String text = shareDialogEditTextMessageWorking.getText().toString();
		if (text == null || text.length() == 0) {
			text = "...";
		}
		shareDialogTextViewSummarySharing.setText(u.wantToShare(mPeriod, mRoom, text));
		commonDismissSoftKeyBoard(shareDialogTextViewSummarySharing);
	}

	/**
	 * {@link #share}: Put a onClickListener on an imageView in order to share
	 * the location and time when clicking share, if available.
	 * 
	 * @param shareView
	 *            the view on which to put the listener (might be an image or
	 *            not!)
	 * @param homeView
	 *            reference to the home view
	 * @param mOccupancy
	 *            the holder of data for location and time
	 */
	public void shareSetClickListener(View shareView, final FreeRoomHomeView homeView, final FRRoomOccupancy mOccupancy) {

		if (!mOccupancy.isIsOccupiedAtLeastOnce() && mOccupancy.isIsFreeAtLeastOnce()) {
			shareView.setClickable(true);
			shareView.setEnabled(true);
			if (shareView instanceof ImageView) {
				((ImageView) shareView).setImageResource(R.drawable.freeroom_ic_action_share_enabled);
			}
			shareView.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View v) {
					homeView.shareDisplayDialog(mOccupancy.getTreatedPeriod(), mOccupancy.getRoom());
				}
			});
		} else {
			shareView.setClickable(false);
			shareView.setEnabled(false);
			shareView.setOnClickListener(null);

			if (shareView instanceof ImageView) {
				((ImageView) shareView).setImageResource(R.drawable.freeroom_ic_action_share_disabled);
			}
		}
	}

	/**
	 * {@link #share}: Sends directly a sharing with the server, without going
	 * thru the dialog and/or asking for a message or a confirmation. It shares
	 * only the time and location.
	 * 
	 * @param mPeriod
	 *            the room (location) to share
	 * @param mRoom
	 *            the period (of time) to share
	 */
	public void shareDirectWithServer(FRPeriod mPeriod, FRRoom mRoom) {
		shareDefinetely(mPeriod, mRoom, false, "");
	}

	/**
	 * {@link #share}: share a given location and time, optionally message, with
	 * the server. If
	 * 
	 * @param mPeriod
	 *            period to share
	 * @param mRoom
	 *            room to share
	 * @param withFriends
	 *            true if friends should be notified
	 * @param toShare
	 *            activity to share (might be null or empty if not shared)
	 */
	private void shareDefinetely(FRPeriod mPeriod, FRRoom mRoom, boolean withFriends, String toShare) {
		FRWorkingOccupancy work = new FRWorkingOccupancy(mPeriod, mRoom);
		CheckBox mShareDialogCheckBoxShareMessageServer = (CheckBox) share
				.findViewById(R.id.freeroom_layout_dialog_share_checkbox_server);
		if (mShareDialogCheckBoxShareMessageServer != null && mShareDialogCheckBoxShareMessageServer.isChecked()
				&& toShare != null && toShare != "") {
			work.setMessage(toShare);
		}
		FRImWorkingRequest request = new FRImWorkingRequest(work, mModel.getAnonymID());
		mController.prepareImWorking(request);
		mModel.setOnlyServer(!withFriends);
		mController.ImWorking(this);
		if (withFriends) {
			shareWithFriends(mPeriod, mRoom, toShare);
		}
	}

	/**
	 * {@link #share}: Construct the Intent to share the location and time with
	 * friends. The same information is shared with the server at the same time
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
		startActivity(Intent.createChooser(sendIntent, getString(R.string.freeroom_share_intent_title)));
	}

	/**
	 * {@link #share}: display the {@link #share} with the given time and
	 * location.
	 * 
	 * @param mPeriod
	 *            time
	 * @param mRoom
	 *            location
	 */
	public void shareDisplayDialog(final FRPeriod mPeriod, final FRRoom mRoom) {

		share.hide();
		share.show();

		shareDialogTextViewSummarySharing.setText(u.wantToShare(mPeriod, mRoom, "..."));

		shareDialogEditTextMessageWorking.setOnEditorActionListener(new OnEditorActionListener() {

			@Override
			public boolean onEditorAction(TextView arg0, int arg1, KeyEvent arg2) {
				shareDialogEditTextMessageWorkingUpdated(mPeriod, mRoom);
				return true;
			}
		});

		Button shareWithServer = share.getButton(DialogInterface.BUTTON_NEUTRAL);
		shareWithServer.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				shareDefinetely(mPeriod, mRoom, false, shareDialogEditTextMessageWorking.getText().toString());
				share.dismiss();
			}
		});

		Button shareWithFriends = share.getButton(DialogInterface.BUTTON_POSITIVE);
		shareWithFriends.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				shareDefinetely(mPeriod, mRoom, true, shareDialogEditTextMessageWorking.getText().toString());
				share.dismiss();
			}
		});

		final Spinner spinner = (Spinner) shareView.findViewById(R.id.freeroom_layout_dialog_share_spinner_course);
		// Create an ArrayAdapter using the string array and a default spinner
		// layout : this is NOT displayed for now!
		// other's activity are only displayed in the given popup, not there.
		ArrayList<String> suggest = new ArrayList<String>();
		// 1st result is the "title"
		suggest.add(getString(R.string.freeroom_share_others_activity));

		ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, suggest);
		// Specify the layout to use when the list of choices appears
		adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		// Apply the favoritesListAdapter to the spinner
		spinner.setAdapter(adapter);

		spinner.setOnItemSelectedListener(new OnItemSelectedListener() {

			@Override
			public void onItemSelected(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
				// item 0 is the title
				if (arg2 != 0) {
					shareDialogEditTextMessageWorking.setText(arg0.getItemAtPosition(arg2).toString());
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
		share.show();
	}

	// ******* SETTINGS/PARAMETERS *****///
	/**
	 * {@link #settings}: Dialog that holds the {@link #settings} Dialog.
	 */
	private AlertDialog settings;
	/**
	 * {@link #settings}: View that holds the {@link #settings} dialog content,
	 * defined in xml in layout folder.
	 */
	private View settingsView;

	/**
	 * Inits the {@link #settings} to change settings.
	 */
	private void initSettingsDialog() {
		// Instantiate an AlertDialog.Builder with its constructor
		AlertDialog.Builder builder = new AlertDialog.Builder(this);
		builder.setTitle(getString(R.string.freeroom_settings_title));
		builder.setIcon(R.drawable.freeroom_ic_action_settings);

		// Get the AlertDialog from create()
		settings = builder.create();

		// redefine paramaters to dim screen when displayed
		WindowManager.LayoutParams lp = settings.getWindow().getAttributes();
		lp.dimAmount = 0.60f;
		// these doesn't work
		lp.width = LayoutParams.FILL_PARENT;
		lp.height = LayoutParams.WRAP_CONTENT;
		settings.getWindow().addFlags(WindowManager.LayoutParams.FLAG_WATCH_OUTSIDE_TOUCH);
		settings.getWindow().addFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND);
		settings.getWindow().setAttributes(lp);

		settingsView = commonLayoutInflater.inflate(R.layout.freeroom_layout_dialog_param, null);

		// these work perfectly
		settingsView.setMinimumWidth((int) (activityWidth * 0.95f));

		settings.setView(settingsView);

		// fill with the real value from model!
		initSettingsDialogDataFromModel();
		// when dismissing, make a new search with (new) default value
		settings.setOnDismissListener(new OnDismissListener() {

			@Override
			public void onDismiss(DialogInterface dialog) {
				homeInitDefaultRequest(false);
				commonReplayRefresh();
			}
		});

		settings.setOnShowListener(new OnShowListener() {
			@Override
			public void onShow(DialogInterface dialog) {
				trackEvent("Settings", null);
			}
		});
	}

	/**
	 * {@link #settings}: display the correct data according to model saved
	 * settings.
	 */
	private void initSettingsDialogDataFromModel() {
		settingsRefreshTimeFormatExample();
		settingsRefreshColorBlindExamples();

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
		rd = (RadioButton) settingsView.findViewById(id);
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
		rd = (RadioButton) settingsView.findViewById(id);
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
		rd = (RadioButton) settingsView.findViewById(id);
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

		CheckBox advancedCheckBox = (CheckBox) settingsView
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
		rd = (RadioButton) settingsView.findViewById(id);
		rd.setChecked(true);
	}

	// EXAMPLES OF SETTINGS

	/**
	 * {@link #settings}: Updates the colors example for the accurate
	 * {@link ColorBlindMode}.
	 */
	private void settingsRefreshColorBlindExamples() {
		onColorBlindModeChangedUpdateBasicClick();

		FRPeriod period = new FRPeriod(System.currentTimeMillis(), System.currentTimeMillis() + FRTimes.ONE_HOUR_IN_MS);
		FRRoom room = new FRRoom("mock", "1234");
		List<FRPeriodOccupation> occupancy = new ArrayList<FRPeriodOccupation>(1);
		List<FRRoomOccupancy> occupancies = new ArrayList<FRRoomOccupancy>(4);
		FRRoomOccupancy free = new FRRoomOccupancy(room, occupancy, false, true, period);
		FRRoomOccupancy part = new FRRoomOccupancy(room, occupancy, true, true, period);
		FRRoomOccupancy occupied = new FRRoomOccupancy(room, occupancy, true, false, period);
		FRRoomOccupancy error = new FRRoomOccupancy(room, occupancy, false, false, period);
		occupancies.add(free);
		occupancies.add(part);
		occupancies.add(occupied);
		occupancies.add(error);
		List<TextView> textViews = new ArrayList<TextView>(4);
		mModel.getColoredDotDrawable(free);
		TextView free_text = (TextView) settingsView
				.findViewById(R.id.freeroom_layout_dialog_param_advanced_color_ex_free);
		TextView part_text = (TextView) settingsView
				.findViewById(R.id.freeroom_layout_dialog_param_advanced_color_ex_part);
		TextView occupied_text = (TextView) settingsView
				.findViewById(R.id.freeroom_layout_dialog_param_advanced_color_ex_occ);
		TextView error_text = (TextView) settingsView
				.findViewById(R.id.freeroom_layout_dialog_param_advanced_color_ex_err);
		textViews.add(free_text);
		textViews.add(part_text);
		textViews.add(occupied_text);
		textViews.add(error_text);
		for (int i = 0; i < 4; i++) {
			TextView tv = textViews.get(i);
			FRRoomOccupancy occ = occupancies.get(i);
			tv.setBackgroundColor(mModel.getColorLine(occ));
			tv.setCompoundDrawablesWithIntrinsicBounds(mModel.getColoredDotDrawable(occ), 0, 0, 0);
		}
	}

	/**
	 * {@link #settings}: Refreshes the example text of formatting times.
	 * <p>
	 * Used in param dialog to show the impact of a particular formatting.
	 */
	private void settingsRefreshTimeFormatExample() {
		times = mModel.getFRTimesClient(this);
		TextView tv = (TextView) settingsView.findViewById(R.id.freeroom_layout_dialog_param_time_language_example);
		long now = System.currentTimeMillis() + FRTimes.ONE_WEEK_IN_MS;
		Calendar selected = Calendar.getInstance();
		selected.setTimeInMillis(now);
		tv.setText(times.formatFullDate(selected));
	}

	// LISTENERS CALLED IN XML ON CHECKBOX/RADIOBUTTON CHANGE

	/**
	 * {@link #settings}: Listener to change some model parameter/settings.
	 * 
	 * @param v
	 *            caller view (not used)
	 */
	public void onHomeBehaviourRoomSetFavorites(View v) {
		mModel.setHomeBehaviourRoom(HomeBehaviourRoom.FAVORITES);
	}

	/**
	 * {@link #settings}: Listener to change some model parameter/settings.
	 * 
	 * @param v
	 *            caller view (not used)
	 */
	public void onHomeBehaviourRoomSetFavoritesFree(View v) {
		mModel.setHomeBehaviourRoom(HomeBehaviourRoom.FAVORITES_ONLY_FREE);
	}

	/**
	 * {@link #settings}: Listener to change some model parameter/settings.
	 * 
	 * @param v
	 *            caller view (not used)
	 */
	public void onHomeBehaviourRoomSetAnyFreeRoom(View v) {
		mModel.setHomeBehaviourRoom(HomeBehaviourRoom.ANYFREEROOM);
	}

	/**
	 * {@link #settings}: Listener to change some model parameter/settings.
	 * 
	 * @param v
	 *            caller view (not used)
	 */
	public void onHomeBehaviourRoomSetLastRequest(View v) {
		mModel.setHomeBehaviourRoom(HomeBehaviourRoom.LASTREQUEST);
	}

	/**
	 * {@link #settings}: Listener to change some model parameter/settings.
	 * 
	 * @param v
	 *            caller view (not used)
	 */
	public void onHomeBehaviourTimeSetCurrent(View v) {
		mModel.setHomeBehaviourTime(HomeBehaviourTime.CURRENT_TIME);
	}

	/**
	 * {@link #settings}: Listener to change some model parameter/settings.
	 * 
	 * @param v
	 *            caller view (not used)
	 */
	public void onHomeBehaviourTimeSetEndOfDay(View v) {
		mModel.setHomeBehaviourTime(HomeBehaviourTime.UP_TO_END_OF_DAY);
	}

	/**
	 * {@link #settings}: Listener to change some model parameter/settings.
	 * 
	 * @param v
	 *            caller view (not used)
	 */
	public void onHomeBehaviourTimeSetWholeDay(View v) {
		mModel.setHomeBehaviourTime(HomeBehaviourTime.WHOLE_DAY);
	}

	/**
	 * {@link #settings}: Updates the basic selection according to the change of
	 * the advanced selected {@link ColorBlindMode}.
	 */
	private void onColorBlindModeChangedUpdateBasicClick() {
		CheckBox basicCheckBox = (CheckBox) settingsView
				.findViewById(R.id.freeroom_layout_dialog_param_colorblind_basic);
		ColorBlindMode current = mModel.getColorBlindMode();
		if (current.equals(ColorBlindMode.DOTS_DISCOLORED)
				|| current.equals(ColorBlindMode.DOTS_SYMBOL_LINEFULL_DISCOLORED)) {
			basicCheckBox.setChecked(true);
		} else {
			basicCheckBox.setChecked(false);
		}
	}

	/**
	 * {@link #settings}: Listener to change some model parameter/settings.
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
		((RadioButton) settingsView.findViewById(id)).performClick();
		settingsRefreshColorBlindExamples();
	}

	/**
	 * {@link #settings}: Listener to change some model parameter/settings.
	 * 
	 * @param v
	 *            the checkbox to choose advanced mode.
	 */
	public void onColorBlindAdvancedChecked(View v) {
		CheckBox advancedCheckBox = (CheckBox) v;
		CheckBox basicCheckBox = (CheckBox) settingsView
				.findViewById(R.id.freeroom_layout_dialog_param_colorblind_basic);
		LinearLayout advancedColor = (LinearLayout) settingsView
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
		settingsRefreshColorBlindExamples();
	}

	/**
	 * {@link #settings}: Listener to change some model parameter/settings.
	 * 
	 * @param v
	 *            caller view (not used)
	 */
	public void onColorBlindModeSetDefault(View v) {
		mModel.setColorBlindMode(ColorBlindMode.DEFAULT);
		settingsRefreshColorBlindExamples();
	}

	/**
	 * {@link #settings}: Listener to change some model parameter/settings.
	 * 
	 * @param v
	 *            caller view (not used)
	 */
	public void onColorBlindModeSetDotsDiscolored(View v) {
		mModel.setColorBlindMode(ColorBlindMode.DOTS_DISCOLORED);
		settingsRefreshColorBlindExamples();
	}

	/**
	 * {@link #settings}: Listener to change some model parameter/settings.
	 * 
	 * @param v
	 *            caller view (not used)
	 */
	public void onColorBlindModeSetSymbolic(View v) {
		mModel.setColorBlindMode(ColorBlindMode.DOTS_SYMBOL);
		settingsRefreshColorBlindExamples();
	}

	/**
	 * {@link #settings}: Listener to change some model parameter/settings.
	 * 
	 * @param v
	 *            caller view (not used)
	 */
	public void onColorBlindModeSetSymbolicLines(View v) {
		mModel.setColorBlindMode(ColorBlindMode.DOTS_SYMBOL_LINEFULL);
		settingsRefreshColorBlindExamples();
	}

	/**
	 * {@link #settings}: Listener to change some model parameter/settings.
	 * 
	 * @param v
	 *            caller view (not used)
	 */
	public void onColorBlindModeSetSymbolicLinesDiscolored(View v) {
		mModel.setColorBlindMode(ColorBlindMode.DOTS_SYMBOL_LINEFULL_DISCOLORED);
		settingsRefreshColorBlindExamples();
	}

	/**
	 * {@link #settings}: Listener to change some model parameter/settings.
	 * 
	 * @param v
	 *            caller view (not used)
	 */
	public void onFormattingSetDefault(View v) {
		mModel.setTimeLanguage(TimeLanguage.DEFAULT);
		settingsRefreshTimeFormatExample();
	}

	/**
	 * {@link #settings}: Listener to change some model parameter/settings.
	 * 
	 * @param v
	 *            caller view (not used)
	 */
	public void onFormattingSetEnglish(View v) {
		mModel.setTimeLanguage(TimeLanguage.ENGLISH);
		settingsRefreshTimeFormatExample();
	}

	/**
	 * {@link #settings}: Listener to change some model parameter/settings.
	 * 
	 * @param v
	 *            caller view (not used)
	 */
	public void onFormattingSetFrench(View v) {
		mModel.setTimeLanguage(TimeLanguage.FRENCH);
		settingsRefreshTimeFormatExample();
	}

	// WELCOME DIALOG FOR BETA
	/**
	 * {@link #welcome}: Dialog that holds the {@link #welcome} Dialog.
	 * <p>
	 */
	private AlertDialog welcome;
	/**
	 * - * {@link #welcome}: View that holds the {@link #welcome} dialog
	 * content, - * defined in xml in layout folder.
	 * <p>
	 */
	private View welcomeView;

	/**
	 * {@link #welcome}: Inits the {@link #welcome} dialog.
	 * <p>
	 * TODO: beta-only
	 */
	private void initWelcomeDialog() {
		// Instantiate an AlertDialog.Builder with its constructor
		AlertDialog.Builder builder = new AlertDialog.Builder(this);
		builder.setTitle(getString(R.string.freeroom_welcome_title));
		builder.setIcon(R.drawable.sdk_info);
		builder.setNeutralButton(getString(R.string.freeroom_welcome_dismiss), null);

		// Get the AlertDialog from create()
		welcome = builder.create();

		// redefine paramaters to dim screen when displayed
		WindowManager.LayoutParams lp = welcome.getWindow().getAttributes();
		lp.dimAmount = 0.60f;
		// these doesn't work
		lp.width = LayoutParams.FILL_PARENT;
		lp.height = LayoutParams.WRAP_CONTENT;
		welcome.getWindow().addFlags(WindowManager.LayoutParams.FLAG_WATCH_OUTSIDE_TOUCH);
		welcome.getWindow().addFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND);
		welcome.getWindow().setAttributes(lp);

		welcomeView = commonLayoutInflater.inflate(R.layout.freeroom_layout_dialog_welcome, null);

		// these work perfectly
		welcomeView.setMinimumWidth((int) (activityWidth * 0.9f));
		welcomeView.setMinimumHeight((int) (homeActivityHeight * 0.8f));

		welcome.setView(welcomeView);

		welcome.setOnShowListener(new OnShowListener() {
			@Override
			public void onShow(DialogInterface dialog) {
				trackEvent("Welcome", null);
			}
		});
	}

	// DEVELOPMENT/TEST FUNCTIONS (do not remove for production, it's hidden and
	// might break other things)

	/**
	 * DEVTEST:
	 * <p>
	 * Prefixes for all debug hidden functions. <br>
	 * DO NOT REMOVE FOR PRODUCTION (it will break major things) <br>
	 * The codes are not easy to guesse, and the konami code must be activated
	 * first to enter the text!
	 */
	private final static String devTestPrefix = "debug@jwvm:";
	private final static String devTestChangeDateOn = "changedate=on";
	private final static String devTestChangeDateOff = "changedate=off";
	private final static String devTestChangeGroupOn = "changegrp=on";
	private final static String devTestChangeGroupOff = "changegrp=off";

	/**
	 * DEVTEST: Activates hidden functionnalities for debug puposes.
	 */
	private void devTestActivateDebug(String query) {
		if (query.matches("[Jj][Ww]") || query.matches("[Vv][Mm]")) {
			errorDialogShowMessage("Julien Weber and Valentin Minder\n"
					+ "are those who made freeroom accesible to anyone!\nThanks! :D");
		}
		if (query.matches("[Dd][Aa][Tt][Ee]")) {
			mModel.setAdvancedTime(!mModel.getAdvancedTime());
			errorDialogShowMessage("Change date switched");
		}

		if (query.matches("[Aa][Rr][Rr][Oo][Ww]")) {
			TimePickersPref tpf = mModel.getTimePickersPref();
			if (tpf.equals(TimePickersPref.ARROWS)) {
				mModel.setTimePickersPref(TimePickersPref.BOTH);
			} else if (tpf.equals(TimePickersPref.BOTH)) {
				mModel.setTimePickersPref(TimePickersPref.PICKERS);
			} else {
				mModel.setTimePickersPref(TimePickersPref.ARROWS);
			}
			errorDialogShowMessage("Time selection with pickers/arrows set to: " + mModel.getTimePickersPref().name()
					+ "\nType \"arrow\" again to switch");
		}

		String timeLang = "Date/Time language format set to ";
		String resetTimeLang = "\nType \"english\" or \"french\" to change";
		if (query.matches("[Ee][Nn][Gg][Ll][Ii][Ss][Hh]")) {
			TimeLanguage tl = mModel.getTimeLanguage();
			if (tl.equals(TimeLanguage.ENGLISH)) {
				mModel.setTimeLanguage(TimeLanguage.DEFAULT);
			} else {
				mModel.setTimeLanguage(TimeLanguage.ENGLISH);
			}
			errorDialogShowMessage(timeLang + mModel.getTimeLanguage().name() + resetTimeLang);
		}
		if (query.matches("[Ff][Rr][Ee][Nn][Cc][Hh]")) {
			TimeLanguage tl = mModel.getTimeLanguage();
			if (tl.equals(TimeLanguage.FRENCH)) {
				mModel.setTimeLanguage(TimeLanguage.DEFAULT);
			} else {
				mModel.setTimeLanguage(TimeLanguage.FRENCH);
			}
			errorDialogShowMessage(timeLang + mModel.getTimeLanguage().name() + resetTimeLang);
		}
		if (query.matches("[Rr][Ee][Ss][Ee][Tt][Aa][Ll][Ll]")) {
			mModel.resetAll(false);
			errorDialogShowMessage("All settings have been reset, including UID.");
		}
		if (query.matches("[Rr][Ee][Ss][Ee][Tt]")) {
			mModel.resetAll(true);
			errorDialogShowMessage("All settings have been reset!");
		}
		// initSearchDialog();
		if (query.matches("[Dd][Ee][Bb][Uu][Gg]") && !query.startsWith(devTestPrefix)) {
			boolean advanced = mModel.getAdvancedTime();
			mModel.setAdvancedTime(!advanced);
			// initSearchDialog();
			if (advanced) {
				mModel.setGroupAccess();
				errorDialogShowMessage("Debug mode deactivated!");
			} else {
				mModel.setGroupAccess(Integer.MAX_VALUE);
				errorDialogShowMessage("Debug mode activated! Try with great care! :p");
			}
		}
		if (!query.startsWith(devTestPrefix)) {
			return;
		} else if (query.equalsIgnoreCase(devTestPrefix + devTestChangeDateOn)) {
			mModel.setAdvancedTime(true);
			errorDialogShowMessage("Change date activated");
			// initSearchDialog();
		} else if (query.equalsIgnoreCase(devTestPrefix + devTestChangeDateOff)) {
			mModel.setAdvancedTime(false);
			errorDialogShowMessage("Change date disabled");
			// initSearchDialog();
		} else if (query.equalsIgnoreCase(devTestPrefix + devTestChangeGroupOn)) {
			mModel.setGroupAccess(Integer.MAX_VALUE);
			errorDialogShowMessage("Change group access activated");
		} else if (query.equalsIgnoreCase(devTestPrefix + devTestChangeGroupOff)) {
			mModel.setGroupAccess();
			errorDialogShowMessage("Change group access disabled");
		}
	}

	// SERVICE

	@Override
	protected String screenName() {
		return "freeroom";
	}

	@Override
	public void autoCompleteLaunch() {
		// TODO Auto-generated method stub

	}

	@Override
	public void autoCompleteUpdated() {
		// TODO Auto-generated method stub

	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		switch (requestCode) {
		case SEARCH_ACTIVITY_REQUEST:
			if (resultCode == RESULT_OK) {
				commonReplayRefresh();
				return;
			}
			break;

		default:
			break;
		}
		// TODO Auto-generated method stub
		super.onActivityResult(requestCode, resultCode, data);
	}

}