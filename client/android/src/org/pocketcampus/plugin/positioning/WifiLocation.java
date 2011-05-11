package org.pocketcampus.plugin.positioning;




/*
 ********************* [ P O C K E T C A M P U S ] *****************
 * [    LICENCE    ] 	see "licence"-file in the root directory
 * [   MAINTAINER  ]	tarek.benoudina@epfl.ch
 * [     STATUS    ]    under developement
 *
 **************************[ C O M M E N T S ]**********************
 *   Naive version
 *
 *******************************************************************
 */



import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;


import org.osmdroid.util.GeoPoint;
//import org.pocketcampus.map.element.MapElement;
//import org.pocketcampus.map.util.CoordinateConverter;

import org.pocketcampus.R;
import org.pocketcampus.shared.plugin.map.Position;

import android.app.Activity;
import android.content.Context;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiManager;
import android.os.Environment;
import android.util.Log;

public class WifiLocation {
	
	private Context ctx_;
	private WifiManager wifiManager;
	private HashMap<String, String> ApBSSIDToName;
	private HashMap<String, Position> ApNameToPosition;

	
	
	public WifiLocation(Context ctx) {
		ctx_ = ctx;
		wifiManager = (WifiManager) ctx_.getSystemService(Context.WIFI_SERVICE);
		ApBSSIDToName = readApBSSIDToNameFile();
		ApNameToPosition = readApNameToPositionFile();
		
	}

	@SuppressWarnings("unchecked")
	private HashMap<String, Position> readApNameToPositionFile() {
		File file;
		FileInputStream fin;
		HashMap<String, Position> hash = null;

		try {
			
			InputStream f = ctx_.getResources()
			.openRawResource(R.raw.nametopos);
			ObjectInputStream ois = new ObjectInputStream(f);
			hash = (HashMap<String, Position>) ois.readObject();
		} catch (Exception e) {
			e.printStackTrace();
		}

		return hash;
	}

	@SuppressWarnings("unchecked")
	private HashMap<String, String> readApBSSIDToNameFile() {
		File file;
		HashMap<String, String> hash = null;

		try {
			InputStream fin = ctx_.getResources().openRawResource(
					R.raw.mactoname);
			ObjectInputStream ois = new ObjectInputStream(fin);
			hash = (HashMap<String, String>) ois.readObject();
		} catch (Exception e) {
			e.printStackTrace();
		}

		return hash;
	}

	
	
	public List<AccessPoint> getAccessPoints() {
		List<ScanResult> visibleAP = null;
		List<AccessPoint> usableAP = null;

		wifiManager.startScan();
		System.out.println("Start Scanning AccessPoints!");
		visibleAP = wifiManager.getScanResults();

		usableAP = new ArrayList<AccessPoint>();

		if (visibleAP == null) {
			return null;
		}

		for (Iterator<ScanResult> iterator = visibleAP.iterator(); iterator
		.hasNext();) {
			ScanResult ap = (ScanResult) iterator.next();
			// System.out.println(ap.SSID);
			// System.out.println(ap.level);

			if (ap.SSID.equals("epfl") || ap.SSID.equals("public-epfl")) {
				// if(ap.level > -99) {
				String name = ApBSSIDToName.get(ap.BSSID);
				Position pos = ApNameToPosition.get(name);
				if (pos != null) {
//					pos = CoordinateConverter.convertCH1903ToLatLong(pos
//							.getLon(), pos.getLat(), pos.getLevel());
					Position geoPos = new Position(pos.getLongitude(),pos.getLatitude(),pos.getLatitude());
					
					usableAP.add(new AccessPoint(ap, name, geoPos));
				}
				// }
			}
		}

		return usableAP;
	}





	/**
	 * getStronguestPosition() gives the position of the Ap which gives the high
	 * intensity of signal
	 * 
	 * */
	public Position getStronguestPosition(List<AccessPoint> listAP) {
		// List<AccessPoint> accessPoints = getAccessPoints();
		Position po;
		AccessPoint Ap = null;
		int lev = 0;
		for (AccessPoint p : listAP) {
			System.out.println("DISTANCE !!!!!!!!!!!!!!!!!!!!!!!!!!");
			System.out.println(p.getDistance() + "  " + p.getSignalLevel());
			if (p.getSignalLevel() > lev) {
				lev = p.getSignalLevel();
				Ap = p;
			}
		}
		po = new Position(Ap.position().getLatitude(), Ap.position().getLongitude(), Ap
				.position().getLatitude());
		System.out.println("The Nearest!!!!!!!!!!!!!!!!!!!!!!!!!!!!!");
		System.out.println("Level" + Ap.getSignalLevel() + "Distance"
				+ Ap.getDistance());
		return po;
	}

	

	/**
	 * to disable Wifi
	 * @return boolean
	 **/
	public boolean disconnect() {
		return this.wifiManager.setWifiEnabled(false);

	}

	
	/**
	 * to enable Wifi
	 * @return boolean
	 **/
	public boolean connect() {
		return this.wifiManager.setWifiEnabled(true);
	}


	
	
