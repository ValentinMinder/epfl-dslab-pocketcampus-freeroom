package org.pocketcampus.plugin.freeroom.server.utils;

import org.pocketcampus.plugin.freeroom.shared.FRPeriod;
import org.pocketcampus.plugin.freeroom.shared.FreeRoomRequest;

/**
 * This is an utility class doing useful conversions, and defining a few
 * constants.
 * 
 * @author FreeFroom Project Team - Julien WEBER <julien.weber@epfl.ch> and
 *         Valentin MINDER <valentin.minder@epfl.ch>
 * 
 */
public class Utils {
	public static final long m30s_IN_MS = 30 * 1000;

	/**
	 * Adjust the period given in the request. It adds 30s to the lower
	 * bound, substract 30s from the upper bound. It is used to allow a
	 * margin for error with the timestamps
	 * 
	 * @param req
	 *            The intial request issued by the client
	 * @return The new request with correct timestamps.
	 */
	public static FreeRoomRequest convertMinPrecision(FreeRoomRequest req) {
		FRPeriod period = req.getPeriod();
		period.setTimeStampStart(period.getTimeStampStart() + m30s_IN_MS);
		period.setTimeStampEnd(period.getTimeStampEnd() - m30s_IN_MS);

		return new FreeRoomRequest(period);

	}
}
