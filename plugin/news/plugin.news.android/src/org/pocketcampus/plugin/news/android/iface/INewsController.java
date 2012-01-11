package org.pocketcampus.plugin.news.android.iface;

import org.pocketcampus.plugin.news.android.NewsItemWithImage;

/**
 * Interface to the public methods of the News Controller.
 * 
 * @author Elodie <elodienilane.triponez@epfl.ch>
 * 
 */
public interface INewsController {

	/**
	 * Initiates a request to the server to get the news items.
	 */
	public void getNewsItems();

	/**
	 * Initiates a request to the server to get the feed urls and names.
	 */
	public void getFeedUrls();
	
	/**
	 * Initiates a request to the server to get the feed urls and names.
	 */
	public void getNewsContent(long itemId);
}
