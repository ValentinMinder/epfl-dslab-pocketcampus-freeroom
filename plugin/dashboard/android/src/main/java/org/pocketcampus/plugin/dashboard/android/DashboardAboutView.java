package org.pocketcampus.plugin.dashboard.android;

import org.pocketcampus.platform.android.core.PluginController;
import org.pocketcampus.platform.android.core.PluginView;
import org.pocketcampus.plugin.dashboard.R;

import android.os.Bundle;
import android.text.Html;
import android.widget.TextView;

/**
 * DashboardStatusView - shows who is currently logged in
 * allows to log out
 * 
 * @author Amer <amer.chamseddine@epfl.ch>
 * 
 */
public class DashboardAboutView extends PluginView {


	private TextView statusText;
	
	@Override
	protected Class<? extends PluginController> getMainControllerClass() {
		return DashboardController.class;
	}

	@Override
	protected void onDisplay(Bundle savedInstanceState, PluginController controller) {
	
		setContentView(R.layout.dashboard_about_view);
		statusText = (TextView) findViewById(R.id.dashboard_about_text);
		
		
		updateDisplay();
	}
	
	

	
	private void updateDisplay() {
		
		;
		
		statusText.setText(Html.fromHtml("<h1>PocketCampus EPFL</h1>" +
				"<p>" + DashboardController.getAppVersion(getApplicationContext()) + "</p>" +
				"<p>" + getString(R.string.dashboard_about_developed) + "</p>" +
				"<p><b>PocketCampus.Org</b></p>" +
				"<p>Amer Chamseddine<br>Silviu Andrica<br>Loïc Gardiol<br>Solal Pirelli</p>" +
				"<p><b>" + getString(R.string.dashboard_about_contact) + "</b></p>" +
				"<p><p>team@pocketcampus.org</p>" +
				"<p><b>" + getString(R.string.dashboard_about_support) + "</b></p>" +
				"<p><p>1234@epfl.ch</p>"));
		
		
	}
	

	
	@Override
	protected String screenName() {
		return "/dashboard/about";
	}


}
