package org.pocketcampus.plugin.positioning;


import java.util.ArrayList;
import java.util.List;
import java.util.Stack;


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
	private long updateTime_ = 2000;//8500;
	
	private boolean isRunning_ = false;
	private LocationUpdater updater_;
	
	public HybridLocationUpdater(Context context, LocationListener listener) {
		this.listener_ = listener;
		try {
			hybridLocation_ = new HybridLocation(context);
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
			updater_ = new LocationUpdater(hybridLocation_, listener_, updateTime_,hybridLocation_.getPosition());
			updater_.start();
			hybridLocation_.startListening();
		}
	}
	
	public synchronized void stopListening() {
		isRunning_ = false;
		if(updater_ != null) {
			updater_.stopUpdating();
			updater_ = null;
		}
		if(hybridLocation_ != null) {
			hybridLocation_.stopListening();
		}
	}
}

class LocationUpdater extends Thread {
	private HybridLocation hl;
	private LocationListener l;
	private long t;
	private boolean isRunning = true;
	private Location firstLocation;
	private Stack<Location> locationsStack;
	
	public LocationUpdater(HybridLocation hybridLocation, LocationListener listener, long updateTime,Location _firstLocation) {
		this.l = listener;
		this.t = updateTime;
		this.hl = hybridLocation;
		this.firstLocation=_firstLocation;
		this.locationsStack = new Stack<Location>(); 
	}
	
	@Override
	public void run() {
		//Location firstLocation; 
		
		isRunning = true;
		while(isRunning) {
//			try{
//				Thread.sleep(t);
//			} catch (InterruptedException e) {
//				//nothing to do
//			}
			Location loc = null;
			for(Location locat:getIntermediateLocations()){
			try {
				Thread.sleep(t/5);
				loc = locat;
			} catch(Exception e) {
				Log.e("HybridLocationUpdater", "error getting position");
				e.printStackTrace();
			}
			Log.d("LocationUpdater", "New location: " + loc);
			if(loc != null) {
				l.onLocationChanged(loc);
			}
			
			}
		}
	}
	
	public void stopUpdating() {
		isRunning = false;
	}
	
	public List<Location> getIntermediateLocations(){
		List<Location> intermediateLocation = new ArrayList<Location>();
		//locationsStack.push(firstLocation);
		Location secondLocation=null;
		Location intermediateResult ;
		double euclidianDistance;
		double x = firstLocation.getLatitude();
		double y = firstLocation.getLongitude();
		double x2,y2;
		try{
			Thread.sleep(t);
		} catch (InterruptedException e) {
			//nothing to do
		}	
		secondLocation = hl.getPosition();
		System.out.println("First Location : "+firstLocation.getLatitude()+" "+firstLocation.getLongitude());
		System.out.println("Second Location : "+secondLocation.getLatitude()+" "+secondLocation.getLongitude());
		//intermediateResult = firstLocation;
		x2 = secondLocation.getLatitude();
		y2 = secondLocation.getLongitude();
		
		System.out.println(" x "+x+" y "+y);
		System.out.println(" x2 "+x2+" y2 "+y2);
		for(int i=0;i<5;i++){
			intermediateResult = new Location("Wifi");
			double x3 = x+(i*(x2-x)/4);
			double y3 = y+(i*(y2-y)/4);
			System.out.println(" x3 :"+x3+" y3"+y3);
			intermediateResult.setLatitude(x3);
			intermediateResult.setLongitude(y3);
			intermediateResult.setAltitude(0.0);
			intermediateResult.setAccuracy(hl.getAccuracy());
			locationsStack.push(intermediateResult);
			intermediateLocation.add(intermediateResult);
		}
		
		for(Location loc:intermediateLocation){
			System.out.println("Intermediate Location : "+loc.getLatitude());
			System.out.println("Intermediate Location : "+loc.getLongitude());
			System.out.println("Intermediate Location : "+loc.getAltitude());
		}
		this.firstLocation = locationsStack.pop();
		return intermediateLocation;
	}
}
