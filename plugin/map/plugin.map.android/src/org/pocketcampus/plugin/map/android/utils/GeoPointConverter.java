package org.pocketcampus.plugin.map.android.utils;

import org.osmdroid.util.GeoPoint;
import org.pocketcampus.plugin.map.shared.Position;

/**
 * Utility class to convert <code>GeoPoint</code>s to our own <code>Position</code>s.
 * 
 * @author Florian <florian.laurent@epfl.ch>
 *
 */
public class GeoPointConverter {
	public static Position toPosition(GeoPoint g) {
		return new Position(g.getLatitudeE6(), g.getLongitudeE6(), g.getAltitude());
	}
}
