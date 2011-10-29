package org.pocketcampus.android.plugin.dashboard;

import java.util.ArrayList;

import org.pocketcampus.R;
import org.pocketcampus.android.platform.sdk.core.GlobalContext;
import org.pocketcampus.android.platform.sdk.core.PluginController;
import org.pocketcampus.android.platform.sdk.core.PluginInfo;
import org.pocketcampus.android.platform.sdk.core.PluginView;

import android.os.Bundle;
import android.widget.RelativeLayout;
import android.widget.RelativeLayout.LayoutParams;

public class DashboardView extends PluginView {

	private PluginDashboard	mDashboard;
	
	@Override
	protected void onDisplay(Bundle savedInstanceState, PluginController controller) {
		setContentView(R.layout.dashboard_main);

		mDashboard = new PluginDashboard(this);

		// TODO use an intent filter instead
		ArrayList<PluginInfo> pluginManifests = ((GlobalContext) getApplication()).getAllPluginInfos();
		mDashboard.addPlugins(pluginManifests);

		RelativeLayout mainLayout = (RelativeLayout) findViewById(R.id.dashboard_main);
		LayoutParams layoutParams = new LayoutParams(LayoutParams.FILL_PARENT, LayoutParams.FILL_PARENT);
		mainLayout.addView(mDashboard.getView(), layoutParams);
	}

}