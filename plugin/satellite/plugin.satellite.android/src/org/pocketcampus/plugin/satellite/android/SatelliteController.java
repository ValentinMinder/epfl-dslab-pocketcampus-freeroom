package org.pocketcampus.plugin.satellite.android;

import org.pocketcampus.platform.android.core.PluginController;
import org.pocketcampus.platform.android.core.PluginModel;
import org.pocketcampus.plugin.satellite.android.iface.ISatelliteController;
import org.pocketcampus.plugin.satellite.android.req.AffluenceRequest;
import org.pocketcampus.plugin.satellite.android.req.BeerRequest;
import org.pocketcampus.plugin.satellite.shared.SatelliteService.Client;
import org.pocketcampus.plugin.satellite.shared.SatelliteService.Iface;

/**
 * The controller for the Satellite plugin. Takes care of interactions between
 * the model and the view and gets information from the server.
 * 
 * @author Oriane <oriane.rodriguez@epfl.ch>
 * 
 */
public class SatelliteController extends PluginController implements
		ISatelliteController {
	/** The plugin model. */
	private SatelliteModel mModel;
	/** The name of the plugin */
	private String mPluginName = "satellite";

	/**
	 * Initializes the plugin with the model.
	 */
	@Override
	public void onCreate() {
		// Initializing the model is part of the controller's job
		mModel = new SatelliteModel();
	}

	/**
	 * Returns the model for which this controller works.
	 */
	@Override
	public PluginModel getModel() {
		return mModel;
	}

	/**
	 * Initiates a request to the server to get the beer of the month.
	 */
	@Override
	public void getBeerOfMonth() {
		new BeerRequest().start(this,
				(Iface) getClient(new Client.Factory(), mPluginName),
				(Object) null);
	}

	/**
	 * Initiates a request to the server to get the current affluence at
	 * Satellite.
	 */
	@Override
	public void getAffluence() {
		new AffluenceRequest().start(this,
				(Iface) getClient(new Client.Factory(), mPluginName),
				(Object) null);
	}
}
