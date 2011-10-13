namespace java org.pocketcampus.plugin.authentication.shared

include "../include/common.thrift"

typedef i32 int

struct SessionToken {
	1: required string id;
}

service AuthenticationService {
	SessionToken login(1: string username, 2: string password);
	bool authenticate(1: SessionToken token);
	bool logout(1: SessionToken token);
}