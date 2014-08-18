package org.pocketcampus.plugin.recommendedapps.android;


import org.pocketcampus.platform.android.core.IView;
import org.pocketcampus.platform.android.core.PluginModel;
import org.pocketcampus.plugin.recommendedapps.android.iface.IRecommendedAppsModel;
import org.pocketcampus.plugin.recommendedapps.android.iface.IRecommendedAppsView;

import android.content.Context;

/**
 * RecommendedAppsModel - The Model that stores the data of this plugin.
 * 
 * This is the Model associated with the RecommendedApps plugin.
 * It stores the data required for the correct functioning of the plugin.
 * Some data is persistent. e.g.recommendedappsCookie.
 * Other data are temporary.
 * 
 * @author Amer <amer.chamseddine@epfl.ch>
 *
 */
public class RecommendedAppsModel extends PluginModel implements IRecommendedAppsModel {
	
	/**
	 * Reference to the Views that need to be notified when the stored data changes.
	 */
	IRecommendedAppsView mListeners = (IRecommendedAppsView) getListeners();
	
	/**
	 * Constructor with reference to the context.
	 * 
	 * We need the context to be able to instantiate
	 * the SharedPreferences object in order to use
	 * persistent storage.
	 * 
	 * @param context is the Application Context.
	 */
	public RecommendedAppsModel(Context context) {
		
	}
	
	/**
	 * Returns the Type of the Views associated with this plugin.
	 */
	@Override
	protected Class<? extends IView> getViewInterface() {
		return IRecommendedAppsView.class;
	}

	/**
	 * Returns the registered listeners to by notified.
	 */
	public IRecommendedAppsView getListenersToNotify() {
		return mListeners;
	}
	
}
