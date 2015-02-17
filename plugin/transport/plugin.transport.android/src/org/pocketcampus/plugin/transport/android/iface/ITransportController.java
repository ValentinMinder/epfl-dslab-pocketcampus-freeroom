package org.pocketcampus.plugin.transport.android.iface;

import org.pocketcampus.plugin.transport.shared.TransportStation;

/**
 * The interface that defines the methods implemented by a controller of the
 * plugin.
 * 
 * @author silviu@pocketcampus.org
 * 
 */
public interface ITransportController {

	/** Initiates a request to the server for the default stations */

	void getDefaultStations();

	/** Searches for station matching a name */
	boolean searchForStations(String stationName);

	/**
	 * Searches for a trip from a station to another station
	 */
	void searchForTrips(TransportStation from, TransportStation to);

}
