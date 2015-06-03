package org.pocketcampus.plugin.freeroom.android;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OptionalDataException;
import java.math.BigInteger;
import java.security.SecureRandom;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.ConcurrentModificationException;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.TreeSet;

import org.pocketcampus.platform.android.core.IView;
import org.pocketcampus.platform.android.core.PluginModel;
import org.pocketcampus.plugin.freeroom.R;
import org.pocketcampus.plugin.freeroom.android.iface.IFreeRoomModel;
import org.pocketcampus.plugin.freeroom.android.iface.IFreeRoomView;
import org.pocketcampus.plugin.freeroom.android.utils.FRRequestDetails;
import org.pocketcampus.plugin.freeroom.android.utils.FRTimesClient;
import org.pocketcampus.plugin.freeroom.android.utils.OrderMapListFew;
import org.pocketcampus.plugin.freeroom.android.utils.SetArrayList;
import org.pocketcampus.plugin.freeroom.shared.FRMessageFrequency;
import org.pocketcampus.plugin.freeroom.shared.FRPeriod;
import org.pocketcampus.plugin.freeroom.shared.FRPeriodOccupation;
import org.pocketcampus.plugin.freeroom.shared.FRRoom;
import org.pocketcampus.plugin.freeroom.shared.FRRoomOccupancy;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.drawable.Drawable;

/**
 * FreeRoomModel - The Model that stores the data of this plugin.
 * <p>
 * This is the Model associated with the FreeRoom plugin. It stores the data
 * required for the correct functioning of the plugin. Some data is persistent,
 * other data are temporary.
 * <p>
 * This class is very long, but it's divided and organized in several parts: <br>
 * - common things, starting at {@link #getViewInterface()} <br>
 * - interaction with controller/view, start at
 * {@link #setOccupancyResults(Map)}<br>
 * - non-stored values and preferences, start at {@link #getFRRequestDetails()}
 * <br>
 * - stored values and preferences, start at {@link #generateAnonymID()} <br>
 * - colors methods, start at {@link #isColorLineFull()} <br>
 * - general object storage, start at {@link #writeObjectToFile(String, Object)}
 * <br>
 * - favorites storage, start at {@link #retrieveFavorites()} <br>
 * - previous request storage, start at {@link #retrievePreviousRequest()} <br>
 * 
 * @author FreeRoom Project Team (2014/05)
 * @author Julien WEBER <julien.weber@epfl.ch>
 * @author Valentin MINDER <valentin.minder@epfl.ch>
 * 
 */
public class FreeRoomModel extends PluginModel implements IFreeRoomModel {

	/**
	 * Keys to persistent storage
	 */
	private final String PREF_USER_DETAILS_KEY = "KEY_USER_DETAILS";
	/*
	 * Keys for the setting.
	 */
	private final String anonymIDKey = "anonymIDKey";
	private final String homeBehaviourRoomIDKey = "homeBehaviourRoomIDKey";
	private final String homeBehaviourTimeIDKey = "homeBehaviourTimeIDKey";
	private final String timeLanguageIDKey = "timeLanguageIDKey";
	private final String previousRequestNumberIDKey = "previousRequestNumberIDKey";
	private final String timePickersPrefIDKey = "timePickersPrefIDKey";
	private final String registeredTimeIDKey = "registeredTimeIDKey";
	private final String registeredUserIDKey = "registeredUserIDKey";
	private final String advancedTimeIDKey = "advancedTimeIDKey";
	private final String groupAccessIDKey = "groupAccessIDKey";
	private final String colorBlindModeIDKey = "colorBlindModeIDKey";

	/*
	 * Colors references
	 */
	private final int COLOR_TRANSPARENT = Color.TRANSPARENT;
	private final int COLOR_DEFAULT = Color.WHITE;
	private final int COLOR_TRANSPARENCY = 128;
	private final int COLOR_GREEN_FREE = Color.argb(COLOR_TRANSPARENCY, 99,
			199, 99);
	private final int COLOR_RED_OCCUPIED = Color.argb(COLOR_TRANSPARENCY, 199,
			50, 50);
	private final int COLOR_ORANGE_ATLEASTONCE = Color.argb(COLOR_TRANSPARENCY,
			255, 170, 10);
	private final int COLOR_GREEN_FREE_CB = Color.argb(COLOR_TRANSPARENCY, 0,
			51, 153);
	private final int COLOR_RED_OCCUPIED_CB = Color.argb(COLOR_TRANSPARENCY,
			153, 0, 0);
	private final int COLOR_ORANGE_ATLEASTONCE_CB = Color.argb(
			COLOR_TRANSPARENCY, 255, 255, 153);
	private final int COLOR_HEADER_HIGHLIGHT = Color.GRAY;

	/**
	 * Default values, used to RESET.
	 */
	private final HomeBehaviourRoom DEFAULT_HOMEBEHAVIOUR_ROOM = HomeBehaviourRoom.ANYFREEROOM;
	private final HomeBehaviourTime DEFAULT_HOMEBEHAVIOUR_TIME = HomeBehaviourTime.CURRENT_TIME;
	private final TimeLanguage DEFAULT_TIMELANGUAGE = TimeLanguage.DEFAULT;
	private final TimePickersPref DEFAULT_TIMEPICKERS = TimePickersPref.PICKERS;
	private final ColorBlindMode DEFAULT_COLORBLINDMODE = ColorBlindMode.DEFAULT;
	private final int DEFAULT_PREVREQUEST = 20;
	private final boolean DEFAULT_ADVANCED_TIME = false;
	public final int DEFAULT_GROUP_ACCESS = 10;

	/**
	 * 
	 * Reference to the Views that need to be notified when the stored data
	 * changes.
	 */
	IFreeRoomView mListeners = (IFreeRoomView) getListeners();

	/**
	 * Storing the occupancies, mapped by building.
	 */
	private OrderMapListFew<String, List<?>, FRRoomOccupancy> occupancyByBuilding;

	/**
	 * Storing the <code>WorkingOccupancy</code> of people who indicate their
	 * are going to work there.
	 */
	private List<FRMessageFrequency> listMessageFrequency = new ArrayList<FRMessageFrequency>();

	/**
	 * Reference to application context.
	 */
	private Context context;

	/**
	 * Storage for basic preferences, setting and so on.
	 */
	private SharedPreferences preferences;

	/**
	 * Get the language of the device, if not available on server, default is
	 * english
	 * **/
	public String getUserLanguage() {
		return Locale.getDefault().getDisplayLanguage().toLowerCase();
	}

	/**
	 * Constructor with reference to the context.
	 * <p>
	 * We need the context to be able to instantiate the SharedPreferences
	 * object in order to use persistent storage.
	 * 
	 * @param context
	 *            is the Application Context.
	 */
	public FreeRoomModel(Context context) {
		preferences = context.getSharedPreferences(PREF_USER_DETAILS_KEY,
				Context.MODE_PRIVATE);
		initSharedPreferences();
		occupancyByBuilding = new OrderMapListFew<String, List<?>, FRRoomOccupancy>(
				30);
		this.context = context;
	}

