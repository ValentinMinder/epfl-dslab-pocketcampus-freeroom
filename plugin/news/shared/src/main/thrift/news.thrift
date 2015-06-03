namespace java org.pocketcampus.plugin.news.shared

// OLD STUFF - DO NOT TOUCH
struct NewsItem {
	1: required i64 newsItemId;
	2: required string title;
	3: required string link;
	4: required string feed;
	5: required i64 pubDate;
	6: optional string imageUrl;
}
struct Feed {
	1: required i64 feedId;
	2: required string title;
	3: required string link;
	4: required string description;
	5: required list<NewsItem> items;
}

// NEW STUFF
enum NewsStatusCode {
    // The request was successful
    OK = 200,
    // The requested ID does not correspond to anything
    INVALID_ID = 400,
    // A network error occured on the server
    NETWORK_ERROR = 404,
}

struct NewsFeedItem {
    // The item ID, used to get its contents
    1: required i32 itemId;
    // The item's title
    2: required string title;
    // The publication date of the item (milliseconds since Jan. 1, 1970)
    3: required i64 date;
    // An URL to the news item's picture, if there is any
    // Contains {x} and {y} tokens to change its size
    4: optional string imageUrl;
}

struct NewsFeed {
    // The feed's name
    1: required string name;
    // The feed items
    2: required list<NewsFeedItem> items;
    // The feed's ID, which is language-independent (useful to store a list of feeds on a client)
    3: required string feedId;
}

struct NewsFeedItemContent {
    // The name of the feed the item belongs to
    1: required string feedName;
    // The item's title
    2: required string title;
    // An URL to view the news item in a browser
    3: required string link;
    // The item's content, as HTML
    4: required string content;    
    // An URL to the news item's picture, if there is any
    // Contains {x} and {y} tokens to change its size
    5: optional string imageUrl;
}

struct NewsFeedsRequest {
    // The language the feeds should be in ("fr" or "en")
    1: required string language;
    // Whether to include the "all news" feed
    2: required bool generalFeedIncluded;
}

struct NewsFeedsResponse {
    // The response status
    1: required NewsStatusCode statusCode;
    // The list of feeds (empty if status is not OK)
    2: required list<NewsFeed> feeds;
}

struct NewsFeedItemContentRequest {
    // The language the content should be in ("fr" or "en")
    1: required string language;
    // The ID of the item whose content is requested
    2: required i32 itemId;
}

struct NewsFeedItemContentResponse {
    // The response status
    1: required NewsStatusCode statusCode;
    // The item's content
    2: optional NewsFeedItemContent content;
}


service NewsService {
	// OLD STUFF - DO NOT TOUCH
	list<NewsItem> getNewsItems(1: string language);
	string getNewsItemContent(1: i64 newsItemId);
	map<string, string> getFeedUrls(1: string language);
	list<Feed> getFeeds(1: string language);
	
	// NEW STUFF
	// Gets all available feeds
    NewsFeedsResponse getAllFeeds(1: NewsFeedsRequest request);
    // Gets the content of a feed item
    NewsFeedItemContentResponse getFeedItemContent(1: NewsFeedItemContentRequest request);
}
