package org.pocketcampus.plugin.news.android.req;

import java.util.ArrayList;
import java.util.List;

import org.pocketcampus.android.platform.sdk.io.Request;
import org.pocketcampus.plugin.news.android.NewsController;
import org.pocketcampus.plugin.news.android.NewsModel;
import org.pocketcampus.plugin.news.shared.NewsItem;
import org.pocketcampus.plugin.news.shared.NewsService.Iface;

public class NewsItemRequest extends
		Request<NewsController, Iface, Object, List<NewsItem>> {
	@Override
	protected List<NewsItem> runInBackground(Iface client, Object param)
			throws Exception {
		System.out.println("Requesting news items");
		List<String> feedUrls = new ArrayList<String>();
		feedUrls.add("http://www.pocketcampus.org/feed/");
		feedUrls.add("http://actu.epfl.ch/feeds/rss/mediacom/en/");
		feedUrls.add("http://actu.epfl.ch/feeds/rss/enac/en/");
		feedUrls.add("http://actu.epfl.ch/feeds/rss/sb/en/");
		feedUrls.add("http://actu.epfl.ch/feeds/rss/ic/en/");
		feedUrls.add("http://actu.epfl.ch/feeds/rss/cdh/en/");
		feedUrls.add("http://actu.epfl.ch/feeds/rss/sti/en/");
		feedUrls.add("http://actu.epfl.ch/feeds/rss/sv/en/");
		feedUrls.add("http://actu.epfl.ch/feeds/rss/cdm/en/");
		
//		return client.getNewsItems();
		return null;
	}

	@Override
	protected void onResult(NewsController controller, List<NewsItem> result) {
		System.out.println("onResult");
		((NewsModel) controller.getModel()).setNews(result);
	}

	@Override
	protected void onError(NewsController controller, Exception e) {
		System.out.println("onError");
		controller.getModel().notifyNetworkError();
		e.printStackTrace();
	}
}