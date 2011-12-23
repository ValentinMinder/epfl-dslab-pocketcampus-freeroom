package org.pocketcampus.plugin.dashboard.android;

import java.util.ArrayList;

import org.pocketcampus.R;
import org.pocketcampus.android.platform.sdk.core.GlobalContext;
import org.pocketcampus.android.platform.sdk.core.PluginController;
import org.pocketcampus.android.platform.sdk.core.PluginInfo;
import org.pocketcampus.android.platform.sdk.core.PluginView;
import org.pocketcampus.android.platform.sdk.tracker.Tracker;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.widget.RelativeLayout;
import android.widget.RelativeLayout.LayoutParams;

public class DashboardView extends PluginView {

	private PluginDashboard	mDashboard;
	
	@Override
	protected void onDisplay(Bundle savedInstanceState, PluginController controller) {
		//Tracker
		Tracker.getInstance().trackPageView("dashboard");
		
		setContentView(R.layout.dashboard_main);
				
		mDashboard = new PluginDashboard(this);

		// TODO use an intent filter instead
		ArrayList<PluginInfo> pluginManifests = ((GlobalContext) getApplication()).getAllPluginInfos();
		mDashboard.addPlugins(pluginManifests);

		RelativeLayout mainLayout = (RelativeLayout) findViewById(R.id.dashboard_main);
		LayoutParams layoutParams = new LayoutParams(LayoutParams.FILL_PARENT, LayoutParams.FILL_PARENT);
		mainLayout.addView(mDashboard.getView(), layoutParams);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		MenuInflater inflater = getMenuInflater();
		inflater.inflate(R.menu.dashboard_menu, menu);
		return true;
	}
	
	@Override
	public boolean onOptionsItemSelected(android.view.MenuItem item) {
		if (item.getItemId() == R.id.dashboard_about) {
			startActivity(new Intent(this, AboutView.class));
			overridePendingTransition(R.anim.fadein, R.anim.fadeout);
		}

		return true;
	}
}