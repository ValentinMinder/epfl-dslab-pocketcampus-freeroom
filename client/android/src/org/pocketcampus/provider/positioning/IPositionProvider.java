package org.pocketcampus.provider.positioning;

import java.io.IOException;

import org.osmdroid.util.GeoPoint;
import org.pocketcampus.shared.plugin.map.Position;


public interface IPositionProvider {

	
public Position getPosition();
public GeoPoint getGsmPosition() throws IOException;
public Position getGpsPosition();
public boolean  userInCampus();
//public Position getHybridLocation();
public double getAccuracy();
	
}
