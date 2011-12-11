package org.pocketcampus.plugin.transport.server;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.apache.thrift.TException;
import org.pocketcampus.plugin.transport.shared.QueryTripsResult;
import org.pocketcampus.plugin.transport.shared.TransportStation;
import org.pocketcampus.plugin.transport.shared.TransportStationType;
import org.pocketcampus.plugin.transport.shared.QueryDepartureResult;
import org.pocketcampus.plugin.transport.shared.TransportService;

import de.schildbach.pte.NetworkProvider.WalkSpeed;
import de.schildbach.pte.SbbProvider;
import de.schildbach.pte.dto.NearbyStationsResult;

public class TransportServiceImpl implements TransportService.Iface {
	private SbbProvider mSbbProvider;

	public TransportServiceImpl() {
		mSbbProvider = new SbbProvider(
				"MJXZ841ZfsmqqmSymWhBPy5dMNoqoGsHInHbWJQ5PTUZOJ1rLTkn8vVZOZDFfSe");

		try {
			//System.out.println(autocomplete("Neuchatel").get(0).id);
			//System.out.println(connections("EPFL", "Bassenges"));
			// EPFL -> Neuchatel
			ArrayList<Integer> l = new ArrayList<Integer>();
			l.add(new Integer(8501214));
			System.out
					.println(getLocationsFromIDs(l));
			
			System.out.println("---------------------");
			System.out.println(nextDepartures("8501214"));
		} catch (TException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}

		ArrayList<Integer> ids = new ArrayList<Integer>();
		System.out.println("Transport started");
		ids.add(new Integer(8501214));
		ids.add(new Integer(8504221));
		try {
			List<TransportStation> li = getLocationsFromIDs(ids);
			for (TransportStation l : li) {
				if(l != null)
					System.out.println("pouet" + l.name);
			}
		} catch (TException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

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
	
	@Override
	public List<TransportStation> getLocationsFromIDs(List<Integer> ids)
			throws TException {
		ArrayList<TransportStation> locations = new ArrayList<TransportStation>();

		try {
			for (Integer inte : ids) {
				de.schildbach.pte.dto.Location sLocation = new de.schildbach.pte.dto.Location(
						de.schildbach.pte.dto.LocationType.STATION,
						inte.intValue());
				NearbyStationsResult res = mSbbProvider.queryNearbyStations(
						sLocation, 1000, 5);
				
				boolean found = false;
				if (res != null) {
					for(TransportStation loc: SchildbachToPCConverter.convertSchToPC(res.stations))
					{
						if(loc.id == inte.intValue()){
							found = true;
							locations.add(loc);
							break;
						}
							
					}
					
					if(!found)
						locations.add(null);
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
	public QueryTripsResult getTrips(String from, String to)
			throws TException {

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

		Date date = new Date();
		boolean dep = true;
		String products = (String) null;
		WalkSpeed walkSpeed = WalkSpeed.NORMAL;

		QueryTripsResult connections = null;
		try {
			connections = SchildbachToPCConverter.convertSchToPC(mSbbProvider.queryConnections(fromLoc,
					viaLoc, toLoc, date, dep, products, walkSpeed));
		} catch (IOException e) {
			e.printStackTrace();
		}

		return connections;
	}

	@Override
	public QueryTripsResult getTripsFromStationsIDs(String fromID,
			String toID) throws TException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public QueryTripsResult getTripsAtTime(String from, String to, long time,
			boolean isDeparture) throws TException {
		// TODO Auto-generated method stub
		return null;
	}

}
