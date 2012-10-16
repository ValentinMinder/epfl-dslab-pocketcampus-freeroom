namespace java org.pocketcampus.plugin.news.shared

include "../../../../platform/sdk/platform.sdk.shared/def/common.thrift"

typedef i32 int
typedef i64 timestamp

struct NewsItem {
	1: required common.Id newsItemId;
	2: required string title;
	3: required string link;
	4: required string feed;
	5: required timestamp pubDate;
	6: optional string imageUrl;
}

struct Feed {
	1: required common.Id feedId;
	2: required string title;
	3: required string link;
	4: required string description;
	5: required list<NewsItem> items;
}

service NewsService {
	list<NewsItem> getNewsItems(1: string language);
	string getNewsItemContent(1: common.Id newsItemId);
	map<string, string> getFeedUrls(1: string language);
	list<Feed> getFeeds(1: string language);
}