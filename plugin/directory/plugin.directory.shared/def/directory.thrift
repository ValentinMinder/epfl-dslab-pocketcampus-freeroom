namespace java org.pocketcampus.plugin.directory.shared


struct DirectoryPersonRole {
	1: required string extendedLocalizedUnit;
	2: required string localizedTitle;
}

struct Person {
	1: required string firstName;
	2: required string lastName;
	3: required string sciper;
	4: optional string email;
	5: optional string web;
	6: optional string privatePhoneNumber;
	7: optional string officePhoneNumber;
	8: optional string office;
	9: optional string gaspar;
	10: optional list<string> organisationalUnits;
	11: optional string pictureUrl;
	
	12: optional map<string, DirectoryPersonRole> roles;
	13: optional map<string, string> homepages;
}

exception LDAPException {
	1: string message;
}

exception NoPictureFound{
	1: string message;
}

struct DirectoryRequest {
	1: required string query;
	4: optional string language;
	3: optional binary resultSetCookie;
}

struct DirectoryResponse {
	1: required i32 status;
	2: optional list<Person> results;
	3: optional binary resultSetCookie;
}

service DirectoryService {
	//list<Person> searchPersons(1: string nameOrSciper) throws (1: LDAPException le);
	//string getProfilePicture(1: string sciper) throws (1: NoPictureFound npf);
	//list<string> autocomplete(1:string constraint);
	
	DirectoryResponse searchDirectory(1: DirectoryRequest req);
}
