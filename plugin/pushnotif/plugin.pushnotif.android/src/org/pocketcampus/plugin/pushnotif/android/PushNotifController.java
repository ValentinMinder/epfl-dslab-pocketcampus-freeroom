package org.pocketcampus.plugin.pushnotif.android;

import java.io.File;
import java.net.URI;
import java.util.Locale;

import org.apache.http.HttpResponse;
import org.apache.http.client.RedirectHandler;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.protocol.HttpContext;
import org.pocketcampus.android.platform.sdk.cache.RequestCache;
import org.pocketcampus.android.platform.sdk.core.PluginController;
import org.pocketcampus.android.platform.sdk.core.PluginModel;
import org.pocketcampus.plugin.pushnotif.android.iface.IPushNotifController;
import org.pocketcampus.plugin.pushnotif.android.req.RegisterRequest;
import org.pocketcampus.plugin.pushnotif.android.req.GetTequilaTokenRequest;
import org.pocketcampus.plugin.pushnotif.android.PushNotifModel;
import org.pocketcampus.plugin.pushnotif.shared.PlatformType;
import org.pocketcampus.plugin.pushnotif.shared.PushNotifRequest;
import org.pocketcampus.plugin.pushnotif.shared.TequilaToken;
import org.pocketcampus.plugin.pushnotif.shared.PushNotifService.Client;
import org.pocketcampus.plugin.pushnotif.shared.PushNotifService.Iface;

import com.google.android.gcm.GCMRegistrar;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
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
	
	private TequilaToken teqToken;
	private String registrationId; 
	
	@Override
	public void onCreate() {
		mModel = new PushNotifModel(getApplicationContext());
		mClient = (Iface) getClient(new Client.Factory(), mPluginName);
	}
	
	@Override
	public int onStartCommand(Intent aIntent, int flags, int startId) {
		Bundle extras = aIntent.getExtras();
		if("org.pocketcampus.plugin.authentication.AUTHENTICATION_FINISHED".equals(aIntent.getAction())) {
			if(extras != null && extras.getInt("usercancelled") != 0) {
				Log.v("DEBUG", "PushNotifController::onStartCommand user cancelled");
				GCMRegistrar.unregister(this);
				// TODO
			} else if(extras != null && extras.getString("tequilatoken") != null) {
				Log.v("DEBUG", "PushNotifController::onStartCommand auth succ");
				tokenAuthenticationFinished();
			} else {
				Log.v("DEBUG", "PushNotifController::onStartCommand auth failed");
				GCMRegistrar.unregister(this);
				// TODO
			}
		} else if("org.pocketcampus.plugin.authentication.LOGOUT".equals(aIntent.getAction())) {
			Log.v("DEBUG", "PushNotifController::onStartCommand logout");
			GCMRegistrar.unregister(this);
			// TODO
		} else if("org.pocketcampus.plugin.pushnotif.REGISTRATION_ID".equals(aIntent.getAction())) {
			if(extras != null && extras.getString("registrationid") != null) {
				Log.v("DEBUG", "PushNotifController::onStartCommand regisration_id ok");
				registrationId = extras.getString("registrationid");
				getTequilaToken();
			} else {
				Log.v("DEBUG", "PushNotifController::onStartCommand regisration_id but no regisrationid");
			}
		}
		stopSelf();
		return START_NOT_STICKY;
	}
	
	@Override
	public PluginModel getModel() {
		return mModel;
	}
	
	public void setRegistrationId(String val) {
		registrationId = val;
	}
	
	public void getTequilaToken() {
		Log.v("DEBUG", "PushNotifController::getTequilaToken");
		new GetTequilaTokenRequest().start(this, mClient, null);
	}
	public void gotTequilaToken(TequilaToken token) {
		Log.v("DEBUG", "PushNotifController::gotTequilaToken");
		teqToken = token;
		pingAuthPlugin(getApplicationContext(), teqToken.getITequilaKey());
	}
	public void tokenAuthenticationFinished() {
		Log.v("DEBUG", "PushNotifController::tokenAuthenticationFinished");
		PushNotifRequest req = new PushNotifRequest(PlatformType.PC_PLATFORM_ANDROID);
		req.setIAndroidRegistrationId(registrationId);
		req.setIAuthenticatedToken(teqToken);
		new RegisterRequest().start(this, mClient, req);
	}
	public void registrationFinished(boolean success) {
		Log.v("DEBUG", "PushNotifController::registrationFinished");
		if(success) {
			GCMRegistrar.setRegisteredOnServer(this, true);
		} else {
			Log.v("DEBUG", "PushNotifController::registrationFinished failed to reg on PC server");
		}
	}
	public void unregistrationFinished(boolean success) {
		// TODO
	}

	public static void pingAuthPlugin(Context context, String tequilaToken) {
		Intent authIntent = new Intent("org.pocketcampus.plugin.authentication.ACTION_AUTHENTICATE",
				Uri.parse("pocketcampus://authentication.plugin.pocketcampus.org/authenticatetoken"));
		authIntent.putExtra("tequilatoken", tequilaToken);
		authIntent.putExtra("callbackurl", "pocketcampus://pushnotif.plugin.pocketcampus.org/tokenauthenticated");
		authIntent.putExtra("shortname", "pushnotif");
		authIntent.putExtra("longname", "PushNotif");
		context.startService(authIntent);
	}
	
}
