namespace java org.pocketcampus.plugin.news.shared
namespace csharp org.pocketcampus.plugin.news.shared

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

service NewsService {
	list<NewsItem> getNewsItems(1: string language);
	string getNewsItemContent(1: i64 newsItemId);
	map<string, string> getFeedUrls(1: string language);
	list<Feed> getFeeds(1: string language);
}