package org.pocketcampus.plugin.dashboard.android;

import org.pocketcampus.R;
import org.pocketcampus.android.platform.sdk.core.PluginController;
import org.pocketcampus.android.platform.sdk.core.PluginView;
import org.pocketcampus.android.platform.sdk.tracker.Tracker;

import android.content.Context;
import android.content.pm.PackageManager.NameNotFoundException;
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
		Tracker.getInstance().trackPageView("dashboard/about");
		
		setContentView(R.layout.dashboard_about);
		
		// sets the website link with the correct style
		TextView link = (TextView) findViewById(R.id.url);
		link.setText(Html.fromHtml("<font color=\"white\"><a href=\""
				+ getString(R.string.website_url) + "\">"
				+ getString(R.string.website_url) + "</a>"));
		link.setMovementMethod(LinkMovementMethod.getInstance());
		
		Context context = getApplicationContext();
		try {
			String versionName = context.getPackageManager().getPackageInfo(context.getPackageName(), 0 ).versionName;
			TextView version = (TextView) findViewById(R.id.version);
			version.setText(versionName);
		} catch (NameNotFoundException e) {
			e.printStackTrace();
		}
	}

	/**
	 * Modifies the default entry and exit animations with a fade-in and fade-out.
	 */
	@Override
	protected void onPause() {
		super.onPause();
		overridePendingTransition(R.anim.fadein, R.anim.fadeout);
	}
}