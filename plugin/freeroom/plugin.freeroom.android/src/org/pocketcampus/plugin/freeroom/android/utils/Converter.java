package org.pocketcampus.plugin.freeroom.android.utils;

import java.util.Calendar;

import org.pocketcampus.plugin.freeroom.shared.FRDay;
import org.pocketcampus.plugin.freeroom.shared.FRFreeRoomRequestFromTime;
import org.pocketcampus.plugin.freeroom.shared.FRPeriod;
import org.pocketcampus.plugin.freeroom.shared.FRTimeStamp;

public class Converter {
	public static FRFreeRoomRequestFromTime convert (FRDay day, int startHour, int endHour) {
		Calendar calendar = Calendar.getInstance();
		calendar.setFirstDayOfWeek(Calendar.MONDAY);
		int today_day = calendar.get(Calendar.DAY_OF_WEEK);
		int now_hour = calendar.get(Calendar.HOUR_OF_DAY);
		
		//first case we are the given day but the specified hour is before, thus need to go to the next given day
		//also the case for cases where the given day is different from today
		//BE CAREFUL WHEN USING FRDay, it is an enum and thus monday is mapped to 0 !
		long timestampshift = 0;
		if (now_hour > startHour && day.getValue() + 1 == today_day) {
			timestampshift = 7*3600*24*1000;
		} else if (day.getValue() + 1 != today_day) {
			if (today_day > day.getValue() + 1) {
				timestampshift = (day.getValue() + 1 - today_day)*24*3600*1000;
			} else {
				int daysToCompleteWeek = 7 - today_day;
				int daysToGo = daysToCompleteWeek + day.getValue() + 1;
				timestampshift = daysToGo*24*3600*1000;
			}
		}
		
		calendar.setTimeInMillis(calendar.getTimeInMillis() + timestampshift);
		calendar.set(Calendar.HOUR_OF_DAY, startHour);
		
		int t_start = (int) (calendar.getTimeInMillis() / 1000);
		
		
		int t_end = t_start + (endHour - startHour)*3600;
//		//find the next day that correspond to the given day
//		long now_ms = now.getTimeInMillis();
//		for (int i = day_week+7; i>day.getValue(); i--) {
//			now_ms += (3600*24*1000);
//		}
//		now.setTimeInMillis(now_ms);
//		
//		int year = now.get(Calendar.YEAR);
//		int month = now.get(Calendar.MONTH);
//		int day_month = now.get(Calendar.DAY_OF_MONTH);
//		
//		
//		Calendar start = Calendar.getInstance();
//		start.set(year, month, day_month, startHour, 0);
//		int t_start = (int) (start.getTimeInMillis()/1000);
		FRTimeStamp ts_start = new FRTimeStamp();
		ts_start.setTimeSeconds(t_start);
//		
//		Calendar end = Calendar.getInstance();
//		end.set(year, month, day_month, endHour, 0);
//		int t_end = (int) (start.getTimeInMillis()/1000);
		FRTimeStamp ts_end = new FRTimeStamp();
		ts_end.setTimeSeconds(t_end);
		
		FRPeriod period = new FRPeriod(ts_start, ts_end, false);
		
		FRFreeRoomRequestFromTime req = new FRFreeRoomRequestFromTime(period);
		
		return req;
	}
}
