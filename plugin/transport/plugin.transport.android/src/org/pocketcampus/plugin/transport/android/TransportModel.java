package org.pocketcampus.plugin.transport.android;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.pocketcampus.android.platform.sdk.core.IView;
import org.pocketcampus.android.platform.sdk.core.PluginModel;
import org.pocketcampus.plugin.transport.android.iface.ITransportModel;
import org.pocketcampus.plugin.transport.android.iface.ITransportView;
import org.pocketcampus.plugin.transport.shared.QueryTripsResult;
import org.pocketcampus.plugin.transport.shared.TransportStation;
import org.pocketcampus.plugin.transport.shared.TransportTrip;

/**
 * The Main Model for the Transport plugin. Handles all the data relative to
 * this plugin : preferred destinations, auto completed destinations, ...
 * 
 * @author Oriane <oriane.rodriguez@epfl.ch>
 * @author Pascal <pascal.scheiben@epfl.ch>
 * @author Florian <florian.laurent@epfl.ch>
 */
public class TransportModel extends PluginModel implements ITransportModel {
	/** The views listening to updates in this model */
	private ITransportView mListeners = (ITransportView) getListeners();
	/** The list of destinations auto completed when the user is typing */
	private List<TransportStation> mAutoCompletedDestinations;
	/** The user's preferred destinations */
	private HashMap<String, List<TransportTrip>> mPreferredDestinations;

	/**
	 * The constructor of the plugin: initializes object instances.
	 */
	public TransportModel() {
		mPreferredDestinations = new HashMap<String, List<TransportTrip>>();
		mAutoCompletedDestinations = new ArrayList<TransportStation>();
	}

	/**
	 * Returns the interface that the views must implement.
	 */
	@Override
	protected Class<? extends IView> getViewInterface() {
		return ITransportView.class;
	}

	/**
	 * Returns the user's preferred destinations.
	 * 
	 * @return mPreferredDestinations The list of the user's preferred
	 *         destinations.
	 */
	@Override
	public HashMap<String, List<TransportTrip>> getPreferredDestinations() {
		return mPreferredDestinations;
	}

	/**
	 * Set all the current preferred destinations.
	 * 
	 * @param destinations
	 *            The new list of preferred destinations
	 */
	@Override
	public void setAutoCompletedDestinations(List<TransportStation> destinations) {
		if (destinations != null) {
			mAutoCompletedDestinations.clear();
			mAutoCompletedDestinations.addAll(destinations);
			// Notifies the view(s)
			mListeners.autoCompletedDestinationsUpdated();
		}
	}

	/**
	 * Called when the result of the connections request is received to notify
	 * that connections have been found. Notifies the view(s) with the result
	 * 
	 * @param result
	 */
	@Override
	public void setConnections(QueryTripsResult result) {
		if (result != null) {
			List<TransportTrip> connections = result.getConnections();

			if (connections != null && !connections.isEmpty()) {
				int i = 0;
				for (TransportTrip c : connections) {
					if (c != null) {
						if (i < 3) {
							i++;
							// Update displayed locations
							if (mPreferredDestinations.get(c.getTo().getName()) == null) {
								mPreferredDestinations.put(c.getTo().getName(),
										new ArrayList<TransportTrip>());
							}
							mPreferredDestinations.get(c.getTo().getName())
									.add(c);
						}
					}
				}
			}

		}
		// Notifies the view(s)
		mListeners.connectionsUpdated(result);
	}

	/**
	 * Sends the current auto completed destinations to refresh the view and
	 * display them to the user while he's taping.
	 * 
	 * @return mAutoCompletedDestinations The list of current auto completed
	 *         destinations to display to the user.
	 */
	public List<TransportStation> getAutoCompletedDestinations() {
		// Notifies the view(s)
		return mAutoCompletedDestinations;
	}

	/**
	 * Returns the locations corresponding to a list of locations names.
	 */
	@Override
	public void setPreferredDestinations(List<TransportStation> result) {
		if (result != null) {
			for (TransportStation location : result) {
				// Prepares the hash map to contain connections to those
				// destinations.
				if (mPreferredDestinations.get(location.getName()) == null) {
					mPreferredDestinations.put(location.getName(),
							new ArrayList<TransportTrip>());
				}
			}
			// Notifies the view(s)
			mListeners.locationsFromNamesUpdated(result);
		}
	}

	/**
	 * Removes all connections from the map, in order to update with new ones.
	 */
	public void freeConnections() {
		mPreferredDestinations = new HashMap<String, List<TransportTrip>>();
	}
}
