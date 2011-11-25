package org.pocketcampus.plugin.satellite.android.iface;

/**
 * The interface that defines the public methods of SatelliteController
 * 
 * @author Oriane <oriane.rodriguez@epfl.ch
 * 
 */
public interface ISatelliteController {

	/**
	 * Initiates a request to the server to get the beer of the month
	 */
	public void getBeerOfMonth();
	
	/**
	 * Initiates a request to the server to get the affluence at Satellite
	 */
	public void getAffluence();

	/**
	 * Initiates a request to the server to get the list of all beers Satellite
	 * proposes
	 */
//	public void getAllBeers();

	/**
	 * Initiates a request to the server to get the list of sandwiches Satellite
	 * proposes
	 */
//	public void getSandwiches();

	/**
	 * Initiates a request to the server to get the list of next events at
	 * Satellite
	 */
//	public void getEvents();

}
