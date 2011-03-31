package org.pocketcampus.plugin.bikes;

public class BikeStation {

	private int empty_;
	private int bikes_;
	private double geoLat_;
	private double geoLng_;
	private String name_;
	
	
	public BikeStation(int empty, int bikes, double geoLat, double geoLng, String name) {
		this.empty_ = empty;
		this.bikes_ = bikes;
		this.geoLat_ = geoLat;
		this.geoLng_ = geoLng;
		this.name_ = name;
	}


	public int getEmpty_() {
		return empty_;
	}


	public int getBikes_() {
		return bikes_;
	}


	public double getGeoLat_() {
		return geoLat_;
	}


	public double getGeoLng_() {
		return geoLng_;
	}


	public String getName_() {
		return name_;
	}


	@Override
	public String toString() {
		return name_ + " ("+geoLat_+", "+geoLng_+") :" + bikes_ + " / " + empty_;
	}
	
}
