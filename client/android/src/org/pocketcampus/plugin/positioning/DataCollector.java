/*
 ********************* [ P O C K E T C A M P U S ] *****************
 * [    LICENCE    ] 	see "licence"-file in the root directory
 * [   MAINTAINER  ]	tarek.benoudina@epfl.ch
 * [     STATUS    ]    under construction
 *
 **************************[ C O M M E N T S ]**********************
 *
 *FingerPrint is used when there is no AccessPoint references
 *
 *******************************************************************
 */

package org.pocketcampus.plugin.positioning;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import android.content.Context;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiManager;

/**
 * Author : Tarek
 *          Benoudina
 *          
 * FingerPrint for wifi location,
 * 
 * returns : aproximative userPosition according to stored data.
 * 
 */
public class DataCollector {
	private List<AccessPoint> APList_;
	private Context context_;
	private WifiManager wifiManager_;

	
	public DataCollector(Context _ctx){
		context_ = _ctx;
		wifiManager_ = (WifiManager) context_.getSystemService(Context.WIFI_SERVICE);
		APList_ = getAPList();
	}
	
	
	public List<AccessPoint> getAPList() {
		List<ScanResult> visibleAP = null;
		List<AccessPoint> usableAP = null;

		wifiManager_.startScan();
		System.out.println("Start Scanning AccessPoints!");
		visibleAP = wifiManager_.getScanResults();

		usableAP = new ArrayList<AccessPoint>();

		if (visibleAP == null) {
			return null;
		}

		for (Iterator<ScanResult> iterator = visibleAP.iterator(); iterator
		.hasNext();) {
			ScanResult ap = (ScanResult) iterator.next();
			// System.out.println(ap.SSID);
			// System.out.println(ap.level);
//
//			if (ap.SSID.equals("epfl") || ap.SSID.equals("public-epfl")) {
//				// if(ap.level > -99) {
//				String name = ApBSSIDToName.get(ap.BSSID);
//				Position pos = ApNameToPosition.get(name);
//				if (pos != null) {
////					pos = CoordinateConverter.convertCH1903ToLatLong(pos
////							.getLon(), pos.getLat(), pos.getLevel());
//					Position geoPos = new Position(pos.getLongitude(),pos.getLatitude(),pos.getLatitude());
					
//					usableAP.add(new AccessPoint(ap));
				}
				// }
			//}
		//}

		return usableAP;
	}

	
	public int getAccessPointListSize(){
		return getAPList().size();
	}
	
	
}
