package org.pocketcampus.plugin.authentication.android;

import java.util.List;

import org.pocketcampus.platform.android.cache.RequestCache;
import org.pocketcampus.platform.android.core.PluginController;
import org.pocketcampus.platform.android.core.PluginView;
import org.pocketcampus.platform.android.utils.DialogUtils;
import org.pocketcampus.plugin.authentication.R;
import org.pocketcampus.plugin.authentication.android.iface.IAuthenticationView;
import org.pocketcampus.plugin.authentication.android.req.FetchUserAttributes;
import org.pocketcampus.plugin.authentication.android.req.LogoutAllSessions;
import org.pocketcampus.plugin.authentication.shared.LogoutRequest;

import android.content.Intent;
import android.os.Bundle;
import android.text.Html;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;

/**
 * AuthenticationStatusView - shows who is currently logged in
 * allows to log out
 * 
 * @author Amer <amer.chamseddine@epfl.ch>
 * 
 */
public class AuthenticationStatusView extends PluginView implements IAuthenticationView {


	private AuthenticationController mController;
	private AuthenticationModel mModel;
	
	private Button logoutButton;
	private TextView statusText;
	
	@Override
	protected Class<? extends PluginController> getMainControllerClass() {
		return AuthenticationController.class;
	}

	@Override
	protected void onDisplay(Bundle savedInstanceState, PluginController controller) {
	
		// Get and cast the controller and model
		mController = (AuthenticationController) controller;
		mModel = (AuthenticationModel) controller.getModel();

		
		setContentView(R.layout.authentication_status);
		logoutButton = (Button) findViewById(R.id.authentication_logout_button);
		statusText = (TextView) findViewById(R.id.authentication_status_text);
		
		logoutButton.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				trackEvent("LogOut", null);
				logout();
			}
		});
		logoutButton.setText(getString(R.string.authentication_signed_out_all));
		
		setActionBarTitle(getString(R.string.authentication_plugin_title));

		refreshView();
	}
	
	public void refreshView() {
		if(mModel.getPcSessionId() == null) {
			updateDisplay(null);
			return;
		}
		mController.getUserAttributes(this, true);
	}

	

	@Override
	public void gotUserAttributes(List<String> attr) {
		if(attr == null || attr.size() != 2)
			updateDisplay(null);
		else
			updateDisplay(attr.get(0) + " " + attr.get(1));
	}

	
	private void updateDisplay(String name) {
		String account = null;
		if(mModel.getSavedGasparPassword() != null)
			account = mModel.getGasparUsername();
		
		String s1 = (name == null ? "" : String.format(getString(R.string.authentication_logged_in_as), name));
		String s2 = (account == null ? (name == null ? getString(R.string.authentication_not_logged_in) : 
			getString(R.string.authentication_session_will_time_out)) : 
				String.format(getString(R.string.authentication_password_saved), account));
		
		
		statusText.setText(Html.fromHtml("<p>" + s1 + "</p><p>" + s2 + "</p>"));
		
		logoutButton.setVisibility(account == null && name == null ? View.GONE : View.VISIBLE);
		
	}
	
	public void logout() {
//		boolean signingOut = false;
//		if(mModel.getPcSessionId() != null) {
//			signingOut = true;
//			LogoutRequest req = new LogoutRequest(mModel.getPcSessionId());
//			new LogoutAllSessions(this).start(mController, mController.getThriftClient(), req);
//		}
		
		mModel.setSavedGasparPassword(null);
		mModel.setGasparUsername(null);
		mModel.setStorePassword(true);
		mModel.setPcSessionId(null);
		
		RequestCache.invalidateCache(this, FetchUserAttributes.class.getCanonicalName());

		Intent intent = new Intent();
		intent.setAction("org.pocketcampus.plugin.authentication.LOGOUT");
		sendBroadcast(intent, "org.pocketcampus.permissions.AUTHENTICATE_WITH_TEQUILA");
		
//		if(!signingOut)
//			refreshView();
		refreshView();
	}
	
	@Override
	public void deletedSessions(Integer c) {
		if(c == null) {
			DialogUtils.alert(this, getString(R.string.authentication_string_logout), getString(R.string.authentication_string_failed));
		} else {
			DialogUtils.alert(this, getString(R.string.authentication_string_logout), String.format(getString(R.string.authentication_signed_out), c));
		}
		refreshView();
	}
	

	
	@Override
	protected String screenName() {
		return "/authentication/status";
	}
	

	@Override
	public void networkErrorHappened() {
	}

	@Override
	public void shouldFinish() {
	}


}
