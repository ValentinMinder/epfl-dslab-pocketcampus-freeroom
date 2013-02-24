package org.pocketcampus.plugin.events.android.iface;

import java.util.List;

import org.pocketcampus.android.platform.sdk.core.IView;

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
	 */
	void eventItemsUpdated(List<Long> ids);
	void eventPoolsUpdated(List<Long> ids);
	
	/**
	 * Display errors and notices.
	 */
	void networkErrorHappened();
	void networkErrorCacheExists();
	void mementoServersDown();
	void identificationRequired();
	
}
