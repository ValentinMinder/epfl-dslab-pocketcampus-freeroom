package org.pocketcampus.plugin.mainscreen;

import java.util.ArrayList;
import java.util.List;

public class MainscreenNewsProvider {

	
	public static List<MainscreenNews> getNews() {
		List<MainscreenNews> l = new ArrayList<MainscreenNews>();
		
		/*
		 * In order to test your news on the mainscreen, you can add the following line here:
		 * l.addAll(myIMainscreenNewsProvider.getNews());
		 * 
		 * This will be modified with the addition of the mainscreen configuration page
		 * 
		 */
				
		return l;
	}
	
}
