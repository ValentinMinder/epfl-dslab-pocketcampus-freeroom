package org.pocketcampus.plugin.freeroom.android.utils;

import java.util.Calendar;

import org.pocketcampus.plugin.freeroom.shared.FRPeriod;
import org.pocketcampus.plugin.freeroom.shared.FreeRoomRequest;

/**
 * This is an utility class doing useful conversions, and defining a few
 * constants.
 * <p>
 * 
 * @author FreeFroom Project Team - Julien WEBER <julien.weber@epfl.ch> and
 *         Valentin MINDER <valentin.minder@epfl.ch>
 * 
 */
public class Converter {
	public static final long ONE_MIN_IN_MS = 60 * 1000;
	public static final long ONE_HOUR_IN_MS = 60 * 60 * 1000;
	public static final long ONE_DAY_IN_MS = ONE_HOUR_IN_MS * 24;
	public static final long ONE_WEEK_IN_MS = ONE_DAY_IN_MS * 7;

	// TODO: defines that const elsewhere
	private static final int firstHourCheckable = 8;
	private static final int lastHourCheckable = 19;
	private static final int minMinutesIntervalToCheck = 5;

	public static FreeRoomRequest convert(int day, int startHour, int endHour) {
		Calendar calendar = Calendar.getInstance();
		int today_day = calendar.get(Calendar.DAY_OF_WEEK);
		int now_hour = calendar.get(Calendar.HOUR_OF_DAY);

		// first case we are the given day but the specified hour is before,
		// thus need to go to the next given day
		// if you select the current hour, it's this day! But if it's before the
		// current hour, it's the next week!
		// also the case for cases where the given day is different from today
		long timestampshift = 0;
		if (now_hour >= startHour && day == today_day) {
			timestampshift = ONE_WEEK_IN_MS;
		} else if (day != today_day) {
			if (today_day < day) {
				timestampshift = (day - today_day) * ONE_DAY_IN_MS;
			} else {
				int daysToCompleteWeek = 7 - today_day;
				int daysToGo = daysToCompleteWeek + day;
				timestampshift = daysToGo * ONE_DAY_IN_MS;
			}
		}

		calendar.setTimeInMillis(calendar.getTimeInMillis() + timestampshift);
		calendar.set(Calendar.HOUR_OF_DAY, startHour);
		calendar.set(Calendar.MINUTE, 0);

		long t_start = calendar.getTimeInMillis();
		long t_end = t_start + (endHour - startHour) * ONE_HOUR_IN_MS;

		FRPeriod period = new FRPeriod(t_start, t_end, false);

		FreeRoomRequest req = new FreeRoomRequest(period);

		return req;
	}

	/**
	 * Define the period with more precision. for instance : Monday, 10h25-10h59
	 * convertWith...sion(Calendar.Monday, 10, 15, 10, 59)
	 * 
	 * @param day
	 *            The day as defined in Calendar class.
	 * @param startHour
	 *            The start of the period in hours
	 * @param startMin
	 *            The precision in minutes for the start
	 * @param endHour
	 *            The end of the period in hours
	 * @param endMin
	 *            The precision in minutes for the end
	 * @return
	 */
	public static FreeRoomRequest convertWithMinPrecision(int day,
			int startHour, int startMin, int endHour, int endMin) {
		FRPeriod period = convert(day, startHour, endHour).getPeriod();
		period.setTimeStampStart(period.getTimeStampStart() + startMin
				* ONE_MIN_IN_MS);
		period.setTimeStampEnd(period.getTimeStampEnd() + endMin
				* ONE_MIN_IN_MS);

		return new FreeRoomRequest(period);

	}

	static int testCounter = -1;

