package org.pocketcampus.plugin.recommendedapps.server;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import org.apache.thrift.TException;
import org.pocketcampus.plugin.recommendedapps.shared.AppStore;
import org.pocketcampus.plugin.recommendedapps.shared.RecommendedApp;
import org.pocketcampus.plugin.recommendedapps.shared.RecommendedAppCategory;
import org.pocketcampus.plugin.recommendedapps.shared.RecommendedAppOSConfiguration;
import org.pocketcampus.plugin.recommendedapps.shared.RecommendedAppsResponse;
import org.pocketcampus.plugin.recommendedapps.shared.RecommendedAppsResponseStatus;
import org.pocketcampus.plugin.recommendedapps.shared.RecommendedAppsService;

/**
 * RecommendedAppsServiceImpl
 * 
 * The implementation of the server side of the RecommendedApps Plugin.
 * 
 * It fetches the user's RecommendedApps data from the RecommendedApps servers.
 * 
 * @author Amer <amer.chamseddine@epfl.ch>
 * 
 */
public class RecommendedAppsServiceImpl implements RecommendedAppsService.Iface {

	public RecommendedAppsServiceImpl() {
		System.out.println("Starting RecommendedApps plugin server ...");
	}

	@Override
	public RecommendedAppsResponse getRecommendedApps() throws TException {

		RecommendedAppCategory category = new RecommendedAppCategory(
				0,
				"Social",
				"http://www.duckdiverllc.com/wp-content/uploads/2013/04/Social-Media-Icons.gif",
				"Time wasting", Arrays.asList(1, 2, 3, 4, 5));

		Map<AppStore, RecommendedAppOSConfiguration> appOSConfigurations = new HashMap<>();

		RecommendedAppOSConfiguration iosConfiguration = new RecommendedAppOSConfiguration(
				"284882215", "lalalalala");
		appOSConfigurations.put(AppStore.iOS, iosConfiguration);

		RecommendedApp app = new RecommendedApp(1, "Facebook",
				"https://www.facebook.com/images/fb_icon_325x325.png",
				"What better way to waste time stalking people?",
				appOSConfigurations);

		Map<Integer, RecommendedApp> apps = new HashMap<>();
		apps.put(1, app);
		apps.put(2, app);
		apps.put(3, app);
		apps.put(4, app);
		apps.put(5, app);

		RecommendedAppsResponse response = new RecommendedAppsResponse(
				RecommendedAppsResponseStatus.OK, Arrays.asList(category,
						category), apps);
		
		System.out.println("Returning "+response);
		return response;
	}

}
