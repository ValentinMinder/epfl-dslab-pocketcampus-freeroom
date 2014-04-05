package org.pocketcampus.plugin.freeroom.android.iface;

import org.pocketcampus.android.platform.sdk.core.IView;

/**
 * Interface for the Views of the FreeRoom plugin.
 * 
 * It contains the method that are called by the Model when some data is
 * updated.
 * 
 * The methods that are called by the "HttpRequest" classes when some usual
 * behavior occurs are in <code>IAbstractFreeRoomView</code>
 * 
 * @author FreeRoom Project Team - Julien WEBER <julien.weber@epfl.ch> and
 *         Valentin MINDER <valentin.minder@epfl.ch>
 */
public interface IFreeRoomView extends IView {
	// TODO: delete after NEW INTERFACE of 2014.04.04 is okay
	void freeRoomResultsUpdated();

	void autoCompletedUpdated();

	// TODO: delete after NEW INTERFACE of 2014.04.04 is okay
	void occupancyResultUpdated();

	// TODO: NEW INTERFACE as of 2014.04.04
	void occupancyResultsUpdated();

	void initializeView();

}
