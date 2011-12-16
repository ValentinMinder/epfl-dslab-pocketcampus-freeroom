namespace java org.pocketcampus.plugin.events.shared

include "../include/common.thrift"

typedef i32 int
typedef i64 timestamp

struct EventsItem {
	1: required common.Id eventsItemId;
	2: required string title;
	3: required string content;
	4: required string link;
	5: required string feed;
	6: required string organizer;
	7: required string startTime;
	8: required timestamp startDate;
	9: required timestamp endDate;
}

struct Feed {
	1: required common.Id feedId;
	2: required string title;
	3: required string link;
	4: required string description;
	5: required list<EventsItem> items;
}

service EventsService {
	list<EventsItem> getEventsItems(1: string language);
	map<string, string> getFeedUrls(1: string language);
	list<Feed> getFeeds(1: string language);
}