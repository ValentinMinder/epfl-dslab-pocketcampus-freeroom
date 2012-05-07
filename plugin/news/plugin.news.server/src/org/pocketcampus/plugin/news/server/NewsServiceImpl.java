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
 * Class that takes care of the services the News server provides to the client.
 * 
 * @author Elodie <elodienilane.triponez@epfl.ch>
 * 
 */
public class NewsServiceImpl implements NewsService.Iface {

	/** HashMap of languages with their Feed Urls. */
	private HashMap<String, HashMap<String, String>> mLanguagesFeedUrls;

	/** HashMap of languages with their Feeds. */
	private HashMap<String, List<Feed>> mLanguagesFeedsList;

	/** HashMap of languages with their NewsItems. */
	private HashMap<String, List<NewsItem>> mLanguagesNewsItemsList;

	/** HashMap of NewsDescriptions. */
	private HashMap<Long, String> mNewsContents;

	/** Date of the last Feeds update. */
	private Date mLastImportedFeeds;

	/** Interval in minutes at which the news should be fetched. */
	private int REFRESH_INTERVAL = 60;

	/** The maximum number of results to return per feed. */
	private final int MAX_NUMBER_RESULTS = 10;

	/**
	 * The default News Language, to be used when the news are not available in
	 * the user's phone's language.
	 */
	private final String DEFAULT_LANGUAGE = "en";

	/**
	 * Constructor imports feed Urls and feed contents the server's first
	 * execution.
	 * 
	 * @throws TException
	 */
	public NewsServiceImpl() {
		System.out.println("Starting News plugin server...");

		mLanguagesFeedsList = new HashMap<String, List<Feed>>();
		mLanguagesNewsItemsList = new HashMap<String, List<NewsItem>>();
		mNewsContents = new HashMap<Long, String>();
		
		parseFeedsUrls();
		importFeeds();
	}

	/**
	 * Initiates parsing of the feeds list from the file stored on the server.
	 */
	private void parseFeedsUrls() {
		FeedsListParser flp = new FeedsListParser("NewsFeedsLanguages.txt");
		mLanguagesFeedUrls = flp.getFeeds();
	}

	/**
	 * Returns the feed urls and their corresponding name.
	 * 
	 * @param language
	 *            The language for which we want the feed Urls.
	 * @return The feed urls and their names.
	 */
	@Override
	public HashMap<String, String> getFeedUrls(String language)
			throws TException {
		if (mLanguagesFeedUrls != null
				&& mLanguagesFeedUrls.containsKey(language)) {
			return mLanguagesFeedUrls.get(language);
		} else {
			return mLanguagesFeedUrls.get(DEFAULT_LANGUAGE);
		}
	}

	/**
	 * Imports newsItems from the RSS feeds.
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
			mLastImportedFeeds = new Date();
		}
	}

	/**
	 * Imports all feeds in the given language from the corresponding Urls.
	 * 
	 * @param language
	 *            The language of the feeds to import.
	 * @param mFeedUrls
	 *            The url to the feeds.
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
				// Add feed's items to the list
				List<NewsItem> feedItems = feed.getItems();

				// Keep only the 5 latest news.
				List<NewsItem> toKeep = new ArrayList<NewsItem>();
				for (int i = 0; i < MAX_NUMBER_RESULTS && i < feedItems.size(); i++) {
					toKeep.add(feedItems.get(i));
				}
				// Add the items to the list of News Items.
				if (mLanguagesNewsItemsList.containsKey(language)) {
					toKeep.addAll(mLanguagesNewsItemsList.get(language));
					mLanguagesNewsItemsList.remove(language);
				}

				Collections.sort(toKeep, newsItemComparator);
				//mLanguagesNewsItemsList.clear();
				mLanguagesNewsItemsList.put(language, toKeep);
				
				// Add contents to list
				mNewsContents.putAll(parser.getNewsContents());
				allFeeds.add(feed);
			}
		}
		mLanguagesFeedsList.put(language, allFeeds);
	}

	/**
	 * Update the NewsItems from the corresponding list of feeds and return
	 * them.
	 * 
	 * @param language
	 *            The language in which the NewsItems are requested
	 * @return The list of NewsItems for the corresponding language, or English
	 *         if it's not available
	 */
	@Override
	public List<NewsItem> getNewsItems(String language) throws TException {
		importFeeds();
		List<NewsItem> toReturn = null;
		if (mLanguagesNewsItemsList != null
				&& mLanguagesNewsItemsList.containsKey(language)) {
			toReturn = mLanguagesNewsItemsList.get(language);
		} else {
			toReturn = mLanguagesNewsItemsList.get(DEFAULT_LANGUAGE);
		}

		return toReturn;
	}

	/**
	 * Get the content of a specific News.
	 * 
	 * @param language
	 *            The language of the NewsItem.
	 * @param newsItem
	 *            The NewsItem for which the content is requested.
	 * @return The content of the NewsItem.
	 */
	@Override
	public String getNewsItemContent(long newsItemId) throws TException {
		importFeeds();
		String toReturn = null;
		if (mNewsContents != null && mNewsContents.containsKey(newsItemId)) {
			return mNewsContents.get(newsItemId);
		}

		return toReturn;
	}

	/**
	 * Comparator for two NewsItems. Compares the publication date.
	 */
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
	 * 
	 * @param language
	 *            The language in which the Feeds are requested.
	 * @return The list of Feeds for the corresponding language, English if it's
	 *         not available.
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
	 * particular hour).
	 * 
	 * @param oldDate
	 *            The date to check.
	 * @return True if it is, false otherwise.
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
	 * @param then
	 *            The first date.
	 * @param now
	 *            The second date.
	 * @return The minutes that separate two dates
	 */
	private long getMinutes(Date then, Date now) {
		long diff = now.getTime() - then.getTime();

		long realDiff = diff / 60000;

		return realDiff;
	}
}