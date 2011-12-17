package org.pocketcampus.plugin.map.android.utils;

import org.osmdroid.util.GeoPoint;
import org.pocketcampus.plugin.map.shared.Position;

public class GeoPointConverter {
	public static Position toPosition(GeoPoint g) {
		return new Position(g.getLatitudeE6(), g.getLongitudeE6(), g.getAltitude());
	}
}
