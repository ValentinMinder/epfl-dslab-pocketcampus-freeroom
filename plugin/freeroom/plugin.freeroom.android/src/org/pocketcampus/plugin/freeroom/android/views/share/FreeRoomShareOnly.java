package org.pocketcampus.plugin.freeroom.android.views.share;

import org.pocketcampus.android.platform.sdk.core.PluginController;
import org.pocketcampus.android.platform.sdk.tracker.Tracker;
import org.pocketcampus.plugin.freeroom.android.FreeRoomAbstractView;
import org.pocketcampus.plugin.freeroom.android.FreeRoomController;
import org.pocketcampus.plugin.freeroom.android.iface.IFreeRoomView;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

/**
 * <code>FreeRoomCopyClipBoard</code> is a <code>View</code> that display
 * NOTHING and only handles an <code>Intent</code> in order to display a
 * "Share only with pocketcampus server" button in share menus in Android.
 * 
 * @author FreeRoom Project Team (2014/05)
 * @author Julien WEBER <julien.weber@epfl.ch>
 * @author Valentin MINDER <valentin.minder@epfl.ch>
 */
public class FreeRoomShareOnly extends FreeRoomAbstractView implements
		IFreeRoomView {

	@Override
	protected Class<? extends PluginController> getMainControllerClass() {
		return FreeRoomController.class;
	}

	@Override
	protected void onDisplay(Bundle savedInstanceState,
			PluginController controller) {
		// Tracker
		Tracker.getInstance().trackPageView("freeroom/share_server");

		Intent intent = getIntent();
		String action = intent.getAction();
		String type = intent.getType();

		if (Intent.ACTION_SEND.equals(action) && type != null) {
			if ("text/*".equals(type)) {
				handleSendText(intent); // Handle text being sent
			} else {
				// handling other types being sent
				// (THESE ARE NOT ALLOWED BY MANIFEST, WE DONT CARE)
			}
		} else {
			// Handle other intents, such as being started from the home screen
			// (THESE ARE NOT ALLOWED BY MANIFEST, WE DONT CARE)
		}
		Log.v(this.getClass().getName(),
				"activity successfully ended without nothinh to do as expected, probably shared");
		this.finish(); // KILL THE VIEW
	}

	/**
	 * This is called when the Activity is resumed.
	 * 
	 * If the user presses back on the Authentication window, This Activity is
	 * resumed but we do not have the freeroomCookie. In this case we close the
	 * Activity.
	 */
	@Override
	protected void onResume() {
		super.onResume();
	}

	/**
	 * As it's only shared with server, no action is needed with the text.
	 * 
	 * @param intent
	 *            the Intent coming form the share action.
	 */
	private void handleSendText(Intent intent) {
		String sharedText = intent.getStringExtra(Intent.EXTRA_TEXT);
		if (sharedText != null) {
			// WE DO NOTHING!
		}
	}

	@Override
	public void freeRoomResultsUpdated() {
		// WE DO NOTHING!

	}

	@Override
	public void autoCompletedUpdated() {
		// WE DO NOTHING!

	}

	@Override
	public void occupancyResultUpdated() {
		// WE DO NOTHING!

	}

	@Override
	public void occupancyResultsUpdated() {
		// WE DO NOTHING!

	}

	@Override
	public void initializeView() {
		// WE DO NOTHING!

	}
}