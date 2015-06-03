package org.pocketcampus.plugin.news.android.req;

import org.pocketcampus.platform.android.io.Request;
import org.pocketcampus.plugin.news.android.NewsController;
import org.pocketcampus.plugin.news.android.NewsModel;
import org.pocketcampus.plugin.news.android.iface.INewsView;
import org.pocketcampus.plugin.news.shared.NewsFeedsRequest;
import org.pocketcampus.plugin.news.shared.NewsFeedsResponse;
import org.pocketcampus.plugin.news.shared.NewsService.Iface;
import org.pocketcampus.plugin.news.shared.NewsStatusCode;

/**
 * AllFeedsRequest
 * 
 * This class sends an HttpRequest using Thrift to the PocketCampus server
 * in order to get all the news feeds.
 * 
 * @author Amer <amer.chamseddine@epfl.ch>
 *
 */
public class AllFeedsRequest extends Request<NewsController, Iface, NewsFeedsRequest, NewsFeedsResponse> {

	private INewsView caller;
	
	public AllFeedsRequest(INewsView caller) {
		this.caller = caller;
	}
	
	@Override
	protected NewsFeedsResponse runInBackground(Iface client, NewsFeedsRequest param) throws Exception {
		return client.getAllFeeds(param);
	}

	@Override
	protected void onResult(NewsController controller, NewsFeedsResponse result) {
		if(result.getStatusCode() == NewsStatusCode.OK) {
			
			((NewsModel) controller.getModel()).setNewsFeeds(result.getFeeds());
			
			keepInCache();
		} else {
			caller.newsServersDown();
		}
	}

	@Override
	protected void onError(NewsController controller, Exception e) {
		if(foundInCache())
			caller.networkErrorCacheExists();
		else
			caller.networkErrorHappened();
		e.printStackTrace();
	}
	
}
