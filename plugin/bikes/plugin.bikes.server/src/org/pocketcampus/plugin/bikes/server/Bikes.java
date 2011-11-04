//package org.pocketcampus.plugin.bikes;
//
//import java.io.IOException;
//import java.util.ArrayList;
//import java.util.Date;
//import java.util.List;
//
//import javax.servlet.http.HttpServletRequest;
//
//import org.pocketcampus.core.plugin.IPlugin;
//import org.pocketcampus.core.plugin.PublicMethod;
//import org.pocketcampus.provider.mapelements.IMapElementsProvider;
//import org.pocketcampus.shared.plugin.authentication.AuthToken;
//import org.pocketcampus.shared.plugin.bikes.BikeStation;
//import org.pocketcampus.shared.plugin.map.MapElementBean;
//import org.pocketcampus.shared.plugin.map.MapLayerBean;
//
//public class Bikes implements IPlugin, IMapElementsProvider {
//	private final static long REFRESH_TIME = 5 * 60 * 1000;
//	
//	private ArrayList<BikeStation> bikeStationsCache_;
//	private Date bikeStationsCacheDate_;
//	private MapLayerBean mapLayerBean_;
//	
//	public Bikes() {
//		mapLayerBean_ = new MapLayerBean("Velopass", "data/map/map_marker_bike.png", this, 1, 300, true);
//		loadBikeStations();
//	}
//
//	@PublicMethod
//	public ArrayList<BikeStation> bikes(HttpServletRequest request) {
//		if(isRefreshNeeded()) {
//			loadBikeStations();
//		}
//		
//		return bikeStationsCache_;
//    }
//	
//	private void loadBikeStations() {
//		try {
//			bikeStationsCache_ = new BikeStationParser().parseBikesStations();
//			bikeStationsCacheDate_ = new Date();
//			
//		} catch (IOException e) {
//			bikeStationsCache_ = null;
//			bikeStationsCacheDate_ = null;
//		}
//	}
//	
//	private boolean isRefreshNeeded() {
//		// data couldn't be loaded the previous time
//		if(bikeStationsCache_ == null || bikeStationsCacheDate_ == null) {
//			return true;
//		}
//		
//		// data is outdated
//		if(((new Date()).getTime() - bikeStationsCacheDate_.getTime()) > REFRESH_TIME) {
//			return true;
//		}
//		
//		return false;
//	}
//	
//	@PublicMethod
//	public String getLayerId(HttpServletRequest request) {
//		return mapLayerBean_.getExternalId();
//    }
//
//	@Override
//	public List<MapLayerBean> getLayers() {
//		// the list always contains the same lone layer
//		List<MapLayerBean> mapLayerBeans_ = new ArrayList<MapLayerBean>();
//		mapLayerBeans_.add(mapLayerBean_);
//		
//		return mapLayerBeans_;
//	}
//
//	@Override
//	public List<MapElementBean> getLayerItems(AuthToken token, int layerId) {
//		ArrayList<BikeStation> b = bikes(null);
//		
//		List<MapElementBean> items = new ArrayList<MapElementBean>();
//		
//		for(BikeStation s : b) {
//			StringBuffer description = new StringBuffer();
//			description.append("Vélos libres: ");
//			description.append(String.valueOf(s.getFreeBikes()));
//			description.append("\n");
//			
//			description.append("Places libres: ");
//			description.append(String.valueOf(s.getEmptyRacks()));
//			
//			items.add(new MapElementBean(s.getName(), description.toString(), s.getGeoLat(), s.getGeoLng(), 0, 1, s.getId(), "org.pocketcampus.plugin.bikes.BikesPlugin"));
//		}
//		
//		return items;
//	}
//	
//
//}
