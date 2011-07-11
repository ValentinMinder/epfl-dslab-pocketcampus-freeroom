package org.pocketcampus.plugin.scanner;

import java.util.List;

import android.content.Context;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiManager;

public class WifiScanner {
	private WifiManager wifiManager_;
	private Context ctx_;
	
	public WifiScanner(Context ctx) {
		ctx_ = ctx;
		wifiManager_ = (WifiManager) ctx_.getSystemService(Context.WIFI_SERVICE);
	}
	
	public List<ScanResult> scan() {
		wifiManager_.startScan();
		List<ScanResult> visibleAccessPoints = wifiManager_.getScanResults();
		return visibleAccessPoints;
	}

}
