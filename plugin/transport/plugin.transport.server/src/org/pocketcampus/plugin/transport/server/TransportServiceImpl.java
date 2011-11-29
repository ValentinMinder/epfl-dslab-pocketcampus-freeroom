package org.pocketcampus.plugin.transport.server;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;

import org.apache.thrift.TException;
import org.pocketcampus.plugin.transport.shared.Departure;
import org.pocketcampus.plugin.transport.shared.FareType;
import org.pocketcampus.plugin.transport.shared.Line;
import org.pocketcampus.plugin.transport.shared.LineDestination;
import org.pocketcampus.plugin.transport.shared.Location;
import org.pocketcampus.plugin.transport.shared.Connection;
import org.pocketcampus.plugin.transport.shared.Fare;
import org.pocketcampus.plugin.transport.shared.NearbyStatus;
import org.pocketcampus.plugin.transport.shared.Point;
import org.pocketcampus.plugin.transport.shared.Part;
import org.pocketcampus.plugin.transport.shared.LocationType;
import org.pocketcampus.plugin.transport.shared.QueryConnectionsResult;
import org.pocketcampus.plugin.transport.shared.QueryDepartureResult;
import org.pocketcampus.plugin.transport.shared.StationDepartures;
import org.pocketcampus.plugin.transport.shared.Stop;
import org.pocketcampus.plugin.transport.shared.TransportService;

import de.schildbach.pte.NetworkProvider.WalkSpeed;
import de.schildbach.pte.SbbProvider;
import de.schildbach.pte.dto.NearbyStationsResult;
import de.schildbach.pte.dto.QueryDeparturesResult;


public class TransportServiceImpl implements TransportService.Iface {
	private SbbProvider mSbbProvider;
	
	public TransportServiceImpl() {
		mSbbProvider = new SbbProvider("MJXZ841ZfsmqqmSymWhBPy5dMNoqoGsHInHbWJQ5PTUZOJ1rLTkn8vVZOZDFfSe");
		
		try {
			System.out.println(autocomplete("Neuchatel").get(0).id);
			System.out.println(connections("EPFL", "Bassenges"));
			// EPFL -> Neuchatel
			System.out.println(connectionsFromStationsIDs("8501214", "8504221"));
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
				NearbyStationsResult res = mSbbProvider.queryNearbyStations(sLocation, 0, 0);
				if(res != null ){
					locations.addAll((convertSchToPC(res.stations)));
					System.out.println("haha " + res.status + ": " + res.stations.size());
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
	public QueryDepartureResult nextDepartures(String IDStation) throws TException {
			
		if(IDStation == null) {
			return null;
		}
		
		QueryDepartureResult nextDepartures = null;
		
		try {
			nextDepartures = convertSchToPC(mSbbProvider.queryDepartures(Integer.parseInt(IDStation), 5, false));
		} catch (IOException e) {
			e.printStackTrace();
		}

		return nextDepartures;
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
		QueryConnectionsResult qcr = new QueryConnectionsResult(
				s.queryUri,
				convertSchToPC(s.from),
				convertSchToPC(s.via),
				convertSchToPC(s.to),
				s.context,
				convertSchConToPC(s.connections)
		);
		qcr.ambiguousFrom = convertSchToPC(s.ambiguousFrom);
		qcr.ambiguousVia = convertSchToPC(s.ambiguousVia);
		qcr.ambiguousTo = convertSchToPC(s.ambiguousTo);
		return qcr;
	}
	
	private QueryDepartureResult convertSchToPC(QueryDeparturesResult sq) {
		return new QueryDepartureResult(convertSchToPC(sq.status),
				convertSchStaDepToPC(sq.stationDepartures));
	}
	
	//STATIONS DEPARTURS
	private List<StationDepartures> convertSchStaDepToPC(List<de.schildbach.pte.dto.StationDepartures> l){
		LinkedList<StationDepartures> ret = new LinkedList<StationDepartures>();
		for(de.schildbach.pte.dto.StationDepartures sd : l){
			ret.add(convertSchToPC(sd));
		}
		return ret;
	}
	
	private StationDepartures convertSchToPC(de.schildbach.pte.dto.StationDepartures sf){
		return new StationDepartures(convertSchToPC(sf.location),
				convertSchDepToPC(sf.departures),
				convertSchLiDesToPC(sf.lines));
	}

	private List<LineDestination> convertSchLiDesToPC(List<de.schildbach.pte.dto.LineDestination> lines) {
		if(lines == null)
			return null;
		
		LinkedList<LineDestination> ret = new LinkedList<LineDestination>();
		for(de.schildbach.pte.dto.LineDestination sd : lines){
			ret.add(convertSchToPC(sd));
		}
		return ret;
	}

	private LineDestination convertSchToPC(de.schildbach.pte.dto.LineDestination sd) {
		return new LineDestination(sd.line.label, null, sd.destinationId, sd.destination);
	}

	// DEPARTURES
	private List<Departure> convertSchDepToPC(List<de.schildbach.pte.dto.Departure> ld) {
		LinkedList<Departure> ret = new LinkedList<Departure>();
		for(de.schildbach.pte.dto.Departure d : ld){
			ret.add(convertSchToPC(d));
		}
		return ret;
	}

	private Departure convertSchToPC(de.schildbach.pte.dto.Departure d) {
		long plt = 0, prt = 0;
		if(d.plannedTime!= null)
			plt = d.plannedTime.getTime();
		
		if(d.predictedTime != null)
			prt = d.predictedTime.getTime();
		
		return new Departure(plt,
				prt,
				d.line.label, 
				null, "", d.position, d.destinationId, d.destination, d.message);
	}

	private NearbyStatus convertSchToPC(de.schildbach.pte.dto.QueryDeparturesResult.Status ss) {
		switch(ss){
		case INVALID_STATION:
			return NearbyStatus.INVALID_STATION;
		case OK:
			return NearbyStatus.OK;
		case SERVICE_DOWN:
			default:
				return NearbyStatus.SERVICE_DOWN;
		}
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
		Part part = new Part(convertSchToPC(sf.departure),
				convertSchToPC(sf.arrival),
				convertSchPointsToPC(sf.path));
		
		if(sf instanceof de.schildbach.pte.dto.Connection.Trip ){
			de.schildbach.pte.dto.Connection.Trip sft = (de.schildbach.pte.dto.Connection.Trip) sf;
			
			part.line = convertSchToPC(sft.line);
			part.destination = convertSchToPC(sft.destination);
			part.departureTime = sft.departureTime.getTime();
			part.arrivalTime = sft.arrivalTime.getTime();
			part.departurePosition = sft.departurePosition;
			part.arrivalPosition = sft.arrivalPosition;
			part.intermediateStops = convertSchStopToPC(sft.intermediateStops);
		}
		if(sf instanceof de.schildbach.pte.dto.Connection.Footway){
			de.schildbach.pte.dto.Connection.Footway sff = (de.schildbach.pte.dto.Connection.Footway) sf;
			part.foot = true;
			part.min = sff.min;
			
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


