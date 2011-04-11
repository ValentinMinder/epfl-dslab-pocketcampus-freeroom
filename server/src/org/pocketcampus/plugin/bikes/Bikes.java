package org.pocketcampus.plugin.bikes;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.pocketcampus.core.plugin.IPlugin;
import org.pocketcampus.core.plugin.PublicMethod;
import org.pocketcampus.provider.mapelements.IMapElementsProvider;
import org.pocketcampus.shared.plugin.bikes.BikeStation;
import org.pocketcampus.shared.plugin.map.MapElementBean;
import org.pocketcampus.shared.plugin.map.MapLayerBean;

public class Bikes implements IPlugin, IMapElementsProvider {

	@PublicMethod
	public ArrayList<BikeStation> bikes(HttpServletRequest request) {

		ArrayList<BikeStation> bikes = new ArrayList<BikeStation>();
		try {
			bikes = new BikeStationParser().parserBikes();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		return bikes;
    }

	@Override
	public MapLayerBean getLayer() {
		return new MapLayerBean("Velopass", "http://www.google.com", 1337, 5, true);
	}

	@Override
	public List<MapElementBean> getLayerItems() {
		ArrayList<BikeStation> b = bikes(null);
		
		List<MapElementBean> items = new ArrayList<MapElementBean>();
		
		for(BikeStation s : b) {
			items.add(new MapElementBean(s.getName_(), String.valueOf(s.getBikes_()), s.getGeoLat_(), s.getGeoLng_(), 0));
		}
		
		return items;
		
	}
	

}
