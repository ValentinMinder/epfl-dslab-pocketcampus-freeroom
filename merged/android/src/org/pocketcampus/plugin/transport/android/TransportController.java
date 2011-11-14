package org.pocketcampus.plugin.transport.android;

import org.pocketcampus.android.platform.sdk.core.PluginController;
import org.pocketcampus.android.platform.sdk.core.PluginModel;
import org.pocketcampus.plugin.transport.android.req.AutoCompleteRequest;
import org.pocketcampus.plugin.transport.shared.TransportService.Client;
import org.pocketcampus.plugin.transport.shared.TransportService.Iface;

public class TransportController extends PluginController {

	private TransportModel mModel;
	private Iface mClient;
	
	/**
	 *  This name must match given in the Server.java file in plugin.launcher.server.
	 *  It's used to route the request to the right server implementation.
	 */
	private String mPluginName = "transport";

	@Override
	public void onCreate() {
		mModel = new TransportModel();
		mClient = (Iface) getClient(new Client.Factory(), mPluginName);
	}
	
	/**
	 * The view will call this in order to register in the model's listener list.
	 */
	@Override
	public PluginModel getModel() {
		return mModel;
	}

	public void getAutocompletions(String constraint) {
		new AutoCompleteRequest().start(this, mClient, constraint);
	}

}













