package org.pocketcampus.plugin.bikes.android.iface;

/**
 * Interface to the public methods of the Bikes Controller.
 * @author Pascal <pascal.scheiben@gmail.com>
 *
 */
public interface IBikesController {
	
	/**
	 * Initiates a request to the server to get the BikeEmplacement.
	 */
	public void getAvailableBikes();

}
