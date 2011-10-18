//package org.pocketcampus.plugin.news.android.req;
//
//import java.util.List;
//
//import org.pocketcampus.android.platform.sdk.io.Request;
//import org.pocketcampus.plugin.news.android.NewsController;
//import org.pocketcampus.plugin.news.android.NewsModel;
//import org.pocketcampus.plugin.news.shared.NewsItem;
//import org.pocketcampus.plugin.news.shared.NewsService.Iface;
//
//public class NewsItemRequest extends Request<NewsController, Iface, Object, List<NewsItem>> {
//	@Override
//	protected List<NewsItem> runInBackground(Iface client, Object param) throws Exception {
//		System.out.println("Requesting news items");
//		return client.getNewsItems(feedUrls);
//	}
//
//	@Override
//	protected void onResult(NewsController controller, List<NewsItem> result) {
//		System.out.println("onResult");
//		((NewsModel) controller.getModel()).setBar(result);
//	}
//	
//	@Override
//	protected void onError(NewsController controller, Exception e) {
//		System.out.println("onError");
//		controller.getModel().notifyNetworkError();
//		e.printStackTrace();
//	}
//}