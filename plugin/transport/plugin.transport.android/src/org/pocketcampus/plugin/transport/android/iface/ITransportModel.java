package org.pocketcampus.plugin.transport.android.iface;

import java.util.List;

import org.pocketcampus.plugin.transport.shared.Location;
import org.pocketcampus.plugin.transport.shared.QueryConnectionsResult;

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
	 * 
	 * @param result
	 */
	public void setConnections(QueryConnectionsResult result);
}
