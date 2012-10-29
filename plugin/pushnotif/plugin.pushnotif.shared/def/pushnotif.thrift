namespace java org.pocketcampus.plugin.pushnotif.shared

include "../../../../platform/sdk/platform.sdk.shared/def/authentication.thrift"

enum PlatformType {
	PC_PLATFORM_ANDROID;
	PC_PLATFORM_IOS;
}

struct PushNotifRegReq {
	1: required authentication.TequilaToken iAuthenticatedToken;
	2: required PlatformType iPlatformType;
	3: required string RegistrationId;
}

struct PushNotifReply {
	1: required i32 iStatus;
}


service PushNotifService {
	authentication.TequilaToken getTequilaTokenForPushNotif();
	PushNotifReply registerPushNotif(1: PushNotifRegReq aPushNotifRequest);
}
