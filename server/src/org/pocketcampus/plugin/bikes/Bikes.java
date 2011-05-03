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
	public List<MapLayerBean> getLayers() {
		List<MapLayerBean> l = new ArrayList<MapLayerBean>();
		l.add(new MapLayerBean("Velopass", "http://128.178.254.75:8080/pocketcampus-server/data/bikes_normal_mini.png", this, 1, 9, true));
		return l;
	}

	@Override
	public List<MapElementBean> getLayerItems(int layerId) {
		ArrayList<BikeStation> b = bikes(null);
		
		List<MapElementBean> items = new ArrayList<MapElementBean>();
		
		for(BikeStation s : b) {
			items.add(new MapElementBean(s.getName_(), String.valueOf(s.getBikes_()), s.getGeoLat_() + Math.random() * 0.002, s.getGeoLng_() + Math.random() * 0.002, 0));
		}
		
		return items;
		
	}
	

}
