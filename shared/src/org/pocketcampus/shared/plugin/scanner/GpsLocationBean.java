package org.pocketcampus.shared.plugin.scanner;

import java.io.Serializable;

public class GpsLocationBean implements Serializable {

	private float accuracy_;
	private double altitude_;
	private float bearing_;
	private double latitude_;
	private double longitude_;

	public void setAccuracy(float accuracy_) {
		this.accuracy_ = accuracy_;
	}
	
	public void setAltitude(double altitude_) {
		this.altitude_ = altitude_;
	}
	
	public void setBearing(float bearing_) {
		this.bearing_ = bearing_;
	}
	
	public void setLatitude(double latitude_) {
		this.latitude_ = latitude_;
	}
	
	public void setLongitude(double longitude_) {
		this.longitude_ = longitude_;
	}

	public double getLatitude() {
		return latitude_;
	}

	public double getLongitude() {
		return longitude_;
	}

	public double getAltitude() {
		return altitude_;
	}

	public float getBearing() {
		return bearing_;
	}

	public float getAccuracy() {
		return accuracy_;
	}

}
