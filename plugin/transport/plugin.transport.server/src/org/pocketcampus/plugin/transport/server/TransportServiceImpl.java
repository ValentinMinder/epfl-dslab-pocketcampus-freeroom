package org.pocketcampus.plugin.transport.server;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.apache.thrift.TException;
import org.pocketcampus.plugin.transport.shared.QueryDepartureResult;
import org.pocketcampus.plugin.transport.shared.QueryTripsResult;
import org.pocketcampus.plugin.transport.shared.TransportService;
import org.pocketcampus.plugin.transport.shared.TransportStation;
import org.pocketcampus.plugin.transport.shared.TransportStationType;

import de.schildbach.pte.NetworkProvider.WalkSpeed;
import de.schildbach.pte.SbbProvider;
import de.schildbach.pte.dto.Location;
import de.schildbach.pte.dto.LocationType;
import de.schildbach.pte.dto.NearbyStationsResult;

public class TransportServiceImpl implements TransportService.Iface {
	private SbbProvider mSbbProvider;

	public TransportServiceImpl() {
		mSbbProvider = new SbbProvider(
				"MJXZ841ZfsmqqmSymWhBPy5dMNoqoGsHInHbWJQ5PTUZOJ1rLTkn8vVZOZDFfSe");
		
		System.out.println("Transport started");
		
		//testing getLocationsFromIDs
//		ArrayList<Integer> l = new ArrayList<Integer>();
//		l.add(new Integer(8501214));
//		l.add(new Integer(8501215));
//		l.add(new Integer(8501216));
//		l.add(new Integer(8501217));
//		l.add(new Integer(8501218));
//		l.add(new Integer(8504221));
//		
//		try {
//			for(TransportStation loc : getLocationsFromIDs(l)){
//				if(loc != null)
//					System.out.println(loc.name);
//				else
//					System.out.println("no corresponding station was found");
//			}
//		} catch (TException e) {
//			System.out.println("something very bad happend, you probably gonna die");
//		}
		

//		try {
//			//System.out.println(autocomplete("Neuchatel").get(0).id);
//			QueryTripsResult res = getTrips("EPFL", "Neuchatel");
//			QueryTripsResult res = getTripsFromStationsIDs("8501214", "8504221");
//			System.out.println("from "+ res.from.name + " to " + res.to.name);
//			for(TransportTrip tt : res.connections){
//				System.out.println(new Date(tt.departureTime));
//			}
//			// EPFL -> Neuchatel
//			
//			
//			
			//testing newDepartures
//			QueryDepartureResult q = nextDepartures("8501214");
//			for(StationDepartures s :q.stationDepartures){
//				for(Departure d : s.departures){
//					System.out.println(d.destination + " with " + d.line + " at " + (new Date(d.plannedTime)).toString());
//				}
//			}
//		} catch (TException e1) {
//			// TODO Auto-generated catch block
//			e1.printStackTrace();
//		}

	}

	@Override
	public List<TransportStation> autocomplete(String constraint) throws TException {
		System.out.println("autocomplete");

		List<de.schildbach.pte.dto.Location> sbbCompletions = null;
		try {
			sbbCompletions = mSbbProvider.autocompleteStations(constraint);
		} catch (IOException e) {
			e.printStackTrace();
		}

		List<TransportStation> completions = new ArrayList<TransportStation>();
		for (de.schildbach.pte.dto.Location location : sbbCompletions) {
			completions.add(new TransportStation(TransportStationType.ANY, location.id,
					location.lat, location.lon, location.place, location.name));
		}

		return completions;
	}

