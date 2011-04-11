package org.pocketcampus.provider.mapelements;

import java.util.List;

import org.pocketcampus.core.provider.IProvider;
import org.pocketcampus.shared.plugin.map.MapElementBean;
import org.pocketcampus.shared.plugin.map.MapLayerBean;

public interface IMapElementsProvider extends IProvider {
	abstract public MapLayerBean getLayer();
	
	abstract public List<MapElementBean> getLayerItems();
}
