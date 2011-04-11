package org.pocketcampus.shared.plugin.map;

import java.util.ArrayList;

/**
 * Class Path gives all points connecting from_ & to_ points
 * */

public class Path {
	private ArrayList<Position> positionList_;
	private ArrayList<Position> roadmapList_;
	

	public Path() {
		positionList_ = new ArrayList<Position>();
		roadmapList_ = new ArrayList<Position>();
	}
	
	
	public Path(ArrayList<Position> p, ArrayList<Position> r) {
		positionList_ = p;
		roadmapList_ = r;
	}
	

	public ArrayList<Position> getPositionList() {
		return positionList_;
	}

	public ArrayList<Position> getRoadmapList() {
		return roadmapList_;
	}

	/*
	 * TODO
	public double length() {
		int length = 0;
		
		if(GeoPointList == null) {
			return length;
		}
		
		Position oldPos = null;
		for (Iterator<Position> iterator = GeoPointList.iterator(); iterator.hasNext();) {
			Position pos = iterator.next();
			
			if(oldPos != null) {
				length += new Position(oldPos.getLatitudeE6(), oldPos.getLongitudeE6()).distanceTo(new Position(pos.getLatitudeE6(), pos.getLongitudeE6()));
			}
			oldPos = pos;
		}
		
		return length;
	}
	*/
}







