package org.pocketcampus.platform.android.core;

import static org.pocketcampus.platform.android.core.PCAndroidConfig.PC_ANDR_CFG;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import android.content.pm.ApplicationInfo;
import org.pocketcampus.platform.android.BuildConfig;
import org.pocketcampus.platform.android.R;
import org.pocketcampus.platform.android.tracker.GATracker;

import android.app.Application;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.os.Bundle;
import android.os.Environment;
import android.webkit.WebView;

/**
 * Core PocketCampus class, handles the plugin discovery and initialization. 
 * This class extends Application and is therefore available in every <code>Activity</code> using <code>getApplication()</code>. 
 * 
 * @author Amer C <amer.chamseddine@epfl.ch>
 */
public class GlobalContext extends Application {
	private Map<String, PluginInfo> mPluginInfoList;

	private int mRequestCounter = 0;
	private RequestActivityListener mRequestActivityListener;
	private String pushNotifToken = null;
	private String pcSessId = null;
	
	public static final String GA_EVENT_CATEG = "UserAction";
	public static String USER_AGENT = "PocketCampus";
	
	@Override
	public void onCreate() {
		super.onCreate();
		
		refresh();
	}

	public String getAppVersion() {
		try {
			return getPackageManager().getPackageInfo(getPackageName(), 0).versionName;
		} catch (PackageManager.NameNotFoundException e) {
			e.printStackTrace();
		}
		return null;
	}

	public void refresh() {
		loadConfig();
		loadPluginManifests();
		
		//Starts the Tracker for the google analytics
		GATracker.getInstance().start(getApplicationContext());
		
		USER_AGENT = new WebView(getApplicationContext()).getSettings().getUserAgentString();
	}

	private void loadPluginManifests() {
		mPluginInfoList = new HashMap<String, PluginInfo>();
		
		PackageManager pm = getPackageManager();
		PluginFilter pluginFilter = new PluginFilter(this);
		pluginFilter.setActionConstraint("pocketcampus.intent.action.PLUGIN_MAIN");
		List<ResolveInfo> resolveInfos = pluginFilter.getMatchingPlugins();
		List<String> enabledPlugins = Arrays.asList(PC_ANDR_CFG.getString("ENABLED_PLUGINS").toLowerCase().split("[,]"));

		for(ResolveInfo resolveInfo : resolveInfos) {
			ActivityInfo activityInfo = resolveInfo.activityInfo;
			if(!activityInfo.name.startsWith("org.pocketcampus.plugin.")) {
				continue;
			}
			String shName = activityInfo.name.split("[.]")[3].toLowerCase();
			if(!enabledPlugins.contains(shName)) {
//				if(getPackageName().equals(activityInfo.packageName))
//					pm.setComponentEnabledSetting(new ComponentName(activityInfo.packageName, activityInfo.name), PackageManager.COMPONENT_ENABLED_STATE_DISABLED, PackageManager.DONT_KILL_APP);
				continue;
			}
			PluginInfo pluginInfo = new PluginInfo();
			try {
				pluginInfo.setMainClassName(activityInfo.name);
				pluginInfo.setMainPackageName(activityInfo.packageName);
				
				pluginInfo.setId(shName);

				pluginInfo.setIcon(resolveInfo.loadIcon(pm));
				pluginInfo.setLabel(resolveInfo.loadLabel(pm).toString());

			} catch (Exception e) {
				e.printStackTrace();
				continue;
			}

			mPluginInfoList.put(shName, pluginInfo);
		}
	}

	public ArrayList<PluginInfo> getAllPluginInfos() {
		return new ArrayList<PluginInfo>(mPluginInfoList.values());
	}

	public void displayPlugin(Context ctx, PluginInfo pluginManifest) {
		startPlugin(ctx, new ComponentName(pluginManifest.getMainPackageName(), pluginManifest.getMainClassName()));
	}
	
	public void startPluginActivity(Context ctx, String plugin, String activity, String action, Bundle extras) {
		PluginInfo pi = mPluginInfoList.get(plugin);
		if(pi == null)
			return;
		startPluginWithActionAndExtras(ctx, new ComponentName(pi.getMainPackageName(), activity), action, extras);
	}

	private void startPlugin(Context ctx, ComponentName comp) {
		startPluginWithActionAndExtras(ctx, comp, null, null);
	}

	private void startPluginWithActionAndExtras(Context ctx, ComponentName comp, String action, Bundle extras) {
		Intent intent = new Intent();
		intent.setComponent(comp);
		if(action != null)
			intent.setAction(action);
		if(extras != null)
			intent.putExtras(extras);
		intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		ctx.startActivity(intent);
	}

	public void setPushNotifToken(String tok) {
		pushNotifToken = tok;
	}
	
	public String getPushNotifToken() {
		return pushNotifToken;
	}

	public void setPcSessionId(String s) {
		pcSessId = s;
	}
	
	public String getPcSessionId() {
		return pcSessId;
	}

	public boolean hasPcSessionId() {
		return (pcSessId != null);
	}

	public void incrementRequestCounter() {
		mRequestCounter++;
		
		if(mRequestActivityListener != null) {
			mRequestActivityListener.requestsChanged(mRequestCounter);
		}
	}

	public void decrementRequestCounter() {
		mRequestCounter--;
		
		if(mRequestActivityListener != null) {
			mRequestActivityListener.requestsChanged(mRequestCounter);
		}
		
		if(mRequestCounter < 0) {
			throw new RuntimeException("Negative number of queries running?!");
		}
	}
	
	public int getRequestsCount() {
		return mRequestCounter;
	}

	public void setRequestActivityListener(RequestActivityListener requestActivityListener) {
		mRequestActivityListener = requestActivityListener;
	}

	private void loadConfig() {
		
		try {
			
			/**
			* First load internal config.
			*   This should be exhaustive/comprehensive
			*   meaning all config params should be assigned a value here.
			*/
			if ( 0 != ( getApplicationInfo().flags & ApplicationInfo.FLAG_DEBUGGABLE ) ) {
				PC_ANDR_CFG.load(getResources().openRawResource(R.raw.pocketcampus_debug));
			} else {
				PC_ANDR_CFG.load(getResources().openRawResource(R.raw.pocketcampus_release));
			}

		} catch (IOException e) {
			e.printStackTrace();
		}
		try {
			
			/**
			* Then override with common server config.
			*   The server config file is in the private dir;
			*   It is downloaded and written by the dashboard plugin.
			*/
			try {
				PC_ANDR_CFG.load(openFileInput("pocketcampus_common.config"));
			} catch (FileNotFoundException e) {
			}
			
		} catch (IOException e) {
			e.printStackTrace();
		}
		try {

			/**
			 * Then override with authenticated server config.
			 *   The server config file is in the private dir;
			 *   It is downloaded and written by the dashboard plugin.
			 */
			try {
				PC_ANDR_CFG.load(openFileInput("pocketcampus_authenticated.config"));
			} catch (FileNotFoundException e) {
			}

		} catch (IOException e) {
			e.printStackTrace();
		}
		try {
			
			/**
			* Finally override with config file on sdcard.
			*   This is mainly used for development or
			*   debugging purposes.
			*/
			String configFile = Environment.getExternalStorageDirectory() + "/pocketcampus.config";
			if(new File(configFile).exists()) {
				PC_ANDR_CFG.load(new FileInputStream(configFile));
			}
			
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
}