	private void initSharedPreferences() {
		// color-blind is stored in a variable as it's used many times!
		colorBlindMode = ColorBlindMode.valueOf(preferences.getString(
				colorBlindModeIDKey, DEFAULT_COLORBLINDMODE.name()));
		// generates the anonym ID at first launch time
		getAnonymID();
		registeredTime = preferences.getLong(registeredTimeIDKey,
				registeredTime);
		registeredUser = preferences.getBoolean(registeredUserIDKey,
				registeredUser);
	}

	/**
	 * Returns the Type of the Views associated with this plugin.
	 */
	@Override
	protected Class<? extends IView> getViewInterface() {
		return IFreeRoomView.class;
	}

	/**
	 * Returns the registered listeners to by notified.
	 */
	public IFreeRoomView getListenersToNotify() {
		return mListeners;
	}

	public void autoCompleteLaunch() {
		mListeners.autoCompleteLaunch();
	}

	/* ALL CONTROLLER - VIEW INTERACTION */
	/* START OF OCCUPANCY RESULTS */

	/**
	 * Update the occupancy results in the model. The reference to the old data
	 * is kept, only the old data are trashed but the reference is kept.
	 * 
	 * @param occupancyOfRooms
	 */
	public void setOccupancyResults(
			Map<String, List<FRRoomOccupancy>> occupancyOfRooms) {
		occupancyByBuilding.clear();
		// keys are ordered!
		TreeSet<String> keySetOrder = new TreeSet<String>(
				occupancyOfRooms.keySet());
		List<String> buildings = getOrderedBuildings();
		for (String building : buildings) {
			List<FRRoomOccupancy> list = occupancyOfRooms.get(building);
			if (list != null) {
				keySetOrder.remove(building);
				occupancyByBuilding.put(building, list);
			}
		}

		for (String key : keySetOrder) {
			occupancyByBuilding.put(key, occupancyOfRooms.get(key));
		}
		mListeners.occupancyResultsUpdated();
	}

	/**
	 * Get the occupancy results. Note that the reference never changes, so you
	 * simply need to update your adapter, never put the date again in it.
	 * 
	 * @return
	 */
	public OrderMapListFew<String, List<?>, FRRoomOccupancy> getOccupancyResults() {
		return this.occupancyByBuilding;
	}

	/* END OF OCCUPANCY RESULTS */
	/* START OF AUTOCOMPLETE RESULTS */

	/**
	 * Stores the rooms autocompleted, mapped by buildings.
	 */
	private Map<String, List<FRRoom>> listRoom;

	public void setAutoComplete(Map<String, List<FRRoom>> listRoom) {
		this.listRoom = listRoom;
		mListeners.autoCompleteUpdated();
	}

	/**
	 * Return the rooms autocompleted, mapped by buildings.
	 * 
	 * @return the rooms autocompleted, mapped by buildings.
	 */
	public Map<String, List<FRRoom>> getAutoComplete() {
		return listRoom;
	}

	/* END OF AUTOCOMPLETE RESULTS */
	/* "WHO'S WORKING THERE" PART */

	/**
	 * Stores a list of <code>WorkingOccupancy</code> to represent what others
	 * are doing.
	 * 
	 * @param listMessageFrequency
	 */
	public void setListMessageFrequency(
			List<FRMessageFrequency> listMessageFrequency) {
		this.listMessageFrequency.clear();
		this.listMessageFrequency.addAll(listMessageFrequency);
		mListeners.workingMessageUpdated();
	}

	/**
	 * Retrieves the stored <code>List</code> of <code>WorkingOccupancy</code>.
	 * 
	 * @return
	 */
	public List<FRMessageFrequency> getListMessageFrequency() {
		return listMessageFrequency;
	}

	/* END OF "WHO'S WORKING THERE" PART */
	/* NON-STORED PARAMETERS/VALUEWS */

	/**
	 * Stores the currently displayed request.
	 */
	private FRRequestDetails mFRRequest;

	/**
	 * Get the currently displayed request.
	 * 
	 * @return the currently displayed request.
	 */
	public FRRequestDetails getFRRequestDetails() {
		return mFRRequest;
	}

	/**
	 * Set the currently displayed request.
	 * <p>
	 * Make sure you call a notify method on the previous request adapter.
	 * 
	 * @param request
	 *            next currently displayed request.
	 * @param save
	 *            if the request should be kept in history or not.
	 */
	public void setFRRequestDetails(FRRequestDetails request, boolean save) {
		request.setULanguage(getUserLanguage());
		if (save) {
			// write in history each time a request is set.
			addPreviousRequest(request);
		}
		this.mFRRequest = request;
	}

	private FRRoomOccupancy occupancy;

	public void setDisplayedOccupancy(FRRoomOccupancy occupancy) {
		this.occupancy = occupancy;
	}

	public FRRoomOccupancy getDisplayedOccupancy() {
		return occupancy;
	}

	/**
	 * True if the last sharing was only for server, false if also for friends.
	 */
	private boolean onlyServer = false;

	/**
	 * Return the value of onlyServer boolean.
	 * 
	 * @return true if the last sharing was only for server, false if also for
	 *         friends.
	 */
	public boolean isOnlyServer() {
		return onlyServer;
	}

	/**
	 * Set the value of onlyServer boolean.
	 * 
	 * @param newValue
	 *            true if the last sharing was only for server, false if also
	 *            for friends.
	 */
	public void setOnlyServer(boolean newValue) {
		onlyServer = newValue;
	}

	/**
	 * Stores the whole period treated by the last FRReply received from server.
	 */
	private FRPeriod overAllTreatedPeriod = null;

	/**
	 * Set the whole period treated by the last FRReply received from server.
	 * 
	 * @param overAllTreatedPeriod
	 *            the new period
	 */
	public void setOverAllTreatedPeriod(FRPeriod overAllTreatedPeriod) {
		this.overAllTreatedPeriod = overAllTreatedPeriod;
	}

	/**
	 * Retrieves the whole period treated by the last FRReply received from
	 * server.
	 * 
	 * @return the whole period.
	 */
	public FRPeriod getOverAllTreatedPeriod() {
		return overAllTreatedPeriod;
	}

	/**
	 * WARNING: THIS FEATURE HAS BEEN CANCELED AND this is not kept permanently!
	 * <p>
	 * Order of the buildings for displaying to the user.
	 */
	private List<String> orderedBuildings = new ArrayList<String>();

	/**
	 * * WARNING: THIS FEATURE HAS BEEN CANCELED AND this is not kept
	 * permanently!
	 * <p>
	 * Get the orderedBuilding list to display
	 * 
	 * @return the list of ordered buildings.
	 */
	public List<String> getOrderedBuildings() {
		return orderedBuildings;
	}

	/* ALL STORED PREFERENCES */

