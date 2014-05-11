package org.pocketcampus.plugin.freeroom.shared.utils;

import java.util.Calendar;
import java.util.HashMap;
import java.util.List;

import org.pocketcampus.plugin.freeroom.shared.FRPeriod;
import org.pocketcampus.plugin.freeroom.shared.FreeRoomRequest;
import org.pocketcampus.plugin.freeroom.shared.Occupancy;

public class TimesUtils {
	/**
	 * Round to the nearest half hour before : if the minutes are less than
	 * 30min, we round to the previous full hour, otherwise we round the half
	 * hour
	 * 
	 * @param timestamp
	 *            The timestamp to round
	 * @return The rounded timestamp
	 */
	public static long roundToNearestHalfHourBefore(long timestamp) {
		long timeToCompleteHour = FRTimes.ONE_HOUR_IN_MS - timestamp
				% FRTimes.ONE_HOUR_IN_MS;

		if (timeToCompleteHour < FRTimes.m30_MIN_IN_MS) {
			return (timestamp + timeToCompleteHour) - FRTimes.m30_MIN_IN_MS;
		}

		long timeInMin = timestamp % FRTimes.ONE_HOUR_IN_MS;
		return timestamp - timeInMin;
	}

	/**
	 * Round the nearest half hour after
	 * 
	 * @param timestamp
	 *            The timestamp to round
	 * @return The rounded timestamp
	 */
	public static long roundToNearestHalfHourAfter(long timestamp) {
		long timeToCompleteHour = FRTimes.ONE_HOUR_IN_MS - timestamp
				% FRTimes.ONE_HOUR_IN_MS;
		long timeInMin = timestamp % FRTimes.ONE_HOUR_IN_MS;

		// if the hour is full (like 8:00am) no need to round
		if (timeInMin == 0) {
			return timestamp;
		} else if (timeInMin < FRTimes.MARGIN_ERROR && timeInMin > 0) {
			// in this case we are very close to the full hour, take the full
			// hour
			return timestamp - timeInMin;
		}

		// if we are beyond 30min for the hour, we take the next one
		if (timeToCompleteHour < FRTimes.m30_MIN_IN_MS) {
			return timestamp + timeToCompleteHour;
		}

		// otherwise we take the next half hour
		long timeInMinToHalfHour = FRTimes.m30_MIN_IN_MS - timestamp
				% FRTimes.m30_MIN_IN_MS;
		return timestamp + timeInMinToHalfHour;
	}

	/**
	 * Round the given timestamp to the previous hour.
	 * 
	 * @param timestamp
	 *            The timestamp to round
	 * @return The timestamp rounded to the previous hour (10h12 -> 10h00)
	 */
	public static long roundHourBefore(long timestamp) {
		long min = timestamp % FRTimes.ONE_HOUR_IN_MS;
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
		long minToCompleteHour = FRTimes.ONE_HOUR_IN_MS
				- (timestamp % FRTimes.ONE_HOUR_IN_MS);
		long min = timestamp % FRTimes.ONE_HOUR_IN_MS;

		// if the hour is really close (according to MARGIN_ERROR) to the
		// previous hour, we simply keep the current hour (e.g if MARGIN_ERROR
		// is 5min, if we get 10h04 return value will be 10h00)
		if (min <= FRTimes.MARGIN_ERROR) {
			return timestamp - min;
		} else if (minToCompleteHour == FRTimes.ONE_HOUR_IN_MS) {
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
		long startHour = TimesUtils.roundHourBefore(tsStart);
		long endHour = TimesUtils.roundHourAfter(tsEnd);

		Calendar mCalendar = Calendar.getInstance();
		mCalendar.setTimeInMillis(startHour);
		int start = mCalendar.get(Calendar.HOUR_OF_DAY);
		mCalendar.setTimeInMillis(endHour);
		int end = mCalendar.get(Calendar.HOUR_OF_DAY);

		return end - start;
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

	/**
	 * Adjust the period given in the request. It adds 30s to the lower bound,
	 * substract 30s from the upper bound. It is used to allow a margin for
	 * error with the timestamps
	 * 
	 * @param req
	 *            The initial request issued by the client
	 * @return The new request with correct timestamps.
	 */
	public static FreeRoomRequest convertMinPrecision(FreeRoomRequest req) {
		FRPeriod period = req.getPeriod();
		period.setTimeStampStart(period.getTimeStampStart()
				+ FRTimes.m30_MIN_IN_MS);
		period.setTimeStampEnd(period.getTimeStampEnd() - FRTimes.m30_MIN_IN_MS);

		return new FreeRoomRequest(period);

	}

	/**
	 * Round the timestamps of a given FRRequest.
	 * 
	 * If the minutes (e.g 10h32, the minutes are 32) are between 0 and 29 we
	 * round to the previous complete hour (10h23 -> 10h00) and the total period
	 * has length ONE hour.
	 * 
	 * If the minutes are between 30 and 59 we round to the previous complete
	 * hour (10h45-> 10h00) and the total period has length TWO hours.
	 * 
	 * @param period
	 *            The period to round
	 * @return The new period rounded
	 */
	public static FRPeriod roundFRRequestTimestamp(FRPeriod period) {
		long tsStart = period.getTimeStampStart();
		long tsEnd = period.getTimeStampEnd();

		long minutesStart = tsStart % FRTimes.ONE_HOUR_IN_MS;
		if (minutesStart < FRTimes.m30_MIN_IN_MS) {
			tsStart -= minutesStart;
			tsEnd = tsStart + FRTimes.ONE_HOUR_IN_MS;
		} else {
			tsStart -= minutesStart;
			tsEnd = tsStart + 2 * FRTimes.ONE_HOUR_IN_MS;
		}

		tsStart = roundSAndMSToZero(tsStart);
		tsEnd = roundSAndMSToZero(tsEnd);

		return new FRPeriod(tsStart, tsEnd, false);
	}

}
