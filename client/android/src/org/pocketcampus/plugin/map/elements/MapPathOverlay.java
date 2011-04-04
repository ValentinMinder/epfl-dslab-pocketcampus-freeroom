package org.pocketcampus.plugin.map.elements;

import org.osmdroid.util.GeoPoint;
import org.osmdroid.views.overlay.PathOverlay;

import android.content.Context;

public class MapPathOverlay extends PathOverlay {

	public MapPathOverlay(int color, Context ctx) {
		super(color, ctx);
	}
	
	public void setPath(Path path) {
		
		this.clearPath();
		
		for(GeoPoint p : path.getGeoPointList()) {
			this.addPoint(p.getLatitudeE6(), p.getLongitudeE6());
		}
	}
	

}