	/**
	 * RESET DEFINITELY ALL THE SHARED PREFERENCES.
	 * <p>
	 * 
	 * @param keepAnonymUID
	 *            if the unique ID should be kept in the operation.
	 */
	public void resetAll(boolean keepAnonymUID) {
		SharedPreferences.Editor editor = preferences.edit();
		if (keepAnonymUID) {
			String uid = getAnonymID();
			long time = getRegisteredTime();
			boolean reg = getRegisteredUser();
			editor.clear();
			editor.putString(anonymIDKey, uid);
			editor.putLong(registeredTimeIDKey, time);
			editor.putBoolean(registeredUserIDKey, reg);
		} else {
			editor.clear();
		}
		editor.commit();
		resetFavorites();
		resetPreviousRequest();
	}

	/**
	 * Retrieves the 32-char unique and anonymous device-identifier.
	 * <p>
	 * This identifiers guarantees the uniqueness among users and hold no
	 * personal data, neither ID or information from the device, it's therefore
	 * anonymous. We DONT use the ID of the device as this is not anonymous
	 * enough (it cannot be changed nor deleted, and identifies the device also
	 * for other apps)
	 * <p>
	 * It's stored in persistent memory, and generated if not exists at this
	 * time. It can be deleted only if the user deletes and reinstall the app,
	 * and this is not considered as an issue.
	 * 
	 * @return the 32-char unique and anonymous device-identifier.
	 */
	public String getAnonymID() {
		String anonymID = preferences.getString(anonymIDKey, null);
		if (anonymID != null) {
			return anonymID;
		} else {
			anonymID = generateAnonymID();
			SharedPreferences.Editor editor = preferences.edit();
			editor.putString(anonymIDKey, anonymID);
			editor.commit();
			return anonymID;
		}
	}

	/**
	 * Generates an unique and anonymous device-identifier based on the time of
	 * generation in milliseconds concatenated to a random String.
	 * 
	 * <p>
	 * This guarantees that every user has a different identifier with a very
	 * high certainty. To be the same: must be generated at the exact same
	 * millisecond + get the exact the same random string, which probability can
	 * be considered as 0 as there is no security issue with this identifier.
	 * 
	 * @return a 32-char anonymous and unique ID.
	 */
	private String generateAnonymID() {
		// time in millis as a string
		long time = System.currentTimeMillis();
		setRegisteredTime(time);
		String timeAsString = time + "";

		// random string to complete to 32 chars
		return timeAsString
				+ new BigInteger(130, new SecureRandom())
						.toString(32 - timeAsString.length());
	}

	/**
	 * Stores the default behavior at launch time.
	 * <p>
	 * Favorites: the favorites occupancy, free or not. (NOT default)<br>
	 * Favorites only free: the favorites that are free <br>
	 * Any free room: all the free rooms (default)<br>
	 * Lastrequest: replay last request (regarding of room, NOT time: default
	 * param for time will be used!). <br>
	 */
	public enum HomeBehaviourRoom {
		FAVORITES, FAVORITES_ONLY_FREE, ANYFREEROOM, LASTREQUEST;
	}

	/**
	 * Stores the homeBehaviourRoom setting.
	 * <p>
	 * Default: HomeBehaviourRoom.ANYFREEROOM (NOT Favorites!!!).
	 */

	/**
	 * Set the homeBehaviourRoom setting.
	 * 
	 * @param next
	 *            the new homeBehaviourRoom setting.
	 */
	public void setHomeBehaviourRoom(HomeBehaviourRoom next) {
		SharedPreferences.Editor editor = preferences.edit();
		editor.putString(homeBehaviourRoomIDKey, next.name());
		editor.commit();
	}

	/**
	 * Retrieves the homeBehaviourRoom setting.
	 * 
	 * @return the current homeBehaviourRoom setting.
	 */
	public HomeBehaviourRoom getHomeBehaviourRoom() {
		return HomeBehaviourRoom.valueOf(preferences.getString(
				homeBehaviourRoomIDKey, DEFAULT_HOMEBEHAVIOUR_ROOM.name()));
	}

	/**
	 * Stores the default behavior at launch time.
	 * <p>
	 * Current time: request for current hour (or two) <br>
	 * Up to end of day: current hour up to last hour <br>
	 * Whole day: from first to last hour checkable. <br>
	 */
	public enum HomeBehaviourTime {
		CURRENT_TIME, UP_TO_END_OF_DAY, WHOLE_DAY;
	}

	/**
	 * Set the homeBehaviourTime setting.
	 * 
	 * @param next
	 *            the new homeBehaviourTime setting.
	 */
	public void setHomeBehaviourTime(HomeBehaviourTime next) {
		SharedPreferences.Editor editor = preferences.edit();
		editor.putString(homeBehaviourTimeIDKey, next.name());
		editor.commit();
	}

	/**
	 * Retrieves the homeBehaviourTime setting.
	 * 
	 * @return the current homeBehaviourTime setting.
	 */
	public HomeBehaviourTime getHomeBehaviourTime() {
		return HomeBehaviourTime.valueOf(preferences.getString(
				homeBehaviourTimeIDKey, DEFAULT_HOMEBEHAVIOUR_TIME.name()));
	}

	/**
	 * Declaration of TimeLanguage type supported.
	 * <p>
	 * Default will choose the one translated in your language, if any. If the
	 * application is not translated, it will choose English formatting.
	 * Otherwise, it's useful to force using other language formatting (you may
	 * have your device in English but still want European formatting).
	 * <p>
	 * Note: English formatting is understood there as US format.
	 */
	public enum TimeLanguage {
		DEFAULT, ENGLISH, FRENCH;
	}

	/**
	 * Set the timeLanguage setting.
	 * 
	 * @param tl
	 *            the new timeLanguage setting.
	 */
	public void setTimeLanguage(TimeLanguage tl) {
		SharedPreferences.Editor editor = preferences.edit();
		editor.putString(timeLanguageIDKey, tl.name());
		editor.commit();
	}

	/**
	 * Retrieves the timeLanguage setting.
	 * 
	 * @return the current timeLanguage setting.
	 */
	public TimeLanguage getTimeLanguage() {
		return TimeLanguage.valueOf(preferences.getString(timeLanguageIDKey,
				DEFAULT_TIMELANGUAGE.name()));
	}

	/**
	 * # of previous request before previous requests are deleted.
	 * 
	 * @return the previousRequestNumber
	 */
	private int getPreviousRequestNumber() {
		return preferences.getInt(previousRequestNumberIDKey,
				DEFAULT_PREVREQUEST);
	}

	/**
	 * # of previous request before previous requests are deleted.
	 * 
	 * @param previousRequestNumber
	 *            the previousRequestNumber to set
	 */
	public void setPreviousRequestNumber(int previousRequestNumber) {
		SharedPreferences.Editor editor = preferences.edit();
		editor.putInt(previousRequestNumberIDKey, previousRequestNumber);
		editor.commit();
	}

