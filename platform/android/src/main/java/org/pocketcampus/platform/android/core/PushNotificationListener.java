package org.pocketcampus.platform.android.core;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

public class PushNotificationListener extends BroadcastReceiver {

	public static final String PUSH_NOTIF_TOKEN_EXTRA = "PUSH_NOTIF_TOKEN";
	
	@Override
	public void onReceive(Context arg0, Intent arg1) {
		((GlobalContext) arg0.getApplicationContext()).setPushNotifToken(arg1.getStringExtra(PUSH_NOTIF_TOKEN_EXTRA));
	}

}
