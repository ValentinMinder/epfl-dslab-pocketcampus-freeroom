package org.pocketcampus.plugin.pushnotif.android;

import static org.pocketcampus.platform.android.core.PCAndroidConfig.PC_ANDR_CFG;

import org.pocketcampus.platform.android.core.PluginController;
import org.pocketcampus.platform.android.core.PluginModel;
import org.pocketcampus.platform.android.core.PushNotificationListener;
import org.pocketcampus.plugin.pushnotif.android.iface.IPushNotifController;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

import com.google.android.gcm.GCMRegistrar;

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

	/**
	 * Stores reference to the Model associated with this plugin.
	 */
	private PushNotifModel mModel;
	
	private String pushToken = null;
	
	@Override
	public void onCreate() {
		mModel = new PushNotifModel(getApplicationContext());
	}
	
	@Override
	public int onStartCommand(Intent aIntent, int flags, int startId) {
		Bundle extras = aIntent.getExtras();
		if("org.pocketcampus.plugin.pushnotif.GCM_INTENT".equals(aIntent.getAction())) {
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
		pushToken = val;
		pingBackAndStop("succeeded");
	}
	
	private void pingBackAndStop(String extra) {
		Intent intent = new Intent();
		intent.setAction("org.pocketcampus.plugin.pushnotif.REGISTRATION_FINISHED");
		intent.putExtra(PushNotificationListener.PUSH_NOTIF_TOKEN_EXTRA, pushToken);
		if(extra != null)
			intent.putExtra(extra, 1);
		sendBroadcast(intent, "org.pocketcampus.permissions.USE_PC_PUSHNOTIF"); 
		stopSelf();
	}

}
