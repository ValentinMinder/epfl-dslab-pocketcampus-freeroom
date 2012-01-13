package org.pocketcampus.plugin.map.android;

import org.pocketcampus.android.platform.sdk.core.PluginController;
import org.pocketcampus.android.platform.sdk.core.PluginModel;
import org.pocketcampus.plugin.map.android.req.LayerItemsRequest;
import org.pocketcampus.plugin.map.android.req.LayerRequest;
import org.pocketcampus.plugin.map.android.req.SearchRequest;
import org.pocketcampus.plugin.map.shared.MapService.Iface;
import org.pocketcampus.plugin.map.shared.MapService.Client;

/**
 * Map's main controller.
 * @author Florian <florian.laurent@epfl.ch>
 *
 */
public class MapMainController extends PluginController {
	private MapModel mModel;
	private String mPluginName = "map";

	@Override
	public void onCreate() {
		mModel = new MapModel();
	}
	
	@Override
	public PluginModel getModel() {
		return mModel;
	}

	public void getLayers() {
		new LayerRequest().start(this,  (Iface) getClient(new Client.Factory(), mPluginName), (Object)null);
	}

	public void getLayerItems(int layerId) {
		new LayerItemsRequest().start(this,  (Iface) getClient(new Client.Factory(), mPluginName), layerId);
	}

	public void search(String query) {
		System.out.println("Search: " + query);
		new SearchRequest().start(this,  (Iface) getClient(new Client.Factory(), mPluginName), query);
	}
}













