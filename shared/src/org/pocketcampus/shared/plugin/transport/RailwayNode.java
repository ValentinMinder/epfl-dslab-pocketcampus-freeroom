package org.pocketcampus.shared.plugin.transport;
import java.io.Serializable;
import java.util.HashMap;


public class RailwayNode implements Serializable, Comparable<RailwayNode> {
	private final static double METRO_SPEED = 5.0;
	
	private static final long serialVersionUID = -5897504431323906844L;
	private HashMap<String, String> tags_;
	private double lat_;
	private double lon_;
	private int ref_;
	private int num_;
	private double distFromPrevious_;
	private int previousRef_;
	private int uicRef_;
	
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
	
	public void setRef(int ref) {
		ref_ = ref;
	}
	
	public void setNum(int num) {
		num_ = num;
	}
	
	public double getDistFromPrevious() {
		return distFromPrevious_;
	}
	
	public void setDistFromPrevious(double distFromLast) {
		distFromPrevious_ = distFromLast;
	}

	public void addTag(String name, String value) {
		tags_.put(name, value);
	}
	
	@Override
	public String toString() {
		return "("+num_+","+ref_+":"+lat_+","+lon_+")" + tags_.toString() + "\n";
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
	
	public String getTag(String key) {
		return tags_.get(key);
	}

	public int getRef() {
		return ref_;
	}

	@Override
	public int compareTo(RailwayNode other) {
		return (num_<other.getNum())?0:1;
	}

	public int getNum() {
		return num_;
	}

	public void setPreviousRef(int i) {
		previousRef_ = i;
	}
	
	public int getPreviousRef() {
		return previousRef_;
	}
	
	public void setUicRef(String value) {
		uicRef_ = Integer.parseInt(value);
	}
	
	public int getUicRef() {
		return uicRef_;
	}
}
