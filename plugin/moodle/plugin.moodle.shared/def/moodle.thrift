namespace java org.pocketcampus.plugin.moodle.shared

struct TequilaToken {
	1: required string iTequilaKey;
	2: optional string loginCookie;
}

struct SessionId {
	1: required i32 tos;
	3: optional string moodleCookie;
}


struct MoodleRequest {
	1: required SessionId iSessionId;
	2: required string iLanguage;
	3: optional i32 iCourseId;
}

struct MoodleSession {
	1: required string moodleCookie;
}

struct MoodleCourse {
	1: required i32 iId;
	2: required string iTitle;
}

struct CoursesListReply {
	1: optional list<MoodleCourse> iCourses;
	2: required i32 iStatus;
}


struct MoodleAssignment {
	1: required i32 iId;
	2: required string iTitle;
	3: required string iDesc;
	4: required MoodleCourse iCourse;
	5: optional i64 iPostingDate;
	6: required i64 iDueDate;
	7: optional string iGrade;
}

struct MoodleUserEvent {
	1: required i32 iId;
	2: required string iTitle;
	3: required string iDesc;
	4: required i64 iStartDate;
	5: optional i64 iEndDate;
}

enum MoodleEventType {
	MOODLE_EVENT_UNKNOWN;
	MOODLE_EVENT_ASSIGNMENT;
	MOODLE_EVENT_USEREVENT;
}

struct MoodleEvent {
	1: required i32 iId;
	2: required string iTitle;
	3: required i64 iDate;
	4: required MoodleEventType iType;
	5: optional MoodleAssignment iAssignment;
	6: optional MoodleUserEvent iUserEvent;
}

struct EventsListReply {
	1: optional list<MoodleEvent> iEvents;
	2: required i32 iStatus;
}


struct MoodleResource {
	1: required string iName;
	2: required string iUrl;
}

struct MoodleSection {
	1: required list<MoodleResource> iResources;
	2: required string iText;
	3: optional i64 iStartDate;
	4: optional i64 iEndDate;
	5: optional bool iCurrent;
}

struct SectionsListReply {
	1: optional list<MoodleSection> iSections;
	2: required i32 iStatus;
}


service MoodleService {
	TequilaToken getTequilaTokenForMoodle();
	MoodleSession getMoodleSession(1: TequilaToken iTequilaToken);
	CoursesListReply getCoursesList(1: MoodleRequest iRequest);
	EventsListReply getEventsList(1: MoodleRequest iRequest);
	SectionsListReply getCourseSections(1: MoodleRequest iRequest);
}
