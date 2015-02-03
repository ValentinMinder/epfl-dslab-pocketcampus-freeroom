package org.pocketcampus.plugin.authentication.android;

import java.util.List;
import java.util.Map;
import java.util.Set;

import org.pocketcampus.platform.android.core.PluginController;
import org.pocketcampus.platform.android.core.PluginView;
import org.pocketcampus.platform.android.utils.DialogUtils;
import org.pocketcampus.plugin.authentication.R;
import org.pocketcampus.plugin.authentication.android.iface.IAuthenticationView;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.Html;
import android.text.TextUtils;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.TextView;

/**
 * AuthenticationView - Main view that opens up as a login dialog.
 * 
 * This is the main view in the authentication plugin. It opens as a dialog in
 * the middle of the screen. It either prompts the user for a username and
 * password or, if the there is a valid Tequila cookie, it shows a wait screen
 * and authenticates the user silently to the required service. More info on how
 * this plugin works in the Controller.
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
	 * When we do that, the Controller will be automatically created and passed
	 * to onDisplay.
	 */
	@Override
	protected Class<? extends PluginController> getMainControllerClass() {
		return AuthenticationController.class;
	}

	/**
	 * Builds the Activity/View.
	 * 
	 * Called once the view is connected to the controller. Here we "create" the
	 * activity to be displayed.
	 */
	@Override
	protected void onDisplay(Bundle savedInstanceState, PluginController controller) {

		// Get and cast the controller and model
		mController = (AuthenticationController) controller;
		mModel = (AuthenticationModel) controller.getModel();

		// The ActionBar is added automatically when you call setContentView,
		// unless we disable it :-)
		// disableActionBar();
	}

	@Override
	protected String screenName() {
		return "/authentication";
	}

	/**
	 * Handles the other plugins' requests to authenticate.
	 * 
	 * Override handleIntent in order to handle the requests of other plugins
	 * who are requesting authentication. For the other plugins to authenticate
	 * the user, they should send an intent with action ACTION_VIEW and data
	 * pocketcampus
	 * -authenticate://authentication.plugin.pocketcampus.org/do_auth?service=%s
	 * with %s replaced by the name of the service.
	 */
	@Override
	protected void handleIntent(Intent aIntent) {
		/**
		 * This should never happen. It happened with the Monkey once, so I
		 * added this code to overcome it.
		 */
		if (mController == null) // resuming?
			return;

		/**
		 * Read the intent data and act accordingly
		 */
		if (aIntent == null)
			return;

		Uri data = aIntent.getData();
		if (data != null && data.getQueryParameter("requestkey") != null) {
			mModel.setTequilaToken(data.getQueryParameter("requestkey"));
			mModel.setCallbackUrl(null);
			mModel.setFromBrowser(true);
			mModel.setSelfAuth(false);

			if (mModel.getSavedGasparPassword() != null) {
				// use saved password
				mModel.setTempGasparPassword(mModel.getSavedGasparPassword());
				showLoading();
				mController.startPreLogin();
			} else {
				displayForm();

			}
			return;
		}

		Bundle extras = aIntent.getExtras();
		if (extras != null && extras.getInt("askpermission") != 0) {
			askPermission();
		} else if (extras != null && extras.getInt("showloading") != 0) {
			showLoading();
		} else if (extras != null && extras.getInt("badcredentials") != 0) {
			DialogUtils.alert(this, getString(R.string.authentication_plugin_title),
					getString(R.string.authentication_invalid_credentials));
			displayForm();
		} else if (extras != null && extras.getInt("doshibboleth") != 0) {
			displayForm();
			showOrgPromptDialog();
		} else {
			displayForm();
		}
	}

	/**
	 * Displays the authentication form.
	 */
	private void displayForm() {
		setContentView(R.layout.authentication_customloginpage);

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
				mModel.setNotFromEpfl(false);
				mController.startPreLogin();
				trackEvent("LogIn", (storePasswordField.isChecked() ? "SavePasswordYes" : "SavePasswordNo"));
				done();
			}
		});

		TextView notEPFL = (TextView) findViewById(R.id.authentication_notfromepfl);
		notEPFL.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				mModel.setNotFromEpfl(true);
				mController.startPreLogin();
				done();
			}
		});
	}

	private void askPermission() {
		trackEvent("AuthenticateForService", mModel.getServiceName());
		setContentView(R.layout.authentication_askpermissionpage);
		TextView serviceName = (TextView) findViewById(R.id.authentication_servicelongname);
		serviceName.setText(mModel.getServiceName());
		TextView accessTo = (TextView) findViewById(R.id.authentication_serviceaccessto);
		accessTo.setText(TextUtils.join(", ", mModel.getServiceAccess()));
		Button button;
		button = (Button) findViewById(R.id.authentication_alwaysallowbutton);
		button.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				trackEvent("AlwaysAllow", mModel.getServiceName());
				mController.allowService(true);
				done();
			}
		});
		button = (Button) findViewById(R.id.authentication_allowbutton);
		button.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				trackEvent("Allow", mModel.getServiceName());
				mController.allowService(false);
				done();
			}
		});
		button = (Button) findViewById(R.id.authentication_denybutton);
		button.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				trackEvent("Deny", mModel.getServiceName());
				mController.denyService(false);
				done();
			}
		});
		button = (Button) findViewById(R.id.authentication_alwaysdenybutton);
		button.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				trackEvent("AlwaysDeny", mModel.getServiceName());
				mController.denyService(true);
				done();
			}
		});
	}

	private void showWebView(String entityId) {
		setContentView(R.layout.authentication_webview);

		WebView webView = (WebView) findViewById(R.id.authWebView);
		WebSettings settings = webView.getSettings();
		settings.setJavaScriptEnabled(true);
		settings.setSupportZoom(true);
		settings.setBuiltInZoomControls(true);
		webView.setWebViewClient(new WebViewClient() {
			boolean override = false;

			public boolean shouldOverrideUrlLoading(WebView view, String url) {
				System.out.println("Override=" + url);
				if (override) {
					if (mModel.getFromBrowser())
						mModel.setCallbackUrl(url);
					mController.tokenAuthenticationFinished();
					showLoading();
					return true;
				}
				if (url.startsWith("https://tequila.epfl.ch/tequilas/login")) {
					override = true;
					return false;
				}
				return false;
			}
		});

		String tequilaCallbackUrl = new Uri.Builder().scheme("https").authority("tequila.epfl.ch")
				.appendPath("tequilas").appendPath("login")
				.appendQueryParameter("requestkey", mModel.getTequilaToken()).build().toString();

		String shibbolethUrl = new Uri.Builder().scheme("https").authority("tequila.epfl.ch")
				.appendPath("Shibboleth.sso").appendPath("DS").appendQueryParameter("SAMLDS", "1")
				.appendQueryParameter("target", tequilaCallbackUrl).appendQueryParameter("entityID", entityId).build()
				.toString();

		webView.loadUrl(shibbolethUrl);
	}

	/**
	 * Displays the waiting screen.
	 */
	private void showLoading() {
		setContentView(R.layout.authentication_redirectionpage);
	}

	/**
	 * Displays dialog to prompt for org.
	 */
	private void showOrgPromptDialog() {
		final Map<String, String> orgs = mModel.getServiceOrgs();
		if (orgs == null) {
			DialogUtils.alert(this, getString(R.string.authentication_plugin_title),
					getString(R.string.authentication_ext_org_not_allowed));
			displayForm();
			return;
		}
		Set<String> keys = orgs.keySet();
		final String[] entries = keys.toArray(new String[keys.size()]);
		AlertDialog.Builder builder = new AlertDialog.Builder(this);
		builder.setCustomTitle(DialogUtils.buildDialogTitle(this, getString(R.string.authentication_org_prompt_title)));
		builder.setItems(entries, new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface arg0, int arg1) {
				showWebView(orgs.get(entries[arg1]));
			}
		});
		Dialog dlg = builder.create();
		dlg.setCanceledOnTouchOutside(true);
		dlg.show();
	}

	private void done() {
		showLoading();
	}

	@Override
	public boolean onMenuItemSelected(int featureId, MenuItem item) {
		if(featureId == 0){
			onBackPressed();
		}
		return super.onMenuItemSelected(featureId, item);
	}

	/**
	 * Called when an IOException occurs.
	 * 
	 * Called when a network error (or more generally any IOException) occurs.
	 * Usually this means that the phone is not connected to the Internet.
	 */
	@Override
	public void networkErrorHappened() {
	}

	@Override
	public void onBackPressed() {
		mController.cancelAuth();
		finish();
	}

	@Override
	public void shouldFinish() {
		finish();
	}

	@Override
	public void gotUserAttributes(List<String> attr) {
	}

	@Override
	public void deletedSessions(Integer c) {
	}

}
