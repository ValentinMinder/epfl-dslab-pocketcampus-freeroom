package org.pocketcampus.plugin.satellite.android.iface;

import org.pocketcampus.android.platform.sdk.core.IView;

/**
 * The interface that defines the public methods for SatelliteMainView
 * 
 * @author Oriane <oriane.rodriguez@epfl.ch>
 * 
 */
public interface ISatelliteMainView extends IView {

	/**
	 * Called when the beer of the month is updated in the SatelliteModel
	 */
	public void beerUpdated();
	
	/**
	 * Called when the list of beers is updated in the SatelliteModel
	 */
	public void beersUpdated();
	
	/**
	 * Called when the list of sandwiches is updated in the SatelliteModel
	 */
	public void sandwichesUpdated();
	
	/**
	 * Called when the affluence is updated in the SatelliteModel
	 */
	public void affluenceUpdated();
	
	/**
	 * Called when the list of events is updated in the SatelliteModel
	 */
	public void eventsUpdated();
}
