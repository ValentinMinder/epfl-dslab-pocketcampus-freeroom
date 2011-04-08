package org.pocketcampus.plugin.bikes;

import java.io.IOException;
import java.util.ArrayList;

import javax.servlet.http.HttpServletRequest;

import org.pocketcampus.core.plugin.IPlugin;
import org.pocketcampus.core.plugin.PublicMethod;
import org.pocketcampus.provider.mapelements.IMapElementsProvider;
import org.pocketcampus.shared.bikes.BikeStation;

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
	public String getLayerName() {
		return "Velopass";
	}

	@Override
	public String getLayerDescription() {
		return "Shows available bicycles.";
	}
	

}
