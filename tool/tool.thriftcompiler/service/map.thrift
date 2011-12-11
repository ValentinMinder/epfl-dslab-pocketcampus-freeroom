namespace java org.pocketcampus.plugin.map.shared

include "../include/common.thrift"

typedef i32 int

struct MapLayer {
	1: required string name;
	2: string drawableUrl;
	3: required common.Id layerId;
	5: required int cacheInSeconds;
	6: required bool displayable;
}

struct MapItem {
	1: required string title;
	2: required string description
	3: required double latitude;
	4: required double longitude;
	5: required common.Id layerId;
	6: required common.Id itemId;
}

service MapService {
	list<MapLayer> getLayerList();
	list<MapItem> getLayerItems(1: common.Id layerId);
}