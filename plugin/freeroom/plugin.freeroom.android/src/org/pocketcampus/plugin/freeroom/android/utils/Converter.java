package org.pocketcampus.plugin.freeroom.android.utils;

import java.util.Calendar;

import org.pocketcampus.plugin.freeroom.shared.FreeRoomRequest;
import org.pocketcampus.plugin.freeroom.shared.FRPeriod;
import org.pocketcampus.plugin.freeroom.shared.FRTimeStamp;

public class Converter {
	public static final long ONE_HOUR_IN_MS = 60 * 60 * 1000;
	public static final long ONE_DAY_IN_MS = ONE_HOUR_IN_MS * 24;
	public static final long ONE_WEEK_IN_MS = ONE_DAY_IN_MS * 7;

	public static FreeRoomRequest convert (int day, int startHour, int endHour) {
		Calendar calendar = Calendar.getInstance();
		int today_day = calendar.get(Calendar.DAY_OF_WEEK);
		int now_hour = calendar.get(Calendar.HOUR_OF_DAY);
		
		// first case we are the given day but the specified hour is before, thus need to go to the next given day
		// if you select the current hour, it's this day! But if it's before the current hour, it's the next week!
		// also the case for cases where the given day is different from today
		long timestampshift = 0;
		if (now_hour >= startHour && day == today_day) {
			timestampshift = ONE_WEEK_IN_MS; 
		} else if (day != today_day) {
			if (today_day > day) {
				timestampshift = (day - today_day) * ONE_DAY_IN_MS;
			} else {
				int daysToCompleteWeek = 7 - today_day;
				int daysToGo = daysToCompleteWeek + day;
				timestampshift = daysToGo * ONE_DAY_IN_MS;
			}
		}
		
		calendar.setTimeInMillis(calendar.getTimeInMillis() + timestampshift);
		calendar.set(Calendar.HOUR_OF_DAY, startHour);
		
		long t_start = calendar.getTimeInMillis();
		long t_end = t_start + (endHour - startHour) * ONE_HOUR_IN_MS;

		FRTimeStamp ts_start = new FRTimeStamp(t_start);
		FRTimeStamp ts_end = new FRTimeStamp(t_end);		
		FRPeriod period = new FRPeriod(ts_start, ts_end, false);
		
		FreeRoomRequest req = new FreeRoomRequest(period);
		
		return req;
	}
}
