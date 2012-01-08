package org.pocketcampus.plugin.transport.android.iface;

import java.util.List;

import org.pocketcampus.android.platform.sdk.core.IView;
import org.pocketcampus.plugin.transport.shared.TransportStation;
import org.pocketcampus.plugin.transport.shared.QueryTripsResult;

/**
 * The interface that defines the methods implemented by a view of the plugin.
 * 
 * @author Oriane <oriane.rodriguez@epfl.ch>
 * @author Pascal <pascal.scheiben@epfl.ch>
 * @author Florian <florian.laurent@epfl.ch>
 * 
 */
public interface ITransportView extends IView {

	/**
	 * Called by the model when the list of auto completed stations has been
	 * updated.
	 */
	void autoCompletedStationsUpdated();

	/**
	 * Called by the model when the data for the resulted connections between
	 * two stations has been updated.
	 * 
	 * @param result
	 *            A <code>QueryTripsResult</code> consisting of the connections
	 *            between two stations, and all the information that goes with
	 *            it.
	 */
	void connectionsUpdated(QueryTripsResult result);

	/**
	 * Called by the model when the list of favorite stations has been updated
	 * and refreshes the view.
	 */
	void favoriteStationsUpdated();

	/**
	 * Called by the model when the stations corresponding to a list of
	 * <code>String</code> have been updated.
	 * 
	 * @param result
	 *            The list of corresponding stations.
	 */
	public void stationsFromNamesUpdated(List<TransportStation> result);
}
