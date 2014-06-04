package org.pocketcampus.plugin.dashboard.android;

import org.apache.http.impl.client.DefaultHttpClient;
import org.pocketcampus.android.platform.sdk.core.PluginController;
import org.pocketcampus.android.platform.sdk.core.PluginModel;
import org.pocketcampus.plugin.dashboard.android.req.FetchDynamicConfigRequest;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager.NameNotFoundException;
import android.net.Uri;

/**
 * DashboardController
 * 
 * Mainly responsible for fetching dyn config.
 * 
 * @author Amer <amer.chamseddine@epfl.ch>
 * 
 */
public class DashboardController extends PluginController {

	private DefaultHttpClient threadSafeClient = null;
	private DashboardModel mModel;
	
	@Override
	public void onCreate() {
		mModel = new DashboardModel(getApplicationContext());
		threadSafeClient = getThreadSafeClient();
	}
	
	@Override
	public PluginModel getModel() {
		return mModel;
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
