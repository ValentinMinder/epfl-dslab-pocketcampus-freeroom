package org.pocketcampus.plugin.authentication.android.iface;

import org.pocketcampus.android.platform.sdk.core.IView;

public interface IAuthenticationView extends IView {
	void intStateUpdated();
	void authStateUpdated();
	
	void notifyBadCredentials();
	void notifyBadToken();
	void notifyUnexpectedErrorHappened();
	void networkErrorHappened();
}
