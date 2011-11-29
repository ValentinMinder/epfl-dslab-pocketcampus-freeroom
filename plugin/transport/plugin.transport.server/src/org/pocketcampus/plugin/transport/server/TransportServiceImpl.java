package org.pocketcampus.plugin.transport.server;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;

import org.apache.thrift.TException;
import org.pocketcampus.plugin.transport.shared.FareType;
import org.pocketcampus.plugin.transport.shared.Line;
import org.pocketcampus.plugin.transport.shared.Location;
import org.pocketcampus.plugin.transport.shared.Connection;
import org.pocketcampus.plugin.transport.shared.Fare;
import org.pocketcampus.plugin.transport.shared.NearbyStatus;
import org.pocketcampus.plugin.transport.shared.Point;
import org.pocketcampus.plugin.transport.shared.Part;
import org.pocketcampus.plugin.transport.shared.LocationType;
import org.pocketcampus.plugin.transport.shared.QueryConnectionsResult;
import org.pocketcampus.plugin.transport.shared.QueryDepartureResult;
import org.pocketcampus.plugin.transport.shared.Stop;
import org.pocketcampus.plugin.transport.shared.TransportService;

import de.schildbach.pte.NetworkProvider.WalkSpeed;
import de.schildbach.pte.SbbProvider;
import de.schildbach.pte.dto.NearbyStationsResult;


public class TransportServiceImpl implements TransportService.Iface {
	private SbbProvider mSbbProvider;
	