	/**
	 * Set the advancedTime setting.
	 * 
	 * @param next
	 *            the new advancedTime setting.
	 */
	public void setAdvancedTime(boolean next) {
		SharedPreferences.Editor editor = preferences.edit();
		editor.putBoolean(advancedTimeIDKey, next);
		editor.commit();
	}

	/**
	 * Retrieves the advancedTime setting.
	 * 
	 * @return the current advancedTime setting.
	 */
	public boolean getAdvancedTime() {
		return preferences.getBoolean(advancedTimeIDKey, DEFAULT_ADVANCED_TIME);
	}

	/**
	 * Stores the registeredTime setting.
	 * <p>
	 * Default: 0.
	 */
	private long registeredTime = 0;

	/**
	 * Set the registeredTime setting.
	 * 
	 * @param next
	 *            the new registeredTime setting.
	 */
	private void setRegisteredTime(long next) {
		this.registeredTime = next;
		SharedPreferences.Editor editor = preferences.edit();
		editor.putLong(registeredTimeIDKey, registeredTime);
		editor.commit();
	}

	/**
	 * Declaration of TimePickersPref type supported.
	 * <p>
	 * This settings is not available to user, but as some developers, a cheat
	 * code can activate them.
	 */
	public enum TimePickersPref {
		PICKERS, ARROWS, BOTH;
	}

	/**
	 * Set the timePickersPref setting.
	 * 
	 * @param tpf
	 *            the new timePickersPref setting.
	 */
	public void setTimePickersPref(TimePickersPref tpf) {
		SharedPreferences.Editor editor = preferences.edit();
		editor.putString(timePickersPrefIDKey, tpf.name());
		editor.commit();
	}

	/**
	 * Retrieves the timePickersPref setting.
	 * 
	 * @return the current timePickersPref setting.
	 */
	public TimePickersPref getTimePickersPref() {
		return TimePickersPref.valueOf(preferences.getString(
				timePickersPrefIDKey, DEFAULT_TIMEPICKERS.name()));
	}

	/**
	 * Retrieve the group access the user is registered for.
	 * 
	 * @return the previousRequestNumber
	 */
	public int getGroupAccess() {
		return preferences.getInt(groupAccessIDKey, DEFAULT_GROUP_ACCESS);
	}

	/**
	 * Set the group access the user is registered for.
	 * 
	 * @param previousRequestNumber
	 *            the previousRequestNumber to set
	 */
	public void setGroupAccess(int groupAccess) {
		SharedPreferences.Editor editor = preferences.edit();
		editor.putInt(groupAccessIDKey, groupAccess);
		editor.commit();
	}

	/**
	 * Set the group access to the default value.
	 */
	public void setGroupAccess() {
		setGroupAccess(DEFAULT_GROUP_ACCESS);
	}

	/**
	 * Retrieves the registeredTime setting.
	 * 
	 * @return the current registeredTime setting.
	 */
	public long getRegisteredTime() {
		return this.registeredTime;
	}

	/**
	 * Stores the registeredUser setting.
	 * <p>
	 * Default: false.
	 */
	private boolean registeredUser = false;

	/**
	 * Set the registeredUser setting.
	 * 
	 * @param next
	 *            the new registeredUser setting.
	 */
	public void setRegisteredUser(boolean next) {
		this.registeredUser = next;
		SharedPreferences.Editor editor = preferences.edit();
		editor.putBoolean(registeredUserIDKey, registeredUser);
		editor.commit();
	}

	/**
	 * Retrieves the registeredUser setting.
	 * 
	 * @return the current registeredUser setting.
	 */
	public boolean getRegisteredUser() {
		return this.registeredUser;
	}

	/**
	 * Set the registeredUser setting.
	 * 
	 * @param next
	 *            the new registeredUser setting.
	 */
	public void setRegisteredUserAuto() {
		setRegisteredTime(System.currentTimeMillis());
		setRegisteredUser(true);
	}

	/**
	 * Retrieves the registeredUser setting.
	 * 
	 * @return the current registeredUser setting.
	 */
	public boolean getRegisteredUserNeedUpdate() {
		if (!this.registeredUser) {
			return true;
		} else {
			long now = System.currentTimeMillis();

//			if (getRegisteredTime() - now < FRTimes.ONE_WEEK_IN_MS * 1) {
//				return false;
//			}

			Calendar calNow = Calendar.getInstance();
			calNow.setTimeInMillis(now);
			int year = calNow.get(Calendar.YEAR);

			Calendar cal15Feb = Calendar.getInstance();
			cal15Feb.setTimeInMillis(now);
			cal15Feb.set(year, Calendar.FEBRUARY, 14, 1, 0);

			Calendar cal30Mai = Calendar.getInstance();
			cal15Feb.setTimeInMillis(now);
			cal15Feb.set(year, Calendar.MAY, 30, 1, 0);

			Calendar cal5July = Calendar.getInstance();
			cal15Feb.setTimeInMillis(now);
			cal15Feb.set(year, Calendar.JULY, 5, 1, 0);

			Calendar cal15Sept = Calendar.getInstance();
			cal15Sept.setTimeInMillis(now);
			cal15Sept.set(year, Calendar.SEPTEMBER, 14, 1, 0);

			Calendar cal23Dec = Calendar.getInstance();
			cal15Sept.setTimeInMillis(now);
			cal15Sept.set(year, Calendar.DECEMBER, 23, 1, 0);

			Calendar calReg = Calendar.getInstance();
			calReg.setTimeInMillis(getRegisteredTime());

			return compareNowAndRegistrationToThreshold(calReg, cal15Feb,
					calNow)
					|| compareNowAndRegistrationToThreshold(calReg, cal30Mai,
							calNow)
					|| compareNowAndRegistrationToThreshold(calReg, cal5July,
							calNow)
					|| compareNowAndRegistrationToThreshold(calReg, cal15Sept,
							calNow)
					|| compareNowAndRegistrationToThreshold(calReg, cal23Dec,
							calNow);
		}
	}

	/**
	 * Checks if the registration is before the given threshold, AND that now
	 * given is after the threshold.
	 * 
	 * @param calReg
	 *            calendar corresponding to the registration time.
	 * @param calTreshold
	 *            calendar corresponding to the threshold time.
	 * @param calNow
	 *            calendar corresponding to now time.
	 * @return
	 */
	private boolean compareNowAndRegistrationToThreshold(Calendar calReg,
			Calendar calTreshold, Calendar calNow) {
		if ((calReg.compareTo(calTreshold) < 0)
				&& (calTreshold.compareTo(calNow) < 0)) {
			return true;
		}
		return false;
	}

	/* TIMES */

	/**
	 * Return a <code>FRTimesClient</code> with the given context and formatters
	 * ready depending on the language or setting chosen in model.
	 * 
	 * @param context
	 *            application context for getString
	 * @return a FRTimesClient with appropriate context and formatters.
	 */
	public FRTimesClient getFRTimesClient(Context context) {
		return getFRTimesClient(context, getTimeLanguage());
	}

