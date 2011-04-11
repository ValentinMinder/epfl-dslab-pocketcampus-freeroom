package org.pocketcampus.plugin.transport;


import java.io.IOException;
import java.util.Date;
import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.pocketcampus.core.plugin.IPlugin;
import org.pocketcampus.core.plugin.PublicMethod;
import org.pocketcampus.shared.plugin.transport.Location;
import org.pocketcampus.shared.plugin.transport.QueryConnectionsResult;


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
		
		Location from = null, via = null, to = null;
		
		try {
			//FIXME list maybe empty
			from = sbbProvider_.nearbyStations(fromIDConstraint, 0, 0, 1, 1).stations.get(0);
			to = sbbProvider_.nearbyStations(toIDConstraint, 0, 0, 1, 1).stations.get(0);
		} catch (IOException e) {
			e.printStackTrace();
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
















