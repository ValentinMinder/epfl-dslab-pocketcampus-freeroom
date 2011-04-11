package org.pocketcampus.shared.plugin.map;

import java.io.Serializable;

public class MapElementBean implements Serializable {
	
	private static final long serialVersionUID = -5827393357802491225L;
	private String title, description;
	private double latitude, longitude, altitude;
	private int id, layer_id;
	
	public MapElementBean() { }
	
	public MapElementBean(String title, String description, double latitude,
			double longitude, double altitude) {
		this.title = title;
		this.description = description;
		this.latitude = latitude;
		this.longitude = longitude;
		this.altitude = altitude;
	}
	
	public MapElementBean(String title, String description, double latitude,
			double longitude, double altitude, int id, int layer_id) {
		this.title = title;
		this.description = description;
		this.latitude = latitude;
		this.longitude = longitude;
		this.altitude = altitude;
		this.id = id;
		this.layer_id = layer_id;
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public int getLayer_id() {
		return layer_id;
	}

	public void setLayer_id(int layer_id) {
		this.layer_id = layer_id;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public double getLatitude() {
		return latitude;
	}

	public void setLatitude(double latitude) {
		this.latitude = latitude;
	}

	public double getLongitude() {
		return longitude;
	}

	public void setLongitude(double longitude) {
		this.longitude = longitude;
	}

	public double getAltitude() {
		return altitude;
	}

	public void setAltitude(double altitude) {
		this.altitude = altitude;
	}
	
	@Override
	public String toString() {
		return title + "," + description + "," + latitude + "," + longitude + "," + altitude;
	}
	
}
