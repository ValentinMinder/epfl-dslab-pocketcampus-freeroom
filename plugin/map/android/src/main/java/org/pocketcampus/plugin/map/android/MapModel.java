package org.pocketcampus.plugin.map.android;

import java.util.Arrays;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.pocketcampus.platform.android.core.IView;
import org.pocketcampus.platform.android.core.PluginModel;
import org.pocketcampus.plugin.map.android.iface.IMapView;
import org.pocketcampus.plugin.map.shared.MapItem;

/**
 * Map model.
 * 
 * @author Florian <florian.laurent@epfl.ch>
 *
 */
public class MapModel extends PluginModel {
	private IMapView mListeners = (IMapView) getListeners();
	
	private List<MapItem> mSearchResults;
	private Map<String, String> layerNames = new HashMap<String, String>();

	public MapModel() {
		layerNames.clear();
		layerNames.put("parkings_publics{floor}", "parkings publics");
		layerNames.put("arrets_metro{floor}", "arrets metro");
		layerNames.put("transports_publics{floor}", "transports publics");
		layerNames.put("information{floor}", "information");

	}
	
	@Override
	protected Class<? extends IView> getViewInterface() {
		return IMapView.class;
	}


	public void setSearchResult(List<MapItem> results) {
		System.out.println(results);
		mSearchResults = results;
		mListeners.searchResultsUpdated();
	}

	public List<MapItem> getSearchResults() {
		return mSearchResults;
	}
	
	public void setLayerNames(Map<String, String> map) {
		layerNames = map;
	}
	
	public Map<String, String> getLayerNames() {
		return layerNames;
	}

	
}
