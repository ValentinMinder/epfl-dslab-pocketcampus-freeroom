namespace java org.pocketcampus.plugin.satellite.shared

// ---------
// OLD STUFF
// ---------

struct Beer {
	1: required i64 Id;
	2: required string name;
	3: required string description;
	4: optional double price;
	5: optional string pictureUrl;
}

enum Affluence {
	EMPTY;
	MEDIUM;
	CROWDED;
	FULL;
	CLOSED;
	ERROR;
}

// ---------
// NEW STUFF
// ---------

enum SatelliteBeerContainer {
  DRAFT = 1,
  BOTTLE = 2,
  LARGE_BOTTLE = 3
}

struct SatelliteBeer {
  1: required string name;
  2: required string breweryName;
  3: required string originCountry;
  4: required double alcoholRate;
  5: required double price;
  6: required string description;
}

struct SatelliteMenuPart {
  1: required list<SatelliteBeer> beersOfTheMonth;
  2: required map<string, list<SatelliteBeer>> beers;
}

enum SatelliteStatusCode {
  // The request completed successfully
  OK = 200,
  // An error occurred while reaching the Satellite website
  NETWORK_ERROR = 404
}

struct BeersResponse {
  // required if the request completed successfully
  1: optional map<SatelliteBeerContainer, SatelliteMenuPart> beerList;
  2: required SatelliteStatusCode statusCode;
}

service SatelliteService {
    // OLD STUFF
	Beer getBeerOfTheMonth();
	Affluence getAffluence();
	
  // NEW STUFF
  BeersResponse getBeers();
}
