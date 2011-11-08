package org.pocketcampus.plugin.bikes.android;

import org.pocketcampus.android.platform.sdk.core.PluginController;
import org.pocketcampus.android.platform.sdk.core.PluginModel;
import org.pocketcampus.plugin.bikes.android.iface.IBikesController;
import org.pocketcampus.plugin.bikes.android.req.BikesRequest;
import org.pocketcampus.plugin.bikes.shared.BikeService.Client;
import org.pocketcampus.plugin.bikes.shared.BikeService.Iface;

public class BikesController extends PluginController implements IBikesController{

	private BikesModel mModel;
	private Iface mClient;
	
	private String mPluginName = "bikes";
	
	@Override
	public void onCreate() {
		// Initializing the model is part of the controller's job...
		mModel = new BikesModel();
		
		// ...as well as initializing the client.
		// The "client" is the connection we use to access the service.
		mClient = (Iface) getClient(new Client.Factory(), mPluginName);
	}
	
	@Override
	public PluginModel getModel() {
		return mModel;
	}

	@Override
	public void getAvailableBikes() {
		new BikesRequest().start(this, mClient, (Object)null);
		
	}

}
