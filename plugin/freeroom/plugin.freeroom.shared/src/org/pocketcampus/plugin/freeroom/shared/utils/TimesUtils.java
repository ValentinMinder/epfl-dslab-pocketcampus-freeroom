package org.pocketcampus.plugin.freeroom.shared.utils;

public class TimesUtils {
	/**
	 * Round to the nearest half hour before : if the minutes are less than
	 * 30min, we round to the previous full hour, otherwise we round the half
	 * hour
	 * 
	 * @param timestamp The timestamp to round
	 * @return The rounded timestamp
	 */
	public static long roundToNearestHalfHourBefore(long timestamp) {
		long timeToCompleteHour = FRTimes.ONE_HOUR_IN_MS - timestamp % FRTimes.ONE_HOUR_IN_MS;

		if (timeToCompleteHour < FRTimes.m30_MIN_IN_MS) {
			return (timestamp + timeToCompleteHour) - FRTimes.m30_MIN_IN_MS;
		}

		long timeInMin = timestamp % FRTimes.ONE_HOUR_IN_MS;
		return timestamp - timeInMin;
	}
	
	/**
	 * Round the nearest half hour after
	 * @param timestamp The timestamp to round
	 * @return The rounded timestamp
	 */
	public static long roundToNearestHalfHourAfter(long timestamp) {
		long timeToCompleteHour = FRTimes.ONE_HOUR_IN_MS - timestamp % FRTimes.ONE_HOUR_IN_MS;
		long timeInMin = timestamp % FRTimes.ONE_HOUR_IN_MS;

		// if the hour is full (like 8:00am) no need to round
		if (timeInMin == 0) {
			return timestamp;
		} else if (timeInMin < FRTimes.MARGIN_ERROR && timeInMin > 0) {
			//in this case we are very close to the full hour, take the full hour
			return timestamp - timeInMin;
		}

		// if we are beyond 30min for the hour, we take the next one
		if (timeToCompleteHour < FRTimes.m30_MIN_IN_MS) {
			return timestamp + timeToCompleteHour;
		}

		// otherwise we take the next half hour
		long timeInMinToHalfHour = FRTimes.m30_MIN_IN_MS - timestamp % FRTimes.m30_MIN_IN_MS;
		return timestamp + timeInMinToHalfHour;
	}

}
