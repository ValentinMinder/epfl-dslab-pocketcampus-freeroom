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
	 * Returns the favorite stations of the user.
	 * 
	 * @return The list of favorite stations.
	 */
	public HashMap<String, List<TransportTrip>> getFavoriteStations();

	/**
	 * Sets the stations received from the server while the user is typing.
	 * Called each time the user types a character.
	 * 
	 * @param stations
	 *            The new list of auto completed stations.
	 */
	public void setAutoCompletedStations(List<TransportStation> stations);

	/**
	 * Called when the connections are returned by the server. Notifies the view
	 * that the connections between two stations have been updated.
	 * 
	 * @param result
	 *            A <code>QueryTripsResult</code> consisting of the connections
	 *            between two stations, and all the information that goes with
	 *            it.
	 */
	public void setConnections(QueryTripsResult result);

	/**
	 * Called when the stations are returned by the server. Notifies the view
	 * that the favorite stations have been updated.
	 * 
	 * @param result
	 *            The list of favorite stations.
	 */
	public void setFavoriteStations(List<TransportStation> result);
}
