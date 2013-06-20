namespace java org.pocketcampus.platform.sdk.shared.pushnotif

struct PushNotifMapRequest {
	1: required string pluginName;
	2: required string userId;
	3: required string deviceOs;
	4: required string pushToken;
}

struct PushNotifSendRequest {
	1: required string pluginName;
	2: required list<string> userIds;
	3: required map<string, string> messageMap;
}

