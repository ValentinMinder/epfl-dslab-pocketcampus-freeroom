package org.pocketcampus.provider.positioning;

import org.pocketcampus.shared.plugin.map.Position;

import android.location.Location;


public interface IPositionProvider {

	
public Position getPosition();
public Location getGsmPosition();
public Location getGpsPosition();
public boolean  userInCampus();
//public Position getHybridLocation();
public double getAccuracy();
public void startListening();
public void stopListening();
	
}
