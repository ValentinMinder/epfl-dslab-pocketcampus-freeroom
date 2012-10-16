package org.pocketcampus.plugin.blank.android;

import org.pocketcampus.R;
import org.pocketcampus.android.platform.sdk.core.PluginController;
import org.pocketcampus.android.platform.sdk.core.PluginView;
import org.pocketcampus.android.platform.sdk.tracker.Tracker;
import org.pocketcampus.android.platform.sdk.ui.layout.StandardTitledLayout;
import org.pocketcampus.plugin.blank.android.iface.IBlankView;

import android.os.Bundle;
import android.widget.Toast;

/**
 * BlankMainView - Main view that shows Blank courses.
 * 
 * This is the main view in the Blank Plugin.
 * It checks if the user is logged in, if not it pings
 * the Authentication Plugin.
 * When it gets back a valid SessionId it fetches the
 * user's Blank data.
 * 
 * @author Amer <amer.chamseddine@epfl.ch>
 * 
 */
public class BlankMainView extends PluginView implements IBlankView {

	private BlankController mController;
	private BlankModel mModel;
	
	private StandardTitledLayout mLayout;
	
	@Override
	protected Class<? extends PluginController> getMainControllerClass() {
		return BlankController.class;
	}

	@Override
	protected void onDisplay(Bundle savedInstanceState, PluginController controller) {
		//Tracker
		Tracker.getInstance().trackPageView("blank");
		
		// Get and cast the controller and model
		mController = (BlankController) controller;
		mModel = (BlankModel) controller.getModel();

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
	 * blankCookie. In this case we close the Activity.
	 */
	@Override
	protected void onResume() {
		super.onResume();
		/*if(mModel != null && mModel.getBlankCookie() == null) {
			// Resumed and lot logged in? go back
			finish();
		}*/
	}

	@Override
	public void networkErrorHappened() {
		Toast.makeText(getApplicationContext(), getResources().getString(
				R.string.blank_connection_error_happened), Toast.LENGTH_SHORT).show();
	}

}