	/**
	 * Return a <code>FRTimesClient</code> with the given context and formatters
	 * ready depending on the language or setting chosen in model.
	 * <p>
	 * You should usually call the method WITHOUT the timeLanguage, and the
	 * model use the one selected in setting.
	 * 
	 * @param context
	 *            application context for getString
	 * @param timeLanguage
	 *            the language chosen (may be default).
	 * @return a FRTimesClient with appropriate context and formatters.
	 */
	private FRTimesClient getFRTimesClient(Context context,
			TimeLanguage timeLanguage) {

		// Formatting will use the device language settings
		// if it has been overridden, it will these one:
		// English format: Saturday, May 17, 2014
		// French format: samedi, 17 mai 2014

		// IT AFFECTS ONLY FORMATTING, NEVER THE LANGUAGE !!!

		Locale defaultLocale = Locale.getDefault();

		if (timeLanguage.equals(TimeLanguage.FRENCH)) {
			return new FRTimesClient(context, defaultLocale,
					"EEEE, d MMMM yyyy");
		}

		if (timeLanguage.equals(TimeLanguage.ENGLISH)) {
			return new FRTimesClient(context, defaultLocale,
					"EEEE, MMMM d, yyyy");
		}

		// default
		return new FRTimesClient(context, Locale.getDefault(), null);
	}

	/* COLORS */

	/**
	 * Stores the color-blind mode in a local variable (change must be done
	 * trhough the correct setter to be stored).
	 */
	private ColorBlindMode colorBlindMode = ColorBlindMode.DEFAULT;

	/**
	 * Enum for color-blind modes. Available modes are:
	 * <p>
	 * {@link #DEFAULT}: small color-dots on the left.<br>
	 * {@link #DOTS_DISCOLORED}: color-dots are discolored according to
	 * color-blind vision. <br>
	 * {@link #DOTS_SYMBOL}: dots are symbol showing the state. <br>
	 * {@link #DOTS_SYMBOL_LINEFULL}: the line is fully colored, with the dots
	 * symbol.<br>
	 * {@link #DOTS_SYMBOL_LINEFULL_DISCOLORED}: the line is fully colored with
	 * color-blind colors, with the dots symbol.<br>
	 * <br>
	 */
	public enum ColorBlindMode {
		DEFAULT, DOTS_DISCOLORED, DOTS_SYMBOL, DOTS_SYMBOL_LINEFULL, DOTS_SYMBOL_LINEFULL_DISCOLORED;
	}

	/**
	 * Set the {@link ColorBlindMode} and save it to persistent storage.
	 * 
	 * @param colorBlindMode
	 *            the new {@link ColorBlindMode}
	 */
	public void setColorBlindMode(ColorBlindMode colorBlindMode) {
		this.colorBlindMode = colorBlindMode;
		SharedPreferences.Editor editor = preferences.edit();
		editor.putString(colorBlindModeIDKey, colorBlindMode.name());
		editor.commit();
	}

	/**
	 * Set the {@link ColorBlindMode} to the switch settings according to the
	 * checkbox.
	 * 
	 * @param bool
	 *            if the checkbox is checked.
	 */
	public void setColorBlindModeBasicSwitch(boolean bool) {
		if (bool) {
			setColorBlindMode(ColorBlindMode.DOTS_DISCOLORED);
		} else {
			setColorBlindMode(ColorBlindMode.DEFAULT);
		}
	}

	/**
	 * Retrieves the {@link ColorBlindMode}.
	 * 
	 * @return the current {@link ColorBlindMode}.
	 */
	public ColorBlindMode getColorBlindMode() {
		return colorBlindMode;
	}

	/* ENDS OF PREFERENCES */
	/* COLORS METHODS */

	/**
	 * Return true if lines should be colored according to the
	 * {@link ColorBlindMode}.
	 * 
	 * @return true if lines should be colored.
	 */
	private boolean isColorLineFull() {
		if (colorBlindMode.equals(ColorBlindMode.DOTS_SYMBOL_LINEFULL)
				|| colorBlindMode
						.equals(ColorBlindMode.DOTS_SYMBOL_LINEFULL_DISCOLORED)) {
			return true;
		}
		return false;
	}

	/**
	 * Return true if dots should be displayed in front of room names, according
	 * to the {@link ColorBlindMode}.
	 * <p>
	 * Actually ALL modes display dots now, either a colored one or a symbol.
	 * 
	 * @return true if dots should be present.
	 */
	private boolean isColorColoredDots() {
		if (colorBlindMode.equals(ColorBlindMode.DEFAULT)
				|| colorBlindMode.equals(ColorBlindMode.DOTS_DISCOLORED)
				|| colorBlindMode.equals(ColorBlindMode.DOTS_SYMBOL)
				|| colorBlindMode.equals(ColorBlindMode.DOTS_SYMBOL_LINEFULL)
				|| colorBlindMode
						.equals(ColorBlindMode.DOTS_SYMBOL_LINEFULL_DISCOLORED)) {
			return true;
		}
		return false;
	}

	/**
	 * Return true if the current {@link ColorBlindMode} is for color-blind
	 * people.
	 * 
	 * @return
	 */
	private boolean isColorBlind() {
		if (colorBlindMode.equals(ColorBlindMode.DOTS_DISCOLORED)
				|| colorBlindMode
						.equals(ColorBlindMode.DOTS_SYMBOL_LINEFULL_DISCOLORED)) {
			return true;
		}
		return false;
	}

	/**
	 * Return the appropriate color according to the {@link Occupation} given,
	 * and the {@link FreeRoomModel} color settings ({@link ColorBlindMode}).
	 * 
	 * @param mOccupation
	 *            occupation for a given room (for multiple periods)
	 * @return the appropriate color
	 */
	public int getColorLine(FRRoomOccupancy mOccupancy) {
		if (!isColorLineFull()) {
			return COLOR_TRANSPARENT;
		}

		if (mOccupancy.isIsFreeAtLeastOnce()) {
			if (mOccupancy.isIsOccupiedAtLeastOnce()) {
				return getColorOrange();
			} else {
				return getColorGreen();
			}
		} else {
			if (mOccupancy.isIsOccupiedAtLeastOnce()) {
				return getColorRed();
			} else {
				// default
				return COLOR_DEFAULT;
			}
		}
	}

	/**
	 * Return the appropriate color according to the {@link FRPeriodOccupation}
	 * given, and the {@link FreeRoomModel} color settings (
	 * {@link ColorBlindMode}).
	 * 
	 * @param mActualOccupation
	 *            actual occupation for a given period
	 * @return the appropriate color
	 */
	public int getColorLine(FRPeriodOccupation mActualOccupation) {
		if (!isColorLineFull()) {
			return COLOR_TRANSPARENT;
		}

		if (mActualOccupation.isAvailable()) {
			return getColorGreen();
		} else {
			return getColorRed();
		}
	}

