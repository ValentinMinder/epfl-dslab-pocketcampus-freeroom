package org.pocketcampus.plugin.freeroom.shared.utils;

import org.pocketcampus.plugin.freeroom.shared.Constants;
import org.pocketcampus.plugin.freeroom.shared.FRPeriod;

import java.util.Calendar;

/**
 * This is an utility class doing useful conversions, and defining a few
 * constants.
 * <p/>
 *
 * @author FreeRoom Project Team (2014/05)
 * @author Julien WEBER <julien.weber@epfl.ch>
 * @author Valentin MINDER <valentin.minder@epfl.ch>
 */
public class FRTimes {
    /*
     * All of these constants ARE NOT in thrift intentionally. Only used by
     * server and Android.
     */
    public static final long ONE_SEC_IN_MS = 1000;
    public static final long ONE_MIN_IN_MS = ONE_SEC_IN_MS * 60;
    public static final long m30_MIN_IN_MS = ONE_MIN_IN_MS * 30;
    public static final long ONE_HOUR_IN_MS = ONE_MIN_IN_MS * 60;
    public static final long ONE_DAY_IN_MS = ONE_HOUR_IN_MS * 24;
    public static final long ONE_WEEK_IN_MS = ONE_DAY_IN_MS * 7;

    public static final long MARGIN_ERROR = 5 * 60 * 1000;
    public static final long MIN_PERIOD = 15 * 60 * 1000;

    /**
     * Return a FRPeriod from a given day, starthour and endhour.
     */
    public static FRPeriod convertFRPeriod(int day, int startHour, int endHour) {
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
        calendar.set(Calendar.SECOND, 0);
        calendar.set(Calendar.MILLISECOND, 0);

        long t_start = calendar.getTimeInMillis();
        long t_end = t_start + (endHour - startHour) * ONE_HOUR_IN_MS;

        return new FRPeriod(t_start, t_end);
    }

	/*
     * All the validCalendars share the same JAVADOC.
	 */

    /**
     * See the doc of
     * <code>public static String validCalendarsString(FRPeriod mFrPeriod,
     * long nowTimeStamp)</code>
     */
    public static boolean validCalendars(FRPeriod mFrPeriod) {
        String message = validCalendarsString(mFrPeriod);
        return message.equals("");
    }

    /**
     * See the doc of
     * <code>public static String validCalendarsString(FRPeriod mFrPeriod,
     * long nowTimeStamp)</code>
     */
    public static boolean validCalendars(FRPeriod mFrPeriod, long now) {
        String message = validCalendarsString(mFrPeriod, now);
        return message.equals("");
    }

    /**
     * See the doc of
     * <code>public static String validCalendarsString(FRPeriod mFrPeriod,
     * long nowTimeStamp)</code>
     */
    public static boolean validCalendars(Calendar start, Calendar end, long now) {
        FRPeriod mFRPeriod = new FRPeriod(start.getTimeInMillis(),
                end.getTimeInMillis());
        return validCalendars(mFRPeriod, now);
    }

    /**
     * See the doc of
     * <code>public static String validCalendarsString(FRPeriod mFrPeriod,
     * long nowTimeStamp)</code>
     *
     * @param mFrPeriod
     * @return
     */
    public static String validCalendarsString(FRPeriod mFrPeriod) {
        long now = System.currentTimeMillis();
        return validCalendarsString(mFrPeriod, now);
    }

