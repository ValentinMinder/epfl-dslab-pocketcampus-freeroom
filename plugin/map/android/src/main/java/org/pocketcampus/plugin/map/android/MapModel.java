package org.pocketcampus.plugin.map.android;

import java.util.Arrays;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.pocketcampus.platform.android.core.IView;
import org.pocketcampus.platform.android.core.PluginModel;
import org.pocketcampus.plugin.map.android.iface.IMapView;
import org.pocketcampus.plugin.map.shared.MapItem;

/**
 * Map model.
 * 
 * @author Florian <florian.laurent@epfl.ch>
 *
 */
public class MapModel extends PluginModel {
	private IMapView mListeners = (IMapView) getListeners();
	
	private List<MapItem> mSearchResults;
	private Map<String, String> layerNames = new HashMap<String, String>();
	private Map<String, String> epflFloors = new HashMap<String, String>();
	private Comparator<String> floorKeyComparator;
	
	public MapModel() {
		layerNames.clear();
		layerNames.put("parkings_publics{floor}", "parkings publics");
		layerNames.put("arrets_metro{floor}", "arrets metro");
		layerNames.put("transports_publics{floor}", "transports publics");
		layerNames.put("information{floor}", "information");
		
		epflFloors.clear();
		epflFloors.put("all", "All floors");
		epflFloors.put("8", "Floor 8");
		epflFloors.put("7", "Floor 7");
		epflFloors.put("6", "Floor 6");
		epflFloors.put("5", "Floor 5");
		epflFloors.put("4", "Floor 4");
		epflFloors.put("3", "Floor 3");
		epflFloors.put("2", "Floor 2");
		epflFloors.put("1", "Floor 1");
		epflFloors.put("0", "Floor 0");
		epflFloors.put("-1", "Floor -1");
		epflFloors.put("-2", "Floor -2");
		epflFloors.put("-3", "Floor -3");
		epflFloors.put("-4", "Floor -4");
		epflFloors.put("", "None");
		
		floorKeyComparator = new Comparator<String>() {
			private List<String> floorKeyOrder = Arrays.asList("all", "8", "7", "6", "5", "4", "3", "2", "1", "0", "-1", "-2", "-3", "-4", "");
			public int compare(String arg0, String arg1) {
				Integer i0 = floorKeyOrder.indexOf(arg0);
				Integer i1 = floorKeyOrder.indexOf(arg1);
				if(i0 == -1 || i1 == -1) {
					return arg0.compareTo(arg1);
				}
				return i0.compareTo(i1);
			}
		};
	}
	
	@Override
	protected Class<? extends IView> getViewInterface() {
		return IMapView.class;
	}


	public void setSearchResult(List<MapItem> results) {
		System.out.println(results);
		mSearchResults = results;
		mListeners.searchResultsUpdated();
	}

	public List<MapItem> getSearchResults() {
		return mSearchResults;
	}
	
	public void setLayerNames(Map<String, String> map) {
		layerNames = map;
	}
	
	public Map<String, String> getLayerNames() {
		return layerNames;
	}

	public Map<String, String> getEpflFloors() {
		return epflFloors;
	}
	
	public Comparator<String> getFloorKeyComparator() {
		return floorKeyComparator;
	}
	
}
