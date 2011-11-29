namespace java org.pocketcampus.plugin.transport.shared

/**
* Service definition for the Transport plugin.
*/

include "../include/common.thrift"

//To represent a date (milisec from January 1, 1970, 00:00:00 GMT)
typedef i64 timestamp

enum LocationType {
	ANY;
	STATION;
	POI;
	ADDRESS;
}

struct Location {
	1: LocationType type;
	2: i32 id;
	3: i32 lat;
	4: i32 lon;
	5: string place;
	6: string name;
}

enum Type{
	ADULT; CHILD; YOUTH; STUDENT; MILITARY; SENIOR; DISABLED;
}

struct Fare{
	1: required string network;
	2: required Type type;
	3: required string currency;
	4: required double fare;
	5: required string unitName;
	6: required string units;
}

struct Point{
	1: required i32 lat;
	2: required i32 lon;
}

typedef i32 Integer
struct Line{
	1: required string label;
	2: required list<string> colors;
}


struct Stop{
	1: required Location location;
	2: required string position;
	3: required timestamp time;
}

struct Part {
	1: required Location departure;
	2: required Location arrival;
	3: required list<Point> path;
	
	4: optional Line line;
	5: optional Location destination;
	6: optional timestamp departureTime;
	7: optional string departurePosition;
	8: optional timestamp arrivalTime;
	9: optional string arrivalPosition;
	10: optional list<Stop> intermediateStops;
	
	11: optional bool foot;
	12: optional i32 min;
}

struct Connection 
{
	1: required string id;
	2: optional string link;
	3: required timestamp departureTime;
	4: required timestamp arrivalTime;
	5: required Location from;
	6: required Location to;
	7: optional list<Part> parts;
	8: optional list<Fare> fares;
}

enum FareType{
	ADULT; CHILD; YOUTH; STUDENT; MILITARY; SENIOR; DISABLED;
}

struct Fare{
	1: required string network;
	2: required FareType type;
	3: required string currency;
	4: required double fare;
	5: required string unitName;
	6: required string units;
}



struct Departure{
	1: required timestamp plannedTime;
	2: required timestamp predictedTime;
	3: required string line;
	4: required list<i32> lineColors;
	5: required string lineLink;
	6: required string position;
	7: required i32 destinationId;
	8: required string destination;
	9: required string message;
}




struct GetConnectionDetailsResult{
	1: required timestamp currentDate;
	2: required Connection connection;
}



struct LineDestination{
	1: required string line;
	2: required list<string> lineColors;
	3: required i32 destinationId;
	4: required string destination;
}




enum NearbyStatus	{
		OK; INVALID_STATION; SERVICE_DOWN;
}
struct NearbyStationsResult
{
	1: required NearbyStatus status;
	2: required list<Location> stations;
}



enum Status{
		sOK; AMBIGUOUS; TOO_CLOSE; UNRESOLVABLE_ADDRESS; NO_CONNECTIONS; INVALID_DATE; SERVICE_DOWN;
	}
struct QueryConnectionsResult{
	1: optional list<Location> ambiguousFrom;
	2: optional list<Location> ambiguousVia;
	3: optional list<Location> ambiguousTo;

	4: optional string queryUri;
	5: required Location from;
	6: optional Location via;
	7: required Location to;
	8: optional string context;
	9: required list<Connection> connections;
}

struct StationDepartures
{
	1: required Location location;
	2: required list<Departure> departures;
	3: required list<LineDestination> lines;
}

struct QueryDepartureResult{
	1: required NearbyStatus status;
	2: required list<StationDepartures> stationDepartures
}

struct RailwayNode{
 	
 	1: required map<string, string> tags_;
	2: required i32 lat_;
	3: required i32 lon_;
	4: required i32 ref_;
	5: required i32 num_;
	6: required double distFromPrevious_;
	7: required i32 previousRef_;
	8: required i32 uicRef_;
 }
struct RailwayNd{
 	1: required i32 num;
 	2: required i32 ref;
}

struct RailwayWay{
 	1: required set<RailwayNd> nds;
 	2: required i32 num;
 }
 
struct RailwayMember{
	1: required string type_;
	2: required i32 ref_;
	3: required string role_;
	4: required i32 num_;
} 

struct Railway{
	1: required map<i32, RailwayNode> nodes_;
	2: required map<i32, RailwayWay> ways_;
	3: required set<RailwayMember> members_;
	4: required set<RailwayNode> railway_;
	5: required map<i32, RailwayNode> stopNodes_;
}

service TransportService {
	list<Location> autocomplete(1:string constraint);
	list<Location> getLocationsFromIDs(1: list<i32> ids);
	list<Location> getLocationsFromNames(1: list<string> names);
	QueryDepartureResult nextDepartures(2:string IDStation);
	QueryConnectionsResult connections(1:string from; 2:string to);
	QueryConnectionsResult connectionsFromStationsIDs(1: string fromID; 2:string toID);
}