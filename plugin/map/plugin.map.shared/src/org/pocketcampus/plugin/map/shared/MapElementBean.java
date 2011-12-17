package org.pocketcampus.plugin.map.shared;

import java.io.Serializable;

/**
 * Class used to transfer information from the server to the client.
 * It represents an item on the map.
 * 
 * @status complete
 * 
 * @author Jonas, Johan
 *
 */
public class MapElementBean implements Serializable {
	
	private static final long serialVersionUID = -5827393357802491225L;
	private String title, description;
	private double latitude, longitude, altitude;
	private int id, layerId;
	private String pluginId; // Name of the plugin to launch when an item is clicked
	
	/**
	 * Bean constructor 
	 * @param title Title of the item
	 * @param description Description, if any 
	 * @param latitude Position
	 * @param longitude Position
	 * @param altitude Position
	 * @param layerId ID of the layer the item is on
	 * @param itemId ID of this item
	 */
	public MapElementBean(String title, String description, double latitude,
			double longitude, double altitude, int layerId, int itemId) {
		this.title = title;
		this.description = description;
		this.latitude = latitude;
		this.longitude = longitude;
		this.altitude = altitude;
		this.id = itemId;
		this.layerId = layerId;
	}
	
	/**
	 * Bean constructor 
	 * @param title Title of the item
	 * @param description Description, if any 
	 * @param latitude Position
	 * @param longitude Position
	 * @param altitude Position
	 * @param layerId ID of the layer the item is on
	 * @param itemId ID of this item
	 * @param pluginId Package name of the client plugin that can be launched when the user clicks on the item. Example: "org.pocketcampus.plugin.bikes.BikesPlugin"
	 */
	public MapElementBean(String title, String description, double latitude,
			double longitude, double altitude, int layerId, int itemId, String pluginId) {
		
		this(title, description, latitude, longitude, altitude, layerId, itemId);
		this.pluginId = pluginId;
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public int getLayerId() {
		return layerId;
	}

	public void setLayerId(int layerId) {
		this.layerId = layerId;
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
	
	public String getPluginId() {
		return pluginId;
	}

	public void setPluginId(String pluginId) {
		this.pluginId = pluginId;
	}

	@Override
	public String toString() {
		return title + "," + description + "," + latitude + "," + longitude + "," + altitude;
	}
	
}
