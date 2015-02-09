package org.pocketcampus.plugin.recommendedapps.android.iface;

import org.pocketcampus.platform.android.core.IView;

/**
 * IRecommendedAppsView
 * 
 * Interface for the Views of the RecommendedApps plugin.
 * 
 * It contains the method that are called by the Model
 * when some data is updated, as well as the methods that
 * are called by the "HttpRequest" classes when some usual
 * behavior occurs.
 * 
 * @author Amer <amer.chamseddine@epfl.ch>
 *
 */
public interface IRecommendedAppsView extends IView {
	void serverDown();
	void networkErrorHappened();
	void recommendedAppsRefreshed();
}
