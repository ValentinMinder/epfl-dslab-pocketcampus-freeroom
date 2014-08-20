package org.pocketcampus.plugin.recommendedapps.server;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
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

		System.out.println("[TEST] Recommended Apps: "
				+ getRecommendedApps(new RecommendedAppsRequest("EN",
						AppStore.iOS)));
	}

	private Map<Integer, RecommendedApp> getApps(String languageSuffix,
			AppStore appStore) throws Exception {
		Connection connection = connectionManager.getConnection();

		Map<Integer, RecommendedApp> apps = new HashMap<>();

		PreparedStatement getAppOSConfigurationsStatement = connection
				.prepareStatement("SELECT * FROM RecommendedAppsOSConfigurations WHERE AppStore = "
						+ appStore.getValue());
		getAppOSConfigurationsStatement.execute();

		ResultSet results = getAppOSConfigurationsStatement.getResultSet();

		while (results.next()) {
			int appId = results.getInt("AppId");

			String appStoreQuery = results.getString("AppStoreQuery");
			String appOpenURLPattern = results.getString("AppOpenURLPattern");
			String appLogoURL = results.getString("AppLogoURL");

			RecommendedApp app = new RecommendedApp();
			app.setAppId(appId);
			app.setAppStoreQuery(appStoreQuery);
			if (appStore != AppStore.iOS) {
				app.setAppLogoURL(appLogoURL);
			}
			app.setAppOpenURLPattern(appOpenURLPattern);

			apps.put(appId, app);
		}

		PreparedStatement getAppsStatement = connection
				.prepareStatement("SELECT * FROM RecommendedApps");

		getAppsStatement.execute();

		results = getAppsStatement.getResultSet();

		while (results.next()) {
			int appId = results.getInt("AppId");
			if (!apps.containsKey(appId)) {
				continue;
			}
			if (appStore != AppStore.iOS) {
				RecommendedApp app = apps.get(appId);
				String appName = results.getString("AppName");
				app.setAppName(appName);
				String appDescription = results.getString("AppDescription"
						+ languageSuffix);
				app.setAppDescription(appDescription);
			}

		}
		return apps;
	}

	private List<RecommendedAppCategory> getCategories(String languageSuffix,
			Collection<Integer> appsForPlatform) throws SQLException {
		Connection connection = connectionManager.getConnection();

		List<RecommendedAppCategory> categories = new Vector<>();

		PreparedStatement getCategoriesStatement = connection
				.prepareStatement("SELECT * FROM RecommendedAppsCategories");

		getCategoriesStatement.execute();

		ResultSet results = getCategoriesStatement.getResultSet();

		while (results.next()) {
			RecommendedAppCategory category = new RecommendedAppCategory();

			int categoryId = results.getInt("CategoryId");
			category.setCategoryId(categoryId);

			// String categoryLogoURL = results.getString("CategoryLogoURL");
			// category.setCategoryLogoURL(categoryLogoURL);

			String categoryName = results.getString("CategoryName"
					+ languageSuffix);
			category.setCategoryName(categoryName);

			String categoryDescription = results
					.getString("CategoryDescription" + languageSuffix);
			category.setCategoryDescription(categoryDescription);

			PreparedStatement getAppsForCategoryStatement = connection
					.prepareStatement("SELECT AppId FROM RecommendedAppsCategoriesToApps WHERE categoryId = ?");
			getAppsForCategoryStatement.setInt(1, categoryId);

			getAppsForCategoryStatement.execute();

			ResultSet appList = getAppsForCategoryStatement.getResultSet();
			while (appList.next()) {
				int appId = appList.getInt(1);
				if (appsForPlatform.contains(appId)) {
					category.addToAppIds(appId);
				}
			}

			if (category.isSetAppIds() && (category.getAppIds().size() > 0)) {
				categories.add(category);
			}
		}
		return categories;
	}

	private static final List<String> knownLanguages = Arrays.asList("EN");

	@Override
	public RecommendedAppsResponse getRecommendedApps(
			RecommendedAppsRequest request) {
		System.out.println("Recommended apps for " + request);
		RecommendedAppsResponse response = new RecommendedAppsResponse();
		response.setStatus(RecommendedAppsResponseStatus.OK);

		String language = request.getLanguage().toUpperCase();
		if (!knownLanguages.contains(language)) {
			language = "EN";
		}
		String languageSuffix = "_" + language;

		try {
			Map<Integer, RecommendedApp> apps = getApps(languageSuffix,
					request.getAppStore());
			response.setApps(apps);
		} catch (Exception e) {
			e.printStackTrace();
			System.err.println("Returning ERROR status");
			response.setStatus(RecommendedAppsResponseStatus.ERROR);
			return response;
		}

		try {
			List<RecommendedAppCategory> categories = getCategories(
					languageSuffix, response.getApps().keySet());
			response.setCategories(categories);
		} catch (Exception e) {
			e.printStackTrace();
			System.err.println("Returning ERROR status");
			response.setStatus(RecommendedAppsResponseStatus.ERROR);
			response.unsetApps();
			return response;
		}

		System.out.println("Returning recommended apps for " + request + " "
				+ response);
		return response;
	}

}
