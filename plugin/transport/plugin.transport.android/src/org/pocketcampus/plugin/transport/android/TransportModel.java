package org.pocketcampus.plugin.transport.android;

import java.util.ArrayList;
import java.util.List;

import org.pocketcampus.android.platform.sdk.core.IView;
import org.pocketcampus.android.platform.sdk.core.PluginModel;
import org.pocketcampus.plugin.transport.android.iface.ITransportModel;
import org.pocketcampus.plugin.transport.android.iface.ITransportView;
import org.pocketcampus.plugin.transport.shared.Location;
import org.pocketcampus.plugin.transport.shared.QueryConnectionsResult;

import android.util.Log;

/**
 * The Main Model for the Transport plugin. Handles all the data relative to
 * this plugin : preferred destinations, autocompleted destinations, ...
 * 
 * 
 * @author Oriane <oriane.rodriguez@epfl.chY
 * @author Pascal <pascal.scheiben@epfl.ch>
 * @author Florian <florian.laurent@epfl.ch>
 * 
 */
public class TransportModel extends PluginModel implements ITransportModel {
	/** The views listening to updates in this model */
	private ITransportView mListeners = (ITransportView) getListeners();
	/** The list of preferred locations set by the user */
	private List<Location> mPreferredDestinations;
	/** The list of locations autocompleted when the user is typing */
	private List<Location> mAutoCompletedDestinations;

	/**
	 * The constructor
	 * 
	 * Initializes object instances
	 */
	public TransportModel() {
		mPreferredDestinations = new ArrayList<Location>();
		mAutoCompletedDestinations = new ArrayList<Location>();
	}

	/**
	 * Returns the interface that the views must implement
	 */
	@Override
	protected Class<? extends IView> getViewInterface() {
		return ITransportView.class;
	}

	/**
	 * @return mPreferredDestinations The list of preferred destinations
	 */
	@Override
	public List<Location> getPreferredDestinations() {
		return mPreferredDestinations;
	}

	/**
	 * Set all the preferred destinations
	 * 
	 * @param destinations
	 *            The new list of preferred destinations
	 */
	@Override
	public void setAutoCompletedDestinations(List<Location> destinations) {
		mAutoCompletedDestinations.clear();
		mAutoCompletedDestinations.addAll(destinations);
		/** Update the views */
		mListeners.autoCompletedDestinationsUpdated();
	}

	/**
	 * Add a location to the current preferred destinations (called by the
	 * TransportTimeView)
	 * 
	 * @param location
	 *            The new destination to add in the preferred destinations
	 */
	@Override
	public void setNewPreferredDestination(Location location) {
		if (!mPreferredDestinations.contains(location)) {
			Log.d("TRANSPORT", "New preferred Location set (model)");
			mPreferredDestinations.add(location);
		}
		/** update the views */
		mListeners.destinationsUpdated();
	}

	/**
	 * Called by the request to notify that the connection has been found.
	 * Notifies the view(s) with the result
	 * 
	 * @param result
	 */
	@Override
	public void setConnections(QueryConnectionsResult result) {
		/** update the views */
		Log.d("TRANSPORT", "Connection set (model)");
		mListeners.connectionUpdated(result);
	}

	/**
	 * Sends the current autocompleted destinations to refresh the view and
	 * display them to the user while he's taping
	 * 
	 * @return mAutoCompletedDestinations The list of current autocompleted
	 *         destinations to display to the user
	 */
	public List<Location> getAutoCpmpletedDestinations() {
		/** Notify the view(s) */
		return mAutoCompletedDestinations;
	}

	/**
	 * Sends the locations corresponding to a list of strings to the view(s)
	 */
	@Override
	public void setLocationsFromNames(List<Location> result) {
		if(result != null) {			
			Log.d("TRANSPORT", "Locations from Names set (model)");
			
			for(Location location : result) {
				if(!mPreferredDestinations.contains(location)) {					
					mPreferredDestinations.add(location);
				}
			}
			mListeners.locationsFromNamesUpdated(result);
		}
	}

}
