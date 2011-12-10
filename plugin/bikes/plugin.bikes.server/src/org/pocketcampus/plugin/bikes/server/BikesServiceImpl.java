package org.pocketcampus.plugin.bikes.server;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.apache.thrift.TException;
import org.pocketcampus.plugin.bikes.shared.BikeEmplacement;
import org.pocketcampus.plugin.bikes.shared.BikeService;
import org.pocketcampus.plugin.bikes.shared.WebParseException;

public class BikesServiceImpl implements BikeService.Iface{

private final static long REFRESH_TIME = 5 * 60 * 1000;
	
	private ArrayList<BikeEmplacement> bikeStationsCache_;
	private Date bikeStationsCacheDate_;
	
	public BikesServiceImpl(){
		System.out.println("Starting Bike plugin server");
		bikeStationsCache_ = new ArrayList<BikeEmplacement>();
		
	}
	
	
	@Override
	public List<BikeEmplacement> getBikeStations() throws WebParseException, TException {
		if(isRefreshNeeded()) {
			loadBikeStations();
		}
		
		return bikeStationsCache_;
	}
	
	private void loadBikeStations() throws WebParseException {
		try {
			bikeStationsCache_ = new BikeStationParser().parseBikesStations();
			bikeStationsCacheDate_ = new Date();
			
		} catch (IOException e) {
			bikeStationsCache_ = null;
			bikeStationsCacheDate_ = null;
			throw new WebParseException("Could not get bike informations");
			
			
		}
	}
	
	private boolean isRefreshNeeded() {
		// data couldn't be loaded the previous time
		if(bikeStationsCache_ == null || bikeStationsCacheDate_ == null) {
			return true;
		}
		
		// data is outdated
		if(((new Date()).getTime() - bikeStationsCacheDate_.getTime()) > REFRESH_TIME) {
			return true;
		}
		
		return false;
	}

}
