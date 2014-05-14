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
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;

import org.pocketcampus.android.platform.sdk.core.IView;
import org.pocketcampus.android.platform.sdk.core.PluginModel;
import org.pocketcampus.plugin.freeroom.R;
import org.pocketcampus.plugin.freeroom.android.iface.IFreeRoomModel;
import org.pocketcampus.plugin.freeroom.android.iface.IFreeRoomView;
import org.pocketcampus.plugin.freeroom.android.utils.FRRequestDetails;
import org.pocketcampus.plugin.freeroom.android.utils.FRTimesClient;
import org.pocketcampus.plugin.freeroom.android.utils.OrderMapListFew;
import org.pocketcampus.plugin.freeroom.shared.FRPeriod;
import org.pocketcampus.plugin.freeroom.shared.FRRoom;
import org.pocketcampus.plugin.freeroom.shared.Occupancy;
import org.pocketcampus.plugin.freeroom.shared.WorkingOccupancy;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Color;

/**
 * FreeRoomModel - The Model that stores the data of this plugin.
 * <p>
 * This is the Model associated with the FreeRoom plugin. It stores the data
 * required for the correct functioning of the plugin. Some data is persistent
 * (none at the moment!) Other data are temporary.
 * <p>
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

	public final int COLOR_CHECK_OCCUPANCY_DEFAULT = Color.WHITE;
	public final int COLOR_CHECK_OCCUPANCY_FREE = Color.GREEN;
	public final int COLOR_CHECK_OCCUPANCY_OCCUPIED = Color.RED;
	public final int COLOR_CHECK_OCCUPANCY_ATLEASTONCE = Color.YELLOW;

	/**
	 * 
	 * Reference to the Views that need to be notified when the stored data
	 * changes.
	 */
	IFreeRoomView mListeners = (IFreeRoomView) getListeners();

	/**
	 * Storing the <code>WorkingOccupancy</code> of people who indicate their
	 * are going to work there.
	 */
	private List<WorkingOccupancy> listWorkingOccupancies = new ArrayList<WorkingOccupancy>();

	private Context context;
	/**
	 * Storage for basic preferences, parameters and so on.
	 */
	private SharedPreferences preferences;

	// NEW INTERFACE as of 2104.04.04.
	private OrderMapListFew<String, List<?>, Occupancy> occupancyByBuilding;

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
		occupancyByBuilding = new OrderMapListFew<String, List<?>, Occupancy>(
				30);
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

	/**
	 * Stores the default behavior at launch time.
	 * <p>
	 * Favorites: the favorites occupancy, free or not. <br>
	 * Favorites only free: the favorites that are free <br>
	 * Any free room: all the free rooms<br>
	 * Lastrequest: replay last request (regarding of room, NOT time: default
	 * param for time will be used!). <br>
	 */
	public enum HomeBehaviourRoom {
		FAVORITES, FAVORITES_ONLY_FREE, ANYFREEROOM, LASTREQUEST;
	}

	/**
	 * Stores the homeBehaviourRoom parameters.
	 * <p>
	 * Default: HomeBehaviourRoom.FAVORITES.
	 */
	private HomeBehaviourRoom homeBehaviourRoom = HomeBehaviourRoom.FAVORITES;

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
	 * Default: 100 requests.
	 */
	private int previousRequestNumber = 100;

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

	// ********** START OF "WHO'S WORKING THERE" PART **********

	/**
	 * Stores a list of <code>WorkingOccupancy</code> to represent what others
	 * are doing.
	 * 
	 * @param listWorkingOccupancies
	 */
	public void setListWorkingOccupancies(
			List<WorkingOccupancy> listWorkingOccupancies) {
		this.listWorkingOccupancies = listWorkingOccupancies;
	}

	/**
	 * Retrieves the stored <code>List</code> of <code>WorkingOccupancy</code>.
	 * 
	 * @return
	 */
	public List<WorkingOccupancy> getListWorkingOccupancies() {
		return listWorkingOccupancies;
	}

	// ********** END OF "WHO'S WORKING THERE" PART **********

	/**
	 * TODO: deprecated
	 * <p>
	 * Returns the building part in mDoorCode.
	 * 
	 * Door codes should be like PH D2 398 with PH the building D2 the zone 398
	 * the number (including floor)
	 * 
	 * It works ONLY if spaces are correctly set!
	 * 
	 * @param mDoorCode
	 * @return
	 */
	public String getBuilding(String mDoorCode) {
		mDoorCode = mDoorCode.trim();
		int firstSpace = mDoorCode.indexOf(" ");
		if (firstSpace > 0) {
			mDoorCode = mDoorCode.substring(0, firstSpace);
		}
		return mDoorCode;
	}

	/**
	 * Sort a given set of rooms by its buildings, the returning map maps
	 * building's name to the list of rooms in this buildings. This also add's a
	 * category named Favorites that contains all the favorites if boolean
	 * wantFavoritesList is true
	 **/
	public TreeMap<String, List<FRRoom>> sortFRRoomsByBuildingsAndFavorites(
			Set<FRRoom> rooms, boolean wantFavoritesList) {
		Iterator<FRRoom> iter = rooms.iterator();
		TreeMap<String, List<FRRoom>> sortedResult = new TreeMap<String, List<FRRoom>>();
		ArrayList<String> buildingsList = new ArrayList<String>();

		ArrayList<FRRoom> roomsFavorites = null;
		if (wantFavoritesList) {
			buildingsList.add(context
					.getString(R.string.freeroom_result_group_favorites));
			roomsFavorites = new ArrayList<FRRoom>();
		}

		while (iter.hasNext()) {
			FRRoom frRoom = iter.next();

			if (wantFavoritesList && isFavorite(frRoom)) {
				roomsFavorites.add(frRoom);
			}

			String building = getBuilding(frRoom.getDoorCode());

			List<FRRoom> roomsNumbers = sortedResult.get(building);
			if (roomsNumbers == null) {
				buildingsList.add(building);
				roomsNumbers = new ArrayList<FRRoom>();
				sortedResult.put(building, roomsNumbers);
			}
			roomsNumbers.add(frRoom);
		}

		// we leave an empty favorites list!
		if (wantFavoritesList && roomsFavorites.isEmpty()) {
			sortedResult.remove(buildingsList.get(0));
			buildingsList.remove(0);
		}
		return sortedResult;
	}

	// ********** END OF "FAVORITES" PART **********
	/*
	 * methods are ordered by functionality in model !! PLEASE insert your new
	 * method in an existing category or create a new one with two separators
	 * like this one!
	 */
	// ********** END OF FILE **********

	/**
	 * TODO: NEW INTERFACE as of 2014.04.04.
	 * <p>
	 * Update the occupancy results in the model. The reference to the old data
	 * is kept, only the old data are trashed but the reference is kept.
	 * 
	 * @param occupancyOfRooms
	 */
	public void setOccupancyResults(
			Map<String, List<Occupancy>> occupancyOfRooms) {
		occupancyByBuilding.clear();
		Set<String> keySet = occupancyOfRooms.keySet();
		List<String> buildings = getOrderedBuildings();
		for (String building : buildings) {
			List<Occupancy> list = occupancyOfRooms.get(building);
			if (list != null) {
				keySet.remove(building);
				occupancyByBuilding.put(building, list);
			}
		}

		for (String key : keySet) {
			occupancyByBuilding.put(key, occupancyOfRooms.get(key));
		}
		mListeners.occupancyResultsUpdated();
	}

	/**
	 * TODO: NEW INTERFACE as of 2014.04.04.
	 * <p>
	 * Get the occupancy results. Note that the reference never changes, so you
	 * simply need to update your adapter, never put the date again in it.
	 * 
	 * @return
	 */
	public OrderMapListFew<String, List<?>, Occupancy> getOccupancyResults() {
		return this.occupancyByBuilding;
	}

	/**
	 * TODO: NEW INTERFACE as of 2014.04.04.
	 * <p>
	 * TODO: this is not kept permanently!
	 * <p>
	 * Order of the buildings for displaying to the user.
	 */
	private List<String> orderedBuildings = new ArrayList<String>();

	/**
	 * TODO: NEW INTERFACE as of 2014.04.04.
	 * <p>
	 * Get the orderedBuilding list to display
	 * 
	 * @return the list of ordered buildings.
	 */
	public List<String> getOrderedBuildings() {
		// TODO: this is not stored so far!
		return orderedBuildings;
	}

	/**
	 * TODO: NEW INTERFACE as of 2014.04.04.
	 * <p>
	 * Get the appropriate color according to the occupancy.
	 * 
	 * @param mOccupancy
	 * @return
	 */
	public int getColor(Occupancy mOccupancy) {
		if (mOccupancy == null) {
			return COLOR_CHECK_OCCUPANCY_DEFAULT;
		}

		boolean atLeastOneFree = mOccupancy.isIsAtLeastFreeOnce();
		boolean atLeastOneOccupied = mOccupancy.isIsAtLeastOccupiedOnce();

		if (atLeastOneFree) {
			if (atLeastOneOccupied) {
				return COLOR_CHECK_OCCUPANCY_ATLEASTONCE;
			} else {
				return COLOR_CHECK_OCCUPANCY_FREE;
			}
		} else {
			if (atLeastOneOccupied) {
				return COLOR_CHECK_OCCUPANCY_OCCUPIED;
			} else {
				// default
				return COLOR_CHECK_OCCUPANCY_DEFAULT;
			}
		}
	}

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

	/**
	 * Return the id of the right image based on the ratio given.
	 * <p>
	 * TODO: may be do that better TODO: insert a "???" image if information is
	 * not available.
	 * 
	 * @param ratio
	 *            ratio of WorstCaseProbableOccupancy
	 * @return
	 */
	public int getImageFromRatioOccupation(double ratio) {

		double ratioLowg = 0.00;
		double ratioLow = 0.05;
		double ratioMedg = 0.10;
		double ratioMed = 0.15;
		double ratioHighg = 0.20;
		double ratioHigh = 0.25;
		int id = R.drawable.occupation_empty;
		if (ratio < 0) {
			id = R.drawable.occupation_unknown;
		}
		if (ratio > ratioLowg) {
			id = R.drawable.occupation_lowg;
			if (ratio > ratioLow) {
				id = R.drawable.occupation_low;
				if (ratio > ratioMedg) {
					id = R.drawable.occupation_medg;
					if (ratio > ratioMed) {
						id = R.drawable.occupation_med;
						if (ratio > ratioHighg) {
							id = R.drawable.occupation_highg;
							if (ratio > ratioHigh) {
								id = R.drawable.occupation_high;
							}
						}
					}
				}
			}
		}
		return id;
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
		SharedPreferences preferences = context.getSharedPreferences(
				PREF_USER_DETAILS_KEY, Context.MODE_PRIVATE);
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
		String anonymID = ((Long) System.currentTimeMillis()).toString();

		// random string to complete to 32 chars
		anonymID += new BigInteger(130, new SecureRandom())
				.toString(32 - anonymID.length());

		return anonymID;
	}

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
			return false;
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
				resetFavorites();
			}
		}
		return favorites;
	}

	/**
	 * Reset the favorites structure to an empty structure and save it to file.
	 * Useful when a bug appear, when changing structures during updates, or
	 * simply at first launch of the app.
	 * <p>
	 * Call getFavorites in usual mode, this is reserved for particular uses.
	 * 
	 * @return true if written to file successful.
	 */
	public boolean resetFavorites() {
		favorites = new OrderMapListFew<String, List<FRRoom>, FRRoom>(50);
		favorites.setAvailableLimit(Integer.MAX_VALUE);
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
			String key = getKey(mRoom);
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
		String key = getKey(mRoom);
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
		// ensure favorites structure exists.
		getFavorites();

		String key = getKey(mRoom);
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
	}

	// TODO: this is not secured (may not exist)
	public String getKey(FRRoom mRoom) {
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

	/* STORAGE OF PREV. REQUEST */

	private List<FRRequestDetails> previousRequestDetails;
	private String PREV_REQ_FILENAME = "freeroom_prev_req_file.dat";

	/**
	 * Retrieves the previous request object from persistent file.
	 * 
	 * @return true if successful.
	 */
	private boolean retrievePreviousRequest() {
		Object read = readObjectFromFile(PREV_REQ_FILENAME);
		if (read instanceof List<?>) {
			previousRequestDetails = (List<FRRequestDetails>) read;
			return true;
		} else {
			return false;
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
	 * Reset the previous request structure to an empty structure and save it to
	 * file. Useful when a bug appear, when changing structures during updates,
	 * or simply at first launch of the app.
	 * <p>
	 * Call getPreviousRequest in usual mode, this is reserved for particular
	 * uses.
	 * 
	 * @return true if written to file successful.
	 */
	public boolean resetPreviousRequest() {
		previousRequestDetails = new ArrayList<FRRequestDetails>(10);
		return savePreviousRequest();
	}

	/**
	 * Add a request to the previous request, and save the previous request.
	 * 
	 * @param request
	 *            request to add to previous request.
	 * @return true if successful
	 */
	public boolean addPreviousRequest(FRRequestDetails request) {
		// ensure favorites structure exists.
		getPreviousRequest();
		return previousRequestDetails.add(request);
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
		if (timeLanguage.equals(TimeLanguage.ENGLISH)) {
			return new FRTimesClient(
					context,
					context.getString(R.string.freeroom_pattern_day_format_english),
					context.getString(R.string.freeroom_pattern_hour_format_long_english),
					context.getString(R.string.freeroom_pattern_hour_format_short_english));
		}

		if (timeLanguage.equals(TimeLanguage.FRENCH)) {
			return new FRTimesClient(
					context,
					context.getString(R.string.freeroom_pattern_day_format_french),
					context.getString(R.string.freeroom_pattern_hour_format_long_french),
					context.getString(R.string.freeroom_pattern_hour_format_short_french));
		}

		// default
		return new FRTimesClient(
				context,
				context.getString(R.string.freeroom_pattern_day_format_default),
				context.getString(R.string.freeroom_pattern_hour_format_long_default),
				context.getString(R.string.freeroom_pattern_hour_format_short_default));
	}
}