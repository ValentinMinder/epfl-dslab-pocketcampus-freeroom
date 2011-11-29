package org.pocketcampus.plugin.transport.android.iface;

import org.pocketcampus.android.platform.sdk.core.IView;
import org.pocketcampus.plugin.transport.shared.QueryConnectionsResult;

/**
 * 
 * @author oriane
 *
 */
public interface ITransportView extends IView {

	/**
	 * Called by the model when the list of autocompleted destinations has been
	 * updated and refreshes the view
	 */
	void autoCompletedDestinationsUpdated();
	
	/**
	 * Called by the model when the data for the resulted connection has been
	 * updated
	 */
	void connectionUpdated(QueryConnectionsResult result);
	
	/**
	 * Called by the model when the list of preferred destinations has been
	 * updated and refreshes the view
	 */
	void destinationsUpdated();
}
