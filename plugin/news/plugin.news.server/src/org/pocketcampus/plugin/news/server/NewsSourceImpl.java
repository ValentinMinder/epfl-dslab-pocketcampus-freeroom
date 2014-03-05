package org.pocketcampus.plugin.news.server;

import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.lang.StringEscapeUtils;
import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;
import org.pocketcampus.platform.sdk.server.HttpClient;
import org.pocketcampus.platform.sdk.server.XElement;

public final class NewsSourceImpl implements NewsSource {
	private static final String FEED_FORMAT = "http://actu.epfl.ch/feeds/rss/%s/%s/";
	private static final String MAIN_FEED_ID = "mediacom";
	private static final String[] FEED_IDS = { "mediacom", "enac", "sb", "ic", "cdh", "sti", "sv", "cdm" };
	private static final Charset RSS_CHARSET = Charset.forName("UTF-8");
	private static final DateTimeFormatter RSS_DATE_FORMAT = DateTimeFormat.forPattern("E, d M y H:m:s Z");
	private static final String RSS_FEED_ELEMENT = "channel";
	private static final String RSS_FEED_NAME_ELEMENT = "title";
	private static final String RSS_FEED_ITEM_ELEMENT = "item";
	private static final String RSS_FEED_ITEM_TITLE_ELEMENT = "title";
	private static final String RSS_FEED_ITEM_LINK_ELEMENT = "link";
	private static final String RSS_FEED_ITEM_CONTENT_ELEMENT = "description";
	private static final String RSS_FEED_ITEM_DATE_ELEMENT = "pubDate";
	private static final Pattern IMAGE_PATTERN = Pattern.compile("http://actu.epfl.ch/image/\\d+/(\\d+x\\d+).jpg");
	private static final String IMAGE_SIZE_TOKEN = "{x}x{y}";

	private final HttpClient _client;

	public NewsSourceImpl(HttpClient client) {
		_client = client;
	}

	@Override
	public Feed[] getFeeds(String language) {
		List<Feed> feeds = new ArrayList<Feed>();
		for (String feedId : FEED_IDS) {
			String url = String.format(FEED_FORMAT, feedId, language);

			String rss = null;
			try {
				rss = _client.getString(url, RSS_CHARSET);
			} catch (Exception e) {
				return null;
			}

			XElement rootElem = XElement.parse(rss);
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

				items.put(id, new FeedItem(id, title, link, date, getPictureUrl(content), sanitize(content)));
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

		return (Feed[]) feeds.toArray();
	}

	private static String getPictureUrl(String itemContent) {
		Matcher matcher = IMAGE_PATTERN.matcher(itemContent);
		if (matcher.matches()) {
			return matcher.group(0).replace(matcher.group(1), IMAGE_SIZE_TOKEN);
		}
		return null;
	}

	private static String sanitize(String itemContent) {
		// TODO: what?
		return itemContent;
	}
}