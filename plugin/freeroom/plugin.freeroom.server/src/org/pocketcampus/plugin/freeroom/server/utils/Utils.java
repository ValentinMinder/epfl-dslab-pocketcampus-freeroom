package org.pocketcampus.plugin.freeroom.server.utils;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Scanner;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.pocketcampus.plugin.freeroom.shared.FRRoom;

/**
 * This is an utility class doing useful conversions, and defining a few
 * constants.
 * 
 * @author FreeRoom Project Team - Julien WEBER <julien.weber@epfl.ch> and
 *         Valentin MINDER <valentin.minder@epfl.ch>
 * 
 */
public class Utils {
	public static final int GROUP_STUDENT = 1;
	public static final int GROUP_STAFF = 20;
	
	public static final List<String> mediacomList = Arrays.asList( "875", "876", "9001", "877", "878",
			"880", "1884", "1886", "1887", "1888", "1895", "1835", "1898",
			"1837", "1891", "1896", "2043", "2044", "2045", "2046", "2047",
			"2124", "2125", "2126", "2127", "12205", "12206", "12207", "12208",
			"9208", "9209", "9210", "9275", "9276", "9277", "9278", "9281",
			"9313", "9054", "9055", "4911", "4913", "4914", "4915", "3014",
			"3137", "3208", "3623", "3624", "3625", "3702", "3738");
	
	private static final String FILENAME_FORBIDDEN_WORDS = "src" + File.separator + "forbiddenWords.txt";
	private static ArrayList<String> forbiddenWords = null;
	
	//TODO eventually find a better way to store/access such things
	private static void loadForbiddenWords() {
		if (forbiddenWords != null) {
			return ;
		}
		
		try {
			Scanner sc = new Scanner(new File(FILENAME_FORBIDDEN_WORDS));
			forbiddenWords = new ArrayList<String>();
			while (sc.hasNextLine()) {
				String line = sc.nextLine();
				forbiddenWords.add(line.toLowerCase());
			}
		} catch (FileNotFoundException e) {
			e.printStackTrace();
			forbiddenWords = new ArrayList<String>();
		} 		
	}
	
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

	public static int determineGroupAccessRoom(String uid) {
		return mediacomList.contains(uid) ? GROUP_STUDENT : GROUP_STAFF;
	}

	/**
	 * Check if the given message is good
	 * @param userMessage The message to check
	 * @return true if the sentence does not contain any of the blacklisted word, false otherwise (even if null)
	 */
	public static boolean checkUserMessage(String userMessage) {
		if (userMessage == null) {
			return false;
		}
		
		loadForbiddenWords();
		
		String lowerCaseMessage = userMessage.toLowerCase();
		//split received message into words, and check each word 
		String[] words = lowerCaseMessage.split("\\s+");
		for (String w : words) {
			if (forbiddenWords.contains(w)) {
				return false;
			}
		}
		return true;
	}
}
