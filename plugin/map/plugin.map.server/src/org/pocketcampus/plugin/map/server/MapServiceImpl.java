package org.pocketcampus.plugin.map.server;

import java.util.List;

import org.apache.thrift.TException;
import org.pocketcampus.plugin.map.shared.MapItem;
import org.pocketcampus.plugin.map.shared.MapLayer;
import org.pocketcampus.plugin.map.shared.MapLayersResponse;
import org.pocketcampus.plugin.map.shared.MapService;
import org.pocketcampus.plugin.map.shared.MapStatusCode;

public class MapServiceImpl implements MapService.Iface {
	
	private final MapDatabase mapDb;
	
	public MapServiceImpl() {
		mapDb = new MapDatabase();
	}
	
	@Override
	public MapLayersResponse getLayers() throws TException {
		String lang = "en"; //TODO get lang code dynamically from client request
		List<MapLayer> layers = mapDb.getMapLayers(lang);
		MapLayersResponse response = new MapLayersResponse(MapStatusCode.OK, layers);
		return response;
	}
	
	@Override
	public List<MapItem> search(String query) throws TException {
		System.out.println("search(query: "+query+")");
		List<MapItem> results = Search.searchTextOnEpflWebsite(query, 100);
		return results;
	}

}
