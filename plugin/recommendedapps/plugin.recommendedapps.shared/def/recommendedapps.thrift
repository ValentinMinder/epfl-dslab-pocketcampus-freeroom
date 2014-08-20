namespace java org.pocketcampus.plugin.recommendedapps.shared
enum AppStore{
	iOS = 1, Android = 2, WindowsPhone8 = 3;
}

struct RecommendedApp{
	1: string appStoreQuery;
	2: optional string appName;
	3: optional string appDescription;
	4: optional string appOpenURLPattern;
	5: optional string appLogoURL;
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

struct RecommendedAppsRequest{
	1: string language;
	2: AppStore appStore;
}

service RecommendedAppsService{
	RecommendedAppsResponse getRecommendedApps(1: RecommendedAppsRequest request);
}
