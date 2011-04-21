/*
 ********************* [ P O C K E T C A M P U S ] *****************
 * [    LICENCE    ] 	see "licence"-file in the root directory
 * [   MAINTAINER  ]	tarek.benoudina@epfl.ch
 * [     STATUS    ]    stable
 *
 **************************[ C O M M E N T S ]**********************
 *
 * Class reused from V1
 * Represents an Acess Point.
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
import org.pocketcampus.shared.plugin.map.Position;

import android.net.wifi.ScanResult;

public class AccessPoint {
	private Position position;
	private String SSID;
	private int signalLevel;
	private String name;
	private int radiatedPower;
	private double distance;
	private double estimatedDistance;
	private int frequency_;
	
	public AccessPoint(ScanResult sr, String apName, Position pos) {
		SSID = sr.SSID;
		signalLevel = 100 + sr.level;   // sr.level < 0
		name = apName;
		position = pos;
		radiatedPower = 100;  // cisco value 
		distance = getDistance();
		estimatedDistance = getEstimatedDistance();
	}

	public Position position() {
		return position;
	}
	
	public int getSignalLevel() {
		return signalLevel;
	}
	
	@Override
	public String toString() {
		String apName = "??";
		Position pos = new Position(0, 0, 0);
		
		if(name != null) {
			apName = name;
		}
		
		if(position != null) {
			pos = position;
		}
		
		return SSID + ", "+apName+": ("+ pos.getLatitude() + ";"+ pos.getLongitude() +";"+pos.getLatitude()+"), "+ signalLevel;
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
	
	public double getDistance(){
		double distance = 0;
		double pi = Math.PI;
		int power = this.radiatedPower;
		int lev = this.getSignalLevel();
		distance = Math.sqrt((power/4*pi*lev));
		
		
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
	
	public void setDistance(double d){
		
		this.distance = d;
	}
	
	public void setEstimatedDistance(double d){
		this.estimatedDistance = d;
	}
}
