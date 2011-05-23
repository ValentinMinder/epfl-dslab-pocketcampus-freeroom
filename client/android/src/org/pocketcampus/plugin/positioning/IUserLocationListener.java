package org.pocketcampus.plugin.positioning;

import android.location.Location;

public interface IUserLocationListener {
	/**
	 * This method is called when the position of the user is available
	 * @param location the location of the user
	 */
	public void userLocationReceived(Location location);
}
