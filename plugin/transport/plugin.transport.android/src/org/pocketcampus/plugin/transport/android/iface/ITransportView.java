package org.pocketcampus.plugin.transport.android.iface;

import java.util.List;

import org.pocketcampus.android.platform.sdk.core.IView;
import org.pocketcampus.plugin.transport.shared.QueryTripsResult;
import org.pocketcampus.plugin.transport.shared.TransportStation;

/**
 * The interface that defines the method implemented by a view of the plugin
 * 
 * @author Oriane <oriane.rodriguez@epfl.ch>
 * @author Pascal <pascal.scheiben@epfl.ch>
 * @author Florian <florian.laurent@epfl.ch>
 * 
 */
public interface ITransportView extends IView {

	/**
	 * Called by the model when the list of autocompleted destinations has been
	 * updated and refreshes the view
	 */
	void autoCompletedDestinationsUpdated();

	/**
	 * Called by the model when the data for the resulted connections for some
	 * destination has been updated
	 */
	void connectionUpdated(QueryTripsResult result);

	/**
	 * Called by the model when the list of preferred destinations has been
	 * updated and refreshes the view
	 */
	void destinationsUpdated();

	/**
	 * Called by the model when the Locations corresponding to a list of strings
	 * have been updated
	 * 
	 * @param result
	 */
	public void locationsFromNamesUpdated(List<TransportStation> result);
}
