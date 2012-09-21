package org.pocketcampus.plugin.bikes.android;

import org.pocketcampus.android.platform.sdk.core.PluginController;
import org.pocketcampus.android.platform.sdk.core.PluginModel;
import org.pocketcampus.plugin.bikes.android.iface.IBikesController;
import org.pocketcampus.plugin.bikes.android.req.BikesRequest;
import org.pocketcampus.plugin.bikes.shared.BikesService.Client;
import org.pocketcampus.plugin.bikes.shared.BikesService.Iface;

/**
 * Controller for the bikes plugin.
 * Handles the request from the plugin to the server.
 * Bikes controller only has to get the number of available bikes for every station.
 * @author Pascal <pascal.scheiben@gmail.com>
 */
public class BikesController extends PluginController implements IBikesController{

	/** Model of this plugin*/
	private BikesModel mModel;
	/** Client for the request*/
	private Iface mClient;
	
	/**
	 *  This name must match given in the Server.java file in plugin.launcher.server.
	 *  It's used to route the request to the right server implementation.
	 */
	private String mPluginName = "bikes";
	
	/**
	 * Initializing
	 */
	@Override
	public void onCreate() {
		// Initializing the model is part of the controller's job...
		mModel = new BikesModel();
		
		// ...as well as initializing the client.
		// The "client" is the connection we use to access the service.
		mClient = (Iface) getClient(new Client.Factory(), mPluginName);
	}
	
	/**
	 * Returnds the associated model.
	 */
	@Override
	public PluginModel getModel() {
		return mModel;
	}

	/**
	 * Makes a request to the server to get a list of <code>BikeEmplacement</code>.
	 * Every BikeEmplacement contains the number of available bikes and empty slots.
	 */
	@Override
	public void getAvailableBikes() {
		new BikesRequest().start(this, mClient, (Object)null);
		
	}

}
