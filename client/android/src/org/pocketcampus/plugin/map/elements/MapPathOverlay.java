package org.pocketcampus.plugin.map.elements;

import org.osmdroid.views.overlay.PathOverlay;
import org.pocketcampus.shared.plugin.map.Path;
import org.pocketcampus.shared.plugin.map.Position;

import android.content.Context;

public class MapPathOverlay extends PathOverlay {

	public MapPathOverlay(int color, Context ctx) {
		super(color, ctx);
	}
	
	public void setPath(Path path) {
		
		this.clearPath();
		
		for(Position p : path.getPositionList()) {
			this.addPoint(p.getLatitudeE6(), p.getLongitudeE6());
		}
	}
	

}