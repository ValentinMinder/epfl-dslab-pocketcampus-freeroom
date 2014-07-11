namespace java org.pocketcampus.plugin.moodle.shared

const string MOODLE_RAW_ACTION_KEY = "action";
const string MOODLE_RAW_ACTION_DOWNLOAD_FILE = "download_file";
const string MOODLE_RAW_FILE_PATH = "file_path";


// EXTREMELY OLD STUFF, DO NOT USE

struct TequilaToken {
	1: required string iTequilaKey;
	2: optional string loginCookie;
}

struct SessionId {
	1: required i32 tos;
	3: optional string moodleCookie;
}

struct MoodleSession {
	1: required string moodleCookie;
}

struct MoodleRequest {
	1: required SessionId iSessionId;
	2: required string iLanguage;
	3: optional i32 iCourseId;
}

// OLD STUFF, DO NOT USE

struct MoodleCourse {
	1: required i32 iId;
	2: required string iTitle;
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

struct CoursesListReply {
	1: optional list<MoodleCourse> iCourses;
	2: required i32 iStatus;
}

struct SectionsListReply {
	1: optional list<MoodleSection> iSections;
	2: required i32 iStatus;
}


service MoodleService {
    // EXTREMELY OLD STUFF - DO NOT USE
	TequilaToken getTequilaTokenForMoodle();
	MoodleSession getMoodleSession(1: TequilaToken iTequilaToken);
	CoursesListReply getCoursesList(1: MoodleRequest iRequest);
	SectionsListReply getCourseSections(1: MoodleRequest iRequest);

    // OLD STUFF - DO NOT USE
	CoursesListReply getCoursesListAPI(1: string dummy);
	SectionsListReply getCourseSectionsAPI(1: string courseId);
}
