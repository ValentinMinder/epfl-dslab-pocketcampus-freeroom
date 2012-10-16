namespace java org.pocketcampus.plugin.pushnotif.shared

struct TequilaToken {
	1: required string iTequilaKey;
	2: optional string loginCookie;
}


enum PlatformType {
	PC_PLATFORM_ANDROID;
	PC_PLATFORM_IOS;
}

struct PushNotifRequest {
	1: optional TequilaToken iAuthenticatedToken;
	2: required PlatformType iPlatformType;
	3: optional string iAndroidRegistrationId;
}

struct PushNotifReply {
	1: required i32 iStatus;
}


service PushNotifService {
	TequilaToken getTequilaTokenForPushNotif();
	PushNotifReply registerPushNotif(1: PushNotifRequest aPushNotifRequest);
	PushNotifReply unregisterPushNotif(1: PushNotifRequest aPushNotifRequest);
}
