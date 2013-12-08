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

struct ScheduleToken {
  1: required string tequilaToken;
  2: required string sessionId;
}

struct ScheduleRequest {
  1: required ScheduleToken token;
  // default is current week
  2: optional timestamp weekStart;
  // default is "fr"
  3: optional string language;
}

enum ScheduleStatusCode {
  // The request was successful
  OK = 200,
  // A network error occurred
  NETWORK_ERROR = 404,
  // The provided session is invalid
  INVALID_SESSION = 407
}

struct ScheduleTokenResponse {
  // Required if the request completed successfully
  1: optional ScheduleToken token;
  2: required ScheduleStatusCode statusCode;
}

struct ScheduleResponse {
  // Required if the request completed successfully
  1: optional list<StudyDay> days;
  2: required ScheduleStatusCode statusCode;
}

service IsAcademiaService {
    ScheduleTokenResponse getScheduleToken();
    ScheduleResponse getSchedule(1: ScheduleRequest req);
}