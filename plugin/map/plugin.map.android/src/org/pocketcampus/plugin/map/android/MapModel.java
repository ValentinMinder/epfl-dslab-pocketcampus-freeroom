package org.pocketcampus.plugin.map.android;

import java.util.List;

import org.pocketcampus.android.platform.sdk.core.IView;
import org.pocketcampus.android.platform.sdk.core.PluginModel;
import org.pocketcampus.plugin.map.android.iface.IMapView;
import org.pocketcampus.plugin.map.shared.MapLayer;

public class MapModel extends PluginModel {
	private IMapView mListeners = (IMapView) getListeners();
	
	private List<MapLayer> mLayers;
	
	@Override
	protected Class<? extends IView> getViewInterface() {
		return IMapView.class;
	}

	public void setLayers(List<MapLayer> layers) {
		mLayers = layers;
		mListeners.layersUpdated();
	}
	
	public List<MapLayer> getLayers() {
		return mLayers;
	}
}
