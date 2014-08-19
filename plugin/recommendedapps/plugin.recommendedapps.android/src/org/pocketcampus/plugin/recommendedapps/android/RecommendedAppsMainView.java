package org.pocketcampus.plugin.recommendedapps.android;

import org.pocketcampus.platform.android.core.PluginController;
import org.pocketcampus.platform.android.core.PluginView;
import org.pocketcampus.platform.android.ui.layout.StandardTitledLayout;
import org.pocketcampus.plugin.recommendedapps.R;
import org.pocketcampus.plugin.recommendedapps.android.iface.IRecommendedAppsView;

import android.os.Bundle;
import android.widget.Toast;

/**
 * RecommendedAppsMainView - Main view that shows RecommendedApps courses.
 * 
 * This is the main view in the RecommendedApps Plugin.
 * It checks if the user is logged in, if not it pings
 * the Authentication Plugin.
 * When it gets back a valid SessionId it fetches the
 * user's RecommendedApps data.
 * 
 * @author Amer <amer.chamseddine@epfl.ch>
 * 
 */
public class RecommendedAppsMainView extends PluginView implements IRecommendedAppsView {

	private RecommendedAppsController mController;
	private RecommendedAppsModel mModel;
	
	private StandardTitledLayout mLayout;
	
	@Override
	protected Class<? extends PluginController> getMainControllerClass() {
		return RecommendedAppsController.class;
	}

	@Override
	protected void onDisplay(Bundle savedInstanceState, PluginController controller) {
		// Get and cast the controller and model
		mController = (RecommendedAppsController) controller;
		mModel = (RecommendedAppsModel) controller.getModel();

		// Setup the layout
		mLayout = new StandardTitledLayout(this);

		// The ActionBar is added automatically when you call setContentView
		setContentView(mLayout);
		mLayout.hideTitle();

		mLayout.setText("Loading recommended apps");
		setActionBarTitle(getString(R.string.recommendedapps_plugin_title));

	}

	/**
	 * This is called when the Activity is resumed.
	 * 
	 * If the user presses back on the Authentication window,
	 * This Activity is resumed but we do not have the
	 * recommendedappsCookie. In this case we close the Activity.
	 */
	@Override
	protected void onResume() {
		super.onResume();
		mController.refreshRecommendedApps(this);
	}
	
	@Override
	protected String screenName() {
		return "/recommendedapps";
	}

	@Override
	public void networkErrorHappened() {
		Toast.makeText(getApplicationContext(), getResources().getString(
				R.string.recommendedapps_connection_error_happened), Toast.LENGTH_SHORT).show();
	}

	@Override
	public void serverDown() {
		Toast.makeText(getApplicationContext(), getResources().getString(
				R.string.recommendedapps_connection_error_happened), Toast.LENGTH_SHORT).show();
		
	}

	@Override
	public void networkErrorCacheExists() {
		Toast.makeText(getApplicationContext(), getResources().getString(
				R.string.recommendedapps_connection_error_happened), Toast.LENGTH_SHORT).show();
		
	}

	@Override
	public void recommendedAppsRefreshed() {
		mLayout.setText(mModel.apps()+"\n\n"+mModel.categories());
		System.out.println(mModel.apps());
		System.out.println(mModel.categories());
	}

}
