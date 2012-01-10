package org.pocketcampus.plugin.authentication.android;

import org.pocketcampus.android.platform.sdk.core.IView;
import org.pocketcampus.android.platform.sdk.core.PluginModel;
import org.pocketcampus.plugin.authentication.android.iface.IAuthenticationModel;
import org.pocketcampus.plugin.authentication.android.iface.IAuthenticationView;
import org.pocketcampus.plugin.authentication.shared.SessionId;
import org.pocketcampus.plugin.authentication.shared.TequilaKey;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;

public class AuthenticationModel extends PluginModel implements IAuthenticationModel {
	IAuthenticationView mListeners = (IAuthenticationView) getListeners();
	
	public AuthenticationModel(Context context) {
		iStorage = context.getSharedPreferences(AUTH_STORAGE_NAME, 0);
		tequilaCookie = iStorage.getString(TEQUILA_COOKIE_KEY, null);
	}
	

	public TequilaKey getTequilaKey() {
		return iTequilaKey;
	}
	public void setTequilaKey(TequilaKey value) {
		iTequilaKey = value;
		mListeners.gotTequilaKey();
	}

	public String getAuthenticatedToken() {
		return iAuthenticatedToken;
	}
	public void setAuthenticatedToken(String value) {
		iAuthenticatedToken = value;
		mListeners.gotAuthenticatedToken();
	}

	public SessionId getSessionId() {
		return iSessionId;
	}
	public void setSessionId(SessionId value) {
		iSessionId = value;
		mListeners.gotSessionId();
	}

	
	
	
	public void setTequilaCookie(String value) {
		tequilaCookie = value;
		Editor editor = iStorage.edit();
		editor.putString(TEQUILA_COOKIE_KEY, tequilaCookie);
		editor.commit();
		mListeners.gotTequilaCookie();
	}
	public String getTequilaCookie() {
		return tequilaCookie;
	}

	
	
	@Override
	protected Class<? extends IView> getViewInterface() {
		return IAuthenticationView.class;
	}
	public IAuthenticationView getListenersToNotify() {
		return mListeners;
	}
	
	
	
	private TequilaKey iTequilaKey;
	private SessionId iSessionId;
	private String iAuthenticatedToken;
	
	private String tequilaCookie; // this is the only thing we need to store

	///////////

	private SharedPreferences iStorage;
	private static final String AUTH_STORAGE_NAME = "AUTH_STORAGE_NAME";
	private static final String TEQUILA_COOKIE_KEY = "TEQUILA_COOKIE_KEY";

}
