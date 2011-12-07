package org.pocketcampus.plugin.authentication.android;

import android.app.IntentService;
import android.content.Intent;
import android.util.Log;

public class AuthenticationService extends IntentService {

	public AuthenticationService() {
		super("authentication@pocketcampus.org");
	}

	@Override
	protected void onHandleIntent(Intent intent) {
		Log.v("DEBUG", "AuthenticationService::onHandleIntent");
		Log.v("DEBUG", "Action: " + intent.getAction());
		Log.v("DEBUG", "Data: " + intent.getDataString());
	}

}
