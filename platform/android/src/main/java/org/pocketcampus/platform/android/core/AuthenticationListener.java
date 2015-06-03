package org.pocketcampus.platform.android.core;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

public class AuthenticationListener extends BroadcastReceiver {

	public static final String PC_SESSION_ID_EXTRA = "PC_SESSION_ID";
	
	@Override
	public void onReceive(Context arg0, Intent arg1) {
		((GlobalContext) arg0.getApplicationContext()).setPcSessionId(arg1.getStringExtra(PC_SESSION_ID_EXTRA));
	}

}
