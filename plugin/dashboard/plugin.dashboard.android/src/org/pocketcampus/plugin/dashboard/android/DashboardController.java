package org.pocketcampus.plugin.dashboard.android;

import org.apache.http.impl.client.DefaultHttpClient;
import org.pocketcampus.android.platform.sdk.core.PluginController;
import org.pocketcampus.android.platform.sdk.core.PluginModel;
import org.pocketcampus.plugin.dashboard.android.req.FetchDynamicConfigRequest;

import android.content.Context;
import android.content.pm.PackageManager.NameNotFoundException;

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
	
	public void fetchDynamicConfig() {
		new FetchDynamicConfigRequest().start(this, threadSafeClient, getApplicationContext());
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
