package org.pocketcampus.plugin.recommendedapps.android;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Vector;

import org.pocketcampus.platform.android.core.IView;
import org.pocketcampus.platform.android.core.PluginModel;
import org.pocketcampus.plugin.recommendedapps.android.iface.IRecommendedAppsModel;
import org.pocketcampus.plugin.recommendedapps.android.iface.IRecommendedAppsView;
import org.pocketcampus.plugin.recommendedapps.shared.RecommendedApp;
import org.pocketcampus.plugin.recommendedapps.shared.RecommendedAppCategory;
import org.pocketcampus.plugin.recommendedapps.shared.RecommendedAppsResponse;

import android.content.Context;

/**
 * RecommendedAppsModel - The Model that stores the data of this plugin.
 * 
 * This is the Model associated with the RecommendedApps plugin. It stores the
 * data required for the correct functioning of the plugin. Some data is
 * persistent. e.g.recommendedappsCookie. Other data are temporary.
 * 
 * @author Amer <amer.chamseddine@epfl.ch>
 * 
 */
public class RecommendedAppsModel extends PluginModel implements
		IRecommendedAppsModel {

	private RecommendedAppsResponse response;
	/**
	 * Reference to the Views that need to be notified when the stored data
	 * changes.
	 */
	IRecommendedAppsView mListeners = (IRecommendedAppsView) getListeners();

	/**
	 * Constructor with reference to the context.
	 * 
	 * We need the context to be able to instantiate the SharedPreferences
	 * object in order to use persistent storage.
	 * 
	 * @param context
	 *            is the Application Context.
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

	@Override
	public List<RecommendedAppCategory> categories() {
		if (response != null) {
			return response.getCategories();
		}
		return new Vector<RecommendedAppCategory>();
	}

	@Override
	public Map<Integer, RecommendedApp> apps() {
		if (response != null) {
			return response.getApps();
		}
		return new HashMap<Integer, RecommendedApp>();
	}

	public void setRecommendedAppsResponse(RecommendedAppsResponse response) {
		this.response = response;
		getListenersToNotify().recommendedAppsRefreshed();
	}
}
