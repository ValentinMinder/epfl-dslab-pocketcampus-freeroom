package org.pocketcampus.plugin.sunrise.android.iface;

import org.pocketcampus.android.platform.sdk.core.IView;

/**
 * ISunriseView
 * 
 * Interface for the Views of the Sunrise plugin.
 * 
 * It contains the method that are called by the Model
 * when some data is updated, as well as the methods that
 * are called by the "HttpRequest" classes when some usual
 * behavior occurs.
 * 
 * @author Amer <amer.chamseddine@epfl.ch>
 *
 */
public interface ISunriseView extends IView {
	
	/**
	 * Methods called by the "Request" classes.
	 */
	void networkErrorHappened();
	void serverErrorOccurred();
	void smsSent();
	void loginSucceeded();
	void badCredentials();
	void remainingFreeSmsUpdated();
	
}
