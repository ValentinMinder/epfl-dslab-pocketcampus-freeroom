package org.pocketcampus.plugin.transport;


import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.pocketcampus.core.plugin.IPlugin;
import org.pocketcampus.core.plugin.PublicMethod;
import org.pocketcampus.shared.plugin.transport.Connection;
import org.pocketcampus.shared.plugin.transport.GetConnectionDetailsResult;
import org.pocketcampus.shared.plugin.transport.Location;
import org.pocketcampus.shared.plugin.transport.LocationType;
import org.pocketcampus.shared.plugin.transport.NearbyStationsResult;
import org.pocketcampus.shared.plugin.transport.QueryConnectionsResult;
import org.pocketcampus.shared.plugin.transport.QueryDeparturesResult;
import org.pocketcampus.shared.plugin.transport.Point;


import de.schildbach.pte.BahnProvider;
import de.schildbach.pte.NetworkProvider.WalkSpeed;
import de.schildbach.pte.SbbProvider;

public class Transport implements IPlugin {

	private SbbProvider sbbProvider_;

	public Transport() {
		sbbProvider_ = new SbbProvider("MJXZ841ZfsmqqmSymWhBPy5dMNoqoGsHInHbWJQ5PTUZOJ1rLTkn8vVZOZDFfSe");
	}

	@PublicMethod
	public Object autocomplete(HttpServletRequest request) {
		String constraint = request.getParameter("constraint");

		if(constraint == null) {
			return null;
		}

		List<Location> completions = null;
		try {
			completions = sbbProvider_.autocompleteStations(constraint);
		} catch (IOException e) {
			e.printStackTrace();
		}

		return completions;
	}

	@PublicMethod
	public Object nextDepartures(HttpServletRequest request) {
		String idStation = request.getParameter("idStation");
		
		if(idStation == null) {
			return null;
		}
		
		QueryDeparturesResult nextDepartures = null;
		
		try {
			nextDepartures = sbbProvider_.queryDepartures(idStation, 5, false);
		} catch (IOException e) {
			e.printStackTrace();
		}

		return nextDepartures;
	}
	
	@PublicMethod
	public Object nearbyStations(HttpServletRequest request) {
		String idStation = request.getParameter("idStation");
		
		if(idStation == null) {
			return null;
		}
		
		NearbyStationsResult nearbyStations = null;
		
		try {
			nearbyStations = sbbProvider_.nearbyStations(idStation, 46520381, 6568444, 1000, 3);
		} catch (IOException e) {
			e.printStackTrace();
		}

		return nearbyStations;
	}
	
