package org.pocketcampus.plugin.map.utils;

import org.osmdroid.views.MapView;
import org.pocketcampus.plugin.positioning.HybridLocation;
import org.pocketcampus.shared.plugin.map.CoordinateConverter;
import org.pocketcampus.shared.plugin.map.Position;

import android.content.Context;
import android.location.Location;
import android.location.LocationListener;
import android.util.Log;

public class HybridLocationUpdater {
	private LocationListener listener_;
	private HybridLocation hybridLocation_;
	
	/**
	 * The number of ms between two updates
	 */
	private long updateTime_ = 5000;
	
	private boolean isRunning_ = false;
	private LocationUpdater updater_;
	
	public HybridLocationUpdater(Context context, LocationListener listener) {
		this.listener_ = listener;
		try {
			hybridLocation_ = new HybridLocation(context, new MapView(context, 256));
		} catch (Exception e) {
			Log.e("HybridLocationUpdater", "Error creating HybridLocation instance");
			e.printStackTrace();
		}
	}
	
	public synchronized void startListening() {
		if(hybridLocation_ == null)
			return;
		
		if(!isRunning_) {
			isRunning_ = true;
			updater_ = new LocationUpdater(hybridLocation_, listener_, updateTime_);
			updater_.start();
		}
	}
	
	public synchronized void stopListening() {
		isRunning_ = false;
		if(updater_ != null) {
			updater_.stopUpdating();
			updater_ = null;
		}
	}
}

class LocationUpdater extends Thread {
	private HybridLocation hl;
	private LocationListener l;
	private long t;
	private boolean isRunning = true;
	
	public LocationUpdater(HybridLocation hybridLocation, LocationListener listener, long updateTime) {
		this.l = listener;
		this.t = updateTime;
		this.hl = hybridLocation;
	}
	
	@Override
	public void run() {
		isRunning = true;
		while(isRunning) {
			Log.d("LocationUpdater", "tick");
			try{
				Thread.sleep(t);
			} catch (InterruptedException e) {
				//nothing to do
			}
			Position p = null;
			try {
				p = hl.getPosition();
			} catch(Exception e) {
				Log.e("HybridLocationUpdater", "error getting position");
				e.printStackTrace();
			}
			Log.d("HybridLocationUpdater", "Position: " + p);
			if(p != null && p.getLatitude() != Double.NaN && p.getLongitude() != Double.NaN) {
				p = CoordinateConverter.convertCH1903ToLatLong(p.getLatitude(), p.getLongitude(), p.getAltitude());
				Location location = new Location("HybridLocation");
				location.setLatitude(p.getLatitude());
				location.setLongitude(p.getLongitude());
				location.setAltitude(p.getAltitude());
				l.onLocationChanged(location);
				Log.d("LOCATION", location + "");
			}
		}
	}
	
	public void stopUpdating() {
		isRunning = false;
	}
}
