package org.pocketcampus.plugin.camipro.android;

import java.util.List;

import org.pocketcampus.android.platform.sdk.core.PluginController;
import org.pocketcampus.android.platform.sdk.core.PluginModel;
import org.pocketcampus.plugin.camipro.android.iface.ICamiproController;
import org.pocketcampus.plugin.camipro.android.CamiproModel;
import org.pocketcampus.plugin.camipro.shared.CamiproService.Client;
import org.pocketcampus.plugin.camipro.shared.CamiproService.Iface;

public class CamiproController extends PluginController implements ICamiproController{

	private CamiproModel mModel;
	private Iface mClient;
	private String mPluginName = "camipro";
	

	@Override
	public void onCreate() {
		// Initializing the model is part of the controller's job...
		mModel = new CamiproModel();
		
		// ...as well as initializing the client.
		// The "client" is the connection we use to access the service.
		mClient = (Iface) getClient(new Client.Factory(), mPluginName);
	}
	
	/**
	 * The view will call this in order to register in the model's listener list.
	 */
	@Override
	public PluginModel getModel() {
		return mModel;
	}

}
