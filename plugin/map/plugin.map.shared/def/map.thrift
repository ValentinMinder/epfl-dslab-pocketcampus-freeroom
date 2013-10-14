namespace java org.pocketcampus.plugin.map.shared
namespace csharp org.pocketcampus.plugin.map.shared

include "../../../../platform/sdk/platform.sdk.shared/def/common.thrift"

struct MapLayer {
	1: required string name;
	2: string drawableUrl;
	3: required common.Id layerId;
	5: required i32 cacheInSeconds;
	6: required bool displayable;
}

struct MapItem {
	1: required string title;
	2: string description
	3: required double latitude;
	4: required double longitude;
	5: required common.Id layerId;
	6: required common.Id itemId;
	7: optional i32 floor;
	8: optional string category;
}

service MapService {
	list<MapLayer> getLayerList();
	list<MapItem> getLayerItems(1: common.Id layerId);
	list<MapItem> search(1: string query);
//	list<MapItem> searchLayersItems(1: MapLayer.name layerName, 2: string itemName);
}