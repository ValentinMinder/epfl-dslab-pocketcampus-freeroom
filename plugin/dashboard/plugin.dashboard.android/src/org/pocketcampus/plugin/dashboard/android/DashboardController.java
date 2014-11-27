package org.pocketcampus.plugin.dashboard.android;

import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.params.CoreProtocolPNames;
import org.pocketcampus.platform.android.core.GlobalContext;
import org.pocketcampus.platform.android.core.PluginController;
import org.pocketcampus.platform.android.core.PluginModel;
import org.pocketcampus.platform.android.core.PushNotificationListener;
import org.pocketcampus.plugin.dashboard.android.req.FetchDynamicConfigRequest;

import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager.NameNotFoundException;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;

/**
 * DashboardController
 * 
 * Mainly responsible for fetching dyn config.
 * 
 * @author Amer <amer.chamseddine@epfl.ch>
 * 
 */
public class DashboardController extends PluginController {

	public static class PushNotifListener extends PushNotificationListener {
		@Override
		public void onReceive(Context context, Intent intent) {
			super.onReceive(context, intent);
			Log.v("DEBUG", "DashboardController$PushNotifListener push notif reg finished");
			Intent intenteye = new Intent("org.pocketcampus.plugin.pushnotif.REGISTRATION_FINISHED", 
					Uri.parse("pocketcampus://dashboard.plugin.pocketcampus.org/pushnotif_reg_finished"));
			if(intent.getIntExtra("succeeded", 0) != 0)
				intenteye.putExtra("succeeded", 1);
			if(intent.getIntExtra("failed", 0) != 0)
				intenteye.putExtra("failed", 1);
			if(intent.getIntExtra("networkerror", 0) != 0)
				intenteye.putExtra("networkerror", 1);
			context.startService(intenteye);
		}
	}
	
	private DefaultHttpClient threadSafeClient = null;
	private DashboardModel mModel;
	
	@Override
	public void onCreate() {
		mModel = new DashboardModel(getApplicationContext());
		threadSafeClient = getThreadSafeClient();
		threadSafeClient.getParams().setParameter(CoreProtocolPNames.USER_AGENT, GlobalContext.USER_AGENT);
		
		// initialize ImageLoader
		ImageLoader.getInstance().init(ImageLoaderConfiguration.createDefault(getApplicationContext()));
	}
	
	@Override
	public PluginModel getModel() {
		return mModel;
	}
	
	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		if("org.pocketcampus.plugin.pushnotif.REGISTRATION_FINISHED".equals(intent.getAction())) {
			Bundle extras = intent.getExtras();
			if(extras != null && extras.getInt("succeeded") != 0) {
				Log.v("DEBUG", "DashboardController.onStartCommand push notif reg succeeded");
				// may need to re create thrift client so that pushnotif token is attached in the http headers
			} else if(extras != null && extras.getInt("failed") != 0) {
				Log.v("DEBUG", "DashboardController.onStartCommand push notif reg failed");
			} else if(extras != null && extras.getInt("networkerror") != 0) {
				Log.v("DEBUG", "DashboardController.onStartCommand push notif reg net error");
			} else {
			}
		}
		stopSelf();
		return START_NOT_STICKY;
	}
	
	public void fetchDynamicConfig(DashboardView view) {
		new FetchDynamicConfigRequest(view).start(this, threadSafeClient, getApplicationContext());
	}
	
	public void registerPushNotif() {
        Intent authIntent = new Intent("org.pocketcampus.plugin.pushnotif.REGISTER_FOR_PUSH",
                        Uri.parse("pocketcampus://pushnotif.plugin.pocketcampus.org/reg_for_push"));
        startService(authIntent);
	}
	
	public static String getAppVersion(Context context) {
		try {
			return context.getPackageManager().getPackageInfo(context.getPackageName(), 0 ).versionName;
		} catch (NameNotFoundException e) {
			e.printStackTrace();
		}
		return null;
	}
		
}
