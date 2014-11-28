package org.pocketcampus.plugin.freeroom.android;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
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
import org.pocketcampus.platform.android.ui.layout.StandardTitledLayout;
import org.pocketcampus.plugin.freeroom.R;
import org.pocketcampus.plugin.freeroom.android.FreeRoomModel.ColorBlindMode;
import org.pocketcampus.plugin.freeroom.android.FreeRoomModel.HomeBehaviourRoom;
import org.pocketcampus.plugin.freeroom.android.FreeRoomModel.HomeBehaviourTime;
import org.pocketcampus.plugin.freeroom.android.FreeRoomModel.TimeLanguage;
import org.pocketcampus.plugin.freeroom.android.FreeRoomModel.TimePickersPref;
import org.pocketcampus.plugin.freeroom.android.adapter.ActualOccupationArrayAdapter;
import org.pocketcampus.plugin.freeroom.android.adapter.ExpandableListViewFavoriteAdapter;
import org.pocketcampus.plugin.freeroom.android.adapter.ExpandableListViewOccupancyAdapter;
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
import org.pocketcampus.plugin.freeroom.shared.utils.FRStruct;
import org.pocketcampus.plugin.freeroom.shared.utils.FRTimes;

import android.app.Activity;
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
import android.os.Bundle;
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
public class FreeRoomHomeView extends FreeRoomAbstractView implements
		IFreeRoomView {

	/* MVC STRUCTURE */
	/**
	 * COMMON: {@link FreeRoomController} is the controller in MVC scheme.
	 */
	private FreeRoomController mController;
	/**
	 * COMMON: {@link FreeRoomModel} is the model in MVC scheme.
	 */
	private FreeRoomModel mModel;

	/**
	 * COMMON: Reference to times utility method for client-side.
	 */
	private FRTimesClient times;
	/**
	 * COMMON: Reference to other utility method for client-side.
	 */
	private FRUtilsClient u;

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
	 * HOME: Titled layout that holds the title and the main layout.
	 */
	private StandardTitledLayout homeTitleLayout;
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
	protected void onDisplay(Bundle savedInstanceState,
			PluginController controller) {

		// Get and cast the controller and model
		mController = (FreeRoomController) controller;
		mModel = (FreeRoomModel) controller.getModel();
		times = mModel.getFRTimesClient(this);
		u = new FRUtilsClient(this);

		// Setup the layout
		commonLayoutInflater = this.getLayoutInflater();
		homeTitleLayout = new StandardTitledLayout(this);
		homeMainContentLayout = (LinearLayout) commonLayoutInflater.inflate(
				R.layout.freeroom_layout_home, null);
		// The ActionBar is added automatically when you call setContentView
		setContentView(homeTitleLayout);
		homeSetTitleDefault();

		homeResultExpListView = (ExpandableListView) homeMainContentLayout
				.findViewById(R.id.freeroom_layout_home_list);
		homeStatusTextView = (TextView) homeMainContentLayout
				.findViewById(R.id.freeroom_layout_home_text_summary);
		homeSetStatusTextSummary(getString(R.string.freeroom_home_init_please_wait));
		initializeView();

		// add the main layout to the pocketcampus titled layout.
		homeTitleLayout.addFillerView(homeMainContentLayout);
		
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
	 * Overrides {@link Activity#onTouchEvent(MotionEvent)}.
	 * <p>
	 * The {@link MotionEvent} are checked for {@link KonamiCodeMove} Code
	 * gesture, and then replayed to the superclass.
	 * 
	 * @param event
	 *            {@link MotionEvent} as specified in overridden method
	 */
	public boolean onTouchEvent(MotionEvent event) {
		konamiCodeCheck(event);
		return super.onTouchEvent(event);
	}

	/**
	 * Overrides {@link Activity#onKeyDown(int, KeyEvent)} in order to override
	 * some hardware button implementation. If the event is not used or has no
	 * particular use, it's replayed to the superclass.
	 * 
	 * @param keyCode
	 *            keycode as specified by overridden method
	 * @param event
	 *            {@link KeyEvent} as specified by overridden method
	 * @return boolean value as specified by overridden method
	 */
	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		// overrides search button for devices who are equipped with such
		// hardware button, and launch automatically the search popup.
		if (keyCode == KeyEvent.KEYCODE_SEARCH) {
			search.show();
			return true;
		}

		// overrides clear button for devices who are equipped with such
		// hardware button, and clear all the search introduced (reset)
		if (keyCode == KeyEvent.KEYCODE_CLEAR) {
			searchResetMain();
			return true;
		}

		// overrides enter button for devices who are equipped with such
		// hardware button, and launch a search if valid.
		if (keyCode == KeyEvent.KEYCODE_ENTER) {
			if (search.isShowing() && !addSearchRoom.isShowing()
					&& !editSearchRoom.isShowing()
					&& (searchAuditSubmit() == 0)) {
				searchLaunchPrepareSearchQuery(true);
				return true;
			}
		}

		// overrides envelope button for devices who are equipped with such
		// hardware button, and share the location if the detailled info popup
		// is displayed and available for the whole period.
		if (keyCode == KeyEvent.KEYCODE_ENVELOPE) {
			if (infoDetailsRoom.isShowing()) {
				FRRoomOccupancy mOccupancy = mModel.getDisplayedOccupancy();
				if (mOccupancy != null && mOccupancy.isIsFreeAtLeastOnce()
						&& !mOccupancy.isIsOccupiedAtLeastOnce()) {
					Button shareButton = infoDetailsRoom
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
			// it's implemented now to display the same as the actionOverflow
			// button,
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
		autoCompleteUpdateMessage(errorMessage);
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

		konamiCodeConstructListMove();
		// retrieve display dimensions
		Rect displayRectangle = new Rect();
		Window window = this.getWindow();
		window.getDecorView().getWindowVisibleDisplayFrame(displayRectangle);
		activityWidth = displayRectangle.width();
		homeActivityHeight = displayRectangle.height();

		homeResultExpListAdapter = new ExpandableListViewOccupancyAdapter<FRRoomOccupancy>(
				getApplicationContext(), mModel.getOccupancyResults(),
				mController, this);
		homeResultExpListView.setAdapter(homeResultExpListAdapter);
		homeResultExpListView
				.setOnGroupExpandListener(new OnGroupExpandListener() {

					@Override
					public void onGroupExpand(int groupPosition) {
						// when we expand a group, it gets the focus
						// (highlighted)
						homeResultExpListAdapter.setGroupFocus(groupPosition);
					}
				});
		homeResultExpListView
				.setOnGroupCollapseListener(new OnGroupCollapseListener() {

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

		// actionSearch action is always there, on phones AND tablet modes
		addActionToActionBar(actionSearch);
		// on tablet, put all the actions, without the actionOverflow.
		if (commonIsLandscapeTabletMode()) {
			addActionToActionBar(actionFavoritesEdit);
			addActionToActionBar(actionSettings);
			addActionToActionBar(actionRefresh);
			addActionToActionBar(actionAbout);
		} else {
			// on phones, put all the other actions in the action
			// actionOverflow.
			addActionToActionBar(actionOverflow);
		}

		// init all other popup needed!
		initInfoDetailsRoomDialog();
		initSearchDialog();
		initFavoritesDialog();
		initAddFavoritesDialog();
		initAddSearchRoomDialog();
		initEditSearchRoomDialog();
		initShareDialog();
		initWarningDialog();
		initErrorDialog();
		initSettingsDialog();
		initWhoIsWorkingDialog();
		initWelcomeDialog();

		if (mModel.getRegisteredUserNeedUpdate()) {
			welcome.show();
			mModel.setRegisteredUserAuto();
		}
	}

	/* ACTIONS FOR THE ACTION BAR */

	/**
	 * ACTION/MENU: Action to open the actionOverflow actions.
	 * <p>
	 * ALL the actions are in actionOverflow, even if already visible.
	 */
	private Action actionOverflow = new Action() {
		public void performAction(View view) {
			// open the legacy options menu: deprecated on new phones.
			// openOptionsMenu() ;
			// show the compat popup menu.
			showPopupMenuCompat(view);
		}

		public int getDrawable() {
			return R.drawable.freeroom_ic_action_overflow;
		}
	};

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
	};
	/**
	 * ACTION/MENU: Action to perform a customized search, by showing the
	 * {@link #search} dialog.
	 */
	private Action actionSearch = new Action() {
		public void performAction(View view) {
			search.show();
		}

		public int getDrawable() {
			return R.drawable.freeroom_ic_action_search;
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
			favoritesListAdapter.notifyDataSetChanged();
			favorites.show();
		}

		public int getDrawable() {
			return R.drawable.freeroom_ic_action_important;
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
			return R.drawable.freeroom_ic_action_refresh;
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
			return R.drawable.freeroom_ic_action_about;
		}
	};

	/**
	 * /* MENUS
	 */

	/**
	 * ACTION/MENU: Overrides {@link Activity#onCreateOptionsMenu(Menu)} and
	 * adds a relevant menu only conditionally, when the landscape mode is not
	 * triggered.
	 * <p>
	 * See also: {@link #commonIsLandscapeTabletMode()}.
	 */
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// on portrait devices, we have both actionOverflow and menu with same
		// options.
		// on landscape devices, non is available.
		if (!commonIsLandscapeTabletMode()) {
			// Inflate the menu items, the same as in actionOverflow action bar
			MenuInflater inflater = getMenuInflater();
			inflater.inflate(R.menu.freeroom_main_activity_actions, menu);
		}
		return super.onCreateOptionsMenu(menu);
	}

	/**
	 * ACTION/MENU:
	 * 
	 * @param v
	 */
	public void showPopupMenuCompat(View v) {
		PopupMenuCompat menu = PopupMenuCompat.newInstance(this, v);
		menu.inflate(R.menu.freeroom_main_activity_actions);
		menu.setOnMenuItemClickListener(new PopupMenuCompat.OnMenuItemClickListener() {
			@Override
			public boolean onMenuItemClick(MenuItem item) {
				return onOptionsItemSelected(item);
			}
		});

		menu.show();
	}

	/**
	 * ACTION/MENU: Overrides {@link Activity#onOptionsItemSelected(MenuItem)}
	 * and does the correct action when a menu item is clicked.
	 */
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle presses on the action bar items
		switch (item.getItemId()) {
		case R.id.freeroom_action_favorites:
			favorites.show();
			return true;
		case R.id.freeroom_action_refresh:
			// actionRefresh if no timeout, otherwise new default request.
			homeMainStartDefault();
			return true;
		case R.id.freeroom_action_settings:
			settings.show();
			return true;
		case R.id.freeroom_action_about:
			welcome.show();
			return true;
		default:
			return super.onOptionsItemSelected(item);
		}
	}

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
			if (intent.getAction().equalsIgnoreCase(
					"android.intent.action.MAIN")) {
				u.logV("starting MAIN and default mode");
				homeMainStartDefault();
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
								errorIntentHandled = searchByIntentUriPrepareArguments(uid);
							} else if ("/search".equals(intentUriPath)
									&& intentUri.getQueryParameter("name") != null) {
								// the completion is added THERE (%) because the
								// autocomplete method is set to "exactmatch"
								errorIntentHandled = searchByIntentUriPrepareArguments(intentUri
										.getQueryParameter("name") + "%");
							} else if ("/match".equals(intentUriPath)
									&& intentUri.getQueryParameter("name") != null) {
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
				u.logE("ERROR: Found an unhandled action: \""
						+ intent.getAction() + "\"");
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
		errorDialogShowMessage(getString(R.string.freeroom_urisearch_error_basis)
				+ "\n"
				+ errorMessage
				+ "\n"
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
			return getString(R.string.freeroom_urisearch_error_AutoComplete_error)
					+ " "
					+ getString(R.string.freeroom_urisearch_error_AutoComplete_precond);
		} else {
			searchByIntentUriTriggered = true;
			// if the URI is triggered, we want to give access to the room,
			// event if the user might no have right to see the room.
			FRAutoCompleteRequest req = new FRAutoCompleteRequest(constraint,
					Math.max(mModel.getGroupAccess(), Integer.MAX_VALUE));
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
			searchByIntentUriOnError(getString(R.string.freeroom_urisearch_error_AutoComplete_error)
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
			searchPreviousRequestAdapter.notifyDataSetChanged();
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
			Log.e("defaultmainstart",
					"Controller or Model not defined: cannot start default mode.");
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
		searchPreviousRequestAdapter.notifyDataSetChanged();
	}

	/**
	 * HOME: Construct a valid and default request. If useFavorites is true, it
	 * will check all the favorites for the next valid period, otherwise or if
	 * there are not.
	 * 
	 * @param forceUseFavorites
	 *            if it should consider the favorites or not
	 * @return a valid and default request, based or nor on the favorites.
	 */
	private FRRequestDetails homeValidRequest(boolean forceUseFavorites) {
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

				addAllFavoriteToCollection(array, true);

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
	 * COMMON: Minimal width in pixel to trigger landscape mode according to
	 * {@link #commonIsLandscapeTabletMode()}.
	 */
	private int commonMinWidthForLandscapeMode = 480;

	/**
	 * COMMON: Check if the height is smaller than the width of the displayed
	 * screen.
	 * <p>
	 * As the plugin is NOT sensible to landscape mode, this will ONLY occur on
	 * tablets.
	 * <p>
	 * Please note some phones are also wider than higher, even if their are
	 * very small. To avoid the "landscape tablet mode" to be triggered, we
	 * check than the witdh is wider than the
	 * {@link #commonMinWidthForLandscapeMode} constant in pixels, which is not
	 * really perfect, but not an issue as these phones are low-pixels densities
	 * and most recent tablets are medium or high-density.
	 * 
	 * @return true if landscape mode should be activated.
	 */
	private boolean commonIsLandscapeTabletMode() {
		return (homeActivityHeight < activityWidth)
				&& (activityWidth > commonMinWidthForLandscapeMode);
	}

	/**
	 * COMMON: Dismiss the keyboard associated with the view.
	 * 
	 * @param v
	 *            the view to which the keyboard is attached to, as specified by
	 *            {@link InputMethodManager#hideSoftInputFromWindow(android.os.IBinder, int)}
	 */
	private void commonDismissSoftKeyBoard(View v) {
		InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
		imm.hideSoftInputFromWindow(v.getWindowToken(), 0);
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
		homeTitleLayout.setTitle(getString(R.string.freeroom_title_main_title));
	}

	/**
	 * HOME: Sets the title to the default error value (FreeRoom: error :/)
	 * <p>
	 * Used by transmission/network error, internal server error, bad request.
	 */
	private void homeSetTitleError() {
		homeTitleLayout
				.setTitle(getString(R.string.freeroom_title_main_title_error));
	}

	/**
	 * HOME: Sets the title to the default no result value (FreeRoom: no result
	 * :/)
	 * <p>
	 * Used when a successfully answer was received, but empty! (eg when no
	 * selected room is free!)
	 */
	private void homeSetTitleNoResults() {
		homeTitleLayout
				.setTitle(getString(R.string.freeroom_title_main_title_no_result));
	}

	/**
	 * HOME: Sets the title to the default udpating value (FreeRoom:
	 * updating...)
	 */
	private void homeSetTitleUpdating() {
		homeTitleLayout
				.setTitle(getString(R.string.freeroom_title_main_title_updating));
	}

	/**
	 * HOME: Sets the title to the given value.
	 * 
	 * @param titleValue
	 *            the new title
	 */
	private void homeSetTitle(String titleValue) {
		homeTitleLayout.setTitle(titleValue);
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
		if (mModel.getOccupancyResults().isEmpty()) {
			// popup with no results message
			subtitle = getString(R.string.freeroom_home_error_no_results);
			errorDialogShowMessage(subtitle);
			homeSetTitleNoResults();
		} else {
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
				List<?> list = mModel.getOccupancyResults().get(
						mModel.getBuildingKeyLabel(room));
				Iterator<?> iter = list.iterator();
				label: while (iter.hasNext()) {
					Object o = iter.next();
					if (o instanceof FRRoomOccupancy) {
						if (((FRRoomOccupancy) o).getRoom().getUid()
								.equals(room.getUid())) {
							mModel.setDisplayedOccupancy((FRRoomOccupancy) o);
							// doesn't work
							infoDetailsActualOccupationAdapter
									.notifyDataSetChanged();
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
	private void homeUpdateCollapse(ExpandableListView ev,
			ExpandableListViewOccupancyAdapter<FRRoomOccupancy> ad) {
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

	// AUTOCOMPLETE - shared for add room and add favorites

	/**
	 * MVC METHOD/AUTOCOMPLETE: Override
	 * {@link IFreeRoomView#autoCompleteUpdated()} and notifies the autocomplete
	 * have been updated. Results in {@link #addFavorites} AND
	 * {@link #addSearchRoom} are updated, as they share teh SAME autocomplete
	 * (they cannot be displayed at the same time).
	 */
	@Override
	public void autoCompleteUpdated() {
		addSearchRoomSuggestionAdapter.notifyDataSetInvalidated();
		mAddFavoritesAdapter.notifyDataSetInvalidated();
		addSearchRoomAutoCompleteArrayListFRRoom.clear();
		addFavoritesAutoCompleteArrayListFRRoom.clear();
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
				if (!addSearchRoomSelectedRooms.contains(room)) {
					addSearchRoomAutoCompleteArrayListFRRoom.add(room);
				}
				addFavoritesAutoCompleteArrayListFRRoom.add(room);
			}
		}

		/*
		 * If there was a non-empty result but all rooms got rejected, we
		 * display "no more" instead of "up-to-date". Not useful for favorites
		 * as no room is rejected.
		 */
		if (!emptyResult) {
			if (addSearchRoomAutoCompleteArrayListFRRoom.isEmpty()) {
				addSearchRoomAutoCompleteStatusTextView
						.setText(getString(R.string.freeroom_dialog_add_autocomplete_nomore));
			}
			if (addFavoritesAutoCompleteArrayListFRRoom.isEmpty()) {
				addFavoritesAutoCompleteStatus
						.setText(getString(R.string.freeroom_dialog_add_autocomplete_nomore));
			}
		}

		if (searchByIntentUriTriggered) {
			searchByIntentUriMakeRequest(addSearchRoomAutoCompleteArrayListFRRoom);
		}

		addSearchRoomSuggestionAdapter.notifyDataSetChanged();
		mAddFavoritesAdapter.notifyDataSetChanged();
	}

	/**
	 * AUTOCOMPLETE: checks if a query is valid, and if it is, it will try
	 * remove the soft keyboard.
	 * 
	 * @param query
	 *            the query to check
	 * @param view
	 *            the view from which the soft keyboard should be hidden.
	 */
	private void autoCompleteValidateQuery(String query, View view) {
		if (u.validQuery(query)) {
			commonDismissSoftKeyBoard(view);
			FRAutoCompleteRequest request = new FRAutoCompleteRequest(query,
					mModel.getGroupAccess());
			mController.autoCompleteBuilding(this, request);
		} else {
			autoCompleteCancel();
		}
		// activation of hidden settings (not wanted visible in production, but
		// kept for compatibility reason. DO NOT DELETE)
		if (query.matches("[Dd][Aa][Yy]")) {
			mModel.setAdvancedTime(!mModel.getAdvancedTime());
			errorDialogShowMessage("Advanced time activated:"
					+ mModel.getAdvancedTime());
		}
		devTestActivateDebug(query);
	}

	/**
	 * MVC METHOD/AUTOCOMPLETE: Override
	 * {@link IFreeRoomView#autoCompleteLaunch()} and notify an autocomplete
	 * request have been launched, and that the user should way until it's
	 * completed.
	 */
	@Override
	public void autoCompleteLaunch() {
		addFavoritesAutoCompleteStatus
				.setText(getString(R.string.freeroom_dialog_add_autocomplete_updating));
		addSearchRoomAutoCompleteStatusTextView
				.setText(getString(R.string.freeroom_dialog_add_autocomplete_updating));
	}

	/**
	 * AUTOCOMPLETE: To be called when autocomplete is not lauchable and ask the
	 * user to type in.
	 */
	public void autoCompleteCancel() {
		addFavoritesAutoCompleteArrayListFRRoom.clear();
		mAddFavoritesAdapter.notifyDataSetInvalidated();
		addSearchRoomAutoCompleteArrayListFRRoom.clear();
		addSearchRoomSuggestionAdapter.notifyDataSetInvalidated();

		addFavoritesAutoCompleteStatus
				.setText(getString(R.string.freeroom_dialog_add_autocomplete_typein));
		addSearchRoomAutoCompleteStatusTextView
				.setText(getString(R.string.freeroom_dialog_add_autocomplete_typein));
	}

	/**
	 * AUTOCOMPLETE: Update the text message in autocomplete status text view
	 * (updating/up-to-date/error/...)
	 * 
	 * @param text
	 *            the new message to display.
	 */
	private void autoCompleteUpdateMessage(CharSequence text) {
		addFavoritesAutoCompleteStatus.setText(text);
		addSearchRoomAutoCompleteStatusTextView.setText(text);
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
		builder.setPositiveButton(
				getString(R.string.freeroom_dialog_info_share), null);
		builder.setNegativeButton(
				getString(R.string.freeroom_dialog_fav_close), null);

		// Get the AlertDialog from create()
		infoDetailsRoom = builder.create();

		// redefine paramaters to dim screen when displayed
		WindowManager.LayoutParams lp = infoDetailsRoom.getWindow()
				.getAttributes();
		lp.dimAmount = 0.60f;
		// these doesn't work
		lp.width = LayoutParams.FILL_PARENT;
		lp.height = LayoutParams.WRAP_CONTENT;
		infoDetailsRoom.getWindow().addFlags(
				WindowManager.LayoutParams.FLAG_WATCH_OUTSIDE_TOUCH);
		infoDetailsRoom.getWindow().addFlags(
				WindowManager.LayoutParams.FLAG_DIM_BEHIND);
		infoDetailsRoom.getWindow().setAttributes(lp);

		infoDetailsRoomView = commonLayoutInflater.inflate(
				R.layout.freeroom_layout_dialog_info, null);

		ListView lv = (ListView) infoDetailsRoomView
				.findViewById(R.id.freeroom_layout_dialog_info_roomOccupancy);

		ViewGroup header = (ViewGroup) commonLayoutInflater.inflate(
				R.layout.freeroom_layout_dialog_info_header, lv, false);
		lv.addHeaderView(header, null, false);
		ViewGroup footer = (ViewGroup) commonLayoutInflater.inflate(
				R.layout.freeroom_layout_dialog_info_footer, lv, false);
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
			ImageView peopleImageView = (ImageView) infoDetailsRoomView
					.findViewById(R.id.freeroom_layout_dialog_info_people);
			peopleImageView.setImageResource(mModel
					.getImageFromRatioOccupation(mOccupancy
							.getRatioWorstCaseProbableOccupancy()));
			peopleImageView.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View arg0) {
					shareDirectWithServer(mOccupancy.getTreatedPeriod(), mRoom);
				}
			});

			ImageView map = (ImageView) infoDetailsRoomView
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
						errorDialogShowMessage(getString(R.string.freeroom_error_map_plugin_missing));
					}
				}
			});

			ImageView shareImageView = (ImageView) infoDetailsRoomView
					.findViewById(R.id.freeroom_layout_dialog_info_share);
			shareSetClickListener(shareImageView, this, mOccupancy);

			Button shareButton = infoDetailsRoom
					.getButton(AlertDialog.BUTTON_POSITIVE);
			shareButton.setEnabled(mOccupancy.isIsFreeAtLeastOnce()
					&& !mOccupancy.isIsOccupiedAtLeastOnce());
			shareSetClickListener(shareButton, this, mOccupancy);

			ListView roomOccupancyListView = (ListView) infoDetailsRoomView
					.findViewById(R.id.freeroom_layout_dialog_info_roomOccupancy);
			infoDetailsActualOccupationAdapter = new ActualOccupationArrayAdapter<FRPeriodOccupation>(
					getApplicationContext(), mOccupancy, mController, this);
			roomOccupancyListView
					.setAdapter(infoDetailsActualOccupationAdapter);

			TextView detailsTextView = (TextView) infoDetailsRoomView
					.findViewById(R.id.freeroom_layout_dialog_info_details);

			// UID and surface are display only to user who have special rights
			// (time and room access), and therefore also in debug mode.
			boolean value = mModel.getAdvancedTime()
					&& (mModel.getGroupAccess() > mModel.DEFAULT_GROUP_ACCESS);
			detailsTextView.setText(u.getInfoFRRoom(mOccupancy.getRoom(),
					value, value));
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
		WindowManager.LayoutParams lp = whoIsWorking.getWindow()
				.getAttributes();
		lp.dimAmount = 0.60f;
		// these doesn't work
		lp.width = LayoutParams.FILL_PARENT;
		lp.height = LayoutParams.WRAP_CONTENT;
		whoIsWorking.getWindow().addFlags(
				WindowManager.LayoutParams.FLAG_WATCH_OUTSIDE_TOUCH);
		whoIsWorking.getWindow().addFlags(
				WindowManager.LayoutParams.FLAG_DIM_BEHIND);
		whoIsWorking.getWindow().setAttributes(lp);

		whoIsWorkingView = commonLayoutInflater.inflate(
				R.layout.freeroom_layout_dialog_working, null);

		// these work perfectly
		// whoIsWorkingView.setMinimumWidth((int) (activityWidth * 0.9f));
		// whoIsWorkingView.setMinimumHeight((int) (homeActivityHeight * 0.8f));

		whoIsWorking.setView(whoIsWorkingView);

		whoIsWorking.setOnShowListener(new OnShowListener() {
			@Override
			public void onShow(DialogInterface dialog) {
				whoIsWorkingDisclaimer
						.setText(R.string.freeroom_whoIsWorking_wait);
				trackEvent("WorkingThere", null);
			}
		});

		ListView lv = (ListView) whoIsWorkingView
				.findViewById(R.id.freeroom_layout_dialog_working_time_list);

		ViewGroup header = (ViewGroup) commonLayoutInflater.inflate(
				R.layout.freeroom_layout_dialog_working_header, lv, false);
		lv.addHeaderView(header, null, false);
		ViewGroup footer = (ViewGroup) commonLayoutInflater.inflate(
				R.layout.freeroom_layout_dialog_working_footer, lv, false);
		lv.addFooterView(footer, null, false);

		whoIsWorkingTimeSummary = (TextView) whoIsWorkingView
				.findViewById(R.id.freeroom_layout_dialog_working_time);
		whoIsWorkingDisclaimer = (TextView) whoIsWorkingView
				.findViewById(R.id.freeroom_layout_dialog_working_disclaimer);

		whoIsWorkingMessageList = mModel.getListMessageFrequency();
		whoIsWorkingMessageAdapter = new MessageFrequencyArrayAdapter<FRMessageFrequency>(
				this, getApplicationContext(),
				R.layout.freeroom_layout_message,
				R.id.freeroom_layout_message_text, whoIsWorkingMessageList);
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
		whoIsWorkingTimeSummary.setText(times
				.formatFullDateFullTimePeriod(period));
	}

	/**
	 * MVC METHOD ({@link #whoIsWorking}): Override
	 * {@link IFreeRoomView#workingMessageUpdated()} and notifies the user
	 * message have been updated. Refresh the {@link #whoIsWorking} accordingly
	 * (if it's shown).
	 */
	@Override
	public void workingMessageUpdated() {
		whoIsWorkingDisclaimer
				.setText(getString(R.string.freeroom_whoIsWorking_disclaimer));
		whoIsWorkingMessageAdapter.notifyDataSetChanged();
	}

	// FAVORITES

	/**
	 * {@link #favorites}: AlertDialog that holds the {@link #favorites} dialog.
	 */
	private AlertDialog favorites;
	/**
	 * {@link #favorites}: View that holds the {@link #favorites} dialog
	 * content, defined in xml in layout folder.
	 */
	private View favoritesView;
	/**
	 * {@link #favorites}: adapter for the favorites ListView.
	 */
	private ExpandableListViewFavoriteAdapter<FRRoom> favoritesListAdapter;

	/**
	 * {@link #favorites}: Inits the {@link #favorites} dialog to display the
	 * favorites.
	 */
	private void initFavoritesDialog() {
		// Instantiate an AlertDialog.Builder with its constructor
		AlertDialog.Builder builder = new AlertDialog.Builder(this);
		builder.setTitle(getString(R.string.freeroom_dialog_fav_title));
		builder.setIcon(R.drawable.freeroom_ic_action_important);
		builder.setPositiveButton(getString(R.string.freeroom_dialog_fav_add),
				null);
		builder.setNegativeButton(
				getString(R.string.freeroom_dialog_fav_close), null);
		builder.setNeutralButton(getString(R.string.freeroom_dialog_fav_reset),
				null);

		// Get the AlertDialog from create()
		favorites = builder.create();

		// redefine paramaters to dim screen when displayed
		WindowManager.LayoutParams lp = favorites.getWindow().getAttributes();
		lp.dimAmount = 0.60f;
		// these doesn't work
		lp.width = LayoutParams.FILL_PARENT;
		lp.height = LayoutParams.WRAP_CONTENT;
		favorites.getWindow().addFlags(
				WindowManager.LayoutParams.FLAG_WATCH_OUTSIDE_TOUCH);
		favorites.getWindow().addFlags(
				WindowManager.LayoutParams.FLAG_DIM_BEHIND);
		favorites.getWindow().setAttributes(lp);

		favoritesView = commonLayoutInflater.inflate(
				R.layout.freeroom_layout_dialog_fav, null);

		// these work perfectly
		favoritesView.setMinimumWidth((int) (activityWidth * 0.9f));
		favoritesView.setMinimumHeight((int) (homeActivityHeight * 0.8f));

		favorites.setView(favoritesView);

		favorites.setOnShowListener(new OnShowListener() {
			@Override
			public void onShow(DialogInterface dialog) {
				favoritesListAdapter.notifyDataSetChanged();
				favoritesUpdateSummary();
				trackEvent("Favorites", null);
			}
		});

		favorites.hide();
		favorites.show();
		favorites.dismiss();

		favorites.setOnDismissListener(new AlertDialog.OnDismissListener() {

			@Override
			public void onDismiss(DialogInterface dialog) {
				// sends a new request with the new favorites
				homeInitDefaultRequest(true);
				commonReplayRefresh();
			}
		});

		Button tv = favorites.getButton(DialogInterface.BUTTON_POSITIVE);
		tv.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				addFavorites.show();
			}
		});

		Button bt = favorites.getButton(DialogInterface.BUTTON_NEUTRAL);
		bt.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				warning.show();
			}
		});

		ExpandableListView lv = (ExpandableListView) favoritesView
				.findViewById(R.id.freeroom_layout_dialog_fav_list);
		favoritesListAdapter = new ExpandableListViewFavoriteAdapter<FRRoom>(
				this, mModel.getFavorites(), mModel, this);
		lv.setAdapter(favoritesListAdapter);
		favoritesListAdapter.notifyDataSetChanged();
	}

	/**
	 * {@link #favorites}: Updates the favorites summary after something has
	 * changed.
	 * <p>
	 * Display the number of favorites, or a small message if no favorites.
	 */
	public void favoritesUpdateSummary() {
		TextView favoritesSummaryTextView = (TextView) favoritesView
				.findViewById(R.id.freeroom_layout_dialog_fav_status);
		int count = favoritesListAdapter.getGroupCount();
		String text = "";
		if (count == 0) {
			text = getString(R.string.freeroom_dialog_fav_status_no);
		} else {
			text = getString(R.string.freeroom_dialog_fav_status_fav);
			int total = 0;
			for (int i = 0; i < count; i++) {
				total += favoritesListAdapter.getChildrenCount(i);
			}
			text += getResources().getQuantityString(
					R.plurals.freeroom_results_room_header, total, total);
		}
		favoritesSummaryTextView.setText(text);
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
		builder.setPositiveButton(
				getString(R.string.freeroom_dialog_share_button_friends), null);
		builder.setNegativeButton(getString(R.string.freeroom_search_cancel),
				null);
		builder.setNeutralButton(
				getString(R.string.freeroom_dialog_share_button_server), null);
		builder.setIcon(R.drawable.freeroom_ic_action_share);

		// Get the AlertDialog from create()
		share = builder.create();

		// redefine paramaters to dim screen when displayed
		WindowManager.LayoutParams lp = share.getWindow().getAttributes();
		lp.dimAmount = 0.60f;
		// these doesn't work
		lp.width = LayoutParams.FILL_PARENT;
		lp.height = LayoutParams.FILL_PARENT;
		share.getWindow().addFlags(
				WindowManager.LayoutParams.FLAG_WATCH_OUTSIDE_TOUCH);
		share.getWindow().addFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND);
		share.getWindow().setAttributes(lp);

		shareView = commonLayoutInflater.inflate(
				R.layout.freeroom_layout_dialog_share, null);
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
	private void shareDialogEditTextMessageWorkingUpdated(
			final FRPeriod mPeriod, final FRRoom mRoom) {
		String text = shareDialogEditTextMessageWorking.getText().toString();
		if (text == null || text.length() == 0) {
			text = "...";
		}
		shareDialogTextViewSummarySharing.setText(u.wantToShare(mPeriod, mRoom,
				text));
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
	public void shareSetClickListener(View shareView,
			final FreeRoomHomeView homeView, final FRRoomOccupancy mOccupancy) {

		if (!mOccupancy.isIsOccupiedAtLeastOnce()
				&& mOccupancy.isIsFreeAtLeastOnce()) {
			shareView.setClickable(true);
			shareView.setEnabled(true);
			if (shareView instanceof ImageView) {
				((ImageView) shareView)
						.setImageResource(R.drawable.freeroom_ic_action_share_enabled);
			}
			shareView.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View v) {
					homeView.shareDisplayDialog(mOccupancy.getTreatedPeriod(),
							mOccupancy.getRoom());
				}
			});
		} else {
			shareView.setClickable(false);
			shareView.setEnabled(false);
			shareView.setOnClickListener(null);

			if (shareView instanceof ImageView) {
				((ImageView) shareView)
						.setImageResource(R.drawable.freeroom_ic_action_share_disabled);
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
	private void shareDefinetely(FRPeriod mPeriod, FRRoom mRoom,
			boolean withFriends, String toShare) {
		FRWorkingOccupancy work = new FRWorkingOccupancy(mPeriod, mRoom);
		CheckBox mShareDialogCheckBoxShareMessageServer = (CheckBox) share
				.findViewById(R.id.freeroom_layout_dialog_share_checkbox_server);
		if (mShareDialogCheckBoxShareMessageServer != null
				&& mShareDialogCheckBoxShareMessageServer.isChecked()
				&& toShare != null && toShare != "") {
			work.setMessage(toShare);
		}
		FRImWorkingRequest request = new FRImWorkingRequest(work,
				mModel.getAnonymID());
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
		startActivity(Intent.createChooser(sendIntent,
				getString(R.string.freeroom_share_intent_title)));
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

		shareDialogTextViewSummarySharing.setText(u.wantToShare(mPeriod, mRoom,
				"..."));

		shareDialogEditTextMessageWorking
				.setOnEditorActionListener(new OnEditorActionListener() {

					@Override
					public boolean onEditorAction(TextView arg0, int arg1,
							KeyEvent arg2) {
						shareDialogEditTextMessageWorkingUpdated(mPeriod, mRoom);
						return true;
					}
				});

		Button shareWithServer = share
				.getButton(DialogInterface.BUTTON_NEUTRAL);
		shareWithServer.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				shareDefinetely(mPeriod, mRoom, false,
						shareDialogEditTextMessageWorking.getText().toString());
				share.dismiss();
			}
		});

		Button shareWithFriends = share
				.getButton(DialogInterface.BUTTON_POSITIVE);
		shareWithFriends.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				shareDefinetely(mPeriod, mRoom, true,
						shareDialogEditTextMessageWorking.getText().toString());
				share.dismiss();
			}
		});

		final Spinner spinner = (Spinner) shareView
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
		// Apply the favoritesListAdapter to the spinner
		spinner.setAdapter(adapter);

		spinner.setOnItemSelectedListener(new OnItemSelectedListener() {

			@Override
			public void onItemSelected(AdapterView<?> arg0, View arg1,
					int arg2, long arg3) {
				// item 0 is the title
				if (arg2 != 0) {
					shareDialogEditTextMessageWorking.setText(arg0
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
		share.show();
	}

	// WARNING

	/**
	 * {@link #warning}: Dialog that holds the {@link #warning} Dialog (with two
	 * button: confirm/cancel).
	 */
	private AlertDialog warning;

	/**
	 * Inits the {@link #warning}.
	 * <p>
	 * CAUTION: so far, it has a SINGLE function: erase favorites !!!
	 */
	private void initWarningDialog() {
		AlertDialog.Builder builder = new AlertDialog.Builder(this);
		builder.setTitle(getString(R.string.freeroom_dialog_warn_title));
		builder.setMessage(getString(R.string.freeroom_dialog_warn_delete_fav_text));
		builder.setIcon(R.drawable.freeroom_ic_action_warning);
		builder.setPositiveButton(
				getString(R.string.freeroom_dialog_warn_confirm),
				new AlertDialog.OnClickListener() {

					@Override
					public void onClick(DialogInterface dialog, int which) {
						mModel.resetFavorites();
						favoritesListAdapter.notifyDataSetChanged();
					}
				});
		builder.setNegativeButton(
				getString(R.string.freeroom_dialog_warn_cancel), null);

		// Get the AlertDialog from create()
		warning = builder.create();

		// redefine paramaters to dim screen when displayed
		WindowManager.LayoutParams lp = warning.getWindow().getAttributes();
		lp.dimAmount = 0.60f;
		// these doesn't work
		lp.width = LayoutParams.WRAP_CONTENT;
		lp.height = LayoutParams.WRAP_CONTENT;
		warning.getWindow().addFlags(
				WindowManager.LayoutParams.FLAG_WATCH_OUTSIDE_TOUCH);
		warning.getWindow()
				.addFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND);
		warning.getWindow().setAttributes(lp);

		warning.setOnDismissListener(new OnDismissListener() {

			@Override
			public void onDismiss(DialogInterface dialog) {
				favoritesUpdateSummary();
				favoritesListAdapter.notifyDataSetChanged();
				trackEvent("Warning", null);
			}
		});
	}

	// ERROR

	/**
	 * {@link #error}: Dialog that holds the {@link #error} (with one button:
	 * dismiss)
	 */
	private AlertDialog error;

	/**
	 * {@link #error}: Inits the {@link #error} to show a single error message
	 * with only a close button.
	 */
	private void initErrorDialog() {
		AlertDialog.Builder builder = new AlertDialog.Builder(this);
		builder.setTitle(getString(R.string.freeroom_dialog_error_title));
		builder.setIcon(R.drawable.freeroom_ic_action_error);
		builder.setNeutralButton(R.string.freeroom_dialog_error_dismiss, null);

		// Get the AlertDialog from create()
		error = builder.create();

		// redefine paramaters to dim screen when displayed
		WindowManager.LayoutParams lp = error.getWindow().getAttributes();
		lp.dimAmount = 0.60f;
		// these doesn't work
		lp.width = LayoutParams.WRAP_CONTENT;
		lp.height = LayoutParams.WRAP_CONTENT;
		error.getWindow().addFlags(
				WindowManager.LayoutParams.FLAG_WATCH_OUTSIDE_TOUCH);
		error.getWindow().addFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND);
		error.getWindow().setAttributes(lp);

		// reset the message when dismiss
		// (to avoid showing with previous message!)
		error.setOnDismissListener(new OnDismissListener() {

			@Override
			public void onDismiss(DialogInterface dialog) {
				error.setMessage("");
			}
		});

		error.setOnShowListener(new OnShowListener() {
			@Override
			public void onShow(DialogInterface dialog) {
				trackEvent("Error", null);
			}
		});
	}

	/**
	 * {@link #error}: Show the {@link #error} with the given message.
	 * 
	 * @param text
	 *            error message to display
	 */
	private void errorDialogShowMessage(String text) {
		// error dialog may be null at init time!
		if (error != null) {
			error.setMessage(text);
			error.show();
		}
	}

	/* ADD FAVORITES */

	/**
	 * {@link #addFavorites}: AlertDialog that holds the {@link #addFavorites}
	 * dialog.
	 */
	private AlertDialog addFavorites;
	/**
	 * {@link #addFavorites}: View that holds the {@link #addFavorites} dialog
	 * content, defined in xml in layout folder.
	 */
	private View addFavoritesView;
	/**
	 * {@link #addFavorites}: TextView for autocomplete status for adding
	 * favorites.
	 */
	private TextView addFavoritesAutoCompleteStatus;

	/**
	 * {@link #addFavorites}: listview to display autocomplete results.
	 */
	private ListView addFavoritesAutoCompleteListView;
	/**
	 * {@link #addFavorites}: list of suggestion room by autocomplete.
	 */
	private List<FRRoom> addFavoritesAutoCompleteArrayListFRRoom;

	/**
	 * {@link #addFavorites}:The input bar to make the search
	 */
	private InputBarElement addFavoritesAutoCompleteInputBarElement;
	/**
	 * {@link #addFavorites}:Adapter for the <code>mListView</code>
	 */
	private FRRoomSuggestionArrayAdapter<FRRoom> mAddFavoritesAdapter;

	/**
	 * {@link #addFavorites}: inits the {@link #addFavorites} to add new
	 * favorites (and alos remove them!)
	 */
	private void initAddFavoritesDialog() {
		// Instantiate an AlertDialog.Builder with its constructor
		AlertDialog.Builder builder = new AlertDialog.Builder(this);
		builder.setTitle(getString(R.string.freeroom_dialog_add_favorites_title));
		builder.setIcon(R.drawable.freeroom_ic_action_new);

		// Get the AlertDialog from create()
		addFavorites = builder.create();

		// redefine paramaters to dim screen when displayed
		WindowManager.LayoutParams lp = addFavorites.getWindow()
				.getAttributes();
		lp.dimAmount = 0.60f;
		// these doesn't work
		lp.width = LayoutParams.FILL_PARENT;
		lp.height = LayoutParams.WRAP_CONTENT;
		addFavorites.getWindow().addFlags(
				WindowManager.LayoutParams.FLAG_WATCH_OUTSIDE_TOUCH);
		addFavorites.getWindow().addFlags(
				WindowManager.LayoutParams.FLAG_DIM_BEHIND);
		addFavorites.getWindow().setAttributes(lp);

		addFavoritesView = commonLayoutInflater.inflate(
				R.layout.freeroom_layout_dialog_add_favorites_room, null);
		// these work perfectly
		addFavoritesView.setMinimumWidth((int) (activityWidth * 0.9f));

		addFavorites.setView(addFavoritesView);

		addFavoritesAutoCompleteStatus = (TextView) addFavoritesView
				.findViewById(R.id.freeroom_layout_dialog_add_room_status);

		addFavorites.setOnShowListener(new OnShowListener() {
			@Override
			public void onShow(DialogInterface dialog) {
				trackEvent("AddFavorite", null);
			}
		});

		addFavorites.setOnDismissListener(new OnDismissListener() {

			@Override
			public void onDismiss(DialogInterface dialog) {
				favoritesListAdapter.notifyDataSetChanged();

				// WARNING: BUG FIX
				// when the favorites are modified (removed) and the parent
				// group is opened, this cause a NullPointerException on the
				// main favorites window.
				ExpandableListView lv = (ExpandableListView) favoritesView
						.findViewById(R.id.freeroom_layout_dialog_fav_list);
				for (int i = favoritesListAdapter.getGroupCount() - 1; i >= 0; i--) {
					lv.collapseGroup(i);
				}
				favoritesUpdateSummary();
				autoCompleteCancel();
				addSearchRoomAutoCompleteInputBarElement.setInputText("");
				addFavoritesAutoCompleteInputBarElement.setInputText("");
			}
		});

		addFavoritesAutoCompleteArrayListFRRoom = new ArrayList<FRRoom>(50);

		addFavoritesUIConstructInputBar();
		LinearLayout ll = (LinearLayout) addFavoritesView
				.findViewById(R.id.freeroom_layout_dialog_add_favorites_layout_main);
		ll.addView(addFavoritesAutoCompleteInputBarElement);
		addFavoritesUICreateSuggestionsList();
	}

	/**
	 * {@link #addFavorites}:
	 */
	private void addFavoritesUIConstructInputBar() {
		final IFreeRoomView view = this;

		addFavoritesAutoCompleteInputBarElement = new InputBarElement(
				this,
				null,
				getString(R.string.freeroom_check_occupancy_search_inputbarhint));
		addFavoritesAutoCompleteInputBarElement
				.setImeOptions(EditorInfo.IME_ACTION_SEARCH);

		// click on magnify glass on the keyboard
		addFavoritesAutoCompleteInputBarElement
				.setOnEditorActionListener(new OnEditorActionListener() {
					@Override
					public boolean onEditorAction(TextView v, int actionId,
							KeyEvent event) {
						if (actionId == EditorInfo.IME_ACTION_SEARCH) {
							String query = addFavoritesAutoCompleteInputBarElement
									.getInputText();
							autoCompleteValidateQuery(query, v);
						}

						return true;
					}
				});

		// click on BUTTON magnify glass on the inputbar
		addFavoritesAutoCompleteInputBarElement
				.setOnButtonClickListener(new OnClickListener() {
					@Override
					public void onClick(View v) {
						String query = addFavoritesAutoCompleteInputBarElement
								.getInputText();
						autoCompleteValidateQuery(query, v);
					}
				});

		mAddFavoritesAdapter = new FRRoomSuggestionArrayAdapter<FRRoom>(
				getApplicationContext(),
				R.layout.freeroom_layout_list_room_add_fav,
				R.id.freeroom_layout_list_room_add_fav,
				addFavoritesAutoCompleteArrayListFRRoom, mModel, true);

		addFavoritesAutoCompleteInputBarElement
				.setOnKeyPressedListener(new OnKeyPressedListener() {
					@Override
					public void onKeyPressed(String text) {
						addFavoritesAutoCompleteListView
								.setAdapter(mAddFavoritesAdapter);

						if (!u.validQuery(text)) {
							addFavoritesAutoCompleteInputBarElement
									.setButtonText(null);
							autoCompleteCancel();
						} else {
							addFavoritesAutoCompleteInputBarElement
									.setButtonText("");
							// remove this if you don't want
							// automatic autocomplete
							// without pressing the button
							FRAutoCompleteRequest request = new FRAutoCompleteRequest(
									text, mModel.getGroupAccess());
							request.setUserLanguage(mModel.getUserLanguage());
							mController.autoCompleteBuilding(view, request);
						}
					}
				});
	}

	/**
	 * {@link #addFavorites}: Initialize the autocomplete suggestion list
	 */
	private void addFavoritesUICreateSuggestionsList() {
		addFavoritesAutoCompleteListView = new ListView(this);
		addFavoritesAutoCompleteInputBarElement
				.addView(addFavoritesAutoCompleteListView);

		addFavoritesAutoCompleteListView
				.setOnItemClickListener(new OnItemClickListener() {
					@Override
					public void onItemClick(AdapterView<?> adapter, View view,
							int pos, long id) {
						// when an item is clicked, the keyboard is dimissed
						commonDismissSoftKeyBoard(view);
						FRRoom room = addFavoritesAutoCompleteArrayListFRRoom
								.get(pos);
						if (mModel.isFavorite(room)) {
							mModel.removeFavorite(room);
						} else {
							mModel.addFavorite(room);
						}

						// WE DONT REMOVE the text in the input bar
						// INTENTIONNALLY: user may want to select multiple
						// rooms in the same building
						autoCompleteUpdated();
					}
				});
		addFavoritesAutoCompleteListView.setAdapter(mAddFavoritesAdapter);
	}

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
	 * {@link #addSearchRoom}: the listview of autocomplete suggestion
	 */
	private ListView addSearchRoomAutoCompleteListView;
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
		WindowManager.LayoutParams lp = addSearchRoom.getWindow()
				.getAttributes();
		lp.dimAmount = 0.60f;
		// these doesn't work
		lp.width = LayoutParams.FILL_PARENT;
		lp.height = LayoutParams.WRAP_CONTENT;
		addSearchRoom.getWindow().addFlags(
				WindowManager.LayoutParams.FLAG_WATCH_OUTSIDE_TOUCH);
		addSearchRoom.getWindow().addFlags(
				WindowManager.LayoutParams.FLAG_DIM_BEHIND);
		addSearchRoom.getWindow().setAttributes(lp);

		addSearchRoomView = commonLayoutInflater.inflate(
				R.layout.freeroom_layout_dialog_add_room, null);
		// these work perfectly
		addSearchRoomView.setMinimumWidth((int) (activityWidth * 0.9f));

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
				addFavoritesAutoCompleteInputBarElement.setInputText("");

				commonDismissSoftKeyBoard(addSearchRoomView);
				editSearchRoom.dismiss();
				addSearchRoom.dismiss();
			}
		});

		Button bt_done = (Button) addSearchRoomView
				.findViewById(R.id.freeroom_layout_dialog_add_room_done);
		bt_done.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				commonDismissSoftKeyBoard(v);
				editSearchRoom.dismiss();
				addSearchRoom.dismiss();
			}
		});

		Button bt_edit = (Button) addSearchRoomView
				.findViewById(R.id.freeroom_layout_dialog_add_room_edit);
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
		LinearLayout ll = (LinearLayout) addSearchRoomView
				.findViewById(R.id.freeroom_layout_dialog_add_layout_main);
		ll.addView(addSearchRoomAutoCompleteInputBarElement);
		addSearchRoomCreateSuggestionsList();
	}

	/**
	 * {@link #addSearchRoom}: construct the UI.
	 */
	private void addSearchRoomUIConstructInputBar() {
		final IFreeRoomView view = this;

		addSearchRoomAutoCompleteInputBarElement = new InputBarElement(
				this,
				null,
				getString(R.string.freeroom_check_occupancy_search_inputbarhint));
		addSearchRoomAutoCompleteInputBarElement
				.setImeOptions(EditorInfo.IME_ACTION_SEARCH);

		// click on magnify glass on the keyboard
		addSearchRoomAutoCompleteInputBarElement
				.setOnEditorActionListener(new OnEditorActionListener() {
					@Override
					public boolean onEditorAction(TextView v, int actionId,
							KeyEvent event) {
						if (actionId == EditorInfo.IME_ACTION_SEARCH) {
							String query = addSearchRoomAutoCompleteInputBarElement
									.getInputText();
							autoCompleteValidateQuery(query, v);
						}

						return true;
					}
				});

		// click on BUTTON magnify glass on the inputbar
		addSearchRoomAutoCompleteInputBarElement
				.setOnButtonClickListener(new OnClickListener() {
					@Override
					public void onClick(View v) {
						String query = addSearchRoomAutoCompleteInputBarElement
								.getInputText();
						autoCompleteValidateQuery(query, v);
					}
				});

		addSearchRoomSuggestionAdapter = new FRRoomSuggestionArrayAdapter<FRRoom>(
				getApplicationContext(),
				R.layout.freeroom_layout_list_room_add_room,
				R.id.freeroom_layout_list_room_add_room,
				addSearchRoomAutoCompleteArrayListFRRoom, mModel, false);

		addSearchRoomAutoCompleteInputBarElement
				.setOnKeyPressedListener(new OnKeyPressedListener() {
					@Override
					public void onKeyPressed(String text) {
						addSearchRoomAutoCompleteListView
								.setAdapter(addSearchRoomSuggestionAdapter);

						cancelAutoCompleteBuildingTask();
						if (!u.validQuery(text)) {
							addSearchRoomAutoCompleteInputBarElement
									.setButtonText(null);
							autoCompleteCancel();
						} else {
							addSearchRoomAutoCompleteInputBarElement
									.setButtonText("");
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
				FRAutoCompleteRequest request = new FRAutoCompleteRequest(text,
						mModel.getGroupAccess());
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
		addSearchRoomAutoCompleteInputBarElement
				.addView(addSearchRoomAutoCompleteListView);

		addSearchRoomAutoCompleteListView
				.setOnItemClickListener(new OnItemClickListener() {
					@Override
					public void onItemClick(AdapterView<?> adapter, View view,
							int pos, long id) {
						// when an item is clicked, the keyboard is dimissed
						commonDismissSoftKeyBoard(view);
						FRRoom room = addSearchRoomAutoCompleteArrayListFRRoom
								.get(pos);
						addSearchRoomAddNewRoomToCheck(room);
						searchLaunchValidateButton
								.setEnabled(searchAuditSubmit() == 0);

						// WE DONT REMOVE the text in the input bar
						// INTENTIONNALLY: user may want to select multiple
						// rooms in the same building

						// actionRefresh the autocomplete, such that selected
						// rooms are not displayed anymore
						autoCompleteUpdated();

					}
				});
		addSearchRoomAutoCompleteListView
				.setAdapter(addSearchRoomSuggestionAdapter);
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
			Log.e(this.getClass().toString(),
					"room cannot be added: already added");
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
		builder.setIcon(R.drawable.freeroom_ic_action_edit_light);

		// Get the AlertDialog from create()
		editSearchRoom = builder.create();

		// redefine paramaters to dim screen when displayed
		WindowManager.LayoutParams lp = editSearchRoom.getWindow()
				.getAttributes();
		lp.dimAmount = 0.60f;
		// these doesn't work
		lp.width = LayoutParams.FILL_PARENT;
		lp.height = LayoutParams.WRAP_CONTENT;
		editSearchRoom.getWindow().addFlags(
				WindowManager.LayoutParams.FLAG_WATCH_OUTSIDE_TOUCH);
		editSearchRoom.getWindow().addFlags(
				WindowManager.LayoutParams.FLAG_DIM_BEHIND);
		editSearchRoom.getWindow().setAttributes(lp);

		editSearchRoomView = commonLayoutInflater.inflate(
				R.layout.freeroom_layout_dialog_edit_room, null);
		// these work perfectly
		editSearchRoomView.setMinimumWidth((int) (activityWidth * 0.9f));
		editSearchRoomView.setMinimumHeight((int) (homeActivityHeight * 0.8f));

		editSearchRoom.setView(editSearchRoomView);

		editSearchRoomSelectedListView = (ListView) editSearchRoomView
				.findViewById(R.id.freeroom_layout_dialog_edit_room_list);
		addSearchRoomSelectedRoomArrayAdapter = new FRRoomRemoveArrayAdapter<FRRoom>(
				this, getApplicationContext(),
				R.layout.freeroom_layout_room_edit,
				R.id.freeroom_layout_selected_text, addSearchRoomSelectedRooms);
		editSearchRoomSelectedListView
				.setAdapter(addSearchRoomSelectedRoomArrayAdapter);
		editSearchRoomSelectedListView
				.setOnItemClickListener(new OnItemClickListener() {

					@Override
					public void onItemClick(AdapterView<?> arg0, View arg1,
							int arg2, long arg3) {
						addSearchRoomSelectedRoomArrayAdapter
								.remove(addSearchRoomSelectedRoomArrayAdapter
										.getItem(arg2));
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

		Button bt_done = (Button) editSearchRoomView
				.findViewById(R.id.freeroom_layout_dialog_edit_room_done);
		bt_done.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				commonDismissSoftKeyBoard(v);
				editSearchRoom.dismiss();
				addSearchRoom.dismiss();
			}
		});

		Button bt_more = (Button) editSearchRoomView
				.findViewById(R.id.freeroom_layout_dialog_edit_room_add);
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
		addSearchRoomSelectedRoomArrayAdapter
				.remove(addSearchRoomSelectedRoomArrayAdapter.getItem(position));
		editSearchRoomSelectedListView.refreshDrawableState();
	}

	// SEARCH !!! //
	/* UI ELEMENTS FOR DIALOGS - SEARCH */

	/**
	 * {@link #search}: Dialog that holds the {@link #search} Dialog.
	 */
	private AlertDialog search;
	/**
	 * {@link #search}: View that holds the {@link #search} dialog content,
	 * defined in xml in layout folder.
	 */
	private View searchView;
	/**
	 * {@link #search}: ListView that holds previous searches.
	 */
	private ListView searchPreviousSearchesListView;
	/**
	 * {@link #search}: TextView to write "previous searches" +show/hide
	 */
	private TextView searchPreviousRequestTitleTextView;
	/**
	 * {@link #search}: Button to come back to up of search page.
	 */
	private Button searchGoUpHomeButton;
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
		builder.setIcon(R.drawable.freeroom_ic_action_search);

		// Get the AlertDialog from create()

		search = builder.create();

		// redefine paramaters to dim screen when displayed
		WindowManager.LayoutParams lp = search.getWindow().getAttributes();
		lp.dimAmount = 0.60f;
		// these doesn't work
		lp.width = LayoutParams.FILL_PARENT;
		lp.height = LayoutParams.FILL_PARENT;
		search.getWindow().addFlags(
				WindowManager.LayoutParams.FLAG_WATCH_OUTSIDE_TOUCH);
		search.getWindow().addFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND);
		search.getWindow().setAttributes(lp);

		searchView = commonLayoutInflater.inflate(
				R.layout.freeroom_layout_dialog_search, null);
		// these work perfectly
		searchView.setMinimumWidth((int) (activityWidth * 0.9f));
		searchView.setMinimumHeight((int) (homeActivityHeight * 0.8f));

		search.setView(searchView);

		search.setOnShowListener(new OnShowListener() {
			@Override
			public void onShow(DialogInterface dialog) {
				searchLaunchValidateButton.setEnabled(searchAuditSubmit() == 0);
				searchPreviousRequestInitTitle();
				searchResetMain();
				trackEvent("Search", null);
			}
		});

		// this is necessary o/w buttons don't exists!
		search.hide();
		search.show();
		search.dismiss();
		searchLaunchResetButton = search
				.getButton(DialogInterface.BUTTON_NEUTRAL);
		searchLaunchValidateButton = search
				.getButton(DialogInterface.BUTTON_POSITIVE);

		// display the previous searches
		searchPreviousSearchesListView = (ListView) searchView
				.findViewById(R.id.freeroom_layout_dialog_search_prev_search_list);
		searchPreviousRequestAdapter = new PreviousRequestArrayAdapter<FRRequestDetails>(
				this, this, R.layout.freeroom_layout_list_prev_req,
				R.id.freeroom_layout_prev_req_text, mModel.getPreviousRequest());

		ViewGroup header = (ViewGroup) commonLayoutInflater.inflate(
				R.layout.freeroom_layout_search_header,
				searchPreviousSearchesListView, false);
		searchPreviousSearchesListView.addHeaderView(header, null, false);
		ViewGroup footer = (ViewGroup) commonLayoutInflater.inflate(
				R.layout.freeroom_layout_search_footer,
				searchPreviousSearchesListView, false);
		searchPreviousSearchesListView.addFooterView(footer, null, false);
		searchPreviousSearchesListView.setAdapter(searchPreviousRequestAdapter);

		searchPreviousRequestTitleString = getString(R.string.freeroom_search_previous_search);
		searchPreviousRequestTitleTextView = (TextView) searchView
				.findViewById(R.id.freeroom_layout_dialog_search_prev_search_title);
		// go home: useful for very long lists to go back up.
		searchGoUpHomeButton = (Button) searchView
				.findViewById(R.id.freeroom_layout_dialog_search_footer_home);
		searchGoUpHomeButton.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				searchPreviousSearchesListView.smoothScrollToPosition(0);
			}
		});

		searchParamSelectedRoomsTextViewSearchMenu = (TextView) search
				.findViewById(R.id.freeroom_layout_dialog_search_text_summary);
		// the view will be removed or the text changed, no worry
		searchParamSelectedRoomsTextViewSearchMenu
				.setText(getString(R.string.freeroom_add_rooms_empty));

		initSearchUIMain();
	}

	/**
	 * {@link #search}: UI init (main).
	 */
	private void initSearchUIMain() {

		searchSelectOptionalLineLinearLayoutWrapperFirst = (LinearLayout) search
				.findViewById(R.id.freeroom_layout_dialog_search_opt_line_wrapper_1st);
		searchSelectOptionalLineLinearLayoutWrapperSecond = (LinearLayout) searchView
				.findViewById(R.id.freeroom_layout_dialog_search_opt_line_wrapper_2nd);

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
		searchTimeDatePicker = (Button) searchView
				.findViewById(R.id.freeroom_layout_dialog_search_date);
		if (!mModel.getAdvancedTime()) {
			searchTimeDatePicker.setVisibility(View.GONE);
		}

		searchTimeDatePickerDialog = new DatePickerDialog(this,
				new DatePickerDialog.OnDateSetListener() {

					@Override
					public void onDateSet(DatePicker view, int nYear,
							int nMonthOfYear, int nDayOfMonth) {
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
		searchTimeStartPicker = (Button) searchView
				.findViewById(R.id.freeroom_layout_dialog_search_hour_start);
		searchTimeStartShortPicker = (Button) searchView
				.findViewById(R.id.freeroom_layout_dialog_search_hour_start_short);
		searchTimePickerStartDialog = new TimePickerDialog(this,
				new OnTimeSetListener() {

					@Override
					public void onTimeSet(TimePicker view, int nHourOfDay,
							int nMinute) {
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
						if (startHourSelected != -1
								&& !searchTimeAdvUpToEndSelected) {
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
		searchTimeEndPicker = (Button) searchView
				.findViewById(R.id.freeroom_layout_dialog_search_hour_end);
		searchTimeEndShortPicker = (Button) searchView
				.findViewById(R.id.freeroom_layout_dialog_search_hour_end_short);
		searchTimePickerEndDialog = new TimePickerDialog(this,
				new OnTimeSetListener() {

					@Override
					public void onTimeSet(TimePicker view, int nHourOfDay,
							int nMinute) {
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
							endMinSelected = startMinSelected
									+ Constants.MIN_MINUTE_INTERVAL;
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
							searchTimeAdvUpToEndHourButton
									.setEnabled(!searchTimeAdvUpToEndSelected);
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
		searchParamSpecificButton = (RadioButton) searchView
				.findViewById(R.id.freeroom_layout_dialog_search_spec);
		searchParamSpecificButton.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				if (searchParamSpecificButton.isChecked()) {
					searchSelectOptionalLineLinearLayoutWrapperFirst
							.setVisibility(View.VISIBLE);

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
					searchSelectOptionalLineLinearLayoutWrapperSecond
							.setVisibility(View.VISIBLE);

					searchParamSelectUserDefButton.setChecked(true);
					addSearchRoom.show();
					// as it's user-defined, we dont check for search
					// button
					// enabled now
					searchLaunchValidateButton.setEnabled(false);
				} else {
					searchParamSelectFavoritesButton.setChecked(true);
					searchLaunchValidateButton
							.setEnabled(searchAuditSubmit() == 0);
				}
			}
		});

		searchParamAnyFreeRoomButton = (RadioButton) searchView
				.findViewById(R.id.freeroom_layout_dialog_search_any);
		searchParamAnyFreeRoomButton.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				if (searchParamAnyFreeRoomButton.isChecked()) {
					searchSelectOptionalLineLinearLayoutWrapperFirst
							.setVisibility(View.GONE);
					searchSelectOptionalLineLinearLayoutWrapperSecond
							.setVisibility(View.GONE);
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

		searchParamSelectFavoritesButton = (CheckBox) searchView
				.findViewById(R.id.freeroom_layout_dialog_search_fav);
		searchParamSelectFavoritesButton.setEnabled(true);
		searchParamSelectFavoritesButton
				.setOnClickListener(new OnClickListener() {

					@Override
					public void onClick(View v) {
						if (!searchParamSelectUserDefButton.isChecked()) {
							searchParamSelectFavoritesButton.setChecked(true);
						}
						searchParamAnyFreeRoomButton.setChecked(false);
						searchParamSpecificButton.setChecked(true);
						searchLaunchValidateButton
								.setEnabled(searchAuditSubmit() == 0);
					}
				});

		searchParamSelectUserDefButton = (CheckBox) searchView
				.findViewById(R.id.freeroom_layout_dialog_search_user);
		searchParamSelectUserDefButton
				.setOnClickListener(new OnClickListener() {

					@Override
					public void onClick(View v) {
						if (searchParamSelectUserDefButton.isChecked()
								|| !searchParamSelectFavoritesButton
										.isChecked()) {
							if (searchParamSelectUserDefButton.isChecked()) {
								searchSelectUserDefReset(false);
							}
							searchParamSelectUserDefButton.setChecked(true);

							searchParamAnyFreeRoomButton.setChecked(false);
							searchParamSpecificButton.setChecked(true);
							searchParamOnlyFreeRoomsButton.setChecked(false);

							searchSelectOptionalLineLinearLayoutWrapperSecond
									.setVisibility(View.VISIBLE);

							searchParamSelectedRoomsTextViewSearchMenu.setText(u
									.getSummaryTextFromCollection(addSearchRoomSelectedRooms));
							addSearchRoom.show();
							searchLaunchValidateButton.setEnabled(false);
						} else {
							searchSelectUserDefReset(false);
							searchSelectOptionalLineLinearLayoutWrapperSecond
									.setVisibility(View.GONE);
							searchLaunchValidateButton
									.setEnabled(searchAuditSubmit() == 0);
						}
					}
				});

		searchParamOnlyFreeRoomsButton = (CheckBox) searchView
				.findViewById(R.id.freeroom_layout_dialog_search_non_free);
		searchParamOnlyFreeRoomsButton.setEnabled(true);
		searchParamOnlyFreeRoomsButton
				.setOnClickListener(new OnClickListener() {
					@Override
					public void onClick(View v) {
						if (!searchParamOnlyFreeRoomsButton.isChecked()) {
							searchParamAnyFreeRoomButton.setChecked(false);
							searchParamSpecificButton.setChecked(true);
						}
						searchLaunchValidateButton
								.setEnabled(searchAuditSubmit() == 0);
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

		searchTimeAdvDownToStartHourButton = (ImageButton) searchView
				.findViewById(R.id.freeroom_layout_dialog_search_hour_start_tostart);
		searchTimeAdvDownToStartHourButton.setEnabled(true);
		searchTimeAdvDownToStartHourButton
				.setOnClickListener(new OnClickListener() {

					@Override
					public void onClick(View v) {
						startMinSelected = 0;
						int shift = startHourSelected
								- Constants.FIRST_HOUR_CHECK;
						startHourSelected = Constants.FIRST_HOUR_CHECK;
						if (!searchTimeAdvUpToEndSelected && shift > 0) {
							endHourSelected -= shift;
						}
						searchTimeUpdateAllPickersAndButtons();
					}
				});

		searchTimeAdvDownStartHourButton = (ImageButton) searchView
				.findViewById(R.id.freeroom_layout_dialog_search_hour_start_minus);
		searchTimeAdvDownStartHourButton.setEnabled(true);
		searchTimeAdvDownStartHourButton
				.setOnClickListener(new OnClickListener() {

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

		searchTimeAdvUpStartHourButton = (ImageButton) searchView
				.findViewById(R.id.freeroom_layout_dialog_search_hour_start_plus);
		searchTimeAdvUpStartHourButton.setEnabled(true);
		searchTimeAdvUpStartHourButton
				.setOnClickListener(new OnClickListener() {

					@Override
					public void onClick(View v) {
						if (startHourSelected <= Constants.LAST_HOUR_CHECK - 2) {
							startHourSelected += 1;
							if (!searchTimeAdvUpToEndSelected) {
								endHourSelected = Math.min(endHourSelected + 1,
										Constants.LAST_HOUR_CHECK);
							}
						}
						searchTimeUpdateAllPickersAndButtons();
					}
				});

		searchTimeAdvDownEndHourButton = (ImageButton) searchView
				.findViewById(R.id.freeroom_layout_dialog_search_hour_end_minus);
		searchTimeAdvDownEndHourButton.setEnabled(true);
		searchTimeAdvDownEndHourButton
				.setOnClickListener(new OnClickListener() {

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

		searchTimeAdvUpEndHourButton = (ImageButton) searchView
				.findViewById(R.id.freeroom_layout_dialog_search_hour_end_plus);
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

		searchTimeAdvUpToEndHourButton = (ImageButton) searchView
				.findViewById(R.id.freeroom_layout_dialog_search_hour_end_toend);
		searchTimeAdvUpToEndHourButton.setEnabled(true);
		searchTimeAdvUpToEndHourButton
				.setOnClickListener(new OnClickListener() {

					@Override
					public void onClick(View v) {
						endHourSelected = Constants.LAST_HOUR_CHECK;
						endMinSelected = 0;
						searchTimeAdvUpToEndSelected = true;
						searchTimeUpdateAllPickersAndButtons();
					}
				});

		searchParamChangeUserDefAddButton = (Button) searchView
				.findViewById(R.id.freeroom_layout_dialog_search_user_add);
		searchParamChangeUserDefAddButton
				.setOnClickListener(new OnClickListener() {

					@Override
					public void onClick(View v) {
						addSearchRoom.show();
					}
				});

		searchParamChangeUserDefEditButton = (Button) searchView
				.findViewById(R.id.freeroom_layout_dialog_search_user_edit);
		searchParamChangeUserDefEditButton
				.setOnClickListener(new OnClickListener() {

					@Override
					public void onClick(View v) {
						editSearchRoom.show();
					}
				});

		searchParamChangeUserDefResetButton = (Button) searchView
				.findViewById(R.id.freeroom_layout_dialog_search_user_reset);
		searchParamChangeUserDefResetButton
				.setOnClickListener(new OnClickListener() {

					@Override
					public void onClick(View v) {
						searchSelectUserDefReset(false);
					}
				});

		// for landscape device, mainly tablet, some layout are programmatically
		// changed to horizontal values, and weighted more logically.
		// XML IS ALWAYS DESIGNED FOR PHONES, as it's probably more than 97% of
		// users. tablets are changing their layout here.
		LinearLayout header_main = (LinearLayout) searchView
				.findViewById(R.id.freeroom_layout_dialog_search_upper_main);
		LinearLayout header_1st = (LinearLayout) searchView
				.findViewById(R.id.freeroom_layout_dialog_search_upper_first);
		LinearLayout header_2nd = (LinearLayout) searchView
				.findViewById(R.id.freeroom_layout_dialog_search_upper_second);
		LinearLayout header_3rd = (LinearLayout) searchView
				.findViewById(R.id.freeroom_layout_dialog_search_upper_third);
		if (commonIsLandscapeTabletMode()) {

			header_main.setOrientation(LinearLayout.HORIZONTAL);
			LinearLayout.LayoutParams p = new LinearLayout.LayoutParams(0,
					LinearLayout.LayoutParams.FILL_PARENT);
			p.weight = 1;

			header_1st.setLayoutParams(p);
			header_2nd.setLayoutParams(p);
			header_3rd.setLayoutParams(p);

			// radio group and chexbox group made horizontal
			// all 5 buttons made fillparent verticallly.
			RadioGroup rg = (RadioGroup) searchView
					.findViewById(R.id.freeroom_layout_dialog_search_any_vs_spec);
			rg.setOrientation(RadioGroup.HORIZONTAL);
			RadioGroup.LayoutParams q = new RadioGroup.LayoutParams(
					LayoutParams.FILL_PARENT, LayoutParams.WRAP_CONTENT);
			rg.setLayoutParams(q);

			searchParamAnyFreeRoomButton.setHeight(LayoutParams.FILL_PARENT);
			searchParamSpecificButton.setHeight(LayoutParams.FILL_PARENT);

			LinearLayout mLinearLayout = (LinearLayout) search
					.findViewById(R.id.freeroom_layout_dialog_search_opt_line_wrapper_1st);
			mLinearLayout.setOrientation(LinearLayout.HORIZONTAL);

			searchParamOnlyFreeRoomsButton.setHeight(LayoutParams.FILL_PARENT);
			searchParamSelectFavoritesButton
					.setHeight(LayoutParams.FILL_PARENT);
			searchParamSelectUserDefButton.setHeight(LayoutParams.FILL_PARENT);

			// Layouts to change the horizontal weight
			// They have ONLY ONE CHILD: Children cannot have weight
			LinearLayout anyButtonLayout = (LinearLayout) search
					.findViewById(R.id.freeroom_layout_dialog_search_any_layout);
			LinearLayout specButtonLayout = (LinearLayout) search
					.findViewById(R.id.freeroom_layout_dialog_search_spec_layout);
			LinearLayout favButtonLayout = (LinearLayout) search
					.findViewById(R.id.freeroom_layout_dialog_search_fav_layout);
			LinearLayout userDefButtonLayout = (LinearLayout) search
					.findViewById(R.id.freeroom_layout_dialog_search_user_layout);
			LinearLayout freeButtonLayout = (LinearLayout) search
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

		searchParamSelectedRoomsTextViewSearchMenu.setText(u
				.getSummaryTextFromCollection(addSearchRoomSelectedRooms));
		addSearchRoomAutoCompleteInputBarElement.setInputText("");

		if (!itsTheEnd) {
			searchLaunchValidateButton.setEnabled(searchAuditSubmit() == 0);
		}
	}

	/**
	 * {@link #search}: fill the search with the currently displayed request. <br>
	 * Not really used anymore, as we have previous request for this matter.
	 */
	private void searchFillWithRequest() {
		final FRRequestDetails request = mModel.getFRRequestDetails();
		if (request != null) {
			searchFillWithRequest(request);
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
		searchSelectOptionalLineLinearLayoutWrapperFirst
				.setVisibility(View.GONE);
		if (enabled) {
			searchSelectOptionalLineLinearLayoutWrapperFirst
					.setVisibility(View.VISIBLE);
		}
		searchSelectOptionalLineLinearLayoutWrapperSecond
				.setVisibility(View.GONE);
		if (request.isUser()) {
			searchSelectOptionalLineLinearLayoutWrapperSecond
					.setVisibility(View.VISIBLE);
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

	/**
	 * {@link #search}: RESET THE SEARCH COMPLETELY (SET TO DEFAULT VALUES)
	 */
	private void searchResetMain() {
		searchLaunchValidateButton.setEnabled(false);

		// reset the list of selected rooms
		addSearchRoomSelectedRooms.clear();
		// TODO: mSummarySelectedRoomsTextView
		// .setText(getString(R.string.freeroom_check_occupancy_search_text_no_selected_rooms));

		addSearchRoomAutoCompleteArrayListFRRoom.clear();

		searchResetTimes();

		searchParamAnyFreeRoomButton.setChecked(true);
		searchSelectOptionalLineLinearLayoutWrapperFirst
				.setVisibility(View.GONE);
		searchSelectOptionalLineLinearLayoutWrapperSecond
				.setVisibility(View.GONE);

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
		searchTimeAdvUpStartHourButton
				.setEnabled(startHourSelected <= Constants.LAST_HOUR_CHECK - 2);
		searchTimeAdvDownStartHourButton
				.setEnabled(startHourSelected > Constants.FIRST_HOUR_CHECK);

		searchTimeAdvUpEndHourButton
				.setEnabled(endHourSelected < Constants.LAST_HOUR_CHECK);
		searchTimeAdvDownEndHourButton
				.setEnabled(endHourSelected >= Constants.FIRST_HOUR_CHECK + 2);
		searchTimeAdvUpToEndHourButton
				.setEnabled(!searchTimeAdvUpToEndSelected);
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
		selected.setTimeInMillis(searchLaunchPreparePeriod()
				.getTimeStampStart());
		searchTimeDatePicker.setText(times.formatFullDate(selected));

		searchTimeDatePickerDialog.updateDate(yearSelected, monthSelected,
				dayOfMonthSelected);
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
		searchTimeStartShortPicker.setText(times.formatTime(
				searchLaunchPreparePeriod().getTimeStampStart(), true));
		searchTimeStartPicker.setText(times.generateTimeSummaryWithPrefix(
				getString(R.string.freeroom_selectstartHour), true, times
						.formatTime(searchLaunchPreparePeriod()
								.getTimeStampStart(), false)));
		searchTimePickerStartDialog.updateTime(startHourSelected,
				startMinSelected);
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
		searchTimeEndShortPicker.setText(times.formatTime(
				searchLaunchPreparePeriod().getTimeStampEnd(), true));
		searchTimeEndPicker.setText(times.generateTimeSummaryWithPrefix(
				getString(R.string.freeroom_selectendHour), true, times
						.formatTime(searchLaunchPreparePeriod()
								.getTimeStampEnd(), false)));
		if (endHourSelected >= Constants.LAST_HOUR_CHECK
				|| (endHourSelected == Constants.LAST_HOUR_CHECK - 1 && endMinSelected != 0)) {
			searchTimeAdvUpEndHourButton.setEnabled(false);
		} else {
			searchTimeAdvUpEndHourButton.setEnabled(true);
		}
		searchTimePickerEndDialog.updateTime(endHourSelected, endMinSelected);
	}

	/**
	 * {@link #search}: Construct the <code>FRPeriod</code> object asscociated
	 * with the current selected times.
	 * 
	 * @return
	 */
	private FRPeriod searchLaunchPreparePeriod() {
		Calendar start = Calendar.getInstance();
		start.set(yearSelected, monthSelected, dayOfMonthSelected,
				startHourSelected, startMinSelected, 0);
		start.set(Calendar.MILLISECOND, 0);

		Calendar end = Calendar.getInstance();
		end.set(yearSelected, monthSelected, dayOfMonthSelected,
				endHourSelected, endMinSelected, 0);
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

		List<String> mUIDList = new ArrayList<String>(
				addSearchRoomSelectedRooms.size());

		if (searchParamSelectFavoritesButton.isChecked()) {
			addAllFavoriteToCollection(mUIDList, true);
		}
		Set<FRRoom> userDef = new HashSet<FRRoom>(
				addSearchRoomSelectedRooms.size());
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
		FRRequestDetails details = new FRRequestDetails(period,
				searchParamOnlyFreeRoomsButton.isChecked(), mUIDList, any, fav,
				user, userDef, mModel.getGroupAccess());
		mModel.setFRRequestDetails(details, save);
		searchPreviousRequestAdapter.notifyDataSetChanged();
		commonReplayRefresh();
		search.dismiss();

		searchSelectUserDefReset(true); // cleans the selectedRooms of
										// userDefined
	}

	/**
	 * {@link #search}: Check that the times set are valid, according to the
	 * shared definition.
	 * 
	 * @return 0 if times are valids, positive integer otherwise
	 */
	private int searchAuditTime() {
		return searchAuditTimeString().equals("") ? 0 : 1;
	}

	/**
	 * {@link #search}: Check that the times set are valid, according to the
	 * shared definition.
	 * 
	 * @return the errors
	 */
	private String searchAuditTimeString() {
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
		FRPeriod period = searchLaunchPreparePeriod();
		String errorsTime = FRTimes.validCalendarsString(period);
		boolean isValid = errorsTime.equals("") ? true : false;
		TextView tv = (TextView) searchView
				.findViewById(R.id.freeroom_layout_dialog_search_time_summary);
		if (isValid) {
			// time summary ?
			char limit = commonIsLandscapeTabletMode() ? ' ' : '\n';
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
						Constants.FIRST_HOUR_CHECK, Constants.LAST_HOUR_CHECK,
						Constants.MIN_MINUTE_INTERVAL,
						Constants.MAXIMAL_WEEKS_IN_PAST,
						Constants.MAXIMAL_WEEKS_IN_FUTURE);
			} else {
				return getString(R.string.freeroom_search_invalid_time_basic,
						Constants.FIRST_HOUR_CHECK, Constants.LAST_HOUR_CHECK,
						Constants.MIN_MINUTE_INTERVAL);
			}
		}
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
		TextView tv = (TextView) searchView
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
				|| (!searchParamAnyFreeRoomButton.isChecked()
						&& searchParamSelectUserDefButton.isChecked() && addSearchRoomSelectedRooms
							.isEmpty())) {
			ret += getString(R.string.freeroom_search_check_empty_select);
		}

		if (searchParamAnyFreeRoomButton.isChecked()
				&& (searchParamSelectFavoritesButton.isChecked() || searchParamSelectUserDefButton
						.isChecked())) {
			ret += getString(R.string.freeroom_search_check_any_incompat);
		}
		if (!searchParamAnyFreeRoomButton.isChecked()
				&& !searchParamSelectFavoritesButton.isChecked()
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
		if (searchParamAnyFreeRoomButton.isChecked()
				&& !searchParamOnlyFreeRoomsButton.isChecked()) {
			ret += getString(R.string.freeroom_search_check_any_must_be_free);
		}
		return ret + searchAuditTimeString();
	}

	// PREVIOUS REQUEST MANAGEMENT //

	/**
	 * {@link #search}: Inits the title for previous request, with empty value
	 * if none, with "show" if not displayed, or "prev request" otherwise.
	 */
	private void searchPreviousRequestInitTitle() {
		if (mModel.getPreviousRequest().isEmpty()) {
			searchPreviousRequestTitleTextView.setText("");
			searchPreviousRequestTitleTextView.setVisibility(View.GONE);
			searchGoUpHomeButton.setVisibility(View.GONE);
		} else {
			searchPreviousRequestTitleTextView
					.setText(searchPreviousRequestTitleString);
			searchPreviousRequestTitleTextView.setVisibility(View.VISIBLE);
			searchGoUpHomeButton.setVisibility(View.VISIBLE);
		}
	}

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
			search.show();
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
					build.append(u.getSummaryTextFromCollection(
							req.getUidNonFav(), "", max));
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
		settings.getWindow().addFlags(
				WindowManager.LayoutParams.FLAG_WATCH_OUTSIDE_TOUCH);
		settings.getWindow().addFlags(
				WindowManager.LayoutParams.FLAG_DIM_BEHIND);
		settings.getWindow().setAttributes(lp);

		settingsView = commonLayoutInflater.inflate(
				R.layout.freeroom_layout_dialog_param, null);

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

		FRPeriod period = new FRPeriod(System.currentTimeMillis(),
				System.currentTimeMillis() + FRTimes.ONE_HOUR_IN_MS);
		FRRoom room = new FRRoom("mock", "1234");
		List<FRPeriodOccupation> occupancy = new ArrayList<FRPeriodOccupation>(
				1);
		List<FRRoomOccupancy> occupancies = new ArrayList<FRRoomOccupancy>(4);
		FRRoomOccupancy free = new FRRoomOccupancy(room, occupancy, false,
				true, period);
		FRRoomOccupancy part = new FRRoomOccupancy(room, occupancy, true, true,
				period);
		FRRoomOccupancy occupied = new FRRoomOccupancy(room, occupancy, true,
				false, period);
		FRRoomOccupancy error = new FRRoomOccupancy(room, occupancy, false,
				false, period);
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
			tv.setCompoundDrawablesWithIntrinsicBounds(
					mModel.getColoredDotDrawable(occ), 0, 0, 0);
		}
	}

	/**
	 * {@link #settings}: Refreshes the example text of formatting times.
	 * <p>
	 * Used in param dialog to show the impact of a particular formatting.
	 */
	private void settingsRefreshTimeFormatExample() {
		times = mModel.getFRTimesClient(this);
		TextView tv = (TextView) settingsView
				.findViewById(R.id.freeroom_layout_dialog_param_time_language_example);
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
				|| current
						.equals(ColorBlindMode.DOTS_SYMBOL_LINEFULL_DISCOLORED)) {
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
		builder.setIcon(R.drawable.freeroom_ic_action_about);
		builder.setNeutralButton(getString(R.string.freeroom_welcome_dismiss),
				null);

		// Get the AlertDialog from create()
		welcome = builder.create();

		// redefine paramaters to dim screen when displayed
		WindowManager.LayoutParams lp = welcome.getWindow().getAttributes();
		lp.dimAmount = 0.60f;
		// these doesn't work
		lp.width = LayoutParams.FILL_PARENT;
		lp.height = LayoutParams.WRAP_CONTENT;
		welcome.getWindow().addFlags(
				WindowManager.LayoutParams.FLAG_WATCH_OUTSIDE_TOUCH);
		welcome.getWindow()
				.addFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND);
		welcome.getWindow().setAttributes(lp);

		welcomeView = commonLayoutInflater.inflate(
				R.layout.freeroom_layout_dialog_welcome, null);

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

	// KONAMI CODE

	/**
	 * KONAMI: Enum to describe Moves taken into account in Konami Code.
	 * <p>
	 * None represent the null action, if the trigger was not long enough.
	 * 
	 */
	private enum KonamiCodeMove {
		NONE, UP, DOWN, LEFT, RIGHT;
	}

	/**
	 * KONAMI: Konami Code as string in English.
	 */
	private final static String konamiCodeEnglish = "UUDDLRLRBA";
	/**
	 * KONAMI: Konami Code as string in French.
	 */
	private final static String konamiCodeFrench = "hhbbgdgdBA";

	/**
	 * KONAMI: Construct the {@link #konamiCodeMoveList} list of ordered
	 * {@link KonamiCodeMove} moves to enter.
	 */
	private void konamiCodeConstructListMove() {
		konamiCodeMoveList = new ArrayList<KonamiCodeMove>();
		konamiCodeMoveList.add(KonamiCodeMove.UP);
		konamiCodeMoveList.add(KonamiCodeMove.UP);
		konamiCodeMoveList.add(KonamiCodeMove.DOWN);
		konamiCodeMoveList.add(KonamiCodeMove.DOWN);
		konamiCodeMoveList.add(KonamiCodeMove.LEFT);
		konamiCodeMoveList.add(KonamiCodeMove.RIGHT);
		konamiCodeMoveList.add(KonamiCodeMove.LEFT);
		konamiCodeMoveList.add(KonamiCodeMove.RIGHT);
	}

	/**
	 * KONAMI: Stores the list of ordered {@link KonamiCodeMove}.
	 */
	private List<KonamiCodeMove> konamiCodeMoveList;
	/**
	 * KONAMI: The minimal coordinate change to trigger a {@link KonamiCodeMove}
	 * .
	 */
	private int konamiCodeMinChangeCoord = 50;
	/**
	 * KONAMI: The maximal time elapsed between two move to trigger a
	 * {@link KonamiCodeMove}
	 */
	private long konamiCodeMaxTime = 3000;

	/**
	 * KONAMI: Stores the index to where the KonamiCode list has been triggered.
	 * <p>
	 * 0 means no trigger no so far, or trigger reset <br>
	 * 1-6 means trigger is ongoing <br>
	 * 7 means konami is triggered <br>
	 * 8 means konami has been triggered <br>
	 */
	private int konamiCodeCurrentIndex = 0;
	/**
	 * KONAMI: Stores the X-Coordinate of the start of the move
	 */
	private float konamiCodePrevX = 0;
	/**
	 * KONAMI: Stores the Y-Coordinate of the start of the move
	 */
	private float konamiCodePrevY = 0;

	/**
	 * KONAMI: Stores the time of end the last triggered {@link KonamiCodeMove}.
	 */
	private long konamiCodeLastTimeTriggered = System.currentTimeMillis();

	/**
	 * KONAMI: Checks an event given by the System to detect a
	 * {@link KonamiCodeMove}.
	 * <p>
	 * All moves on the screen starts by a {@link MotionEvent#ACTION_DOWN} event
	 * when the screen is pressed, followed a long list of
	 * {@link MotionEvent#ACTION_MOVE} when the user moves his finger and ends
	 * by a {@link MotionEvent#ACTION_UP} event when the user release the
	 * screen.
	 * <p>
	 * To detect a {@link KonamiCodeMove}, we are interested in the difference
	 * of coordinates between the start {@link MotionEvent#ACTION_DOWN} and the
	 * end {@link MotionEvent#ACTION_UP} events. If the coordinates change more
	 * than {@link FreeRoomHomeView#konamiCodeMinChangeCoord}, then a
	 * {@link KonamiCodeMove} will happen! If it's the next one in the
	 * {@link FreeRoomHomeView#konamiCodeMoveList} according to
	 * {@link FreeRoomHomeView#konamiCodeCurrentIndex}, then the index is
	 * incremented. If not, the index is reset to the start (0).
	 * 
	 * @param event
	 *            a motion event to check.
	 */
	private void konamiCodeCheck(MotionEvent event) {
		if (konamiCodeCurrentIndex >= 8) {
			konamiCodeCurrentIndex = 0;
			return;
		} else if (event.getAction() == MotionEvent.ACTION_DOWN) {
			// action down/start: if too much time elapsed, reset
			if (konamiCodeCurrentIndex != 0) {
				long elapsed = System.currentTimeMillis()
						- konamiCodeLastTimeTriggered;
				if (elapsed > konamiCodeMaxTime) {
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
			KonamiCodeMove move = KonamiCodeMove.NONE;
			if (absX < konamiCodeMinChangeCoord
					&& absY < konamiCodeMinChangeCoord) {
				// if changes are not significant: NONE move.
			} else if (absY > absX) {
				// if more change on Y axe: up/down event.
				if (diffY > 0) {
					move = KonamiCodeMove.DOWN;
				} else if (diffY < 0) {
					move = KonamiCodeMove.UP;
				}
			} else {
				// if more change on X axe: left/right event.
				if (diffX > 0) {
					move = KonamiCodeMove.RIGHT;
				} else if (diffX < 0) {
					move = KonamiCodeMove.LEFT;
				}
			}

			if (move.equals(KonamiCodeMove.NONE)) {
				// none event: reset
				konamiCodeCurrentIndex = 0;
				return;
			} else if (move.equals(konamiCodeMoveList
					.get(konamiCodeCurrentIndex))) {
				// next event in the row: sucess
				// update index and time of last success
				konamiCodeLastTimeTriggered = System.currentTimeMillis();
				konamiCodeCurrentIndex++;
				if (konamiCodeCurrentIndex == 8) {
					konamiCodeActivateKeyBoard();
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
	 * KONAMI: Display the input edittext and keyboard to type "ba" and complete
	 * konami code.
	 */
	private void konamiCodeActivateKeyBoard() {
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
				devTestActivateDebug(text);
				if (text.equalsIgnoreCase("ba")) {
					konamiCodeActivate();
				}
				konamiEditText.setText("");
				commonDismissSoftKeyBoard(v);
				konamiLayout.setVisibility(View.GONE);
			}
		});
		konamiConfirm.setOnLongClickListener(new OnLongClickListener() {

			@Override
			public boolean onLongClick(View v) {
				String text = konamiEditText.getText().toString();
				devTestActivateDebug(text);
				if (text.equalsIgnoreCase("ba")) {
					konamiCodeActivate();
				}
				return false;
			}
		});

	}

	/**
	 * KONAMI: Activates and display the konami code.
	 * <p>
	 * This popup is NEVER closable. The user who cheats has to close the
	 * application by the application manager! :p
	 * <p>
	 * Never trust cheaters! :D
	 */
	private void konamiCodeActivate() {
		ImageView konami = new ImageView(this);
		konami.setImageResource(R.drawable.freeroom_konami);
		error.setView(konami);

		errorDialogShowMessage("KONAMI CODE IS CHEATING! \nNOW FIND THE WAY OUT YOU CHEATER!");

		error.setOnDismissListener(new OnDismissListener() {
			@Override
			public void onDismiss(DialogInterface dialog) {
				error.show();
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
		if (query.equalsIgnoreCase(konamiCodeEnglish)
				|| query.equalsIgnoreCase(konamiCodeFrench)) {
			konamiCodeActivate();
		}
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
			errorDialogShowMessage("Time selection with pickers/arrows set to: "
					+ mModel.getTimePickersPref().name()
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
			errorDialogShowMessage(timeLang + mModel.getTimeLanguage().name()
					+ resetTimeLang);
		}
		if (query.matches("[Ff][Rr][Ee][Nn][Cc][Hh]")) {
			TimeLanguage tl = mModel.getTimeLanguage();
			if (tl.equals(TimeLanguage.FRENCH)) {
				mModel.setTimeLanguage(TimeLanguage.DEFAULT);
			} else {
				mModel.setTimeLanguage(TimeLanguage.FRENCH);
			}
			errorDialogShowMessage(timeLang + mModel.getTimeLanguage().name()
					+ resetTimeLang);
		}
		if (query.matches("[Rr][Ee][Ss][Ee][Tt][Aa][Ll][Ll]")) {
			mModel.resetAll(false);
			errorDialogShowMessage("All settings have been reset, including UID.");
		}
		if (query.matches("[Rr][Ee][Ss][Ee][Tt]")) {
			mModel.resetAll(true);
			errorDialogShowMessage("All settings have been reset!");
		}
		initSearchDialog();
		if (query.matches("[Dd][Ee][Bb][Uu][Gg]")
				&& !query.startsWith(devTestPrefix)) {
			boolean advanced = mModel.getAdvancedTime();
			mModel.setAdvancedTime(!advanced);
			initSearchDialog();
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
			initSearchDialog();
		} else if (query.equalsIgnoreCase(devTestPrefix + devTestChangeDateOff)) {
			mModel.setAdvancedTime(false);
			errorDialogShowMessage("Change date disabled");
			initSearchDialog();
		} else if (query.equalsIgnoreCase(devTestPrefix + devTestChangeGroupOn)) {
			mModel.setGroupAccess(Integer.MAX_VALUE);
			errorDialogShowMessage("Change group access activated");
		} else if (query
				.equalsIgnoreCase(devTestPrefix + devTestChangeGroupOff)) {
			mModel.setGroupAccess();
			errorDialogShowMessage("Change group access disabled");
		}
	}

	// SERVICE

	/**
	 * Add all the favorites FRRoom to the collection. The collection will be
	 * cleared prior to any adding.
	 * 
	 * @param collection
	 *            collection in which you want the favorites to be added.
	 * 
	 * @param addOnlyUID
	 *            true to add UID, false to add fully FRRoom object.
	 */
	private void addAllFavoriteToCollection(Collection collection,
			boolean addOnlyUID) {
		collection.clear();
		OrderMapListFew<String, List<FRRoom>, FRRoom> set = mModel
				.getFavorites();
		Iterator<String> iter = set.keySetOrdered().iterator();
		while (iter.hasNext()) {
			String key = iter.next();
			Iterator<FRRoom> iter2 = set.get(key).iterator();
			while (iter2.hasNext()) {
				FRRoom mRoom = iter2.next();

				if (addOnlyUID) {
					collection.add(mRoom.getUid());
				} else {
					collection.add(mRoom);
				}
			}
		}
	}

	@Override
	protected String screenName() {
		return "freeroom";
	}

}