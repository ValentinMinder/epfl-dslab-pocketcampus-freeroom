namespace java org.pocketcampus.plugin.recommendedapps.shared
enum AppStore{
	iOS = 1, Android = 2, WindowsPhone8 = 3;
}

struct RecommendedApp{
	1: required string appStoreQuery;
	2: optional string appName;
	3: optional string appDescription;
	4: optional string appOpenURLPattern;
	5: optional string appLogoURL;
}

struct RecommendedAppCategory{
	1: required string categoryName;
	2: optional string categoryLogoURL;
	3: required string categoryDescription;
	4: required list<i32> appIds;
}

enum RecommendedAppsResponseStatus{
	OK = 200, ERROR = 500
}

struct RecommendedAppsResponse{
	1: required RecommendedAppsResponseStatus status;
	2: required list<RecommendedAppCategory> categories;
	3: required map<i32, RecommendedApp> apps;
}

struct RecommendedAppsRequest{
	1: required string language;
	2: required AppStore appStore;
}

service RecommendedAppsService{
	RecommendedAppsResponse getRecommendedApps(1: RecommendedAppsRequest request);
}