	/**
	 * Return the appropriate color dot {@link Drawable} according to the
	 * {@link FRRoomOccupancy} given, and the {@link FreeRoomModel} color
	 * settings ( {@link ColorBlindMode}).
	 * 
	 * @param mOccupancy
	 *            occupancy for a given room (for multiple periods)
	 * @return a color dot {@link Drawable}
	 */
	public int getColoredDotDrawable(FRRoomOccupancy mOccupancy) {
		if (!isColorColoredDots()) {
			return R.drawable.freeroom_ic_dot_empty;
		}

		if (mOccupancy.isIsFreeAtLeastOnce()) {
			if (mOccupancy.isIsOccupiedAtLeastOnce()) {
				return getColoredDotOrange();
			} else {
				return getColoredDotGreen();
			}
		} else {
			if (mOccupancy.isIsOccupiedAtLeastOnce()) {
				return getColoredDotRed();
			} else {
				// default: should not appear!
				return getColoredDotUnknown();
			}
		}
	}

	/**
	 * Return the correct color dot {@link Drawable} according to the
	 * {@link FRPeriodOccupation} given, and the {@link FreeRoomModel} color
	 * settings ({@link ColorBlindMode}).
	 * 
	 * @param mActualOccupation
	 *            actual occupation for a given period
	 * @return a color dot {@link Drawable}
	 */
	public int getColoredDotDrawable(FRPeriodOccupation mActualOccupation) {
		if (!isColorColoredDots()) {
			return R.drawable.freeroom_ic_dot_empty;
		}
		if (mActualOccupation.isAvailable()) {
			return getColoredDotGreen();
		} else {
			return getColoredDotRed();
		}
	}

	/**
	 * Return the HIGHLIGHT {@link Color}.
	 * <p>
	 * This is constant and DOESN'T depend on the {@link ColorBlindMode} and
	 * {@link #isColorBlind()} method.
	 * 
	 * @return the accurate HIGHLIGHT color.
	 */
	public int getColorHighlight() {
		return COLOR_HEADER_HIGHLIGHT;
	}

	/**
	 * Return the TRANSPARENT {@link Color}.
	 * <p>
	 * This is constant and DOESN'T depend on the {@link ColorBlindMode} and
	 * {@link #isColorBlind()} method.
	 * 
	 * @return the accurate HIGHLIGHT color.
	 */
	public int getColorTransparent() {
		return COLOR_TRANSPARENT;
	}

	/**
	 * Return the GREEN {@link Color} according to the {@link ColorBlindMode}
	 * and {@link #isColorBlind()} method.
	 * 
	 * @return the accurate GREEN color.
	 */
	private int getColorGreen() {
		if (isColorBlind()) {
			return COLOR_GREEN_FREE_CB;
		}
		return COLOR_GREEN_FREE;
	}

	/**
	 * Return the RED {@link Color} according to the {@link ColorBlindMode} and
	 * {@link #isColorBlind()} method.
	 * 
	 * @return the accurate RED color.
	 */
	private int getColorRed() {
		if (isColorBlind()) {
			return COLOR_RED_OCCUPIED_CB;
		}
		return COLOR_RED_OCCUPIED;
	}

	/**
	 * Return the ORANGE {@link Color} according to the {@link ColorBlindMode}
	 * and {@link #isColorBlind()} method.
	 * 
	 * @return the accurate ORANGE color.
	 */
	private int getColorOrange() {
		if (isColorBlind()) {
			return COLOR_ORANGE_ATLEASTONCE_CB;
		}
		return COLOR_ORANGE_ATLEASTONCE;
	}

	/**
	 * Return the "grey" dot, indicating the room occupancy is UNKNOWN,
	 * according to color preferences.
	 * <p>
	 * Note: this option is made private because it depends on model settings.
	 * This should be called only if the color is sure. For the color depending
	 * of the {@link FRRoomOccupancy}, call {@link getColorDrawable(Occupancy)}
	 * . For the color depending of the {@link FRPeriodOccupation}, call
	 * {@link #getColoredDotDrawable(FRPeriodOccupation)}
	 * 
	 * @return the dot indicating "UNKNOWN".
	 */
	private int getColoredDotUnknown() {
		if (colorBlindMode.equals(ColorBlindMode.DOTS_SYMBOL)
				|| colorBlindMode.equals(ColorBlindMode.DOTS_SYMBOL_LINEFULL)
				|| colorBlindMode
						.equals(ColorBlindMode.DOTS_SYMBOL_LINEFULL_DISCOLORED)) {
			return R.drawable.freeroom_ic_dot_grey_symbol;
		} else {
			// grey is the same for color-blind and default mode
			return R.drawable.freeroom_ic_dot_grey;
		}
	}

	/**
	 * Return the "red" dot, indicating the room is OCCUPIED, according to color
	 * preferences.
	 * <p>
	 * Note: this option is made private because it depends on model settings.
	 * This should be called only if the color is sure. For the color depending
	 * of the {@link FRRoomOccupancy}, call {@link getColorDrawable(Occupancy)}
	 * . For the color depending of the {@link FRPeriodOccupation}, call
	 * {@link #getColoredDotDrawable(FRPeriodOccupation)}
	 * 
	 * @return the dot indicating "OCCUPIED".
	 */
	private int getColoredDotRed() {
		if (colorBlindMode.equals(ColorBlindMode.DOTS_DISCOLORED)) {
			return R.drawable.freeroom_ic_dot_red_cb;
		} else if (colorBlindMode.equals(ColorBlindMode.DOTS_SYMBOL)
				|| colorBlindMode.equals(ColorBlindMode.DOTS_SYMBOL_LINEFULL)
				|| colorBlindMode
						.equals(ColorBlindMode.DOTS_SYMBOL_LINEFULL_DISCOLORED)) {
			return R.drawable.freeroom_ic_dot_red_symbol;
		} else {
			return R.drawable.freeroom_ic_dot_red;
		}
	}

	/**
	 * Return the "green" dot, indicating the room is FREE, according to color
	 * preferences.
	 * <p>
	 * Note: this option is made private because it depends on model settings.
	 * This should be called only if the color is sure. For the color depending
	 * of the {@link FRRoomOccupancy}, call {@link getColorDrawable(Occupancy)}
	 * . For the color depending of the {@link FRPeriodOccupation}, call
	 * {@link #getColoredDotDrawable(FRPeriodOccupation)}
	 * 
	 * @return the dot indicating "FREE".
	 */
	private int getColoredDotGreen() {
		if (colorBlindMode.equals(ColorBlindMode.DOTS_DISCOLORED)) {
			return R.drawable.freeroom_ic_dot_green_cb;
		} else if (colorBlindMode.equals(ColorBlindMode.DOTS_SYMBOL)
				|| colorBlindMode.equals(ColorBlindMode.DOTS_SYMBOL_LINEFULL)
				|| colorBlindMode
						.equals(ColorBlindMode.DOTS_SYMBOL_LINEFULL_DISCOLORED)) {
			return R.drawable.freeroom_ic_dot_green_symbol;
		} else {
			return R.drawable.freeroom_ic_dot_green;
		}
	}

