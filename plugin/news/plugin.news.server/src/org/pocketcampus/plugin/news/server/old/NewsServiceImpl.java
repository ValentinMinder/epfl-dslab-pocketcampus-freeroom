package org.pocketcampus.plugin.news.server.old;

import org.apache.thrift.TException;
import org.pocketcampus.plugin.news.shared.Feed;
import org.pocketcampus.plugin.news.shared.NewsItem;

import java.util.*;

/**
 * 
 * Class that takes care of the services the News server provides to the client.
 * 
 * @author Elodie <elodienilane.triponez@epfl.ch>
 * @author Amer <amer.chamseddine@epfl.ch>
 * 
 */
public class NewsServiceImpl {

	/** HashMap of languages with their Feed Urls. */
	private HashMap<String, HashMap<String, String>> mLanguagesFeedUrls;

	/** HashMap of languages with their Feeds. */
	@Deprecated
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
	public HashMap<String, String> getFeedUrls(String language)
			throws TException {
		System.out.println("getFeedUrls");
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
		if (!(mLanguagesFeedUrls != null
				&& (!isUpToDate(mLastImportedFeeds)
						|| mLanguagesNewsItemsList == null || mLanguagesNewsItemsList
							.isEmpty()))) {
			return;
		}
		mLastImportedFeeds = new Date();
		System.out.println("<News> Reimporting Feeds Asynchroneously");
		final NewsServiceImpl instance = this;
		new Thread(new Runnable() {
			public void run() {
				instance.importFeedsAsync();
			}
		}).start();
	}
	
	private void importFeedsAsync() {
		
		
		HashMap<String, List<Feed>> tLanguagesFeedsList = new HashMap<String, List<Feed>>();
		HashMap<String, List<NewsItem>> tLanguagesNewsItemsList = new HashMap<String, List<NewsItem>>();
		HashMap<Long, String> tNewsContents = new HashMap<Long, String>();
		

		
		for (String language : mLanguagesFeedUrls.keySet()) {
			HashMap<String, String> tFeedUrls = mLanguagesFeedUrls.get(language);
			if (tFeedUrls.isEmpty()) { // There is no feed to download
				continue;
			}
		
		
			// Create a parser for each feed and put the items into the list
			RssParser parser;
			Feed feed;
			Set<String> feedNames = tFeedUrls.keySet();
			List<Feed> allFeeds = new ArrayList<Feed>();
			for (String feedName : feedNames) {
				parser = new RssParser(feedName, tFeedUrls.get(feedName));
	
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
					if (tLanguagesNewsItemsList.containsKey(language)) {
						toKeep.addAll(tLanguagesNewsItemsList.get(language));
						tLanguagesNewsItemsList.remove(language);
					}
					//System.out.println(language + "======" + feedName + "=======" + toKeep.size());
	
					Collections.sort(toKeep, newsItemComparator);
					//mLanguagesNewsItemsList.clear();
					tLanguagesNewsItemsList.put(language, toKeep);
					
					// Add contents to list
					tNewsContents.putAll(parser.getNewsContents());
					allFeeds.add(feed);
				}
			}
			tLanguagesFeedsList.put(language, allFeeds);
			
			
		}
		
		
		mLanguagesFeedsList = tLanguagesFeedsList;
		mLanguagesNewsItemsList = tLanguagesNewsItemsList;
		mNewsContents = tNewsContents;
		

		
		System.out.println("<News> Asynchroneous Reimport Finished");
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
	public List<NewsItem> getNewsItems(String language) throws TException {
		System.out.println("getNewsItems");
		importFeeds();
		HashMap<String, List<NewsItem>> tLanguagesNewsItemsList = mLanguagesNewsItemsList;
		List<NewsItem> toReturn = null;
		if (tLanguagesNewsItemsList != null
				&& tLanguagesNewsItemsList.containsKey(language)) {
			toReturn = tLanguagesNewsItemsList.get(language);
		} else {
			toReturn = tLanguagesNewsItemsList.get(DEFAULT_LANGUAGE);
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
	public String getNewsItemContent(long newsItemId) throws TException {
		System.out.println("getNewsItemContent");
		importFeeds();
		HashMap<Long, String> tNewsContents = mNewsContents;
		String toReturn = null;
		if (tNewsContents != null && tNewsContents.containsKey(newsItemId)) {
			return tNewsContents.get(newsItemId);
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
	public List<Feed> getFeeds(String language) throws TException {
		System.out.println("getFeeds");
		importFeeds();
		HashMap<String, List<Feed>> tLanguagesFeedsList = mLanguagesFeedsList;
		if (tLanguagesFeedsList != null
				&& tLanguagesFeedsList.containsKey(language)) {
			return tLanguagesFeedsList.get(language);
		} else {
			return tLanguagesFeedsList.get(DEFAULT_LANGUAGE);
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