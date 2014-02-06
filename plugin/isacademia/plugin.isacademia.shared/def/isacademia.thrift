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
  1: required string sessionId;
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
  INVALID_SESSION = 407
}

struct IsaTokenResponse {
  // Required if the request completed successfully
  1: optional string tequilaToken;
  2: required IsaStatusCode statusCode;
}

struct IsaSessionResponse {
  // Required if the request completed successfully
  1: optional string sessionId;
  2: required IsaStatusCode statusCode;
}

struct ScheduleResponse {
  // Required if the request completed successfully
  1: optional list<StudyDay> days;
  2: required IsaStatusCode statusCode;
}

service IsAcademiaService {
    IsaTokenResponse getIsaTequilaToken();
    IsaSessionResponse getIsaSessionId(1: string tequilaToken);
    ScheduleResponse getSchedule(1: ScheduleRequest req);
}
