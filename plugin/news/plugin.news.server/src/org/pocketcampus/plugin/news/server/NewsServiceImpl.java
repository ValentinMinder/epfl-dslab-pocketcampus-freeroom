package org.pocketcampus.plugin.news.server;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Set;

import org.apache.thrift.TException;
import org.pocketcampus.plugin.news.server.parse.FeedsListParser;
import org.pocketcampus.plugin.news.server.parse.FeedsLists;
import org.pocketcampus.plugin.news.server.parse.RssParser;
import org.pocketcampus.plugin.news.shared.Feed;
import org.pocketcampus.plugin.news.shared.NewsItem;
import org.pocketcampus.plugin.news.shared.NewsService;

/**
 * 
 * Class that takes care of the services the News server provides to the client
 * 
 * @author Elodie <elodienilane.triponez@epfl.ch>
 * 
 */
public class NewsServiceImpl implements NewsService.Iface {

	/** List of Feed Urls for English */
	private HashMap<String, String> mFeedUrls;

	/** List of Feeds for English */
	private List<Feed> mFeedsList;

	/** List of NewsItems for English */
	private List<NewsItem> mNewsItemsList;

	/** List of Feed Urls for French */
	private HashMap<String, String> mFeedUrlsFr;

	/** List of Feeds for French */
	private List<Feed> mFeedsListFr;

	/** List of NewsItems for French */
	private List<NewsItem> mNewsItemsListFr;

	/** Date of the last Feeds update */
	private Date mLastImportedFeeds;

	/** Interval in minutes at which the news should be fetched */
	private int REFRESH_INTERVAL = 30;

	/**
	 * Constructor import feed Urls and feed Contents since it's the first
	 * execution of the server.
	 */
	public NewsServiceImpl() {
		System.out.println("Starting News plugin server...");

		List<String> languages = new ArrayList<String>();
		languages.add("fr");
		languages.add("en");
		
		
		mFeedsList = new ArrayList<Feed>();
		mFeedsListFr = new ArrayList<Feed>();
		mNewsItemsList = new ArrayList<NewsItem>();
		mNewsItemsListFr = new ArrayList<NewsItem>();

		getFeedsUrls();
		importFeeds();
	}

	/**
	 * Initiates parsing of the feeds list from the file stored on the server
	 */
	private void getFeedsUrls() {
		FeedsListParser flp = new FeedsListParser("feeds_list_en.txt");
		mFeedUrls = flp.getFeeds();
		// FeedsListParser flpFr = new FeedsListParser("feeds_list_fr.txt");
		// mFeedUrlsFr = flp.getFeeds();
	}

	/**
	 * Imports newsItems from the RSS feed
	 */
	private void importFeeds() {
		if (!isUpToDate(mLastImportedFeeds) || mNewsItemsList == null
				|| mNewsItemsList.isEmpty()) {
			importFeedForLanguage(mFeedUrls, mNewsItemsList, mFeedsList);
		}
		// if (!isUpToDate(mLastImportedFeeds) || mNewsItemsListFr == null
		// || mNewsItemsListFr.isEmpty()) {
		// importFeedForLanguage(mFeedUrlsFr, mNewsItemsListFr, mFeedsListFr);
		// }
		mLastImportedFeeds = new Date();
	}

	private void importFeedForLanguage(HashMap<String, String> mFeedUrls,
			List<NewsItem> mNewsItemsList, List<Feed> mFeedsList) {
		System.out.println("<News> Reimporting Feeds");
		// There is no feed to download
		if (mFeedUrls.isEmpty()) {
			return;
		}

		if (mNewsItemsList != null) {
			mNewsItemsList.clear();
		}
		if (mFeedsList != null) {
			mFeedsList.clear();
		}
		// Create a parser for each feed and put the items into the list
		RssParser parser;
		Feed feed;
		Set<String> feedNames = mFeedUrls.keySet();
		for (String feedName : feedNames) {
			parser = new RssParser(feedName, mFeedUrls.get(feedName));

			parser.parse();
			feed = parser.getFeed();

			if (feed != null) {
				List<NewsItem> feedItems = feed.getItems();
				for (int i = 0; i < 5 && i < feedItems.size(); i++) {
					mNewsItemsList.add(feedItems.get(i));

				}
				mFeedsList.add(feed);
			}
		}

		// Sort the news (done asynchronously)
		Collections.sort(mNewsItemsList, newsItemComparator);

	}

	/**
	 * Update the NewsItems from the corresponding list of feeds and return
	 * them.
	 */
	@Override
	public List<NewsItem> getNewsItems(String language) throws TException {
		FeedsLists.getFeedsLists();
		importFeeds();
		System.out.println(mNewsItemsList.size());
		return mNewsItemsList;
	}

	Comparator<NewsItem> newsItemComparator = new Comparator<NewsItem>() {
		@Override
		public int compare(NewsItem o1, NewsItem o2) {
			try {
				if (o2.getPubDate() < o1.getPubDate()) {
					return -1;
				} else if (o2.getPubDate() > o1.getPubDate()) {
					return 1;
				}
				return 0;
			} catch (NullPointerException e) {
				return 0;
			}
		}
	};

	/**
	 * Update the Feeds from the corresponding list of feeds and return them.
	 */
	@Override
	public List<Feed> getFeeds(String language) throws TException {
		importFeeds();
		return mFeedsList;
	}

	/**
	 * Checks whether the date is up to date (according to today and this
	 * particular hour)
	 * 
	 * @param oldDate
	 * @return true if it is
	 */
	private boolean isUpToDate(Date oldDate) {
		if (oldDate == null)
			return false;

		Calendar now = Calendar.getInstance();
		now.setTime(new Date());

		Calendar then = Calendar.getInstance();
		then.setTime(oldDate);

		if (now.get(Calendar.DAY_OF_WEEK) != then.get(Calendar.DAY_OF_WEEK)) {
			return false;
		} else {
			if (getMinutes(then.getTime(), now.getTime()) > REFRESH_INTERVAL) {
				return false;
			}
		}
		return true;
	}

	/**
	 * To get the minutes separating two different dates
	 * 
	 * @param then
	 * @param now
	 * @return the minutes that separate both dates
	 */
	private long getMinutes(Date then, Date now) {
		long diff = now.getTime() - then.getTime();

		long realDiff = diff / 60000;

		return realDiff;
	}
}