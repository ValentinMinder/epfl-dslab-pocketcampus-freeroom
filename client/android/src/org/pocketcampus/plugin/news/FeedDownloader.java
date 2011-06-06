package org.pocketcampus.plugin.news;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import android.os.AsyncTask;

/**
 * Downloads RSS feeds and fills a news provider
 * 
 * @status complete
 * 
 * @author Jonas
 * 
 * @see org.pocketcampus.plugin.news.NewsItem
 * @see org.pocketcampus.plugin.news.Feed
 *
 */
public class FeedDownloader extends AsyncTask<String, Void, List<NewsItem>> {
	
	// The NewsProvider to fill
	private NewsProvider newsProvider_;
	
	/**
	 * Constructor
	 * @param adapter The adapter to fill
	 */
	public FeedDownloader(NewsProvider adapter) {
		this.newsProvider_ = adapter;
	}

	@Override
	protected List<NewsItem> doInBackground(String... params) {
		
		// List of items in the feeds
		List<NewsItem> list = new ArrayList<NewsItem>();
		
		// There is no feed to download
		if(params.length < 1) {
			return null;
		}
		
		// Create a parser for each feed and put the items into the list
		RssParser parser;
		Feed feed;
		for(String feedUrl : params) {
			parser = new RssParser(feedUrl);
			
			parser.parse();
			feed = parser.getFeed();
			
			if(feed != null) {
				list.addAll(feed.getItems());
			}
		}
		
		// Sort the news (done asynchronously)
		Collections.sort(list);
		
		return list;
	}
	
	@Override
	protected void onPostExecute(List<NewsItem> result) {
		// Empty the list and put the new items 
		newsProvider_.clear();
		
		newsProvider_.addAll(result);
		
		// Notify that the data changed
		newsProvider_.dataSetUpdated();
		
	}


}
