package org.pocketcampus.plugin.dashboard.android;

import org.pocketcampus.android.platform.sdk.core.PluginController;
import org.pocketcampus.android.platform.sdk.core.PluginView;
import org.pocketcampus.plugin.dashboard.R;

import android.os.Bundle;
import android.text.Html;
import android.text.method.LinkMovementMethod;
import android.widget.TextView;

/**
 * View with the About information: team members, version, website URL and sponsors.
 * 
 * @author Florian <florian.laurent@epfl.ch>
 */
public class AboutView extends PluginView {

	@Override
	protected void onDisplay(Bundle savedInstanceState, PluginController controller) {
		setContentView(R.layout.dashboard_about);
		
		// sets the website link with the correct style
		TextView link = (TextView) findViewById(R.id.url);
		link.setText(Html.fromHtml("<font color=\"white\"><a href=\""
				+ getString(R.string.website_url) + "\">"
				+ getString(R.string.website_url) + "</a>"));
		link.setMovementMethod(LinkMovementMethod.getInstance());
		
		TextView version = (TextView) findViewById(R.id.version);
		version.setText(DashboardController.getAppVersion(getApplicationContext()));
	}

	/**
	 * Modifies the default entry and exit animations with a fade-in and fade-out.
	 */
	@Override
	protected void onPause() {
		super.onPause();
		overridePendingTransition(R.anim.fadein, R.anim.fadeout);
	}
	
	@Override
	protected String screenName() {
		return "/dashboard/about";
	}
	
}