    /**
     * Check if the given period is correct
     * <p/>
     * Constructed from the previous methods in both server and client. Now they
     * share the exact same definition of a "valid" or "invalid" period.
     * <p/>
     * A valid period: is between Monday and Friday, between 8am and 7pm, the
     * start timestamp is before the end timestamp, and as a length of at least
     * 5 minutes.
     * <p/>
     * It cannot be in the past more than 1 month, and in the future more than 4
     * month.
     * <p/>
     * All these constants are ONLY defined in THIS class. Changing them there
     * will immediately change them for both server and client.
     * <p/>
     * The string returned is empty ("") if everything is fine. It contains an
     * error message per line otherwise.
     * <p/>
     * CAUTION: if you change the conditions, especially if you strengthen them,
     * during PRODUCTION, and if you already have clients, they will probably
     * have errors (they will be allowed to do things the server don't accept
     * anymore).
     *
     * @param mFrPeriod    a period of time to check
     * @param nowTimeStamp a time stamp representing "now" (used for testing purposes).
     * @return empty string if is everything is fine, an error message if some
     * conditions does not hold (a message per condition broken)
     */
    public static String validCalendarsString(FRPeriod mFrPeriod,
                                              long nowTimeStamp) {
        return validCalendarsString(mFrPeriod, nowTimeStamp, false, false);
    }

    /**
     * See {@link #validCalendarsString(FRPeriod, long)}.
     * <p/>
     * This method let allow weekends and/or evenings.
     *
     * @param mFrPeriod
     * @param nowTimeStamp
     * @param allowWeekEnds if weekend should be accepted
     * @param allowEvenings if evening should be accepted
     * @return
     */
    public static String validCalendarsString(FRPeriod mFrPeriod,
                                              long nowTimeStamp, boolean allowWeekEnds, boolean allowEvenings) {
        StringBuffer buffer = new StringBuffer();
        buffer.append("");

        Calendar mStartCalendar = Calendar.getInstance();
        long startTimeStamp = mFrPeriod.getTimeStampStart();
        mStartCalendar.setTimeInMillis(startTimeStamp);

        Calendar mEndCalendar = Calendar.getInstance();
        long endTimeStamp = mFrPeriod.getTimeStampEnd();
        mEndCalendar.setTimeInMillis(endTimeStamp);

        // the end date should be after the start, not equal or before.
        if ((endTimeStamp - startTimeStamp) < 0) {
            buffer.append("Timestamp start is after timestamp end.\n");
        }

        // same year
        if (mStartCalendar.get(Calendar.YEAR) != mEndCalendar
                .get(Calendar.YEAR)) {
            buffer.append("Timestamps start and end must be same year.\n");
        }

        // same month
        if (mStartCalendar.get(Calendar.MONTH) != mEndCalendar
                .get(Calendar.MONTH)) {
            buffer.append("Timestamps start and end must be same month.\n");
        }

        // same day of month
        if (mStartCalendar.get(Calendar.DAY_OF_WEEK) != mEndCalendar
                .get(Calendar.DAY_OF_WEEK)) {
            buffer.append("Timestamps start and end must be same day of month.\n");
        }

        // defines the minimal time interval
        if (Math.abs(endTimeStamp - startTimeStamp) < Constants.MIN_MINUTE_INTERVAL
                * ONE_MIN_IN_MS) {
            buffer.append("Their should be at least "
                    + Constants.MIN_MINUTE_INTERVAL + " minutes to check.\n");
        }

        // not more than 24h (It's redundant: no message)
        if (Math.abs(mStartCalendar.getTimeInMillis()
                - mEndCalendar.getTimeInMillis()) > ONE_DAY_IN_MS
                + Constants.MIN_MINUTE_INTERVAL) {
            buffer.append(" ");
        }

        if (!allowWeekEnds) {
            // we only accept weekdays
            int dayWeek = mStartCalendar.get(Calendar.DAY_OF_WEEK);
            if (dayWeek == Calendar.SUNDAY || dayWeek == Calendar.SATURDAY) {
                buffer.append("Only weekdays are accepted. Please avoid Saturdays and Sundays.\n");
            }
        }

        int startHour = mStartCalendar.get(Calendar.HOUR_OF_DAY);
        int startMinutes = mStartCalendar.get(Calendar.MINUTE);
        int endHour = mEndCalendar.get(Calendar.HOUR_OF_DAY);
        int endMinutes = mEndCalendar.get(Calendar.MINUTE);

        if (!allowEvenings) {
            // we limit the first hour checkable
            if (startHour < Constants.FIRST_HOUR_CHECK) {
                buffer.append("Start time cannot be before "
                        + Constants.FIRST_HOUR_CHECK + " AM .\n");
            }

            // we limit the last hour checkable
            // case > 19h
            if (endHour > Constants.LAST_HOUR_CHECK) {
                buffer.append("End time cannot be after "
                        + Constants.LAST_HOUR_CHECK + " PM .\n");
            }
            // case 19hXX
            if (endHour == Constants.LAST_HOUR_CHECK && endMinutes != 0) {
                buffer.append("End time cannot be after "
                        + Constants.LAST_HOUR_CHECK + " PM .\n");
            }
        }

        // It's redundant, no message
        if ((startHour > endHour)
                || ((startHour == endHour) && startMinutes > endMinutes)) {
            buffer.append(" ");
        }

        // limit the timestamp in the past
        if (nowTimeStamp > startTimeStamp) {
            if ((nowTimeStamp - startTimeStamp) > Constants.MAXIMAL_WEEKS_IN_PAST
                    * ONE_WEEK_IN_MS) {
                buffer.append("You cannot check more than "
                        + Constants.MAXIMAL_WEEKS_IN_PAST
                        + " weeks in the past.\n");
            }
        }

        // limit the timestamp in the future
        if (nowTimeStamp < startTimeStamp) {
            if ((startTimeStamp - nowTimeStamp) > Constants.MAXIMAL_WEEKS_IN_FUTURE
                    * ONE_WEEK_IN_MS) {
                buffer.append("You cannot check more than "
                        + Constants.MAXIMAL_WEEKS_IN_FUTURE
                        + " weeks in the future.\n");
            }
        }

        return buffer.toString();
    }

