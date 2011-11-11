package org.pocketcampus.plugin.news.server;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.apache.thrift.TException;
import org.pocketcampus.plugin.news.shared.Feed;
import org.pocketcampus.plugin.news.shared.NewsItem;
import org.pocketcampus.plugin.news.shared.NewsService;

/**
 * 
 * Class that takes care of the services the server provides to the client
 * 
 * @author Elodie
 * 
 */
public class NewsServiceImpl implements NewsService.Iface {

	// List of items in the feeds
	private List<NewsItem> mList;
	private List<String> mFeedUrls;

	public NewsServiceImpl() {
		System.out.println("Starting News plugin server...");

		mList = new ArrayList<NewsItem>();

		mFeedUrls = new ArrayList<String>();
		mFeedUrls.add("http://www.pocketcampus.org/feed/");
		mFeedUrls.add("http://actu.epfl.ch/feeds/rss/mediacom/en/");
		mFeedUrls.add("http://actu.epfl.ch/feeds/rss/enac/en/");
		mFeedUrls.add("http://actu.epfl.ch/feeds/rss/sb/en/");
		mFeedUrls.add("http://actu.epfl.ch/feeds/rss/ic/en/");
		mFeedUrls.add("http://actu.epfl.ch/feeds/rss/cdh/en/");
		mFeedUrls.add("http://actu.epfl.ch/feeds/rss/sti/en/");
		mFeedUrls.add("http://actu.epfl.ch/feeds/rss/sv/en/");
		mFeedUrls.add("http://actu.epfl.ch/feeds/rss/cdm/en/");
	}

	/**
	 * Download the NewsItems from the corresponding list of feeds and return
	 * them.
	 */
	// TODO: Parse only once, not every time a request is done and save them
	// somewhere in the server, like the menus
	@Override
	public List<NewsItem> getNewsItems() throws TException {
		if (mList != null && !mList.isEmpty()) {
			return mList;
		} else {
			// There is no feed to download
			if (mFeedUrls.isEmpty()) {
				return null;
			}

			// Create a parser for each feed and put the items into the list
			RssParser parser;
			Feed feed;
			for (String feedUrl : mFeedUrls) {
				parser = new RssParser(feedUrl);

				parser.parse();
				feed = parser.getFeed();

				if (feed != null) {
					mList.addAll(feed.getItems());
				}
			}

			// Sort the news (done asynchronously)
			Collections.sort(mList);
		}

		return mList;
	}

}
