namespace java org.pocketcampus.platform.sdk.shared.common

//////////
// MISC //
//////////



// Let's keep familiar names.
typedef i64 integer // XXX remove? common.integer is too long...

typedef string PushNotificationPhoneId

// We use Ids to identify choices etc...
// This way the client doesn't send them back but can use this instead.
typedef integer Id

// Uses Java's Date format, for last update time etc.
typedef integer Timestamp

// Duration in seconds.
typedef integer PreparationTime

struct TakeoutServerAddress{
	1: required string serverIP;
	2: required integer port;
}

// Currency, used for the restaurants.
struct Currency {
	1: required string name;
	2: required string symbol;
}

// 3d location
struct Location{
	1: required double latitude;
	2: required double longitude;
	3: optional double altitude;
}

// Five-star rating system.
enum Rating {
	UNKNOWN;
	ZERO;
	HALF;
	ONE;
	ONE_AND_A_HALF;
	TWO;
	TWO_AND_A_HALF;
	THREE;
	THREE_AND_A_HALF;
	FOUR;
	FOUR_AND_A_HALF;
	FIVE;
}

/////////////
// CHOICES //
/////////////

// Represents a possible choice.
struct Choice { 
	1: required Id choiceId;
	2: required string choiceValue;
}

// FROM SERVER TO CLIENT
// single choice
struct SingleChoiceOption {
	1: required Id singleChoiceId;
	2: required string name;
	3: required list<Choice> choices;
	4: required Choice defaultChoice;
}

// multiple choices
struct MultiChoiceOption {
	1: required Id multiChoiceId;
	2: required string name;
	3: required list<Choice> choices;
	4: required list<Choice> defaultChoices;
}

// FROM CLIENT TO SERVER
// single choice
struct ChosenSingleChoiceOption {
	1: required Id singleChoiceId;
	2: required Id chosenId;
}

// multiple choices
struct ChosenMultiChoiceOption {
	1: required Id multiChoiceId;
	2: required list<Id> chosenIds;
}
