package org.pocketcampus.plugin.map.elements;

import java.util.ArrayList;
import java.util.Iterator;

import org.osmdroid.util.GeoPoint;

/**
 * Class Path gives all points connecting from_ & to_ points
 * */

public class Path {
	private ArrayList<GeoPoint> GeoPointList;
	private ArrayList<GeoPoint> roadmapList;
	

	public Path() {
		GeoPointList = new ArrayList<GeoPoint>();
		roadmapList = new ArrayList<GeoPoint>();
	}

	public ArrayList<GeoPoint> getGeoPointList() {
		return GeoPointList;
	}
	
	public ArrayList<GeoPoint> getRoadmapList() {
		return roadmapList;
	}

	public double length() {
		int length = 0;
		
		if(GeoPointList == null) {
			return length;
		}
		
		GeoPoint oldPos = null;
		for (Iterator<GeoPoint> iterator = GeoPointList.iterator(); iterator.hasNext();) {
			GeoPoint pos = iterator.next();
			
			if(oldPos != null) {
				length += new GeoPoint(oldPos.getLatitudeE6(), oldPos.getLongitudeE6()).distanceTo(new GeoPoint(pos.getLatitudeE6(), pos.getLongitudeE6()));
			}
			oldPos = pos;
		}
		
		return length;
	}
}







