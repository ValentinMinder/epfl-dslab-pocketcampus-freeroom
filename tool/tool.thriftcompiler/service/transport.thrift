namespace java org.pocketcampus.plugin.transport.shared

/**
* Service definition for the Transport plugin.
*/

include "../include/common.thrift"

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



/*
enum TransportType {
	BUS;
	FOOT;
	TRAIN;
	BOAT; //I'M ON BOOOAT!
	PLANE
}

struct Connection {
  1: required i32 id;
  2: required string name;
  3: Operation op;
  4: optional string comment
}

struct Line {
	1:string line
}


struct Part {
	1: Location departure;
	2: Location arrival;
	3: list<Point> path;
	
	4: Line line;
	5: Location destination;
	6: date departureTime;
	7: string departurePosition;
	8: date arrivalTime;
	9: string arrivalPosition;
	10: list<Stop> intermediateStops;
	
	11:boolean foot;
	12: i32 min;
}
*/

service TransportService {
	list<Location> autocomplete(1:string constraint);
	/*string nextDepartures(2:string IDStation);
	string connections(1:string from; 2:string to);
	string connectionsFromStationsIDs(1: string fromID; 2:string toID);*/
}