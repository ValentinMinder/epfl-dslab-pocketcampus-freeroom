package org.pocketcampus.plugin.mainscreen;

import java.util.ArrayList;

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
		
		ArrayList<PluginBase> newsProviders = Core.getInstance().getProvidersOf(IMainscreenNewsProvider.class);
		Log.d("MainscreenNewsProvider", "Array size: " + newsProviders.size());

		for(PluginBase plugin : newsProviders) {
			
			if(PreferenceManager.getDefaultSharedPreferences(ctx).getBoolean(plugin.getPluginInfo().getId().toString(), true)) {
				Log.d("MainscreenNewsProvider","New MainscreenNewsGetter created");
				
				new MainscreenNewsGetter((IMainscreenNewsProvider)plugin, ctx, main).execute();
			}
			
		}
	}
	
}
