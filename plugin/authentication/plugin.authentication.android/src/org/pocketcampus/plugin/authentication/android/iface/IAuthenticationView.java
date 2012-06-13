package org.pocketcampus.plugin.authentication.android.iface;

import org.pocketcampus.android.platform.sdk.core.IView;

/**
 * IAuthenticationView
 * 
 * Interface for the Views of the Authentication plugin.
 * 
 * It contains the method that are called by the Model
 * when some data is updated, as well as the methods that
 * are called by the "HttpRequest" classes when some usual
 * behavior occurs.
 * 
 * @author Amer <amer.chamseddine@epfl.ch>
 *
 */
public interface IAuthenticationView extends IView {
	
	/**
	 * Methods called from the Model when data is updated.
	 */
	/*void gotTequilaCookie();
	void gotTequilaKey();
	void gotSessionId();*/
	
	/**
	 * Methods called from the "Request" classes
	 * when something unusual happens.
	 */
	/*void doneAuthenticatingToken();
	void doneAuthenticatingSecToken();
	void notifyBadCredentials();
	void notifyCookieTimedOut();
	void networkErrorHappened();*/
	
}
