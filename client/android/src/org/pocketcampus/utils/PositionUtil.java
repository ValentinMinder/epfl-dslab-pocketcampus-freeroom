package org.pocketcampus.utils;

import org.pocketcampus.R;

import android.content.Context;
import android.location.Location;

public class PositionUtil {
	
	/**
	 * Checks if the given location is inside the campus area
	 * @param context the application context (needed to obtain the resources)
	 * @param location the location to check.
	 * @return true if the location is inside the campus area, false otherwise.
	 */
	public static boolean isLocationOnCampus(Context context, Location location) {
		double minLat = Double.parseDouble(context.getResources().getString(R.string.map_campus_min_latitude));
		double maxLat = Double.parseDouble(context.getResources().getString(R.string.map_campus_max_latitude));
		double minLon = Double.parseDouble(context.getResources().getString(R.string.map_campus_min_longitude));
		double maxLon = Double.parseDouble(context.getResources().getString(R.string.map_campus_max_longitude));
		
		return (location.getLatitude() > minLat &&
				location.getLatitude() < maxLat &&
				location.getLongitude() > minLon &&
				location.getLongitude() < maxLon);
	}
}
