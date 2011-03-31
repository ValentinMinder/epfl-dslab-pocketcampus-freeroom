package org.pocketcampus.plugin.bikes;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Enumeration;

import javax.servlet.http.HttpServletRequest;

import org.pocketcampus.core.router.IServerBase;
import org.pocketcampus.core.router.PublicMethod;

public class Bikes  implements IServerBase {

	@Override
	public String getDefaultMethod() {
		return "capitalize";
	}
	
	@PublicMethod
	public String bikes(HttpServletRequest request) {

		ArrayList<BikeStation> bikes = new ArrayList<BikeStation>();
		try {
			bikes = new BikeStationParser().parserBikes();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		StringBuffer out = new StringBuffer();
		
		for (BikeStation bikeStation : bikes) {
			out.append(bikeStation.toString());
		}
		
		return out.toString();
    }
	
	@PublicMethod
	public String capitalize(HttpServletRequest request) {
    	Enumeration<String> attrNames = request.getParameterNames();
		
		if(!attrNames.hasMoreElements())
			return "vide";
		
		String ret = new String();
		while(attrNames.hasMoreElements()){
			String s = (String)request.getParameter( attrNames.nextElement() );
			ret += s.toUpperCase();
		}
		
		return ret;
    }
	

}
