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
	 * Methods called by the Model.
	 */
	void coursesUpdated();
	void examsUpdated();
	void scheduleUpdated();
	
	/**
	 * Methods called by the "Request" classes.
	 */
	void networkErrorHappened();
	void isaServersDown();
	void notLoggedIn();
	
}
