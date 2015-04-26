package org.pocketcampus.plugin.news.android.req;

import org.pocketcampus.platform.android.io.Request;
import org.pocketcampus.plugin.news.android.NewsController;
import org.pocketcampus.plugin.news.android.NewsModel;
import org.pocketcampus.plugin.news.android.iface.INewsView;
import org.pocketcampus.plugin.news.shared.NewsFeedItemContentRequest;
import org.pocketcampus.plugin.news.shared.NewsFeedItemContentResponse;
import org.pocketcampus.plugin.news.shared.NewsService.Iface;
import org.pocketcampus.plugin.news.shared.NewsStatusCode;

/**
 * FeedItemRequest
 * 
 * This class sends an HttpRequest using Thrift to the PocketCampus server
 * in order to get the contents of a news feed.
 * 
 * @author Amer <amer.chamseddine@epfl.ch>
 *
 */
public class FeedItemRequest extends Request<NewsController, Iface, NewsFeedItemContentRequest, NewsFeedItemContentResponse> {

	private INewsView caller;
	
	public FeedItemRequest(INewsView caller) {
		this.caller = caller;
	}
	
	@Override
	protected NewsFeedItemContentResponse runInBackground(Iface client, NewsFeedItemContentRequest param) throws Exception {
		return client.getFeedItemContent(param);
	}

	@Override
	protected void onResult(NewsController controller, NewsFeedItemContentResponse result) {
		if(result.getStatusCode() == NewsStatusCode.OK) {
			
			((NewsModel) controller.getModel()).setItemContents(result.getContent());
			
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
