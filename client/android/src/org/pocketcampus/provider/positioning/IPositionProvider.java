package org.pocketcampus.provider.positioning;

import android.location.Location;


public interface IPositionProvider {
	/**
	 * Returns the best available location
	 * @return the user position.
	 */
	public Location getPosition();
	public Location getGsmPosition();
	public Location getGpsPosition();
	public boolean  userInCampus();
	/**
	 * Starts listening for new positions.
	 * (It enables the GPS)
	 */
	public void startListening();
	/**
	 * Stops listening for position updates.
	 * This method must me called in OnPause
	 */
	public void stopListening();
}
