package org.pocketcampus.plugin.authentication.android;

import org.pocketcampus.android.platform.sdk.core.IView;
import org.pocketcampus.android.platform.sdk.core.PluginModel;
import org.pocketcampus.plugin.authentication.android.iface.IAuthenticationModel;
import org.pocketcampus.plugin.authentication.android.iface.IAuthenticationView;
import org.pocketcampus.plugin.authentication.shared.SessionId;
import org.pocketcampus.plugin.authentication.shared.TequilaKey;
import org.pocketcampus.plugin.authentication.shared.TypeOfService;

public class AuthenticationModel extends PluginModel implements IAuthenticationModel {
	IAuthenticationView mListeners = (IAuthenticationView) getListeners();
	
	private AuthenticationModel() {
	}
	public static AuthenticationModel getInstance(){
		if(self == null)
			self = new AuthenticationModel();
		return self;
	}
	
	
	

	public TypeOfService getTypeOfService() {
		return iTypeOfService;
	}
	public void setTypeOfService(TypeOfService value) {
		iTypeOfService = value;
	}

	public TequilaKey getTequilaKey() {
		return iTequilaKey;
	}
	public void setTequilaKey(TequilaKey value) {
		iTequilaKey = value;
	}

	public SessionId getSessionId() {
		return iSessionId;
	}
	public void setSessionId(SessionId value) {
		iSessionId = value;
	}

	
	
	
	public void setTequilaCookie(String value) {
		tequilaCookie = value;
	}
	public String getTequilaCookie() {
		return tequilaCookie;
	}
	public void setAuthState(int value) {
		authState = value;
		mListeners.authStateUpdated();
	}
	public int getAuthState() {
		return authState;
	}
	public void setIntState(int value) {
		intState = value;
		mListeners.intStateUpdated();
	}
	public int getIntState() {
		return intState;
	}

	
	
	@Override
	protected Class<? extends IView> getViewInterface() {
		return IAuthenticationView.class;
	}
	public IAuthenticationView getListenersToNotify() {
		return mListeners;
	}
	
	
	
	private TypeOfService iTypeOfService;
	private TequilaKey iTequilaKey;
	private SessionId iSessionId;
	
	private String tequilaCookie; // this is the only thing we need to store
	private int authState = 0; // plugin authentication state
	private int intState = 0; // internal state

	private static AuthenticationModel self = null;

}
