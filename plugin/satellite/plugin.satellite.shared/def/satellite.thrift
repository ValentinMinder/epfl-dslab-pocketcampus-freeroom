namespace java org.pocketcampus.plugin.satellite.shared

include "../../../../platform/sdk/platform.sdk.shared/def/common.thrift"

//To represent a date (milisec from January 1, 1970, 00:00:00 GMT)
typedef i64 timestamp

struct Beer {
	1: required common.Id Id;
	2: required string name;
	3: required string description;
	4: optional double price;
	5: optional string pictureUrl;
}

struct Sandwich {
	1: required common.Id Id;
	3: required string name;
}

enum Affluence {
	EMPTY;
	MEDIUM;
	CROWDED;
	FULL;
	CLOSED;
	ERROR;
}

struct Event {
	1: required common.Id Id;
	2: required string title;
	3: required string description;
	4: required timestamp date;
	5: required double price;
}

service SatelliteService {
	Beer getBeerOfTheMonth();
	list<Beer> getAllBeers();
	list<Sandwich> getSatSandwiches();
	Affluence getAffluence();
	list<Event> getNextEvents();
}