	/**
	 * getStronguestPosition() gives the position of the Ap which gives the high
	 * intensity of signal
	 * 
	 * */
	public AccessPoint getStrongestAP() {
		List<AccessPoint> listAP;
		listAP = getAccessPoints();
		AccessPoint Ap = null;
		int lev = 0;
		for (AccessPoint p : listAP) {
			if (p.getSignalLevel() > lev) {
				lev = p.getSignalLevel();
				Ap = p;
			}
		}
		
		System.out.println("AP Nearest Distance:"+Ap.getDistance()+" Level : "+Ap.getSignalLevel()+" Name :"+Ap.getSSID());
		return Ap;
	}
	
	/**
	 * getWeakestPosition() gives the position of the Ap which gives the low
	 * intensity of signal
	 * 
	 * */
	public AccessPoint getWeakestAP() {
		List<AccessPoint> listAP;
		listAP = getAccessPoints();
		AccessPoint Ap = null;
		int lev = 100;
		for (AccessPoint p : listAP) {
			if (p.getSignalLevel() < lev) {
				lev = p.getSignalLevel();
				Ap = p;
			}
		}
		
		System.out.println("AP Farest Distance:"+Ap.getDistance()+" Level : "+Ap.getSignalLevel()+" Name :"+Ap.getSSID());

		return Ap;
	}
	
	
	
	/**
	 * getMinNumberOfNodes() gives the number of Ap's limited to the weakestSignal
	 * in other way limited to minmum distance 
	 * 
	 * */
	public int getMinNumberOfnodes(List<AccessPoint> listAP) {
		AccessPoint Ap = null;
		int i =0;
		int lev = getWeakestAP().getSignalLevel();
		for (AccessPoint p : listAP) {
			if ((p.getSignalLevel() < lev)||(p.getSignalLevel() == lev)) {
				i++;
			}
		}
		return i;
	}

	
	
	public Position getWifiLocationPerCoefficient() {
		
		List<AccessPoint> treatedList = getAccessPoints();
		if (treatedList.size() == 0)
			return null;

		double posXAvg = 0.0;
		double posYAvg = 0.0;
		double levelAvg = 0.0;
		int total = 0;

		AccessPoint emitter;
		int level;

		for (Iterator<AccessPoint> iterator = treatedList.iterator(); iterator
		.hasNext();) {
			emitter = (AccessPoint) iterator.next();
			System.out.println("Ap: lev :"+emitter.getPathLoss()+" Ap : distance :"+emitter.getDistance());
			level = emitter.getSignalLevel();
			

			if(level>50){
				return emitter.position();
			}else if (level > 40) {
				total += 5 * level;
				posXAvg += 5 * emitter.position().getLatitude() * level;
				posYAvg += 5 * emitter.position().getLongitude() * level;
				//levelAvg += 3 * emitter.position().getLevel() * level;
			} else if (level > 20) {
				total += 2 * level;
				posXAvg += 2 * emitter.position().getLatitude() * level;
				posYAvg += 2 * emitter.position().getLongitude() * level;
				levelAvg += 2 * emitter.position().getLatitude() * level;
			}
			else if (level > 10) {
				total += level;
				posXAvg += emitter.position().getLatitude() * level;
				posYAvg += emitter.position().getLongitude() * level;
				//levelAvg += emitter.position().getLevel() * level;
			}
		}
		
		return adjustPosition(new Position(posXAvg / total, posYAvg / total,
				(int) Math.ceil(levelAvg / total)));

	}

	public List<AccessPoint> adjustAccessPointList(List<AccessPoint> list){
		if(list == null){
			return null;
		}
        AccessPoint acp = getStrongestAP();
		int refLevel = (int) acp.position().getLatitude();
		List<AccessPoint> myList = new ArrayList<AccessPoint>();

		for(AccessPoint ap : list){

			//System.out.println("Valuuuuuuuuuuuuuuues:::::::::"+ap.getSignalLevel());
			if((ap.position().getAltitude()==refLevel)||
					(ap.position().getAltitude()==refLevel+1)||
					(ap.position().getAltitude()==refLevel-1)){	
				if(ap.getSignalLevel()>30){
					myList.add(ap);
					myList.add(ap);
					myList.add(ap);
				}else if(ap.getSignalLevel()>10){
					myList.add(ap);
				}
			}else{//do nothing 
			}
		}
		for(AccessPoint ac: myList){
			System.out.println("AP:::::::::::::::::::::::"+ac.getName()+" Lev :"+ac.getSignalLevel());
		}

		return myList;
	}
	
	
	

	public Position adjustPosition(Position position){
		Position result = null;

		if(position == null){
			return null;
		}
		System.out.println("Adjusted result :::"+position.toString());

		result = new Position((double)position.getLatitude()-0.000090,(double)position.getLongitude()+0.000020,position.getAltitude());



		return result;
	}

}
