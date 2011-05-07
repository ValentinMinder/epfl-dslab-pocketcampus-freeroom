package org.pocketcampus.plugin.mainscreen;

import java.util.ArrayList;
import java.util.List;

import org.pocketcampus.plugin.news.NewsPlugin;

import android.content.Context;

public class MainscreenNewsProvider {

	
	public static List<MainscreenNews> getNews(Context ctx) {
		List<MainscreenNews> l = new ArrayList<MainscreenNews>();
		
		/*
		 * In order to test your news on the mainscreen, you can add the following line here:
		 * l.addAll(myIMainscreenNewsProvider.getNews(ctx));
		 * 
		 * This will be modified with the addition of the mainscreen configuration page
		 * 
		 */

		// May work, or not
		l.addAll(new NewsPlugin().getNews(ctx));
				
		return l;
	}
	
}
