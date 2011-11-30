package org.pocketcampus.plugin.authentication.android;

import org.pocketcampus.R;
import org.pocketcampus.android.platform.sdk.core.PluginController;
import org.pocketcampus.android.platform.sdk.core.PluginView;
import org.pocketcampus.android.platform.sdk.ui.layout.StandardLayout;
import org.pocketcampus.plugin.authentication.android.iface.IAuthenticationModel;
import org.pocketcampus.plugin.authentication.android.iface.IAuthenticationView;
import org.pocketcampus.plugin.authentication.shared.TequilaKey;
import org.pocketcampus.plugin.authentication.shared.TypeOfService;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

public class AuthenticationView extends PluginView implements IAuthenticationView {
	
	private AuthenticationController mController;
	private IAuthenticationModel mModel;
	
	private StandardLayout mLayout;

	@Override
	protected Class<? extends PluginController> getMainControllerClass() {
		return AuthenticationController.class;
	}
	
	/**
	 * Called once the view is connected to the controller.
	 * If you don't implement <code>getMainControllerClass()</code> 
	 * then the controller given here will simply be <code>null</code>.
	 */
	@Override
	protected void onDisplay(Bundle savedInstanceState, PluginController controller) {
		/*Intent aIntent = getIntent();
		String aAction = aIntent.getAction();
		Uri aData = aIntent.getData();
		Bundle aExtras = aIntent.getExtras();
		Log.v("TEST", aAction);
		Log.v("TEST", (aData == null ? "aData is null" : aData.toString()));
		Log.v("TEST", (aExtras == null ? "aExtras is null" : aExtras.toString()));
		// 11-08 21:50:05.975: V/TEST(10987): android.intent.action.VIEW
		// 11-08 21:50:05.975: V/TEST(10987): pocketcampus-redirect://PocketCampus?key=fz4nqsgcp0wasiftzptwxbbcpz3xp7m3
		Intent aIntent = getIntent();
		if("android.intent.action.VIEW".equals(aIntent.getAction())) {
			Uri aData = aIntent.getData();
			Log.v("TEST", aData.getAuthority());
			Log.v("TEST", aData.getHost());
			Log.v("TEST", aData.getPath());
			Log.v("TEST", aData.getQuery());
			Log.v("TEST", aData.getQueryParameter("key"));
			Log.v("TEST", aData.getScheme());
		}
		*/
		
		// Get and cast the controller and model
		mController = (AuthenticationController) controller;
		mModel = (AuthenticationModel) controller.getModel();
		
		// The StandardLayout is a RelativeLayout with a TextView in its center.
		mLayout = new StandardLayout(this);
		
		// The ActionBar is added automatically when you call setContentView
		setContentView(mLayout);

		// We need to force the display before asking the controller for the data, 
		// as the controller may take some time to get it.
		displayData();
		
	}

	@Override
	protected void handleIntent(Intent aIntent) {
		
		if(mController == null) // resuming?
			return;
		
		if(aIntent == null)
			return;
		if(!Intent.ACTION_VIEW.equals(aIntent.getAction()))
			return;
		Uri aData = aIntent.getData();
		if(aData == null)
			return;
		Log.v("DEBUG", aData.toString());
		if("pocketcampus-redirect".equals(aData.getScheme())) {
			TypeOfService tos = mapHostToTypeOfService(aData);
			if(tos != null) {
				mController.forwardTequilaKeyForService(tos);
			} else {
				Log.e("DEBUG", "mapQueryParameterToTypeOfService returned null");
			}
		} else if("pocketcampus-authenticate".equals(aData.getScheme())) {
			// e.g. pocketcampus-authenticate://authentication.plugin.pocketcampus.org/do_auth?service=moodle
			TypeOfService tos = mapQueryParameterToTypeOfService(aData);
			if(tos != null) {
				authenticateUserForService(tos);
			} else {
				Log.e("DEBUG", "mapQueryParameterToTypeOfService returned null");
			}
		} else {
			// TODO
			// currently moodle and camipro redirect back to http and https respectively
			// so we must capture them from here
			// ultimately this part should be captured by the pocketcampus-redirect section
			TypeOfService tos = mapHostToTypeOfService(aData);
			if(tos != null) {
				mController.forwardTequilaKeyForService(tos);
			} else {
				Log.e("DEBUG", "mapQueryParameterToTypeOfService returned null");
			}
		}
	}

