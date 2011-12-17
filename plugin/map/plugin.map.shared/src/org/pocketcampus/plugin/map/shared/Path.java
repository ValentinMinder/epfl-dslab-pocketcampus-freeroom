package org.pocketcampus.plugin.map.shared;

import java.util.List;
import java.util.ArrayList;

/**
 * Class Path gives all points connecting from_ & to_ points
 * 
 * Taken from V1
 */
public class Path {
	private List<Position> positionList_;
	private List<Position> roadmapList_;
	

	public Path() {
		positionList_ = new ArrayList<Position>();
		roadmapList_ = new ArrayList<Position>();
	}
	
	
	public Path(List<Position> p, List<Position> r) {
		positionList_ = p;
		roadmapList_ = r;
	}
	

	public List<Position> getPositionList() {
		return positionList_;
	}

	public List<Position> getRoadmapList() {
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







