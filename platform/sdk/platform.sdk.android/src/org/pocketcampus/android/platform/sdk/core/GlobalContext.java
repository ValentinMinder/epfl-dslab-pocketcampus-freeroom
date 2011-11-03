package org.pocketcampus.android.platform.sdk.core;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.apache.thrift.TServiceClient;
import org.pocketcampus.android.platform.sdk.ui.Icon;

import android.app.Application;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.pm.ResolveInfo;

/**
 * Core PocketCampus class, handles the plugin discovery and initialization. 
 * This class extends Application and is therefore available in every <code>Activity</code> using <code>getApplication()</code>. 
 * 
 * @author Florian
 */
public class GlobalContext extends Application {
	/** Binds the package names of the plugins to their infos. */
	private HashMap<String, PluginInfo> mPluginInfoMap;
	private HashMap<String, TServiceClient> mPluginClientMap;

	private int mRequestCounter = 0;
	private RequestActivityListener mRequestActivityListener;
	
	@Override
	public void onCreate() {
		super.onCreate();
		
		mPluginInfoMap = new HashMap<String, PluginInfo>();
		mPluginClientMap = new HashMap<String, TServiceClient>();
		
		loadPluginManifests();
	}

	/**
	 * Loads the plugin list from the application Manifest.
	 * 
	 * @param applicationManifest
	 */
	private void loadPluginManifests() {
		PluginFilter pluginFilter = new PluginFilter(this);
		pluginFilter.setActionConstraint("pocketcampus.intent.action.PLUGIN_MAIN");
		List<ResolveInfo> resolveInfos = pluginFilter.getMatchingPlugins();

		for(ResolveInfo resolveInfo : resolveInfos) {
			ActivityInfo activityInfo = resolveInfo.activityInfo;

			String label = getLabel(activityInfo);
			String pluginMainClassName = activityInfo.name;
			Icon pluginIcon = new Icon(activityInfo.icon);

			PluginInfo pluginInfo = new PluginInfo();
			try {
				pluginInfo.setLabel(label);
				pluginInfo.setMainClassName(pluginMainClassName);
				//pluginInfo.setPreferenceClassName(TODO);
				pluginInfo.setIcon(pluginIcon);

			} catch (ClassNotFoundException e) {
				// TODO log the error
				e.printStackTrace();
				continue;
			}

			// adds plugin to the map
			String packageName = getActivityPackageName(activityInfo);
			mPluginInfoMap.put(packageName, pluginInfo);
		}
	}

	private String getLabel(ActivityInfo activityInfo) {
		String label = activityInfo.nonLocalizedLabel.toString();
		if(activityInfo.labelRes != 0) {
			label = getString(activityInfo.labelRes);
		}

		return label;
	}

	/**
	 * Extracts the package name of an Activity, using its <code>ActivityInfo</code>.
	 * @param activityInfo
	 * @return
	 */
	private String getActivityPackageName(ActivityInfo activityInfo) {
		String[] nameParts = activityInfo.name.split("\\.");
		String className = nameParts[nameParts.length - 1];
		String packageName = activityInfo.name.replace("."+className, "");

		return packageName;
	}

	/**
	 * 
	 * @param packageName
	 * @return
	 */
//	public TServiceClient getPluginClient(String packageName) {
//		if(mPluginClientMap.containsKey(packageName)) {
//			// client already exists
//			return mPluginClientMap.get(packageName);
//			
//		}
//		
//		// we need to create the client
//		return null;
//	}

	/**
	 * Gets the <code>PluginInfo</code> of the plugin from the given package.
	 * @param pluginPackageName
	 * @return
	 */
	public PluginInfo getPluginInfo(String pluginPackageName) {
		if(mPluginInfoMap.containsKey(pluginPackageName)) {
			//System.out.println("Found manifest for " + pluginPackageName);
			return mPluginInfoMap.get(pluginPackageName);
		}

		System.out.println("Unknown plugin package " + pluginPackageName);
		return null;
	}

	/**
	 * Returns the <code>PluginInfo</code>s of all available plugins.
	 * 
	 * @return
	 */
	public ArrayList<PluginInfo> getAllPluginInfos() {
		return new ArrayList<PluginInfo>(mPluginInfoMap.values());
	}

	/**
	 * Launches the plugin main <code>Activity</code>.
	 * 
	 * @param ctx
	 * @param pluginManifest
	 */
	public void displayPlugin(Context ctx, PluginInfo pluginManifest) {
		Class<? extends PluginView> pluginMainClass = pluginManifest.getMainClass();
		startPlugin(ctx, pluginMainClass);
	}

	/**
	 * Launches the plugin preference <code>Activity</code>.
	 * TODO, use PLUGIN_PREFERENCES category?
	 * 
	 * @param ctx
	 * @param pluginManifest
	 */
	//	public void displayPluginPreference(Context ctx, PluginManifest pluginManifest) {
	//		Class<? extends PluginActivity> pluginPreferenceClass = pluginManifest.getPreferenceActivity();
	//		startPlugin(ctx, pluginPreferenceClass);
	//	}

	private void startPlugin(Context ctx, Class<? extends PluginView> pluginClass) {
		Intent intent = new Intent(ctx.getApplicationContext(), pluginClass);
		intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		ctx.startActivity(intent);
	}

	public void incrementRequestCounter() {
		if(mRequestCounter == 0) {
			mRequestActivityListener.requestStarted();
		}
		
		mRequestCounter++;
	}

	public void decrementRequestCounter() {
		mRequestCounter--;
		
		if(mRequestCounter == 0) {
			mRequestActivityListener.requestStopped();
		}
		
		if(mRequestCounter < 0) {
			throw new RuntimeException("Negative number of queries running?!");
		}
	}

	public void setRequestActivityListener(RequestActivityListener requestActivityListener) {
		mRequestActivityListener = requestActivityListener;
	}

}
