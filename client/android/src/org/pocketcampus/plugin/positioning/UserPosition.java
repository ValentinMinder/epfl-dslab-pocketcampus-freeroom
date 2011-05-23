package org.pocketcampus.plugin.positioning;

import android.content.Context;
import android.location.Location;
import android.location.LocationListener;
import android.os.Bundle;

public class UserPosition implements LocationListener{
	private IUserLocationListener listener_;
	private HybridLocationUpdater locationUpdater_;
	private Location location_;
	private boolean hasReturned_ = false;
	private float minimumAccuracy_;
	
	/**
	 * Used to obtain the user position. When the user position is known
	 * the method userLocationReceived of the listener is called.
	 * The method userLocationReceived is called when we obtain the accurate position
	 * (whose accuracy <= minimumAccuracy) or after maxWaitTime milliseconds.
	 * The method may possibly have a "null" parameter.
	 * @param context the context of the application.
	 * @param listener the listener containing the userLocationReceived method.
	 * @param maxWaitTime the maximum waiting time (in milliseconds). 
	 * @param minimumAccuracy the minimum accuracy we want to reach.
	 */
	public UserPosition(Context context, IUserLocationListener listener,final long maxWaitTime, float minimumAccuracy) {
		this.listener_ = listener;
		this.minimumAccuracy_ = minimumAccuracy;
		locationUpdater_ = new HybridLocationUpdater(context, this);
		locationUpdater_.startListening();
		
		Runnable r = new Runnable() {
			@Override
			public void run() {
				long stop = System.currentTimeMillis() + maxWaitTime;
				while(stop > System.currentTimeMillis()) {
					try {
						Thread.sleep(1000);
					} catch (InterruptedException e) {
						//Nothing to do
					}
				}
				if(!hasReturned_) {
					locationUpdater_.stopListening();
					listener_.userLocationReceived(location_);
				}
			}
		};
		Thread t = new Thread(r);
		t.start();
	}
	
	public void stop() {
		this.hasReturned_ = true;
		this.locationUpdater_.stopListening();
	}
	
	@Override
	public void onLocationChanged(Location location) {
		if(location != null) {
			this.location_ = location;
			if(location.hasAccuracy() && location.getAccuracy() <= minimumAccuracy_) {
				this.hasReturned_ = true;
				this.locationUpdater_.stopListening();
				this.listener_.userLocationReceived(location);
			}
		}
		
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
}
