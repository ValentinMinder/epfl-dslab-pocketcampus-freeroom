namespace java org.pocketcampus.plugin.freeroom.shared
//namespace csharp org.pocketcampus.plugin.freeroom.shared

enum FRDay{
	MONDAY; TUESDAY; WEDNESDAY; THURSDAY; FRIDAY; SATURDAY; SUNDAY;
}

enum FRRoomType{
	AUDITORIUM; EXERCISES; COMPUTER_ROOM; CONFERENCE;
}

struct FRPeriodOfTime{
	1: required FRDay day;
	2: required i32 startHour;
	3: required i32 endHour;
}

struct FRRoom{
	1: required string building;
	2: required string number;
	3: optional FRRoomType type;
	4: optional i32 capacity;
}

service FreeRoomService {
	set<FRRoom> getFreeRoomsFromTime(1: FRPeriodOfTime period);
}
