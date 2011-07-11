package org.pocketcampus.provider.mapelements;

import java.util.List;

import org.pocketcampus.core.provider.IProvider;
import org.pocketcampus.shared.plugin.authentication.AuthToken;
import org.pocketcampus.shared.plugin.map.MapElementBean;
import org.pocketcampus.shared.plugin.map.MapLayerBean;

public interface IMapElementsProvider extends IProvider {
	/**
	 * Gives the list of available layers for this plugin.
	 * @return
	 */
	abstract public List<MapLayerBean> getLayers();
	
	/**
	 * Returns a specific layer provided by this plugin.
	 * @param token
	 * @param layerId
	 * @return
	 */
	abstract public List<MapElementBean> getLayerItems(AuthToken token, int layerId);
}
