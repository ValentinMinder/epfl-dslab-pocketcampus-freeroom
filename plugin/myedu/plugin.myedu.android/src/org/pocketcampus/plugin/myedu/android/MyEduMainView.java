package org.pocketcampus.plugin.myedu.android;

import org.pocketcampus.R;
import org.pocketcampus.android.platform.sdk.core.PluginController;
import org.pocketcampus.android.platform.sdk.core.PluginView;
import org.pocketcampus.android.platform.sdk.tracker.Tracker;
import org.pocketcampus.android.platform.sdk.ui.layout.StandardTitledLayout;
import org.pocketcampus.plugin.myedu.android.iface.IMyEduView;

import android.os.Bundle;
import android.widget.Toast;

/**
 * MyEduMainView - Main view that shows MyEdu courses.
 * 
 * This is the main view in the MyEdu Plugin.
 * It checks if the user is logged in, if not it pings
 * the Authentication Plugin.
 * When it gets back a valid SessionId it fetches the
 * user's MyEdu data.
 * 
 * @author Amer <amer.chamseddine@epfl.ch>
 * 
 */
public class MyEduMainView extends PluginView implements IMyEduView {

	private MyEduController mController;
	private MyEduModel mModel;
	
	private StandardTitledLayout mLayout;
	
	@Override
	protected Class<? extends PluginController> getMainControllerClass() {
		return MyEduController.class;
	}

	@Override
	protected void onDisplay(Bundle savedInstanceState, PluginController controller) {
		//Tracker
		Tracker.getInstance().trackPageView("myedu");
		
		// Get and cast the controller and model
		mController = (MyEduController) controller;
		mModel = (MyEduModel) controller.getModel();

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
	 * myeduCookie. In this case we close the Activity.
	 */
	@Override
	protected void onResume() {
		super.onResume();
		/*if(mModel != null && mModel.getMyEduCookie() == null) {
			// Resumed and lot logged in? go back
			finish();
		}*/
	}

	@Override
	public void networkErrorHappened() {
		Toast.makeText(getApplicationContext(), getResources().getString(
				R.string.myedu_connection_error_happened), Toast.LENGTH_SHORT).show();
	}

}
