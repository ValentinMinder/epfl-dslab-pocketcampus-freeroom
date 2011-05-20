package org.pocketcampus.plugin.mainscreen;

import org.pocketcampus.core.plugin.ICallback;
import org.pocketcampus.core.provider.IProvider;

import android.content.Context;


/**
 * This interface allows the plugins to display some news on the mainscreen
 * 
 * 
 * @author Guillaume Ulrich
 *
 */
public interface IMainscreenNewsProvider extends IProvider {

	public void getNews(Context ctx, ICallback callback);
	
}
