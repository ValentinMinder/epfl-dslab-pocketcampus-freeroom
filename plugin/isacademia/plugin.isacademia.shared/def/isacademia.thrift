namespace java org.pocketcampus.plugin.isacademia.shared
namespace csharp org.pocketcampus.plugin.isacademia.shared

typedef i64 timestamp

enum StudyPeriodType {
	LECTURE,
	EXERCISES,
	LAB,
	PROJECT
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
  1: required string tequilaCookie;
  // default is current week
  2: optional timestamp weekStart;
  // default is "fr"
  3: optional string language;
}

struct ScheduleResponse {
  1: required list<StudyDay> days;
}

exception ScheduleException {
  1: required string errorMessage;
}

service IsAcademiaService {
    ScheduleResponse getSchedule(1: ScheduleRequest req) throws (1: ScheduleException e);
}