    /**
     * getNextValidPeriod() should return the next valid period to check,
     * MON-FRI 8am-7pm
     * <p/>
     * MON-FRI 8am-6pm should return nextHour to nextHour+1, minutes to 0.
     * <p/>
     * MON-FRI 6am-6:55pm should return exact min up to 7pm
     * <p/>
     * All night (after 6:55pm) should return next day 8am-9am
     * <p/>
     * All weekend (after FR 6:55pm) should return next Monday 8am-9am
     *
     * @return
     */
    public static FRPeriod getNextValidPeriod() {
        return getNextValidPeriod(System.currentTimeMillis());
    }

    public static FRPeriod getNextValidPeriod(Calendar calendar) {
        return getNextValidPeriod(calendar.getTimeInMillis());
    }

    /**
     * Get the next valid period according to the following criterias.
     * <p/>
     * Starting from Friday LAST_HOUR_CHECK until Monday 00h we return Monday
     * from 8h to 9h.
     * <p/>
     * For any week day between 18h and 19h we return the given day from 18h to
     * 19h.
     * <p/>
     * For any week day between LAST_HOUR_CHECK and 00h (except for Friday which
     * is matched by the first rule) we return the next day from 8h to 9h.
     * <p/>
     * For any week day between 00h and FIRST_HOUR_CHECK we return the given day
     * from 8h to 9h.
     * <p/>
     * For any week and between FIRST_HOUR_CHECK and LAST_HOUR_CHECK - 1 we
     * apply the rules described by TimeUtils.roundFRRequestTimestamp(FRPeriod).
     *
     * @param nowTimeStampNeeded The timestamp from which we want the next valid period
     * @return The next valid period
     */
    public static FRPeriod getNextValidPeriod(long nowTimeStampNeeded) {

        Calendar mCalendar = Calendar.getInstance();
        mCalendar.setTimeInMillis(roundSAndMSToZero(nowTimeStampNeeded));
        long tsStart = mCalendar.getTimeInMillis();

        FRPeriod period = new FRPeriod(tsStart, tsStart + ONE_HOUR_IN_MS);

        int day = mCalendar.get(Calendar.DAY_OF_WEEK);
        int hour = mCalendar.get(Calendar.HOUR_OF_DAY);

        if (day == Calendar.SATURDAY || day == Calendar.SUNDAY) {
            return shiftWeekEndToMondayFirstHour(tsStart);
        } else if (day == Calendar.FRIDAY && hour >= Constants.LAST_HOUR_CHECK) {
            return shiftWeekEndToMondayFirstHour(tsStart);
        } else if (hour == Constants.LAST_HOUR_CHECK - 1) {
            mCalendar.set(Calendar.MINUTE, 0);
            return new FRPeriod(mCalendar.getTimeInMillis(),
                    mCalendar.getTimeInMillis() + ONE_HOUR_IN_MS);
        } else if (hour >= 0 && hour < Constants.FIRST_HOUR_CHECK) {
            int hourShift = Constants.FIRST_HOUR_CHECK - hour;
            mCalendar.set(Calendar.MINUTE, 0);
            tsStart = mCalendar.getTimeInMillis() + ONE_HOUR_IN_MS * hourShift;
            return new FRPeriod(tsStart, tsStart + ONE_HOUR_IN_MS);
        } else if (day != Calendar.FRIDAY && hour >= Constants.LAST_HOUR_CHECK
                && hour <= 23) {
            int hourShift = Constants.FIRST_HOUR_CHECK + (24 - hour);
            mCalendar.set(Calendar.MINUTE, 0);
            tsStart = mCalendar.getTimeInMillis() + ONE_HOUR_IN_MS * hourShift;
            return new FRPeriod(tsStart, tsStart + ONE_HOUR_IN_MS);
        } else {
            return getNextValidPeriodDuringDay(period);
        }
    }

