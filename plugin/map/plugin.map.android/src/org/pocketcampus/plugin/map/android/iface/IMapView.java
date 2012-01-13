package org.pocketcampus.plugin.map.android.iface;

import org.pocketcampus.android.platform.sdk.core.IView;

/**
 * Map View interface.
 * 
 * @author Florian <florian.laurent@epfl.ch>
 *
 */
public interface IMapView extends IView {
	/**
	 * Triggered when the layers are updated.
	 */
	void layersUpdated();
	
	/**
	 * Triggered when elements of a layer have been updated.
	 */
	void layerItemsUpdated();
	
	/**
	 * Triggered when the search results are updated.
	 */
	void searchResultsUpdated();
}
