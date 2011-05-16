package org.pocketcampus.plugin.map.ui;

import org.osmdroid.LocationListenerProxy;
import org.osmdroid.views.MapView;
import org.osmdroid.views.overlay.MyLocationOverlay;
import org.pocketcampus.plugin.map.utils.HybridLocationUpdater;

import android.content.Context;
import android.location.LocationManager;
import android.util.Log;

public class HybridPositioningOverlay extends MyLocationOverlay {
	private static final String TAG = "HybridPositioningOverlay";
	
	/**
	 * The minimum time between two updates
	 */
	private static final long LOCATION_UPDATE_MIN_TIME = 5000;
	private HybridLocationUpdater locationUpdater_;
	
	/**
	 * Never used but necessary to create a valid mLocationListener for the superclass
	 */
	private LocationManager locationManager_;
	
	public HybridPositioningOverlay(final Context context, final MapView mapView) {
		super(context, mapView);
		setLocationUpdateMinTime(LOCATION_UPDATE_MIN_TIME);
		
		locationManager_ = (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);
		
		locationUpdater_ = new HybridLocationUpdater(context, this);
	}
	
	@Override
	public boolean enableMyLocation() {
		try {
			locationUpdater_.startListening();
		} catch (Exception e) {
			Log.e(TAG, "Error starting location updater");
			return false;
		}
		
		mLocationListener = new LocationListenerProxy(locationManager_);
		//Center the map on the current location if enabled
		if(isFollowLocationEnabled()) {
			enableFollowLocation();
		}

        return true;
	}
	
	@Override
	public void disableMyLocation() {
		mLocationListener = null;
		
		try {
			locationUpdater_.stopListening();
		} catch(Exception e) {
			Log.e(TAG, "Error locationUpdater stopListening");
		}
		
		if(mMapView != null) {
			mMapView.postInvalidate();
		}
	}
	
	
}
