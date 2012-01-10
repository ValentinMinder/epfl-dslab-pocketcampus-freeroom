package org.pocketcampus.plugin.authentication.android;

import static org.pocketcampus.plugin.authentication.android.AuthenticationController.mapQueryParameterToTypeOfService;

import org.pocketcampus.R;
import org.pocketcampus.android.platform.sdk.core.PluginController;
import org.pocketcampus.android.platform.sdk.core.PluginView;
import org.pocketcampus.android.platform.sdk.tracker.Tracker;
import org.pocketcampus.plugin.authentication.android.iface.IAuthenticationView;
import org.pocketcampus.plugin.authentication.shared.SessionId;
import org.pocketcampus.plugin.authentication.shared.TypeOfService;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
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
		
		// Get and cast the controller and model
		mController = (AuthenticationController) controller;
		mModel = (AuthenticationModel) controller.getModel();
		
		// The ActionBar is added automatically when you call setContentView, unless we disable it :-)
		disableActionBar();
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
		if("pocketcampus-authenticate".equals(aData.getScheme())) {
			// e.g. pocketcampus-authenticate://authentication.plugin.pocketcampus.org/do_auth?service=moodle
			// TODO add check if we are already authenticating, do not accept another request
			TypeOfService tos = mapQueryParameterToTypeOfService(aData);
			if(tos != null) {
				mController.nSetTypeOfService(tos);
				if(mController.isTequilaEnabledService() && mModel.getTequilaCookie() != null) {
					displayWait();
					mController.nGetTequilaKey();
				} else {
					displayForm();
				}
			} else {
				Log.e("DEBUG", "mapQueryParameterToTypeOfService returned null");
			}
		}
	}

	//////////////////
	
	private void forwardSessionIdToCaller(SessionId sessId) {
		if(sessId == null) {
			return; // TODO tell view that unexpected error happened
		}
		String url = "pocketcampus-authenticate://%s.plugin.pocketcampus.org/auth_done?sessid=%s";
		switch(sessId.getTos()) {
		case SERVICE_POCKETCAMPUS:
			url = String.format(url, "pocketcampus", Uri.encode(sessId.getPocketCampusSessionId()));
			break;
		case SERVICE_MOODLE:
			url = String.format(url, "moodle", Uri.encode(sessId.getMoodleCookie()));
			break;
		case SERVICE_CAMIPRO:
			url = String.format(url, "camipro", Uri.encode(sessId.getCamiproCookie()));
			break;
		case SERVICE_ISA:
			url = String.format(url, "isacademia", Uri.encode(sessId.getIsaCookie()));
			break;
		default:
			// error
			return;
		}
		Intent callerIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
		callerIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
		startActivity(callerIntent);
		//mModel.setMustFinish();
	}
	
	//////////////////
	
	private void enableLogin() {
		Button loginButton = (Button) findViewById(R.id.authentication_loginbutton);
		loginButton.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				TextView usernameField = (TextView) findViewById(R.id.authentication_username);
				TextView passwordField = (TextView) findViewById(R.id.authentication_password);
				mController.nSetLocalCredentials(usernameField.getText().toString(), passwordField.getText().toString());
				//setContentView(R.layout.authentication_redirectionpage);
				if(mController.isTequilaEnabledService()) {
					mController.nGetTequilaCookie();
				} else {
					mController.nGetSessionIdDirectlyFromProvider();
				}
				displayWait();
				//getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
			}
		});
		loginButton.setEnabled(true);
	}
	
	//////////////////
	
	private void displayForm() {
		setContentView(R.layout.authentication_customloginpage);
		enableLogin();
	}
	private void displayWait() {
		setContentView(R.layout.authentication_redirectionpage);
	}
	
	//////////////////
	
	@Override
	public void gotTequilaCookie() {
		mController.nGetTequilaKey();
	}

	@Override
	public void gotTequilaKey() {
		mController.nAuthenticateToken();
	}

	@Override
	public void gotAuthenticatedToken() {
		mController.nGetSessionId();
	}

	@Override
	public void gotSessionId() {
		forwardSessionIdToCaller(mModel.getSessionId());
		finish();
	}

	//////////////////
	
	@Override
	public void notifyBadCredentials() {
		Toast toast = Toast.makeText(getApplicationContext(), getResources().getString(R.string.authentication_invalid_credentials), Toast.LENGTH_SHORT);
		toast.show();
		displayForm();
	}

	@Override
	public void notifyCookieTimedOut() {
		Toast toast = Toast.makeText(getApplicationContext(), getResources().getString(R.string.authentication_cookie_expired), Toast.LENGTH_SHORT);
		toast.show();
		mModel.setTequilaCookie(null);
		displayForm();
	}

	@Override
	public void networkErrorHappened() {
		Toast toast = Toast.makeText(getApplicationContext(), getResources().getString(R.string.authentication_connection_error_happened), Toast.LENGTH_SHORT);
		toast.show();
		displayForm();
	}

}
