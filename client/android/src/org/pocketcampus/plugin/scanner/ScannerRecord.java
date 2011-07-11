package org.pocketcampus.plugin.scanner;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import org.pocketcampus.shared.plugin.scanner.GpsLocationBean;
import org.pocketcampus.shared.plugin.scanner.GsmCellLocationBean;
import org.pocketcampus.shared.plugin.scanner.ScanResultBean;
import org.pocketcampus.shared.plugin.scanner.ScannerRecordBean;


import android.location.Location;
import android.net.wifi.ScanResult;
import android.telephony.gsm.GsmCellLocation;

public class ScannerRecord implements Serializable {
	private static final long serialVersionUID = 5228601604244543091L;
	
	private int pointId_;
	private ArrayList<ScanResult> accessPoints_ = new ArrayList<ScanResult>();
	private ArrayList<Location> gpsLocations_ = new ArrayList<Location>();
	private GsmCellLocation gsmLocation_;
	
	public ScannerRecord(int pointId) {
		pointId_ = pointId;
	}

	public void setWifiData(List<ScanResult> accessPoints) {
		if(accessPoints == null) {
			return;
		}
		
		accessPoints_.clear();
		accessPoints_.addAll(accessPoints);
	}

	public void setGsmData(GsmCellLocation gsmCellLocation) {
		gsmLocation_ = gsmCellLocation;
	}

	public void setGpsData(ArrayList<Location> gpsLocations) {
		gpsLocations_ = gpsLocations;
	}
	
	public ScannerRecordBean makeBean() {
		System.out.println("GSM, " + accessPoints_.size() + " access points visibles, " + gpsLocations_.size() + " GPS fixes.");
		
		ScannerRecordBean bean = new ScannerRecordBean();
		
		ArrayList<ScanResultBean> accessPoints = new ArrayList<ScanResultBean>();
		for(ScanResult accessPoint : accessPoints_) {
			ScanResultBean scanResultBean = new ScanResultBean();
			scanResultBean.setBssid(accessPoint.BSSID);
			scanResultBean.setCapabilities(accessPoint.capabilities);
			scanResultBean.setFrequency(accessPoint.frequency);
			scanResultBean.setLevel(accessPoint.level);
			scanResultBean.setSsid(accessPoint.SSID);
			accessPoints.add(scanResultBean);
		}
		bean.setAccessPoints(accessPoints);
		
		ArrayList<GpsLocationBean> gpsLocations = new ArrayList<GpsLocationBean>();
		for(Location gpsLocation : gpsLocations_) {
			GpsLocationBean gpsLocationBean = new GpsLocationBean();
			gpsLocationBean.setLatitude(gpsLocation.getLatitude());
			gpsLocationBean.setLongitude(gpsLocation.getLongitude());
			gpsLocationBean.setAccuracy(gpsLocation.getAccuracy());
			gpsLocationBean.setAltitude(gpsLocation.getAltitude());
			gpsLocationBean.setBearing(gpsLocation.getBearing());
			gpsLocations.add(gpsLocationBean);
		}
		bean.setGpsLocations(gpsLocations);
		
		GsmCellLocationBean gsmCellLocationBean = new GsmCellLocationBean();
		gsmCellLocationBean.setCid(gsmLocation_.getCid());
		gsmCellLocationBean.setLac(gsmLocation_.getLac());
		bean.setGsmLocation(gsmCellLocationBean);
		
		bean.setPointId(pointId_);
		
		return bean;
		
	}

	public ArrayList<Location> getGpsData() {
		return gpsLocations_;
	}

	public int getNbAccessPoints() {
		return accessPoints_.size();
	}
}












