namespace java org.pocketcampus.plugin.myedu.shared

struct MyEduTequilaToken {
	1: required string iTequilaKey;
	2: optional string iLoginCookie;
}

struct MyEduSession {
	1: required string iMyEduCookie;
}

struct MyEduRequest {
	1: required MyEduSession iMyEduSession;
	2: optional string iLanguage;
}

service MyEduService {
	MyEduTequilaToken getTequilaTokenForMyEdu();
	MyEduSession getMyEduSession(1: MyEduTequilaToken iTequilaToken);
}
