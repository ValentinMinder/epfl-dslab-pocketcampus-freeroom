package org.pocketcampus.plugin.isacademia.android;

import org.pocketcampus.platform.android.cache.RequestCache;
import org.pocketcampus.platform.android.core.AuthenticationListener;
import org.pocketcampus.platform.android.core.GlobalContext;
import org.pocketcampus.platform.android.core.PluginController;
import org.pocketcampus.platform.android.core.PluginModel;
import org.pocketcampus.plugin.isacademia.android.iface.IIsAcademiaController;
import org.pocketcampus.plugin.isacademia.android.iface.IIsAcademiaView;
import org.pocketcampus.plugin.isacademia.android.req.GetScheduleRequest;
import org.pocketcampus.plugin.isacademia.shared.IsAcademiaService.Client;
import org.pocketcampus.plugin.isacademia.shared.IsAcademiaService.Iface;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask.Status;
import android.os.Bundle;
import android.util.Log;

/**
 * IsAcademiaController - Main logic for the IsAcademia Plugin.
 * 
 * This class issues requests to the IsAcademia PocketCampus
 * server to get the IsAcademia data of the logged in user.
 * 
 * @author Amer <amer.chamseddine@epfl.ch>
 * 
 */
public class IsAcademiaController extends PluginController implements IIsAcademiaController{

	public static class Logouter extends BroadcastReceiver {
		@Override
		public void onReceive(Context context, Intent intent) {
			Log.v("DEBUG", "IsAcademiaController$Logouter logging out");
			Intent authIntent = new Intent("org.pocketcampus.plugin.authentication.LOGOUT",
					Uri.parse("pocketcampus://isacademia.plugin.pocketcampus.org/logout"));
			context.startService(authIntent);
		}
	};


	public static class AuthListener extends AuthenticationListener {
		@Override
		public void onReceive(Context context, Intent intent) {
			super.onReceive(context, intent);
			Log.v("DEBUG", "IsAcademiaController$AuthListener auth finished");
			Intent intenteye = new Intent("org.pocketcampus.plugin.authentication.AUTHENTICATION_FINISHED", 
					Uri.parse("pocketcampus://isacademia.plugin.pocketcampus.org/auth_finished"));
			if(intent.getIntExtra("selfauthok", 0) != 0)
				intenteye.putExtra("selfauthok", 1);
			if(intent.getIntExtra("usercancelled", 0) != 0)
				intenteye.putExtra("usercancelled", 1);
			context.startService(intenteye);
		}
	};

	/**
	 *  This name must match given in the Server.java file in plugin.launcher.server.
	 *  It's used to route the request to the right server implementation.
	 */
	private String mPluginName = "isacademia";
	
	/**
	 * Stores reference to the Model associated with this plugin.
	 */
	private IsAcademiaModel mModel;
	
	/**
	 * HTTP Clients used to communicate with the PocketCampus server.
	 * Use thrift to transport the data.
	 */
	private Iface mClient;
	
	private GetScheduleRequest getScheduleRequest = null;
	
	@Override
	public void onCreate() {
		mModel = new IsAcademiaModel(getApplicationContext());
		mClient = (Iface) getClient(new Client.Factory(), mPluginName);
	}
	
	@Override
	public int onStartCommand(Intent aIntent, int flags, int startId) {
		if("org.pocketcampus.plugin.authentication.AUTHENTICATION_FINISHED".equals(aIntent.getAction())) {
			Bundle extras = aIntent.getExtras();
			if(extras != null && extras.getInt("selfauthok") != 0) {
				Log.v("DEBUG", "IsAcademiaController::onStartCommand auth succ");
				mClient = (Iface) getClient(new Client.Factory(), mPluginName); // need to recreate thrift client coz old one will not have the sessId http header attached
				mModel.getListenersToNotify().authenticationFinished();
			} else if(extras != null && extras.getInt("usercancelled") != 0) {
				Log.v("DEBUG", "IsAcademiaController::onStartCommand user cancelled");
				mModel.getListenersToNotify().userCancelledAuthentication();
			} else {
				Log.v("DEBUG", "IsAcademiaController::onStartCommand auth failed");
				mModel.getListenersToNotify().authenticationFailed();
			}
		}
		if("org.pocketcampus.plugin.authentication.LOGOUT".equals(aIntent.getAction())) {
			Log.v("DEBUG", "IsAcademiaController::onStartCommand logout");
			RequestCache.invalidateCache(this, GetScheduleRequest.class.getCanonicalName());
		}
		stopSelf();
		return START_NOT_STICKY;
	}
	
	@Override
	public PluginModel getModel() {
		return mModel;
	}
	
	public void refreshSchedule(IIsAcademiaView caller, String dayKey, boolean useCache) {
		if(getScheduleRequest != null && getScheduleRequest.getStatus() != Status.FINISHED)
			return;
//		System.out.println("FIRED");
		getScheduleRequest = new GetScheduleRequest(caller);
		getScheduleRequest.setBypassCache(!useCache);
		getScheduleRequest.start(this, mClient, dayKey);
	}
	

	/*****
	 * HELPER CLASSES AND FUNCTIONS
	 */

	public static void pingAuthPlugin(Context context) {
		Intent authIntent = new Intent("org.pocketcampus.plugin.authentication.ACTION_AUTHENTICATE",
				Uri.parse("pocketcampus://authentication.plugin.pocketcampus.org/authenticate"));
		authIntent.putExtra("selfauth", true);
		context.startService(authIntent);
	}
	
	public static boolean sessionExists(Context context) {
		return ((GlobalContext) context.getApplicationContext()).hasPcSessionId();
	}
	
	
}
