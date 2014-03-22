package org.pocketcampus.plugin.freeroom.android;

import org.pocketcampus.android.platform.sdk.core.PluginController;
import org.pocketcampus.android.platform.sdk.core.PluginView;
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
 * @author FreeRoom Project Team - Julien WEBER <julien.weber@epfl.ch> and
 *         Valentin MINDER <valentin.minder@epfl.ch>
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
		Toast.makeText(
				getApplicationContext(),
				getResources().getString(
						R.string.freeroom_connection_error_happened),
				Toast.LENGTH_LONG).show();
	}

	/**
	 * NOT USED... replaced by the three other down there.
	 */
	@Override
	public void freeRoomServersDown() {
		Toast.makeText(
				getApplicationContext(),
				getResources().getString(R.string.freeroom_error_freeroom_down),
				Toast.LENGTH_SHORT).show();
	}

	@Override
	public void freeRoomServerBadRequest() {
		Toast.makeText(getApplicationContext(),
				getResources().getString(R.string.freeroom_error_bad_request),
				Toast.LENGTH_SHORT).show();
	}

	@Override
	public void freeRoomServersInternalError() {
		Toast.makeText(
				getApplicationContext(),
				getResources()
						.getString(R.string.freeroom_error_internal_error),
				Toast.LENGTH_SHORT).show();
	}

	@Override
	public void freeRoomServersUnknownError() {
		Toast.makeText(
				getApplicationContext(),
				getResources().getString(R.string.freeroom_error_unknown_error),
				Toast.LENGTH_SHORT).show();
	}
}
