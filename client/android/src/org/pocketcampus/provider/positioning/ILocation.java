package org.pocketcampus.provider.positioning;

import java.io.IOException;

import org.osmdroid.util.GeoPoint;
import org.pocketcampus.shared.plugin.map.Position;


public interface ILocation {

	
public Position getPosition();
public GeoPoint getGsmLocation() throws IOException;
public Position getGpsLocation();
//public Position getHybridLocation();
public double getAccuracy();
	
}
