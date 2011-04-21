package org.pocketcampus.plugin.positioning;

import java.io.IOException;

import org.osmdroid.util.GeoPoint;
import org.osmdroid.views.MapView;
import org.pocketcampus.provider.positioning.ILocation;
//import org.pocketcampus.provider.positioning.Position;
import org.pocketcampus.plugin.positioning.GpsLocation;
import org.pocketcampus.plugin.positioning.GsmLocation;
import org.pocketcampus.plugin.positioning.WifiLocation;
import org.pocketcampus.shared.plugin.map.Position;

import android.content.Context;

public class HybridLocation implements ILocation{
	
	private GpsLocation gpsLocation_;
	private GsmLocation gsmLocation_;
	private WifiLocation wifiLocation_;
	private Context ctx_;
	private MapView mapView_;
	
	
	public HybridLocation(Context _ctx , MapView _map){
		
		this.ctx_ = _ctx;
		this.mapView_ = _map;
		this.gpsLocation_ = new GpsLocation(ctx_,mapView_);
		this.gsmLocation_ = new GsmLocation(ctx_);
		this.wifiLocation_ = new WifiLocation(ctx_);
	}

	
	
	
	public GeoPoint getGsmLocation() throws IOException{
		return this.gsmLocation_.getGSMLocation();
	}
	
	
	public GeoPoint getGpsLocation(){
		return this.gpsLocation_.getGpsLocation();
	}
	
	public Position getWifiLocation(){
		return null;
		// to do 
	}
	
	
	public Position getHybridLocation(){
		return null;
		// TODO
	}




	@Override
	public Position getPosition() {
		// TODO Auto-generated method stub
		return null;
	}




	@Override
	public double getAccuracy() {
		// TODO Auto-generated method stub
		return (Double) null;
	}
}



