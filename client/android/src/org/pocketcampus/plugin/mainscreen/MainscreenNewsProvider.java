package org.pocketcampus.plugin.mainscreen;

import org.pocketcampus.R;
import org.pocketcampus.core.plugin.PluginBase;

import android.content.Context;
import android.preference.PreferenceManager;
import android.util.Log;

public class MainscreenNewsProvider {
	
	public static void getNews(Context ctx, MainscreenPlugin main) {
				
		MainscreenPlugin.refreshing();
		
		Log.d("MainscreenNewsProvider", "Getting News");
		
        // Feeds to display
		String[] plugins  = ctx.getResources().getStringArray(R.array.mainscreen_provider_plugins);
		
		Log.d("MainscreenNewsProvider", "Array size: " + plugins.length);


        for(String key : plugins) {
        	
    		Log.d("MainscreenNewsProvider", "Current Plugin: " + key);

        	
        	PluginBase plug = null;
			try {
				Class<?> cl = Class.forName(MainscreenPlugin.PACKAGE+key);
	        	plug = (PluginBase) cl.newInstance();
			} catch (ClassNotFoundException e) {
			} catch (IllegalAccessException e) {
			} catch (InstantiationException e) {
			}
        	
			if(plug != null) {
				
				Log.d("MainscreenNewsProvider", "Plugin not null");
				
				if(PreferenceManager.getDefaultSharedPreferences(ctx).getBoolean(plug.getPluginInfo().getName(), true)) {
					Log.d("MainscreenNewsProvider","New MainscreenNewsGetter created");
					new MainscreenNewsGetter((IMainscreenNewsProvider)plug,ctx,main).execute();
				}
			}
		}

	}

	
}
