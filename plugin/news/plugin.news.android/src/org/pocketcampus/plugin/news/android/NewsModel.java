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
	private List<NewsItemWithImage> mNewsItems;

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
	public List<NewsItemWithImage> getNews() {
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
}
