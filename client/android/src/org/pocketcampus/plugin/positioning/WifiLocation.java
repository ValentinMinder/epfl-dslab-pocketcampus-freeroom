package org.pocketcampus.plugin.positioning;




/*
 ********************* [ P O C K E T C A M P U S ] *****************
 * [    LICENCE    ] 	see "licence"-file in the root directory
 * [   MAINTAINER  ]	tarek.benoudina@epfl.ch
 * [     STATUS    ]    under developement
 *
 **************************[ C O M M E N T S ]**********************
 *   
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
import android.location.Location;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiManager;
import android.os.Environment;
import android.util.Log;
import android.widget.Toast;

public class WifiLocation {
	
	private Context ctx_;
	private WifiManager wifiManager;
	private HashMap<String, String> ApBSSIDToName;
	private HashMap<String, Position> ApNameToPosition;
	private List<AccessPoint> ApList_;

	
	
	public WifiLocation(Context ctx) {
		ctx_ = ctx;
		wifiManager = (WifiManager) ctx_.getSystemService(Context.WIFI_SERVICE);
		ApBSSIDToName = readApBSSIDToNameFile();
		ApNameToPosition = readApNameToPositionFile();
		ApList_ = getAccessPoints();
		
		
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
	 * getStronguestPosition(List<Accesspoint> list) gives the position of the Ap which gives the high
	 * intensity of signal depending on the list
	 * 
	 * */
	public AccessPoint getStrongestAP(List<AccessPoint> listAP) {
		AccessPoint Ap = null;
		int lev = 0;
		for (AccessPoint p : listAP) {
			if (p.getSignalLevel() > lev) {
				lev = p.getSignalLevel();
				Ap = p;
			}
		}
		
		//System.out.println("AP Nearest Distance:"+Ap.getDistance()+" Level : "+Ap.getSignalLevel()+" Name :"+Ap.getSSID());
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
			System.out.println("Ap: lev :"+emitter.getSignalLevel()+" Ap : distance :"+emitter.getDistance()+"floor :"+emitter.position().toString());
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
	
	
	public int getnumberOfAP(){
		return this.ApList_.size();
	}
	
	
	
	/**
	 * getTheBestFourth()
	 * @return list containing 4 AccessPoints with the best signal
	 */
	
	public List<AccessPoint> getTheBestFourth(List<AccessPoint> apList){
		//List<AccessPoint> apList = getAccessPoints();
		List<AccessPoint> best4List = new ArrayList<AccessPoint>();
		List<Position> best4position = new ArrayList<Position>();
		
		int i=4;
		AccessPoint bestAp = getStrongestAP(apList);
		best4List.add(bestAp);
		best4position.add(bestAp.position());
		while(i>0){
			if(best4position.contains(bestAp.position())){
				apList.remove(bestAp);
				bestAp = getStrongestAP(apList);
				System.out.println("apList :"+ apList.size());
			}else{
				best4List.add(bestAp);
				best4position.add(bestAp.position());
				apList.remove(bestAp);
				bestAp = getStrongestAP(apList);
				System.out.println("apList_best4 :"+ best4List.size());
				i--;
			}
		}
		return best4List;   
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
	
	
	public Position getWifiLocationPerTaylorSerieLocal(){
		List<AccessPoint> apList = new ArrayList<AccessPoint>();
		List<AccessPoint> apList2 = new ArrayList<AccessPoint>();
		List<Position> positionList = new ArrayList<Position>();
		Taylor taylorEq;
		apList =getDistinctApList();//getAccessPoints();
			
		apList2 = getTheBestFourth(apList);
		AccessPoint ap1 = apList2.get(0);
		AccessPoint ap2 = apList2.get(1);
		AccessPoint ap3 = apList2.get(2);
		AccessPoint ap4 = apList2.get(3);
		
		taylorEq = new Taylor(ap1, ap2, ap3, ap4);
		
		Position result = taylorEq.taylorEquation();

		return result;
	}
	
	public Position getWifiLocationPerTaylorSerieGlobal(){
		//Toast.makeText(ctx_, "Waiting for positioning  !",Toast.LENGTH_LONG);
		List<AccessPoint> apList = new ArrayList<AccessPoint>();
		List<Position> positionList = new ArrayList<Position>();
		Position result2 =null;
		double x = 0,y = 0;
		Taylor taylorEq;
		apList =getAccessPoints();

		int i=0;
		//int j=3;
        while(i<apList.size()-3){
		AccessPoint ap1 = apList.get(i);
		AccessPoint ap2 = apList.get(i+1);
		AccessPoint ap3 = apList.get(i+2);
		AccessPoint ap4 = apList.get(i+3);
		
		taylorEq = new Taylor(ap1, ap2, ap3, ap4);
		
		Position result = taylorEq.taylorEquation();
		if(validate(result)){
			positionList.add(result);
		x =x+result.getLatitude();
		y =y+result.getLongitude();
		}
		System.out.println("X :" +x);
		System.out.println("Y :" +y);
		i++;
        }
		result2 = new Position(x/positionList.size(),y/positionList.size(),0.0);
		System.out.println("Position list size ::::::::::::"+positionList.size());
		return result2;
	}
	
	
	public Location getWifiLocationPerTaylor3DSerieGlobal(){
		List<AccessPoint> apList = new ArrayList<AccessPoint>();
		List<Position> positionList = new ArrayList<Position>();
		Location result2 =null;
		double x = 0.0,y = 0.0,z =0.0;
		Taylor3D taylorEq3D;
		apList =getAccessPoints();

		int i=0;
		//int j=3;
        while(i<apList.size()-3){	
		AccessPoint ap1 = apList.get(i);
		AccessPoint ap2 = apList.get(i+1);
		AccessPoint ap3 = apList.get(i+2);
		AccessPoint ap4 = apList.get(i+3);
		
		taylorEq3D = new Taylor3D(ap1, ap2, ap3, ap4);
		
		Location resultloc = taylorEq3D.taylorEquation();
		Position result = new Position(resultloc.getLatitude(),resultloc.getLongitude(),0.0);
		if(validate(result)){
			positionList.add(result);
		x =x+result.getLatitude();
		y =y+result.getLongitude();
		z =z+result.getAltitude();
		}
		System.out.println("X :" +x);
		System.out.println("Y :" +y);
		System.out.println("Z :" +z);
		i++;
        }
        result2 = new Location("Wifi Taylor");
        result2.setLatitude(x/positionList.size());
        result2.setLongitude(y/positionList.size());
        result2.setAltitude(0.0);
        result2.setAccuracy(10);
		//result2 = new Position(x/positionList.size(),y/positionList.size(),z/positionList.size());
		System.out.println("Position list size ::::::::::::"+positionList.size());
		
		return result2;
	}
	
	
	
	
	private boolean validate(Position result) {
		if((result.getLatitude()==0.0)||(result.getLongitude()==0.0))
		return false;
		else return true;
	}

	public List<AccessPoint> getDistinctApList(){
		List<AccessPoint>  apList = new ArrayList<AccessPoint>();
		List<AccessPoint>  distinctApList = new ArrayList<AccessPoint>();
		List<Position> positionList = new ArrayList<Position>();
		apList = getAccessPoints();
		
		for(AccessPoint currentAp : apList){
			
			if(!positionList.contains(currentAp.position())){
				distinctApList.add(currentAp);
				positionList.add(currentAp.position());
			}
		}
		System.out.println("Distinct : "+distinctApList.size());
		System.out.println("Aplist   : "+apList.size());
		return distinctApList;
	}
	
	
	
	public int getSignificantAP(){
		int num = ApList_.size();
		List<AccessPoint> goodAP = new ArrayList<AccessPoint>();
		for(AccessPoint ap : ApList_){
			if(ap.getSignalLevel()>35)
				goodAP.add(ap);
		}
		return goodAP.size();
	}

}
