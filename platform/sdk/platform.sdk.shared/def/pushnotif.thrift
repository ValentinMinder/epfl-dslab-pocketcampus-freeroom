namespace java org.pocketcampus.platform.sdk.shared.pushnotif

struct PushNotifRequest {
	1: required string pluginName;
	2: required list<string> gasparList;
	3: required string message;
}

struct PushNotifResponse {
	1: required list<string> failedList;
}
