package org.pocketcampus.shared.plugin.map;

import java.io.Serializable;

public class Position implements Serializable {
	private static final long serialVersionUID = -8760312491181393320L;
	private final double latitude_;
	private final double longitude_;
	private final int altitude_;
	
	public Position(double lat, double lon, int alt) {
		this.latitude_ = lat;
		this.longitude_ = lon;
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
	
	public int getAltitude() {
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

