package org.pocketcampus.plugin.map.android;

import org.pocketcampus.android.platform.sdk.core.PluginController;
import org.pocketcampus.android.platform.sdk.core.PluginModel;

public class MapController extends PluginController {
	private MapModel mModel;
//	private Iface mClient;
	
	/**
	 *  This name must match given in the Server.java file in plugin.launcher.server.
	 *  It's used to route the request to the right server implementation.
	 */
	private String mPluginName = "test";

	@Override
	public void onCreate() {
		mModel = new MapModel();
//		mClient = (Iface) getClient(new Client.Factory(), mPluginName);
	}
	
	@Override
	public PluginModel getModel() {
		return mModel;
	}
}













