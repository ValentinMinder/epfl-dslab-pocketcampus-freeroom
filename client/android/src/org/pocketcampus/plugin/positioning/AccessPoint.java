/*
 ********************* [ P O C K E T C A M P U S ] *****************
 * [   MAINTAINER  ]	tarek.benoudina@epfl.ch
 * [     STATUS    ]    stable
 *
 **************************[ C O M M E N T S ]**********************
 *
 * Represents an Access Point.
 *
 *******************************************************************
 */

package org.pocketcampus.plugin.positioning;

/**
 * Author : Tarek
 *          Benoudina
 *          
 * AccessPoint for wifi location,
 * 
 * returns : SSID , signal strength , distance , position.
 * 
 */
import java.util.List;

import org.pocketcampus.shared.plugin.map.Position;

import android.net.wifi.ScanResult;

public class AccessPoint implements Comparable<AccessPoint> {
	private Position position;
	private String SSID;
	private int signalLevel;
	private int pathLoss_;
	private String name;
	private int radiatedPower;
	private double distance;
	private double estimatedDistance;
	private int frequency_;
	private List<Node> nodes_;
	private CartesianPoint coordinates_;
	
	public AccessPoint(ScanResult sr, String apName, Position pos) {
		SSID = sr.SSID;
		signalLevel = 100 + sr.level;   // sr.level < 0
		pathLoss_ = sr.level;
		name = apName;
		position = pos;
		frequency_ = sr.frequency;
		radiatedPower = 100;  // cisco value 
		distance = getDistance();
		estimatedDistance = getEstimatedDistance();
		nodes_ = null;
		coordinates_ = new VirtualConvert(pos).convertPositionToCartesian(pos);
	}
	
	public AccessPoint(AccessPoint _ap ,List<Node> _nodes) {
		SSID = _ap.SSID;
		signalLevel = _ap.signalLevel;   // sr.level < 0
		pathLoss_ = _ap.pathLoss_;
		name = _ap.name;
		position = _ap.position;
		frequency_ = _ap.frequency_;
		radiatedPower = 100;  // cisco value 
		distance = _ap.getDistance();
		estimatedDistance = _ap.getEstimatedDistance();
		nodes_ = _nodes;
	}

	public Position position() {
		return position;
	}
	
	public int getSignalLevel() {
		return signalLevel;
	}
	
//	@Override
//	public String toString() {
//		String apName = "??";
//		Position pos = new Position(0, 0, 0);
//		
//		if(name != null) {
//			apName = name;
//		}
//		
//		if(position != null) {
//			pos = position;
//		}
//		
//		return SSID + ", "+apName+": ("+ pos.getLatitude() + ";"+ pos.getLongitude() +";"+pos.getLatitude()+"), "+ signalLevel;
//	}
	
	
	@Override
	public String toString(){
		return this.name;
	}
	
	
/**	 
 * getDistance()
 * 
 * to get distance to the AP, we assume that we know the power radiated of the AP
 *	Radiated Power (max signal) added to the AP class,
 *	in order to compute the distance from AP,
 *	supposing ours APs are Isotrop Antenna.
 *	so RSS = Power/4*PI*R^2 (R is the distance)
 */
	
//	public double getDistance(){
//		double distance = 0;
//		double pi = Math.PI;
//		int power = this.radiatedPower;
//		int lev = this.getSignalLevel();
//		distance = Math.sqrt((power/4*pi*lev));	
//		return distance;
//	}
	
	public double getDistance(){
		int pathLoss = pathLoss_*(-1);
		double distance;
		int pathlossd0 = 30;
		double n = 1.65;
		double expn = Math.pow(10, n);
		distance = Math.pow(10,((pathLoss-pathlossd0)/expn));
		
		return distance;
	}
	
	
	public int getEstimatedDistance(){
		int distance = 0;
		if(this.getSignalLevel()>55)
			distance = 4;
		else if((this.getSignalLevel()>40)&&(this.getSignalLevel()<=55))
			distance = 8;
		else distance = 12;
		return distance;
	}
	
	public void setSignalLevel(int level){
		this.signalLevel = level;
	}
	
	
	public String getSSID(){
		return this.SSID;
	}
	
	public String getName(){
		return this.name;
	}
	
	public int getPathLoss(){
		return this.pathLoss_;
	}
	
	public void setDistance(double d){
		
		this.distance = d;
	}
	
	public void setEstimatedDistance(double d){
		this.estimatedDistance = d;
	}
	
	
	public void addNode(Node _node){
		if (_node!=null) 
		nodes_.add(_node);
	}
	
	public CartesianPoint getCoordinates(){
		return this.coordinates_;
	}
	
	public void setNodeList(List<Node> list){
		nodes_ = list; 
	}

	public void increaseNode() {
		if(nodes_!=null)
			for(Node node : nodes_ )
				node.increaseValue();
		
	}

	public void decreaseNode() {
		if(nodes_!=null)
			for(Node node : nodes_ )
				node.decrease();
	}

	@Override
	public int compareTo(AccessPoint obj) {
		return this.name.compareTo(obj.toString());
	}
}
