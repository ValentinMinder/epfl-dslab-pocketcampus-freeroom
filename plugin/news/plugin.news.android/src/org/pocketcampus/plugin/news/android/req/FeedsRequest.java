package org.pocketcampus.plugin.news.android.req;

import java.util.List;

import org.pocketcampus.android.platform.sdk.io.Request;
import org.pocketcampus.plugin.news.android.NewsController;
import org.pocketcampus.plugin.news.android.NewsModel;
import org.pocketcampus.plugin.news.shared.Feed;
import org.pocketcampus.plugin.news.shared.NewsService.Iface;

import android.util.Log;

/**
 * 
 * A request to the server for all Feeds
 * 
 * @author Elodie <elodienilane.triponez@epfl.ch>
 * 
 */
public class FeedsRequest extends
		Request<NewsController, Iface, Object, List<Feed>> {

	/**
	 * Initiate the <code>getFeeds</code> request at the server
	 * 
	 * @param client
	 *            the client that communicates with the server
	 * @param param
	 *            the parameters to be sent for the request. Not used.
	 * @return the list of Feeds from the server
	 */
	@Override
	protected List<Feed> runInBackground(Iface client, Object param)
			throws Exception {
		Log.d("<FeedsRequest>:", "Run");
		return client.getFeeds();
	}

	/**
	 * Set the list of feeds in the model.
	 * 
	 * @param controller
	 *            the controller that initiated the request, of which we have to
	 *            notify of the result
	 * @param result
	 *            the list of feeds gotten from the server
	 */
	@Override
	protected void onResult(NewsController controller, List<Feed> result) {
		Log.d("<FeedsRequest>:", "onResult");
		Log.d("<FeedsRequest>:", result.size() + "");
		((NewsModel) controller.getModel()).setFeedsList(result);
	}

	/**
	 * Notifies the Model that an error has occurred while processing the
	 * request.
	 * 
	 * @param controller
	 *            the controller that initiated the request
	 */
	@Override
	protected void onError(NewsController controller, Exception e) {
		Log.d("<FeedsRequest>:", "onError");
		controller.getModel().notifyNetworkError();
		e.printStackTrace();
	}
}
