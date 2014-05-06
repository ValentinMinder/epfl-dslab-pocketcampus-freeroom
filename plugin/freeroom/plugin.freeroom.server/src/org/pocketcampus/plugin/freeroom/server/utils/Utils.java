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
	public static final long MARGIN_ERROR = 5 * 60 * 1000;
	public static final long MIN_PERIOD = 15 * 60 * 1000;

	/**
	 * Adjust the period given in the request. It adds 30s to the lower bound,
	 * substract 30s from the upper bound. It is used to allow a margin for
	 * error with the timestamps
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
	 * Round the given timestamp to the previous hour.
	 * 
	 * @param timestamp
	 *            The timestamp to round
	 * @return The timestamp rounded to the previous hour (10h12 -> 10h00)
	 */
	public static long roundHourBefore(long timestamp) {
		long min = timestamp % ONE_HOUR_MS;
		return timestamp - min;
	}

	/**
	 * Round the given timestamp to the next hour with a margin for error
	 * defined my MARGIN_ERROR (if the minutes in the given hour is less than
	 * MARGIN_ERROR we round to the previous hour).
	 * 
	 * @param timestamp
	 *            The timestamp to round
	 * @return The timestamp rounded as defined above
	 */
	public static long roundHourAfter(long timestamp) {
		long minToCompleteHour = ONE_HOUR_MS - (timestamp % ONE_HOUR_MS);
		long min = timestamp % ONE_HOUR_MS;

		// if the hour is really close (according to MARGIN_ERROR) to the
		// previous hour, we simply keep the current hour (e.g if MARGIN_ERROR
		// is 5min, if we get 10h04 return value will be 10h00)
		if (min <= MARGIN_ERROR) {
			return timestamp - min;
		} else if (minToCompleteHour == ONE_HOUR_MS) {
			return timestamp;
		}
		return timestamp + minToCompleteHour;
	}

	/**
	 * Determine the number of hours between the two given timestamps. The
	 * result will be taken from the rounded version of the two timestamps
	 * 
	 * @param tsStart
	 *            The timestamp of the start of the period
	 * @param tsEnd
	 *            The timestamp of the end of the period
	 * @return The number of hours between the two timestamps
	 */
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

	/**
	 * Round to the nearest half hour before : if the minutes are less than
	 * 30min, we round to the previous full hour, otherwise we round the half
	 * hour
	 * 
	 * @param timestamp The timestamp to round
	 * @return The rounded timestamp
	 */
	public static long roundToNearestHalfHourBefore(long timestamp) {
		long timeToCompleteHour = ONE_HOUR_MS - timestamp % ONE_HOUR_MS;

		if (timeToCompleteHour < m30M_MS) {
			return (timestamp + timeToCompleteHour) - m30M_MS;
		}

		long timeInMin = timestamp % ONE_HOUR_MS;
		return timestamp - timeInMin;
	}

	/**
	 * Round the nearest half hour after
	 * @param timestamp The timestamp to round
	 * @return The rounded timestamp
	 */
	public static long roundToNearestHalfHourAfter(long timestamp) {
		long timeToCompleteHour = ONE_HOUR_MS - timestamp % ONE_HOUR_MS;
		long timeInMin = timestamp % ONE_HOUR_MS;

		// if the hour is full (like 8:00am) no need to round
		if (timeInMin == 0) {
			return timestamp;
		} else if (timeInMin < MARGIN_ERROR && timeInMin > 0) {
			//in this case we are very close to the full hour, take the full hour
			return timestamp - timeInMin;
		}

		// if we are beyond 30min for the hour, we take the next one
		if (timeToCompleteHour < m30M_MS) {
			return timestamp + timeToCompleteHour;
		}

		// otherwise we take the next half hour
		long timeInMinToHalfHour = m30M_MS - timestamp % m30M_MS;
		return timestamp + timeInMinToHalfHour;
	}

	/**
	 * Set the seconds and milliseconds to zero in the given timestamp.
	 * 
	 * @param timeStamp
	 *            The timestamp to round
	 * @return The timestamp rounded.
	 */
	public static long roundSAndMSToZero(long timeStamp) {
		Calendar mCalendar = Calendar.getInstance();
		mCalendar.setTimeInMillis(timeStamp);
		mCalendar.set(Calendar.SECOND, 0);
		mCalendar.set(Calendar.MILLISECOND, 0);
		return mCalendar.getTimeInMillis();
	}
}
