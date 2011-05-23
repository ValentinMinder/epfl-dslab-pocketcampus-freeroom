package org.pocketcampus.plugin.positioning;

import android.content.Context;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;

public abstract class AbstractPosition implements LocationListener {
	/**
	 * The time (in ms) to wait before invalidating the last fix.
	 */
	private static final long INVALIDATE_TIME = 15000;
	
	private LocationManager locationManager_;
	private Location location_;
	private String provider_;
	private long updateTime_;

	public AbstractPosition(Context ctx, String provider) {
		locationManager_ = (LocationManager) ctx.getSystemService(Context.LOCATION_SERVICE);
		this.provider_ = provider;
	}

	@Override
	public void onLocationChanged(Location location) {
		location_ = location;
		updateTime_ = System.currentTimeMillis();
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
		if(System.currentTimeMillis() - updateTime_ > INVALIDATE_TIME) {
			location_ = null;
		}
		return location_;
	}
	
	public void startListening() {
		locationManager_.requestLocationUpdates(provider_, 1000, 0, this);
	}
	
	public void stopListening() {
		locationManager_.removeUpdates(this);
	}
}
