package org.pocketcampus.plugin.news.server;

import org.apache.thrift.TException;
import org.joda.time.Duration;
import org.pocketcampus.platform.server.CachingProxy;
import org.pocketcampus.platform.server.HttpClientImpl;
import org.pocketcampus.plugin.news.shared.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * News service.
 * 
 * @author Solal Pirelli <solal@pocketcampus.org>
 */
public final class NewsServiceImpl implements NewsService.Iface {
	private static final Duration CACHE_DURATION = Duration.standardHours(1);

	private final NewsSource _source;

	public NewsServiceImpl(NewsSource source) {
		_source = source;
	}

	public NewsServiceImpl() {
		this(CachingProxy.create(new NewsSourceImpl(new HttpClientImpl()), CACHE_DURATION, false));
	}

	@Override
	public NewsFeedsResponse getAllFeeds(NewsFeedsRequest request) throws TException {
		NewsSource.Feed[] feeds = _source.getFeeds(request.getLanguage());

		if (feeds == null) {
			return new NewsFeedsResponse(NewsStatusCode.NETWORK_ERROR, new ArrayList<NewsFeed>());
		}

		List<NewsFeed> returnedFeeds = new ArrayList<NewsFeed>();
		for (NewsSource.Feed feed : feeds) {
			if (feed.isMain && !request.isGeneralFeedIncluded()) {
				continue;
			}

			List<NewsFeedItem> returnedItems = new ArrayList<NewsFeedItem>();
			for (NewsSource.FeedItem item : feed.items.values()) {
				NewsFeedItem returnedItem = new NewsFeedItem(item.id, item.title, item.publishDate.getMillis());
				if (item.imageUrl != null) {
					returnedItem.setImageUrl(item.imageUrl);
				}
				returnedItems.add(returnedItem);
			}

			returnedFeeds.add(new NewsFeed(feed.name, returnedItems, feed.id));
		}

		return new NewsFeedsResponse(NewsStatusCode.OK, returnedFeeds);
	}

	@Override
	public NewsFeedItemContentResponse getFeedItemContent(NewsFeedItemContentRequest request) throws TException {
		NewsSource.Feed[] feeds = _source.getFeeds(request.getLanguage());

		if (feeds == null) {
			return new NewsFeedItemContentResponse(NewsStatusCode.NETWORK_ERROR);
		}

		for (NewsSource.Feed feed : feeds) {
			NewsSource.FeedItem item = feed.items.get(request.getItemId());
			if (item != null) {
				NewsFeedItemContent returnedContent = new NewsFeedItemContent(feed.name, item.title, item.link, item.content);

				if (item.imageUrl != null) {
					returnedContent.setImageUrl(item.imageUrl);
				}

				return new NewsFeedItemContentResponse(NewsStatusCode.OK).setContent(returnedContent);
			}
		}

		return new NewsFeedItemContentResponse(NewsStatusCode.INVALID_ID);
	}

	// OLD STUFF - DO NOT TOUCH
	private org.pocketcampus.plugin.news.server.old.NewsServiceImpl _oldService = new org.pocketcampus.plugin.news.server.old.NewsServiceImpl();

	@Override
	public List<NewsItem> getNewsItems(String language) throws TException {
		return _oldService.getNewsItems(language);
	}

	@Override
	public String getNewsItemContent(long newsItemId) throws TException {
		return _oldService.getNewsItemContent(newsItemId);
	}

	@Override
	public Map<String, String> getFeedUrls(String language) throws TException {
		return _oldService.getFeedUrls(language);
	}

	@Override
	public List<Feed> getFeeds(String language) throws TException {
		return _oldService.getFeeds(language);
	}
}