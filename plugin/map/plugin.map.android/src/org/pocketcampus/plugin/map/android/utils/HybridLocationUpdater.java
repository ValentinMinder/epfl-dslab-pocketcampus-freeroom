//package org.pocketcampus.plugin.map.utils;
//
//import org.pocketcampus.plugin.positioning.HybridLocation;
//
//import android.content.Context;
//import android.location.Location;
//import android.location.LocationListener;
//import android.util.Log;
//
//public class HybridLocationUpdater {
//	private LocationListener listener_;
//	private HybridLocation hybridLocation_;
//	
//	/**
//	 * The number of ms between two updates
//	 */
//	private long updateTime_ = 5000;//8500;
//	
//	private boolean isRunning_ = false;
//	private LocationUpdater updater_;
//	
//	public HybridLocationUpdater(Context context, LocationListener listener) {
//		this.listener_ = listener;
//		try {
//			hybridLocation_ = new HybridLocation(context);
//		} catch (Exception e) {
//			Log.e("HybridLocationUpdater", "Error creating HybridLocation instance");
//			e.printStackTrace();
//		}
//	}
//	
//	public synchronized void startListening() {
//		if(hybridLocation_ == null)
//			return;
//		
//		if(!isRunning_) {
//			isRunning_ = true;
//			updater_ = new LocationUpdater(hybridLocation_, listener_, updateTime_);
//			updater_.start();
//			hybridLocation_.startListening();
//		}
//	}
//	
//	public synchronized void stopListening() {
//		isRunning_ = false;
//		if(updater_ != null) {
//			updater_.stopUpdating();
//			updater_ = null;
//		}
//		if(hybridLocation_ != null) {
//			hybridLocation_.stopListening();
//		}
//	}
//}
//
//class LocationUpdater extends Thread {
//	private HybridLocation hl;
//	private LocationListener l;
//	private long t;
//	private boolean isRunning = true;
//	
//	public LocationUpdater(HybridLocation hybridLocation, LocationListener listener, long updateTime) {
//		this.l = listener;
//		this.t = updateTime;
//		this.hl = hybridLocation;
//	}
//	
//	@Override
//	public void run() {
//		isRunning = true;
//		while(isRunning) {
//			try{
//				Thread.sleep(t);
//			} catch (InterruptedException e) {
//				//nothing to do
//			}
//			Location loc = null;
//			try {
//				loc = hl.getPosition();
//			} catch(Exception e) {
//				Log.e("HybridLocationUpdater", "error getting position");
//				e.printStackTrace();
//			}
//			Log.d("LocationUpdater", "New location: " + loc);
//			if(loc != null) {
//				l.onLocationChanged(loc);
//			}
//		}
//	}
//	
//	public void stopUpdating() {
//		isRunning = false;
//	}
//}
