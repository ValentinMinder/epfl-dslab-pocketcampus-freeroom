package org.pocketcampus.android.platform.sdk.core;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

import org.pocketcampus.android.platform.sdk.tracker.Tracker;
import static org.pocketcampus.android.platform.sdk.core.PCAndroidConfig.PC_ANDR_CFG;

import org.pocketcampus.R;
import android.app.Application;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.os.Environment;

/**
 * Core PocketCampus class, handles the plugin discovery and initialization. 
 * This class extends Application and is therefore available in every <code>Activity</code> using <code>getApplication()</code>. 
 * 
 * @author Amer C <amer.chamseddine@epfl.ch>
 */
public class GlobalContext extends Application {
	private List<PluginInfo> mPluginInfoList;

	private int mRequestCounter = 0;
	private RequestActivityListener mRequestActivityListener;
	
	@Override
	public void onCreate() {
		super.onCreate();
		
		initializeConfig();
		
		//Starts the Tracker for the google analytics
		Tracker.getInstance().start(getApplicationContext());
		
		mPluginInfoList = new LinkedList<PluginInfo>();
		
		loadPluginManifests();
	}

	private void loadPluginManifests() {
		PackageManager pm = getPackageManager();
		PluginFilter pluginFilter = new PluginFilter(this);
		pluginFilter.setActionConstraint("pocketcampus.intent.action.PLUGIN_MAIN");
		List<ResolveInfo> resolveInfos = pluginFilter.getMatchingPlugins();
		List<String> enabledPlugins = Arrays.asList(PC_ANDR_CFG.getString("ENABLED_PLUGINS").toLowerCase().split("[,]"));

		for(ResolveInfo resolveInfo : resolveInfos) {
			ActivityInfo activityInfo = resolveInfo.activityInfo;
			if(!activityInfo.name.startsWith("org.pocketcampus.plugin."))
				continue;
			if(!enabledPlugins.contains(activityInfo.name.split("[.]")[3].toLowerCase()))
				continue;
			PluginInfo pluginInfo = new PluginInfo();
			try {
				pluginInfo.setMainClassName(activityInfo.name);
				pluginInfo.setMainPackageName(activityInfo.packageName);

				pluginInfo.setIcon(resolveInfo.loadIcon(pm));
				pluginInfo.setLabel(resolveInfo.loadLabel(pm).toString());

			} catch (Exception e) {
				e.printStackTrace();
				continue;
			}

			mPluginInfoList.add(pluginInfo);
		}
	}

	public ArrayList<PluginInfo> getAllPluginInfos() {
		return new ArrayList<PluginInfo>(mPluginInfoList);
	}

	public void displayPlugin(Context ctx, PluginInfo pluginManifest) {
		startPlugin(ctx, new ComponentName(pluginManifest.getMainPackageName(), pluginManifest.getMainClassName()));
	}

	private void startPlugin(Context ctx, ComponentName comp) {
		Intent intent = new Intent();
		intent.setComponent(comp);
		intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		ctx.startActivity(intent);
	}

	public void incrementRequestCounter() {
		if(mRequestCounter == 0 && mRequestActivityListener != null) {
			mRequestActivityListener.requestStarted();
		}
		
		mRequestCounter++;
	}

	public void decrementRequestCounter() {
		mRequestCounter--;
		
		if(mRequestCounter == 0 && mRequestActivityListener != null) {
			mRequestActivityListener.requestStopped();
		}
		
		if(mRequestCounter < 0) {
			throw new RuntimeException("Negative number of queries running?!");
		}
	}

	public void setRequestActivityListener(RequestActivityListener requestActivityListener) {
		mRequestActivityListener = requestActivityListener;
	}

	private void initializeConfig() {
		
		try {
			
			/**
			* First load internal config.
			*   This should be exhaustive/comprehensive
			*   meaning all config params should be assigned a value here.
			*/
			PC_ANDR_CFG.load(getResources().openRawResource(R.raw.pocketcampus));
			
			/**
			* Then override with server config.
			*   The server config file is in the private dir;
			*   It is downloaded and written by the dashboard plugin.
			*/
			try {
				PC_ANDR_CFG.load(openFileInput("pocketcampus.config"));
			} catch (FileNotFoundException e) {
			}
			
			/**
			* Finally override with config file on sdcard.
			*   This is mainly used for development or
			*   debugging purposes.
			*/
			String configFile = Environment.getExternalStorageDirectory() + "/pocketcampus.config";
			if(new File(configFile).exists()) {
				PC_ANDR_CFG.load(new FileInputStream(configFile));
			}
			
			
			//PC_ANDR_CFG.putStringIfNull("SERVER_PROTO", "http");
			//PC_ANDR_CFG.store(new FileOutputStream(""), null);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
}
