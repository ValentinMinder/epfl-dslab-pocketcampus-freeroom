namespace java org.pocketcampus.plugin.freeroom.shared
namespace csharp org.pocketcampus.plugin.freeroom.shared

struct PeriodOfTime{
	1: required Day day;
	2: required i32 startHour;
	3: required i32 endHour;
}

struct Room{
	1: required string building;
	2: required string number;
	3: optionnal RoomType type;
	4: optionnal i32 capacity;
}

enum Day{
	MONDAY; TUESDAY; WEDNESDAY; THURSDAY; FRIDAY; SATURDAY; SUNDAY;
}

enum RoomType{
	AUDITORIUM; EXERCISES;
}

service FreeRoomService {
	set<Room> getFreeRoomsFromTime(1: PeriodOfTime period);
}
