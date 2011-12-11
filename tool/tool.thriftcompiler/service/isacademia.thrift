namespace java org.pocketcampus.plugin.isacademia.shared

include "authentication.thrift"


enum SeanceType {
	SEANCE_LECTURE; // LIP_COURS;
	SEANCE_EXERCISE; // LIP_EXERCICE;
	SEANCE_LAB; // LIP_LABO;
	SEANCE_PROJECT; // LIP_PROJET;
	SEANCE_PRACTICE; // LIP_TP;
	SEANCE_CONFLICT; // CONFLIT;
}


struct Course {
	1: required string name;
	2: required string code;
	3: required string instructor;
	4: optional string rooms;
	5: optional string dateTime;
	6: required i32 credits;
}

struct Exam {
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

struct Seance {
	1: required string course;
	2: required string instructor;
	3: required SeanceType type;
	4: required i32 weekDay;
	5: required i32 timeStart;
	6: required string room;
}

service IsacademiaService {
	list<Course> getUserCourses(1: authentication.SessionId aSessionId);
	list<Exam> getUserExams(1: authentication.SessionId aSessionId);
	list<Seance> getUserSchedule(1: authentication.SessionId aSessionId);
}
