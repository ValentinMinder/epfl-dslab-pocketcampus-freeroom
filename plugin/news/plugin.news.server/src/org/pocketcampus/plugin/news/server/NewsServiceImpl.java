package org.pocketcampus.plugin.news.server;

import org.apache.thrift.TException;
import org.joda.time.Duration;
import org.pocketcampus.platform.server.CachingProxy;
import org.pocketcampus.platform.server.HttpClientImpl;
import org.pocketcampus.plugin.news.shared.*;

import java.util.*;

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

        List<NewsFeed> returnedFeeds = new ArrayList<>();
        for (NewsSource.Feed feed : feeds) {
            if (feed.isMain && !request.isGeneralFeedIncluded()) {
                continue;
            }

            List<NewsFeedItem> returnedItems = new ArrayList<>();
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


    @Override
    @Deprecated
    public List<NewsItem> getNewsItems(String language) throws TException {
        List<NewsItem> items = new ArrayList<>();
        Set<String> titles = new HashSet<>();
        for (NewsSource.Feed feed : _source.getFeeds(language)) {
            for (Map.Entry<Integer, NewsSource.FeedItem> entry : feed.items.entrySet()) {
                NewsSource.FeedItem item = entry.getValue();
                if (!titles.contains(item.title)) {
                    items.add(new NewsItem(entry.getKey(), item.title, item.link, feed.name, item.publishDate.getMillis()));
                    titles.add(item.title);
                }
            }
        }
        Collections.sort(items, new Comparator<NewsItem>() {
            @Override
            public int compare(NewsItem o1, NewsItem o2) {
                return -Long.compare(o1.getPubDate(), o2.getPubDate());
            }
        });
        return items;
    }

    @Override
    @Deprecated
    public String getNewsItemContent(long newsItemId) throws TException {
        try {
            String content = null;

            // The old protocol didn't include the language, so let's try both FR and EN...
            NewsFeedItemContentResponse frContent = getFeedItemContent(new NewsFeedItemContentRequest("fr", (int) newsItemId));
            if (frContent.getStatusCode() == NewsStatusCode.OK) {
                content = frContent.getContent().getContent();
            }

            NewsFeedItemContentResponse enContent = getFeedItemContent(new NewsFeedItemContentRequest("en", (int) newsItemId));
            if (enContent.getStatusCode() == NewsStatusCode.OK) {
                content = enContent.getContent().getContent();
            }

            if (content != null) {
                // Code borrowed from the old implementation, to remain compatible.
                content = content.replaceAll("<img[^>]+>", "");
                content = content.replaceAll("(&nbsp;)+", "");
                content = content.replaceAll("(<strong>)+", "<b>");
                content = content.replaceAll("(</strong>)+", "</b>");
                content = content.replaceAll("((<br />)\n)+", "\n<br />");
                content = content.replaceAll("(<p>(&nbsp;)+</p>)+", "");

                int carriageReturn = content.indexOf("<br />");
                if (carriageReturn != -1) {
                    String firstParagraph = content.substring(0, carriageReturn);
                    String rest = content.substring(carriageReturn, content.length());
                    content = "<b>" + firstParagraph + "</b>" + rest;
                }

                return content;
            }
        } catch (Exception e) {
            throw new TException("An error occurred.", e);
        }
        throw new TException("No content found.");
    }

    @Override
    @Deprecated
    public Map<String, String> getFeedUrls(String language) throws TException {
        final Map<String, String> feedUrls = new HashMap<>();
        if (language.equals("fr")) {
            feedUrls.put("EPFL Toutes les Nouvelles", "http://actu.epfl.ch/feeds/rss/mediacom/fr/");
            feedUrls.put("EPFL ENAC", "http://actu.epfl.ch/feeds/rss/enac/fr/");
            feedUrls.put("EPFL Sciences de Base", "http://actu.epfl.ch/feeds/rss/sb/fr/");
            feedUrls.put("EPFL Informatique et Communication", "http://actu.epfl.ch/feeds/rss/ic/fr/");
            feedUrls.put("EPFL Collège des Humanités", "http://actu.epfl.ch/feeds/rss/cdh/fr/");
            feedUrls.put("EPFL Sciences et Techniques de l'Ingénieur", "http://actu.epfl.ch/feeds/rss/sti/fr/");
            feedUrls.put("EPFL Sciences de la Vie", "http://actu.epfl.ch/feeds/rss/sv/fr/");
            feedUrls.put("EPFL Management de la Technologie", "http://actu.epfl.ch/feeds/rss/cdm/fr/");
        } else {
            feedUrls.put("EPFL All news", "http://actu.epfl.ch/feeds/rss/mediacom/en/");
            feedUrls.put("EPFL Architecture, Civil and Environmental Engineering", "http://actu.epfl.ch/feeds/rss/enac/en/");
            feedUrls.put("EPFL Basic sciences", "http://actu.epfl.ch/feeds/rss/sb/en/");
            feedUrls.put("EPFL Computer and Communication Sciences", "http://actu.epfl.ch/feeds/rss/ic/en/");
            feedUrls.put("EPFL College of Humanities", "http://actu.epfl.ch/feeds/rss/cdh/en/");
            feedUrls.put("EPFL Engineering", "http://actu.epfl.ch/feeds/rss/sti/en/");
            feedUrls.put("EPFL Life Sciences", "http://actu.epfl.ch/feeds/rss/sv/en/");
            feedUrls.put("EPFL Management of Technology", "http://actu.epfl.ch/feeds/rss/cdm/en/");
        }

        return feedUrls;
    }

    @Override
    @Deprecated
    public List<Feed> getFeeds(String language) throws TException {
        List<Feed> feeds = new ArrayList<>();
        for (NewsSource.Feed feed : _source.getFeeds(language)) {
            Feed oldFeed = new Feed(0, feed.name, "http://actu.epfl.ch/search/" + feed.id, feed.name, new ArrayList<NewsItem>());
            for (NewsSource.FeedItem item : feed.items.values()) {
                oldFeed.addToItems(new NewsItem(item.id, item.title, item.link, feed.name, item.publishDate.getMillis()));
            }
            feeds.add(oldFeed);
        }
        return feeds;
    }
}