package org.pocketcampus.plugin.news.android.iface;

import java.util.List;

import org.pocketcampus.plugin.news.shared.Feed;
import org.pocketcampus.plugin.news.shared.NewsItem;

/**
 * Interface to the public methods of the News Model
 * 
 * @author Elodie <elodienilane.triponez@epfl.ch>
 * 
 */
public interface INewsModel {

	/**
	 * Returns the list of all news items, all feeds put together
	 * 
	 * @return the list of news items
	 */
	public List<NewsItem> getNews();

	/**
	 * Update the list of NewsItems and notify the View
	 * 
	 * @param list
	 *            the new list of Feeds
	 */
	public void setNews(List<NewsItem> list);

	/**
	 * Returns a list of Feeds which contain NewsItems
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
}
