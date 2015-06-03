package org.pocketcampus.plugin.news.android;

import java.util.Comparator;
import java.util.LinkedList;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import org.pocketcampus.platform.android.core.PluginController;
import org.pocketcampus.platform.android.core.PluginModel;
import org.pocketcampus.plugin.news.android.iface.INewsController;
import org.pocketcampus.plugin.news.android.iface.INewsView;
import org.pocketcampus.plugin.news.android.req.AllFeedsRequest;
import org.pocketcampus.plugin.news.android.req.FeedItemRequest;
import org.pocketcampus.plugin.news.shared.NewsFeedItem;
import org.pocketcampus.plugin.news.shared.NewsFeedItemContentRequest;
import org.pocketcampus.plugin.news.shared.NewsFeedsRequest;
import org.pocketcampus.plugin.news.shared.NewsService.Client;
import org.pocketcampus.plugin.news.shared.NewsService.Iface;

import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;

/**
 * NewsController - Main logic for the News Plugin.
 * 
 * This class issues requests to the News PocketCampus
 * server to get the News data from Memento.
 * 
 * @author Amer <amer.chamseddine@epfl.ch>
 * 
 */
public class NewsController extends PluginController implements INewsController {

	/** The plugin's model. */
	private NewsModel mModel;

	/** Interface to the plugin's server client */
	private Iface mClient;

	/** The name of the plugin */
	private String mPluginName = "news";

	/**
	 * Initializes the plugin with a model and a client.
	 */
	@Override
	public void onCreate() {
		// Initializing the model is part of the controller's job...
		mModel = new NewsModel(getApplicationContext());

		// ...as well as initializing the client.
		// The "client" is the connection we use to access the service.
		mClient = (Iface) getClient(new Client.Factory(), mPluginName);

		// initialize ImageLoader
		ImageLoader.getInstance().init(ImageLoaderConfiguration.createDefault(getApplicationContext()));
	}

	/**
	 * Returns the model for which this controller works.
	 */
	@Override
	public PluginModel getModel() {
		return mModel;
	}


	/**
	 * Initiates a request to the server to get the news items.
	 */
	public void requestItemContents(INewsView caller, int itemId, boolean useCache) {
		NewsFeedItemContentRequest req = new NewsFeedItemContentRequest(Locale.getDefault().getLanguage(), itemId);
		new FeedItemRequest(caller).setBypassCache(!useCache).start(this, mClient, req);
	}

	/**
	 * Initiates a request to the server to get the news feeds.
	 */
	public void requestNewsFeeds(INewsView caller, boolean useCache) {
		NewsFeedsRequest req = new NewsFeedsRequest(Locale.getDefault().getLanguage(), true);
		new AllFeedsRequest(caller).setBypassCache(!useCache).start(this, mClient, req);
	}


	/*****
	 * HELPER CLASSES AND FUNCTIONS
	 */
	
	public static Comparator<NewsFeedItem> getNewsFeedItemComp4sort() {
		return new Comparator<NewsFeedItem>() {
			public int compare(NewsFeedItem lhs, NewsFeedItem rhs) {
				return Long.valueOf(rhs.getDate()).compareTo(lhs.getDate());
			}
		};
	}
	
//	public static Comparator<NewsFeed> getNewsFeedComp4sort() {
//		return new Comparator<NewsFeed>() {
//			public int compare(NewsFeed lhs, NewsFeed rhs) {
//				return lhs.getName().compareTo(rhs.getName());
//			}
//		};
//	}
	
//	public static DateFormat getDateFormat(Context c) {
//		return android.text.format.DateFormat.getDateFormat(c);
//	}
	
	public static String getResizedPicUrl(String rawUrl, int factor) {
		if(rawUrl == null)
			return null;
		return rawUrl.replace("{x}", "" + 16 * factor).replace("{y}", "" + 9 * factor);
	}
	
	public static <X, Y> void addToMap(Map<X, List<Y>> map, X x, Y y) {
		if(!map.containsKey(x))
			map.put(x, new LinkedList<Y>());
		map.get(x).add(y);
	}
	
	public static String sanitizeContents(String s) {
		return s.replaceAll("<img[^>]+src\\s*=\\s*['\"]([^'\"]+)['\"][^>]*>", "");
	}
		
}
