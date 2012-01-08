package org.pocketcampus.plugin.authentication.android;

import static org.pocketcampus.plugin.authentication.android.AuthenticationController.mapHostToTypeOfService;
import static org.pocketcampus.plugin.authentication.android.AuthenticationController.mapQueryParameterToTypeOfService;

import org.pocketcampus.R;
import org.pocketcampus.android.platform.sdk.core.PluginController;
import org.pocketcampus.android.platform.sdk.core.PluginView;
import org.pocketcampus.android.platform.sdk.tracker.Tracker;
import org.pocketcampus.plugin.authentication.android.iface.IAuthenticationModel;
import org.pocketcampus.plugin.authentication.android.iface.IAuthenticationView;
import org.pocketcampus.plugin.authentication.android.req.LoginToTequilaRequest;
import org.pocketcampus.plugin.authentication.shared.TequilaKey;
import org.pocketcampus.plugin.authentication.shared.TypeOfService;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

public class AuthenticationView extends PluginView implements IAuthenticationView {
	
	private AuthenticationController mController;
	private AuthenticationModel mModel;
	
	//private StandardLayout mLayout;

	@Override
	protected Class<? extends PluginController> getMainControllerClass() {
		return AuthenticationController.class;
	}
	
	@Override
	protected void onPreCreate() {
		requestWindowFeature(Window.FEATURE_NO_TITLE);
	}
	
	/**
	 * Called once the view is connected to the controller.
	 * If you don't implement <code>getMainControllerClass()</code> 
	 * then the controller given here will simply be <code>null</code>.
	 */
	@Override
	protected void onDisplay(Bundle savedInstanceState, PluginController controller) {
		// Tracker
		Tracker.getInstance().trackPageView("authentication");
		
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
		//mLayout = new StandardLayout(this);
		
		// The ActionBar is added automatically when you call setContentView, unless we disable it :-)
		disableActionBar();
		//setContentView(R.layout.authentication_redirectionpage);

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
				//mController.forwardTequilaKeyForService(tos);
			} else {
				Log.e("DEBUG", "mapQueryParameterToTypeOfService returned null");
			}
		} else if("pocketcampus-authenticate".equals(aData.getScheme())) {
			// e.g. pocketcampus-authenticate://authentication.plugin.pocketcampus.org/do_auth?service=moodle
			TypeOfService tos = mapQueryParameterToTypeOfService(aData);
			if(tos != null) {
				//authenticateUserForService(tos);
				mModel.setTypeOfService(tos);
				mModel.setIntState(0);
				mModel.setAuthState(0);
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
				//mController.forwardTequilaKeyForService(tos);
			} else {
				Log.e("DEBUG", "mapQueryParameterToTypeOfService returned null");
			}
		}
	}

	/*
	public void gotTequilaKey() {
		final TequilaKey teqKey = mModel.getTequilaKey();
		if(teqKey == null)
			return; // TODO display error
		if(AuthenticationController.AUTHENTICATE_TEQUILAENABLEDSERVICES_LOCALLY) {
			// TODO set title showing which service is requesting auth
			Button loginButton = (Button) findViewById(R.id.authentication_loginbutton);
			loginButton.setOnClickListener(new OnClickListener() {
				public void onClick(View v) {
					// Tracker
					Tracker.getInstance().trackPageView("authentication/login/click");
					
					TextView usernameField = (TextView) findViewById(R.id.authentication_username);
					TextView passwordField = (TextView) findViewById(R.id.authentication_password);
					mController.setLocalCredentials(usernameField.getText().toString(), passwordField.getText().toString());
					setContentView(R.layout.authentication_redirectionpage);
					new LoginToTequilaRequest().start(mController, mController.getThreadSafeClient(), mController.getLocalCredentials());
					//mController.signInUserLocallyToTequila(teqKey);
				}
			});
			loginButton.setEnabled(true);
		}
	}*/

	private void displayData() {
		/*mLayout.setText("Tequila Authentication" + "\n\n\n"
				+ "Redirecting... Please wait\n\n");*/
	}
	/*
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
	*/
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
	
	public void enableLogin() {
		Button loginButton = (Button) findViewById(R.id.authentication_loginbutton);
		loginButton.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				TextView usernameField = (TextView) findViewById(R.id.authentication_username);
				TextView passwordField = (TextView) findViewById(R.id.authentication_password);
				mController.setLocalCredentials(usernameField.getText().toString(), passwordField.getText().toString());
				//setContentView(R.layout.authentication_redirectionpage);
				mController.nGetTequilaCookie();
				displayWait();
				getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
			}
		});
		loginButton.setEnabled(true);
	}
	
	public void displayForm() {
		
		setContentView(R.layout.authentication_customloginpage);
	}
	public void displayWait() {
		setContentView(R.layout.authentication_redirectionpage);
		
	}
	
	@Override
	public void notifyBadCredentials() {
		Toast toast = Toast.makeText(getApplicationContext(), getResources().getString(R.string.authentication_invalid_credentials), Toast.LENGTH_SHORT);
		toast.show();
		
		mModel.setAuthState(0);
		mModel.setIntState(0);
		//displayForm();
		
	}

	@Override
	public void notifyBadToken() {
		Toast toast = Toast.makeText(getApplicationContext(), getResources().getString(R.string.authentication_invalid_token), Toast.LENGTH_SHORT);
		toast.show();
		
		mModel.setAuthState(0);
		mModel.setIntState(0);
		//displayForm();
		
	}

	@Override
	public void notifyUnexpectedErrorHappened() {
		Toast toast = Toast.makeText(getApplicationContext(), getResources().getString(R.string.authentication_unexpected_error), Toast.LENGTH_SHORT);
		toast.show();
		
		//mModel.setAuthState(0);
		mModel.setIntState(0);
		//displayForm();
		
	}
	
	@Override
	public void networkErrorHappened() {
		Toast toast = Toast.makeText(getApplicationContext(), getResources().getString(R.string.authentication_connection_error_happened), Toast.LENGTH_SHORT);
		toast.show();
		
		//mModel.setAuthState(0);
		mModel.setIntState(0);
		//displayForm();
		
		/*int is = mModel.getIntState();
		int as = mModel.getAuthState();
		
		if((is|1) == 1) {
			mModel.setIntState(is - 1);
		}
		
		if((as|1) == 1) {
			mModel.setAuthState(as - 1);
		}*/
		
	}

	@Override
	public void intStateUpdated() {
		int is = mModel.getIntState();
		switch(is) {
		case 0:
			displayForm();
			break;
		case 2:
			mController.nAuthenticateToken();
			break;
		default:
			break;
		}
		
	}

	@Override
	public void authStateUpdated() {
		int as = mModel.getAuthState();
		switch(as) {
		case 0:
			mController.nGetTequilaKey();
			break;
		case 2:
			enableLogin();
			break;
		case 4:
			mController.nGetSessionId();
			break;
		case 6:
			mController.nForwardSessionId();
			finish();
			break;
		default:
			break;
		}
		
	}

}
