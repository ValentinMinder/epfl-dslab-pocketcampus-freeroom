namespace java org.pocketcampus.plugin.myedu.shared

//To represent a date (milisec from January 1, 1970, 00:00:00 GMT)
typedef i64 timestamp

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

struct MyEduCourse {
	1: required i32 iId;
	2: required string iCode;
	3: required string iTitle;
	4: required string iDescription;
	5: optional timestamp iCreationTimestamp;
	6: optional timestamp iLastUpdateTimestamp;
}

// REPLIES //

struct SubscribedCoursesListReply {
	1: optional list<MyEduCourse> iSubscribedCourses;
	2: required i32 iStatus;
}

// SERVICE //

service MyEduService {
	MyEduTequilaToken getTequilaTokenForMyEdu();
	MyEduSession getMyEduSession(1: MyEduTequilaToken iTequilaToken);
	SubscribedCoursesListReply getSubscribedCoursesList(1: MyEduRequest iMyEduRequest);
}
