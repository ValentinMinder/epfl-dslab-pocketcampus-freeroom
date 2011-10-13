namespace java org.pocketcampus.plugin.authentication.shared

struct SessionToken {
	1: required string token;
}

exception LoginException {
	1: string message;
}

service AuthenticationService {
	SessionToken login(1: string username, 2: string password) throws (1: LoginException le);
	bool authenticate(1: SessionToken token);
	bool logout(1: SessionToken token);
}