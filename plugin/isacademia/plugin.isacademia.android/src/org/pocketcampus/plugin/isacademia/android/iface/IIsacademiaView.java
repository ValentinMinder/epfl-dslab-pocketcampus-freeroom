package org.pocketcampus.plugin.isacademia.android.iface;

import org.pocketcampus.android.platform.sdk.core.IView;

/**
 * IIsacademiaView
 * 
 * Interface for the Views of the Isacademia plugin.
 * 
 * It contains the method that are called by the Model
 * when some data is updated, as well as the methods that
 * are called by the "HttpRequest" classes when some unusual
 * behavior occurs.
 * 
 * @author Amer <amer.chamseddine@epfl.ch>
 *
 */
public interface IIsacademiaView extends IView {
	
	/**
	 * Update display when we get new data.
	 */
	void coursesUpdated();
	void examsUpdated();
	void scheduleUpdated();
	
	/**
	 * Authentication callbacks.
	 */
	void gotIsaCookie();
	void authenticationFailed();
	void userCancelledAuthentication();
	
	/**
	 * Display errors and notices.
	 */
	void networkErrorHappened();
	void isaServersDown();
	
}