    /**
     * Round the timestamps of a given FRRequest.
     * <p/>
     * If the minutes (e.g 10h32, the minutes are 32) are between 0 and 29 we
     * round to the previous complete hour (10h23 -> 10h00) and the total period
     * has length ONE hour.
     * <p/>
     * If the minutes are between 30 and 59 we round to the previous complete
     * hour (10h45-> 10h00) and the total period has length TWO hours.
     *
     * @param period The period to round
     * @return The new period rounded
     */
    private static FRPeriod getNextValidPeriodDuringDay(FRPeriod period) {
        long tsStart = period.getTimeStampStart();
        long tsEnd;

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

        return new FRPeriod(tsStart, tsEnd);
    }

    private static FRPeriod shiftWeekEndToMondayFirstHour(long timestamp) {
        Calendar mCalendar = Calendar.getInstance();
        mCalendar.setTimeInMillis(roundSAndMSToZero(timestamp));

        mCalendar.set(Calendar.MINUTE, 0);

        int day = mCalendar.get(Calendar.DAY_OF_WEEK);
        int hour = mCalendar.get(Calendar.HOUR_OF_DAY);

        int hourToCompleteDay = 24 - hour;
        if (day == Calendar.FRIDAY) {
            hourToCompleteDay += 2 * 24;
        } else if (day == Calendar.SATURDAY) {
            hourToCompleteDay += 24;
        }

        hourToCompleteDay += Constants.FIRST_HOUR_CHECK;
        long tsStart = mCalendar.getTimeInMillis() + hourToCompleteDay
                * ONE_HOUR_IN_MS;
        return new FRPeriod(tsStart, tsStart + ONE_HOUR_IN_MS);
    }

    /**
     * Compare two calendars to know if it's the same day, basically if their
     * year/month/day are all the same.
     *
     * @param cal1 first calendar to compare
     * @param cal2 second calendar to compare
     * @return true if their year/month/day are all the same.
     */
    public static boolean compareCalendars(Calendar cal1, Calendar cal2) {
        boolean sameYear = (cal1.get(Calendar.YEAR) == cal2.get(Calendar.YEAR));
        boolean sameMonth = (cal1.get(Calendar.MONTH) == cal2
                .get(Calendar.MONTH));
        boolean sameDay = (cal1.get(Calendar.DAY_OF_MONTH) == cal2
                .get(Calendar.DAY_OF_MONTH));
        return sameYear && sameMonth && sameDay;
    }