	/**
	 * Return the "orange" dot, indicating the room is PARTIALLY OCCUPIED,
	 * according to color preferences.
	 * <p>
	 * Note: this option is made private because it depends on model settings.
	 * This should be called only if the color is sure. For the color depending
	 * of the {@link FRRoomOccupancy}, call {@link getColorDrawable(Occupancy)}
	 * . For the color depending of the {@link FRPeriodOccupation}, call
	 * {@link #getColoredDotDrawable(FRPeriodOccupation)}
	 * 
	 * @return the dot indicating "PARTIALLY OCCUPIED".
	 */
	private int getColoredDotOrange() {
		if (colorBlindMode.equals(ColorBlindMode.DOTS_DISCOLORED)) {
			return R.drawable.freeroom_ic_dot_orange_cb;
		} else if (colorBlindMode.equals(ColorBlindMode.DOTS_SYMBOL)
				|| colorBlindMode.equals(ColorBlindMode.DOTS_SYMBOL_LINEFULL)
				|| colorBlindMode
						.equals(ColorBlindMode.DOTS_SYMBOL_LINEFULL_DISCOLORED)) {
			return R.drawable.freeroom_ic_dot_orange_symbol;
		} else {
			return R.drawable.freeroom_ic_dot_orange;
		}
	}

	/**
	 * Return the id of the right image based on the ratio given.
	 * <p>
	 * WARNING: VALUES ARE DEFINED ONLY THERE.
	 * 
	 * @param ratio
	 *            ratio of WorstCaseProbableOccupancy
	 * @return the correct reference to the image.
	 */
	public int getImageFromRatioOccupation(double ratio) {
		// at first presence we already change the image
		double ratioLowg = 0.00;
		double ratioLow = 0.05;
		double ratioMedg = 0.10;
		double ratioMed = 0.15;
		double ratioHighg = 0.20;
		double ratioHigh = 0.25;
		int id = R.drawable.freeroom_ic_occupation_empty;
		if (ratio < 0) {
			id = R.drawable.freeroom_ic_occupation_unknown;
		}
		if (ratio > ratioLowg) {
			id = R.drawable.freeroom_ic_occupation_lowg;
			if (ratio > ratioLow) {
				id = R.drawable.freeroom_ic_occupation_low;
				if (ratio > ratioMedg) {
					id = R.drawable.freeroom_ic_occupation_medg;
					if (ratio > ratioMed) {
						id = R.drawable.freeroom_ic_occupation_med;
						if (ratio > ratioHighg) {
							id = R.drawable.freeroom_ic_occupation_highg;
							if (ratio > ratioHigh) {
								id = R.drawable.freeroom_ic_occupation_high;
							}
						}
					}
				}
			}
		}
		return id;
	}

	/* END OF COLORS */
	/* INTERACTION WITH FILESYSTEM */

	/**
	 * Writes the give Object to the file given.
	 * 
	 * @param filename
	 *            the file to write on.
	 * @param object
	 *            the object to write on file.
	 * @return true if written successfully, false if any error occured.
	 */
	private boolean writeObjectToFile(String filename, Object object) {
		FileOutputStream fos;
		try {
			fos = context.openFileOutput(filename, Context.MODE_PRIVATE);
			ObjectOutputStream oos = new ObjectOutputStream(fos);
			oos.writeObject(object);
			oos.close();
			fos.close();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
			return false;
		} catch (IOException e) {
			e.printStackTrace();
			return false;
		}
		return true;
	}

