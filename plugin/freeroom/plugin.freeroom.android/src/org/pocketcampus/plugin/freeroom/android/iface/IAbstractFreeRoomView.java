package org.pocketcampus.plugin.freeroom.android.iface;

import org.pocketcampus.android.platform.sdk.core.IView;

/**
 * 
 * Interface for the AbstractView of the FreeRoom plugin.
 * 
 * It contains the methods that are called by the "HttpRequest" classes when
 * some usual behavior occurs.
 * 
 * The method that are called by the Model when some data is updated are in
 * <code>IFreeRoomView</code>.
 * 
 * @author FreeRoom Project Team - Julien WEBER <julien.weber@epfl.ch> and
 *         Valentin MINDER <valentin.minder@epfl.ch>
 */
public interface IAbstractFreeRoomView extends IView {

	void anyError();

	void freeRoomServerBadRequest();

	void freeRoomServersInternalError();

	void freeRoomServersUnknownError();

}
