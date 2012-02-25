package org.pocketcampus.plugin.sunrise.android;

import java.net.URLDecoder;

import org.pocketcampus.R;
import org.pocketcampus.android.platform.sdk.core.PluginController;
import org.pocketcampus.android.platform.sdk.core.PluginView;
import org.pocketcampus.plugin.sunrise.android.SunriseController.LocalCredentials;
import org.pocketcampus.plugin.sunrise.android.iface.ISunriseView;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

/**
 * SunriseMainView - Main view that shows Sunrise courses.
 * 
 * @author Amer <amer.chamseddine@epfl.ch>
 * 
 */
public class SunriseMainView extends PluginView implements ISunriseView {

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
		setContentView(R.layout.sunrise_main);

		((Button) findViewById(R.id.sunrise_button_send)).setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				String recipient = ((EditText) findViewById(R.id.sunrise_to_number)).getText().toString();
				String message = ((EditText) findViewById(R.id.sunrise_message_contents)).getText().toString();
				mController.sendSMS(recipient, message);
				((Button) findViewById(R.id.sunrise_button_send)).setEnabled(false);
			}
		});
		((EditText) findViewById(R.id.sunrise_message_contents)).addTextChangedListener(new TextWatcher() {
			@Override
			public void onTextChanged(CharSequence s, int start, int before, int count) {
			}
			@Override
			public void beforeTextChanged(CharSequence s, int start, int count, int after) {
			}
			@Override
			public void afterTextChanged(Editable s) {
				updateCharCounter();
			}
		});
		updateCharCounter();
		remainingFreeSmsUpdated();
		
	}

	/**
	 * Handles the intent that was used to start this plugin.
	 * 
	 * If we were pinged by the OS, then read the smsto number.
	 * If not logged in go to Settings view.
	 */
	@Override
	protected void handleIntent(Intent aIntent) {
		
		String toNumber = null;
		
		if(aIntent != null && Intent.ACTION_SENDTO.equals(aIntent.getAction())) {
			toNumber = aIntent.getDataString();
			System.out.println(toNumber);
			if(toNumber != null && (toNumber.startsWith("smsto:") || toNumber.startsWith("sms:"))) {
				toNumber = toNumber.substring(toNumber.indexOf(":") + 1);
				toNumber = URLDecoder.decode(toNumber);
				toNumber = toNumber.replaceAll("[^0-9+]", "");
				boolean startsWithPlus = toNumber.startsWith("+");
				toNumber = toNumber.replaceAll("[^0-9]", "");
				if(startsWithPlus)
					toNumber = "+" + toNumber;
				System.out.println(toNumber);
			}
		}
		
		if(toNumber != null) {
			((EditText) findViewById(R.id.sunrise_to_number)).setText(toNumber);
			((EditText) findViewById(R.id.sunrise_to_number)).setEnabled(false);
			((EditText) findViewById(R.id.sunrise_message_contents)).requestFocus();
		} else {
			((EditText) findViewById(R.id.sunrise_to_number)).requestFocus();
		}
		
		LocalCredentials lc = mModel.getSunriseCredentials();
		if(lc == null) {
			Intent i = new Intent(this, SunriseSettingsView.class);
			startActivity(i);
		}

	}

	/**
	 * This is called when the Activity is resumed.
	 * 
	 * If the user presses back on the Settings window,
	 * This Activity is resumed but we do not have the
	 * credentials. In this case we close the Activity.
	 */
	@Override
	protected void onResume() {
		super.onResume();
		if(mModel != null && mModel.getSunriseCredentials() == null) {
			// Resumed and lot logged in? go back
			finish();
		}
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		MenuInflater inflater = getMenuInflater();
		inflater.inflate(R.menu.sunrise_main, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(android.view.MenuItem item) {
		if(item.getItemId() == R.id.sunrise_menu_settings) {
			Intent i = new Intent(this, SunriseSettingsView.class);
			startActivity(i);
		}
		return true;
	}
	
	@Override
	public void networkErrorHappened() {
		Toast.makeText(getApplicationContext(), getResources().getString(
				R.string.sunrise_connection_error_happened), Toast.LENGTH_SHORT).show();
		((Button) findViewById(R.id.sunrise_button_send)).setEnabled(true);
	}
	
	@Override
	public void serverErrorOccurred() {
		Toast.makeText(getApplicationContext(), getResources().getString(
				R.string.sunrise_error_server_error), Toast.LENGTH_SHORT).show();
		((Button) findViewById(R.id.sunrise_button_send)).setEnabled(true);
	}

	@Override
	public void badCredentials() {
		Toast.makeText(getApplicationContext(), getResources().getString(
				R.string.sunrise_error_bad_credentials), Toast.LENGTH_SHORT).show();
		((Button) findViewById(R.id.sunrise_button_send)).setEnabled(true);
		mModel.setSunriseCredentials(null);
		Intent i = new Intent(this, SunriseSettingsView.class);
		i.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
		startActivity(i);
	}

	@Override
	public void smsSent() {
		Toast.makeText(getApplicationContext(), getResources().getString(
				R.string.sunrise_sms_sent), Toast.LENGTH_SHORT).show();
		((EditText) findViewById(R.id.sunrise_message_contents)).setText("");
		((Button) findViewById(R.id.sunrise_button_send)).setEnabled(true);
	}

	@Override
	public void loginSucceeded() {
	}

	@Override
	public void remainingFreeSmsUpdated() {
		int remainingFreeSms = mModel.getRemainingFreeSms();
		if(remainingFreeSms == -1)
			return;
		String content = getResources().getString(R.string.sunrise_remaining_free_sms) + ": " + remainingFreeSms;
		((TextView) findViewById(R.id.sunrise_main_remaining_free)).setText(content);
	}
	
	private void updateCharCounter() {
		String message = ((EditText) findViewById(R.id.sunrise_message_contents)).getText().toString();
		int smsnbr = message.length() / 160 + 1;
		int remchars = 160 * smsnbr - message.length();
		if(remchars == 160 && message.length() > 0) {
			remchars = 0;
			smsnbr--;
		}
		((TextView) findViewById(R.id.sunrise_main_char_counter)).setText(remchars + " / " + smsnbr);
	}

}
