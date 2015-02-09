package org.pocketcampus.plugin.news.server;

import org.apache.commons.lang3.StringEscapeUtils;
import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;
import org.pocketcampus.platform.server.HttpClient;
import org.pocketcampus.platform.server.XElement;

import java.nio.charset.Charset;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Implementation of NewsSource using the EPFL RSS feeds.
 * 
 * @author Solal Pirelli <solal@pocketcampus.org>
 */
public final class NewsSourceImpl implements NewsSource {
	// Format of feed URLs; parameters are the feed ID and the language.
	private static final String FEED_URL_FORMAT = "http://actu.epfl.ch/feeds/rss/%s/%s/";
	// ID of the "main" feed, which contains the important news from other feeds
	private static final String MAIN_FEED_ID = "mediacom";
	// All feed IDs. If you add any, make sure you update FEED_NAMES as well.
	private static final String[] FEED_IDS = { "mediacom", "enac", "sb", "ic", "cdh", "sti", "sv", "cdm" };
	// The feed names, per language
	private static final Map<String, Map<String, String>> FEED_NAMES = new HashMap<String, Map<String, String>>();
	// All supported languages
	private static final Set<String> AVAILABLE_LANGUAGES = new HashSet<String>();
	// The default language
	private static final String DEFAULT_LANGUAGE = "en";
	// Charset used by RSS feeds
	private static final Charset RSS_CHARSET = Charset.forName("UTF-8");
	// Date format used by RSS feeds
	private static final DateTimeFormatter RSS_DATE_FORMAT = DateTimeFormat.forPattern("E, d MMM y HH:mm:ss Z").withLocale(Locale.ENGLISH);
	// RSS feed element names
	private static final String RSS_FEED_ELEMENT = "channel";
	private static final String RSS_FEED_ITEM_ELEMENT = "item";
	private static final String RSS_FEED_ITEM_TITLE_ELEMENT = "title";
	private static final String RSS_FEED_ITEM_LINK_ELEMENT = "link";
	private static final String RSS_FEED_ITEM_CONTENT_ELEMENT = "description";
	private static final String RSS_FEED_ITEM_DATE_ELEMENT = "pubDate";
	// Pattern that matches images in EPFL news articles
	private static final Pattern IMAGE_PATTERN = Pattern.compile("http://actu.epfl.ch/image/\\d+/(\\d+x\\d+).jpg");
	// Token for the size of EPFL news images, so that clients can pick their desired size
	private static final String IMAGE_SIZE_TOKEN = "{x}x{y}";

	static {
		AVAILABLE_LANGUAGES.add("fr");
		AVAILABLE_LANGUAGES.add("en");

		// Unfortunately, the feeds don't provide a good name (it's always "EPFL News")
		FEED_NAMES.put("fr", new HashMap<String, String>());
		FEED_NAMES.get("fr").put("mediacom", "Général");
		FEED_NAMES.get("fr").put("enac", "Architecture");
		FEED_NAMES.get("fr").put("sb", "Sciences de Base");
		FEED_NAMES.get("fr").put("ic", "Informatique & Communication");
		FEED_NAMES.get("fr").put("cdh", "Collège des Humanités");
		FEED_NAMES.get("fr").put("sti", "Sciences de l'Ingénieur");
		FEED_NAMES.get("fr").put("sv", "Sciences de la Vie");
		FEED_NAMES.get("fr").put("cdm", "Management de la Technologie");
		FEED_NAMES.put("en", new HashMap<String, String>());
		FEED_NAMES.get("en").put("mediacom", "General");
		FEED_NAMES.get("en").put("enac", "Architecture");
		FEED_NAMES.get("en").put("sb", "Basic Sciences");
		FEED_NAMES.get("en").put("ic", "Computer & Communication Sciences");
		FEED_NAMES.get("en").put("cdh", "Humanities College");
		FEED_NAMES.get("en").put("sti", "Engineering");
		FEED_NAMES.get("en").put("sv", "Life Sciences");
		FEED_NAMES.get("en").put("cdm", "Management of Technology");
	}

	private final HttpClient _client;

	public NewsSourceImpl(HttpClient client) {
		_client = client;
	}

	/** Gets all feeds for the specified language. */
	@Override
	public Feed[] getFeeds(String language) {
		if (!AVAILABLE_LANGUAGES.contains(language)) {
			language = DEFAULT_LANGUAGE;
		}

		List<Feed> feeds = new ArrayList<Feed>();
		for (String feedId : FEED_IDS) {
			String url = String.format(FEED_URL_FORMAT, feedId, language);

			XElement rootElem;
			try {
				String rss = _client.get(url, RSS_CHARSET);
				rootElem = XElement.parse(rss);
			} catch (Exception e) {
				return null;
			}

			XElement channelElem = rootElem.child(RSS_FEED_ELEMENT);

			String feedName = FEED_NAMES.get(language).get(feedId);
			boolean isMain = feedId.equals(MAIN_FEED_ID);

			Map<Integer, FeedItem> items = new LinkedHashMap<Integer, FeedItem>(); // LinkedHashMap keeps insertion order
			for (XElement itemElement : channelElem.children(RSS_FEED_ITEM_ELEMENT)) {
				String title = itemElement.child(RSS_FEED_ITEM_TITLE_ELEMENT).text();
				int id = getFeedItemId(title, feedId);
				String link = itemElement.child(RSS_FEED_ITEM_LINK_ELEMENT).text();
				String dateString = itemElement.child(RSS_FEED_ITEM_DATE_ELEMENT).text();
				DateTime date = DateTime.parse(dateString, RSS_DATE_FORMAT);
				String content = itemElement.child(RSS_FEED_ITEM_CONTENT_ELEMENT).text();
				content = StringEscapeUtils.unescapeHtml4(content);

				items.put(id, new FeedItem(id, title, link, date, getPictureUrl(content), content));
			}

			feeds.add(new Feed(feedId, feedName, isMain, items));
		}

		// Sort the feeds by name, but always put the main feed first.
		Collections.sort(feeds, new Comparator<Feed>() {
			@Override
			public int compare(Feed feed1, Feed feed2) {
				int result = Boolean.valueOf(feed2.isMain).compareTo(Boolean.valueOf(feed1.isMain)); // yes, the order is right
				if (result == 0) {
					return feed1.name.compareTo(feed2.name);
				}
				return result;
			}
		});

		return feeds.toArray(new Feed[feeds.size()]);
	}

	/** Computes an ID for a feed item from its title and the feed ID. */
	private static int getFeedItemId(String itemTitle, String feedID) {
		return itemTitle.hashCode() + feedID.hashCode();
	}

	/** Gets the picture URL of the specified item, given its content. */
	private static String getPictureUrl(String itemContent) {
		Matcher matcher = IMAGE_PATTERN.matcher(itemContent);
		if (matcher.find()) {
			return matcher.group(0).replace(matcher.group(1), IMAGE_SIZE_TOKEN);
		}
		return null;
	}
}