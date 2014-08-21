package org.pocketcampus.plugin.satellite.android.iface;

import org.pocketcampus.platform.android.core.IView;

/**
 * The interface that defines the public methods of the
 * <code>SatelliteMainView</code>.
 * 
 * @author Oriane <oriane.rodriguez@epfl.ch>
 * 
 */
public interface ISatelliteMainView extends IView {

	/**
	 * Called when the beer of the month is updated in the
	 * <code>SatelliteModel</code>.
	 */
	public void beerUpdated();

	/**
	 * Called when the affluence is updated in the <code>SatelliteModel</code>.
	 */
	public void affluenceUpdated();
}
