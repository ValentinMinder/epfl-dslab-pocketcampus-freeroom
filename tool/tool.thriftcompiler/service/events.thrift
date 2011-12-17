namespace java org.pocketcampus.plugin.events.shared

include "../include/common.thrift"

typedef i32 int
typedef i64 timestamp

struct EventsItem {
	1: required common.Id eventsItemId;
	2: required string title;
	3: required string link;
	4: required string content;
	5: required string urlref;
	6: required timestamp startDate;
	7: required timestamp endDate;
	8: required string startTime;
	9: required string speaker;
	10: required string contact;
	11: required string language;
	12: required string audience;
	13: required string expectedPeople;
	14: required string location;
	15: required string room;
	16: required string category;
	17: required string organizer;
	18: required string shorttitle;
	19: required string feed;
}

struct Feed {
	1: required common.Id feedId;
	2: required string title;
	3: required string link;
	4: required string description;
	5: required list<EventsItem> items;
}

service EventsService {
	list<EventsItem> getEventsItems(1: string language, 2: list<Feed> feedsToGet);
	map<string, string> getFeedUrls(1: string language);
	list<Feed> getFeeds(1: string language);
}