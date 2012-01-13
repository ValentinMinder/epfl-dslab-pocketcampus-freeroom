package org.pocketcampus.plugin.map.android;

import java.util.ArrayList;
import java.util.List;

import org.pocketcampus.android.platform.sdk.core.IView;
import org.pocketcampus.android.platform.sdk.core.PluginModel;
import org.pocketcampus.plugin.map.android.iface.IMapView;
import org.pocketcampus.plugin.map.shared.MapItem;
import org.pocketcampus.plugin.map.shared.MapLayer;

import android.widget.ArrayAdapter;

/**
 * Map model.
 * 
 * @author Florian <florian.laurent@epfl.ch>
 *
 */
public class MapModel extends PluginModel {
	private IMapView mListeners = (IMapView) getListeners();
	
	private List<MapLayer> mLayers;
	private List<MapItem> mLayerItems = new ArrayList<MapItem>();
	private List<MapItem> mSearchResults;
	
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

	public void addLayerItems(List<MapItem> result) {
		System.out.println(result);
		mLayerItems.addAll(result);
		mListeners.layerItemsUpdated();
	}

	public void setSearchResult(List<MapItem> results) {
		System.out.println(results);
		mSearchResults = results;
		mListeners.searchResultsUpdated();
	}

	public List<MapItem> getLayerItems() {
		return mLayerItems;
	}

	public List<MapItem> getSearchResults() {
		return mSearchResults;
	}
}
