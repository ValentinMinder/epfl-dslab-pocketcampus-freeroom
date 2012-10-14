namespace java org.pocketcampus.plugin.isacademia.shared

struct TequilaToken {
	1: required string iTequilaKey;
	2: optional string loginCookie;
}


enum SeanceType {
	SEANCE_LECTURE; // LIP_COURS;
	SEANCE_EXERCISE; // LIP_EXERCICE;
	SEANCE_LAB; // LIP_LABO;
	SEANCE_PROJECT; // LIP_PROJET;
	SEANCE_PRACTICE; // LIP_TP;
	SEANCE_CONFLICT; // CONFLIT;
}

struct IsaSession {
	1: required string isaCookie;
}

struct IsaRequest {
	1: required IsaSession isaSession;
	2: required string iLanguage;
}

struct IsaCourse {
	1: required string name;
	2: required string code;
	3: required string instructor;
	4: optional string rooms;
	5: optional string dateTime;
	6: required i32 credits;
}

struct IsaExam {
	1: required string course;
	2: required string code;
	3: required string instructor;
	4: optional string rooms;
	5: optional string dateTime;
	6: required i32 credits;
	7: optional string grade;
	8: required string semester;
	9: required string academicYear;
}

struct IsaSeance {
	1: required string courseName;
	2: required SeanceType seanceType;
	3: required i32 weekDay;
	4: required string startTime;
	5: required string endTime;
	6: required string seanceDate;
	7: required string seanceRoom;
}

struct IsaCoursesListReply {
	1: optional list<IsaCourse> iCourses;
	2: required i32 iStatus;
}

struct IsaExamsListReply {
	1: optional list<IsaExam> iExams;
	2: required i32 iStatus;
}

struct IsaScheduleReply {
	1: optional list<IsaSeance> iSeances;
	2: required i32 iStatus;
}


service IsacademiaService {
	TequilaToken getTequilaTokenForIsa();
	IsaSession getIsaSession(1: TequilaToken iTequilaToken);
	
	IsaCoursesListReply getUserCourses(1: IsaRequest iRequest);
	IsaExamsListReply getUserExams(1: IsaRequest iRequest);
	IsaScheduleReply getUserSchedule(1: IsaRequest iRequest);
}
