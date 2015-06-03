package org.pocketcampus.plugin.recommendedapps.android.iface;

import org.pocketcampus.plugin.recommendedapps.shared.RecommendedAppsResponse;

/**
 * IRecommendedAppsController
 * 
 * Interface for the Controller of the RecommendedApps plugin.
 * It is empty as we have only one Controller in this plugin.
 * 
 * @author Amer <amer.chamseddine@epfl.ch>
 *
 */
public interface IRecommendedAppsController {
	void updateModelWithRecommendedAppsResponse(RecommendedAppsResponse response);
	void refreshRecommendedApps(IRecommendedAppsView caller);
}
