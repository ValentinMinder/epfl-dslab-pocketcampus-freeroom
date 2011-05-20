package org.pocketcampus.plugin.positioning;

import android.content.Context;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;

public abstract class AbstractPosition implements LocationListener {

	private LocationManager locationManager_;
	private Location location_;
	private String provider_;

	public AbstractPosition(Context ctx, String provider) {
		locationManager_ = (LocationManager) ctx.getSystemService(Context.LOCATION_SERVICE);
		this.provider_ = provider;
	}

	@Override
	public void onLocationChanged(Location location) {
		location_ = location;
	}

	@Override
	public void onProviderDisabled(String provider) {
		// Nothing to do
	}

	@Override
	public void onProviderEnabled(String provider) {
		// Nothing to do
	}

	@Override
	public void onStatusChanged(String provider, int status, Bundle extras) {
		// Nothing to do
	}

	public Location getLocation() {
		return location_;
	}
	
	public void startListening() {
		locationManager_.requestLocationUpdates(provider_, 0, 0, this);
	}
	
	public void stopListening() {
		locationManager_.removeUpdates(this);
	}
}
