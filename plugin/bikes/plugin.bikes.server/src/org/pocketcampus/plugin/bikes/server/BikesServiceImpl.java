package org.pocketcampus.plugin.bikes.server;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.apache.thrift.TException;
import org.pocketcampus.plugin.bikes.shared.BikeEmplacement;
import org.pocketcampus.plugin.bikes.shared.BikesService;
import org.pocketcampus.plugin.bikes.shared.WebParseException;

/**
 * Class that manages the services the server side of Directory provides to the client.
 * @author Pascal <pascal.scheiben@gmail.com>
 * @author Guillaumd <guillaume.ulrich@epfl.ch>
 */
public class BikesServiceImpl implements BikesService.Iface{

	/** Refresh time in ms*/
	private final static long REFRESH_TIME = 5 * 60 * 1000;
	
	/** List of <code>bikeEmplacement</code>, used as cache*/
	private ArrayList<BikeEmplacement> bikeStationsCache_;
	/** Date of the last update of the cache*/
	private Date bikeStationsCacheDate_;
	
	/**
	 * Constructor, only initiates the list.
	 */
	public BikesServiceImpl(){
		System.out.println("Starting Bike plugin server");
		bikeStationsCache_ = new ArrayList<BikeEmplacement>();
		
	}
	
	/**
	 * Method called by the client to get the <code>bikeEmplacement</code>.
	 * Checks if a refresh is needed
	 */
	@Override
	public List<BikeEmplacement> getBikeStations() throws WebParseException, TException {
		if(isRefreshNeeded()) {
			loadBikeStations();
		}
		
		return bikeStationsCache_;
	}
	
	/**
	 * Calls <code>BikeStationParser</code> to get the latest info from the velopass webpage
	 * @throws WebParseException If an exception occured while parsing the webpage. This exception is transmitted to the client
	 */
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
	
	/**
	 * Check the difference between <code>BikeStationsCacheDate</code> and the actual time.
	 * 
	 * @return True if the difference is bigger than <code>REFRESH_TIME</code>, False otherwise
	 */
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
