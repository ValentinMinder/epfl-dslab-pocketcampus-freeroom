package org.pocketcampus.plugin.satellite.android.iface;


/**
 * The interface that defines the public methods for SatelliteEventsView
 * 
 * @author Oriane <oriane.rodriguez@epfl.ch>
 * 
 */
public interface ISatelliteEventsView extends ISatelliteMainView {

	/**
	 * Called when the list of events is updated in the SatelliteModel
	 */
	public void eventsUpdated();
}
