package org.pocketcampus.plugin.recommendedapps.server;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Vector;

import org.pocketcampus.platform.server.database.ConnectionManager;
import org.pocketcampus.platform.server.launcher.PocketCampusServer;
import org.pocketcampus.plugin.recommendedapps.shared.AppStore;
import org.pocketcampus.plugin.recommendedapps.shared.RecommendedApp;
import org.pocketcampus.plugin.recommendedapps.shared.RecommendedAppCategory;
import org.pocketcampus.plugin.recommendedapps.shared.RecommendedAppsRequest;
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
 * @author Silviu <silviu@pocketcampus.org>
 * 
 */
public class RecommendedAppsServiceImpl implements RecommendedAppsService.Iface {

	private final ConnectionManager connectionManager;

	public RecommendedAppsServiceImpl() {
		System.out.println("Starting RecommendedApps plugin server ...");
		connectionManager = new ConnectionManager(
				PocketCampusServer.CONFIG.getString("DB_URL"),
				PocketCampusServer.CONFIG.getString("DB_USERNAME"),
				PocketCampusServer.CONFIG.getString("DB_PASSWORD"));
		
	}

	private Map<Integer, RecommendedApp> getApps(String languageSuffix, AppStore appStore, 
			Map<Integer, List<Integer>> appCategs) throws SQLException {
		
		Connection connection = connectionManager.getConnection();
		Map<Integer, RecommendedApp> apps = new HashMap<Integer, RecommendedApp>();

		PreparedStatement stm = connection.prepareStatement("SELECT * FROM RecommendedApps WHERE AppStore = ?");
		stm.setInt(1, appStore.getValue());
		stm.execute();
		ResultSet results = stm.getResultSet();
		while (results.next()) {
			int appId = results.getInt("AppId");
			
			int categId = results.getInt("AppCategory");
			if(!appCategs.containsKey(categId)) {
				appCategs.put(categId, new LinkedList<Integer>());
			}
			appCategs.get(categId).add(appId);

			RecommendedApp app = new RecommendedApp();
			app.setAppStoreQuery(results.getString("AppStoreQuery"));
			app.setAppLogoURL(results.getString("AppLogoURL"));
			app.setAppOpenURLPattern(results.getString("AppOpenURLPattern"));
			app.setAppName(results.getString("AppName" + languageSuffix));
			app.setAppDescription(results.getString("AppDescription" + languageSuffix));

			apps.put(appId, app);
		}
		return apps;
	}

	private List<RecommendedAppCategory> getCategories(String languageSuffix, 
			Map<Integer, List<Integer>> appCategs) throws SQLException {
		
		Connection connection = connectionManager.getConnection();
		List<RecommendedAppCategory> categories = new Vector<RecommendedAppCategory>();

		PreparedStatement stm = connection.prepareStatement("SELECT * FROM RecommendedAppsCategories");
		stm.execute();
		ResultSet results = stm.getResultSet();
		while (results.next()) {
			int categoryId = results.getInt("CategoryId");
			if(appCategs.containsKey(categoryId)) {
				RecommendedAppCategory category = new RecommendedAppCategory();
				category.setCategoryLogoURL(results.getString("CategoryLogoURL"));
				category.setCategoryName(results.getString("CategoryName" + languageSuffix));
				category.setCategoryDescription(results.getString("CategoryDescription" + languageSuffix));
				category.setAppIds(appCategs.get(categoryId));
				categories.add(category);
			}
		}
		return categories;
	}

	private static final List<String> knownLanguages = Arrays.asList("EN", "FR");

	@Override
	public RecommendedAppsResponse getRecommendedApps(RecommendedAppsRequest request) {
		
		System.out.println("Recommended apps for " + request);
		RecommendedAppsResponse response = new RecommendedAppsResponse();
		response.setStatus(RecommendedAppsResponseStatus.OK);

		String language = request.getLanguage().toUpperCase();
		if (!knownLanguages.contains(language)) {
			language = "EN";
		}
		String languageSuffix = "_" + language;

		try {
			Map<Integer, List<Integer>> appCategs = new HashMap<Integer, List<Integer>>();
			
			Map<Integer, RecommendedApp> apps = getApps(languageSuffix, request.getAppStore(), appCategs);
			response.setApps(apps);
			
			List<RecommendedAppCategory> categories = getCategories(languageSuffix, appCategs);
			response.setCategories(categories);
			
			System.out.println("Returning recommended apps for " + request + " " + response);
			return response;
			
		} catch (SQLException e) {
			e.printStackTrace();
			System.err.println("Returning ERROR status");
			response.setStatus(RecommendedAppsResponseStatus.ERROR);
			return response;
		}

	}

}
