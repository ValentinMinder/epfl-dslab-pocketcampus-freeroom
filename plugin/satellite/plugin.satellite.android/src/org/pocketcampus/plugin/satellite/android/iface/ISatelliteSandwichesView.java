package org.pocketcampus.plugin.satellite.android.iface;


/**
 * The interface that defines the public methods for SatelliteSandwichView
 * 
 * @author Oriane <oriane.rodriguez@epfl.ch>
 * 
 */
public interface ISatelliteSandwichesView extends ISatelliteMainView {

	/**
	 * Called when the list of sandwiches is updated in the SatelliteModel
	 */
	public void sandwichesUpdated();
}
