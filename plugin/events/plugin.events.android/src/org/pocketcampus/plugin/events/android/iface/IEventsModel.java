package org.pocketcampus.plugin.events.android.iface;

import java.util.List;
import java.util.Map;

import org.pocketcampus.plugin.events.android.EventsItemWithImage;
import org.pocketcampus.plugin.events.shared.EventsItem;
import org.pocketcampus.plugin.events.shared.Feed;

import android.content.Context;

/**
 * Interface to the public methods of the events Model
 * 
 * @author Elodie <elodienilane.triponez@epfl.ch>
 * 
 */
public interface IEventsModel {

	/**
	 * Returns the list of all events items, all feeds put together
	 * 
	 * @return the list of events items
	 */
	public List<EventsItemWithImage> getEvents(Context ctx);

	/**
	 * Update the list of EventsItems and notify the View
	 * 
	 * @param list
	 *            the new list of Feeds
	 */
	public void setEvents(List<EventsItem> list);

	/**
	 * Returns a list of Feeds which contain EventsItems
	 * 
	 * @return the list of Feeds
	 */
	public List<Feed> getFeedsList();

	/**
	 * Update the list of Feeds and notify the View
	 * 
	 * @param list
	 *            the new list of Feeds
	 */
	public void setFeedsList(List<Feed> list);

	/**
	 * Returns a map of feed names with their Urls
	 * 
	 * @return the map of feeds with their names
	 */
	public Map<String, String> getFeedsUrls();

	/**
	 * Update the list of Feeds urls and names and notify the View
	 * 
	 * @param map
	 *            the new map of urls and names
	 */
	public void setFeedsUrls(Map<String, String> map);

	/**
	 * Called when an error has happened while updating the events
	 */
	public void notifyNetworkError();
	
	/**
	 * Called when an error has happened while updating the feed names
	 */
	public void notifyNetworkErrorFeedUrls();
}
