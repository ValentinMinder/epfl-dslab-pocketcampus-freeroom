package org.pocketcampus.plugin.pushnotif.android;

import org.pocketcampus.plugin.pushnotif.R;
import org.pocketcampus.android.platform.sdk.core.PluginController;
import org.pocketcampus.android.platform.sdk.core.PluginView;
import org.pocketcampus.android.platform.sdk.ui.layout.StandardTitledLayout;
import org.pocketcampus.plugin.pushnotif.android.iface.IPushNotifView;

import android.os.Bundle;
import android.widget.Toast;

/**
 * PushNotifMainView - Main view that shows PushNotif courses.
 * 
 * This is the main view in the PushNotif Plugin.
 * It checks if the user is logged in, if not it pings
 * the Authentication Plugin.
 * When it gets back a valid SessionId it fetches the
 * user's PushNotif data.
 * 
 * @author Amer <amer.chamseddine@epfl.ch>
 * 
 */
public class PushNotifMainView extends PluginView implements IPushNotifView {

	private PushNotifController mController;
	private PushNotifModel mModel;
	
	private StandardTitledLayout mLayout;
	    
	@Override
	protected Class<? extends PluginController> getMainControllerClass() {
		return PushNotifController.class;
	}

	@Override
	protected void onDisplay(Bundle savedInstanceState, PluginController controller) {
		
		// Get and cast the controller and model
		mController = (PushNotifController) controller;
		mModel = (PushNotifModel) controller.getModel();

		// Setup the layout
		mLayout = new StandardTitledLayout(this);

		// The ActionBar is added automatically when you call setContentView
		setContentView(mLayout);
		//mLayout.hideTitle();

		
		mController.startRegistrationProcess();
		
	}


	@Override
	public void networkErrorHappened() {
		Toast.makeText(getApplicationContext(), getResources().getString(
				R.string.sdk_connection_error_happened), Toast.LENGTH_SHORT).show();
	}

}
