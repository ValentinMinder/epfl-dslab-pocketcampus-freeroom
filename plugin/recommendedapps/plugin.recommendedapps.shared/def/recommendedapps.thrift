namespace java org.pocketcampus.plugin.recommendedapps.shared

struct RecommendedAppOSConfiguration{
	1: string appStoreURL;
	2: string appOpenURLPattern;
}

enum AppStore{
	iOS, Android, WindowsPhone8;
}

struct RecommendedApp{
	1: i32 appId;
	2: string appName;
	3: string appLogoURL;
	4: string appDescription;
	5: map<AppStore, RecommendedAppOSConfiguration> appOSConfigurations;
}

struct RecommendedAppCategory{
	1: i32 categoryId;
	2: string categoryName;
	3: string categoryLogoURL;
	4: string categoryDescription;
	5: list<i32> appIds;
}

enum RecommendedAppsResponseStatus{
	OK = 200, ERROR = 500
}

struct RecommendedAppsResponse{
	1: RecommendedAppsResponseStatus status;
	2: list<RecommendedAppCategory> categories;
	3: map<i32, RecommendedApp> apps;
}

service RecommendedAppsService{
	RecommendedAppsResponse getRecommendedApps();
}
