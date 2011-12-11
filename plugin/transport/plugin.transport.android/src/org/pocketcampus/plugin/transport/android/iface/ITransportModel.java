package org.pocketcampus.plugin.transport.android.iface;

import java.util.HashMap;
import java.util.List;

import org.pocketcampus.plugin.transport.shared.TransportTrip;
import org.pocketcampus.plugin.transport.shared.TransportStation;
import org.pocketcampus.plugin.transport.shared.QueryTripsResult;

/**
 * The interface that defines the methods implemented by a model of the plugin.
 * 
 * @author Oriane <oriane.rodriguez@epfl.ch>
 * @author Pascal <pascal.scheiben@epfl.ch>
 * @author Florian <florian.laurent@epfl.ch>
 * 
 */
public interface ITransportModel {

	/**
	 * @return mPreferredDestinations The list of preferred destinations.
	 */
	public HashMap<String, List<TransportTrip>> getPreferredDestinations();

	/**
	 * Sets all the preferred destinations.
	 * 
	 * @param destinations
	 *            The new list of preferred destinations
	 */
	public void setAutoCompletedDestinations(List<TransportStation> destinations);

	/**
	 * Called when the connection is returned by the server. Notifies the view
	 * that the connections for some destinations have been updated.
	 * 
	 * @param result
	 */
	public void setConnections(QueryTripsResult result);

	/**
	 * Called when the Locations are returned by the server. Notifies the view
	 * that the Locations for the strings have been updated.
	 * 
	 * @param result
	 */
	public void setPreferredDestinations(List<TransportStation> result);
}