	@PublicMethod
	public Object connections(HttpServletRequest request) {
		String fromConstraint = request.getParameter("from");
		String toConstraint = request.getParameter("to");
		
		if(fromConstraint==null || toConstraint==null) {
			return null;
		}
		
		Location from = null, via = null, to = null;
		
		try {
			// FIXME autocomplete not optimal, use connectionsFromStationsIDs instead
			from = sbbProvider_.autocompleteStations(fromConstraint).get(0);
			to = sbbProvider_.autocompleteStations(toConstraint).get(0);
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		if(from==null || to==null) {
			return null;
		}
		
		Date date = new Date();
		boolean dep = true;
		String products = (String)null;
		WalkSpeed walkSpeed = WalkSpeed.NORMAL;
		
		QueryConnectionsResult connections = null;
		try {
			connections = sbbProvider_.queryConnections(from, via, to, date, dep, products, walkSpeed);
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		return connections;
	}
	
	
	@PublicMethod
	public Object connectionsFromStationsIDs(HttpServletRequest request){
		String fromIDConstraint = request.getParameter("fromID");
		String toIDConstraint = request.getParameter("toID");
		
		if(fromIDConstraint==null || toIDConstraint==null) {
			return null;
		}
		
		int fromId = Integer.parseInt(fromIDConstraint);
		int toId = Integer.parseInt(toIDConstraint);
		
		Location from = null, via = null, to = null;
		
		//try {
			//FIXME list maybe empty
//			from = sbbProvider_.nearbyStations(fromIDConstraint, 0, 0, 1, 1).stations.get(0);
//			to = sbbProvider_.nearbyStations(toIDConstraint, 0, 0, 1, 1).stations.get(0);
			
			from = new Location(LocationType.STATION, fromId);
			to = new Location(LocationType.STATION, toId);
//		} catch (IOException e) {
//			e.printStackTrace();
//		}
		
		if(from==null || to==null) {
			return null;
		}
			
		Date date = new Date();
		boolean dep = true;
		String products = (String)null;
		WalkSpeed walkSpeed = WalkSpeed.NORMAL;
		
		QueryConnectionsResult connections = null;
		try {
			connections = sbbProvider_.queryConnections(from, via, to, date, dep, products, walkSpeed);
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		return connections;
		
	}
	
	
	//from, to and a time are necessary
	//if you set fromID and from, the ID version will be used. Also true for other locations, ID > locations as String
	//depTime > arrTime
	@PublicMethod
	public Object magicConnections(HttpServletRequest request){
		String fromIDConstraint = request.getParameter("fromID");
		String toIDConstraint = request.getParameter("toID");
		String fromConstraint = request.getParameter("from");
		String toConstraint = request.getParameter("to");
		String viaConstraint = request.getParameter("via");
		String viaIDConstraint = request.getParameter("viaID");
		String departureTime = request.getParameter("depTime");
		String arrivalTime = request.getParameter("arrTime");
		
		
		//build the locations
		Location from = buildLocation(fromIDConstraint, fromConstraint);
		Location to = buildLocation(toIDConstraint, toConstraint);
		if(from == null || to == null)
			return null;
		
		Location via = buildLocation(viaIDConstraint, viaConstraint);
		
		//get the time
		Date date;
		boolean dep;
		try{
			if(departureTime != null){
				date = new Date(Long.valueOf(departureTime));
				dep = true;
			}else if( arrivalTime != null ){
				date = new Date(Long.valueOf(arrivalTime));
				dep = false;
			}else{
				return null;
			}
		}catch(NumberFormatException e){
			System.out.println("<Transport> couldn't get the time");
			return null;
		}
		
		
		//setting the last parameters
		String products = (String)null;
		WalkSpeed walkSpeed = WalkSpeed.NORMAL;
		
		
		//try to get a connection
		QueryConnectionsResult connections = null;
		try {
			connections = sbbProvider_.queryConnections(from, via, to, date, dep, products, walkSpeed);
			
			
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		return connections;
	}
	
	@PublicMethod
	public Object detailedConnection(HttpServletRequest request){
		//getting the parameters
		String fromIDConstraint = request.getParameter("fromID");
		String toIDConstraint = request.getParameter("toID");
		String departureTime = request.getParameter("depTime");
		String arrivalTime = request.getParameter("arrTime");
		
		//quit if non valid
		if(fromIDConstraint == null 
				|| toIDConstraint == null
				|| departureTime == null)
			return null;
		
		//get the usable variable from parameters
		Location depStation = getLocationFromID(fromIDConstraint);
		Location arrStation = getLocationFromID(toIDConstraint);
		
		Date depTime;
		Date arrTime;
		try{
			depTime = new Date(Long.valueOf(departureTime));
			if(arrivalTime != null)
				arrTime = new Date(Long.valueOf(arrivalTime));
			else 
				arrTime = depTime;
			
		}catch(NumberFormatException e){
			System.out.println("<Transport> couldn't get the time");
			e.printStackTrace();
			return null;
		}
		
		//get the global connection
		String products = (String)null;
		WalkSpeed walkSpeed = WalkSpeed.NORMAL;
		Boolean done = false;
		
		QueryConnectionsResult globalResult = null;
		try {
			globalResult = sbbProvider_.queryConnections(depStation, null, arrStation, depTime, true, products, walkSpeed);
			if(globalResult == null)
				return null;
		} catch (IOException e) {
			System.out.println("bwaaaaaaaaaaaaasdfa" + depTime);
		} catch (Exception e) {
			System.out.println("bwaaaaaaaaaaaaaaaaaaaaaaaa" + depTime);
		}
		
		
		
		//and then for each part, get the details
		List<Connection.Part> result = new ArrayList<Connection.Part>();
		try {
			QueryConnectionsResult tmp;
			Location via = null;
			
			Connection travel_plan = globalResult.connections.get(0);
			Date previous_connections_arrival_time = travel_plan.departureTime;
			System.out.println("--------------------- " + depStation + " to " + arrStation);
			System.out.println("--------------------- " + previous_connections_arrival_time );
			
			for(Connection.Part p : travel_plan.parts){
				tmp = sbbProvider_.queryConnections(p.departure, via, p.arrival, previous_connections_arrival_time, true, products, walkSpeed);
				System.out.print(p.departure + " TO " + p.arrival);
				if(tmp.status != QueryConnectionsResult.Status.NO_CONNECTIONS){
					Connection ic = tmp.connections.get(0);
					System.out.println(" dep:" + ic.departureTime + " arr:" +  ic.arrivalTime );
					Connection.Trip t = new Connection.Trip(null, travel_plan.to, ic.departureTime, null, ic.from, ic.arrivalTime, null, ic.to, null, null);
					result.add(t);
					
					previous_connections_arrival_time = ic.arrivalTime;
				}else{
					List<Point> path = new ArrayList<Point>();
					path.add(new Point(p.departure.lat, p.departure.lon));
					path.add(new Point(p.arrival.lat, p.arrival.lon));
					int duration = 1;
					Connection.Footway t = new Connection.Footway(duration, p.departure, p.arrival, path);
					result.add(t);
					
					Date a = previous_connections_arrival_time;
					long someMinute = 1000L * 60L * duration;
					long bs = a.getTime() + someMinute;
					previous_connections_arrival_time = new Date(bs);
					
					System.out.println(" trajet à pied de " + duration +" min (" +a+ " - " + previous_connections_arrival_time+")" );
				}
			}
			done = true;
			
		} catch (IOException e) {
			System.out.println("plop la mouette");
			e.printStackTrace();
		} 
		
		//check
		int cpt = result.size() -1;
		boolean check = false;
		while(cpt >= 0 && !check){
			Connection.Part a = result.get(cpt);
			if(a instanceof Connection.Trip)
				if( ((Connection.Trip)a).arrivalTime.equals(arrTime) )
					check = true;
					
		}
		if(check){
			System.out.println("no tress sur les trajets à pied");
		}else{
			System.out.println("un petit jogging?");
		}
		
		if(!done)
			result.clear();
		
		return result;
		
	}
	
	private Location getLocationFromID(String stationID){
		if(stationID == null)
			return null;
		
		Location station = null;
		try {
			station = sbbProvider_.nearbyStations(stationID, 0, 0, 1, 1).stations.get(0);
		} catch (IOException e) {
			System.out.println("<Transport> couldn't get station from ID.");
			e.printStackTrace();
		}
		
		return station;
	}
	
	private Location getLocationFromString(String stationConstraint){
		if(stationConstraint == null)
			return null;
		
		Location station = null;
		try {
			station = sbbProvider_.autocompleteStations(stationConstraint).get(0);
		} catch (IOException e) {
			System.out.println("<Transport> couldn't get station from string.");
			e.printStackTrace();
		}
		
		return station;
	}

	private Location buildLocation(String stationID, String stationString){
		Location station = getLocationFromID(stationID);
		if(station == null)
		{
			station = getLocationFromString(stationString);
			if( station == null)
				return null;
		}
		
		
		return station;
	}
	
	
}
















