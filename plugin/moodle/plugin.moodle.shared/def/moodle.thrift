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




// NEW STUFF - USE THIS!

struct MoodleFile2 {
    // File name
    1: required string name;
    // File extension
    2: required string extension;
    // Download URL (use the PocketCampus moodle proxy for it)
    3: required string url;
    // Moodle file icon URL, contains a {size} token which must be replaced by a size.
    //   32, 64, 128, and other common sizes are supported for (almost) all files;
    //   see https://github.com/moodle/moodle/tree/master/pix/f for detailed info.
    //   You can also replace('-{size}', '') to get the original image, but it's very small.
    // Not provided for files inside a folder, unfortunately.
    4: optional string icon;
}

struct MoodleFolder2 {
    // Folder name
    1: required string name;
    // Files
    2: required list<MoodleFile2> files;
}

struct MoodleUrl2 {
    // Name
    1: required string name;
    // URL
    2: required string url;
}

// Union struct. Exactly 1 of the 3 fields is set.
struct MoodleResource2 {
    1: optional MoodleFile2 file;
    2: optional MoodleFolder2 folder;
    3: optional MoodleUrl2 url;
}

struct MoodleCourseSection2 {
    // Resources (folders, files & URLs)
    1: required list<MoodleResource2> resources;
    // Title, if it's not a week section
    2: optional string title;
    // Start date of the section, if it's a week section (Java timestamp)
    3: optional i64 startDate;
    // End date of the section, if it's a week section (Java timestamp)
    4: optional i64 endDate;
    // Details, as HTML, if any
    5: optional string details;

    // Invariant: (text is set) or (startDate and endDate are set) but not both
}

struct MoodleCourse2 {
    // ID, used when requesting the sections
    1: required i32 courseId;
    // Name
    2: required string name;
}

enum MoodleStatusCode2 {
    // Success
    OK = 200,
    // Authentication error, authenticate to the authentication plugin again
    AUTHENTICATION_ERROR = 403,
    // Error while reaching Moodle, try again later
    NETWORK_ERROR = 404
}

struct MoodleCoursesRequest2 {
    // Unused for now
    1: required string language;
}

struct MoodleCoursesResponse2 {
    // Status code
    1: required MoodleStatusCode2 statusCode;
    // Courses, empty if statusCode != OK
    2: required list<MoodleCourse2> courses;
}

struct MoodleCourseSectionsRequest2 {
    // Unused for now
    1: required string language;
    // The requested course ID
    2: required i32 courseId;
}

struct MoodleCourseSectionsResponse2 {
    // Status code
    1: required MoodleStatusCode2 statusCode;
    // Course sections (may be empty)
    2: required list<MoodleCourseSection2> sections;
}

struct MoodlePrintFileRequest2 {
    // Same as the URL used to download the file via the file proxy
    1: required string fileUrl;
}

struct MoodlePrintFileResponse2 {
    // Status code
    1: required MoodleStatusCode2 statusCode;
    // In case of success, will contain the print job id (as assigned by the CloudPrint plugin)
    2: optional i64 printJobId;
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
    
    
    // NEW STUFF - USE THIS!
    
    // Get all courses
    MoodleCoursesResponse2 getCourses( 1: MoodleCoursesRequest2 request );
    // Get course sections
    MoodleCourseSectionsResponse2 getSections( 1: MoodleCourseSectionsRequest2 request );
    // Print Moodle file
    MoodlePrintFileResponse2 printFile( 1: MoodlePrintFileRequest2 request );
}
