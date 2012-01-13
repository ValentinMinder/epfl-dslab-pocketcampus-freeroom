package org.pocketcampus.plugin.map.android.elements;

import org.osmdroid.util.GeoPoint;
import org.osmdroid.views.overlay.OverlayItem;
import org.pocketcampus.plugin.map.shared.MapElementBean;

/**
 * Represents an element on the map (AKA POI).
 * 
 * @author Florian <florian.laurent@epfl.ch>
 *
 */
public class MapElement extends OverlayItem {
	
	private String pluginId_;
	private int itemId_;
	
	public MapElement(String title, String description, GeoPoint coordinates) {
		super(title, description, coordinates);
	}
	
	public MapElement(String title, String description, GeoPoint coordinates, String pluginId, int itemId) {
		this(title, description, coordinates);
		this.pluginId_ = pluginId;
		this.itemId_ = itemId;
	}
	
	public MapElement(MapElementBean meb) {
		this(meb.getTitle(), meb.getDescription(), new GeoPoint(meb.getLatitude(), meb.getLongitude(), meb.getAltitude()), meb.getPluginId(), meb.getId());
	}
	
	public String getPluginId() {
		return pluginId_;
	}

	public int getItemId() {
		return itemId_;
	}
}
