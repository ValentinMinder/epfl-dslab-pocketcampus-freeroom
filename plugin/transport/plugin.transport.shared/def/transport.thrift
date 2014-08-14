namespace java org.pocketcampus.plugin.transport.shared

struct TransportStation {
	// Don't use; for compatibility purposes only
	2: required i32 id;
	
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
	// required if not on foot
	4: optional TransportLine line;
	// Java timestamp (UNIX in milliseconds)
	// required if not on foot
	6: optional i64 departureTime;
	// e.g. platform 4
	7: optional string departurePosition;
	// Java timestamp (UNIX in milliseconds)
	// required if not on foot
	8: optional i64 arrivalTime;
	// e.g. platform 4
	9: optional string arrivalPosition;
	
	11: required bool foot;
	// required if on foot; in minutes
	12: optional i32 footDuration;
}

struct TransportTrip {
	// Don't use; for compatibility purposes only
	1: required string id;
	
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

struct TransportGeoPoint{
	1: double latitude;
	2: double longitude;
}

enum TransportResponseStatusCode{
	OK = 200, 
	NETWORK_ERROR = 404
}


struct TransportStationSearchResponse{
	1: optional list<TransportStation> stations;
	2: TransportResponseStatusCode statusCode;
}

struct TransportDefaultStationsResponse{
	1: optional list<TransportStation> stations;
	2: TransportResponseStatusCode statusCode;
}

struct TransportStationSearchRequest{
	1: string stationName;
	2: optional TransportGeoPoint geoPoint;
}

struct TransportTripSearchRequest{
	1: TransportStation fromStation;
	2: TransportStation toStation;
}

struct TransportTripSearchResponse{
	1: optional list<TransportTrip> trips;
	2: TransportResponseStatusCode statusCode;
}

service TransportService {
	list<TransportStation> autocomplete(1: string constraint);
	list<TransportStation> getLocationsFromNames(1: list<string> names);
	QueryTripsResult getTrips(1: string from; 2: string to);

// NEW APIs
	TransportStationSearchResponse searchForStationByName(1: TransportStationSearchRequest searchRequest);
	TransportDefaultStationsResponse getDefaultStations();
	TransportTripSearchResponse searchForTrips(1: TransportTripSearchRequest searchRequest);
}
