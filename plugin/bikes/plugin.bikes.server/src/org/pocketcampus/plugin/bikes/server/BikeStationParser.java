package org.pocketcampus.plugin.bikes.server;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.pocketcampus.plugin.bikes.shared.BikeEmplacement;
import org.pocketcampus.platform.sdk.shared.utils.URLLoader;;

/**
 * Parser for the velopass website
 * @author Pascal <pascal.scheiben@gmail.com>
 * @author Guillaume <guillaume.ulrich@epfl.ch>
 */
public class BikeStationParser {

	/**url of the velopass website */
	private static final String URL = "http://www.bicincitta.com/wsexchange/panoramica.aspx?city=2000&usr=polyright&pw=bv9y7t34b9je";	
	
	/** Parse the information from the website*/
	public ArrayList<BikeEmplacement> parseBikesStations() throws IOException {
		
		ArrayList<BikeEmplacement> stations = new ArrayList<BikeEmplacement>();
		ArrayList<String> names = new ArrayList<String>();
		HashMap<String, BikeEmplacement> map =  new HashMap<String, BikeEmplacement>();
		
		String source = URLLoader.getSource(URL);
		
		// Dirty fix for the "é" character.
		// It's a problem on their side, we can't do anything cleaner on our side to fix it.
		source = source.replace("ï¿½", "é");
		source = source.replace("�", "é");
		
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
				String name = m.group(5);
				map.put(name, new BikeEmplacement(empty, bikes, geoLat, geoLng, name));
				names.add(name);
//				stations.add(new BikeEmplacement(empty, bikes, geoLat, geoLng, m.group(5)));
			}
		}
		
		Collections.sort(names);
		for(String name: names){
			stations.add(map.get(name));
		}
		
		return stations;
	}

	
}
