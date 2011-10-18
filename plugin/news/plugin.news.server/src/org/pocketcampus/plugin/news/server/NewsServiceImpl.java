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

	public NewsServiceImpl() {
		System.out.println("Starting News plugin server...");
		// List<String> feedUrls = new ArrayList<String>();
		// feedUrls.add("http://www.pocketcampus.org/feed/");
		// feedUrls.add("http://actu.epfl.ch/feeds/rss/mediacom/en/");
		// feedUrls.add("http://actu.epfl.ch/feeds/rss/enac/en/");
		// feedUrls.add("http://actu.epfl.ch/feeds/rss/sb/en/");
		// feedUrls.add("http://actu.epfl.ch/feeds/rss/ic/en/");
		// feedUrls.add("http://actu.epfl.ch/feeds/rss/cdh/en/");
		// feedUrls.add("http://actu.epfl.ch/feeds/rss/sti/en/");
		// feedUrls.add("http://actu.epfl.ch/feeds/rss/sv/en/");
		// feedUrls.add("http://actu.epfl.ch/feeds/rss/cdm/en/");
		// try {
		// List<NewsItem> newsItems = getNewsItems(feedUrls);
		// System.out.println(newsItems.size());
		// for(int i = 0; i<newsItems.size(); i++){
		// System.out.println(newsItems.get(i).getTitle());
		// }
		// } catch (TException e) {
		// e.printStackTrace();
		// }
	}

	/**
	 * Download the NewsItems from the corresponding list of feeds and return
	 * them.
	 */
	// TODO: Parse only once, not everytime a request is done and save them
	// somewhere in the server, like the menus
	@Override
	public List<NewsItem> getNewsItems(List<String> feedUrls) throws TException {
		// List of items in the feeds
		List<NewsItem> list = new ArrayList<NewsItem>();

		// There is no feed to download
		if (feedUrls.isEmpty()) {
			return null;
		}

		// Create a parser for each feed and put the items into the list
		RssParser parser;
		Feed feed;
		for (String feedUrl : feedUrls) {
			parser = new RssParser(feedUrl);

			parser.parse();
			feed = parser.getFeed();

			if (feed != null) {
				list.addAll(feed.getItems());
			}
		}

		// Sort the news (done asynchronously)
		Collections.sort(list);

		return list;
	}

}
