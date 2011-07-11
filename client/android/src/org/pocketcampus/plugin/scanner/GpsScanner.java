package org.pocketcampus.plugin.scanner;

import java.util.ArrayList;

import android.content.Context;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;

public class GpsScanner extends Handler implements LocationListener {
	private static final int SCAN_DURATION = 15;

	private ArrayList<Location> fixes_;

	private GpsScannerScanFinishedCallback onScanFinish_;
	private GpsScannerLocationChangedCallback onLocationChanged_;

	private Context ctx_;

	private LocationManager locationManager_;

	public GpsScanner(Context ctx) {
		ctx_ = ctx;
		fixes_ = new ArrayList<Location>();
	}

	public void startScanning() {
		locationManager_ = (LocationManager) ctx_.getSystemService(Context.LOCATION_SERVICE);
		locationManager_.requestLocationUpdates(LocationManager.GPS_PROVIDER, 1000, 0, this);

		Message msg = new Message();
		this.sendMessageDelayed(msg, SCAN_DURATION * 1000);
	}

	public void stopScanning() {
		locationManager_.removeUpdates(this);
	}

	@Override
	public void handleMessage(Message msg) {
		onScanFinish_.call(fixes_);
	}

	@Override
	public void onLocationChanged(Location location) {
		fixes_.add(location);
		onLocationChanged_.call(location);
	}

	public void setOnScanFinishListener(GpsScannerScanFinishedCallback onScanFinish) {
		onScanFinish_ = onScanFinish;
	}

	public void setOnFixListener(GpsScannerLocationChangedCallback onInfo) {
		onLocationChanged_ = onInfo;
	}

	@Override
	public void onProviderDisabled(String arg0) {
		// nothing to do
	}

	@Override
	public void onProviderEnabled(String arg0) {
		// nothing to do
	}

	@Override
	public void onStatusChanged(String arg0, int arg1, Bundle arg2) {
		// nothing to do
	}
	
	public int getNbFixes() {
		return fixes_.size();
	}
}














