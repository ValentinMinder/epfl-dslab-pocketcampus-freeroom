package org.pocketcampus.plugin.positioning;

import org.pocketcampus.provider.positioning.IPositionProvider;
import org.pocketcampus.plugin.positioning.GpsLocation;
import org.pocketcampus.plugin.positioning.WifiLocation;
import org.pocketcampus.shared.plugin.map.Position;

import android.content.Context;
import android.location.Location;

public class HybridLocation implements IPositionProvider{
	
	private GpsLocation gpsLocation_;
	private GsmNetworkPosition gsmLocation_;
	private WifiLocation wifiLocation_;
	private Context ctx_;
	
	public HybridLocation(Context _ctx){
		this.ctx_ = _ctx;
		this.gpsLocation_ = new GpsLocation(ctx_);
		this.gsmLocation_ = new GsmNetworkPosition(ctx_);
		this.wifiLocation_ = new WifiLocation(ctx_);
	}

	
	
	
	public Location getGsmPosition() {
		return this.gsmLocation_.getLocation();
	}
	
	
	public Location getGpsPosition(){
		return this.gpsLocation_.getLocation();
	}
	
	public Position getWifiLocation(){
		return wifiLocation_.getWifiLocationPerCoefficient();
		// return wifiLocation_.getWifiLocationPerTaylorSerieGlobal();
	}
	
	
	
	public Position getCombinedLocation(){
		Position centroid = wifiLocation_.getWifiLocationPerCoefficient();
		Position taylor = wifiLocation_.getWifiLocationPerTaylorSerieGlobal();
		Position result = centroid;
		if(taylor!=null){
			if(Math.pow((centroid.getLatitude()-taylor.getLatitude()),2)+Math.pow((centroid.getLongitude()-taylor.getLongitude()), 2)<20)
				result = new Position((centroid.getLatitude()+taylor.getLatitude())/2,(centroid.getLongitude()+taylor.getLongitude())/2,0.0);
		        //result = taylor ;
		       }
		return result;
		
	}
	
	
	public Position getHybridLocation(){
		return null;
		// TODO
	}




	@Override
	public Position getPosition() {
		if(wifiLocation_!=null)
			 return getWifiLocation();
		else return null;
	}




	@Override
	public double getAccuracy() {
		// TODO Auto-generated method stub
		return (Double) null;
	}




	@Override
	public boolean userInCampus() {
		if(wifiLocation_.getAccessPoints().size()==0)
		return false;
		else return true;
	}
	
	@Override
	public void startListening() {
		gsmLocation_.startListening();
		gpsLocation_.startListening();
	}
	
	@Override
	public void stopListening() {
		gsmLocation_.stopListening();
		gpsLocation_.stopListening();
	}
}



