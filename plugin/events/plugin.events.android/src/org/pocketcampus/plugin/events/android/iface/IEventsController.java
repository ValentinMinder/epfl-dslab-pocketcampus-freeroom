package org.pocketcampus.plugin.events.android.iface;

/**
 * Interface to the public methods of the News Controller
 * 
 * @author Elodie <elodienilane.triponez@epfl.ch>
 * 
 */
public interface IEventsController {

	/**
	 * Initiates a request to the server to get the events items.
	 */
	public void getEventItems();

	/**
	 * Initiates a request to the server to get the feed urls and names.
	 */
	public void getFeedUrls();
}
