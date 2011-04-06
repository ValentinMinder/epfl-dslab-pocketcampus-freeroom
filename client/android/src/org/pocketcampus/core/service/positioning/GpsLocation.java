/*
 ********************* [ P O C K E T C A M P U S ] *****************
 * [    LICENCE    ] 	see "licence"-file in the root directory
 * [   MAINTAINER  ]	tarek.benoudina@epfl.ch
 * [     STATUS    ]    in progress
 *
 **************************[ C O M M E N T S ]**********************
 *
 *
 *
 *
 *******************************************************************
 */

package org.pocketcampus.core.service.positioning;


/**
 * Author : Tarek
 *          Benoudina
 *          
 * GpsLocation,
 * 
 * returns : The GPS position of the cellPhone .
 * 
 * 
 * 
 */
	import android.content.Context;
	import android.location.LocationManager;

	import org.osmdroid.util.GeoPoint;
	import org.osmdroid.views.MapView;
	import org.osmdroid.views.overlay.MyLocationOverlay;

	public class GpsLocation {
		
		
		private Context ctx_;
	    private LocationManager lm_;
	    private MyLocationOverlay locOverlay_;
	    
	    
	    public GpsLocation(Context _ctx,MapView _mapView){
	    	ctx_ = _ctx;
	    	//lm_ = (LocationManager) ctx_.getSystemService(Context.LOCATION_SERVICE);
	    	locOverlay_ = new MyLocationOverlay(_ctx, _mapView);
	    }
	    
	    public GeoPoint getGpsLocation(){
	    	
	    	if(!(locOverlay_.isMyLocationEnabled()))
	    		locOverlay_.enableMyLocation();
	    	
	    	return locOverlay_.getMyLocation();
	    }
	    
	    public double getLatitude(){
	    	
	    	return this.locOverlay_.getMyLocation().getLatitudeE6();
	    }
	    
	    public double getLongitude(){
	    	
	    	return this.locOverlay_.getMyLocation().getLongitudeE6();
	    }
	    
	 
	    public MyLocationOverlay getLocOverlay_(){
	    	
	    	return locOverlay_;
	    }
	    
	    
	}
