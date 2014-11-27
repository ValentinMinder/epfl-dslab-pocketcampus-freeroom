package org.pocketcampus.plugin.freeroom.android.iface;

import org.pocketcampus.platform.android.core.IView;

/**
 * 
 * Interface for the AbstractView of the FreeRoom plugin.
 * <p>
 * It contains the methods that are called by the "HttpRequest" classes when
 * some usual behavior occurs.
 * <p>
 * The method that are called by the Model when some data is updated are in
 * <code>IFreeRoomView</code>.
 * 
 * @author FreeRoom Project Team (2014/05)
 * @author Julien WEBER <julien.weber@epfl.ch>
 * @author Valentin MINDER <valentin.minder@epfl.ch>
 */
public interface IAbstractFreeRoomView extends IView {

	/**
	 * Resets all the "updating" status to "error", like in autocomplete, main
	 * view or working there pop-up. Should be called when any transmission or
	 * server error occurs. This does NOT take into account which request had an
	 * error, so if any currently processing request has an issue, it will
	 * notify the status textview, even if a new one has been launched in the
	 * mean time.
	 */
	void anyError();

	void freeRoomServerBadRequest();

	void freeRoomServersInternalError();

	void freeRoomServersUnknownError();

}
