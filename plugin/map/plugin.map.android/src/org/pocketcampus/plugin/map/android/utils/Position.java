package org.pocketcampus.plugin.map.android.utils;

import java.io.Serializable;

/**
 * Position class. 
 *
 * @author Jonas
 *
 */
public class Position implements Serializable {
	private static final long serialVersionUID = -8760312491181393320L;
	private double latitude_;
	private double longitude_;
	private double altitude_;

	public Position(){}

	public Position(double lat, double lon, double alt) {
		this.latitude_ = lat;
		this.longitude_ = lon;
		this.altitude_ = alt;
	}

	public Position(int lat, int lon, int alt) {
		this.latitude_ = lat / 1E6;
		this.longitude_ = lon / 1E6;
		this.altitude_ = alt;
	}
	
	public double getLatitude() {
		return latitude_;
	}
	
	public int getLatitudeE6() {
		return (int) (latitude_ * 1E6);
	}

	public double getLongitude() {
		return longitude_;
	}
	
	public int getLongitudeE6() {
		return (int) (longitude_ * 1E6);
	}
	
	public double getAltitude() {
		return altitude_;
	}
	
	@Override
	public String toString() {
		return "("+latitude_+", "+longitude_+", "+altitude_+")";
	}
	
	@Override
	public int hashCode() {
		return toString().hashCode();
	}
}

