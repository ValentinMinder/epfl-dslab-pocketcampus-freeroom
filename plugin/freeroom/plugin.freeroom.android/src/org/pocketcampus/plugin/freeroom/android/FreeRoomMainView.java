package org.pocketcampus.plugin.freeroom.android;

import org.pocketcampus.plugin.freeroom.R;
import org.pocketcampus.android.platform.sdk.core.PluginController;
import org.pocketcampus.android.platform.sdk.core.PluginView;
import org.pocketcampus.android.platform.sdk.tracker.Tracker;
import org.pocketcampus.android.platform.sdk.ui.layout.StandardTitledLayout;
import org.pocketcampus.plugin.freeroom.android.iface.IFreeRoomView;

import android.os.Bundle;
import android.widget.Toast;

/**
 * FreeRoomMainView - Main view that shows FreeRoom courses.
 * 
 * This is the main view in the FreeRoom Plugin.
 * It checks if the user is logged in, if not it pings
 * the Authentication Plugin.
 * When it gets back a valid SessionId it fetches the
 * user's FreeRoom data.
 * 
 * @author Amer <amer.chamseddine@epfl.ch>
 * 
 */
public class FreeRoomMainView extends PluginView implements IFreeRoomView {

	private FreeRoomController mController;
	private FreeRoomModel mModel;
	
	private StandardTitledLayout mLayout;
	
	@Override
	protected Class<? extends PluginController> getMainControllerClass() {
		return FreeRoomController.class;
	}

	@Override
	protected void onDisplay(Bundle savedInstanceState, PluginController controller) {
		//Tracker
		Tracker.getInstance().trackPageView("freeroom");
		
		// Get and cast the controller and model
		mController = (FreeRoomController) controller;
		mModel = (FreeRoomModel) controller.getModel();

		// Setup the layout
		mLayout = new StandardTitledLayout(this);

		// The ActionBar is added automatically when you call setContentView
		setContentView(mLayout);
		mLayout.hideTitle();


	}

	/**
	 * This is called when the Activity is resumed.
	 * 
	 * If the user presses back on the Authentication window,
	 * This Activity is resumed but we do not have the
	 * freeroomCookie. In this case we close the Activity.
	 */
	@Override
	protected void onResume() {
		super.onResume();
		/*if(mModel != null && mModel.getFreeRoomCookie() == null) {
			// Resumed and lot logged in? go back
			finish();
		}*/
	}

	@Override
	public void networkErrorHappened() {
		Toast.makeText(getApplicationContext(), getResources().getString(
				R.string.freeroom_connection_error_happened), Toast.LENGTH_SHORT).show();
	}

}
