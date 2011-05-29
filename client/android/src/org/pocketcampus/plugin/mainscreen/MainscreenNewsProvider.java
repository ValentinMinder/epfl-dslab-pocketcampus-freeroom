package org.pocketcampus.plugin.mainscreen;

import java.util.ArrayList;

import org.pocketcampus.core.plugin.Core;
import org.pocketcampus.core.plugin.ICallback;
import org.pocketcampus.core.plugin.PluginBase;

import android.content.Context;
import android.preference.PreferenceManager;
import android.util.Log;

public class MainscreenNewsProvider implements ICallback {
	private static int nbLoading_ = 0;
	private MainscreenPlugin main_;
	private Context ctx_;
	
	public MainscreenNewsProvider(Context ctx, MainscreenPlugin main) {
		main_ = main;
		ctx_ = ctx;
	}
	
	public void getNews() {
		
		main_.refreshing();
		
		Log.d("MainscreenNewsProvider", "Getting News");
		
        // Feeds to display
		ArrayList<PluginBase> plugins = Core.getInstance().getProvidersOf(IMainscreenNewsProvider.class);
		
		Log.d("MainscreenNewsProvider", "Array size: " + plugins.size());

		nbLoading_ = plugins.size();

        for(PluginBase plug : plugins) {
    		Log.d("MainscreenNewsProvider", "Current Plugin: " + plug.getPluginInfo().getId());
        	
			if(plug != null) {
				
				Log.d("MainscreenNewsProvider", "Plugin not null");
				
				if(PreferenceManager.getDefaultSharedPreferences(ctx_).getBoolean(plug.getPluginInfo().getId().toString(), true)) {
					Log.d("MainscreenNewsProvider","NbLoading++: " + nbLoading_);					
					((IMainscreenNewsProvider)plug).getNews(ctx_, this);
					
				} else {
					nbLoading_--;
				}
			} else {
				nbLoading_--;
			}
		}

	}

	@Override
	public void callback(ArrayList<MainscreenNews> news) {
		main_.addAll(news);
		nbLoading_--;
		Log.d("MainscreenNewsProvider","NbLoading--: " + nbLoading_);					
		
		if(nbLoading_ <= 0) {
			Log.d("MainscreenNewsProvider","refreshed");
			main_.refreshed();
		}
	}

	
}













