package org.pocketcampus.shared.plugin.map;
import java.io.Serializable;
import java.util.HashMap;


public class RailwayNode implements Serializable, Comparable<RailwayNode> {
	private static final long serialVersionUID = -5897504431323906844L;
	private HashMap<String, String> tags_;
	private double lat_;
	private double lon_;
	private int num_;
	
	public RailwayNode() {
		tags_ = new HashMap<String, String>();
		num_ = 0;
	}
	
	public void setLat(double lat) {
		lat_ = lat;
	}

	public void setLon(double lon) {
		lon_ = lon;
	}
	
	public void setNum(int id) {
		num_ = id;
	}

	public void addTag(String name, String value) {
		tags_.put(name, value);
	}
	
	@Override
	public String toString() {
		return "("+num_+":"+lat_+","+lon_+")" + tags_.toString() + "\n";
	}

	@Override
	public int compareTo(RailwayNode o) {
		//flon: 46.5199984,6.6299773
//		double selfToFlon = (lat_-46.519997)*(lat_-46.519997) + (lon_-6.6299773)*(lon_-6.6299773);
//		selfToFlon = Math.sqrt(selfToFlon);
//		
//		double otherToFlon = (o.getLat()-46.519997)*(o.getLat()-46.519997) + (o.getLon()-6.6299773)*(o.getLon()-6.6299773);
//		otherToFlon = Math.sqrt(otherToFlon);
//		
//		return (selfToFlon<otherToFlon)?-1:1;
		
		return (num_<o.getNum())?-1:1;
	}

	public double distTo(RailwayNode o) {
		double dist = (lat_-o.getLat())*(lat_-o.getLat()) + (lon_-o.getLon())*(lon_-o.getLon());
		dist = Math.sqrt(dist);
		
		return dist;
	}
	
	public double getLat() {
		return lat_;
	}
	
	public double getLon() {
		return lon_;
	}
	
	public int getNum() {
		return num_;
	}
}
