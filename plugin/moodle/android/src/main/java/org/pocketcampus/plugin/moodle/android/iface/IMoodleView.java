package org.pocketcampus.plugin.moodle.android.iface;

import java.io.File;

import org.pocketcampus.platform.android.core.IView;

/**
 * IMoodleView
 * 
 * Interface for the Views of the Moodle plugin.
 * 
 * It contains the method that are called by the Model
 * when some data is updated, as well as the methods that
 * are called by the "HttpRequest" classes when some usual
 * behavior occurs.
 * 
 * @author Amer <amer.chamseddine@epfl.ch>
 *
 */
public interface IMoodleView extends IView {
	
	/**
	 * Update display when we get data.
	 * Called from Model
	 * Called on ALL listeners
	 */
	void coursesListUpdated();
	void sectionsListUpdated();
	
	/**
	 * Display errors and notices.
	 * Called from Request
	 * Called on the particular object that issued the request
	 */
	void networkErrorHappened();
	void networkErrorCacheExists();
	void moodleServersDown();
	void notLoggedIn();
	void downloadComplete(File localFile);	
	void showLoading();
	void hideLoading();

	/**
	 * Authentication callbacks.
	 * Called FROM Controller on ALL listeners because we dunno who is instantiated 
	 */
	void authenticationFinished();
	void authenticationFailed();
	void userCancelledAuthentication();
	
	/**
	 * Trigger a display update.
	 * called from the dialog (after user deletes a file)
	 */
	void updateDisplay();
}