	public TransportServiceImpl() {
		mSbbProvider = new SbbProvider("MJXZ841ZfsmqqmSymWhBPy5dMNoqoGsHInHbWJQ5PTUZOJ1rLTkn8vVZOZDFfSe");
		
		try {
			System.out.println(autocomplete("Neuchatel").get(0).id);
			System.out.println(connections("neuchatel", "EPFL"));
			// EPFL -> Neuchatel
			System.out.println(connectionsFromStationsIDs("8501214", "8504221"));
		} catch (TException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		
		
		
		ArrayList<Integer> ids = new ArrayList<Integer>();
		System.out.println("Transport started");
		ids.add(new Integer(8501214));
		ids.add(new Integer(8504221));
		try {
			List<Location> li = getLocationsFromIDs(ids);
			for(Location l : li){
				System.out.println("pouet" + l.name);
			}
		} catch (TException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}
	
	@Override
	public List<Location> autocomplete(String constraint) throws TException {
		System.out.println("autocomplete");
		
		List<de.schildbach.pte.dto.Location> sbbCompletions = null;
		try {
			sbbCompletions = mSbbProvider.autocompleteStations(constraint);
		} catch (IOException e) {
			e.printStackTrace();
		}

		List<Location> completions = new ArrayList<Location>();
		for(de.schildbach.pte.dto.Location location : sbbCompletions) {
			completions.add(new Location(LocationType.ANY , location.id, location.lat, location.lon, location.place, location.name));
		}
		
		return completions;
	}

	@Override
	public List<Location> getLocationsFromIDs(List<Integer> ids) throws TException {
		ArrayList<Location> locations = new ArrayList<Location>();
		
		
		try {
			for(Integer inte: ids){
				de.schildbach.pte.dto.Location sLocation = new de.schildbach.pte.dto.Location(de.schildbach.pte.dto.LocationType.STATION, inte.intValue());
				NearbyStationsResult res = mSbbProvider.queryNearbyStations(sLocation, 15000, 10);
				if(res != null ){
					locations.addAll((convertSchToPC(res.stations)));
					System.out.println("haha");
				}else{
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
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public QueryConnectionsResult connections(String from, String to) throws TException {
		
		if(from==null || to==null) {
			return null;
		}
		
		de.schildbach.pte.dto.Location fromLoc = null, viaLoc = null, toLoc = null;
		
		try {
			// FIXME autocomplete not optimal, use connectionsFromStationsIDs instead
			fromLoc = mSbbProvider.autocompleteStations(from).get(0);
			toLoc = mSbbProvider.autocompleteStations(to).get(0);
			
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		if(fromLoc==null || toLoc==null) {
			return null;
		}
		
		Date date = new Date();
		boolean dep = true;
		String products = (String)null;
		WalkSpeed walkSpeed = WalkSpeed.NORMAL;
		
		QueryConnectionsResult connections = null;
		try {
			connections = convertSchToPC(mSbbProvider.queryConnections(fromLoc, viaLoc, toLoc, date, dep, products, walkSpeed));
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		return connections;
	}

	@Override
	public QueryConnectionsResult connectionsFromStationsIDs(String fromID,
			String toID) throws TException {
		// TODO Auto-generated method stub
		return null;
	}
	
	
	//////////////////////////////////////////////////
	//////////////////////////////////////////////////
	//////////////////////////////////////////////////
	//////////////////////////////////////////////////
	//////////////////////////////////////////////////
	////////////// CONVERTERS ////////////////////////
	//////////////////////////////////////////////////
	//////////////////////////////////////////////////
	//////////////////////////////////////////////////
	//////////////////////////////////////////////////
	
	private QueryConnectionsResult convertSchToPC(de.schildbach.pte.dto.QueryConnectionsResult s){
		return new QueryConnectionsResult(convertSchToPC(s.ambiguousFrom),
										convertSchToPC(s.ambiguousVia),
										convertSchToPC(s.ambiguousTo),
										s.queryUri,
										convertSchToPC(s.from),
										convertSchToPC(s.via),
										convertSchToPC(s.to),
										s.context,
										convertSchConToPC(s.connections)
				);
	}
	
	private Location convertSchToPC(de.schildbach.pte.dto.Location s){
		if(s != null)
			return new Location(convertSchToPC(s.type), s.id, s.lat, s.lon, s.place, s.name);
		else
			return null;
	}

	private LocationType convertSchToPC(de.schildbach.pte.dto.LocationType type) {
		switch(type){
			case ADDRESS :
				return LocationType.ADDRESS;
			case STATION :
				return LocationType.STATION;
			case POI	:
				return LocationType.POI;
			case ANY	:
			default		:
				return	LocationType.ANY;
		}
	}

	private List<Location> convertSchToPC(List<de.schildbach.pte.dto.Location> l){
		if(l== null)
			return null;
		
		LinkedList<Location> ret = new LinkedList<Location>();
		for(de.schildbach.pte.dto.Location loc : l){
			ret.add(convertSchToPC(loc));
		}
		return ret;
	}

	private Connection convertSchToPC(de.schildbach.pte.dto.Connection sc){
		return new Connection(sc.id,
				sc.link,
				sc.departureTime.getTime(),
				sc.arrivalTime.getTime(),
				convertSchToPC(sc.from),
				convertSchToPC(sc.to),
				convertSchPartsToPC(sc.parts),
				convertSchFaresToPC(sc.fares)
				);
	}
	
	private List<Connection> convertSchConToPC(List<de.schildbach.pte.dto.Connection> l){
		if(l == null)
			return null;
		
		LinkedList<Connection> ret = new LinkedList<Connection>();
		for(de.schildbach.pte.dto.Connection con : l){
			ret.add(convertSchToPC(con));
		}
		return ret;
	}
	
	// PARTS
	
	private List<Part> convertSchPartsToPC(List<de.schildbach.pte.dto.Connection.Part> l){
		LinkedList<Part> ret = new LinkedList<Part>();
		for(de.schildbach.pte.dto.Connection.Part part : l){
			ret.add(convertSchToPC(part));
		}
		return ret;
	}
	
	private Part convertSchToPC(de.schildbach.pte.dto.Connection.Part sf){
		Part part = null;
		if(sf instanceof de.schildbach.pte.dto.Connection.Trip ){
			part = new Part(convertSchToPC(sf.departure),
					convertSchToPC(sf.arrival),
					convertSchPointsToPC(sf.path));
			de.schildbach.pte.dto.Connection.Trip sft = (de.schildbach.pte.dto.Connection.Trip) sf;
			
			part.line = convertSchToPC(sft.line);
			part.destination = convertSchToPC(sft.destination);
			part.departureTime = sft.departureTime.getTime();
			part.arrivalTime = sft.arrivalTime.getTime();
			part.departurePosition = sft.departurePosition;
			part.arrivalPosition = sft.arrivalPosition;
			part.intermediateStops = convertSchStopToPC(sft.intermediateStops);
		}
		return part;
	}
	
	//STOP
	private Stop convertSchToPC(de.schildbach.pte.dto.Stop s){
		return new Stop(convertSchToPC(s.location),
				s.position,
				s.time.getTime());
	}
	
	private List<Stop> convertSchStopToPC(List<de.schildbach.pte.dto.Stop> ls){
		LinkedList<Stop> ret = new LinkedList<Stop>();
		for(de.schildbach.pte.dto.Stop s : ls){
			ret.add(convertSchToPC(s));
		}
		return ret;
	}
	
	//LINE
	private Line convertSchToPC(de.schildbach.pte.dto.Line sl){
		ArrayList<String> al = new ArrayList<String>();
		for(int i : sl.colors){
			al.add(Integer.toString(i));
		}
		return new Line(sl.label, al);
	}
	
	//POINTS
	private List<Point> convertSchPointsToPC(List<de.schildbach.pte.dto.Point> l){
		if(l == null)
			return null;
		
		LinkedList<Point> ret = new LinkedList<Point>();
		for(de.schildbach.pte.dto.Point p : l){
			ret.add(convertSchToPC(p));
		}
		return ret;
	}
	
	private Point convertSchToPC(de.schildbach.pte.dto.Point sf){
		return new Point(sf.lat, sf.lon);
	}
	
	// FARE
	private List<Fare> convertSchFaresToPC(List<de.schildbach.pte.dto.Fare> l){
		if(l == null)
			return null;
		LinkedList<Fare> ret = new LinkedList<Fare>();
		for(de.schildbach.pte.dto.Fare fare : l){
			ret.add(convertSchToPC(fare));
		}
		return ret;
	}
	
	private Fare convertSchToPC(de.schildbach.pte.dto.Fare sf){
		return new Fare(sf.network, convertSchToPC(sf.type), sf.currency.toString(), (double)sf.fare, sf.unitName, sf.units);
	}
	
	private FareType convertSchToPC(de.schildbach.pte.dto.Fare.Type f){
		switch(f){
		case ADULT:
			return FareType.ADULT;
		case CHILD:
			return FareType.CHILD;
		case YOUTH:
			return FareType.YOUTH;
		case STUDENT:
			return FareType.STUDENT;
		case SENIOR:
			return FareType.SENIOR;
		case MILITARY:
			return FareType.MILITARY;
		case DISABLED:
		default:
			return FareType.DISABLED;
		}
	}


}


