package org.pocketcampus.plugin.authentication.android;

import java.util.HashMap;

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
	
	public void setTequilaKey(TequilaKey value) {
		iTequilaKey = value;
		mListeners.gotTequilaKey();
	}
	
	public void setSessionIdForService(TypeOfService tos, SessionId sessId) {
		if(sessionIds.containsKey(tos)) {
			sessionIds.remove(tos);
		}
		sessionIds.put(tos,  sessId);
	}
	
	public void setMustFinish() {
		mListeners.mustFinish();
	}
	

	@Override
	protected Class<? extends IView> getViewInterface() {
		return IAuthenticationView.class;
	}

	@Override
	public TequilaKey getTequilaKey() {
		return iTequilaKey;
	}

	@Override
	public SessionId getSessionIdForService(TypeOfService tos) {
		return sessionIds.get(tos);
	}

	@Override
	public String getSessionIds() {
		return sessionIds.toString();
	}
	
	// TODO store these in persistent storage, because on rotate the model is destroyed

	private HashMap<TypeOfService, SessionId> sessionIds = new HashMap<TypeOfService, SessionId>();
	
	private TequilaKey iTequilaKey;

	private static AuthenticationModel self = null;

}
