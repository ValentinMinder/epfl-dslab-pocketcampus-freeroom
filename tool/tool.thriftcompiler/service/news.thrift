namespace java org.pocketcampus.plugin.news.shared

include "../include/common.thrift"

typedef i32 int
typedef i64 timestamp

struct NewsItem {
	1: required common.Id id;
	2: required string title;
	3: required string description;
	4: required string link;
	5: required string pubDate;
	6: required timestamp pubDateDate;
	7: required string imageUrl;
}

struct Feed {
	1: required common.Id id;
	2: required string title;
	3: required string link;
	4: required string description;
	5: required list<NewsItem> items;
}

service NewsService {
	list<NewsItem> getNewsItems(1: list<string> feedUrls);
}