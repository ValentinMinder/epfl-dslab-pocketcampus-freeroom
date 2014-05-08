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
}
