package org.pocketcampus.plugin.transport;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;

import org.osmdroid.DefaultResourceProxyImpl;
import org.osmdroid.util.GeoPoint;
import org.osmdroid.views.overlay.ItemizedIconOverlay;
import org.osmdroid.views.overlay.Overlay;
import org.osmdroid.views.overlay.OverlayItem;
import org.pocketcampus.plugin.map.elements.MapElementsList;
import org.pocketcampus.shared.plugin.transport.Connection;
import org.pocketcampus.shared.plugin.transport.Railway;
import org.pocketcampus.shared.plugin.transport.RailwayNode;

import android.content.Context;

public class ConnectionPool {
	private static final double METRO_SPEED = 0.015; //geopoint units/s (TODO in km/h) >0.005  <0.01
	private static final double METRO_STOP_TIME = 20.0; //seconds
	
	private HashMap<Date, ArrayList<Connection>> connections_;
	private Railway path_;
	private Context ctx_;
	
	public ConnectionPool(Context ctx, Railway path) {
		connections_ = new HashMap<Date, ArrayList<Connection>>();
		path_ = path;
		ctx_ = ctx;
	}
	
	public void addConnection(Connection connection) {
		
		Date arrivalTime = connection.arrivalTime;
		
		if(connections_.get(arrivalTime) == null) {
			connections_.put(arrivalTime, new ArrayList<Connection>());
		}
		
		connections_.get(arrivalTime).add(connection);
	}

	public ItemizedIconOverlay<OverlayItem> crunch(double metroSpeed, double timeAtStop) {
		GeoPoint geoPoint;
		OverlayItem item;
		MapElementsList layer = new MapElementsList("Metros", 0);
		
		//System.out.println("-ARRIVAL DATES-");
		double variation = 0;
		
		for(Date date : connections_.keySet()) {
			//System.out.println("\n\n\n----- "+date+" -----");
			double prevClosestNum = 0;
			
			for (Connection c : connections_.get(date)) {
				int timeToArrival = (int) ((c.departureTime.getTime() - (new Date()).getTime())/1000);
				//System.out.println("Reaches " + c.from + " in "+timeToArrival+"s.");
				
				// find closest node to metro
				RailwayNode closest = findClosestNode(path_.getStopNodes().get(c.from.id), timeToArrival, metroSpeed, timeAtStop);
				
				//System.out.println("CLOSEST\t" + closest);
				//System.out.println();
				
				// add it to the map
				if(closest != null) {
					
					// computes variation
					if(prevClosestNum != 0) {
						variation += Math.abs(prevClosestNum - closest.getNum());
					}
					prevClosestNum = closest.getNum();
					
					geoPoint = new GeoPoint(closest.getLat(), closest.getLon());
					item = new OverlayItem("title", "desc", geoPoint);
					layer.add(item);
				}
			}
			
		}
		
		//variation = variation / (float)connections_.size();
		//System.out.println("VARIATION: " + variation);
		//return variation;
		
		ItemizedIconOverlay<OverlayItem> overlay = new ItemizedIconOverlay<OverlayItem>(layer, null, new DefaultResourceProxyImpl(ctx_));
		return overlay;
	}

	private RailwayNode findClosestNode(RailwayNode node, double timeToArrival, double metroSpeed, double timeAtStop) {
		
		while(timeToArrival > 0) {
			
			// takes some time if it's a stop
			if(node.getTag("uic_ref") != null) {
				timeToArrival -= timeAtStop;
				//System.out.println("STOP\t\t"+timeToArrival+"s.left ("+node.getTag("uic_name")+")");
			}
			
			// distance to previous one
			RailwayNode previousNode = path_.getNodes().get(node.getPreviousRef());
			
			if(previousNode == null) {
				// at the bottom of track
				return null;
			}
			
			timeToArrival -= metroSpeed/node.getDistFromPrevious();
			//System.out.println("NODE "+node.getNum()+"\t"+timeToArrival+"s.left");
			
			node = previousNode;
		}
		
		return node;
		
	}

}









