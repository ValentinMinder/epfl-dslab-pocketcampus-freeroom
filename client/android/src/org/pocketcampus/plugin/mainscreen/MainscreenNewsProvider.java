package org.pocketcampus.plugin.mainscreen;

import java.util.ArrayList;

import org.pocketcampus.R;
import org.pocketcampus.core.plugin.Core;
import org.pocketcampus.core.plugin.PluginBase;

import android.content.Context;
import android.preference.PreferenceManager;
import android.util.Log;

public class MainscreenNewsProvider {
	
	public static void getNews(Context ctx, MainscreenPlugin main) {
		
		MainscreenPlugin.refreshing();
		
		MainscreenPlugin.clean();
		
		Log.d("MainscreenNewsProvider", "Getting News");
		
        // Feeds to display
		ArrayList<PluginBase> plugins = Core.getInstance().getProvidersOf(IMainscreenNewsProvider.class);
		
		Log.d("MainscreenNewsProvider", "Array size: " + plugins.size());


        for(PluginBase plug : plugins) {
    		Log.d("MainscreenNewsProvider", "Current Plugin: " + plug.getPluginInfo().getId());
        	
			if(plug != null) {
				
				Log.d("MainscreenNewsProvider", "Plugin not null");
				
				if(PreferenceManager.getDefaultSharedPreferences(ctx).getBoolean(plug.getPluginInfo().getId().toString(), true)) {
					Log.d("MainscreenNewsProvider","New MainscreenNewsGetter created");
					new MainscreenNewsGetter((IMainscreenNewsProvider)plug,ctx,main).execute();
				}
			}
		}

	}

	
}
