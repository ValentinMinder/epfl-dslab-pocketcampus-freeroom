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

/// OFFICIAL TYPES //

struct MyEduCourse {
	1: required i32 iId;
	2: required string iCode;
	3: required string iTitle;
	4: required string iDescription;
	5: required timestamp iCreationTimestamp;
	6: required timestamp iLastUpdateTimestamp;
}

struct MyEduSection {
	1: required i32 iId;
	2: required i32 iCourseId;
	3: required string iTitle;
	4: required string iDescription;
	5: required i32 iSequence;
	6: required timestamp iCreationTimestamp;
	7: required timestamp iLastUpdateTimestamp;
}

struct MyEduModule {
	1: required i32 iId;
	2: required i32 iSectionId;
	3: required i32 iSequence;
	4: required string iTitle;
	5: required bool iVisible;
	6: required string iTextContent;
	7: required string iVideoSourceProvider;
	8: required string iVideoURL;
	9: required timestamp iCreationTimestamp;
	10: required timestamp iLastUpdateTimestamp;
}

struct MyEduMaterial {
	1: required i32 iId;
	2: required i32 iModuleId;
	3: required string iName;
	4: required string iURL;
	5: required timestamp iCreationTimestamp;
	6: required timestamp iLastUpdateTimestamp;
}

struct MyEduModuleRecord {
	1: required i32 iId;
	2: required i32 iModuleId;
	3: optional string iFeedbackText;
	4: required timestamp iFeedbackTimestamp;
	5: required bool iModuleCompleted;
	6: required i32 iRating; //between 1 and 5
	7: required i32 iUserId;
	8: required timestamp iCreationTimestamp;
	9: required timestamp iLastUpdateTimestamp;
}

// REQUESTS //

struct MyEduCourseDetailsRequest {
	1: required string iCourseCode;
}

struct MyEduSectionDetailsRequest {
	1: required string iCourseCode;
	2: required i32 iSectionId;
}

struct MyEduModuleDetailsRequest {
	1: required string iCourseCode;
	2: required i32 iSectionId;
	3: required i32 iModuleId;
}

struct MyEduSubmitFeedbackRequest {
	1: required string iCourseCode;
	2: required i32 iSectionId;
	3: required i32 iModuleId;
	4: required string iText;
	5: required i32 iRating; //between 1 and 5
}

// REPLIES //

struct MyEduSubscribedCoursesListReply {
	1: optional list<MyEduCourse> iSubscribedCourses;
	2: required i32 iStatus;
}

struct MyEduCourseDetailsReply {
	1: optional list<MyEduSection> iMyEduSections;
	2: required i32 iStatus;
}

struct MyEduSectionDetailsReply {
	1: optional list<MyEduModule> iMyEduModules;
	2: required i32 iStatus;
}

struct MyEduModuleDetailsReply {
	1: optional list<MyEduMaterial> iMyEduMaterial;
	2: optional MyEduModuleRecord iMyEduRecord; 
	3: required i32 iStatus;
}

struct MyEduSubmitFeedbackReply {
	1: required bool iSuccess; //true if request succeeded, false otherwise
	2: required string iMessage; //message detailing success/failure
	3: optional MyEduModuleRecord iMyEduModuleRecord; //updated record
}

// SERVICE //

service MyEduService {
	MyEduTequilaToken getTequilaTokenForMyEdu();
	MyEduSession getMyEduSession(1: MyEduTequilaToken iTequilaToken);
	MyEduSubscribedCoursesListReply getSubscribedCoursesList(1: MyEduRequest iMyEduRequest);
	MyEduCourseDetailsReply getCourseDetails(1: MyEduRequest iMyEduRequest, 2: MyEduCourseDetailsRequest iMyEduCourseDetailsRequest);
	MyEduSectionDetailsReply getSectionDetails(1: MyEduRequest iMyEduRequest, 2: MyEduSectionDetailsRequest iMyEduSectionDetailsRequest);
	MyEduModuleDetailsReply getModuleDetails(1: MyEduRequest iMyEduRequest, 2: MyEduModuleDetailsRequest iMyEduModuleDetailsRequest);
	MyEduSubmitFeedbackReply submitFeedback(1: MyEduRequest iMyEduRequest, 2: MyEduSubmitFeedbackRequest iMyEduSubmitFeedbackRequest);
}
