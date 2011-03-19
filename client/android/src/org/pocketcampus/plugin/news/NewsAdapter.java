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
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.TextView;

public class NewsAdapter extends BaseAdapter {

	private List<NewsItem> items_;

	// The first news is selected by default
	private int selectedItem_ = 0;

	private final static String filename_ = "newscache.dat";
	private final static String preferenceCacheKey_ = "news.cache";
	private static final long refreshRate = 60000;

	private LayoutInflater mInflater_;
	private FeedDownloader downloader_;

	private Context context_;
	SharedPreferences prefs_;


	public NewsAdapter(Context context, int textViewResourceId, List<NewsItem> items) {
		super();
		this.context_ = context;

		this.items_ = new ArrayList<NewsItem>(items);

		mInflater_ = LayoutInflater.from(context);
		prefs_ = PreferenceManager.getDefaultSharedPreferences(context);
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		View v = convertView;
		if (v == null) {
			v = mInflater_.inflate(R.layout.news_newsentry, null);
		}

		final NewsItem newsItem = getItem(position);

		if (newsItem != null) {
			TextView tv;

			tv = (TextView) v.findViewById(R.id.news_item_title);
			tv.setText(newsItem.getTitle());

			tv = (TextView) v.findViewById(R.id.news_item_description);
			tv.setText(newsItem.getDescription());
			tv.setMaxLines(selectedItem_ == position ? 20 : 2);

			LoaderNewsImageView liv = (LoaderNewsImageView) v.findViewById(R.id.news_item_image);
			liv.setNewItem(newsItem);

		}

		return v;

	}

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

	public void setClickedItem(AdapterView<?> parent, View view, int position, long id) {
		selectedItem_ = position;

		this.notifyDataSetChanged();
	}

	protected void dataSetChanged() {
		this.sortNews();
		this.notifyDataSetChanged();
		this.saveNewsToFile();
	}

	private void sortNews() {
		Collections.sort(items_);
	}

	private void downloadFeeds() {
		String[] urls = context_.getResources().getStringArray(R.array.news_feeds_url);

		ArrayList<String> urlsToDownload = new ArrayList<String>();

		for(String url: urls) {
			if(prefs_.getBoolean("load_rss" + url, true)) {
				urlsToDownload.add(url);
			}
		}

		downloader_ = new FeedDownloader(this);
		downloader_.execute(urlsToDownload.toArray(new String[0]));
	}

	@SuppressWarnings("unchecked")
	private boolean loadNewsFromFile() {
		try {
			FileInputStream fis = context_.openFileInput(filename_);
			ObjectInputStream in = new ObjectInputStream(fis);

			Object o = in.readObject();

			if(o instanceof ArrayList<?>) {
				items_ = (ArrayList<NewsItem>) o;

				Log.d(this.getClass().toString(), "Got news from file");

				return true;
			}

			// We will return false
		} catch (FileNotFoundException e) {
		} catch (StreamCorruptedException e) {
		} catch (IOException e) {
		} catch (ClassNotFoundException e) {
		}

		Log.d(this.getClass().toString(), "Could not get news from file");

		return false;
	}

	private boolean saveNewsToFile() {

		Log.d(this.getClass().toString(), "Saving news to file");

		FileOutputStream fos;
		try {
			fos = context_.openFileOutput(filename_, Context.MODE_PRIVATE);
			ObjectOutputStream out = new ObjectOutputStream(fos);
			out.writeObject(items_);
			fos.close();

			Log.d(this.getClass().toString(), "Saved news to file");

			return true;

		} catch (FileNotFoundException e) {
		} catch (IOException e) {
		}

		Log.d(this.getClass().toString(), "Could not save news to file");

		return false;
	}

	private void refresh() {
		downloadFeeds();
		prefs_.edit().putLong(preferenceCacheKey_, System.currentTimeMillis()).commit();

		Log.d(this.getClass().toString(), "Redownload news feeds");
	}

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