	@Override
	public void gotTequilaKey() {
		if(AuthenticationController.AUTHENTICATE_TEQUILAENABLEDSERVICES_LOCALLY) {
			// TODO set title showing which service is requesting auth
			final TequilaKey teqKey = mModel.getTequilaKey();
			if(teqKey == null)
				return; // TODO display error
			Button loginButton = (Button) findViewById(R.id.authentication_loginbutton);
			loginButton.setOnClickListener(new OnClickListener() {
				public void onClick(View v) {
					TextView usernameField = (TextView) findViewById(R.id.authentication_username);
					TextView passwordField = (TextView) findViewById(R.id.authentication_password);
					mController.setLocalCredentials(usernameField.getText().toString(), passwordField.getText().toString());
					mController.signInUserLocallyToTequila(teqKey);
				}
			});
			loginButton.setEnabled(true);
		}
	}

	private void displayData() {
		mLayout.setText("Tequila Authentication" + "\n\n\n"
				+ "Redirecting... Please wait\n\n");
	}
	
	private void authenticateUserForService(TypeOfService tos) {
		boolean serviceSupportsTequila = true;
		switch(tos) {
		// put here all services that do not support Tequila
		case SERVICE_ISA:
			serviceSupportsTequila = false;
			break;
		default:
			break;
		}
		
		if(!serviceSupportsTequila || AuthenticationController.AUTHENTICATE_TEQUILAENABLEDSERVICES_LOCALLY)
			setContentView(R.layout.authentication_customloginpage);
		
		if(!serviceSupportsTequila) {
			final TypeOfService finalTos = tos;
			Button loginButton = (Button) findViewById(R.id.authentication_loginbutton);
			loginButton.setOnClickListener(new OnClickListener() {
				public void onClick(View v) {
					TextView usernameField = (TextView) findViewById(R.id.authentication_username);
					TextView passwordField = (TextView) findViewById(R.id.authentication_password);
					mController.setLocalCredentials(usernameField.getText().toString(), passwordField.getText().toString());
					mController.authenticateUserForNonTequilaService(finalTos);
				}
			});
			loginButton.setEnabled(true);
		} else {
			mController.authenticateUserForTequilaEnabledService(tos);
		}
		
	}
	
	private TypeOfService mapHostToTypeOfService(Uri aData) {
		// This is the host part of the URL that Tequila redirects to after successful authentication
		if(aData == null)
			return null;
		String pcService = aData.getHost();
		if(pcService == null)
			return null;
		if("login.pocketcampus.org".equals(pcService)) {
			return TypeOfService.SERVICE_POCKETCAMPUS;
		} else if("moodle.epfl.ch".equals(pcService)) {
			return TypeOfService.SERVICE_MOODLE;
		} else if("cmp2www.epfl.ch".equals(pcService)) {
			return TypeOfService.SERVICE_CAMIPRO;
		} else {
			return null;
		}
	}
	
	private TypeOfService mapQueryParameterToTypeOfService(Uri aData) {
		// This is the QueryParameter "service" that is set by a plugin calling us and asking for authentication
		if(aData == null)
			return null;
		String pcService = aData.getQueryParameter("service");
		if(pcService == null)
			return null;
		if("moodle".equals(pcService)) {
			return TypeOfService.SERVICE_MOODLE;
		} else if("camipro".equals(pcService)) {
			return TypeOfService.SERVICE_CAMIPRO;
		} else if("isa".equals(pcService)) {
			return TypeOfService.SERVICE_ISA;
		} else {
			return null;
		}
	}
	
	/*
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		MenuInflater inflater = getMenuInflater();
		inflater.inflate(R.menu.authentication_menu, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(android.view.MenuItem item) {
		if (item.getItemId() == R.id.authenticate_for_pocketcampus) {
			mController.authenticateUserForService(TypeOfService.SERVICE_POCKETCAMPUS);
			//finish();
		} else if (item.getItemId() == R.id.authenticate_for_moodle) {
			mController.authenticateUserForService(TypeOfService.SERVICE_MOODLE);
			//finish();
		} else if (item.getItemId() == R.id.authenticate_for_camipro) {
			mController.authenticateUserForService(TypeOfService.SERVICE_CAMIPRO);
			//finish();
		}

		return true;
	}
*/
	
	public void wrongCredentials() {
		Toast toast = Toast.makeText(getApplicationContext(), "Invalid credentials!", Toast.LENGTH_SHORT);
		toast.show();
	}

	@Override
	public void networkErrorHappened() {
		Toast toast = Toast.makeText(getApplicationContext(), "Network error!", Toast.LENGTH_SHORT);
		toast.show();
	}

	@Override
	public void mustFinish() {
		finish();
	}

}
