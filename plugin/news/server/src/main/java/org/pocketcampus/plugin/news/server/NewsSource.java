package org.pocketcampus.plugin.news.server;

import org.joda.time.DateTime;

import java.util.Map;

/**
 * News source.
 * 
 * @author Solal Pirelli <solal@pocketcampus.org>
 */
public interface NewsSource {
	/** Gets all feeds in the specified language. */
	Feed[] getFeeds(String language);

	/** News feed */
	public static class Feed {
		public final String id;
		public final String name;
		public final boolean isMain;
		public final Map<Integer, FeedItem> items;

		public Feed(String id, String name, boolean isMain, Map<Integer, FeedItem> items) {
			this.id = id;
			this.name = name;
			this.isMain = isMain;
			this.items = items;
		}
	}

	/** News feed item */
	public static class FeedItem {
		public final int id;
		public final String title;
		public final String link;
		public final DateTime publishDate;
		public final String imageUrl;
		public final String content;

		public FeedItem(int id, String title, String link, DateTime publishDate, String imageUrl, String content) {
			this.id = id;
			this.title = title;
			this.link = link;
			this.publishDate = publishDate;
			this.imageUrl = imageUrl;
			this.content = content;
		}
	}
}