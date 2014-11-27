namespace java org.pocketcampus.plugin.isacademia.shared

/**

From Tim:

Here is the list of possible class types

LIP_TP TP
LIP_ORAL Oral
LIP_COURS Cours
LIP_LABO Labo
LIP_RTP Rapport de TP
LIP_ECRIT Ecrit
LIP_EXERCICE Exercices
LIP_CC Pendant le semestre
LIP_AUTRE_REPRISE  Autre (reprise)
LIP_PROJET Projet
LIP_ECRIT_ORAL Ecrit & Oral
LIP_SYNTHESE Synthèse
LIP_ATELIER Atelier
LIP_MEM Mémoire
LIP_EXP Exposé

**/

typedef i64 timestamp

enum StudyPeriodType { // WARNING do not change the names of these, otherwise the Android app breaks
	LECTURE,
	EXERCISES,
	LAB,
	PROJECT,
  ORAL_EXAM,
  WRITTEN_EXAM
}

struct StudyPeriod {
  1: required string name;
  2: required StudyPeriodType periodType;
  3: required timestamp startTime;
  4: required timestamp endTime;
  5: required list<string> rooms;
}

struct StudyDay {
  1: required timestamp day;
  2: required list<StudyPeriod> periods;
}

struct ScheduleRequest {
  // default is current week
  2: optional timestamp weekStart;
  // default is "fr"
  3: optional string language;
}

enum IsaStatusCode {
  // The request was successful
  OK = 200,
  // A network error occurred
  NETWORK_ERROR = 404,
  // The provided session is invalid
  INVALID_SESSION = 407,
  // An error occured within IS-Academia
  ISA_ERROR = 418
}

struct ScheduleResponse {
  // Required if the request completed successfully
  1: optional list<StudyDay> days;
  2: required IsaStatusCode statusCode;
}

service IsAcademiaService {
    ScheduleResponse getSchedule(1: ScheduleRequest req);
}
