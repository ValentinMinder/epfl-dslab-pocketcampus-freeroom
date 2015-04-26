/*
 * Copyright 2012 Google Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.pocketcampus.plugin.pushnotif.gcm;

import org.pocketcampus.plugin.pushnotif.android.PushNotifController;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;

import com.google.android.gcm.GCMBaseIntentService;
import com.google.android.gcm.GCMRegistrar;

import static org.pocketcampus.platform.android.core.PCAndroidConfig.PC_ANDR_CFG;

/**
 * IntentService responsible for handling GCM messages.
 */
public class GCMIntentService extends GCMBaseIntentService {

    private static final String TAG = "GCMIntentService";

    public GCMIntentService() {
        super(PC_ANDR_CFG.getString("GCM_SENDER_ID"));
    }

    @Override
    protected void onRegistered(Context context, String registrationId) {
        Log.i(TAG, "Device registered: regId = " + registrationId);
        fwdGCMIntent(context, registrationId, null);
    }

    @Override
    protected void onUnregistered(Context context, String registrationId) {
        Log.i(TAG, "Device unregistered");
        if (GCMRegistrar.isRegisteredOnServer(context)) {
            // no need to unreg from PC server
        	// coz google will tell us to remove the token
        	// the next time we try to send something to this guy
        } else {
            // This callback results from the call to unregister made on
            // ServerUtilities when the registration to the server failed.
            Log.i(TAG, "Ignoring unregister callback");
        }
    }

    @Override
    protected void onMessage(Context context, Intent intent) {
        Log.i(TAG, "Received message");
        Bundle extras = intent.getExtras();
        if(extras != null && extras.getString("pluginName") != null) {
        	Log.i(TAG, "Fwding it to plugin");
        	sendPluginMessage(context, extras.getString("pluginName"), intent);
        } else {
        	Log.i(TAG, "Couldn't understand it");
        	// don't understand this msg
        }
    }

    @Override
    protected void onDeletedMessages(Context context, int total) {
        Log.i(TAG, "Received deleted messages notification");
    }

    @Override
    public void onError(Context context, String errorId) {
        Log.i(TAG, "Received error: " + errorId);
        fwdGCMIntent(context, null, "error");
    }

    @Override
    protected boolean onRecoverableError(Context context, String errorId) {
        // log message
        Log.i(TAG, "Received recoverable error: " + errorId);
        return super.onRecoverableError(context, errorId);
    }

	private void fwdGCMIntent(Context context, String regId, String extra) {
		Intent regIntent = new Intent("org.pocketcampus.plugin.pushnotif.GCM_INTENT",
				Uri.parse("pocketcampus://pushnotif.plugin.pocketcampus.org/gcm_intent"));
		if(regId != null)
			regIntent.putExtra("registrationid", regId);
		if(extra != null)
			regIntent.putExtra(extra, 1); // error
		regIntent.setClassName(context.getApplicationContext(), PushNotifController.class.getName());
		context.startService(regIntent);
	}
	
	/***
	 * Requires plugin to be CamelCase
	 */
	private void sendPluginMessage(Context context, String plugin, Intent message) {
		Log.i(TAG, "Sending message to '" + plugin + "' plugin");
		Intent regIntent = new Intent("org.pocketcampus.plugin.pushnotif.PUSHNOTIF_MESSAGE",
				Uri.parse("pocketcampus://" + plugin.toLowerCase() + ".plugin.pocketcampus.org/pushnotif_message"));
		regIntent.putExtras(message);
		regIntent.setClassName(context.getApplicationContext(), "org.pocketcampus.plugin." + plugin.toLowerCase() + ".android." + plugin + "Controller");
		context.startService(regIntent);
	}
	
}
