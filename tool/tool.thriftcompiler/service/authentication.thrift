namespace java org.pocketcampus.plugin.authentication.shared

enum TypeOfService {
	SERVICE_POCKETCAMPUS;
	SERVICE_MOODLE;
	SERVICE_CAMIPRO;
}

struct TequilaKey {
	1: required TypeOfService tos;
	2: required string iTequilaKey;
}

struct SessionId {
	1: required TypeOfService tos;
	2: optional string pocketCampusSessionId;
	3: optional string moodleCookie;
	4: optional string camiproCookie;
}

service AuthenticationService {

	TequilaKey getTequilaKeyForService(1: TypeOfService aService);
	SessionId getSessionIdForService(1: TequilaKey aTequilaKey);
	
}