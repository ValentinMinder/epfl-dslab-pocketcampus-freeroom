package org.pocketcampus.plugin.map.utils;

import org.osmdroid.util.GeoPoint;
import org.pocketcampus.shared.plugin.map.Position;

public class GeoPointConverter {
	public static Position toPosition(GeoPoint g) {
		return new Position(g.getLatitudeE6(), g.getLongitudeE6(), g.getAltitude());
	}
}
