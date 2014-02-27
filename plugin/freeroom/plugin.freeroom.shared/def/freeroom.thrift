namespace java org.pocketcampus.plugin.freeroom.shared
//namespace csharp org.pocketcampus.plugin.freeroom.shared

enum Day{
	MONDAY; TUESDAY; WEDNESDAY; THURSDAY; FRIDAY; SATURDAY; SUNDAY;
}

enum RoomType{
	AUDITORIUM; EXERCISES; COMPUTER_ROOM; CONFERENCE;
}

struct PeriodOfTime{
	1: required Day day;
	2: required i32 startHour;
	3: required i32 endHour;
}

struct Room{
	1: required string building;
	2: required string number;
	3: optional RoomType type;
	4: optional i32 capacity;
}

service FreeRoomService {
	set<Room> getFreeRoomsFromTime(1: PeriodOfTime period);
}
