package org.pocketcampus.plugin.recommendedapps.server;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Vector;

import org.pocketcampus.platform.server.database.ConnectionManager;
import org.pocketcampus.platform.server.launcher.PocketCampusServer;
import org.pocketcampus.plugin.recommendedapps.shared.AppStore;
import org.pocketcampus.plugin.recommendedapps.shared.RecommendedApp;
import org.pocketcampus.plugin.recommendedapps.shared.RecommendedAppCategory;
import org.pocketcampus.plugin.recommendedapps.shared.RecommendedAppOSConfiguration;
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
		
		System.out.println("Recommended Apps: " + getRecommendedApps(new RecommendedAppsRequest("EN")));
	}

	private Map<Integer, RecommendedApp> getApps(String languageSuffix) throws Exception {
		Connection connection = connectionManager.getConnection();

		Map<Integer, RecommendedApp> apps = new HashMap<>();

		PreparedStatement getAppsStatement = connection
				.prepareStatement("SELECT * FROM RecommendedApps");

		getAppsStatement.execute();

		ResultSet results = getAppsStatement.getResultSet();

		while (results.next()) {
			RecommendedApp app = new RecommendedApp();
			int appId = results.getInt("AppId");
			app.setAppId(appId);
			String appName = results.getString("AppName");
			app.setAppName(appName);
			String appLogoURL = results.getString("AppLogoURL");
			app.setAppLogoURL(appLogoURL);
			String appDescription = results.getString("AppDescription"
					+ languageSuffix);
			app.setAppDescription(appDescription);
			Map<AppStore, RecommendedAppOSConfiguration> appOSConfigurations = new HashMap<>();
			app.setAppOSConfigurations(appOSConfigurations);
			apps.put(appId, app);
		}

		PreparedStatement getAppOSConfigurationsStatement = connection
				.prepareStatement("SELECT * FROM RecommendedAppsOSConfigurations");
		getAppOSConfigurationsStatement.execute();

		results = getAppOSConfigurationsStatement.getResultSet();

		while (results.next()) {
			int appId = results.getInt("AppId");

			int appStoreId = results.getInt("AppStore");
			AppStore appStore = AppStore.findByValue(appStoreId);

			String appStoreQuery = results.getString("AppStoreQuery");
			String appOpenURLPattern = results.getString("AppOpenURLPattern");

			RecommendedAppOSConfiguration osConfiguration = new RecommendedAppOSConfiguration(
					appStoreQuery, appOpenURLPattern);

			RecommendedApp app = apps.get(appId);
			app.getAppOSConfigurations().put(appStore, osConfiguration);
		}
		return apps;
	}

	private List<RecommendedAppCategory> getCategories(String languageSuffix) throws SQLException {
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
			
			String categoryLogoURL = results.getString("CategoryLogoURL");
			category.setCategoryLogoURL(categoryLogoURL);
			
			String categoryName = results.getString("CategoryName"+languageSuffix);
			category.setCategoryName(categoryName);
			
			String categoryDescription = results.getString("CategoryDescription" + languageSuffix);
			category.setCategoryDescription(categoryDescription);
			
			PreparedStatement getAppsForCategoryStatement = connection
					.prepareStatement("SELECT AppId FROM RecommendedAppsCategoriesToApps WHERE categoryId = ?");
			getAppsForCategoryStatement.setInt(1, categoryId);

			getAppsForCategoryStatement.execute();

			ResultSet appList = getAppsForCategoryStatement.getResultSet();
			while(appList.next()){
				int appId = appList.getInt(1);
				category.addToAppIds(appId);
			}
			
			categories.add(category);
		}
		return categories;
	}

	@Override
	public RecommendedAppsResponse getRecommendedApps(RecommendedAppsRequest request){
		RecommendedAppsResponse response = new RecommendedAppsResponse();
		response.setStatus(RecommendedAppsResponseStatus.OK);
		String languageSuffix = "_" + request.getLanguage().toUpperCase();
		try{
			List<RecommendedAppCategory> categories = getCategories(languageSuffix);
			response.setCategories(categories);
		}catch(Exception e){
			e.printStackTrace();
			System.err.println("Returning ERROR status");
			response.setStatus(RecommendedAppsResponseStatus.ERROR);
			return response;
		}
		
		try{
			Map<Integer, RecommendedApp> apps = getApps(languageSuffix);
			response.setApps(apps);
		}catch(Exception e){
			e.printStackTrace();
			System.err.println("Returning ERROR status");
			response.setStatus(RecommendedAppsResponseStatus.ERROR);
			response.unsetApps();
			return response;
		}
		return response;
	}

}
