namespace java org.pocketcampus.plugin.events.shared

const i64 CONTAINER_EVENT_ID = -1;

const map<i32, string> EVENTS_CATEGS = {
	-2: "Favorites";
	-1: "Featured events"; 
	0: "All"; 
	1: "Conferences - Seminars";
	2: "Meetings management tips";
	4: "Miscellaneous";
	5: "Exhibitions";
	6: "Movies";
	7: "Celebrations";
	8: "Inaugural lessons - Lessons of honor";
	9: "Cultural events";
	10: "Sporting events";
	11: "Dating EPFL - economy";
	12: "Thesis defenses";
	13: "Academic calendar";
}

enum EventsCategories {
	ALL = 0;
	CONF_SEMINAR = 1;
	MEET_MGMT_TIPS = 2;
	MISC = 4;
	EXHIBIT = 5;
	MOVIES = 6;
	CELEB = 7;
	INAUG_HONOR = 8;
	CULTURAL = 9;
	SPORT = 10;
	DATING_EPFL_ECONOMY = 11;
	THESIS_DEFENSE = 12;
	ACADEMIC_CAL = 13;
}

const map<string, string> EVENTS_TAGS = {
	"epfl": "École Polytechnique Fédérale de Lausanne";
	"enac": "Architecture, Civil and Environmental Engineering";
	"sb": "Basic Sciences";
	"sti": "Engineering";
	"ic": "Computer & Communication Sciences";
	"sv": "Life Sciences";
	"cdm": "Management of Technology";
	"cdh": "College of Humanities";
	"associations": "Associations";
}

enum EventsTags {
	EPFL;
	ENAC;
	SB;
	STI;
	IC;
	SV;
	CDM;
	CDH;
	ACADEMIC_CALENDAR;
	ASSOCIATIONS;
}

const map<i32, string> EVENTS_PERIODS = {
	1: "One day";
	2: "Two days";
	7: "One week";
	14: "Two weeks";
	30: "One month";
	180: "Six months";
	365: "One year";
}

enum EventsPeriods {
	ONE_DAY = 1;
	TWO_DAYS = 2;
	ONE_WEEK = 7;
	TWO_WEEKS = 14;
	ONE_MONTH = 30;
	SIX_MONTHS = 180;
	ONE_YEAR = 365;
}


struct EventItem {
	1: required i64 eventId;
	
	2: optional i64 startDate;
	3: optional i64 endDate;
	4: optional bool fullDay;
	5: optional string eventPicture;
	6: optional string eventTitle;
	7: optional string eventPlace;
	8: optional string eventSpeaker;
	9: optional string eventDetails;
	10: optional string eventThumbnail;
	12: optional string eventUri;
	13: optional string vcalUid;
	16: optional string locationHref;
	17: optional string detailsLink;
	11: optional string secondLine; // if set, overrides the second line in list view
	18: optional string timeSnippet; // if set, overrides time snippet
	21: optional bool hideTitle; // if set, hides the title from event details view
	22: optional bool hideThumbnail; // if set, hides thumbnail from event details view
	23: optional bool hideEventInfo; // if set, hides the block containing date, time, location, speaker, link, etc 
	
	14: optional i32 eventCateg;
	15: optional list<string> eventTags;
	
	30: optional list<i64> childrenPools;
	31: optional i64 parentPool;
}

struct EventPool {
	1: required i64 poolId;
	
	5: optional string poolPicture;
	6: optional string poolTitle;
	7: optional string poolPlace;
	9: optional string poolDetails;
	10: optional bool disableStar;
	11: optional bool disableFilterByCateg;
	12: optional bool disableFilterByTags;
	13: optional bool enableScan;
	14: optional string noResultText;
	16: optional bool refreshOnBack;
	19: optional bool sendStarredItems;
	21: optional string overrideLink;
	
	15: optional list<i64> childrenEvents;
	17: optional i64 parentEvent;
}

struct EventItemRequest {
	1: required i64 eventItemId;
	2: optional string userToken; // deprecated, use userTickets
	3: optional list<string> userTickets;
	
	5: optional string lang;
}

struct EventPoolRequest {
	1: required i64 eventPoolId;
	2: optional string userToken; // deprecated, use userTickets
	3: optional list<string> userTickets;
	
	4: optional list<i64> starredEventItems;
	
	5: optional string lang;
	6: optional i32 period; // in days, deprecated, use periodInHours
	8: optional i32 periodInHours; // if set, will override period
	7: optional bool fetchPast;
}

struct EventItemReply {
	1: required i32 status;
	2: optional EventItem eventItem;
	3: optional map<i64, EventPool> childrenPools;
	
	5: optional map<i32, string> categs;
	6: optional map<string, string> tags;
}

struct EventPoolReply {
	1: required i32 status;
	2: optional EventPool eventPool;
	3: optional map<i64, EventItem> childrenItems;
	
	5: optional map<i32, string> categs;
	6: optional map<string, string> tags;
}

struct ExchangeRequest {
	2: required string exchangeToken;
	
	1: optional string userToken;
	3: optional list<string> userTickets;
}

struct ExchangeReply {
	1: required i32 status;
}

struct SendEmailRequest {
	4: required i64 eventPoolId;
	1: required list<i64> starredEventItems;
	2: optional list<string> userTickets;
	3: optional string emailAddress;
	
	5: optional string lang;
}

struct SendEmailReply {
	1: required i32 status;
}

struct AdminSendRegEmailRequest {
	1: required string templateId;
	2: optional list<string> sendOnlyTo;
}

struct AdminSendRegEmailReply {
	1: required i32 status;
}


service EventsService {
	EventItemReply getEventItem(1: EventItemRequest iRequest);
	EventPoolReply getEventPool(1: EventPoolRequest iRequest);
	ExchangeReply exchangeContacts(1: ExchangeRequest iRequest); // deprecated
	SendEmailReply sendStarredItemsByEmail(1: SendEmailRequest iRequest);
	AdminSendRegEmailReply adminSendRegistrationEmail(1: AdminSendRegEmailRequest iRequest);
}