	/**
	 * getNextValidPeriod() should return the next valid period to check,
	 * MON-FRI 8am-7pm
	 * 
	 * MON-FRI 8am-6pm should return nextHour to nextHour+1, minutes to 0.
	 * 
	 * MON-FRI 6am-6:55pm should return exact min up to 7pm
	 * 
	 * All night (after 6:55pm) should return next day 8am-9am
	 * 
	 * All weekend (after FR 6:55pm) should return next Monday 8am-9am
	 * 
	 * @return
	 */
	public static FRPeriod getNextValidPeriod() {

		// reset the time to the present time
		Calendar mCalendar = Calendar.getInstance();

		int yearSelected = mCalendar.get(Calendar.YEAR);
		int monthSelected = mCalendar.get(Calendar.MONTH);
		int dayOfMonthSelected = mCalendar.get(Calendar.DAY_OF_MONTH);
		int startHourSelected = mCalendar.get(Calendar.HOUR_OF_DAY);
		int startMinSelected = mCalendar.get(Calendar.MINUTE);
		int endHourSelected = mCalendar.get(Calendar.HOUR_OF_DAY);
		int endMinSelected = mCalendar.get(Calendar.MINUTE);

		boolean testActivated = false;
		if (testActivated) {
			switch (testCounter) {
			case 0:
				// thursday, early morning
				mCalendar.set(2014, 02, 20, 01, 00);
				break;
			case 1:
				// thursday, during day, usual
				mCalendar.set(2014, 02, 20, 17, 59);
				break;
			case 2:
				// thursday, late afternoon
				mCalendar.set(2014, 02, 20, 18, 23);
				break;
			case 3:
				// thursday, after delay
				mCalendar.set(2014, 02, 20, 18, 56);
				break;
			case 4:
				// thursday, after delay
				mCalendar.set(2014, 02, 20, 19, 00);
				break;
			case 5:
				// friday, late afternoon
				mCalendar.set(2014, 02, 21, 18, 23);
				break;
			case 6:
				// friday, after delay
				// TODO: dont work
				mCalendar.set(2014, 02, 21, 18, 56);
				break;
			case 7:
				// friday, after delay
				// TODO: dont work
				mCalendar.set(2014, 02, 21, 19, 00);
				break;
			case 8:
				// saturday, early morning
				mCalendar.set(2014, 02, 22, 00, 00);
				break;
			case 9:
				// saturday, early morning
				mCalendar.set(2014, 02, 22, 00, 21);
				break;
			case 10:
				// saturday, during day, usual
				// TODO: dont work
				mCalendar.set(2014, 02, 22, 17, 59);
				break;
			case 11:
				// saturday, late afternoon
				// TODO: dont work
				mCalendar.set(2014, 02, 22, 18, 00);
				break;
			case 12:
				// saturday, after delay
				// TODO: dont work
				mCalendar.set(2014, 02, 22, 18, 55);
				break;
			case 13:
				// saturday, after delay
				// TODO: dont work
				mCalendar.set(2014, 02, 22, 19, 00);
				break;
			case 14:
				// saturday, early morning
				mCalendar.set(2014, 02, 23, 00, 00);
				break;
			case 15:
				// saturday, during day, usual
				// TODO: dont work
				mCalendar.set(2014, 02, 23, 17, 59);
				break;
			case 16:
				// saturday, late afternoon
				// TODO: dont work
				mCalendar.set(2014, 02, 23, 18, 00);
				break;
			case 17:
				// saturday, after delay
				// TODO: dont work
				mCalendar.set(2014, 02, 23, 18, 55);
				break;
			case 18:
				// saturday, after delay
				// TODO: dont work
				mCalendar.set(2014, 02, 23, 19, 00);
				break;
			default:
				mCalendar.setTimeInMillis(System.currentTimeMillis());
				break;
			}
			System.out.println("Test Counter:" + testCounter);
			testCounter++;
		}

		// values for test only (weekends, nights, ...)
		int temp_now_day_week = mCalendar.get(Calendar.DAY_OF_WEEK);
		int temp_now_hour = mCalendar.get(Calendar.HOUR_OF_DAY);
		int temp_now_min = mCalendar.get(Calendar.MINUTE);

		// starting Friday 18h55, and during all the weekend, we shift to Mon
		// between 0am and 1am. It will be reshifted to Mon 8h-9h after.
		int hourShift = 0;
		if (temp_now_day_week == Calendar.SUNDAY) {
			hourShift = 24 - temp_now_hour + 1;
		}
		if (temp_now_day_week == Calendar.SATURDAY) {
			hourShift = 24 - temp_now_hour + 1;
			hourShift += 24;
		}
		if ((temp_now_day_week == Calendar.FRIDAY)
				&& checkHourMinIsEveningAndShifted(temp_now_hour, temp_now_min)) {
			hourShift = 24 - temp_now_hour + 1;
			hourShift += 2 * 24;
		}
		mCalendar.setTimeInMillis(mCalendar.getTimeInMillis() + hourShift
				* 3600 * 1000);

		// actualize with the enventually shifted Calendar
		temp_now_hour = mCalendar.get(Calendar.HOUR_OF_DAY);
		temp_now_min = mCalendar.get(Calendar.MINUTE);

		yearSelected = mCalendar.get(Calendar.YEAR);
		monthSelected = mCalendar.get(Calendar.MONTH);
		dayOfMonthSelected = mCalendar.get(Calendar.DAY_OF_MONTH);

		// default set: next hour to next+1 hour, all minutes to 0
		// Works perfectly Mon-Fri 8h-18h.
		// weekend are handled before to be on next Monday.
		startHourSelected = mCalendar.get(Calendar.HOUR_OF_DAY) + 1;
		startMinSelected = 0;
		endHourSelected = startHourSelected + 1;
		endMinSelected = 0;

		// during the evening (starting 18h55), we shift to the next morning
		// during the night (before 8h), we shift to the first hour of morning
		// (8h-9h).
		// during late afternoon (18h-18h55), we default set from now to 19h00.
		if (temp_now_hour < firstHourCheckable) {
			startHourSelected = firstHourCheckable;
			endHourSelected = firstHourCheckable + 1;
		} else if (checkHourMinIsEveningAndShifted(temp_now_hour, temp_now_min)) {
			yearSelected = mCalendar.get(Calendar.YEAR);
			monthSelected = mCalendar.get(Calendar.MONTH);
			dayOfMonthSelected = mCalendar.get(Calendar.DAY_OF_MONTH);
			System.out.println(yearSelected + "" + monthSelected + ""
					+ dayOfMonthSelected);
			mCalendar
					.setTimeInMillis(mCalendar.getTimeInMillis() + 24 * 3600 * 1000);
			System.out.println(yearSelected + "" + monthSelected + ""
					+ dayOfMonthSelected);
			yearSelected = mCalendar.get(Calendar.YEAR);
			monthSelected = mCalendar.get(Calendar.MONTH);
			dayOfMonthSelected = mCalendar.get(Calendar.DAY_OF_MONTH);
			startHourSelected = firstHourCheckable;
			endHourSelected = firstHourCheckable + 1;
		} else if (temp_now_hour == lastHourCheckable - 1) {
			startHourSelected = temp_now_hour;
			startMinSelected = temp_now_min;
			endHourSelected = temp_now_hour + 1;
		}

		FRPeriod mFrPeriod = new FRPeriod(System.currentTimeMillis(),
				System.currentTimeMillis() + ONE_HOUR_IN_MS, false);

		return mFrPeriod;
	}

	/**
	 * Checks if the given hour and minutes is evening. Request in evening are
	 * shifted to next morning.
	 * 
	 * Should return true if time is greater or equal to 19h00, up to 24h00.
	 * Moreover, it returns true a few minutes before 19h00, because we need at
	 * least a few minutes to check
	 * 
	 * @param hour
	 * @param min
	 * @return
	 */
	private static boolean checkHourMinIsEveningAndShifted(int hour, int min) {
		return (hour >= lastHourCheckable)
				|| (hour == (lastHourCheckable - 1) && min >= (60 - minMinutesIntervalToCheck));
	}
}
