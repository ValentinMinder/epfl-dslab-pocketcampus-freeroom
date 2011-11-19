package org.pocketcampus.plugin.news.server;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.List;

import org.apache.thrift.TException;
import org.pocketcampus.plugin.news.server.parse.FeedsListParser;
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

	/** List of Feed Urls */
	private List<String> mFeedUrls;

	/** List of Feeds */
	private List<Feed> mFeedsList;

	/** List of NewsItems */
	private List<NewsItem> mNewsItemsList;

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

		mFeedsList = new ArrayList<Feed>();
		mNewsItemsList = new ArrayList<NewsItem>();

		getFeedsUrls();
		importFeeds();
	}

	/**
	 * Initiates parsing of the feeds list from the file stored on the server
	 */
	private void getFeedsUrls() {
		FeedsListParser flp = new FeedsListParser("feeds_list.txt");
		mFeedUrls = flp.getFeeds();
	}

	/**
	 * Imports newsItems from the RSS feed
	 */
	private void importFeeds() {
		if (!isUpToDate(mLastImportedFeeds) || mNewsItemsList == null
				|| mNewsItemsList.isEmpty()) {
			// There is no feed to download
			if (mFeedUrls.isEmpty()) {
				return;
			}

			// Create a parser for each feed and put the items into the list
			RssParser parser;
			Feed feed;
			for (String feedUrl : mFeedUrls) {
				parser = new RssParser(feedUrl);

				parser.parse();
				feed = parser.getFeed();

				if (feed != null) {
					mNewsItemsList.addAll(feed.getItems());
					mFeedsList.add(feed);
				}
			}

			// Sort the news (done asynchronously)
			Collections.sort(mNewsItemsList);
			mLastImportedFeeds = new Date();
		}
	}

	/**
	 * Update the NewsItems from the corresponding list of feeds and return
	 * them.
	 */
	@Override
	public List<NewsItem> getNewsItems() throws TException {
		importFeeds();
		return mNewsItemsList;
	}

	/**
	 * Update the Feeds from the corresponding list of feeds and return
	 * them.
	 */
	@Override
	public List<Feed> getFeeds() throws TException {
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