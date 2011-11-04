namespace java org.pocketcampus.plugin.bikes.shared

include "../include/common.thrift"

typedef i32 int

struct BikeEmplacement {
	1: required int empty;
	2: required int availableQuantity;
	3: required double geoLat;
	4: required double geoLng;
	5: required string designation;
	
	
}

exception WebParseException {
	1: string message;
}

service BikeService {
	list<BikeEmplacement> getAvailableBikes() throws (1: WebParseException wpe);
}