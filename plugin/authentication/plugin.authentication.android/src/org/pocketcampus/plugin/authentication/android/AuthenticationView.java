package org.pocketcampus.plugin.authentication.android;

import org.pocketcampus.R;
import org.pocketcampus.android.platform.sdk.core.PluginController;
import org.pocketcampus.android.platform.sdk.core.PluginView;
import org.pocketcampus.android.platform.sdk.tracker.Tracker;
import org.pocketcampus.plugin.authentication.android.iface.IAuthenticationView;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.TextView;

/**
 * AuthenticationView - Main view that opens up as a login dialog.
 * 
 * This is the main view in the authentication plugin.
 * It opens as a dialog in the middle of the screen.
 * It either prompts the user for a username and password
 * or, if the there is a valid Tequila cookie, it shows a wait screen
 * and authenticates the user silently to the required service.
 * More info on how this plugin works in the Controller.
 * 
 * @author Amer <amer.chamseddine@epfl.ch>
 * 
 */
public class AuthenticationView extends PluginView implements IAuthenticationView {

	/**
	 * Stores a reference to the Controller associated with this plugin.
	 */
	private AuthenticationController mController;
	
	/**
	 * Stores a reference to the Model associated with this plugin.
	 */
	private AuthenticationModel mModel;
	
	/**
	 * Specifies the Type of our Controller.
	 * 
	 * When we do that, the Controller will be automatically
	 * created and passed to onDisplay.
	 */
	@Override
	protected Class<? extends PluginController> getMainControllerClass() {
		return AuthenticationController.class;
	}
	
	/**
	 * Disables the Activity Title.
	 */
	@Override
	protected void onPreCreate() {
		requestWindowFeature(Window.FEATURE_NO_TITLE);
	}
	
	/**
	 * Builds the Activity/View.
	 * 
	 * Called once the view is connected to the controller.
	 * Here we "create" the activity to be displayed.
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
	
	/**
	 * Handles the other plugins' requests to authenticate.
	 * 
	 * Override handleIntent in order to handle the requests
	 * of other plugins who are requesting authentication.
	 * For the other plugins to authenticate the user,
	 * they should send an intent with action ACTION_VIEW
	 * and data pocketcampus-authenticate://authentication.plugin.pocketcampus.org/do_auth?service=%s
	 * with %s replaced by the name of the service.
	 */
	@Override
	protected void handleIntent(Intent aIntent) {
		/**
		 * This should never happen.
		 * It happened with the Monkey once,
		 * so I added this code to overcome it.
		 */
		if(mController == null) // resuming?
			return;

		/**
		 * Read the intent data and act accordingly
		 */
		if(aIntent == null)
			return;
		Bundle extras = aIntent.getExtras();
		if(extras != null && extras.getInt("askpermission") != 0) {
			askPermission();
		} else {
			displayForm();
		}
		/*if(!Intent.ACTION_VIEW.equals(aIntent.getAction()))
			return;
		Uri aData = aIntent.getData();
		if(aData == null)
			return;
		Log.v("DEBUG", aData.toString());
		if("pocketcampus-authenticate".equals(aData.getScheme())) {
			if(iAuthenticating) {
				Log.v("DEBUG", "request dropped: already authenticating");
				return;
			}
			iAuthenticating = true;
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
		}*/
	}

	/**
	 * Forwards the SessionId to the plugin that requested authentication.
	 * 
	 * This is called after the authentication has succeeded.
	 * It sends the SessionId to the plugin using an intent.
	 * 
	 * @param sessId The SessionId that we received after authentication 
	 */
	/*private void forwardSessionIdToCaller(SessionId sessId) {
		if(sessId == null) {
			// error
			return;
		}
		String url = AuthenticationController.callBackIntent;
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
	}*/
	
	/**
	 * Enables the login button and attaches the OnClickListener to it.
	 */
	/*private void enableLogin() {
		Button loginButton = (Button) findViewById(R.id.authentication_loginbutton);
		loginButton.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				// Tracker
				Tracker.getInstance().trackPageView("authentication/login");
				
				CheckBox staySignedIn = (CheckBox) findViewById(R.id.authentication_staylogged_cb);
				mModel.setStaySignedIn(staySignedIn.isChecked());
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
	}*/
	
