package org.pocketcampus.plugin.freeroom.android.utils;

import java.util.Calendar;

import org.pocketcampus.plugin.freeroom.shared.FRDay;
import org.pocketcampus.plugin.freeroom.shared.FRFreeRoomRequestFromTime;
import org.pocketcampus.plugin.freeroom.shared.FRPeriod;
import org.pocketcampus.plugin.freeroom.shared.FRTimeStamp;

public class Converter {
	public static FRFreeRoomRequestFromTime convert (FRDay day, int startHour, int endHour) {
		Calendar now = Calendar.getInstance();
		
		int day_week = now.get(Calendar.DAY_OF_WEEK);
		//find the next day that correspond to the given day
		long now_ms = now.getTimeInMillis();
		for (int i = day_week+7; i>day.getValue(); i--) {
			now_ms += (3600*24*1000);
		}
		now.setTimeInMillis(now_ms);
		
		int year = now.get(Calendar.YEAR);
		int month = now.get(Calendar.MONTH);
		int day_month = now.get(Calendar.DAY_OF_MONTH);
		
		
		Calendar start = Calendar.getInstance();
		start.set(year, month, day_month, startHour, 0);
		int t_start = (int) (start.getTimeInMillis()/1000);
		FRTimeStamp ts_start = new FRTimeStamp();
		ts_start.setTimeSeconds(t_start);
		
		Calendar end = Calendar.getInstance();
		end.set(year, month, day_month, endHour, 0);
		int t_end = (int) (start.getTimeInMillis()/1000);
		FRTimeStamp ts_end = new FRTimeStamp();
		ts_end.setTimeSeconds(t_end);
		
		FRPeriod period = new FRPeriod(ts_start, ts_end, false);
		
		FRFreeRoomRequestFromTime req = new FRFreeRoomRequestFromTime(period);
		
		return req;
	}
}
