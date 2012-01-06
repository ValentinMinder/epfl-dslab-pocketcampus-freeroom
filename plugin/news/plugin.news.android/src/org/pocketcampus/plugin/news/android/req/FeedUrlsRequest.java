package org.pocketcampus.plugin.news.android.req;

import java.util.Map;

import org.pocketcampus.android.platform.sdk.io.Request;
import org.pocketcampus.plugin.news.android.NewsController;
import org.pocketcampus.plugin.news.android.NewsModel;
import org.pocketcampus.plugin.news.shared.NewsService.Iface;
import org.pocketcampus.plugin.news.shared.NewsService.getFeedUrls_args;

/**
 * 
 * A request to the server to retrieve the feed urls.
 * 
 * @author Elodie <elodienilane.triponez@epfl.ch>
 * 
 */
public class FeedUrlsRequest extends
		Request<NewsController, Iface, getFeedUrls_args, Map<String, String>> {
	/**
	 * Initiate the <code>getNewsItem</code> Request at the server.
	 * 
	 * @param client
	 *            the client that communicates with the server.
	 * @param param
	 *            the parameters to be sent for the request.
	 * @return A Map containing the Feed Urls and their names.
	 */
	@Override
	protected Map<String, String> runInBackground(Iface client, getFeedUrls_args param)
			throws Exception {
		System.out.println("<News> Requesting feed urls and names");
		return client.getFeedUrls(param.getLanguage());
	}

	/**
	 * Tell the model the Feed urls and names have been updated.
	 * 
	 * @param controller
	 *            the controller that initiated the request, of which we have to
	 *            notify of the result.
	 * @param result
	 *            the feeds urls and names map gotten from the server.
	 */
	@Override
	protected void onResult(NewsController controller, Map<String, String> result) {
		System.out.println("<FeedUrlsRequest> onResult");
		((NewsModel) controller.getModel()).setFeedsUrls(result);
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
		((NewsModel) controller.getModel()).notifyNetworkErrorFeedUrls();
		e.printStackTrace();
	}
}