package org.pocketcampus.plugin.news;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.StreamCorruptedException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.pocketcampus.R;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.util.Log;

/**
 * Provider that is used both by the NewsAdapter and by the NewsInfo which provides news to the core platform.
 * This is a singleton that can be used from multiple part of the app.
 * 
 * @status working on it
 * 
 * @author Jonas
 *
 */
public class NewsProvider {

	private Context context_;
	SharedPreferences prefs_;
	
	// Signleton
	private static NewsProvider instance_;
	
	
	private FeedDownloader downloader_;
	private NewsAdapter newsAdapter_;


	// Data used to cache the feeds 
	private final static String cacheFilename_ = "newscache.dat";
	private final static String preferenceCacheKey_ = "news_cache_time";
	private long refreshRate_;

	// Items  from the feeds (merged)
	private List<NewsItem> items_;
	
	/**
	 * Create a NewsProvider with an empty list of news.
	 * Call refreshIfNeeded to load the news.
	 * @param context
	 */
	private NewsProvider(Context context) {
		this.context_ = context;
		
		this.items_ = new ArrayList<NewsItem>();
		
		prefs_ = PreferenceManager.getDefaultSharedPreferences(context);
		
		String defaultRefresh = context.getResources().getStringArray(R.array.news_refresh_values)[context.getResources().getInteger(R.integer.news_default_refresh)];
		refreshRate_ = Integer.parseInt(prefs_.getString("news_refresh_rate", defaultRefresh));
	}
	
	/**
	 * Get the singleton instance of the news provider
	 * @context The context that is used when creating the singleton
	 */
	public static NewsProvider getInstance(Context context) {
		if(instance_ == null) {
			instance_ = new NewsProvider(context);
		}
		
		return instance_;
	}

	/**
	 * Set the adapter that is going to be called when the list is refreshed.
	 * @param adapter adapter to call
	 */
	public void setAdapter(NewsAdapter adapter) {
		newsAdapter_ = adapter;
	}
	

	/**
	 * Checks if the feeds must be redownloaded
	 */
	public void refreshIfNeeded() {

		// Check if the cache is ok, and get data from the file
		// otherwise download the news
		if(cacheTooOld()) {
			Log.d(this.getClass().toString(), "Cache too old");
			refresh();
		} else {
			Log.d(this.getClass().toString(), "Do not need to download news file");
			loadNewsFromFile();
		}

	}

	/**
	 * Tells that the items changed
	 */
	protected void dataSetChanged() {
		this.sortNews();
		
		if(newsAdapter_ != null) {
			newsAdapter_.notifyDataSetChanged();
		}
		
		this.saveNewsToFile();
	}
	

	private void sortNews() {
		Collections.sort(items_);
	}

	/**
	 * Download the feeds using the @see FeedDownloader
	 */
	private void downloadFeeds() {
		// Get the available feeds
		String[] urls = context_.getResources().getStringArray(R.array.news_feeds_url);

		ArrayList<String> urlsToDownload = new ArrayList<String>();

		// Take only the one selected by the user in the preferences
		for(String url: urls) {
			if(prefs_.getBoolean("load_rss" + url, true)) {
				urlsToDownload.add(url);
			}
		}

		// Download the feeds
		downloader_ = new FeedDownloader(this);
		downloader_.execute(urlsToDownload.toArray(new String[0]));
	}

	/**
	 * Tries to load the feeds data from a cache file
	 * @return Whether it worked or not
	 */
	@SuppressWarnings("unchecked")
	private boolean loadNewsFromFile() {
		try {
			FileInputStream fis = context_.openFileInput(cacheFilename_);
			ObjectInputStream in = new ObjectInputStream(fis);

			Object o = in.readObject();

			if(o instanceof ArrayList<?>) {
				items_ = (ArrayList<NewsItem>) o;

				Log.d(this.getClass().toString(), "Got news from file");

				return true;
			}

			// We will return false
		}
		catch (FileNotFoundException e) {}
		catch (StreamCorruptedException e) {}
		catch (IOException e) {}
		catch (ClassNotFoundException e) {}

		Log.d(this.getClass().toString(), "Could not get news from file");

		return false;
	}

	/**
	 * Tries to save the feeds data into a cache file
	 * @return
	 */
	private boolean saveNewsToFile() {

		Log.d(this.getClass().toString(), "Saving news to file");

		FileOutputStream fos;
		try {
			fos = context_.openFileOutput(cacheFilename_, Context.MODE_PRIVATE);
			ObjectOutputStream out = new ObjectOutputStream(fos);
			out.writeObject(items_);
			fos.close();

			Log.d(this.getClass().toString(), "Saved news to file");

			return true;

		}
		catch (FileNotFoundException e) {}
		catch (IOException e) {}

		Log.d(this.getClass().toString(), "Could not save news to file");

		return false;
	}
	

	/** 
	 * Refresh the feeds (and handles the cache)
	 */
	private void refresh() {
		downloadFeeds();
		prefs_.edit().putLong(preferenceCacheKey_, System.currentTimeMillis()).commit();

		Log.d(this.getClass().toString(), "Redownload news feeds");
	}

	/**
	 * Tells if the cache is too old (and if we have to redownload the feeds)
	 * @return Too old or not
	 */
	private boolean cacheTooOld() {

		Log.d(this.getClass().toString(), "Last cached: " + prefs_.getLong(preferenceCacheKey_, 0));

		return prefs_.getLong(preferenceCacheKey_, 0) + refreshRate_ < System.currentTimeMillis();
	}
	

	public int getCount() {
		return items_.size();
	}

	public NewsItem getItem(int position) {
		return items_.get(position);
	}

	public long getItemId(int position) {
		return position;
	}

	protected void clear() {
		items_.clear();
	}

	protected void add(NewsItem item) {
		items_.add(item);
	}
	
	
}
