package org.pocketcampus.android.plugin.dashboard;

import org.pocketcampus.R;
import org.pocketcampus.android.platform.sdk.core.PluginController;
import org.pocketcampus.android.platform.sdk.core.PluginView;

import android.os.Bundle;

public class AboutView extends PluginView {
	
	@Override
	protected void onDisplay(Bundle savedInstanceState, PluginController controller) {
//		disableActionBar();
		setContentView(R.layout.dashboard_about);
	}

	@Override
	protected void onPause() {
		super.onPause();
		overridePendingTransition(R.anim.fadein, R.anim.fadeout);
	}
}