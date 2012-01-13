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
 * this plugin : favorite stations, auto completed stations, ...
 * 
 * @author Oriane <oriane.rodriguez@epfl.ch>
 * @author Pascal <pascal.scheiben@epfl.ch>
 * @author Florian <florian.laurent@epfl.ch>
 */
public class TransportModel extends PluginModel implements ITransportModel {
	/** The views listening to updates in this model. */
	private ITransportView mListeners = (ITransportView) getListeners();
	/** The list of stations auto completed when the user is typing. */
	private List<TransportStation> mAutoCompletedStations;
	/** The user's favorite stations. */
	private HashMap<String, List<TransportTrip>> mFavoriteStations;

	/**
	 * Class constructor of the plugin initializing object instances.
	 */
	public TransportModel() {
		mFavoriteStations = new HashMap<String, List<TransportTrip>>();
		mAutoCompletedStations = new ArrayList<TransportStation>();
	}

	/**
	 * Returns the interface that the views must implement.
	 */
	@Override
	protected Class<? extends IView> getViewInterface() {
		return ITransportView.class;
	}

	/**
	 * Returns the user's favorite stations.
	 * 
	 * @return mFavoriteStations The list of the user's favorite stations.
	 */
	@Override
	public HashMap<String, List<TransportTrip>> getFavoriteStations() {
		return mFavoriteStations;
	}

	/**
	 * Set all the current favorite stations.
	 * 
	 * @param stations
	 *            The new list of favorite stations.
	 */
	@Override
	public void setAutoCompletedStations(List<TransportStation> stations) {
		if (stations != null) {
			mAutoCompletedStations.clear();
			mAutoCompletedStations.addAll(stations);
			// Notifies the view(s)
			mListeners.autoCompletedStationsUpdated();
		}
	}

	/**
	 * Called when the result of the connections request is received to notify
	 * that connections have been found. Notifies the views with the result.
	 * 
	 * @param result
	 *            A <code>QueryTripsResult</code> consisting of the connections
	 *            between two stations, and all the information that goes with
	 *            it.
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
							// Update displayed stations
							if (c.getTo().getName().equals("Ecublens VD, EPFL")) {
								String fromName = c.getParts().get(0).departure.getName();
								
								if (mFavoriteStations
										.get(fromName) == null) {
									mFavoriteStations.put(
											fromName,
											new ArrayList<TransportTrip>());
								}
								
								
								mFavoriteStations.get(fromName).add(c);
							} else {
								String toName = c.getParts().get(c.getParts().size()-1).arrival.getName();
								
								if (mFavoriteStations.get(toName) == null) {
									mFavoriteStations.put(toName,
											new ArrayList<TransportTrip>());
								}
								
								
								mFavoriteStations.get(toName).add(c);
							}
						}
					}
				}
			}

		}
		// Notifies the views
		mListeners.connectionsUpdated(result);
	}

	/**
	 * Sends the current auto completed stations to refresh the view and display
	 * them to the user while he's taping.
	 * 
	 * @return mAutoCompletedStations The list of current auto completed
	 *         stations to display to the user.
	 */
	public List<TransportStation> getAutoCompletedStations() {
		// Notifies the view(s)
		return mAutoCompletedStations;
	}

	/**
	 * Returns the stations corresponding to a list of stations names.
	 */
	@Override
	public void setFavoriteStations(List<TransportStation> result) {
		if (result != null) {
			for (TransportStation location : result) {
				// Prepares the hash map to contain connections to those
				// stations.
				if (mFavoriteStations.get(location.getName()) == null) {
					mFavoriteStations.put(location.getName(),
							new ArrayList<TransportTrip>());
				}
			}
			// Notifies the view(s)
			mListeners.stationsFromNamesUpdated(result);
		}
	}

	/**
	 * Removes all connections from the map, in order to update with new ones.
	 */
	public void freeConnections() {
		for (String s : mFavoriteStations.keySet()) {
			mFavoriteStations.get(s).clear();
		}
	}

	/**
	 * Removes all stations from the map, in order to update with new ones.
	 */
	public void freeDestinations() {
		mFavoriteStations = new HashMap<String, List<TransportTrip>>();
	}
}
