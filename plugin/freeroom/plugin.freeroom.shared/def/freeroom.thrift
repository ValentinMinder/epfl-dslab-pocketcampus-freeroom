namespace java org.pocketcampus.plugin.freeroom.shared
//namespace csharp org.pocketcampus.plugin.freeroom.shared

enum FRDay {
	Monday; Tuesday; Wednesday; Thursday; Friday; Saturday; Sunday;
}
	
enum FRRoomType{
	AUDITORIUM; EXERCISES; COMPUTER_ROOM; CONFERENCE;
}

struct FRCourse {
	1: required string courseID;
	2: required string courseName;
}

struct FRRoom{
	1: required string doorCode;
	2: required string uid;
	3: optional string doorCodeWithoutSpace
	4: optional i32 capacity;
	5: optional string site_label;
	6: optional double surface;
	7: optional string building_name;
	8: optional string zone;
	9: optional string unitlabel;
	10: optional i32 site_id;
	11: optional i32 floor;
	12: optional string unitname;
	13: optional string site_name
	14: optional i32 unitid;
	15: optional string building_label;
	16: optional string cf;
	17: optional string adminuse;
	31: optional string EWAid;
	32: optional FRRoomType type;
}


struct FRPeriod {
	1: required i64 timeStampStart;
	2: required i64 timeStampEnd;
	
	10: required bool recurrent;
	11: optional i64 firstOccurancy;
	12: optional i32 step;
	13: optional i64 lastOccurancy;
}

struct WorkingOccupancy {
	1: required FRPeriod period;
	2: required FRRoom room;
	3: optional FRCourse course;
	40: optional string message;
}

// NOTE ABOUT REPLY STATUS
// it's compliant with standard HTTP status code
// currently used:
// 200 - OK - when the result is correctly given
// 400 - BAD REQUEST - when the server couldn't answer due to malformed query from the client
// 500 - INTERNAL ERROR - when the server couldn't answer due to internal implementation error

// standard response for a free room
struct FreeRoomReply {
	1: required i32 status;
	2: required string statusComment;
	3: optional set<FRRoom> rooms;
}

// standard request for a free room
struct FreeRoomRequest {
	1: required FRPeriod period;
	2: optional set<FRRoom> forbiddenRooms;
}

// defines if the room is reserved or not, occupation type can give details of the occupation
struct ActualOccupation {
	1: required FRPeriod period;
	2: required bool available;
	//TO DELETE
	3: optional i32 probableOccupation; // if we want to do CFF-style
	4: optional double ratioOccupation;
}

// the occupancy of a room: periods are usually each hour
// but could be less or more depending of the actual occupancy
// please order the list server-side in natural clock order
// and provide period that are contiguous!
struct Occupancy {
	1: required FRRoom room;
	2: required list<ActualOccupation> occupancy;
	3: required bool isAtLeastOccupiedOnce;
	4: required bool isAtLeastFreeOnce;
	5: optional double ratioWorstCaseProbableOccupancy;
}

// check the occupancy request
struct OccupancyRequest {
	1: required list<string> uids;
	2: required FRPeriod period;
}

struct FRRequest {
	1: required FRPeriod period;
	2: required bool onlyFreeRooms;
	//if null, it means every rooms
	3: required list<string> uidList;
}

struct FRReply {
	1: required i32 status;
	2: required string statusComment;
	//map from building to list of occupancies in the building
	3: optional map<string, list<Occupancy>> occupancyOfRooms;
}

// check the occupancy reply
struct OccupancyReply {
	1: required i32 status;
	2: required string statusComment;
	3: optional list<Occupancy> occupancyOfRooms;
}

//forbiddenRooms represents the rooms that shouldn't be replied 
struct AutoCompleteRequest {
	1: required string constraint;
	2: optional set<string> forbiddenRoomsUID;
}

struct AutoCompleteReply {
	1: required i32 status;
	2: required string statusComment;
	//TO DELETE
	3: optional list<FRRoom> listFRRoom;
	4: optional map<string, list<FRRoom>> listRoom;
}

struct ImWorkingRequest {
	1: required WorkingOccupancy work;
}

struct ImWorkingReply {
	1: required i32 status;
	2: required string statusComment;
}

struct WhoIsWorkingRequest {
	1: required FRPeriod period;
	2: optional FRCourse course;
	3: optional string constraint;
}

struct WhoIsWorkingReply {
	1: required i32 status;
	2: required string statusComment;
	3: optional list<WorkingOccupancy> theyAreWorking;
}


service FreeRoomService {
	// generic free room service
	FreeRoomReply getFreeRoomFromTime(1: FreeRoomRequest request);
	
	// generic check the occupancy service
	OccupancyReply checkTheOccupancy(1: OccupancyRequest request);
	
	// new feature, (merge of the two above)
	FRReply getOccupancy(1: FRRequest request);
	
	// autocomplete for searching for a room
	AutoCompleteReply autoCompleteRoom(1: AutoCompleteRequest request);
	
	// indicate that i'm going to work there
	ImWorkingReply indicateImWorking(1: ImWorkingRequest request);
	
	// who is working at this time, this subject?
	WhoIsWorkingReply whoIsWorking(1: WhoIsWorkingRequest request);
}
