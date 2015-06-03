package org.pocketcampus.plugin.news.android.iface;

import org.pocketcampus.platform.android.core.IView;

/**
 * INewsView
 * 
 * Interface for the Views of the News plugin.
 * 
 * It contains the method that are called by the Model
 * when some data is updated, as well as the methods that
 * are called by the "HttpRequest" classes when some unusual
 * behavior occurs.
 * 
 * @author Amer <amer.chamseddine@epfl.ch>
 *
 */
public interface INewsView extends IView {
	
	/**
	 * Update display when we get data.
	 * Called from Model
	 * Called on ALL listeners
	 */
	void gotFeeds();
	void gotContents();
	
	/**
	 * Display errors and notices.
	 * Called from Request
	 * Called on the particular object that issued the request
	 */
	void networkErrorHappened();
	void networkErrorCacheExists();
	void newsServersDown();
	
}
