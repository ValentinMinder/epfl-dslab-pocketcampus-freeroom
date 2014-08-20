package org.pocketcampus.plugin.recommendedapps.android.iface;

import java.util.List;
import java.util.Map;

import org.pocketcampus.plugin.recommendedapps.shared.RecommendedApp;
import org.pocketcampus.plugin.recommendedapps.shared.RecommendedAppCategory;
import org.pocketcampus.plugin.recommendedapps.shared.RecommendedAppsResponse;

/**
 * IRecommendedAppsModel
 * 
 * Interface for the Model of the RecommendedApps plugin.
 * It is empty as we have only one Model in this plugin.
 * 
 * @author Amer <amer.chamseddine@epfl.ch>
 *
 */
public interface IRecommendedAppsModel {
	void setRecommendedAppsResponse(RecommendedAppsResponse response);
	List<RecommendedAppCategory> categories();
	Map<Integer, RecommendedApp> apps();
}
