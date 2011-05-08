package org.pocketcampus.plugin.mainscreen;

import java.util.ArrayList;
import java.util.List;

import org.pocketcampus.R;
import org.pocketcampus.core.plugin.PluginBase;

import android.content.Context;
import android.preference.PreferenceManager;

public class MainscreenNewsProvider {
	
	public static List<MainscreenNews> getNews(Context ctx) {
		List<MainscreenNews> l = new ArrayList<MainscreenNews>();

        // Feeds to display
		String[] plugins  = ctx.getResources().getStringArray(R.array.mainscreen_provider_plugins);
		

        for(String key : plugins) {
        	
        	PluginBase plug = null;
			try {
				Class<?> cl = Class.forName(MainscreenPlugin.PACKAGE+key);
	        	plug = (PluginBase) cl.newInstance();
			} catch (ClassNotFoundException e) {
			} catch (IllegalAccessException e) {
			} catch (InstantiationException e) {
			}
        	
			if(plug != null) {
				if(PreferenceManager.getDefaultSharedPreferences(ctx).getBoolean(plug.getPluginInfo().getName(), false)) {
					l.addAll(((IMainscreenNewsProvider)plug).getNews(ctx));
				}
			}
		}
		

		return l;
	}

	
}
