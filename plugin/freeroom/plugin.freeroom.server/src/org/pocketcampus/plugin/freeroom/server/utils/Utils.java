package org.pocketcampus.plugin.freeroom.server.utils;

import org.pocketcampus.plugin.freeroom.shared.FRPeriod;
import org.pocketcampus.plugin.freeroom.shared.FreeRoomRequest;

public class Utils {
	public static final long ONE_MIN_IN_MS = 60 * 1000;
	public static final long ONE_HOUR_IN_MS = 60 * 60 * 1000;
	public static final long ONE_DAY_IN_MS = ONE_HOUR_IN_MS * 24;
	public static final long ONE_WEEK_IN_MS = ONE_DAY_IN_MS * 7;

	/**
	 * Adjust the period given in the request. It adds one minutes to the lower
	 * bound, substract one min from the upper bound. It is used to allow a 
	 * margin for error with the timestamps
	 * 
	 * @param req The intial request issued by the client
	 * @return The new request with correct timestamps.
	 */
	public static FreeRoomRequest convertMinPrecision(FreeRoomRequest req) {
		FRPeriod period = req.getPeriod();
		period.setTimeStampStart(period.getTimeStampStart() + ONE_MIN_IN_MS);
		period.setTimeStampEnd(period.getTimeStampEnd() - ONE_MIN_IN_MS);

		return new FreeRoomRequest(period);

	}
}
