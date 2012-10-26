package org.pocketcampus.plugin.pushnotif.gcm;

import android.content.Context;

import com.google.android.gcm.GCMBroadcastReceiver;

public class PCGCMBroadcastReceiver extends GCMBroadcastReceiver {

	@Override
	protected String getGCMIntentServiceClassName(Context context) {
		return "org.pocketcampus.plugin.pushnotif.gcm.GCMIntentService";
	}
	
}
