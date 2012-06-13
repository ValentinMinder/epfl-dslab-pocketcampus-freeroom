package org.pocketcampus.plugin.moodle.android.iface;

import java.io.File;

import org.pocketcampus.android.platform.sdk.core.IView;

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
	 * Methods called by the Model.
	 */
	void coursesListUpdated();
	void eventsListUpdated();
	void sectionsListUpdated();
	void moodleCookieUpdated();
	void tequilaTokenUpdated();
	
	void tokenAuthenticationFinished();
	
	/**
	 * Methods called by the "Request" classes.
	 */
	void networkErrorHappened();
	void moodleServersDown();
	void notLoggedIn();
	void downloadComplete(File localFile);
	
}
