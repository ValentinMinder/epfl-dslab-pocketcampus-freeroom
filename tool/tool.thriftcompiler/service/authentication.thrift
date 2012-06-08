namespace java org.pocketcampus.plugin.authentication.shared

enum TypeOfService {
	SERVICE_POCKETCAMPUS;
	SERVICE_MOODLE;
	SERVICE_CAMIPRO;
	SERVICE_ISA;
}

struct TequilaKey {
	1: required TypeOfService tos;
	2: required string iTequilaKey;
	3: optional string loginCookie;
	4: optional string iTequilaKeyForPc;
}

struct SessionId {
	1: required TypeOfService tos;
	2: optional string pocketCampusSessionId;
	3: optional string moodleCookie;
	4: optional string camiproCookie;
	5: optional string isaCookie;
}

service AuthenticationService {

	TequilaKey getTequilaKeyForService(1: TypeOfService aService);
	SessionId getSessionIdForService(1: TequilaKey aTequilaKey);
	i32 logOutSession(1: SessionId aSessionId);
	
}