	/**
	 * Read a single object from the file given.
	 * 
	 * @param filename
	 *            file on which to read
	 * @return a single object read from file, null if an error occured.
	 */
	private Object readObjectFromFile(String filename) {
		FileInputStream fos;
		try {
			fos = context.openFileInput(filename);
			ObjectInputStream oin = new ObjectInputStream(fos);
			Object o = oin.readObject();
			oin.close();
			return o;
		} catch (FileNotFoundException e) {
			e.printStackTrace();

		} catch (OptionalDataException e) {
			e.printStackTrace();
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return null;
	}

	/* NEW FAVORITES IMPLEMENTATION AS OF MAY 8 2014 */

	private OrderMapListFew<String, List<FRRoom>, FRRoom> favorites;
	private String FAVORITES_FILENAME = "freeroom_favorites_file.dat";

	/**
	 * Retrieves the favorites object from persistent file.
	 * 
	 * @return true if successful.
	 */
	private boolean retrieveFavorites() {
		Object read = readObjectFromFile(FAVORITES_FILENAME);
		if (read instanceof OrderMapListFew<?, ?, ?>) {
			favorites = (OrderMapListFew<String, List<FRRoom>, FRRoom>) read;
			return true;
		} else {
			return initFavorites();
		}
	}

	/**
	 * Save the favorites object to persistent file.
	 * 
	 * @return true if successful.
	 */
	private boolean saveFavorites() {
		return writeObjectToFile(FAVORITES_FILENAME, favorites);
	}

	/**
	 * Get a reference to the map of favorites. It will load from the persistent
	 * file if not loaded so far. It will construct a new favorites structure
	 * and save it to file if none is found.
	 * 
	 * @return a reference to the map of favorites.
	 */
	public OrderMapListFew<String, List<FRRoom>, FRRoom> getFavorites() {
		if (favorites == null) {
			if (!retrieveFavorites()) {
				initFavorites();
			}
		}
		return favorites;
	}

	/**
	 * Init the favorites structure to an empty structure and save it to file.
	 * Useful only at first launch.
	 * <p>
	 * NOTE that this function affects a NEW structures, so it will change
	 * reference, therefore ALL UI based on this will become invalid!
	 * 
	 * @return true if written to file successful.
	 */
	private boolean initFavorites() {
		favorites = new OrderMapListFew<String, List<FRRoom>, FRRoom>(50);
		favorites.setAvailableLimit(Integer.MAX_VALUE);
		return saveFavorites();
	}

	/**
	 * Reset the favorites structure by the clearing the EXISTING structure and
	 * save it to file.
	 * 
	 * <p>
	 * Note: calling this if the structure don't exists now, it will create a
	 * new one using {@link #initFavorites()}.
	 * 
	 * @return true if written to file successful.
	 */
	public boolean resetFavorites() {
		if (favorites == null) {
			initFavorites();
		}
		favorites.clear();
		return saveFavorites();
	}

	/**
	 * Add a room to the favorites, and save the favorites.
	 * 
	 * @param mRoom
	 *            room to add to favorites
	 * @return true if successful
	 */
	public boolean addFavorite(FRRoom mRoom) {
		// ensure favorites structure exists.
		getFavorites();
		// cannot add twice!
		if (!isFavorite(mRoom)) {
			String key = getBuildingKeyLabel(mRoom);
			List<FRRoom> list = null;
			if (favorites.containsKey(key)) {
				list = favorites.get(key);
			} else {
				list = new ArrayList<FRRoom>();
			}
			boolean flag1 = list.add(mRoom);
			favorites.put(key, list);
			boolean flag2 = saveFavorites();
			return flag1 && flag2;
		}
		return false;
	}

	/**
	 * Checks if a room is in the favorites.
	 * 
	 * @param mRoom
	 *            room to add to favorites
	 * @return true if successful
	 */
	public boolean isFavorite(FRRoom mRoom) {
		// ensure favorites structure exists.
		getFavorites();
		String key = getBuildingKeyLabel(mRoom);
		List<FRRoom> list = null;
		if (favorites.containsKey(key)) {
			list = favorites.get(key);
			Iterator<FRRoom> iter = list.iterator();
			while (iter.hasNext()) {
				if (iter.next().getUid().equals(mRoom.getUid())) {
					return true;
				}
			}
			return false;
		} else {
			return false;
		}
	}

	/**
	 * Removes a room from the favorites, and save the favorites.
	 * 
	 * @param mRoom
	 *            room to remove from the favorites.
	 * @return true if successful.
	 */
	public boolean removeFavorite(FRRoom mRoom) {
		try {
			// ensure favorites structure exists.
			getFavorites();

			String key = getBuildingKeyLabel(mRoom);
			List<FRRoom> list = null;
			if (favorites.containsKey(key)) {
				list = favorites.get(key);
				boolean flag1 = list.remove(mRoom);
				Iterator<FRRoom> iter = list.iterator();
				while (iter.hasNext()) {
					FRRoom mRoomSel = iter.next();
					if (mRoomSel.getUid().equals(mRoom.getUid())) {
						flag1 = list.remove(mRoomSel);
					}
				}
				if (list.isEmpty()) {
					favorites.remove(key);
				} else {
					favorites.put(key, list);
				}
				boolean flag2 = saveFavorites();
				return flag1 && flag2;
			} else {
				return true;
			}
		} catch (ConcurrentModificationException e) {
			System.err.println("cannot remove fav due "
					+ "to concurrent modification error, "
					+ "but killing the app was avoided!");
			e.printStackTrace();
			return false;
		}
	}

	/**
	 * Get the building key of the room, from its building label if set, or from
	 * its door code otherwise.
	 * <p>
	 * WARNING: THIS MAY BE INCORRECT IF BUILDING LABEL IS NOT SET.
	 * 
	 * @param mRoom
	 *            the given room
	 * @return the room's building name.
	 */
	public String getBuildingKeyLabel(FRRoom mRoom) {
		String key = mRoom.getBuilding_name();
		if (key == null || key.length() == 0) {
			key = getBuilding(mRoom.getDoorCode());
			if (key == null || key.length() == 0) {
				key = "unknown";
			}
		}
		return key;
	}

	/**
	 * WARNING: USE WITH GREAT CARE. PREFER BUILDING_LABLE IN ALL CASES.
	 * <p>
	 * Returns the building part in mDoorCode. <br>
	 * 
	 * Door codes should be like PH D2 398 with PH the building D2 the zone 398
	 * the number (including floor) <br>
	 * 
	 * It works ONLY if spaces are correctly set! <br>
	 * 
	 * @param mDoorCode
	 * @return
	 */
	private String getBuilding(String mDoorCode) {
		mDoorCode = mDoorCode.trim();
		int firstSpace = mDoorCode.indexOf(" ");
		if (firstSpace > 0) {
			mDoorCode = mDoorCode.substring(0, firstSpace);
		}
		return mDoorCode;
	}

	/* STORAGE OF PREV. REQUEST */

	private SetArrayList<FRRequestDetails> previousRequestDetails;
	private String PREV_REQ_FILENAME = "freeroom_prev_req_file.dat";

	/**
	 * Retrieves the previous request object from persistent file.
	 * 
	 * @return true if successful.
	 */
	private boolean retrievePreviousRequest() {
		Object read = readObjectFromFile(PREV_REQ_FILENAME);
		if (read instanceof SetArrayList<?>) {
			previousRequestDetails = (SetArrayList<FRRequestDetails>) read;
			return true;
		} else {
			return initPreviousRequest();
		}
	}

	/**
	 * Save the previous request object to persistent file.
	 * 
	 * @return true if successful.
	 */
	private boolean savePreviousRequest() {
		return writeObjectToFile(PREV_REQ_FILENAME, previousRequestDetails);
	}

	/**
	 * Get a reference to the list of previous request. It will load from the
	 * persistent file if not loaded so far. It will construct a new previous
	 * request structure and save it to file if none is found.
	 * 
	 * @return a reference to the list of previous request.
	 */
	public List<FRRequestDetails> getPreviousRequest() {
		if (previousRequestDetails == null) {
			if (!retrievePreviousRequest()) {
				resetPreviousRequest();
			}
		}
		return previousRequestDetails;
	}

	/**
	 * Reset the EXISTING previous request structure to an empty structure and
	 * save it to file.
	 * <p>
	 * Note: calling this if the structure don't exists now, it will create a
	 * new one.
	 * 
	 * @return true if written to file successful.
	 */
	public boolean resetPreviousRequest() {
		if (previousRequestDetails == null) {
			initPreviousRequest();
		}
		previousRequestDetails.clear();
		return savePreviousRequest();
	}

	/**
	 * Init the previous request structure to a NEW empty structure and save it
	 * to file. Useful only at first launch.
	 * <p>
	 * NOTE that this function affects a NEW structures, so it will change
	 * reference, therefore ALL UI based on this will become invalid!
	 * 
	 * @return true if written to file successful.
	 */
	private boolean initPreviousRequest() {
		previousRequestDetails = new SetArrayList<FRRequestDetails>(
				getPreviousRequestNumber(), true);
		return savePreviousRequest();
	}

	/**
	 * Add a request to the previous request, and save the previous request.
	 * 
	 * @param request
	 *            request to add to previous request.
	 * @return true if successful
	 */
	private boolean addPreviousRequest(FRRequestDetails request) {
		// ensure favorites structure exists.
		getPreviousRequest();
		// if the request was already present, we remove it.
		if (previousRequestDetails.contains(request)) {
			previousRequestDetails.remove(request);
		}
		// and adding at the start!
		previousRequestDetails.addFirst(request);
		// if too much elements, we remove the end.
		while (previousRequestDetails.size() > getPreviousRequestNumber()) {
			previousRequestDetails.removeLast();
		}
		return savePreviousRequest();
	}

	/**
	 * Remove a request from the previous request, and save the previous
	 * request.
	 * 
	 * @param position
	 *            position of request to delete.
	 * @return true if successful
	 */
	public boolean removeRequest(int position) {
		// ensure favorites structure exists.
		getPreviousRequest();
		try {
			previousRequestDetails.remove(position);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return savePreviousRequest();
	}

	/* PREVIOUS REQUEST */

	/*
	 * methods are ordered by functionality in model !! PLEASE insert your new
	 * method in an existing category or create a new one with two separators
	 * like this one!
	 */
	// ********** END OF FILE **********
}