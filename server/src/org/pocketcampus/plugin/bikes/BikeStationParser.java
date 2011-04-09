package org.pocketcampus.plugin.bikes;

import java.io.IOException;
import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.pocketcampus.shared.plugin.bikes.BikeStation;
import org.pocketcampus.shared.utils.URLLoader;

public class BikeStationParser {

	private static final String URL = "http://www.bicincitta.com/wsexchange/panoramica.aspx?city=2000&usr=polyright&pw=bv9y7t34b9je";	
	
	public ArrayList<BikeStation> parserBikes() throws IOException {
		ArrayList<BikeStation> stations = new ArrayList<BikeStation>();
		
		
		String source = URLLoader.getSource(URL);
		
		Pattern p = Pattern.compile("<sites>(.*)</sites>");
		Matcher m = p.matcher(source);
		
		if(m.find()) {
			source = m.group(1);
			
			p = Pattern.compile("<item empty=\\\"([0-9]{1,2})\\\" bikes=\\\"([0-9]{1,2})\\\" .{1,100} geoLat=\\\"([0-9]{1,3},[0-9]{1,15})\\\" geoLng=\\\"([0-9]{1,3},[0-9]{1,15})\\\" .{1,50}>[ ]?[0-9]{4}[ ]?(.{1,25})</item>");
			m = p.matcher(source);
			
			while(m.find()) {
				int empty = Integer.parseInt(m.group(1));
				int bikes = Integer.parseInt(m.group(2));
				double geoLat = Double.parseDouble(m.group(3).replace(",","."));
				double geoLng = Double.parseDouble(m.group(4).replace(",","."));
				stations.add(new BikeStation(empty, bikes, geoLat, geoLng, m.group(5)));
			}
		}
		
		return stations;
	}

	
}
