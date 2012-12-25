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
	4: optional string iDescription;
	5: required timestamp iCreationTimestamp;
	6: required timestamp iLastUpdateTimestamp;
}

struct MyEduSection {
	1: required i32 iId;
	2: required i32 iCourseId;
	3: required string iTitle;
	4: optional string iDescription;
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
	8: required string iVideoID;
	9: optional string iVideoDownloadURL; //might not be available, depending on provider
	10: required timestamp iCreationTimestamp;
	11: required timestamp iLastUpdateTimestamp;
}

enum MyEduMaterialType {
	MATERIAL_TYPE_DOCUMENT;
	MATERIAL_TYPE_WEBSITE;
}

struct MyEduMaterial {
	1: required i32 iId;
	2: required i32 iModuleId;
	3: required string iName;
	4: required MyEduMaterialType iType;
	5: required string iURL
	6: required timestamp iCreationTimestamp;
	7: required timestamp iLastUpdateTimestamp;
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
	1: required MyEduRequest iMyEduRequest;
	2: required string iCourseCode;
}

struct MyEduSectionDetailsRequest {
	1: required MyEduRequest iMyEduRequest;
	2: required string iCourseCode;
	3: required i32 iSectionId;
}

struct MyEduModuleDetailsRequest {
	1: required MyEduRequest iMyEduRequest;
	2: required string iCourseCode;
	3: required i32 iSectionId;
	4: required i32 iModuleId;
}

struct MyEduSubmitFeedbackRequest {
	1: required MyEduRequest iMyEduRequest;
	2: required string iCourseCode;
	3: required i32 iSectionId;
	4: required i32 iModuleId;
	5: required string iText;
	6: required i32 iRating; //between 1 and 5
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
	1: optional list<MyEduMaterial> iMyEduMaterials;
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
	MyEduCourseDetailsReply getCourseDetails(1: MyEduCourseDetailsRequest iMyEduCourseDetailsRequest);
	MyEduSectionDetailsReply getSectionDetails(1: MyEduSectionDetailsRequest iMyEduSectionDetailsRequest);
	MyEduModuleDetailsReply getModuleDetails(1: MyEduModuleDetailsRequest iMyEduModuleDetailsRequest);
	MyEduSubmitFeedbackReply submitFeedback(1: MyEduSubmitFeedbackRequest iMyEduSubmitFeedbackRequest);
}
