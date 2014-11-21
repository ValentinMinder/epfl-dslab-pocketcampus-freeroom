package org.pocketcampus.plugin.freeroom.android.utils;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

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
	 * Storing the chosen locale, overriding system locale.
	 */
	private Locale locale;
	/**
	 * Pattern for the DATE format, if wanting to override locale (is set to
	 * null or empty if using the locale).
	 */
	private String daypattern;

	/**
	 * Constructor.
	 * <p>
	 * UI and main thread should always use this one.
	 * 
	 * @param context
	 *            context of the application.
	 */
	public FRTimesClient(Context context, Locale locale, String daypattern) {
		this.context = context;
		instance = this;
		this.locale = locale;
		this.daypattern = daypattern;
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
	 * Return the appropriated formatted summary of a selected date.
	 * <p>
	 * Formatting is Locale-dependent: it will use the device language settings<br>
	 * English format: Saturday, May 17, 2014 <br>
	 * French format: samedi, 17 mai 2014 <br>
	 * <p>
	 * Instead of the usual format "Wed 24 May", the date is summarize to
	 * "today", "yesterday", "tomorrow" when relevant.
	 * <p>
	 * Formatting can be changed, if it has been overridden, it will look like:
	 * <br>
	 * English format on French language: samedi, mai 17, 2014 <br>
	 * French format on English language: Saturday, 17 May 2014 <br>
	 * IT AFFECTS ONLY FORMATTING, NEVER THE LANGUAGE !!! <br>
	 * 
	 * @param selected
	 *            the selected time as a calendar
	 * @return a well-formatted DATE summary of the date.
	 */
	public String formatFullDate(Calendar selected) {
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
			// Formatting will use the device language settings
			// if it has been overridden, it will these one:
			// English format: Saturday, May 17, 2014
			// French format: samedi, 17 mai 2014
			// IT AFFECTS ONLY FORMATTING, NEVER THE LANGUAGE !!!
			if (daypattern != null && daypattern != "") {
				SimpleDateFormat sdf = new SimpleDateFormat(daypattern, locale);
				return sdf.format(selected.getTime());
			}
			DateFormat df = DateFormat.getDateInstance(DateFormat.FULL, locale);
			return df.format(selected.getTime());
		}
	}

	/**
	 * Return the appropriated formatted DATE AND TIME summary of the period.
	 * <p>
	 * eg: "Wednesday, Apr 24, 2014, 9:00 AM - 12:00 PM"
	 * <p>
	 * Date formatting (specified in {@link formatFullDate})<br>
	 * Formatting is Locale-dependent: it will use the device language settings<br>
	 * English format: Saturday, May 17, 2014 <br>
	 * French format: samedi 17 mai 2014 <br>
	 * Instead of the usual format "Wed 24 May", the date is summarize to
	 * "today", "yesterday", "tomorrow" when relevant.
	 * <p>
	 * Time formatting (specified in {@link generateShortTimeSummary})
	 * 
	 * @param period
	 *            the period of time
	 * @return a well-formatted DATE AND TIME summary of the period.
	 */
	public String formatFullDateFullTimePeriod(FRPeriod period) {
		StringBuilder build = new StringBuilder(100);
		Calendar selected = Calendar.getInstance();
		selected.setTimeInMillis(period.getTimeStampEnd());
		build.append(formatFullDate(selected));
		build.append(", ");
		build.append(formatTimePeriod(period, false, false));
		return build.toString();
	}

	/**
	 * Return the appropriated formatted TIME summary of the period.
	 * <p>
	 * Example of formatting: "9:00 AM - 12:00 PM"<br>
	 * With space disabled: eg: "9:00 AM-12:00 PM"
	 * <p>
	 * Time formatting: "9:00 PM", "21:00"<br>
	 * With veryshort enabled: "9PM", "21h"
	 * <p>
	 * Choosing between 9:00 PM and 21:00 is done by default user settings.
	 * 
	 * @param period
	 *            the period of time
	 * @param veryshort
	 *            if the string returned should be even shorter.
	 * @param spaces
	 *            if a space should be put between the two period.
	 * @return a well-formatted TIME summary of the period.
	 */
	public String formatTimePeriod(FRPeriod period, boolean veryshort,
			boolean spaces) {
		StringBuilder build = new StringBuilder(100);
		build.append(formatTime(period.getTimeStampStart(), veryshort));
		if (spaces) {
			build.append(" - ");
		} else {
			build.append("-");
		}
		build.append(formatTime(period.getTimeStampEnd(), veryshort));
		return build.toString();
	}

	/**
	 * Return the appropriated formatted TIME summary of the given time.
	 * <p>
	 * eg: "9:00 AM"
	 * <p>
	 * Time formatting: "9:00 PM", "21:00"<br>
	 * With veryshort enabled: "9PM", "21h"
	 * <p>
	 * Choosing between 9:00 PM and 21:00 is done by default user settings.
	 * 
	 * @param time
	 *            the point of time given
	 * @param veryshort
	 *            if the string returned should be even shorter.
	 * @return a well-formatted TIME summary of the given time.
	 */
	public String formatTime(long time, boolean veryshort) {
		Date startDate = new Date(time);

		if (veryshort) {
			// default: 21h
			String pattern = "H'h'";
			android.text.format.DateFormat.getTimeFormat(context);
			if (!android.text.format.DateFormat.is24HourFormat(context)) {
				// if we want am/pm: 9PM
				pattern = "ha";
			}
			SimpleDateFormat hour_min = new SimpleDateFormat(pattern);
			return hour_min.format(startDate);
		} else {
			java.text.DateFormat df = android.text.format.DateFormat
					.getTimeFormat(context);
			return df.format(startDate);
		}

	}

	/**
	 * Generates the time summary with a prefix. You can choose if the prefix
	 * will be displayed or not.
	 * 
	 * @param prefix
	 *            eg. "start"
	 * @param displayPrefix
	 *            if the prefix should be printed.
	 * @param timeSummary
	 *            time to display as a formatted string
	 * @return a formatted time with an optional prefix.
	 */
	public String generateTimeSummaryWithPrefix(String prefix,
			boolean displayPrefix, String timeSummary) {
		String returned = "";
		if (displayPrefix) {
			returned = prefix + " " + timeSummary;
		} else {
			returned = timeSummary;
		}
		return returned;
	}

	/**
	 * Return the {@link #formatTimePeriod(FRPeriod, boolean, boolean)} with
	 * <code>(period, true, false);</code> unless the period is strictly now and
	 * less or equal to two hours.
	 * 
	 * @param period
	 *            the given period.
	 * @return the format time period.
	 */
	public String formatTimeSummaryTitle(FRPeriod period) {
		long now = System.currentTimeMillis();
		if (now > period.getTimeStampStart() - FRTimes.MARGIN_ERROR
				&& now < period.getTimeStampEnd() + FRTimes.MARGIN_ERROR
				&& (period.getTimeStampEnd() - period.getTimeStampStart()) <= 2
						* FRTimes.ONE_HOUR_IN_MS + FRTimes.MARGIN_ERROR) {
			return context.getString(R.string.freeroom_now);
		}
		return formatTimePeriod(period, true, false);
	}
}
