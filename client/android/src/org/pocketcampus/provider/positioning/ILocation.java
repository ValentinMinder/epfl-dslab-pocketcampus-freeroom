package org.pocketcampus.provider.positioning;

import java.io.IOException;

import org.osmdroid.util.GeoPoint;


public interface ILocation {

	
public Position getPosition();
public GeoPoint getGsmLocation() throws IOException;
public GeoPoint getGpsLocation();
public Position getHybridLocation();
public double getAccuracy();
	
}
