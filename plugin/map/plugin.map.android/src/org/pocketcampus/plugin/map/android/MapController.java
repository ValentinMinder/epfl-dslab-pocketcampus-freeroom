package org.pocketcampus.plugin.map.android;

import org.pocketcampus.platform.android.core.PluginController;
import org.pocketcampus.platform.android.core.PluginModel;

/**
 * Map controller.
 * 
 * @author Florian <florian.laurent@epfl.ch>
 *
 */
public class MapController extends PluginController {
	private MapModel mModel;
	
	@Override
	public void onCreate() {
		mModel = new MapModel();
	}
	
	@Override
	public PluginModel getModel() {
		return mModel;
	}
}













