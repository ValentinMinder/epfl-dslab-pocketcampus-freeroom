package org.pocketcampus.plugin.news.android.iface;

import java.util.List;
import java.util.Map;

import org.pocketcampus.plugin.news.android.NewsItemWithImage;
import org.pocketcampus.plugin.news.shared.Feed;
import org.pocketcampus.plugin.news.shared.NewsItem;

import android.content.Context;

/**
 * Interface to the public methods of the News Model.
 * 
 * @author Elodie <elodienilane.triponez@epfl.ch>
 * 
 */
public interface INewsModel {

	/**
	 * Returns the list of all news items, all feeds put together.
	 * 
	 * @return the list of news items.
	 */
	public List<NewsItemWithImage> getNews(Context ctx);

	/**
	 * Update the list of NewsItems and notify the View.
	 * 
	 * @param list
	 *            the new list of Feeds.
	 */
	public void setNews(List<NewsItem> list);

	/**
	 * Returns a list of Feeds which contain NewsItems.
	 * 
	 * @return the list of Feeds.
	 */
	public List<Feed> getFeedsList();

	/**
	 * Update the list of Feeds and notify the View.
	 * 
	 * @param list
	 *            the new list of Feeds.
	 */
	public void setFeedsList(List<Feed> list);

	/**
	 * Returns a map of feed names with their Urls.
	 * 
	 * @return the map of feeds with their names.
	 */
	public Map<String, String> getFeedsUrls();

	/**
	 * Update the list of Feeds urls and names and notify the View.
	 * 
	 * @param map
	 *            the new map of urls and names.
	 */
	public void setFeedsUrls(Map<String, String> map);

	/**
	 * Called when an error has happened while updating the news.
	 */
	public void notifyNetworkError();

	/**
	 * Called when an error has happened while updating the feed names.
	 */
	public void notifyNetworkErrorFeedUrls();
	
	/**
	 * Called when the content of a news has been loaded.
	 */
	public void displayNewsContent(String content);
}
