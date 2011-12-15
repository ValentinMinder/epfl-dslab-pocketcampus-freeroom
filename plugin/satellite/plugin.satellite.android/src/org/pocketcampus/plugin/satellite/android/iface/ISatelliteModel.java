package org.pocketcampus.plugin.satellite.android.iface;

import org.pocketcampus.plugin.satellite.shared.Affluence;
import org.pocketcampus.plugin.satellite.shared.Beer;

/**
 * The interface that defines the public methods of SatelliteModel
 * 
 * @author Oriane <oriane.rodriguez@epfl.ch>
 * 
 */
public interface ISatelliteModel {

	/**
	 * Gets the beer of the month
	 */
	public Beer getBeerOfMonth();
	
	/**
	 * Sets the beer of the month
	 */
	public void setBeerOfMonth(Beer beer);
	
	/**
	 * Get the current affluence at Satellite
	 */
	public Affluence getAffluence();
	
	/**
	 * Sets the affluence
	 */
	public void setAffluence(Affluence affluence);
}
