package org.pocketcampus.plugin.bikes;

import java.io.IOException;
import java.util.ArrayList;

import javax.servlet.http.HttpServletRequest;

import org.pocketcampus.core.router.IServerBase;
import org.pocketcampus.core.router.PublicMethod;
import org.pocketcampus.shared.bikes.BikeStation;

public class Bikes  implements IServerBase {

	@Override
	public String getDefaultMethod() {
		return "bikes";
	}
	
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
	

}
