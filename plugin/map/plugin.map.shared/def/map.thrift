namespace java org.pocketcampus.plugin.map.shared

enum MapStatusCode { // NEW
        // The request was successful
        OK = 200,
        // A network error occured on the server
    NETWORK_ERROR = 404
}

struct MapLayer { //CHANGED
    1: required string name;
    // Name of the layer when querying on EPFL layers server. At least one of the two names for query is set.
    // May contain "{floor}"" which you must replace by the floor number you want to query
    2: optional string nameForQuery;
    3: optional string nameForQueryAllFloors;
}

struct MapLayersResponse { //NEW
    1: required MapStatusCode statusCode;
    2: required list<MapLayer> layers;
}

struct MapItem {
    1: required string title;
    2: optional string description; //CHANGED
    3: required double latitude;
    4: required double longitude;
    5: required i64 layerId;
    6: required i64 itemId;
    7: optional i32 floor;
    8: optional string category;
}

service MapService {
    MapLayersResponse getLayers(); // CHANGED
    list<MapItem> search(1: string query);
}