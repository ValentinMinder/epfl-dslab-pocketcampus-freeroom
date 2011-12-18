package org.pocketcampus.plugin.dashboard.android;

import org.pocketcampus.R;
import org.pocketcampus.android.platform.sdk.core.PluginController;
import org.pocketcampus.android.platform.sdk.core.PluginView;

import android.content.res.ColorStateList;
import android.graphics.Color;
import android.os.Bundle;
import android.text.Html;
import android.text.method.LinkMovementMethod;
import android.widget.TextView;

public class AboutView extends PluginView {

	@Override
	protected void onDisplay(Bundle savedInstanceState,
			PluginController controller) {
		setContentView(R.layout.dashboard_about);
		TextView link = (TextView) findViewById(R.id.url);
		link.setText(Html.fromHtml("<font color=\"white\"><a href=\""
				+ getString(R.string.website_url) + "\">"
				+ getString(R.string.website_url) + "</a>"));
		link.setMovementMethod(LinkMovementMethod.getInstance());
	}

	@Override
	protected void onPause() {
		super.onPause();
		overridePendingTransition(R.anim.fadein, R.anim.fadeout);
	}
}