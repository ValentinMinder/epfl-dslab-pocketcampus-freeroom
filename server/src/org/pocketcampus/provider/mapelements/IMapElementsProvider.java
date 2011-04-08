package org.pocketcampus.provider.mapelements;

import org.pocketcampus.core.provider.IProvider;

public interface IMapElementsProvider extends IProvider {
	abstract public String getLayerName();
	
	abstract public String getLayerDescription();
}
