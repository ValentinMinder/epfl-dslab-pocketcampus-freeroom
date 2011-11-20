package org.pocketcampus.plugin.news.android;

import java.util.List;

import org.pocketcampus.android.platform.sdk.core.IView;
import org.pocketcampus.android.platform.sdk.core.PluginModel;
import org.pocketcampus.plugin.news.android.iface.INewsModel;
import org.pocketcampus.plugin.news.android.iface.INewsView;
import org.pocketcampus.plugin.news.shared.NewsItem;

public class NewsModel extends PluginModel implements INewsModel {
	INewsView mListeners = (INewsView) getListeners();

	// List of news items to display.
	private List<NewsItem> mNewsItems;

	@Override
	protected Class<? extends IView> getViewInterface() {
		return INewsView.class;
	}

	public void setNews(List<NewsItem> newsItems) {
		mNewsItems = newsItems;
		mListeners.newsUpdated();
	}

	@Override
	public List<NewsItem> getNews() {
		return mNewsItems;
	}
}
