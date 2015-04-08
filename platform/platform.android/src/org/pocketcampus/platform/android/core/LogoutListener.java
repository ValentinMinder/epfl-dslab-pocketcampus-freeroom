package org.pocketcampus.platform.android.core;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

public class LogoutListener extends BroadcastReceiver {

	@Override
	public void onReceive(Context arg0, Intent arg1) {
		((GlobalContext) arg0.getApplicationContext()).setPcSessionId(null);
	}

}
