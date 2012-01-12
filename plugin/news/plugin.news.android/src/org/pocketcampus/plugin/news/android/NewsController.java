package org.pocketcampus.plugin.news.android;

import java.util.Locale;

import org.pocketcampus.android.platform.sdk.core.PluginController;
import org.pocketcampus.android.platform.sdk.core.PluginModel;
import org.pocketcampus.plugin.news.android.iface.INewsController;
import org.pocketcampus.plugin.news.android.req.FeedUrlsRequest;
import org.pocketcampus.plugin.news.android.req.NewsItemContentRequest;
import org.pocketcampus.plugin.news.android.req.NewsItemsRequest;
import org.pocketcampus.plugin.news.shared.NewsService.Client;
import org.pocketcampus.plugin.news.shared.NewsService.Iface;
import org.pocketcampus.plugin.news.shared.NewsService.getFeedUrls_args;
import org.pocketcampus.plugin.news.shared.NewsService.getNewsItemContent_args;
import org.pocketcampus.plugin.news.shared.NewsService.getNewsItems_args;

/**
 * Controller for the news plugin. Takes care of interactions between the model
 * and the view and gets information from the server.
 * 
 * @author Elodie <elodienilane.triponez@epfl.ch>
 */
public class NewsController extends PluginController implements INewsController {

	/** The plugin's model. */
	private NewsModel mModel;

	/** Interface to the plugin's server client . */
	private Iface mClient;

	/** The name of the plugin. */
	private String mPluginName = "news";

	/**
	 * Initializes the plugin with a model and a client.
	 */
	@Override
	public void onCreate() {
		// Initializing the model is part of the controller's job...
		mModel = new NewsModel();

		// ...as well as initializing the client.
		// The "client" is the connection we use to access the service.
		mClient = (Iface) getClient(new Client.Factory(), mPluginName);

	}

	/**
	 * Returns the model for which this controller works.
	 */
	@Override
	public PluginModel getModel() {
		return mModel;
	}

	/**
	 * Initiates a request to the server to get the feed urls and names.
	 */
	@Override
	public void getFeedUrls() {
		String language = Locale.getDefault().getLanguage();

		getFeedUrls_args param = new getFeedUrls_args(language);
		new FeedUrlsRequest().start(this, mClient, param);
	}

	/**
	 * Initiates a request to the server to get the news items.
	 */
	@Override
	public void getNewsItems() {
		String language = Locale.getDefault().getLanguage();

		getNewsItems_args param = new getNewsItems_args(language);
		new NewsItemsRequest().start(this, mClient, param);
	}

	/**
	 * Loads the news item content for a specific item
	 * 
	 * @param newsItem
	 *            The news item for which to load the content.
	 */
	@Override
	public void getNewsContent(long itemId) {
		getNewsItemContent_args param = new getNewsItemContent_args(itemId);
		new NewsItemContentRequest().start(this, mClient, param);
	}
}
