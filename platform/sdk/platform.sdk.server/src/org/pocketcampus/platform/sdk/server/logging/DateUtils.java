package org.pocketcampus.platform.sdk.server.logging;

import java.util.Date;

public class DateUtils {
	private final static int NB_SECONDS_IN_DAY = 60 * 60 * 24;
	private final static int NB_SECONDS_IN_HOUR = 60 * 60;
	private final static int NB_SECONDS_IN_MINUTE = 60;
	
	/**
	 * Formats the difference between two dates in a nice way.
	 * Eg: "23 seconds", "1 hour and 15 minutes", "3 days, 46 minutes and 1 second"
	 * @param startDate
	 * @param endDate
	 * @param simultanateDates Text if the dates are the same
	 * @return
	 */
	public static String formatDateDelta(Date startDate, Date endDate, String simultaneousDates) {
		int delta = (int) ((endDate.getTime() - startDate.getTime()) / 1000);
		
		if(delta <= NB_SECONDS_IN_MINUTE) {
			return simultaneousDates;
		}
		
		int nbDays = 0;
		int nbHours = 0;
		int nbMinutes = 0;
		
		while(delta > NB_SECONDS_IN_DAY) {
			delta -= NB_SECONDS_IN_DAY;
			nbDays++;
		}
		
		while(delta > NB_SECONDS_IN_HOUR) {
			delta -= NB_SECONDS_IN_HOUR;
			nbHours++;
		}
		
		while(delta > NB_SECONDS_IN_MINUTE) {
			delta -= NB_SECONDS_IN_MINUTE;
			nbMinutes++;
		}
		
		String formattedDateDelta = "";
		
		if(nbDays > 0) {
			formattedDateDelta += nbDays + " " + "day" + plural(nbDays);
		}
		
		if(nbHours > 0) {
			if(nbDays > 0) {
				if(nbMinutes==0 || nbDays>0) {
					formattedDateDelta += " " + "and" + " ";
				} else {
					formattedDateDelta += ", ";
				}
				
			}
			
			formattedDateDelta += nbHours + " " + "hour" + plural(nbHours);
		}
		
		// skip the minutes if more than a day
		if(nbMinutes > 0 && nbDays == 0) {
			if(nbHours > 0) {
				if(nbDays == 0) {
					formattedDateDelta += " and ";
				} else {
					formattedDateDelta += ", ";
				}
			}
			
			formattedDateDelta += nbMinutes + " " + "minute" + plural(nbMinutes);
		}
		
		return formattedDateDelta;
	}

	private static String plural(int nb) {
		return nb>1?"s":"";
	}

}















