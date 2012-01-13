package org.pocketcampus.plugin.bikes.android.iface;

import org.pocketcampus.android.platform.sdk.core.IView;

/**
 * Interface to the public methods of the BikeView
 * @author Pascal <pascal.scheiben@gmail.com>
 */
public interface IBikesView extends IView{
	/**
	 * Called when the list of BikeEmplacement has been updated.
	 */
	void bikeListUpdated();
}
