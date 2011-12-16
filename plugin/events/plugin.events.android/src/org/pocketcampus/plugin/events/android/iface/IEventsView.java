package org.pocketcampus.plugin.events.android.iface;

import org.pocketcampus.android.platform.sdk.core.IView;

/**
 * Interface to the public methods of the EventsView
 * 
 * @author Elodie <elodienilane.triponez@epfl.ch>
 * 
 */
public interface IEventsView extends IView {

	/**
	 * Called when the list of events has been updated
	 */
	public void eventsUpdated();

	/**
	 * Called when the list of feed names and urls has been updated
	 */
	public void feedUrlsUpdated();
}
