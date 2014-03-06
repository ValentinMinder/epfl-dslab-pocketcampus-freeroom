package org.pocketcampus.plugin.news.server;

import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.lang.StringEscapeUtils;
import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;
import org.pocketcampus.platform.sdk.server.HttpClient;
import org.pocketcampus.platform.sdk.server.XElement;

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
	// All feed IDs
	private static final String[] FEED_IDS = { "mediacom", "enac", "sb", "ic", "cdh", "sti", "sv", "cdm" };
	// Charset used by RSS feeds
	private static final Charset RSS_CHARSET = Charset.forName("UTF-8");
	// Date format used by RSS feeds
	private static final DateTimeFormatter RSS_DATE_FORMAT = DateTimeFormat.forPattern("E, d MMM y HH:mm:ss Z").withLocale(Locale.ENGLISH);
	// RSS feed element names
	private static final String RSS_FEED_ELEMENT = "channel";
	private static final String RSS_FEED_NAME_ELEMENT = "title";
	private static final String RSS_FEED_ITEM_ELEMENT = "item";
	private static final String RSS_FEED_ITEM_TITLE_ELEMENT = "title";
	private static final String RSS_FEED_ITEM_LINK_ELEMENT = "link";
	private static final String RSS_FEED_ITEM_CONTENT_ELEMENT = "description";
	private static final String RSS_FEED_ITEM_DATE_ELEMENT = "pubDate";
	// Pattern that matches images in EPFL news articles
	private static final Pattern IMAGE_PATTERN = Pattern.compile("http://actu.epfl.ch/image/\\d+/(\\d+x\\d+).jpg");
	// Token for the size of EPFL news images, so that clients can pick their desired size
	private static final String IMAGE_SIZE_TOKEN = "{x}x{y}";

	private final HttpClient _client;

	public NewsSourceImpl(HttpClient client) {
		_client = client;
	}

	/** Gets all feeds for the specified language. */
	@Override
	public Feed[] getFeeds(String language) {
		List<Feed> feeds = new ArrayList<Feed>();
		for (String feedId : FEED_IDS) {
			String url = String.format(FEED_URL_FORMAT, feedId, language);

			XElement rootElem;
			try {
				String rss = _client.getString(url, RSS_CHARSET);
				rootElem = XElement.parse(rss);
			} catch (Exception e) {
				return null;
			}

			XElement channelElem = rootElem.child(RSS_FEED_ELEMENT);

			String feedName = channelElem.elementText(RSS_FEED_NAME_ELEMENT);
			boolean isMain = feedId.equals(MAIN_FEED_ID);

			Map<Integer, FeedItem> items = new LinkedHashMap<Integer, FeedItem>(); // LinkedHashMap keeps insertion order
			for (XElement itemElement : channelElem.children(RSS_FEED_ITEM_ELEMENT)) {
				String title = itemElement.elementText(RSS_FEED_ITEM_TITLE_ELEMENT);
				int id = title.hashCode();
				String link = itemElement.elementText(RSS_FEED_ITEM_LINK_ELEMENT);
				String dateString = itemElement.elementText(RSS_FEED_ITEM_DATE_ELEMENT);
				DateTime date = DateTime.parse(dateString, RSS_DATE_FORMAT);
				String content = itemElement.elementText(RSS_FEED_ITEM_CONTENT_ELEMENT);
				content = StringEscapeUtils.unescapeHtml(content);

				items.put(id, new FeedItem(id, title, link, date, getPictureUrl(content), content));
			}

			feeds.add(new Feed(feedName, isMain, items));
		}

		// Sort the feeds by name, but always put the main feed first.
		Collections.sort(feeds, new Comparator<Feed>() {
			@Override
			public int compare(Feed feed1, Feed feed2) {
				int result = Boolean.compare(feed1.isMain, feed2.isMain);
				if (result == 0) {
					return feed1.name.compareTo(feed2.name);
				}
				return result;
			}
		});

		return feeds.toArray(new Feed[feeds.size()]);
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