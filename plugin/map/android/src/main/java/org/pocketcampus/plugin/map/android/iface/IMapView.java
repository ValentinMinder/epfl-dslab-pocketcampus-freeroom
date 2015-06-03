package org.pocketcampus.plugin.map.android.iface;

import org.pocketcampus.platform.android.core.IView;

/**
 * Map View interface.
 * 
 * @author Florian <florian.laurent@epfl.ch>
 *
 */
public interface IMapView extends IView {
	
	/**
	 * Triggered when the search results are updated.
	 */
	void searchResultsUpdated();
}
