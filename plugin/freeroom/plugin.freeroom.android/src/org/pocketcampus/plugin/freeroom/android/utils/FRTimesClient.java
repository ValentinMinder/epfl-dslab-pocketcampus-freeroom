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
	 * Constructor.
	 * <p>
	 * UI and main thread should always use this one.
	 * 
	 * @param context
	 *            context of the application.
	 */
	public FRTimesClient(Context context) {
		this.context = context;
		instance = this;
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
	public String getDateText(Calendar selected, SimpleDateFormat sdf) {
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
	 * @param period
	 *            the period of time
	 * @return a string summary of a given period of time.
	 */
	public String generateFullTimeSummary(FRPeriod period) {
		StringBuilder build = new StringBuilder(100);
		Date endDate = new Date(period.getTimeStampEnd());
		Date startDate = new Date(period.getTimeStampStart());
		SimpleDateFormat day_month = new SimpleDateFormat(
				context.getString(R.string.freeroom_pattern_day_format_default));
		SimpleDateFormat hour_min = new SimpleDateFormat(
				context.getString(R.string.freeroom_pattern_hour_format_default));

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
	 * eg: "9:00\n12:00pm"
	 * 
	 * @param period
	 *            the period of time
	 * @return a string summary of a given period of time.
	 */
	public String generateShortTimeSummary(FRPeriod period) {
		StringBuilder build = new StringBuilder(100);
		Date endDate = new Date(period.getTimeStampEnd());
		Date startDate = new Date(period.getTimeStampStart());
		SimpleDateFormat hour_min = new SimpleDateFormat(
				context.getString(R.string.freeroom_pattern_hour_format_default));

		build.append(hour_min.format(startDate));
		build.append(" - ");
		build.append(hour_min.format(endDate));
		return build.toString();
	}

}
