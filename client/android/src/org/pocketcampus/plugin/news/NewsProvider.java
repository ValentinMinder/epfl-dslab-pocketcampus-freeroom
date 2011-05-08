package org.pocketcampus.plugin.news;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.StreamCorruptedException;
import java.util.ArrayList;
import java.util.List;

import org.pocketcampus.R;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.util.Log;

/**
 * Provider that is used both by the NewsAdapter and by the NewsInfo which provides news to the core platform.
 * This is a singleton that can be used from multiple part of the app.
 * The provider can notify that the news have been updated by uing the {@link INewsListener} interface.
 * 
 * @status working on it
 * 
 * @author Jonas
 *
 */
public class NewsProvider {

	private Context context_;
	private SharedPreferences prefs_;
	
	// Singleton
	private static NewsProvider instance_;
	
	private FeedDownloader downloader_;
	
	private List<INewsListener> newsListeners_;

	// Data used to cache the feeds 
	private final static String CACHE_FILENAME = "newscache.dat";
	private long refreshRate_;
	private String defaultRefreshRate_;

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
		this.newsListeners_ = new ArrayList<INewsListener>();
		
		prefs_ = PreferenceManager.getDefaultSharedPreferences(context);
		defaultRefreshRate_ = context.getResources().getStringArray(R.array.news_refresh_values)[context.getResources().getInteger(R.integer.news_default_refresh)];
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
	 * Add a listener
	 * @param listener
	 */
	public void addNewsListener(INewsListener listener) {
		newsListeners_.add(listener);
	}
	
	/**
	 * Remove a listener
	 * @param listener
	 */
	public void removeNewsListener(INewsListener listener) {
		newsListeners_.remove(listener);
	}
	

	/**
	 * Checks if the feeds must be redownloaded
	 */
	public void refreshIfNeeded() {

		// Check if the cache is ok, and get data from the file
		// otherwise download the news
		if(cacheTooOld()) {
			Log.d(this.getClass().toString(), "Cache too old");
			forceRefresh();
		} else {
			Log.d(this.getClass().toString(), "Do not need to download news file");
			loadNewsFromFile();
		}

	}	

	/** 
	 * Refresh the feeds (and handles the cache)
	 */
	public void forceRefresh() {
		
		for (INewsListener listener : newsListeners_) {
			listener.newsRefreshing();
		}
		
		downloadFeeds();
		prefs_.edit().putLong(NewsPreference.cacheTime, System.currentTimeMillis()).commit();

		Log.d(this.getClass().toString(), "Redownload news feeds");
	}

	/**
	 * Tells that the items changed
	 */
	protected void dataSetUpdated() {
		for (INewsListener listener : newsListeners_) {
			listener.newsRefreshed();
		}
		
		this.saveNewsToFile();
	}

	/**
	 * Download the feeds using the @see FeedDownloader
	 */
	private void downloadFeeds() {
		// Get the available feeds
		String[] urls = context_.getResources().getStringArray(R.array.news_feeds_url);

		ArrayList<String> urlsToDownload = new ArrayList<String>();

		// Take only the ones selected by the user in the preferences
		boolean useIt = true;
		for(String url: urls) {
			if(prefs_.getBoolean(NewsPreference.loadRss + url, useIt)) {
				urlsToDownload.add(url);
			}
			useIt = false;
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
			FileInputStream fis = context_.openFileInput(CACHE_FILENAME);
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
			fos = context_.openFileOutput(CACHE_FILENAME, Context.MODE_PRIVATE);
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
	 * Tells if the cache is too old (and if we have to redownload the feeds)
	 * @return Too old or not
	 */
	private boolean cacheTooOld() {
		
		refreshRate_ = Integer.parseInt(prefs_.getString("news_refresh_rate", defaultRefreshRate_));

		Log.d(this.getClass().toString(), "Last cached: " + prefs_.getLong(NewsPreference.cacheTime, 0));
		Log.d(this.getClass().toString(), "Current time: " + System.currentTimeMillis());
		Log.d(this.getClass().toString(), "Refresh rate: " + refreshRate_);
		
		return prefs_.getLong(NewsPreference.cacheTime, 0) + refreshRate_ < System.currentTimeMillis();
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

	protected void addAll(List<NewsItem> items) {
		if(items_ != null && items != null)
			items_.addAll(items);
	}
	
	
}
