package org.pocketcampus.plugin.news.android.req;

import java.util.List;

import org.pocketcampus.android.platform.sdk.io.Request;
import org.pocketcampus.plugin.news.android.NewsController;
import org.pocketcampus.plugin.news.android.NewsModel;
import org.pocketcampus.plugin.news.shared.NewsItem;
import org.pocketcampus.plugin.news.shared.NewsService.Iface;
import org.pocketcampus.plugin.news.shared.NewsService.getNewsItems_args;

/**
 * 
 * A request to the server to retrieve the NewsItems.
 * 
 * @author Elodie <elodienilane.triponez@epfl.ch>
 * 
 */
public class NewsItemsRequest extends
		Request<NewsController, Iface, getNewsItems_args, List<NewsItem>> {
	/**
	 * Initiate the <code>getNewsItem</code> Request at the server.
	 * 
	 * @param client
	 *            the client that communicates with the server
	 * @param param
	 *            the parameters to be sent for the request. Not used.
	 * @return the News Items.
	 */
	@Override
	protected List<NewsItem> runInBackground(Iface client,
			getNewsItems_args param) throws Exception {
		System.out.println("<News> Requesting news items");
		return client.getNewsItems(param.getLanguage());
	}

	/**
	 * Tell the model the NewsItems have been updated.
	 * 
	 * @param controller
	 *            the controller that initiated the request, of which we have to
	 *            notify of the result.
	 * @param result
	 *            the NewsItem list gotten from the server.
	 */
	@Override
	protected void onResult(NewsController controller, List<NewsItem> result) {
		System.out.println("<NewsItemsRequest> onResult");
		((NewsModel) controller.getModel()).setNews(result);
	}

	/**
	 * Notifies the Model that an error has occurred while processing the
	 * request.
	 * 
	 * @param controller
	 *            the controller that initiated the request.
	 */
	@Override
	protected void onError(NewsController controller, Exception e) {
		System.out.println("onError");
		((NewsModel) controller.getModel()).notifyNetworkError();
		e.printStackTrace();
	}
}