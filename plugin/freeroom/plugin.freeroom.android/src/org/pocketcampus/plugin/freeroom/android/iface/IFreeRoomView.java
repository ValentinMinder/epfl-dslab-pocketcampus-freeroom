package org.pocketcampus.plugin.freeroom.android.iface;

import org.pocketcampus.android.platform.sdk.core.IView;

/**
 * IFreeRoomView
 * 
 * Interface for the Views of the FreeRoom plugin.
 * 
 * It contains the method that are called by the Model
 * when some data is updated, as well as the methods that
 * are called by the "HttpRequest" classes when some usual
 * behavior occurs.
 * 
 * @author Amer <amer.chamseddine@epfl.ch>
 *
 */
public interface IFreeRoomView extends IView {

	void freeRoomServersDown();

	void freeRoomResultsUpdated();

	void autoCompletedUpdated();
	
}
