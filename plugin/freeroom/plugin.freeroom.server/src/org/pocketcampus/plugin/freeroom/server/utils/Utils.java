package org.pocketcampus.plugin.freeroom.server.utils;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.pocketcampus.plugin.freeroom.shared.FRPeriod;
import org.pocketcampus.plugin.freeroom.shared.FRRoom;
import org.pocketcampus.plugin.freeroom.shared.FreeRoomRequest;
import org.pocketcampus.plugin.freeroom.shared.utils.FRTimes;

/**
 * This is an utility class doing useful conversions, and defining a few
 * constants.
 * 
 * @author FreeRoom Project Team - Julien WEBER <julien.weber@epfl.ch> and
 *         Valentin MINDER <valentin.minder@epfl.ch>
 * 
 */
public class Utils {

	/**
	 * Extract the building from the doorCode
	 * 
	 * @param doorCode
	 *            The doorCode from which we extract the building, it is assumed
	 *            to be well formatted (with space separating the building from
	 *            the zone and number e.g BC 01) otherwise it takes the first
	 *            characters of the string until it hits a number.
	 * @return The building of the given door code if the door code is correct
	 *         as defined above or the door code itself if no matches has been
	 *         found.
	 */
	public static String extractBuilding(String doorCode) {
		String mDoorCode = doorCode.trim();
		int firstSpace = mDoorCode.indexOf(" ");
		if (firstSpace > 0) {
			mDoorCode = mDoorCode.substring(0, firstSpace);
		} else {
			Pattern mBuildingPattern = Pattern
					.compile("^([A-Za-z]+)[^A-Za-z]$");
			Matcher mMatcher = mBuildingPattern.matcher(doorCode);

			if (mMatcher.matches()) {
				return mMatcher.group(0);
			} else {
				return mDoorCode;
			}
		}
		return mDoorCode;
	}

	/**
	 * From a list of rooms it creates a HashMap that maps a building to a list
	 * of rooms (contained in this building).
	 * 
	 * @param rooms
	 *            The rooms to sort
	 * @return The HashMap as defined above, an empty HashMap is rooms is null or is empty
	 */
	public static Map<String, List<FRRoom>> sortRoomsByBuilding(
			List<FRRoom> rooms) {
		if (rooms == null || rooms.isEmpty()) {
			return new HashMap<String, List<FRRoom>>();
		}

		Iterator<FRRoom> iter = rooms.iterator();
		HashMap<String, List<FRRoom>> sortedResult = new HashMap<String, List<FRRoom>>();
		ArrayList<String> buildingsList = new ArrayList<String>();

		while (iter.hasNext()) {
			FRRoom frRoom = iter.next();

			String building = extractBuilding(frRoom.getDoorCode());

			List<FRRoom> roomsNumbers = sortedResult.get(building);
			if (roomsNumbers == null) {
				buildingsList.add(building);
				roomsNumbers = new ArrayList<FRRoom>();
				sortedResult.put(building, roomsNumbers);
			}
			roomsNumbers.add(frRoom);
		}

		return sortedResult;
	}

	/**
	 * Remove duplicates in a list of rooms
	 * @param uidList The list to check
	 * @return The list with unique ids without duplicates
	 */
	public static List<String> removeDuplicate(List<String> uidList) {
		HashSet<String> uidSet = new HashSet<>();
		uidSet.addAll(uidList);
		return new ArrayList<>(uidSet);
	}

	public static boolean checkValidUID(String uid) {
		Pattern mUIDPattern = Pattern
				.compile("^[0-9]+$");
		Matcher mMatcher = mUIDPattern.matcher(uid);

		return mMatcher.matches();
	}
}