	@Override
	public List<TransportStation> getLocationsFromNames(List<String> names) throws TException {
		ArrayList<TransportStation> locList = new ArrayList<TransportStation>();
		
		for(String name: names){
			try {
				TransportStation loc = SchildbachToPCConverter.convertSchToPC(mSbbProvider.autocompleteStations(name).get(0));
				locList.add(loc);
			} catch (IOException e) {
				System.out.println("could not get stations from name: " + name);
			}
		}
		
		return locList;
	}
	
	
	/**
	 *Returns a TransportStation list with the stations corresponding to the integers id list of the param 
	 * if an id has not been found, the corresponding TransportStation in the result will be null
	 * 
	 * DOES NOT WORK FOR NOW, SHOULD TRY WITH THE UPDATE OF THE SCHILDBACH SDK
	 */
	@Override
	public List<TransportStation> getLocationsFromIDs(List<Integer> ids)
			throws TException {
		ArrayList<TransportStation> locations = new ArrayList<TransportStation>();

		try {
			for (Integer inte : ids) {
				de.schildbach.pte.dto.Location sLocation = new de.schildbach.pte.dto.Location(
						de.schildbach.pte.dto.LocationType.STATION,
						inte.intValue());
				NearbyStationsResult res = mSbbProvider.queryNearbyStations(sLocation, 100000, 5);

				
				if (res != null) {
					boolean found = false;
					
					List<TransportStation> ts_list = SchildbachToPCConverter.convertSchToPC(res.stations);
					System.out.println(res.stations.size());
					for(TransportStation loc: ts_list)
					{
						if(loc.id == inte.intValue()){
							System.out.println(loc);
							found = true;
							locations.add(loc);
							break;
						}
							
					}
					
					if(!found){
						locations.add(null);
						System.out.println(inte.intValue() + " has not been found");
					}
				} else {
					System.out.println(res);
				}
			}

		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return locations;
	}

	@Override
	public QueryDepartureResult nextDepartures(String IDStation)
			throws TException {

		if (IDStation == null) {
			return null;
		}

		QueryDepartureResult nextDepartures = null;

		try {
			
			nextDepartures = SchildbachToPCConverter.convertSchToPC(mSbbProvider.queryDepartures(
					Integer.parseInt(IDStation), 5, false));
		} catch (IOException e) {
			e.printStackTrace();
		}

		return nextDepartures;
	}

	@Override
	public QueryTripsResult getTrips(String from, String to) throws TException {

		long time = (new Date()).getTime();
		return getTripsFromSchildbach(from, to, time, true);
	}

	@Override
	public QueryTripsResult getTripsAtTime(String from, String to, long time, boolean isDeparture) throws TException {
		
		return getTripsFromSchildbach(from, to, time, isDeparture);
	}
	
	private QueryTripsResult getTripsFromSchildbach(String from, String to, long time, boolean isDeparture){
		
		if (from == null || to == null) {
			return null;
		}

		de.schildbach.pte.dto.Location fromLoc = null, viaLoc = null, toLoc = null;

		try {
			// FIXME autocomplete not optimal, use connectionsFromStationsIDs
			// instead
			fromLoc = mSbbProvider.autocompleteStations(from).get(0);
			toLoc = mSbbProvider.autocompleteStations(to).get(0);

		} catch (IOException e) {
			e.printStackTrace();
		}

		if (fromLoc == null || toLoc == null) {
			return null;
		}

		Date date = new Date(time);
		String products = (String) null;
		WalkSpeed walkSpeed = WalkSpeed.NORMAL;

		QueryTripsResult tripResults = null;
		try {
			tripResults = SchildbachToPCConverter.convertSchToPC(mSbbProvider.queryConnections(fromLoc,
					viaLoc, toLoc, date, isDeparture, products, walkSpeed));
		} catch (IOException e) {
			e.printStackTrace();
		}
		System.out.println(tripResults.connections.get(0).parts.get(0).departureTime);
		return tripResults;
	}
	

	@Override
	public QueryTripsResult getTripsFromStationsIDs(String fromID,
			String toID) throws TException {

		de.schildbach.pte.dto.Location fromLoc = null, viaLoc = null, toLoc = null;
		fromLoc = new Location(LocationType.STATION, Integer.parseInt(fromID));
		toLoc   = new Location(LocationType.STATION, Integer.parseInt(toID));
		
		QueryTripsResult tripResults = null;
		try {
			String products = (String) null;
			WalkSpeed walkSpeed = WalkSpeed.NORMAL;
			tripResults = SchildbachToPCConverter.convertSchToPC(mSbbProvider.queryConnections(fromLoc, viaLoc, toLoc, new Date(), true, products, walkSpeed));
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		
		return tripResults;
	}

}
