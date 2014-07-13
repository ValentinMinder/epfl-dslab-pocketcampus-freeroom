package org.pocketcampus.plugin.myedu.android;

import org.pocketcampus.platform.android.core.PluginController;
import org.pocketcampus.platform.android.core.PluginModel;
import org.pocketcampus.plugin.myedu.android.iface.IMyEduController;
import org.pocketcampus.plugin.myedu.android.MyEduModel;
import org.pocketcampus.plugin.myedu.shared.MyEduService.Client;
import org.pocketcampus.plugin.myedu.shared.MyEduService.Iface;


/**
 * MyEduController - Main logic for the MyEdu Plugin.
 * 
 * This class issues requests to the MyEdu PocketCampus
 * server to get the MyEdu data of the logged in user.
 * 
 * @author Amer <amer.chamseddine@epfl.ch>
 * 
 */
public class MyEduController extends PluginController implements IMyEduController{

	/**
	 *  This name must match given in the Server.java file in plugin.launcher.server.
	 *  It's used to route the request to the right server implementation.
	 */
	private String mPluginName = "myedu";
	

	/**
	 * Stores reference to the Model associated with this plugin.
	 */
	private MyEduModel mModel;
	
	/**
	 * HTTP Clients used to communicate with the PocketCampus server.
	 * Use thrift to transport the data.
	 */
	private Iface mClient;

	@Override
	public void onCreate() {
		mModel = new MyEduModel(getApplicationContext());
		mClient = (Iface) getClient(new Client.Factory(), mPluginName);

	}
	
	@Override
	public PluginModel getModel() {
		return mModel;
	}
	

}
