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
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.TextView;

/**
 * Adapter used by the ListView to display the feeds
 * 
 * @status complete
 * 
 * @author Jonas
 *
 */
public class NewsAdapter extends BaseAdapter {

	// Items  from the feeds (merged)
	private List<NewsItem> items_;

	// Selected item, will be shown bigger than the others
	private int selectedItem_ = -1;

	// Data used to cache the feeds 
	private final static String cacheFilename_ = "newscache.dat";
	private final static String preferenceCacheKey_ = "news_cache_time";
	private long refreshRate;

	// Misc
	private LayoutInflater mInflater_;
	private FeedDownloader downloader_;
	private Context context_;
	SharedPreferences prefs_;

	/**
	 * Adapter constructor
	 * @param context Context of the application
	 * @param items Items that have to be on the list
	 */
	public NewsAdapter(Context context, List<NewsItem> items) {
		super();
		this.context_ = context;

		this.items_ = new ArrayList<NewsItem>(items);

		mInflater_ = LayoutInflater.from(context);
		prefs_ = PreferenceManager.getDefaultSharedPreferences(context);
		
		refreshRate = Integer.parseInt(prefs_.getString("news_refresh_rate", "0"));
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		View v = convertView;
		if (v == null) {
			v = mInflater_.inflate(R.layout.news_newsentry, null);
		}

		// The item to display
		final NewsItem newsItem = getItem(position);

		if (newsItem != null) {
			TextView tv;

			tv = (TextView) v.findViewById(R.id.news_item_title);
			tv.setText(newsItem.getTitle());

			tv = (TextView) v.findViewById(R.id.news_item_description);
			tv.setText(newsItem.getDescriptionNoHtml());
			tv.setMaxLines(selectedItem_ == position ? 20 : 2); // Bigger if the item is selected

			LoaderNewsImageView liv = (LoaderNewsImageView) v.findViewById(R.id.news_item_image);
			liv.setNewItem(newsItem);

			// "View more" button, shown only on the selected item
			Button b = (Button) v.findViewById(R.id.news_view_more);
			
			if(selectedItem_ == position) {
				b.setVisibility(View.VISIBLE);

				b.setOnClickListener(new OnClickListener() {
					@Override
					public void onClick(View v) {
						Intent i = new Intent(Intent.ACTION_VIEW, Uri.parse(newsItem.getLink()));
						context_.startActivity(i);
					}
				});

			} else {
				b.setVisibility(View.GONE);
			}

		}

		return v;

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
	 * Sets the item that has been selected, to show it bigger
	 * @param parent 
	 * @param view
	 * @param position
	 * @param id
	 */
	public void setClickedItem(AdapterView<?> parent, View view, int position, long id) {
		selectedItem_ = position;

		// Recompute the view
		this.notifyDataSetChanged();
	}

	/**
	 * Tells that the items changed
	 */
	protected void dataSetChanged() {
		this.sortNews();
		this.notifyDataSetChanged();
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

		return prefs_.getLong(preferenceCacheKey_, 0) + refreshRate < System.currentTimeMillis();
	}

	@Override
	public int getCount() {
		return items_.size();
	}

	@Override
	public NewsItem getItem(int position) {
		return items_.get(position);
	}

	@Override
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