	/**
	 * Displays the authentication form.
	 */
	private void displayForm() {
		setContentView(R.layout.authentication_customloginpage);
		/*String user = mModel.getLocalCredentials().username;
		if(user != null) {
			TextView usernameField = (TextView) findViewById(R.id.authentication_username);
			usernameField.setText(user);
		}*/
		
		TextView usernameField = (TextView) findViewById(R.id.authentication_username);
		usernameField.setText(mModel.getGasparUsername());
		TextView passwordField = (TextView) findViewById(R.id.authentication_password);
		passwordField.setText("");
		CheckBox storePasswordField = (CheckBox) findViewById(R.id.authentication_staylogged_cb);
		storePasswordField.setChecked(mModel.getStorePassword());
		
		Button loginButton = (Button) findViewById(R.id.authentication_loginbutton);
		loginButton.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				TextView usernameField = (TextView) findViewById(R.id.authentication_username);
				mModel.setGasparUsername(usernameField.getText().toString());
				TextView passwordField = (TextView) findViewById(R.id.authentication_password);
				mModel.setTempGasparPassword(passwordField.getText().toString());
				CheckBox storePasswordField = (CheckBox) findViewById(R.id.authentication_staylogged_cb);
				mModel.setStorePassword(storePasswordField.isChecked());
				mController.startLogin();
				finish();
			}
		});
		//loginButton.setEnabled(true);
	}

	private void askPermission() {
		setContentView(R.layout.authentication_askpermissionpage);
		TextView serviceName = (TextView) findViewById(R.id.authentication_servicelongname);
		serviceName.setText(mModel.getLongName());
		Button button;
		button = (Button) findViewById(R.id.authentication_alwaysallowbutton);
		button.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				mController.allowService(true);
				finish();
			}
		});
		button = (Button) findViewById(R.id.authentication_allowbutton);
		button.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				mController.allowService(false);
				finish();
			}
		});
		button = (Button) findViewById(R.id.authentication_denybutton);
		button.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				mController.denyService(false);
				finish();
			}
		});
		button = (Button) findViewById(R.id.authentication_alwaysdenybutton);
		button.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				mController.denyService(true);
				finish();
			}
		});
	}

	/**
	 * Displays the waiting (while authentication) screen.
	 */
	/*private void displayWait() {
		setContentView(R.layout.authentication_redirectionpage);
	}*/

	/**
	 * Called when we successfully login the user to Tequila.
	 */
	/*@Override
	public void gotTequilaCookie() {
		mController.nGetTequilaKey();
	}*/

	/**
	 * Called when we successfully get a token from the service's server
	 * 
	 * Called when we successfully get a token from the server
	 * of the service that is requesting authentication.
	 * e.g. from the Camipro servers.
	 */
	/*@Override
	public void gotTequilaKey() {
		mController.nAuthenticateToken(false);
	}*/

	/**
	 * Called after we successfully authenticate the token
	 * 
	 * Called after we successfully authenticate the
	 * service's token (that we got previously) with Tequila.
	 */
	/*@Override
	public void doneAuthenticatingToken() {
		if(mModel.getStaySignedIn() && mModel.getTequilaKey().isSetITequilaKeyForPc()) {
			System.out.println("Stay signed in");
			mController.nAuthenticateToken(true);
		} else {
			mController.nGetSessionId();
		}
	}*/

	/**
	 * Called after we successfully authenticate the PC token
	 * 
	 * Called after we successfully authenticate the
	 * service's token (that we got previously) with Tequila.
	 */
	/*@Override
	public void doneAuthenticatingSecToken() {
		mController.nGetSessionId();
	}*/

	/**
	 * Called when we successfully get a sessionId
	 * from the server of the service that is requesting
	 * authentication.
	 * This usually signals the end of a successful
	 * authentication procedure.
	 */
	/*@Override
	public void gotSessionId() {
		forwardSessionIdToCaller(mModel.getSessionId());
		finish();
	}*/

	/**
	 * Called when we fail to login the user
	 * due to bad credentials.
	 */
	/*@Override
	public void notifyBadCredentials() {
		Toast toast = Toast.makeText(getApplicationContext(), getResources().getString(R.string.authentication_invalid_credentials), Toast.LENGTH_SHORT);
		toast.show();
		displayForm();
	}*/

	/**
	 * Called when we fail to authenticate a token with Tequila
	 * due to the expiration of the Tequila cookie that we had stored.
	 * In this case we ask the user to type their credentials again
	 * in order to get a new Tequila cookie to be used to authenticate
	 * the tokens.
	 */
	/*@Override
	public void notifyCookieTimedOut() {
		Toast toast = Toast.makeText(getApplicationContext(), getResources().getString(R.string.authentication_cookie_expired), Toast.LENGTH_SHORT);
		toast.show();
		mModel.destroyTequilaCookie();
		displayForm();
	}*/

	/**
	 * Called when an IOException occurs.
	 * 
	 * Called when a network error (or more generally
	 * any IOException) occurs.
	 * Usually this means that the phone is not
	 * connected to the Internet. 
	 */
	@Override
	public void networkErrorHappened() {
		/*Toast toast = Toast.makeText(getApplicationContext(), getResources().getString(R.string.authentication_connection_error_happened), Toast.LENGTH_SHORT);
		toast.show();
		displayForm();*/
	}
	
	@Override
	public void onBackPressed() {
		mController.cancelAuth();
		finish();
	}

}
