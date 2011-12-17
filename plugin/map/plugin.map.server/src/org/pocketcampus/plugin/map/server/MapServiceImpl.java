package org.pocketcampus.plugin.map.server;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import org.apache.thrift.TException;
import org.pocketcampus.plugin.map.shared.MapItem;
import org.pocketcampus.plugin.map.shared.MapLayer;
import org.pocketcampus.plugin.map.shared.MapService;

public class MapServiceImpl implements MapService.Iface {
	private List<MapLayer> mLayersList = new ArrayList<MapLayer>();
	private List<MapItem> mItemsList = new ArrayList<MapItem>();
	private MapDatabase mMapDb;
	
	public MapServiceImpl() {
		System.out.println("Starting Map plugin server...");
		mMapDb = new MapDatabase();
	}
	
	@Override
	public List<MapLayer> getLayerList() throws TException {
		System.out.println("getLayerList");
		
		synchronized (mLayersList) {
			mLayersList = mMapDb.getMapLayers();
			
			if(mLayersList.size() == 0) {
				// Sort the layers by alphabetic order
				Collections.sort(mLayersList, new Comparator<MapLayer>() {
					@Override
					public int compare(MapLayer o1, MapLayer o2) {
						return o1.getName().compareToIgnoreCase(o2.getName());
					}
				});
			}
		}
		
		return mLayersList;
	}

	@Override
	public List<MapItem> getLayerItems(long layerId) throws TException {
		System.out.println("getLayerItems(id: "+layerId+")");
		mItemsList = mMapDb.getMapElements((int) layerId);
		return mItemsList;
	}

	@Override
	public List<MapItem> search(String query) throws TException {
		System.out.println("search(query: "+query+")");
		List<MapItem> results = Search.searchTextOnEpflWebsite(query, 100);
		System.out.println(results);
		return results;
	}

}
