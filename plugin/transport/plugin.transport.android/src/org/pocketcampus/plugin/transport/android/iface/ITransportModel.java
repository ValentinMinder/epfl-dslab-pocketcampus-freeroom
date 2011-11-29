package org.pocketcampus.plugin.transport.android.iface;

import java.util.List;

import org.pocketcampus.plugin.transport.shared.Location;
import org.pocketcampus.plugin.transport.shared.QueryConnectionsResult;

/**
 * The interface that defines the method implemented by a model
 * 
 * @author Oriane <oriane.rodriguez@epfl.ch>
 * @author Pascal <pascal.scheiben@epfl.ch>
 * @author Florian <florian.laurent@epfl.ch>
 * 
 */
public interface ITransportModel {

	/**
	 * @return mPreferredDestinations The list of preferred destinations
	 */
	public List<Location> getPreferredDestinations();

	/**
	 * Set all the preferred destinations
	 * 
	 * @param destinations
	 *            The new list of preferred destinations
	 */
	public void setAutoCompletedDestinations(List<Location> destinations);

	/**
	 * Add a location to the current preferred destinations
	 * 
	 * @param location
	 *            The new destination to add in the preferred destinations
	 */
	public void setNewPreferredDestination(Location location);

	/**
	 * Called when the connection is returned yb the server. Notifies the view
	 * that the connections for some destinations have been updated
	 * 
	 * @param result
	 */
	public void setConnections(QueryConnectionsResult result);
}
