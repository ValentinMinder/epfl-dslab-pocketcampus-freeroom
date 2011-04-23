package org.pocketcampus.plugin.mainscreen;

import java.util.List;

import org.pocketcampus.core.plugin.Icon;

/**
 * This interface allows the plugins to display some news on the mainscreen
 * 
 * 
 * @author Guillaume Ulrich
 *
 */
public interface MainscreenNewsProvider {

	public List<MainscreenNews> getNews();
	
}
