package org.pocketcampus.plugin.blank.android;

import org.pocketcampus.platform.android.core.PluginController;
import org.pocketcampus.platform.android.core.PluginModel;
import org.pocketcampus.plugin.blank.android.iface.IBlankController;
import org.pocketcampus.plugin.blank.android.BlankModel;
import org.pocketcampus.plugin.blank.shared.BlankService.Client;
import org.pocketcampus.plugin.blank.shared.BlankService.Iface;


/**
 * BlankController - Main logic for the Blank Plugin.
 * 
 * This class issues requests to the Blank PocketCampus
 * server to get the Blank data of the logged in user.
 * 
 * @author Amer <amer.chamseddine@epfl.ch>
 * 
 */
public class BlankController extends PluginController implements IBlankController{

	/**
	 *  This name must match given in the Server.java file in plugin.launcher.server.
	 *  It's used to route the request to the right server implementation.
	 */
	private String mPluginName = "blank";
	

	/**
	 * Stores reference to the Model associated with this plugin.
	 */
	private BlankModel mModel;
	
	/**
	 * HTTP Clients used to communicate with the PocketCampus server.
	 * Use thrift to transport the data.
	 */
	private Iface mClient;

	@Override
	public void onCreate() {
		mModel = new BlankModel(getApplicationContext());
		mClient = (Iface) getClient(new Client.Factory(), mPluginName);

	}
	
	@Override
	public PluginModel getModel() {
		return mModel;
	}
	

}
