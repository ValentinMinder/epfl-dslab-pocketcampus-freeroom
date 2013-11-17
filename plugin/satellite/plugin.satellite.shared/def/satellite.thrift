namespace java org.pocketcampus.plugin.satellite.shared
namespace csharp org.pocketcampus.plugin.satellite.shared

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
  SMALL_BOTTLE = 2,
  LARGE_BOTTLE = 3
}

struct SatelliteBeer {
  1: required string name;
  2: required string breweryName;
  3: required string beerType;
  4: required string originCountry;
  5: required double alcoholRate;
  6: required double price;
  7: required string description;
  8: required bool beerOfTheMonth;
  9: required SatelliteBeerContainer container;
}

enum SatelliteAffluence {
  EMPTY = 0,
  MEDIUM = 1,
  CROWDED = 2,
  FULL = 3,
  CLOSED = 4,
  ERROR = 100
}

struct BeersResponse {
  1: required list<SatelliteBeer> beers;
}

struct AffluenceResponse {
  1: required SatelliteAffluence affluence;
}

service SatelliteService {
    // OLD STUFF
	Beer getBeerOfTheMonth();
	Affluence getAffluence();
	
  // NEW STUFF
  BeersResponse getBeers();
  AffluenceResponse getCurrentAffluence();
}