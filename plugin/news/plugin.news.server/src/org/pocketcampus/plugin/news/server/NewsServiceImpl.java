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

	/** HashMap of languages with their Feed Urls */
	private HashMap<String, HashMap<String, String>> mLanguagesFeedUrls;

	/** HashMap of languages with their Feeds */
	private HashMap<String, List<Feed>> mLanguagesFeedsList;

	/** HashMap of languages with their NewsItems */
	private HashMap<String, List<NewsItem>> mLanguagesNewsItemsList;

	/** Date of the last Feeds update */
	private Date mLastImportedFeeds;

	/** Interval in minutes at which the news should be fetched */
	private int REFRESH_INTERVAL = 60;

	private final int MAX_NUMBER_RESULTS = 5;

	private final String DEFAULT_LANGUAGE = "en";

	/**
	 * Constructor import feed Urls and feed Contents since it's the first
	 * execution of the server.
	 * 
	 * @throws TException
	 */
	public NewsServiceImpl() {
		System.out.println("Starting News plugin server...");

		mLanguagesFeedsList = new HashMap<String, List<Feed>>();
		mLanguagesNewsItemsList = new HashMap<String, List<NewsItem>>();

		parseFeedsUrls();
		importFeeds();
	}

	/**
	 * Initiates parsing of the feeds list from the file stored on the server
	 */
	private void parseFeedsUrls() {
		FeedsListParser flp = new FeedsListParser("NewsFeedsLanguages.txt");
		mLanguagesFeedUrls = flp.getFeeds();
	}

	/**
	 * Returns the feed urls and their corresponding name
	 * 
	 * @param language
	 *            the language for which we want the feed Urls.
	 * @return the feed urls and their names
	 */
	@Override
	public HashMap<String, String> getFeedUrls(String language)
			throws TException {
		if (mLanguagesFeedsList != null
				&& mLanguagesFeedsList.containsKey(language)) {
			return mLanguagesFeedUrls.get(language);
		} else {
			return mLanguagesFeedUrls.get(DEFAULT_LANGUAGE);
		}
	}

	/**
	 * Imports newsItems from the RSS feed
	 */
	private void importFeeds() {
		if (mLanguagesFeedUrls != null
				&& (!isUpToDate(mLastImportedFeeds)
						|| mLanguagesNewsItemsList == null || mLanguagesNewsItemsList
							.isEmpty())) {
			Set<String> languages = mLanguagesFeedUrls.keySet();
			for (String language : languages) {
				importFeedForLanguage(language,
						mLanguagesFeedUrls.get(language));
			}
		}
		mLastImportedFeeds = new Date();
	}

	/**
	 * Imports all feeds in the given language from the corresponding urls
	 * 
	 * @param language
	 *            the language of the feeds to import
	 * @param mFeedUrls
	 *            the url to the feeds
	 */
	private void importFeedForLanguage(String language,
			HashMap<String, String> mFeedUrls) {
		System.out.println("<News> Reimporting Feeds for language " + language);
		// There is no feed to download
		if (mFeedUrls.isEmpty()) {
			return;
		}

		// Create a parser for each feed and put the items into the list
		RssParser parser;
		Feed feed;
		Set<String> feedNames = mFeedUrls.keySet();
		List<Feed> allFeeds = new ArrayList<Feed>();
		for (String feedName : feedNames) {
			parser = new RssParser(feedName, mFeedUrls.get(feedName));

			parser.parse();
			feed = parser.getFeed();

			if (feed != null) {
				List<NewsItem> feedItems = feed.getItems();

				List<NewsItem> toKeep = new ArrayList<NewsItem>();
				for (int i = 0; i < MAX_NUMBER_RESULTS && i < feedItems.size(); i++) {
					toKeep.add(feedItems.get(i));
				}
				if (mLanguagesNewsItemsList.containsKey(language)) {
					toKeep.addAll(mLanguagesNewsItemsList.get(language));
				}
				Collections.sort(toKeep, newsItemComparator);
				mLanguagesNewsItemsList.put(language, toKeep);
				allFeeds.add(feed);
			}
		}
		mLanguagesFeedsList.put(language, allFeeds);
	}

	/**
	 * Update the NewsItems from the corresponding list of feeds and return
	 * them.
	 */
	@Override
	public List<NewsItem> getNewsItems(String language) throws TException {
		importFeeds();
		if (mLanguagesNewsItemsList != null
				&& mLanguagesNewsItemsList.containsKey(language)) {
			System.out.println(mLanguagesNewsItemsList.get(language).size());
			return mLanguagesNewsItemsList.get(language);
		} else {
			return mLanguagesNewsItemsList.get("en");
		}
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
		if (mLanguagesFeedsList != null
				&& mLanguagesFeedsList.containsKey(language)) {
			return mLanguagesFeedsList.get(language);
		} else {
			return mLanguagesFeedsList.get("en");
		}
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
			System.out.println("Difference between the 2 dates: "
					+ getMinutes(then.getTime(), now.getTime()));
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