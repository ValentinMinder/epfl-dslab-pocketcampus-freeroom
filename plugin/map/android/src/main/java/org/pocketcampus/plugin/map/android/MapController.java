package org.pocketcampus.plugin.map.android;

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

	@Override
	public void onCreate() {
		mModel = new MapModel();
		client = (Iface) getClient(new Client.Factory(), mPluginName);
	}
	
	@Override
	public PluginModel getModel() {
		return mModel;
	}
	
	public void getLayers() {
		new GetLayersRequest().start(this, client, null);
	}

	public void search(MapMainView context, String query) {
		new SearchRequest(context).start(this, client, query);
	}
}













