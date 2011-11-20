package org.pocketcampus.plugin.news.android;

import java.util.ArrayList;
import java.util.List;

import org.pocketcampus.android.platform.sdk.core.IView;
import org.pocketcampus.android.platform.sdk.core.PluginModel;
import org.pocketcampus.plugin.news.android.iface.INewsModel;
import org.pocketcampus.plugin.news.android.iface.INewsView;
import org.pocketcampus.plugin.news.shared.Feed;
import org.pocketcampus.plugin.news.shared.NewsItem;

/**
 * 
 * @author Elodie <elodienilane.triponez@epfl.ch>
 * 
 */
public class NewsModel extends PluginModel implements INewsModel {
	/** Listeners for the state of the view */
	INewsView mListeners = (INewsView) getListeners();

	/** List of news items to display. */
	private List<NewsItem> mNewsItems;

	/** List of Feeds to display */
	private List<Feed> mNewsFeeds;

	@Override
	protected Class<? extends IView> getViewInterface() {
		return INewsView.class;
	}

	public void setNews(List<NewsItem> newsItems) {
		if (newsItems != null) {
			mNewsItems = newsItems;

			mListeners.newsUpdated();
		}
	}

	@Override
	public List<NewsItem> getNews() {
		return mNewsItems;
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
				mNewsItems = new ArrayList<NewsItem>();
			}

			for (Feed f : mNewsFeeds) {
				mNewsItems.addAll(f.getItems());
			}
			mListeners.newsUpdated();
		}
	}
}
