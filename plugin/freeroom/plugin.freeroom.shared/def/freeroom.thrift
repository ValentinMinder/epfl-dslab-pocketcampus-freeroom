namespace java org.pocketcampus.plugin.freeroom.shared
//namespace csharp org.pocketcampus.plugin.freeroom.shared

const i32 HTTP_UPDATED = 299;
const i32 LENGTH_USERMESSAGE = 50;
const i32 MIN_AUTOCOMPL_LENGTH = 2;

const i32 MAXIMAL_WEEKS_IN_FUTURE = 4;
// TODO: evaluate usefulness
const i32 MAXIMAL_WEEKS_IN_PAST = 1;

// after last hour check (7pm), epfl is closed for public, and most of the room are free
// these times could be extended for staff only
const i32 FIRST_HOUR_CHECK = 8;
const i32 LAST_HOUR_CHECK = 19;
// TODO: evaluate usefulness
const i32 MIN_MINUTE_INTERVAL = 5;

struct FRRoom{

	// Official EPFL Name, of the form “Building [Zone][Floor] RoomNumber”, where [] are optional.
	// For example, PH D3 395 corresponds to the building “PH”, zone “D”, floor “3” and room number “395”.
	// Spaces position are NEVER garantueed.
	1: required string doorCode;
	
	// this is the unique EPFL ID. A room should NEVER be identified by its name
	// (it may change, spaces may be added/removed) but only by its UID.
	2: required string uid;
	
	// all these fields are coming from epfl database (webservice)
	// http://pocketcampus.epfl.ch/proxy/archibus.php/rwsrooms/getRoom?[TOKEN]=1&app=freeroom&caller=sciper&id=895
	// https://websrv.epfl.ch/rwsrooms/doc
	3: optional string doorCodeWithoutSpace;
	4: optional i32 capacity;
	
	// probably 
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
	
	31: optional string EWAid; //bc420@intranet.epfl.ch
	32: optional string typeFR; // eg "Conference room"
	33: optional string typeEN; 
	
	// Please note that if the ALIAS is set, it should ALWAYS be used in place of the official name!
	// IS-Academia and most other EPFL services always use alias, BUT a search must also return results based ont eh official name.
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

// defines if the room is reserved or not
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
	5: required FRPeriod treatedPeriod;
	// not set: we don't know (capacity not set)
	// set: ratio between expected # of people to the capacity
	6: optional double ratioWorstCaseProbableOccupancy;
}

struct FRRequest {
	1: required FRPeriod period;
	2: required bool onlyFreeRooms;
	// if empty, it means every rooms
	// uid of rooms, eg: "12908"
	3: required list<string> uidList;
	//as defined in database, see create-tables.sql in server for more info
	4: required i32 userGroup;
}

struct FRReply {
	1: required i32 status;
	//useful for debugging
	2: required string statusComment;
	//map from building to list of occupancies in the building
	// nothing to do with room uid
	// example: "CO", "BC"
	// it's empty if there are no results
	3: optional map<string, list<Occupancy>> occupancyOfRooms;
	4: optional FRPeriod overallTreatedPeriod;
}

//forbiddenRooms represents the rooms that shouldn't be replied 
struct AutoCompleteRequest {
	// the string must be at least two chars to be accepted by the server
	1: required string constraint;
	// uid of rooms that wont be returned because already selected.
	2: optional set<string> forbiddenRoomsUID;
	//as defined in database, see create-tables.sql in server for more info
	3: required i32 userGroup;
	// if not present or false, autocomplete automatically adds a "%" to your constraint
	// useful ???
	4: optional bool exactString;
}

struct AutoCompleteReply {
	1: required i32 status;
	2: required string statusComment;
	// map from building name to list of rooms in this building (available in freeroom)
	4: optional map<string, list<FRRoom>> listRoom;
}

// TODO: implement on android client side
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
	// how many times the same message appears
	// rename: count
	2: required i32 frequency;
}

struct ImWorkingRequest {
	1: required WorkingOccupancy work;
	// this identifies the user (anonymously)
	// This hash must be unique across all sessions and time
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
	// could be transformed to a map from subject to count.
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
	// main service to get the room occupancies
	FRReply getOccupancy(1: FRRequest request);
	
	// autocomplete for searching for a room
	AutoCompleteReply autoCompleteRoom(1: AutoCompleteRequest request);
	
	//autocomplete of user messages
	// it's used after you already selected a room and a time period
	// retrieves user messages from the exact same room and period
	// (useful if you're a group working on the same thing)
	AutoCompleteUserMessageReply autoCompleteUserMessage(1: AutoCompleteUserMessageRequest request);
	
	// indicate that i'm going to work there
	ImWorkingReply indicateImWorking(1: ImWorkingRequest request);
	
	WhoIsWorkingReply getUserMessages(1: WhoIsWorkingRequest request);
	
	//used to log critical bug that alter user experience
	void logSevere(1: LogMessage log);
	
	void logWarning(1: LogMessage log);
	
	bool registerUserSettings(1: RegisterUser user);
}
