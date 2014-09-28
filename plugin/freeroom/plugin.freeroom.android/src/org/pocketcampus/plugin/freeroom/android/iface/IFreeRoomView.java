package org.pocketcampus.plugin.freeroom.android.iface;

/**
 * Interface for the Views of the FreeRoom plugin.
 * 
 * It contains the method that are called by the Model when some data is
 * updated.
 * 
 * The methods that are called by the "HttpRequest" classes when some usual
 * behavior occurs are in <code>IAbstractFreeRoomView</code>
 * 
 * @author FreeRoom Project Team (2014/05)
 * @author Julien WEBER <julien.weber@epfl.ch>
 * @author Valentin MINDER <valentin.minder@epfl.ch>
 */
public interface IFreeRoomView extends IAbstractFreeRoomView {

	void autoCompleteLaunch();

	void autoCompleteUpdated();

	void occupancyResultsUpdated();

	void initializeView();

	void refreshOccupancies();

	void workingMessageUpdated();

}
