/*
 ********************* [ P O C K E T C A M P U S ] *****************
 * [    LICENCE    ]     see "licence"-file in the root directory
 * [   MAINTAINER  ]    tarek.benoudina@epfl.ch
 * [     STATUS    ]    working
 *
 **************************[ C O M M E N T S ]**********************
 *
 *    Class reused from V1
 *                      
 *******************************************************************
 */ 

package org.pocketcampus.provider.authentication;




public class Position  {
	
	private final double lat_;
	private final double lon_;
	private final int level_;
	
	public Position(double lat, double lon, int level) {
		this.lat_ = lat;
		this.lon_ = lon;
		this.level_ = level;
	}
	
	public Position(double lat,double lon){
		this.lat_ = lat;
		this.lon_ = lon;
		this.level_ = 0;
	}
	
	public double getLat() {
		return lat_;
	}

	public double getLon() {
		return lon_;
	}
	
	public int getLevel() {
		return level_;
	}
	
	@Override
	public String toString() {
		return "("+lat_+", "+lon_+", "+level_+")";
	}
	
	@Override
	public int hashCode() {
		return toString().hashCode();
	}
	
}

