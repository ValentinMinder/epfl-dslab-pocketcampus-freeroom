namespace java org.pocketcampus.plugin.map.shared

enum MapStatusCode {
        // The request was successful
        OK = 200,
        // A network error occured on the server
    NETWORK_ERROR = 404
}

struct MapLayer {
	1: required i64 layerId;
    2: required string name;
    // Name of the layer when querying on EPFL layers server. At least one of the two names for query is set.
    // May contain "{floor}"" which you must replace by the floor number you want to query
    3: optional string nameForQuery;
    4: optional string nameForQueryAllFloors;
}

struct MapLayersResponse {
    1: required MapStatusCode statusCode;
    2: required list<MapLayer> layers;
}

struct MapItem {
    1: required string title;
    2: optional string description;
    3: required double latitude;
    4: required double longitude;
    5: required i64 layerId; //DON'T USE, NOT RELATED TO MapLayer.layerId
    6: required i64 itemId; //DON'T USE
    7: optional i32 floor;
    8: optional string category;
}

service MapService {
    MapLayersResponse getLayers();
    list<MapItem> search(1: string query);
}