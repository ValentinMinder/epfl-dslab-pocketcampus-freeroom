package org.pocketcampus.plugin.news.android;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.pocketcampus.android.platform.sdk.core.IView;
import org.pocketcampus.android.platform.sdk.core.PluginModel;
import org.pocketcampus.plugin.news.android.iface.INewsModel;
import org.pocketcampus.plugin.news.android.iface.INewsView;
import org.pocketcampus.plugin.news.shared.Feed;
import org.pocketcampus.plugin.news.shared.NewsItem;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

/**
 * 
 * @author Elodie <elodienilane.triponez@epfl.ch>
 * 
 */
public class NewsModel extends PluginModel implements INewsModel {
	/** Listeners for the state of the view */
	INewsView mListeners = (INewsView) getListeners();

	/** List of news items to display. */
	private List<NewsItemWithImage> mNewsItems;

	/** Access to the preferences */
	private SharedPreferences mPreferences;

	/** The map of feed names with their Urls */
	private HashMap<String, String> mFeedUrls;

	/** List of Feeds to display */
	private List<Feed> mNewsFeeds;

	@Override
	protected Class<? extends IView> getViewInterface() {
		return INewsView.class;
	}

	public void setNews(List<NewsItem> newsItems) {
		if (newsItems != null) {
			if (mNewsItems == null) {
				mNewsItems = new ArrayList<NewsItemWithImage>();
			}
			for (NewsItem ni : newsItems) {
				NewsItemWithImage newsItem = new NewsItemWithImage(ni);
				mNewsItems.add(newsItem);
			}
			mListeners.newsUpdated();
		}
	}

	@Override
	public List<NewsItemWithImage> getNews(Context ctx) {
		if (mPreferences == null) {
			mPreferences = ctx.getSharedPreferences(
					NewsPreferencesView.NEWS_PREFS_NAME, 0);
		}

		if (mNewsItems == null) {
			return null;
		}

		ArrayList<NewsItemWithImage> filteredList = new ArrayList<NewsItemWithImage>();
		for (NewsItemWithImage newsItem : mNewsItems) {
			if (mPreferences.getBoolean(newsItem.getNewsItem().getFeed(), true)) {
				if (!alreadyContains(filteredList, newsItem)) {
					filteredList.add(newsItem);
				}
			}
		}

		return filteredList;
	}

	private boolean alreadyContains(List<NewsItemWithImage> filteredList,
			NewsItemWithImage newsItemWithImage) {
		for (NewsItemWithImage ni : filteredList) {
			if (ni.getNewsItem().getTitle()
					.equals(newsItemWithImage.getNewsItem().getTitle())
					|| ni.getNewsItem().getLink()
							.equals(newsItemWithImage.getNewsItem().getLink())) {
				return true;
			}
		}
		return false;
	}

	@Override
	public List<Feed> getFeedsList() {
		return mNewsFeeds;
	}

	@Override
	public void setFeedsList(List<Feed> list) {
		if (list != null) {
			mNewsFeeds = list;

			if (mNewsItems == null) {
				mNewsItems = new ArrayList<NewsItemWithImage>();
			}

			for (Feed f : mNewsFeeds) {
				List<NewsItem> feedItems = f.getItems();
				for (NewsItem ni : feedItems) {
					mNewsItems.add(new NewsItemWithImage(ni));
				}
			}
			mListeners.newsUpdated();
		}
	}

	@Override
	public Map<String, String> getFeedsUrls() {
		return mFeedUrls;
	}

	@Override
	public void setFeedsUrls(Map<String, String> map) {
		if (map != null) {
			mFeedUrls = new HashMap<String, String>();
			Iterator<Entry<String, String>> entries = map.entrySet().iterator();
			while (entries.hasNext()) {
				Entry<String, String> thisEntry = (Entry<String, String>) entries
						.next();
				String key = (String) thisEntry.getKey();
				String value = (String) thisEntry.getValue();
				mFeedUrls.put(key, value);
			}
		} else {
			Log.d("NEWSMODEL", "Null map");
		}
		mListeners.feedUrlsUpdated();
	}

	@Override
	public void displayNewsContent(String content) {
		mListeners.newsContentLoaded(content);
	}

	@Override
	public void notifyNetworkErrorFeedUrls() {
		System.out.println("NETWORK ERROR");
	}
}
