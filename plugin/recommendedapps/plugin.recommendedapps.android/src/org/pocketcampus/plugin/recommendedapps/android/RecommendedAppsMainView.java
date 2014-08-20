package org.pocketcampus.plugin.recommendedapps.android;

import java.util.Map;

import org.pocketcampus.platform.android.core.PluginController;
import org.pocketcampus.platform.android.core.PluginView;
import org.pocketcampus.platform.android.ui.layout.StandardTitledLayout;
import org.pocketcampus.plugin.recommendedapps.R;
import org.pocketcampus.plugin.recommendedapps.android.iface.IRecommendedAppsView;
import org.pocketcampus.plugin.recommendedapps.shared.RecommendedApp;
import org.pocketcampus.plugin.recommendedapps.shared.RecommendedAppCategory;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;

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

		mLayout.setText(getString(R.string.recommendedapps_downloading));
		setActionBarTitle(getString(R.string.recommendedapps_plugin_title));
				
		mController.refreshRecommendedApps(this);
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
	}
	
	@Override
	protected String screenName() {
		return "/recommendedapps";
	}

	@Override
	public void networkErrorHappened() {
		mLayout.setText(getString(R.string.recommendedapps_connection_error_happened));
		Toast.makeText(getApplicationContext(), getResources().getString(
				R.string.recommendedapps_connection_error_happened), Toast.LENGTH_SHORT).show();
		
	}

	@Override
	public void serverDown() {
		mLayout.setText(getString(R.string.recommendedapps_error_recommendedapps_down));
		Toast.makeText(getApplicationContext(), getResources().getString(
				R.string.recommendedapps_error_recommendedapps_down), Toast.LENGTH_SHORT).show();
		
	}

	@Override
	public void recommendedAppsRefreshed() {
		int stubImage = android.R.drawable.ic_popup_sync;
	    int imageForEmptyUri = android.R.drawable.ic_menu_gallery;
	    int imageOnFail = android.R.drawable.ic_menu_report_image;
	    
		DisplayImageOptions options = new DisplayImageOptions.Builder()
				.showStubImage(stubImage)
				.showImageForEmptyUri(imageForEmptyUri)
				.showImageOnFail(imageOnFail).cacheInMemory().cacheOnDisc()
				.build();
		System.out.println(mModel.apps());
		System.out.println(mModel.categories());
		
		Map<Integer, RecommendedApp> apps = mModel.apps();
		
		LayoutInflater inflater = (LayoutInflater)getBaseContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		
		LinearLayout storeLayout = (LinearLayout) inflater.inflate(R.layout.app_store, null);
		LinearLayout linearLayout = (LinearLayout) storeLayout.findViewById(R.id.recommendedAppCategoryList);
		
		for(RecommendedAppCategory category : mModel.categories()){
			LinearLayout categoryLayout = (LinearLayout) inflater.inflate(R.layout.app_store_category, null);
			((TextView)categoryLayout.findViewById(R.id.recommendedAppCategoryName)).setText(category.getCategoryName());
			((TextView)categoryLayout.findViewById(R.id.recommendedAppCategoryDescription)).setText(category.getCategoryDescription());
			LinearLayout appLayout = (LinearLayout) categoryLayout.findViewById(R.id.recommendedAppCategoryApps);
			for(int appId : category.getAppIds()){
				final RecommendedApp app = apps.get(appId);
				LinearLayout appThumbLayout = (LinearLayout) inflater.inflate(R.layout.app_thumb, null);
				((TextView)appThumbLayout.findViewById(R.id.recommendedAppName)).setText(app.getAppName());
				((TextView)appThumbLayout.findViewById(R.id.recommendedAppDescription)).setText(app.getAppDescription());
				ImageLoader.getInstance().displayImage(app.getAppLogoURL(), (ImageView) appThumbLayout.findViewById(R.id.recommendedAppLogo), options);
				appLayout.addView(appThumbLayout);
				appThumbLayout.setOnClickListener(new OnClickListener() {
					
					@Override
					public void onClick(View v) {
						System.out.println("CLICKED ON "+app);
						final String appPackageName = app.getAppStoreQuery();
						try {
						    startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=" + appPackageName)));
						} catch (android.content.ActivityNotFoundException anfe) {
						    startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("http://play.google.com/store/apps/details?id=" + appPackageName)));
						}
					}
				});
			}
			linearLayout.addView(categoryLayout);
		}
		
		setContentView(storeLayout);
	}
}
