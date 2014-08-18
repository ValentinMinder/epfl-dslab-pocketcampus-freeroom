package org.pocketcampus.plugin.recommendedapps.android;

import org.pocketcampus.platform.android.core.PluginController;
import org.pocketcampus.platform.android.core.PluginModel;
import org.pocketcampus.plugin.recommendedapps.android.iface.IRecommendedAppsController;
import org.pocketcampus.plugin.recommendedapps.android.RecommendedAppsModel;
import org.pocketcampus.plugin.recommendedapps.shared.RecommendedAppsService.Client;
import org.pocketcampus.plugin.recommendedapps.shared.RecommendedAppsService.Iface;


/**
 * RecommendedAppsController - Main logic for the RecommendedApps Plugin.
 * 
 * This class issues requests to the RecommendedApps PocketCampus
 * server to get the RecommendedApps data of the logged in user.
 * 
 * @author Amer <amer.chamseddine@epfl.ch>
 * 
 */
public class RecommendedAppsController extends PluginController implements IRecommendedAppsController{

	/**
	 *  This name must match given in the Server.java file in plugin.launcher.server.
	 *  It's used to route the request to the right server implementation.
	 */
	private String mPluginName = "recommendedapps";
	

	/**
	 * Stores reference to the Model associated with this plugin.
	 */
	private RecommendedAppsModel mModel;
	
	/**
	 * HTTP Clients used to communicate with the PocketCampus server.
	 * Use thrift to transport the data.
	 */
	private Iface mClient;

	@Override
	public void onCreate() {
		mModel = new RecommendedAppsModel(getApplicationContext());
		mClient = (Iface) getClient(new Client.Factory(), mPluginName);

	}
	
	@Override
	public PluginModel getModel() {
		return mModel;
	}
	

}
