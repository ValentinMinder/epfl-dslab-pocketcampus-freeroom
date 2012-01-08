package org.pocketcampus.plugin.satellite.android.iface;

/**
 * The interface that defines the public methods of the
 * <code>SatelliteController</code>.
 * 
 * @author Oriane <oriane.rodriguez@epfl.ch>
 * 
 */
public interface ISatelliteController {

	/**
	 * Initiates a request to the server to get the beer of the month.
	 */
	public void getBeerOfMonth();

	/**
	 * Initiates a request to the server to get the affluence at Satellite.
	 */
	public void getAffluence();
}
