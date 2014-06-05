namespace java org.pocketcampus.plugin.freeroom.shared
//namespace csharp org.pocketcampus.plugin.freeroom.shared

const i32 HTTP_UPDATED = 299;
const i32 LENGTH_USERMESSAGE = 50;
const i32 MIN_AUTOCOMPL_LENGTH = 2;

const i32 MAXIMAL_WEEKS_IN_FUTURE = 4;
const i32 MAXIMAL_WEEKS_IN_PAST = 1;

struct FRRoom{
	1: required string doorCode;
	2: required string uid;
	3: optional string doorCodeWithoutSpace;
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
	32: optional string typeFR;
	33: optional string typeEN;
	34: optional string doorCodeAlias;
}


struct FRPeriod {
	1: required i64 timeStampStart;
	2: required i64 timeStampEnd;
	//TODO todelete
	10: required bool recurrent;
}

// NOTE ABOUT REPLY STATUS
// it's compliant with standard HTTP status code
// currently used in all request - reply scheme:
// 200 - OK - when the result is correctly given
// 400 - BAD REQUEST - when the server couldn't answer due to malformed query from the client
// 500 - INTERNAL ERROR - when the server couldn't answer due to internal implementation error
// currently used in particular request-reply scheme:
// 409 - CONFLICT - when the same user want to indicate the same period for a ImWorkingRequest.

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
	41: required FRPeriod treatedPeriod;
	5: optional double ratioWorstCaseProbableOccupancy;
}

struct FRRequest {
	1: required FRPeriod period;
	2: required bool onlyFreeRooms;
	//if null, it means every rooms
	3: required list<string> uidList;
	//as defined in database, see create-tables.sql in server for more info
	4: required i32 userGroup;
}

struct FRReply {
	1: required i32 status;
	2: required string statusComment;
	//map from building to list of occupancies in the building
	3: optional map<string, list<Occupancy>> occupancyOfRooms;
	4: optional FRPeriod overallTreatedPeriod;
}

//forbiddenRooms represents the rooms that shouldn't be replied 
struct AutoCompleteRequest {
	// the string must be at least two chars to be accepted by the server
	1: required string constraint;
	2: optional set<string> forbiddenRoomsUID;
	//as defined in database, see create-tables.sql in server for more info
	3: required i32 userGroup;
	// if not present or false, autocomplete automatically adds a "%" to your constraint
	4: optional bool exactString;
}

struct AutoCompleteReply {
	1: required i32 status;
	2: required string statusComment;
	4: optional map<string, list<FRRoom>> listRoom;
}

struct AutoCompleteUserMessageRequest {
	1: required FRPeriod period;
	2: required FRRoom room;
	3: required string constraint;
}

struct AutoCompleteUserMessageReply {
	1: required i32 status;
	2: required string statusComment; 
	3: optional list<string> messages;
}

struct WorkingOccupancy {
	1: required FRPeriod period;
	2: required FRRoom room;
	40: optional string message;
}

struct MessageFrequency {
	1: required string message;
	2: required i32 frequency;
}

struct ImWorkingRequest {
	1: required WorkingOccupancy work;
//This hash must be unique across all sessions and time
	2: required string hash;
}

struct ImWorkingReply {
	1: required i32 status;
	2: required string statusComment;
}

struct WhoIsWorkingRequest {
	1: required string roomUID;
	2: required FRPeriod period;
}

struct WhoIsWorkingReply {
	1: required i32 status;
	2: required string statusComment;
	//map a message to the number of time it appears
	3: optional list<MessageFrequency> messages;
}


struct LogMessage {
	1: required i64 timestamp;
	//path beeing the path to the class where it failed
	2: required string path;
	3: required string message;
}

struct RegisterUser {
	// epfl account
	1: required string email;
	3: required string config;
}

service FreeRoomService {
	// new feature, (merge of the two above)
	FRReply getOccupancy(1: FRRequest request);
	
	// autocomplete for searching for a room
	AutoCompleteReply autoCompleteRoom(1: AutoCompleteRequest request);
	
	//autocomplete of user messages
	AutoCompleteUserMessageReply autoCompleteUserMessage(1: AutoCompleteUserMessageRequest request);
	
	// indicate that i'm going to work there
	ImWorkingReply indicateImWorking(1: ImWorkingRequest request);
	
	WhoIsWorkingReply getUserMessages(1: WhoIsWorkingRequest request);
	
	//used to log critical bug that alter user experience
	void logSevere(1: LogMessage log);
	
	void logWarning(1: LogMessage log);
	
	bool registerUserSettings(1: RegisterUser user);
}
