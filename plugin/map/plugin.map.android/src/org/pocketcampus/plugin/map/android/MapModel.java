package org.pocketcampus.plugin.map.android;

import java.util.List;

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
}
