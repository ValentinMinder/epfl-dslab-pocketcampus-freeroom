namespace java org.pocketcampus.plugin.freeroom.shared
//namespace csharp org.pocketcampus.plugin.freeroom.shared

enum FRDay {
	Monday; Tuesday; Wednesday; Thursday; Friday; Saturday; Sunday;
}
	
enum FRRoomType{
	AUDITORIUM; EXERCISES; COMPUTER_ROOM; CONFERENCE;
}

struct FRRoom{
	1: required string building;
	2: required string number;
	3: optional FRRoomType type;
	4: optional i32 capacity;
}


struct FRPeriod {
	1: required i64 timeStampStart;
	2: required i64 timeStampEnd;
	
	10: required bool recurrent;
	11: optional i64 firstOccurancy;
	12: optional i32 step;
	13: optional i64 lastOccurancy;
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
}

enum OccupationType {
	UNSPECIFIED; FREE; USED; RESERVED; ISA; OTHERS;
}

// defines if the room is reserved or not, occupation type can give details of the occupation
struct ActualOccupation {
	1: required FRPeriod period;
	2: required bool available;
	3: required OccupationType occupationType;
	// 4: required i32 probableOccupation; // if we want to do CFF-style
}

// the occupancy of a room: periods are usually each hour
// but could be less or more depending of the actual occupancy
// please order the list server-side in natural clock order
// and provide period that are contiguous!
struct Occupancy {
	1: required FRRoom room;
	2: required list<ActualOccupation> occupancy;
}

// check the occupancy request
struct OccupancyRequest {
	1: required list<FRRoom> listFRRoom;
	2: required FRPeriod period;
}

// check the occupancy reply
struct OccupancyReply {
	1: required i32 status;
	2: required string statusComment;
	3: optional list<Occupancy> occupancyOfRooms;
}

struct AutoCompleteRequest {
	1: required string constraint;
}

struct AutoCompleteReply {
	1: required i32 status;
	2: required string statusComment;
	3: optional list<FRRoom> listFRRoom;
}

service FreeRoomService {
	// generic free room service
	FreeRoomReply getFreeRoomFromTime(1: FreeRoomRequest request);
	
	// generic check the occupancy service
	OccupancyReply checkTheOccupancy(1: OccupancyRequest request);
	
	// autocomplete for searching for a room
	AutoCompleteReply autoCompleteRoom(1: AutoCompleteRequest request);
}
