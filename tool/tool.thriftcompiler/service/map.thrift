namespace java org.pocketcampus.plugin.map.shared

include "../include/common.thrift"

typedef i32 int

struct MapLayer {
	1: required string name;
	2: string drawableUrl;
	3: required string externalId;
	4: required int pluginInternalId;
	5: required int cacheInSeconds;
	6: required bool displayable;
}

exception WebParseException {
	1: string message;
}

service MapService {
	list<MapLayer> getLayers();
}