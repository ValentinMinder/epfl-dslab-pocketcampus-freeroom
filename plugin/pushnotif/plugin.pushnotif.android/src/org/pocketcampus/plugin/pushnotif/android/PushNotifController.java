package org.pocketcampus.plugin.pushnotif.android;

import static org.pocketcampus.android.platform.sdk.core.PCAndroidConfig.PC_ANDR_CFG;

import org.pocketcampus.android.platform.sdk.core.GlobalContext;
import org.pocketcampus.android.platform.sdk.core.PluginController;
import org.pocketcampus.android.platform.sdk.core.PluginModel;
import org.pocketcampus.plugin.pushnotif.android.iface.IPushNotifController;
import org.pocketcampus.plugin.pushnotif.android.req.DeleteMappingRequest;
import org.pocketcampus.plugin.pushnotif.android.PushNotifModel;
import org.pocketcampus.plugin.pushnotif.shared.PushNotifService.Client;
import org.pocketcampus.plugin.pushnotif.shared.PushNotifService.Iface;

import com.google.android.gcm.GCMRegistrar;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;

/**
 * PushNotifController - Main logic for the PushNotif Plugin.
 * 
 * This class issues requests to the PushNotif PocketCampus
 * server to get the PushNotif data of the logged in user.
 * 
 * @author Amer <amer.chamseddine@epfl.ch>
 * 
 */
public class PushNotifController extends PluginController implements IPushNotifController{

	public static class Logouter extends BroadcastReceiver {
		@Override
		public void onReceive(Context context, Intent intent) {
			Log.v("DEBUG", "PushNotifController$Logouter logging out");
			Intent authIntent = new Intent("org.pocketcampus.plugin.authentication.LOGOUT",
					Uri.parse("pocketcampus://pushnotif.plugin.pocketcampus.org/logout"));
			context.startService(authIntent);
		}
	};

	/**
	 *  This name must match given in the Server.java file in plugin.launcher.server.
	 *  It's used to route the request to the right server implementation.
	 */
	private String mPluginName = "pushnotif";
	
	/**
	 * Stores reference to the Model associated with this plugin.
	 */
	private PushNotifModel mModel;
	
	/**
	 * HTTP Clients used to communicate with the PocketCampus server.
	 * Use thrift to transport the data.
	 */
	private Iface mClient;

	private String callbackUrl = null;
	
	@Override
	public void onCreate() {
		mModel = new PushNotifModel(getApplicationContext());
		mClient = (Iface) getClient(new Client.Factory(), mPluginName);
	}
	
	@Override
	public int onStartCommand(Intent aIntent, int flags, int startId) {
		Bundle extras = aIntent.getExtras();
		if("org.pocketcampus.plugin.authentication.LOGOUT".equals(aIntent.getAction())) {
			Log.v("DEBUG", "PushNotifController::onStartCommand logout");
			new DeleteMappingRequest().start(this, mClient, "dummy");
		} else if("org.pocketcampus.plugin.pushnotif.GCM_INTENT".equals(aIntent.getAction())) {
			if(extras != null && extras.getString("registrationid") != null) {
				Log.v("DEBUG", "PushNotifController::onStartCommand regisration_id ok");
				setRegistrationIdAndStop(extras.getString("registrationid"));
			} else if(extras != null && extras.getInt("error") != 0) {
				Log.v("DEBUG", "PushNotifController::onStartCommand GCM Intent error");
				pingBackAndStop("failed");
			} else {
				Log.v("DEBUG", "PushNotifController::onStartCommand malformed gcm intent");
				pingBackAndStop("failed");
			}
		} else if("org.pocketcampus.plugin.pushnotif.REGISTER_FOR_PUSH".equals(aIntent.getAction())) {
			Log.v("DEBUG", "PushNotifController::onStartCommand received request to register");
			if(extras != null && extras.getString("callbackurl") != null)
				callbackUrl = extras.getString("callbackurl");
			startRegistrationProcess();
		} else {
			Log.v("DEBUG", "PushNotifController::onStartCommand malformed action");
			stopSelf();
		}
		return START_NOT_STICKY;
	}
	
	@Override
	public PluginModel getModel() {
		return mModel;
	}
	
	public void startRegistrationProcess() {
		

        // Make sure the device has the proper dependencies.
        GCMRegistrar.checkDevice(this);
        // Make sure the manifest was properly set - comment out this line
        // while developing the app, then uncomment it when it's ready.
        GCMRegistrar.checkManifest(this);

        final String regId = GCMRegistrar.getRegistrationId(this);
        if (regId.equals("")) {
			Log.v("DEBUG", "PushNotifMainView::onDisplay not reg with gcm");
            // Automatically registers application on startup.
            GCMRegistrar.register(this, PC_ANDR_CFG.getString("GCM_SENDER_ID"));
        } else {
			Log.v("DEBUG", "PushNotifMainView::onDisplay reg with gcm");
            // Device is already registered on GCM, check server.
			setRegistrationIdAndStop(regId);
        }
	}
	
	public void setRegistrationIdAndStop(String val) {
		((GlobalContext) getApplicationContext()).setPushNotifToken(val);
		pingBackAndStop("succeeded");
	}
	
	public void deleteMappingReqFinished() {
		Log.v("DEBUG", "PushNotifController::deleteMappingReqFinished");
		stopSelf();
	}
	
	private void pingBackAndStop(String extra) {
		if(callbackUrl == null) {
			Log.v("DEBUG", "PushNotifController::pingBack SORRY we don't have a callbackUrl");
			stopSelf();
			return;
		}
		Intent intenteye = new Intent("org.pocketcampus.plugin.pushnotif.REGISTRATION_FINISHED", Uri.parse(callbackUrl));
		if(extra != null)
			intenteye.putExtra(extra, 1); // failed or succeeded
		startService(intenteye);
		stopSelf();
	}

}