    /**
     * Get a valid period with start at the last hour and end at the end of the
     * current day, if we are before the end of the day. Otherwise, it will
     * return the whole next day (from the start hour till the end hour).
     *
     * @return a valid period covering now till end of the day.
     */
    public static FRPeriod getNextValidPeriodTillEndOfDay() {
        FRPeriod period = FRTimes.getNextValidPeriod();
        Calendar cal = Calendar.getInstance();
        cal.setTimeInMillis(period.getTimeStampStart());

        while (cal.get(Calendar.HOUR_OF_DAY) < Constants.LAST_HOUR_CHECK) {
            cal.roll(Calendar.HOUR_OF_DAY, true);
        }

        period.setTimeStampEnd(cal.getTimeInMillis());

        return period;
    }

    /**
     * Get a valid period with start at the first hour of current day and end at
     * the end of the current day, if we are before the end of the day.
     * Otherwise, it will return the whole next day (from the start hour till
     * the end hour).
     *
     * @return a valid period covering the whole day.
     */
    public static FRPeriod getNextValidPeriodWholeDay() {
        FRPeriod period = FRTimes.getNextValidPeriod();
        Calendar cal = Calendar.getInstance();
        cal.setTimeInMillis(period.getTimeStampStart());

        cal.set(Calendar.HOUR_OF_DAY, Constants.FIRST_HOUR_CHECK);
        cal.set(Calendar.MINUTE, 0);
        period.setTimeStampStart(cal.getTimeInMillis());

        cal.set(Calendar.HOUR_OF_DAY, Constants.LAST_HOUR_CHECK);
        cal.set(Calendar.MINUTE, 0);
        period.setTimeStampEnd(cal.getTimeInMillis());

        return period;
    }

    /**
     * Round the given timestamp to the previous hour.
     *
     * @param timestamp The timestamp to round
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
     * @param timestamp The timestamp to round
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
     * @param tsStart The timestamp of the start of the period
     * @param tsEnd   The timestamp of the end of the period
     * @return The number of hours between the two timestamps
     */
    public static long determineNumberHour(long tsStart, long tsEnd) {
        long startHour = roundHourBefore(tsStart);
        long endHour = roundHourAfter(tsEnd);

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
     * @param timeStamp The timestamp to round
     * @return The timestamp rounded.
     */
    public static long roundSAndMSToZero(long timeStamp) {
        Calendar mCalendar = Calendar.getInstance();
        mCalendar.setTimeInMillis(timeStamp);
        mCalendar.set(Calendar.SECOND, 0);
        mCalendar.set(Calendar.MILLISECOND, 0);
        return mCalendar.getTimeInMillis();
    }

    public static FRPeriod roundFRRequestTimestamp(FRPeriod period) {
        long tsStart = roundSAndMSToZero(period.getTimeStampStart());
        long tsEnd = roundSAndMSToZero(period.getTimeStampEnd());
        long minStart = tsStart % ONE_HOUR_IN_MS;
        long minEnd = tsEnd % ONE_HOUR_IN_MS;

        // round to the previous hour
        tsStart -= minStart;

        Calendar mCalendar = Calendar.getInstance();
        mCalendar.setTimeInMillis(tsEnd);
        int hourEnd = mCalendar.get(Calendar.HOUR_OF_DAY);

        // if we are not a full hour, take the next hour is possible
        if (minEnd != 0) {
            hourEnd = Math.min(Constants.LAST_HOUR_CHECK, hourEnd + 1);
            mCalendar.set(Calendar.HOUR_OF_DAY, hourEnd);
            mCalendar.set(Calendar.MINUTE, 0);
            tsEnd = mCalendar.getTimeInMillis();
        }

        return new FRPeriod(tsStart, tsEnd);
    }

    public static String convertTimeStampInString(long timestamp) {
        Calendar mCalendar = Calendar.getInstance();
        mCalendar.setTimeInMillis(timestamp);

        int year = mCalendar.get(Calendar.YEAR);
        int month = mCalendar.get(Calendar.MONTH) + 1;
        int day = mCalendar.get(Calendar.DAY_OF_MONTH);

        return year + "-" + month + "-" + day;
    }
}
