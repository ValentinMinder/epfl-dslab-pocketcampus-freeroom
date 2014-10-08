package org.pocketcampus.plugin.map.android;

import org.pocketcampus.platform.android.core.PluginController;
import org.pocketcampus.platform.android.core.PluginModel;
import org.pocketcampus.plugin.map.android.req.SearchRequest;
import org.pocketcampus.plugin.map.shared.MapService.Client;
import org.pocketcampus.plugin.map.shared.MapService.Iface;

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

	public void search(String query) {
		System.out.println("Search: " + query);
		new SearchRequest().start(this,  (Iface) getClient(new Client.Factory(), mPluginName), query);
	}
}













