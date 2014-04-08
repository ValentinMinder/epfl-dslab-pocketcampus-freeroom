package org.pocketcampus.plugin.freeroom.server.utils;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.pocketcampus.plugin.freeroom.shared.FRPeriod;
import org.pocketcampus.plugin.freeroom.shared.FRRoom;
import org.pocketcampus.plugin.freeroom.shared.FreeRoomRequest;

/**
 * This is an utility class doing useful conversions, and defining a few
 * constants.
 * 
 * @author FreeRoom Project Team - Julien WEBER <julien.weber@epfl.ch> and
 *         Valentin MINDER <valentin.minder@epfl.ch>
 * 
 */
public class Utils {
	
	// TODO: move in FRTimes in shared!!!
	public static final long m30s_IN_MS = 30 * 1000;
	public static final long ONE_HOUR_MS = 60 * 60 * 1000;
	public static final long m30M_MS = 60 * 30 * 1000;
	public static final long ONE_DAY_MS = ONE_HOUR_MS * 24;

	/**
	 * Adjust the period given in the request. It adds 30s to the lower
	 * bound, substract 30s from the upper bound. It is used to allow a
	 * margin for error with the timestamps
	 * 
	 * @param req
	 *            The intial request issued by the client
	 * @return The new request with correct timestamps.
	 */
	public static FreeRoomRequest convertMinPrecision(FreeRoomRequest req) {
		FRPeriod period = req.getPeriod();
		period.setTimeStampStart(period.getTimeStampStart() + m30s_IN_MS);
		period.setTimeStampEnd(period.getTimeStampEnd() - m30s_IN_MS);

		return new FreeRoomRequest(period);

	}
	
	public static String extractBuilding(String doorCode) {
		String mDoorCode = doorCode.trim();
		int firstSpace = mDoorCode.indexOf(" ");
		if (firstSpace > 0) {
			mDoorCode = mDoorCode.substring(0, firstSpace);
		} else {
			Pattern mBuildingPattern = Pattern.compile("^([A-Za-z]+)[^A-Za-z]$");
			Matcher mMatcher = mBuildingPattern.matcher(doorCode);
			
			if (mMatcher.matches()) {
				return mMatcher.group(0);
			} else {
				return mDoorCode;
			}
		}
		return mDoorCode;
	}
	
	public static Map<String, List<FRRoom>> sortRoomsByBuilding(List<FRRoom> rooms) {
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
	
	public static long roundHourBefore(long timestamp) {
		long min = timestamp % ONE_HOUR_MS;
		return timestamp - min;
	}

	//TODO one half a hour is added mysteriously from the converter probably
	public static long roundHourAfter(long timestamp) {
		long minToCompleteHour = ONE_HOUR_MS - (timestamp % ONE_HOUR_MS);
		if (minToCompleteHour == ONE_HOUR_MS) {
			return timestamp;
		}
		return timestamp + minToCompleteHour;
	}
	
	public static long determineNumberHour(long tsStart, long tsEnd) {
		long startHour = Utils.roundHourBefore(tsStart);
		long endHour = Utils.roundHourAfter(tsEnd);

		
		Calendar mCalendar = Calendar.getInstance();
		mCalendar.setTimeInMillis(startHour);
		int start = mCalendar.get(Calendar.HOUR_OF_DAY);
		mCalendar.setTimeInMillis(endHour);
		int end = mCalendar.get(Calendar.HOUR_OF_DAY);
		
		return end - start;
	}
	
	public static long roundToNearestHalfHourBefore(long timestamp) {
		long timeToCompleteHour = ONE_HOUR_MS - timestamp % ONE_HOUR_MS;

		if (timeToCompleteHour < m30M_MS) {
			return (timestamp + timeToCompleteHour) - m30M_MS;
		}

		long timeInMin = timestamp % ONE_HOUR_MS;
		return timestamp - timeInMin;
	}

	public static long roundToNearestHalfHourAfter(long timestamp) {
		long timeToCompleteHour = ONE_HOUR_MS - timestamp % ONE_HOUR_MS;

		if (timeToCompleteHour < m30M_MS) {
			return timestamp + timeToCompleteHour;
		}

		long timeInMinToHalfHour = m30M_MS - timestamp % m30M_MS;
		return timestamp + timeInMinToHalfHour;
	}
}
