package org.pocketcampus.plugin.map.elements;

import org.osmdroid.util.GeoPoint;
import org.osmdroid.views.overlay.OverlayItem;

import android.graphics.drawable.Drawable;

public class MapElement extends OverlayItem {
	public MapElement(String title, String description, GeoPoint coordinates) {
		super(title, description, coordinates);
	}
	
	@Override
	public Drawable getDrawable() {
		// TODO Auto-generated method stub
		return null;
	}
}
