package org.pocketcampus.plugin.satellite.android;

import org.pocketcampus.android.platform.sdk.core.PluginController;
import org.pocketcampus.android.platform.sdk.core.PluginModel;
import org.pocketcampus.plugin.satellite.android.iface.ISatelliteController;
import org.pocketcampus.plugin.satellite.android.req.AffluenceRequest;
import org.pocketcampus.plugin.satellite.android.req.SandwichRequest;
import org.pocketcampus.plugin.satellite.shared.SatelliteService.Client;
import org.pocketcampus.plugin.satellite.shared.SatelliteService.Iface;

import android.util.Log;

/**
 * Controller for the Satellite plugin. Takes care of interactions between the
 * model and the view and gets information from the server.
 * 
 * @author Oriane <oriane.rodriguez@epfl.ch>
 * 
 */
public class SatelliteController extends PluginController implements
ISatelliteController {

	/** The plugin's model. */
	private SatelliteModel mModel;

	/** Interface to the plugin's server client */
	private Iface mClient;

	/** The name of the plugin */
	private String mPluginName = "satellite";

	/**
	 * Initializes the plugin with a model and a client.
	 */
	@Override
	public void onCreate() {
		// Initializing the model is part of the controller's job...
		mModel = new SatelliteModel();

		// ...as well as initializing the client.
		// The "client" is the connection we use to access the service.
		mClient = (Iface) getClient(new Client.Factory(), mPluginName);
	}

	/**
	 * Returns the model for which this controller works.
	 */
	@Override
	public PluginModel getModel() {
		return mModel;
	}

	/**
	 * Initiates a request to the server to get the beer of the month
	 */
	@Override
	public void getBeerOfMonth() {

	}

	/**
	 * Initiates a request to the server to get the list of all beers Satellite
	 * proposes
	 */
	@Override
	public void getAllBeers() {

	}

	/**
	 * Initiates a request to the server to get the list of sandwiches Satellite
	 * proposes
	 */
	@Override
	public void getSandwiches() {
		Log.d("SATELLITE", "Sandwich request");

		new SandwichRequest().start(this,
				(Iface) getClient(new Client.Factory(), mPluginName),
				(Object) null);
	}

	/**
	 * Initiates a request to the server to get the list of next events at
	 * Satellite
	 */
	@Override
	public void getEvents() {
		Log.d("SATELLITE", "Events request");

	}

	/**
	 * Initiates a request to the server to get the affluence at Satellite
	 */
	@Override
	public void getAffluence() {
		Log.d("SATELLITE", "Affluence request");
		new AffluenceRequest().start(this,
				(Iface) getClient(new Client.Factory(), mPluginName),
				(Object) null);
	}

}
