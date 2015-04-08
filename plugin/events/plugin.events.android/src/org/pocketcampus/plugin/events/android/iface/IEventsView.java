package org.pocketcampus.plugin.events.android.iface;

import java.util.List;

import org.pocketcampus.platform.android.core.IView;

/**
 * IEventsView
 * 
 * Interface for the Views of the Events plugin.
 * 
 * It contains the method that are called by the Model
 * when some data is updated, as well as the methods that
 * are called by the "HttpRequest" classes when some usual
 * behavior occurs.
 * 
 * @author Amer <amer.chamseddine@epfl.ch>
 *
 */
public interface IEventsView extends IView {
	
	/**
	 * Update display when we get data.
	 * Called from Model
	 * Called on ALL listeners
	 */
	void eventItemsUpdated(List<Long> ids);
	void eventPoolsUpdated(List<Long> ids);
	
	/**
	 * Display errors and notices.
	 * Called from Request
	 * Called on the particular object that issued the request
	 */
	void networkErrorHappened();
	void networkErrorCacheExists();
	void mementoServersDown();
	void exchangeContactsFinished(boolean success);
	void sendEmailRequestFinished(boolean success);
	void sendAdminRegEmailFinished(boolean success);
	void showLoading();
	void hideLoading();
	
}
