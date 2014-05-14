package org.pocketcampus.plugin.freeroom.android.utils;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import org.pocketcampus.plugin.freeroom.R;
import org.pocketcampus.plugin.freeroom.shared.FRPeriod;
import org.pocketcampus.plugin.freeroom.shared.utils.FRTimes;

import android.content.Context;

/**
 * <code>FRTimesClient</code> give some times utility method for the client,
 * that have nothing to do in UI and View classes.
 * <p>
 * None of them are useful to the server, so that's why the are not shared.
 * <p>
 * Most of them are used at only one place and are very specific. Be careful
 * when reusing it if you're not sure what it does exactly.
 * 
 * @author FreeRoom Project Team (2014/05)
 * @author Julien WEBER <julien.weber@epfl.ch>
 * @author Valentin MINDER <valentin.minder@epfl.ch>
 */
public class FRTimesClient {
	/**
	 * Holder of context (useful for getString).
	 */
	private Context context;
	/**
	 * Instance for accessing without a context.
	 */
	private static FRTimesClient instance;
	/**
	 * Pattern for day formatting.
	 */
	private String day_pattern;
	/**
	 * Pattern for hour formatting, long format (8:00 AM/8h00).
	 */
	private String hour_pattern_long;
	/**
	 * Pattern for hour formatting, long format (8 AM/8h)
	 */
	private String hour_pattern_short;

	/**
	 * Constructor.
	 * <p>
	 * UI and main thread should always use this one.
	 * 
	 * @param context
	 *            context of the application.
	 */
	public FRTimesClient(Context context, String day, String hour_long,
			String hour_short) {
		this.context = context;
		instance = this;
		this.day_pattern = day;
		this.hour_pattern_long = hour_long;
		this.hour_pattern_short = hour_short;
	}

	/**
	 * Return the instance of the FRTimesClient, for classes without a context!
	 * <p>
	 * DOES NOT CONSCTRUCT THE OBJECT! THREAD CALL FROM THE UI MUST CALL THE
	 * CONSTRUCTOR FIRST!
	 * 
	 * @return
	 */
	public static FRTimesClient getInstance() {
		return instance;
	}

	/**
	 * Generates the summary of a selected date.
	 * <p>
	 * Instead of the usual format "Wed 24 May", the date is summarize to
	 * "today", "yesterday", "tomorrow" when relevant.
	 * 
	 * @param selected
	 *            the selected time
	 * @param sdf
	 *            the chosen formatter for date
	 * @return a printable summary of the date.
	 */
	public String getDateText(Calendar selected) {
		SimpleDateFormat sdf = new SimpleDateFormat(day_pattern);
		// creating now time reference
		Calendar now = Calendar.getInstance();
		// creating tomorrow time reference
		Calendar tomorrow = Calendar.getInstance();
		tomorrow.roll(Calendar.DAY_OF_MONTH, true);
		// creating yesterday time reference
		Calendar yesterday = Calendar.getInstance();
		yesterday.roll(Calendar.DAY_OF_MONTH, false);

		if (FRTimes.compareCalendars(now, selected)) {
			return context.getString(R.string.freeroom_search_today);
		} else if (FRTimes.compareCalendars(tomorrow, selected)) {
			return context.getString(R.string.freeroom_search_tomorrow);
		} else if (FRTimes.compareCalendars(yesterday, selected)) {
			return context.getString(R.string.freeroom_search_yesterday);
		} else {
			// default case: eg. "Wed May 24"
			return sdf.format(selected.getTime());
		}
	}

	/**
	 * Generates a string summary of a given period of time.
	 * <p>
	 * eg: "Wednesday Apr 24 from 9am to 12pm"
	 * 
	 * todo do agin
	 * 
	 * @param period
	 *            the period of time
	 * @return a string summary of a given period of time.
	 */
	public String generateFullTimeSummary(FRPeriod period) {
		StringBuilder build = new StringBuilder(100);
		Date endDate = new Date(period.getTimeStampEnd());
		Date startDate = new Date(period.getTimeStampStart());
		SimpleDateFormat day_month = new SimpleDateFormat(day_pattern);
		SimpleDateFormat hour_min = new SimpleDateFormat(hour_pattern_long);

		build.append(" ");
		// TODO: if date is today, use "today" instead of specifying date
		build.append(context
				.getString(R.string.freeroom_check_occupancy_result_onthe));
		build.append(" ");
		build.append(day_month.format(startDate));
		build.append(" ");
		build.append(context
				.getString(R.string.freeroom_check_occupancy_result_from));
		build.append(" ");
		build.append(hour_min.format(startDate));
		build.append(" ");
		build.append(context
				.getString(R.string.freeroom_check_occupancy_result_to));
		build.append(" ");
		build.append(hour_min.format(endDate));
		return build.toString();
	}

	/**
	 * Generates a short string summary of a given period of time.
	 * <p>
	 * eg: "9:00 AM - 12:00 PM ", or "9 AM - 12 PM" for shorter.
	 * 
	 * @param period
	 *            the period of time
	 * @param veryshort
	 *            if the string returned should be even shorter.
	 * @return a string summary of a given period of time.
	 */
	public String generateShortTimeSummary(FRPeriod period, boolean veryshort) {
		StringBuilder build = new StringBuilder(100);
		Date endDate = new Date(period.getTimeStampEnd());
		Date startDate = new Date(period.getTimeStampStart());
		String patern = hour_pattern_long;
		if (veryshort) {
			patern = hour_pattern_short;
		}
		SimpleDateFormat hour_min = new SimpleDateFormat(patern);

		build.append(hour_min.format(startDate));
		build.append(" - ");
		build.append(hour_min.format(endDate));
		return build.toString();
	}

	/**
	 * Generates a short string summary of a given point of time.
	 * <p>
	 * eg: "9:00 AM", or "9 AM" for shorter.
	 * 
	 * @param period
	 *            the period of time
	 * @param veryshort
	 *            if the string returned should be even shorter.
	 * @return a string summary of a given period of time.
	 */
	public String generateShortTimeSummary(long time, boolean veryshort) {
		StringBuilder build = new StringBuilder(100);
		Date startDate = new Date(time);
		String patern = hour_pattern_long;
		if (veryshort) {
			patern = hour_pattern_short;
		}
		SimpleDateFormat hour_min = new SimpleDateFormat(patern);

		build.append(hour_min.format(startDate));
		return build.toString();
	}

	/**
	 * Generates the time summary with a prefix. You can choose if the prefix
	 * will be displayed or not.
	 * 
	 * @param prefix
	 *            eg. "start"
	 * @param displayPrefix
	 *            if the prefix should be printed.
	 * @param time
	 *            time to display as a formatted string
	 * @return a formatted time with an optional prefix.
	 */
	public String generateTime(String prefix, boolean displayPrefix, String time) {
		String returned = "";
		if (displayPrefix) {
			returned = prefix + " " + time;
		} else {
			returned = time;
		}
		return returned;
	}

}
