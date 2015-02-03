namespace java org.pocketcampus.plugin.authentication.shared

const list<string> OAUTH2_SCOPES = ["Tequila.profile", "Moodle.read", "ISA.read"];

enum AuthStatusCode {
  // The request was successful
  OK = 200,
  // A network error occurred
  NETWORK_ERROR = 404,
  // The provided session is invalid
  INVALID_SESSION = 407,
  // Internal server error
  SERVER_ERROR = 500
}

struct AuthTokenResponse {
  1: optional string tequilaToken;
  2: required AuthStatusCode statusCode;
}

struct AuthSessionResponse {
  1: optional string sessionId;
  2: required AuthStatusCode statusCode;
}

struct AuthSessionRequest {
  1: required string tequilaToken;
  2: optional bool rememberMe;
}

struct LogoutResponse {
  1: required AuthStatusCode statusCode;
  2: optional i32 deletedSessionsCount;
}

struct LogoutRequest {
  1: required string sessionId;
}

struct UserAttributesResponse {
  1: optional list<string> userAttributes;
  2: required AuthStatusCode statusCode;
}

struct UserAttributesRequest {
  1: required string sessionId;
  2: required list<string> attributeNames;
}

service AuthenticationService {
	AuthSessionResponse getOAuth2TokensFromCode(1: AuthSessionRequest req);
	
	AuthTokenResponse getAuthTequilaToken();
	AuthSessionResponse getAuthSession(1: AuthSessionRequest req);
	LogoutResponse destroyAllUserSessions(1: LogoutRequest req);
	UserAttributesResponse getUserAttributes(1: UserAttributesRequest req);
	AuthSessionResponse getAuthSessionId(1: string tequilaToken); // deprecated
}
