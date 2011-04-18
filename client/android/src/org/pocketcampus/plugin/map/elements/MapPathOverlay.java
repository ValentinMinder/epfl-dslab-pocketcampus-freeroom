package org.pocketcampus.plugin.map.elements;

import java.util.List;

import org.osmdroid.views.overlay.PathOverlay;
import org.pocketcampus.shared.plugin.map.Path;
import org.pocketcampus.shared.plugin.map.Position;

import android.content.Context;

public class MapPathOverlay extends PathOverlay {
	
	private boolean isShowing_;

	public MapPathOverlay(int color, float width, Context ctx) {
		super(color, ctx);
		this.getPaint().setStrokeWidth(width);
	}
	
	public void setPath(Path path) {
		setList(path.getPositionList());
	}
	
	public void setList(List<Position> list) {
		
		this.clearPath();
	
		for(Position p : list) {
			this.addPoint(p.getLatitudeE6(), p.getLongitudeE6());
		}
		
		isShowing_ = true;
	}
	
	@Override
	public void clearPath() {
		super.clearPath();
		isShowing_ = false;
	}

	public boolean isShowingPath() {
		return isShowing_;
	}
	

}