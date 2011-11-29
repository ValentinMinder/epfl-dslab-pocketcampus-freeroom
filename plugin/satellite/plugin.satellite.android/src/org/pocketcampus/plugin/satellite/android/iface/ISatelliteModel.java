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

	/**
	 * Gets the list of all beers Satellite proposes
	 */
//	public List<Beer> getAllBeers();
	
	/**
	 * Sets the list of beers
	 */
//	public void setAllBeers(List<Beer> list);

	/**
	 * Gets the list of sandwiches Satellite proposes
	 */
//	public List<Sandwich> getSandwiches();
	
	/**
	 * Sets the list of sandwiches
	 */
//	public void setSandwiches(List<Sandwich> list);

	/**
	 * Gets the list of next events at Satellite
	 */
//	public List<Event> getEvents();
	
	/**
	 * Sets the list of events
	 */
//	public void setEvents(List<Event> list);

}
