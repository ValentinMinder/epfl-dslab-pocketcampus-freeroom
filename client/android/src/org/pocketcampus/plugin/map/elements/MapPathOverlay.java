package org.pocketcampus.plugin.map.elements;

import java.util.List;

import org.osmdroid.views.overlay.PathOverlay;
import org.pocketcampus.shared.plugin.map.Path;
import org.pocketcampus.shared.plugin.map.Position;

import android.content.Context;

public class MapPathOverlay extends PathOverlay {

	public MapPathOverlay(int color, Context ctx) {
		super(color, ctx);
	}
	
	public void setPath(Path path) {
		setList(path.getPositionList());
	}
	
	public void setList(List<Position> list) {
		
		this.clearPath();
	
		for(Position p : list) {
			this.addPoint(p.getLatitudeE6(), p.getLongitudeE6());
		}
	}
	

}