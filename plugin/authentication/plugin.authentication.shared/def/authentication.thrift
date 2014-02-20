namespace java org.pocketcampus.plugin.authentication.shared
namespace csharp org.pocketcampus.plugin.authentication.shared

enum AuthStatusCode {
  // The request was successful
  OK = 200,
  // A network error occurred
  NETWORK_ERROR = 404,
  // The provided session is invalid
  INVALID_SESSION = 407
}

struct AuthTokenResponse {
  1: optional string tequilaToken;
  2: required AuthStatusCode statusCode;
}

struct AuthSessionResponse {
  1: optional string sessionId;
  2: required AuthStatusCode statusCode;
}

service AuthenticationService {
    AuthTokenResponse getAuthTequilaToken();
    AuthSessionResponse getAuthSessionId(1: string tequilaToken);
}
