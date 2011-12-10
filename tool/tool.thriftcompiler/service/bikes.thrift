namespace java org.pocketcampus.plugin.bikes.shared

include "../include/common.thrift"

typedef i32 int

struct BikeEmplacement {
	1: required int numberOfEmptySpaces;
	2: required int numberOfAvailableBikes;
	3: required double latitude;
	4: required double longitude;
	5: required string name;
	
	
}

exception WebParseException {
	1: string message;
}

service BikeService {
	list<BikeEmplacement> getBikeStations() throws (1: WebParseException wpe);
}