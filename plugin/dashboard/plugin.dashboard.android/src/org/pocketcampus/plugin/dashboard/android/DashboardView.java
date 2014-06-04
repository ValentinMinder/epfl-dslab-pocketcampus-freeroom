package org.pocketcampus.plugin.dashboard.android;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

import org.pocketcampus.android.platform.sdk.core.GlobalContext;
import org.pocketcampus.android.platform.sdk.core.PluginController;
import org.pocketcampus.android.platform.sdk.core.PluginInfo;
import org.pocketcampus.android.platform.sdk.core.PluginView;
import org.pocketcampus.android.platform.sdk.ui.layout.DashboardLayout;
import org.pocketcampus.plugin.dashboard.R;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.RelativeLayout.LayoutParams;
import android.widget.TextView;

/**
 * 
 * View for the Dashboard.
 * 
 * @author Florian <florian.laurent@epfl.ch>
 *
 */
public class DashboardView extends PluginView {

	private PluginDashboard	mDashboard;
	private DashboardController	mController;
	
	@Override
	protected Class<? extends PluginController> getMainControllerClass() {
		return DashboardController.class;
	}

	@Override
	protected void onDisplay(Bundle savedInstanceState, PluginController controller) {
		mController = (DashboardController) controller;
		
		setContentView(R.layout.dashboard_main);
		
		displayPlugins();
		
		mController.registerPushNotif();
		mController.fetchDynamicConfig(this);
	}
	
	public void displayPlugins() {
		// Creates and fills in the <code>PluginDashboard</code>.
		mDashboard = new PluginDashboard(this);
		ArrayList<PluginInfo> pluginManifests = ((GlobalContext) getApplication()).getAllPluginInfos();
		mDashboard.addPlugins(pluginManifests);

		RelativeLayout mainLayout = (RelativeLayout) findViewById(R.id.dashboard_main);
		mainLayout.removeAllViews();
		LayoutParams layoutParams = new LayoutParams(LayoutParams.FILL_PARENT, LayoutParams.FILL_PARENT);
		mainLayout.addView(mDashboard.getView(), layoutParams);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		MenuInflater inflater = getMenuInflater();
		inflater.inflate(R.menu.dashboard_menu, menu);
		return true;
	}
	
	/**
	 * Modifies the default entry and exit animations with a fade-in and fade-out.
	 * 
	 * @param item
	 * @return
	 */
	@Override
	public boolean onOptionsItemSelected(android.view. MenuItem item) {
		if (item.getItemId() == R.id.dashboard_about) {
			trackEvent("OpenAbout", null);
			startActivity(new Intent(this, AboutView.class));
			overridePendingTransition(R.anim.fadein, R.anim.fadeout);
		}
		if (item.getItemId() == R.id.dashboard_logout) {
			trackEvent("LogOut", null);
			Intent intent = new Intent();
			intent.setAction("org.pocketcampus.plugin.authentication.LOGOUT");
			sendBroadcast(intent); 
		}

		return true;
	}
	
	@Override
	protected String screenName() {
		return "/dashboard";
	}
	
	@Override
	protected void onResume() {
		super.onResume();
	}
	
	
	
	
	
	/************
	 * HELPER
	 */
	
	
	public class PluginDashboard {
		private DashboardLayout	mDashboard;
		private Context	mContext;
		private LayoutInflater mInflater;

		public PluginDashboard(Context context) {
			mContext = context;
			mDashboard = new DashboardLayout(context);
			mInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		}

		/**
		 * Adds icons for multiple plugins to the dashboard.
		 * @param pluginInfos <code>PluginManifest</code>s of the plugins to add
		 */
		public void addPlugins(ArrayList<PluginInfo> pluginInfos) {
			Comparator<PluginInfo> comparator = new Comparator<PluginInfo>() {
				@Override
				public int compare(PluginInfo lhs, PluginInfo rhs) {
					return lhs.getLabel().compareToIgnoreCase(rhs.getLabel());
				}
			};
			Collections.sort(pluginInfos, comparator );
			
			for(PluginInfo pluginInfo : pluginInfos) {
				addPlugin(pluginInfo);
			}
		}

		/**
		 * Adds a plugin to the dashboard.
		 * @param pluginInfo <code>PluginManifest</code> of the plugin to add
		 */
		public void addPlugin(final PluginInfo pluginInfo) {
			// fills in and adds the launcher view
			View launcherView = mInflater.inflate(R.layout.dashboard_plugin_button, null);

			ImageView launcherImage = (ImageView) launcherView.findViewById(R.id.launcher_image);
			launcherImage.setImageDrawable(pluginInfo.getIcon());

			TextView launcherText = (TextView) launcherView.findViewById(R.id.launcher_text);
			launcherText.setText(pluginInfo.getLabel());

			mDashboard.addView(launcherView);
			
			// adds the click listener
			launcherView.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View v) {
					trackEvent("OpenPlugin", pluginInfo.getId());
					((GlobalContext) mContext.getApplicationContext()).displayPlugin(mContext, pluginInfo);
				}
			});
		}

		/**
		 * Returns the dashboard's <code>View</code>.
		 * @return
		 */
		public View getView() {
			return mDashboard;
		}



	}

	
}