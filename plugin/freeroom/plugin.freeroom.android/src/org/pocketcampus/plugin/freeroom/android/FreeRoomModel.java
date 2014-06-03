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
import java.util.ConcurrentModificationException;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.TreeSet;

import org.pocketcampus.android.platform.sdk.core.IView;
import org.pocketcampus.android.platform.sdk.core.PluginModel;
import org.pocketcampus.plugin.freeroom.R;
import org.pocketcampus.plugin.freeroom.android.iface.IFreeRoomModel;
import org.pocketcampus.plugin.freeroom.android.iface.IFreeRoomView;
import org.pocketcampus.plugin.freeroom.android.utils.FRRequestDetails;
import org.pocketcampus.plugin.freeroom.android.utils.FRTimesClient;
import org.pocketcampus.plugin.freeroom.android.utils.OrderMapListFew;
import org.pocketcampus.plugin.freeroom.android.utils.SetArrayList;
import org.pocketcampus.plugin.freeroom.shared.ActualOccupation;
import org.pocketcampus.plugin.freeroom.shared.FRPeriod;
import org.pocketcampus.plugin.freeroom.shared.FRRoom;
import org.pocketcampus.plugin.freeroom.shared.MessageFrequency;
import org.pocketcampus.plugin.freeroom.shared.Occupancy;

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
	 * Keys for the parameters.
	 */
	private final String anonymIDKey = "anonymIDKey";
	private final String homeBehaviourRoomIDKey = "homeBehaviourRoomIDKey";
	private final String homeBehaviourTimeIDKey = "homeBehaviourTimeIDKey";
	private final String timeLanguageIDKey = "timeLanguageIDKey";
	private final String displayTimePrefixIDKey = "displayTimePrefixIDKey";
	private final String minutesRequestTimeOutIDKey = "minutesRequestTimeOutIDKey";
	private final String previousRequestNumberIDKey = "previousRequestNumberIDKey";
	private final String previousRequestWeeksIDKey = "previousRequestWeeksIDKey";
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
	 * Default group access, using to reset.
	 */
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
	private OrderMapListFew<String, List<?>, Occupancy> occupancyByBuilding;

	/**
	 * Storing the <code>WorkingOccupancy</code> of people who indicate their
	 * are going to work there.
	 */
	private List<MessageFrequency> listMessageFrequency = new ArrayList<MessageFrequency>();

	/**
	 * Reference to application context.
	 */
	private Context context;

	/**
	 * Storage for basic preferences, parameters and so on.
	 */
	private SharedPreferences preferences;

	/**
	 * Constructor with reference to the context.
	 * 
	 * We need the context to be able to instantiate the SharedPreferences
	 * object in order to use persistent storage.
	 * 
	 * @param context
	 *            is the Application Context.
	 */
	public FreeRoomModel(Context context) {
		preferences = context.getSharedPreferences(PREF_USER_DETAILS_KEY,
				Context.MODE_PRIVATE);
		homeBehaviourRoom = HomeBehaviourRoom.valueOf(preferences.getString(
				homeBehaviourRoomIDKey, homeBehaviourRoom.name()));
		homeBehaviourTime = HomeBehaviourTime.valueOf(preferences.getString(
				homeBehaviourTimeIDKey, homeBehaviourTime.name()));
		timeLanguage = TimeLanguage.valueOf(preferences.getString(
				timeLanguageIDKey, timeLanguage.name()));
		displayTimePrefix = preferences.getBoolean(displayTimePrefixIDKey,
				displayTimePrefix);
		minutesRequestTimeOut = preferences.getInt(minutesRequestTimeOutIDKey,
				minutesRequestTimeOut);
		previousRequestWeeks = preferences.getInt(previousRequestWeeksIDKey,
				previousRequestWeeks);
		previousRequestNumber = preferences.getInt(previousRequestNumberIDKey,
				previousRequestNumber);
		registeredUser = preferences.getBoolean(registeredUserIDKey,
				registeredUser);
		occupancyByBuilding = new OrderMapListFew<String, List<?>, Occupancy>(
				30);
		// generates the anonym ID at first launch time
		getAnonymID();
		groupAccess = preferences.getInt(groupAccessIDKey, groupAccess);
		registeredTime = preferences.getLong(registeredTimeIDKey,
				registeredTime);
		timePickersPref = TimePickersPref.valueOf(preferences.getString(
				timePickersPrefIDKey, timePickersPref.name()));
		advancedTime = preferences.getBoolean(advancedTimeIDKey, advancedTime);
		colorBlindMode = ColorBlindMode.valueOf(preferences.getString(
				colorBlindModeIDKey, colorBlindMode.name()));
		this.context = context;
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
			Map<String, List<Occupancy>> occupancyOfRooms) {
		occupancyByBuilding.clear();
		// keys are ordered!
		TreeSet<String> keySetOrder = new TreeSet<String>(
				occupancyOfRooms.keySet());
		List<String> buildings = getOrderedBuildings();
		for (String building : buildings) {
			List<Occupancy> list = occupancyOfRooms.get(building);
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
	public OrderMapListFew<String, List<?>, Occupancy> getOccupancyResults() {
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
		mListeners.autoCompletedUpdated();
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
			List<MessageFrequency> listMessageFrequency) {
		Iterator<MessageFrequency> iter = this.listMessageFrequency.iterator();
		System.out.println("there:");
		while (iter.hasNext()) {
			System.out.println(iter.next());
		}
		iter = listMessageFrequency.iterator();
		System.out.println("added:");
		while (iter.hasNext()) {
			System.out.println(iter.next());
		}
		this.listMessageFrequency.clear();
		this.listMessageFrequency.addAll(listMessageFrequency);
		mListeners.workingMessageUpdated();
	}

	/**
	 * Retrieves the stored <code>List</code> of <code>WorkingOccupancy</code>.
	 * 
	 * @return
	 */
	public List<MessageFrequency> getListMessageFrequency() {
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
		if (save) {
			// write in history each time a request is set.
			addPreviousRequest(request);
		}
		this.mFRRequest = request;
	}

	private Occupancy occupancy;

	public void setDisplayedOccupancy(Occupancy occupancy) {
		this.occupancy = occupancy;
	}

	public Occupancy getDisplayedOccupancy() {
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
	 * Stores the homeBehaviourRoom parameters.
	 * <p>
	 * Default: HomeBehaviourRoom.ANYFREEROOM (NOT Favorites!!!).
	 */
	private HomeBehaviourRoom homeBehaviourRoom = HomeBehaviourRoom.ANYFREEROOM;

	/**
	 * Set the homeBehaviourRoom parameters.
	 * 
	 * @param next
	 *            the new homeBehaviourRoom parameters.
	 */
	public void setHomeBehaviourRoom(HomeBehaviourRoom next) {
		this.homeBehaviourRoom = next;
		SharedPreferences.Editor editor = preferences.edit();
		editor.putString(homeBehaviourRoomIDKey, homeBehaviourRoom.name());
		editor.commit();
	}

	/**
	 * Retrieves the homeBehaviourRoom parameters.
	 * 
	 * @return the current homeBehaviourRoom parameters.
	 */
	public HomeBehaviourRoom getHomeBehaviourRoom() {
		return this.homeBehaviourRoom;
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
	 * Stores the homeBehaviourTime parameters.
	 * <p>
	 * Default: HomeBehaviourTime.CURRENT_TIME
	 */
	private HomeBehaviourTime homeBehaviourTime = HomeBehaviourTime.CURRENT_TIME;

	/**
	 * Set the homeBehaviourTime parameters.
	 * 
	 * @param next
	 *            the new homeBehaviourTime parameters.
	 */
	public void setHomeBehaviourTime(HomeBehaviourTime next) {
		this.homeBehaviourTime = next;
		SharedPreferences.Editor editor = preferences.edit();
		editor.putString(homeBehaviourTimeIDKey, homeBehaviourTime.name());
		editor.commit();
	}

	/**
	 * Retrieves the homeBehaviourTime parameters.
	 * 
	 * @return the current homeBehaviourTime parameters.
	 */
	public HomeBehaviourTime getHomeBehaviourTime() {
		return this.homeBehaviourTime;
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
	 * Stores the timeLanguage parameters.
	 * <p>
	 * Default: TimeLanguage.DEFAULT (will choose your language if defined, or
	 * english otherwise).
	 */
	private TimeLanguage timeLanguage = TimeLanguage.DEFAULT;

	/**
	 * Set the timeLanguage parameters.
	 * 
	 * @param tl
	 *            the new timeLanguage parameters.
	 */
	public void setTimeLanguage(TimeLanguage tl) {
		this.timeLanguage = tl;
		SharedPreferences.Editor editor = preferences.edit();
		editor.putString(timeLanguageIDKey, timeLanguage.name());
		editor.commit();
	}

	/**
	 * Retrieves the timeLanguage parameters.
	 * 
	 * @return the current timeLanguage parameters.
	 */
	public TimeLanguage getTimeLanguage() {
		return this.timeLanguage;
	}

	/**
	 * Stores the displayTimePrefix parameters.
	 * <p>
	 * Default: true.
	 */
	private boolean displayTimePrefix = true;

	/**
	 * Set the displayTimePrefix parameters.
	 * 
	 * @param next
	 *            the new displayTimePrefix parameters.
	 */
	public void setDisplayTimePrefix(boolean next) {
		this.displayTimePrefix = next;
		SharedPreferences.Editor editor = preferences.edit();
		editor.putBoolean(displayTimePrefixIDKey, displayTimePrefix);
		editor.commit();
	}

	/**
	 * Retrieves the displayTimePrefix parameters.
	 * 
	 * @return the current displayTimePrefix parameters.
	 */
	public boolean getDisplayTimePrefix() {
		return this.displayTimePrefix;
	}

	/**
	 * # minutes before a request becomes invalid for refreshing.
	 * <p>
	 * Default: 5 minutes.
	 */
	private int minutesRequestTimeOut = 5;

	/**
	 * # minutes before a request becomes invalid for refreshing.
	 * 
	 * @return the minutesRequestTimeOut
	 */
	public int getMinutesRequestTimeOut() {
		return minutesRequestTimeOut;
	}

	/**
	 * # minutes before a request becomes invalid for refreshing.
	 * 
	 * @param minutesRequestTimeOut
	 *            the minutesRequestTimeOut to set
	 */
	public void setMinutesRequestTimeOut(int minutesRequestTimeOut) {
		this.minutesRequestTimeOut = minutesRequestTimeOut;
		SharedPreferences.Editor editor = preferences.edit();
		editor.putInt(minutesRequestTimeOutIDKey, minutesRequestTimeOut);
		editor.commit();
	}

	/**
	 * # weeks before a previous requests are deleted.
	 * <p>
	 * Default: 15 weeks (1 semester run).
	 */
	private int previousRequestWeeks = 15;

	/**
	 * # weeks before a previous requests are deleted.
	 * 
	 * @return the previousRequestWeeks
	 */
	public int getPreviousRequestWeeks() {
		return previousRequestWeeks;
	}

	/**
	 * # weeks before a previous requests are deleted.
	 * 
	 * @param previousRequestWeeks
	 *            the previousRequestWeeks to set
	 */
	public void setPreviousRequestWeeks(int previousRequestWeeks) {
		this.previousRequestWeeks = previousRequestWeeks;
		SharedPreferences.Editor editor = preferences.edit();
		editor.putInt(previousRequestWeeksIDKey, previousRequestWeeks);
		editor.commit();
	}

	/**
	 * # of previous request before previous requests are deleted.
	 * <p>
	 * Default: 20 requests.
	 */
	private int previousRequestNumber = 20;

	/**
	 * # of previous request before previous requests are deleted.
	 * 
	 * @return the previousRequestNumber
	 */
	public int getPreviousRequestNumber() {
		return previousRequestNumber;
	}

	/**
	 * # of previous request before previous requests are deleted.
	 * 
	 * @param previousRequestNumber
	 *            the previousRequestNumber to set
	 */
	public void setPreviousRequestNumber(int previousRequestNumber) {
		this.previousRequestNumber = previousRequestNumber;
		SharedPreferences.Editor editor = preferences.edit();
		editor.putInt(previousRequestNumberIDKey, previousRequestNumber);
		editor.commit();
	}

	/**
	 * Stores the advancedTime parameters.
	 * <p>
	 * Default: false.
	 */
	private boolean advancedTime = false;

	/**
	 * Set the advancedTime parameters.
	 * 
	 * @param next
	 *            the new advancedTime parameters.
	 */
	public void setAdvancedTime(boolean next) {
		this.advancedTime = next;
		SharedPreferences.Editor editor = preferences.edit();
		editor.putBoolean(advancedTimeIDKey, advancedTime);
		editor.commit();
	}

	/**
	 * Retrieves the advancedTime parameters.
	 * 
	 * @return the current advancedTime parameters.
	 */
	public boolean getAdvancedTime() {
		return this.advancedTime;
	}

	/**
	 * Stores the registeredTime parameters.
	 * <p>
	 * Default: 0.
	 */
	private long registeredTime = 0;

	/**
	 * Set the registeredTime parameters.
	 * 
	 * @param next
	 *            the new registeredTime parameters.
	 */
	private void setRegisteredTime(long next) {
		this.registeredTime = next;
		SharedPreferences.Editor editor = preferences.edit();
		editor.putLong(registeredTimeIDKey, registeredTime);
		editor.commit();
	}

	/**
	 * Declaration of TimePickersPref type supported.
	 */
	public enum TimePickersPref {
		PICKERS, ARROWS, BOTH;
	}

	/**
	 * Stores the timeLanguage parameters.
	 * <p>
	 * Default: TimeLanguage.PICKERS
	 */
	private TimePickersPref timePickersPref = TimePickersPref.PICKERS;

	/**
	 * Set the timePickersPref parameters.
	 * 
	 * @param tl
	 *            the new timePickersPref parameters.
	 */
	public void setTimePickersPref(TimePickersPref tl) {
		this.timePickersPref = tl;
		SharedPreferences.Editor editor = preferences.edit();
		editor.putString(timePickersPrefIDKey, timePickersPref.name());
		editor.commit();
	}

	/**
	 * Retrieves the timePickersPref parameters.
	 * 
	 * @return the current timePickersPref parameters.
	 */
	public TimePickersPref getTimePickersPref() {
		return this.timePickersPref;
	}

	/**
	 * Stores the group access the user is registered for.
	 * <p>
	 * Default: {@link #DEFAULT_GROUP_ACCESS}.
	 */
	private int groupAccess = DEFAULT_GROUP_ACCESS;

	/**
	 * Retrieve the group access the user is registered for.
	 * 
	 * @return the previousRequestNumber
	 */
	public int getGroupAccess() {
		return groupAccess;
	}

	/**
	 * Set the group access the user is registered for.
	 * 
	 * @param previousRequestNumber
	 *            the previousRequestNumber to set
	 */
	public void setGroupAccess(int groupAccess) {
		this.groupAccess = groupAccess;
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
	 * Retrieves the registeredTime parameters.
	 * 
	 * @return the current registeredTime parameters.
	 */
	public long getRegisteredTime() {
		return this.registeredTime;
	}

	/**
	 * Stores the registeredUser parameters.
	 * <p>
	 * Default: false.
	 */
	private boolean registeredUser = false;

	/**
	 * Set the registeredUser parameters.
	 * 
	 * @param next
	 *            the new registeredUser parameters.
	 */
	public void setRegisteredUser(boolean next) {
		this.registeredUser = next;
		SharedPreferences.Editor editor = preferences.edit();
		editor.putBoolean(registeredUserIDKey, registeredUser);
		editor.commit();
	}

	/**
	 * Retrieves the registeredUser parameters.
	 * 
	 * @return the current registeredUser parameters.
	 */
	public boolean getRegisteredUser() {
		return this.registeredUser;
	}

	/* TIMES */

	/**
	 * Return a <code>FRTimesClient</code> with the given context and formatters
	 * ready depending on the language or parameters chosen in model.
	 * 
	 * @param context
	 *            application context for getString
	 * @return a FRTimesClient with appropriate context and formatters.
	 */
	public FRTimesClient getFRTimesClient(Context context) {
		return getFRTimesClient(context, timeLanguage);
	}

	/**
	 * Return a <code>FRTimesClient</code> with the given context and formatters
	 * ready depending on the language or parameters chosen in model.
	 * <p>
	 * You should usually call the method WITHOUT the timeLanguage, and the
	 * model use the one selected in parameters.
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
	public int getColorLine(Occupancy mOccupancy) {
		if (!isColorLineFull()) {
			return COLOR_TRANSPARENT;
		}

		if (mOccupancy.isIsAtLeastFreeOnce()) {
			if (mOccupancy.isIsAtLeastOccupiedOnce()) {
				return getColorOrange();
			} else {
				return getColorGreen();
			}
		} else {
			if (mOccupancy.isIsAtLeastOccupiedOnce()) {
				return getColorRed();
			} else {
				// default
				return COLOR_DEFAULT;
			}
		}
	}

	/**
	 * Return the appropriate color according to the {@link ActualOccupation}
	 * given, and the {@link FreeRoomModel} color settings (
	 * {@link ColorBlindMode}).
	 * 
	 * @param mActualOccupation
	 *            actual occupation for a given period
	 * @return the appropriate color
	 */
	public int getColorLine(ActualOccupation mActualOccupation) {
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
	 * {@link Occupancy} given, and the {@link FreeRoomModel} color settings (
	 * {@link ColorBlindMode}).
	 * 
	 * @param mOccupancy
	 *            occupancy for a given room (for multiple periods)
	 * @return a color dot {@link Drawable}
	 */
	public int getColoredDotDrawable(Occupancy mOccupancy) {
		if (!isColorColoredDots()) {
			return R.drawable.ic_dot_empty;
		}

		if (mOccupancy.isIsAtLeastFreeOnce()) {
			if (mOccupancy.isIsAtLeastOccupiedOnce()) {
				return getColoredDotOrange();
			} else {
				return getColoredDotGreen();
			}
		} else {
			if (mOccupancy.isIsAtLeastOccupiedOnce()) {
				return getColoredDotRed();
			} else {
				// default: should not appear!
				return getColoredDotUnknown();
			}
		}
	}

	/**
	 * Return the correct color dot {@link Drawable} according to the
	 * {@link ActualOccupation} given, and the {@link FreeRoomModel} color
	 * settings ({@link ColorBlindMode}).
	 * 
	 * @param mActualOccupation
	 *            actual occupation for a given period
	 * @return a color dot {@link Drawable}
	 */
	public int getColoredDotDrawable(ActualOccupation mActualOccupation) {
		if (!isColorColoredDots()) {
			return R.drawable.ic_dot_empty;
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
	 * of the {@link Occupancy}, call {@link getColorDrawable(Occupancy)} . For
	 * the color depending of the {@link ActualOccupation}, call
	 * {@link #getColoredDotDrawable(ActualOccupation)}
	 * 
	 * @return the dot indicating "UNKNOWN".
	 */
	private int getColoredDotUnknown() {
		if (colorBlindMode.equals(ColorBlindMode.DOTS_SYMBOL)
				|| colorBlindMode.equals(ColorBlindMode.DOTS_SYMBOL_LINEFULL)
				|| colorBlindMode
						.equals(ColorBlindMode.DOTS_SYMBOL_LINEFULL_DISCOLORED)) {
			return R.drawable.ic_dot_grey_symbol;
		} else {
			// grey is the same for color-blind and default mode
			return R.drawable.ic_dot_grey;
		}
	}

	/**
	 * Return the "red" dot, indicating the room is OCCUPIED, according to color
	 * preferences.
	 * <p>
	 * Note: this option is made private because it depends on model settings.
	 * This should be called only if the color is sure. For the color depending
	 * of the {@link Occupancy}, call {@link getColorDrawable(Occupancy)} . For
	 * the color depending of the {@link ActualOccupation}, call
	 * {@link #getColoredDotDrawable(ActualOccupation)}
	 * 
	 * @return the dot indicating "OCCUPIED".
	 */
	private int getColoredDotRed() {
		if (colorBlindMode.equals(ColorBlindMode.DOTS_DISCOLORED)) {
			return R.drawable.ic_dot_red_cb;
		} else if (colorBlindMode.equals(ColorBlindMode.DOTS_SYMBOL)
				|| colorBlindMode.equals(ColorBlindMode.DOTS_SYMBOL_LINEFULL)
				|| colorBlindMode
						.equals(ColorBlindMode.DOTS_SYMBOL_LINEFULL_DISCOLORED)) {
			return R.drawable.ic_dot_red_symbol;
		} else {
			return R.drawable.ic_dot_red;
		}
	}

	/**
	 * Return the "green" dot, indicating the room is FREE, according to color
	 * preferences.
	 * <p>
	 * Note: this option is made private because it depends on model settings.
	 * This should be called only if the color is sure. For the color depending
	 * of the {@link Occupancy}, call {@link getColorDrawable(Occupancy)} . For
	 * the color depending of the {@link ActualOccupation}, call
	 * {@link #getColoredDotDrawable(ActualOccupation)}
	 * 
	 * @return the dot indicating "FREE".
	 */
	private int getColoredDotGreen() {
		if (colorBlindMode.equals(ColorBlindMode.DOTS_DISCOLORED)) {
			return R.drawable.ic_dot_green_cb;
		} else if (colorBlindMode.equals(ColorBlindMode.DOTS_SYMBOL)
				|| colorBlindMode.equals(ColorBlindMode.DOTS_SYMBOL_LINEFULL)
				|| colorBlindMode
						.equals(ColorBlindMode.DOTS_SYMBOL_LINEFULL_DISCOLORED)) {
			return R.drawable.ic_dot_green_symbol;
		} else {
			return R.drawable.ic_dot_green;
		}
	}

	/**
	 * Return the "orange" dot, indicating the room is PARTIALLY OCCUPIED,
	 * according to color preferences.
	 * <p>
	 * Note: this option is made private because it depends on model settings.
	 * This should be called only if the color is sure. For the color depending
	 * of the {@link Occupancy}, call {@link getColorDrawable(Occupancy)} . For
	 * the color depending of the {@link ActualOccupation}, call
	 * {@link #getColoredDotDrawable(ActualOccupation)}
	 * 
	 * @return the dot indicating "PARTIALLY OCCUPIED".
	 */
	private int getColoredDotOrange() {
		if (colorBlindMode.equals(ColorBlindMode.DOTS_DISCOLORED)) {
			return R.drawable.ic_dot_orange_cb;
		} else if (colorBlindMode.equals(ColorBlindMode.DOTS_SYMBOL)
				|| colorBlindMode.equals(ColorBlindMode.DOTS_SYMBOL_LINEFULL)
				|| colorBlindMode
						.equals(ColorBlindMode.DOTS_SYMBOL_LINEFULL_DISCOLORED)) {
			return R.drawable.ic_dot_orange_symbol;
		} else {
			return R.drawable.ic_dot_orange;
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
		int id = R.drawable.ic_occupation_empty;
		if (ratio < 0) {
			id = R.drawable.ic_occupation_unknown;
		}
		if (ratio > ratioLowg) {
			id = R.drawable.ic_occupation_lowg;
			if (ratio > ratioLow) {
				id = R.drawable.ic_occupation_low;
				if (ratio > ratioMedg) {
					id = R.drawable.ic_occupation_medg;
					if (ratio > ratioMed) {
						id = R.drawable.ic_occupation_med;
						if (ratio > ratioHighg) {
							id = R.drawable.ic_occupation_highg;
							if (ratio > ratioHigh) {
								id = R.drawable.ic_occupation_high;
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
			System.out.println(request);
			System.out.println("doublon");
			previousRequestDetails.remove(request);
		}
		// adding at the start!
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