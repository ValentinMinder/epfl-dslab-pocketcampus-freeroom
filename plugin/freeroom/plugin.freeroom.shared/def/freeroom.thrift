namespace java org.pocketcampus.plugin.freeroom.shared
//namespace csharp org.pocketcampus.plugin.freeroom.shared

enum FRRoomType{
	AUDITORIUM; EXERCISES; COMPUTER_ROOM; CONFERENCE;
}

struct FRRoom{
	1: required string building;
	2: required string number;
	3: optional FRRoomType type;
	4: optional i32 capacity;
}

// represent a timestamp as defined in UNIX standard, the number of seconds form JAN 1 1970
struct FRTimeStamp {
	1: required i32 timeSeconds;
}

struct FRPeriod {
	1: required FRTimeStamp timeStampStart;
	2: required FRTimeStamp timeStampEnd;
	
	10: required bool recurrent;
	11: optional FRTimeStamp firstOccurancy;
	12: optional i32 step;
	13: optional FRTimeStamp lastOccurancy;
}

// standard response for a free room
struct FRFreeRoomResponseFromTime {
	1: required set<FRRoom> rooms;
}

// standard request for a free room
struct FRFreeRoomRequestFromTime {
	1: required FRPeriod period;
}

service FreeRoomService {
	// generic free room service
	FRFreeRoomResponseFromTime getFreeRoomFromTime(1: FRFreeRoomRequestFromTime request);
}
