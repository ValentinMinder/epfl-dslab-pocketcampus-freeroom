package org.pocketcampus.plugin.scanner;

import android.content.Context;
import android.telephony.TelephonyManager;
import android.telephony.gsm.GsmCellLocation;

public class GsmScanner {
	private Context ctx_;
	int cellId_, lac_;
	public TelephonyManager telephonyManager_;

	public GsmScanner(Context _ctx){
		ctx_ = _ctx;
		telephonyManager_ = (TelephonyManager) ctx_.getSystemService(Context.TELEPHONY_SERVICE) ;
	}

	public GsmCellLocation scan() {
		return (GsmCellLocation) telephonyManager_.getCellLocation();
	}
}
