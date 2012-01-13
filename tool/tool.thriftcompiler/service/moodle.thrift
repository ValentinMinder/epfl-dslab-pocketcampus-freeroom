namespace java org.pocketcampus.plugin.moodle.shared

include "authentication.thrift"


struct MoodleRequest {
	1: required authentication.SessionId iSessionId;
	2: required string iLanguage;
}

struct MoodleCourse {
	1: required string iTitle;
	2: required string iInstructor;
}

struct CoursesListReply {
	1: optional list<MoodleCourse> iCourses;
	2: required i32 iStatus;
}


service MoodleService {
	CoursesListReply getCoursesList(1: MoodleRequest iRequest);
}
