package org.pocketcampus.plugin.map.android;

import org.apache.http.impl.client.DefaultHttpClient;
import org.pocketcampus.platform.android.core.PluginController;
import org.pocketcampus.platform.android.core.PluginModel;
import org.pocketcampus.plugin.map.android.req.GetLayersRequest;
import org.pocketcampus.plugin.map.android.req.SearchRequest;
import org.pocketcampus.plugin.map.shared.MapService.Client;
import org.pocketcampus.plugin.map.shared.MapService.Iface;

/**
 * Map's main controller.
 * @author Amer Chamseddine <amer@pocketcampus.org>
 *
 */
public class MapController extends PluginController {
	private MapModel mModel;
	private String mPluginName = "map";
	private Iface client;
	private DefaultHttpClient httpClient;

	@Override
	public void onCreate() {
		mModel = new MapModel();
		client = (Iface) getClient(new Client.Factory(), mPluginName);
		httpClient = getThreadSafeClient();
	}
	
	@Override
	public PluginModel getModel() {
		return mModel;
	}
	
	public void getLayers() {
		new GetLayersRequest().start(this, client, null);
	}

	public void search(String query) {
//		System.out.println("Search: " + query);
		new SearchRequest().start(this, client, query);
	}
}













