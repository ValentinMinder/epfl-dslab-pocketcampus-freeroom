package org.pocketcampus.plugin.news.android;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.pocketcampus.android.platform.sdk.core.IView;
import org.pocketcampus.android.platform.sdk.core.PluginModel;
import org.pocketcampus.plugin.news.android.iface.INewsModel;
import org.pocketcampus.plugin.news.android.iface.INewsView;
import org.pocketcampus.plugin.news.shared.Feed;
import org.pocketcampus.plugin.news.shared.NewsItem;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

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
	private SharedPreferences prefs_;

	/** The map of feed names with their Urls */
	private HashMap<String, String> mFeedUrls;

	/** The list of items filtered according to what the user wants */
	private List<NewsItemWithImage> filteredList;

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
		if (prefs_ == null) {
			prefs_ = PreferenceManager.getDefaultSharedPreferences(ctx);
		}

		ArrayList<NewsItemWithImage> filteredList = new ArrayList<NewsItemWithImage>();
		for (NewsItemWithImage newsItem : mNewsItems) {
			if (prefs_.getBoolean(NewsPreferences.LOAD_RSS
					+ newsItem.getNewsItem().getFeed(), true)) {
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
					.equals(newsItemWithImage.getNewsItem().getTitle())) {
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
	public HashMap<String, String> getFeedsUrls() {
		return mFeedUrls;
	}

	@Override
	public void setFeedsUrls(Map<String, String> map) {
		System.out.println("Setting feed urls" + map.keySet().toArray()[0]);
		if (map != null) {
			mFeedUrls = (HashMap<String, String>) map;
		}
	}
}
