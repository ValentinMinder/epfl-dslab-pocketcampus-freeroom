package org.pocketcampus.plugin.map.elements;

import org.osmdroid.util.GeoPoint;

import android.graphics.drawable.Drawable;

public interface IMapElement {
	String getTitle();
	String getDescription();
	GeoPoint getPosition();
	Drawable getIcon();
	int getLevel();
}
