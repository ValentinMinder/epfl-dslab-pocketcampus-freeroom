package org.pocketcampus.shared.plugin.scanner;

import java.io.Serializable;
import java.util.ArrayList;

public class ScannerRecordBean implements Serializable {
	private static final long serialVersionUID = 5228601604244543091L;

	private int pointId_;
	ArrayList<ScanResultBean> accessPoints_ = new ArrayList<ScanResultBean>();
	GsmCellLocationBean gsmLocation_;
	ArrayList<GpsLocationBean> gpsLocations_ = new ArrayList<GpsLocationBean>();
	
	public void setAccessPoints(ArrayList<ScanResultBean> accessPoints_) {
		this.accessPoints_ = accessPoints_;
	}
	
	public void setGpsLocations(ArrayList<GpsLocationBean> gpsLocations_) {
		this.gpsLocations_ = gpsLocations_;
	}
	
	public void setGsmLocation(GsmCellLocationBean gsmLocation_) {
		this.gsmLocation_ = gsmLocation_;
	}

	public void setPointId(int pointId) {
		pointId_ = pointId;
	}

	public GsmCellLocationBean getGsmLocation() {
		return gsmLocation_;
	}
	
	public ArrayList<ScanResultBean> getAccessPoints() {
		return accessPoints_;
	}
	
	public int getPointId() {
		return pointId_;
	}
	
	public ArrayList<GpsLocationBean> getGpsLocations() {
		return gpsLocations_;
	}
}
