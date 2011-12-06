package org.pocketcampus.plugin.map.android;

import org.pocketcampus.android.platform.sdk.core.PluginController;
import org.pocketcampus.android.platform.sdk.core.PluginModel;
import org.pocketcampus.plugin.map.android.req.LayerRequest;
import org.pocketcampus.plugin.map.shared.MapService.Iface;
import org.pocketcampus.plugin.map.shared.MapService.Client;

public class MapMainController extends PluginController {
	private MapModel mModel;
	private Iface mClient;
	private String mPluginName = "map";

	@Override
	public void onCreate() {
		mModel = new MapModel();
		mClient = (Iface) getClient(new Client.Factory(), mPluginName);
	}
	
	@Override
	public PluginModel getModel() {
		return mModel;
	}

	public void getLayers() {
		new LayerRequest().start(this, mClient, (Object)null);
	}
}













