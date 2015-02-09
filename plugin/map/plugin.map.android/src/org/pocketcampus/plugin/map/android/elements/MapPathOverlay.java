package org.pocketcampus.plugin.map.android.elements;

import java.util.List;

import org.osmdroid.views.overlay.PathOverlay;
import org.pocketcampus.plugin.map.android.utils.Position;

import android.content.Context;

/**
 * Legacy class to display path on the map.
 * 
 * @author Florian <florian.laurent@epfl.ch>
 *
 */
public class MapPathOverlay extends PathOverlay {

	private boolean isShowing_;

	public MapPathOverlay(int color, float width, Context ctx) {
		super(color, ctx);
		this.getPaint().setStrokeWidth(width);
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