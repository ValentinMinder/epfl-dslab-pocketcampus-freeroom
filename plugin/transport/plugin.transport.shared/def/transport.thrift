namespace java org.pocketcampus.plugin.transport.shared

struct TransportStation {
	// convert lat/lon to double by dividing by 1000000
	3: required i32 latitude;
	4: required i32 longitude;
	6: required string name;
}

struct TransportLine {
	1: required string name;
	// must be there as long as old clients exist, since it's required
	2: required list<string> _UNUSED;
}

struct TransportConnection {
	1: required TransportStation departure;
	2: required TransportStation arrival;
	4: required TransportLine line;
	// Java timestamp (UNIX in milliseconds)
	6: required i64 departureTime;
	// e.g. platform 4
	7: optional string departurePosition;
	8: required i64 arrivalTime;
	9: optional string arrivalPosition;
}

struct TransportTrip {
	1: required string _UNUSED;
	3: required i64 departureTime;
	4: required i64 arrivalTime;
	5: required TransportStation from;
	6: required TransportStation to;
	7: required list<TransportConnection> parts;
}

struct QueryTripsResult {
	5: required TransportStation from;
	7: required TransportStation to;
	9: required list<TransportTrip> connections;
}

service TransportService {
	list<TransportStation> autocomplete(1: string constraint);
	list<TransportStation> getLocationsFromNames(1: list<string> names);
	QueryTripsResult getTrips(1: string from; 2: string to);
}