package org.pocketcampus.plugin.map.android.elements;

import java.util.List;

import org.osmdroid.views.overlay.PathOverlay;
import org.pocketcampus.plugin.map.shared.Position;

import android.content.Context;

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

//	@Override
//	protected void draw(final Canvas canvas, final MapView mapView, final boolean shadow) {
//		super.draw(canvas, mapView, shadow);
//		
//		if(path_ == null || !isShowingPath()) {
//			return;
//		}
//
//		List<Position> roadMap = path_.getRoadmapList();
//
//		if (shadow) {
//			return;
//		}
//
//		if (roadMap.size() < 1) {
//			// nothing to paint
//			return;
//		}
//
//		final Projection pj = mapView.getProjection();
//
//		// precompute new points to the intermediate projection.
//		final int size = roadMap.size();
//
//		/*
//		while (this.mPointsPrecomputed < size) {
//			final Point pt = this.mPoints.get(this.mPointsPrecomputed);
//			pj.toMapPixelsProjected(pt.x, pt.y, pt);
//
//			this.mPointsPrecomputed++;
//		}
//		*/
//
//		Point screenPoint0 = null; // points on screen
//		Point screenPoint1 = null;
//		Point projectedPoint0; // points from the points list
//		Point projectedPoint1;
//
//		// clipping rectangle in the intermediate projection, to avoid performing projection.
//		final Rect clipBounds = pj.fromPixelsToProjected(pj.getScreenRect());
//
//		for(Position p : roadMap) {
//			canvas.drawCircle(50, 50, 10, this.getPaint());
//		}
//
//
//	}

}