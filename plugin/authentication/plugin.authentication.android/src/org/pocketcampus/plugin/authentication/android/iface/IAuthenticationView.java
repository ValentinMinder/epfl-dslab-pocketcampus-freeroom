package org.pocketcampus.plugin.authentication.android.iface;

import org.pocketcampus.android.platform.sdk.core.IView;

public interface IAuthenticationView extends IView {
	void gotTequilaCookie();
	void gotTequilaKey();
	void gotAuthenticatedToken();
	void gotSessionId();
	
	void notifyBadCredentials();
	void notifyCookieTimedOut();
	void networkErrorHappened();
}

