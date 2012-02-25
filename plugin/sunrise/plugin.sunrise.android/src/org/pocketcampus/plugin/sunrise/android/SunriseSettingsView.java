package org.pocketcampus.plugin.sunrise.android;

import org.pocketcampus.R;
import org.pocketcampus.android.platform.sdk.core.PluginController;
import org.pocketcampus.android.platform.sdk.core.PluginView;
import org.pocketcampus.plugin.sunrise.android.SunriseController.LocalCredentials;
import org.pocketcampus.plugin.sunrise.android.iface.ISunriseView;

import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

/**
 * SunriseMainView - View to input Sunrise credentials.
 * 
 * @author Amer <amer.chamseddine@epfl.ch>
 * 
 */
public class SunriseSettingsView extends PluginView implements ISunriseView {

	private SunriseController mController;
	private SunriseModel mModel;
	
	@Override
	protected Class<? extends PluginController> getMainControllerClass() {
		return SunriseController.class;
	}

	@Override
	protected void onDisplay(Bundle savedInstanceState, PluginController controller) {

		// Get and cast the controller and model
		mController = (SunriseController) controller;
		mModel = (SunriseModel) controller.getModel();

		// The ActionBar is added automatically when you call setContentView
		setContentView(R.layout.sunrise_settings);

		LocalCredentials lc = mModel.getSunriseCredentials();
		if(lc != null) {
			((Button) findViewById(R.id.sunrise_settings_login_button)).setText(R.string.sunrise_settings_logout);
			((EditText) findViewById(R.id.sunrise_settings_phonenbr)).setText(lc.username);
			((EditText) findViewById(R.id.sunrise_settings_password)).setText("******************************");
			((EditText) findViewById(R.id.sunrise_settings_phonenbr)).setEnabled(false);
			((EditText) findViewById(R.id.sunrise_settings_password)).setEnabled(false);
			((EditText) findViewById(R.id.sunrise_settings_phonenbr)).setFocusable(false);
			((EditText) findViewById(R.id.sunrise_settings_password)).setFocusable(false);
		}
		((Button) findViewById(R.id.sunrise_settings_login_button)).setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				LocalCredentials lc = mModel.getSunriseCredentials();
				if(lc != null) {
					mModel.setSunriseCredentials(null);
					((EditText) findViewById(R.id.sunrise_settings_phonenbr)).setText("");
					((EditText) findViewById(R.id.sunrise_settings_password)).setText("");
					((EditText) findViewById(R.id.sunrise_settings_phonenbr)).setEnabled(true);
					((EditText) findViewById(R.id.sunrise_settings_password)).setEnabled(true);
					((EditText) findViewById(R.id.sunrise_settings_phonenbr)).setFocusable(true);
					((EditText) findViewById(R.id.sunrise_settings_password)).setFocusable(true);
					((EditText) findViewById(R.id.sunrise_settings_phonenbr)).setFocusableInTouchMode(true);
					((EditText) findViewById(R.id.sunrise_settings_password)).setFocusableInTouchMode(true);
					((EditText) findViewById(R.id.sunrise_settings_phonenbr)).requestFocus();
					((Button) findViewById(R.id.sunrise_settings_login_button)).setText(R.string.sunrise_settings_login);
				} else {
					String user = ((EditText) findViewById(R.id.sunrise_settings_phonenbr)).getText().toString();
					String pass = ((EditText) findViewById(R.id.sunrise_settings_password)).getText().toString();
					mController.loginToSunrise(user, pass);
					((Button) findViewById(R.id.sunrise_settings_login_button)).setEnabled(false);
				}
			}
		});

	}

	@Override
	public void networkErrorHappened() {
		Toast.makeText(getApplicationContext(), getResources().getString(
				R.string.sunrise_connection_error_happened), Toast.LENGTH_SHORT).show();
		((EditText) findViewById(R.id.sunrise_settings_password)).setText("");
		((Button) findViewById(R.id.sunrise_settings_login_button)).setEnabled(true);
	}
	
	@Override
	public void serverErrorOccurred() {
	}

	@Override
	public void badCredentials() {
		Toast.makeText(getApplicationContext(), getResources().getString(
				R.string.sunrise_error_bad_credentials), Toast.LENGTH_SHORT).show();
		((EditText) findViewById(R.id.sunrise_settings_password)).setText("");
		((Button) findViewById(R.id.sunrise_settings_login_button)).setEnabled(true);
	}

	@Override
	public void smsSent() {
	}

	@Override
	public void loginSucceeded() {
		finish();
	}

	@Override
	public void remainingFreeSmsUpdated() {
	}

}
