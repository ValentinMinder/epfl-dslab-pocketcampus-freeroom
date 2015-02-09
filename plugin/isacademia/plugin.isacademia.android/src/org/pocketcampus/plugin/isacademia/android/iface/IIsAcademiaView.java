package org.pocketcampus.plugin.isacademia.android.iface;

import org.pocketcampus.platform.android.core.IView;

/**
 * IIsAcademiaView
 * 
 * Interface for the Views of the IsAcademia plugin.
 * 
 * It contains the method that are called by the Model
 * when some data is updated, as well as the methods that
 * are called by the "HttpRequest" classes when some usual
 * behavior occurs.
 * 
 * @author Amer <amer.chamseddine@epfl.ch>
 *
 */
public interface IIsAcademiaView extends IView {
	
	/**
	 * Update display when we get data.
	 * Called from Model
	 * Called on ALL listeners
	 */
	void scheduleUpdated();
	
	/**
	 * Display errors and notices.
	 * Called from Request
	 * Called on the particular object that issued the request
	 */
	void networkErrorHappened();
	void networkErrorCacheExists();
	void isacademiaServersDown();
	void notLoggedIn();

	/**
	 * Authentication callbacks.
	 * Called FROM Controller on ALL listeners because we dunno who is instantiated 
	 */
	void authenticationFinished();
	void authenticationFailed();
	void userCancelledAuthentication();
	
}
