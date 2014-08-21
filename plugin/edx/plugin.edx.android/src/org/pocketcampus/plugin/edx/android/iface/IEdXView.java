package org.pocketcampus.plugin.edx.android.iface;

import org.pocketcampus.platform.android.core.IView;

/**
 * IEdXView
 * 
 * Interface for the Views of the EdX plugin.
 * 
 * It contains the method that are called by the Model
 * when some data is updated, as well as the methods that
 * are called by the "HttpRequest" classes when some usual
 * behavior occurs.
 * 
 * @author Amer <amer.chamseddine@epfl.ch>
 *
 */
public interface IEdXView extends IView {

	/**
	 * Update display when we get data.
	 * Called from Model
	 * Called on ALL listeners
	 */
	void userCoursesUpdated();
	void courseSectionsUpdated();
	void moduleDetailsUpdated();
	void activeRoomsUpdated();
	
	void userCredentialsUpdated();
	
	/**
	 * Display errors and notices.
	 * Called from Request
	 * Called on the particular object that issued the request
	 */
	void networkErrorHappened();
	void networkErrorCacheExists();
	void upstreamServerFailure();
	void serverFailure();
	
	void loginSucceeded();
	void loginFailed();
	void sessionTimedOut();
	
}
