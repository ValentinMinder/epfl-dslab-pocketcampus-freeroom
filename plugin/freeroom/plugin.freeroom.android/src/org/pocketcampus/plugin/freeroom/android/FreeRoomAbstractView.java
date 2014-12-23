package org.pocketcampus.plugin.freeroom.android;

import org.pocketcampus.platform.android.core.PluginController;
import org.pocketcampus.platform.android.core.PluginView;
import org.pocketcampus.plugin.freeroom.R;
import org.pocketcampus.plugin.freeroom.android.iface.IAbstractFreeRoomView;

import android.widget.Toast;

/**
 * FreeRoomAbstractView - An abstract view that handles shared methods among all
 * FreeRoom views.
 * <p>
 * This view is NOT intended to be instanciated or even used. It's only there to
 * group together all commons method among all the views, like network error
 * message.
 * <p>
 * Be careful to declare there ONLY methods that the views don't need, otherwise
 * no one will warn you if your methods in the view are not declared.
 * <p>
 * 
 * @author FreeRoom Project Team (2014/05)
 * @author Julien WEBER <julien.weber@epfl.ch>
 * @author Valentin MINDER <valentin.minder@epfl.ch>
 * 
 */

public abstract class FreeRoomAbstractView extends PluginView implements
		IAbstractFreeRoomView {

	@Override
	protected Class<? extends PluginController> getMainControllerClass() {
		return FreeRoomController.class;
	}

	@Override
	public void networkErrorHappened() {
		anyError();
		Toast.makeText(
				getApplicationContext(),
				getResources().getString(
						R.string.freeroom_connection_error_happened),
				Toast.LENGTH_LONG).show();
	}

	/**
	 * TODO: may not appear in final version ! >> basic error.
	 */
	private boolean debug = false;

	@Override
	public void freeRoomServerBadRequest() {
		if (debug) {
			Toast.makeText(
					getApplicationContext(),
					getResources().getString(
							R.string.freeroom_error_bad_request),
					Toast.LENGTH_SHORT).show();
		} else {
			anyError();
		}
	}

	/**
	 * TODO: may not appear in final version ! >> basic error.
	 */
	@Override
	public void freeRoomServersInternalError() {
		if (debug) {
			Toast.makeText(
					getApplicationContext(),
					getResources().getString(
							R.string.freeroom_error_internal_error),
					Toast.LENGTH_SHORT).show();
		} else {
			anyError();
		}
	}

	/**
	 * TODO: may not appear in final version ! >> basic error.
	 */
	@Override
	public void freeRoomServersUnknownError() {
		if (debug) {
			Toast.makeText(
					getApplicationContext(),
					getResources().getString(
							R.string.freeroom_error_unknown_error),
					Toast.LENGTH_SHORT).show();
		} else {
			anyError();
		}
	}
}
