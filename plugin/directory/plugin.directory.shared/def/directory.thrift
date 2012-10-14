namespace java org.pocketcampus.plugin.directory.shared

typedef i32 int


//useless but to remember how to do an enum if needed
enum status {
	STUDENT;
	PHD_CANDIDATE;
	PROFESSOR;
	COWORKER;
}

// COMMENTED SO THE MODIFICATIONS IN SHARED WONT BE LOST
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
	10: optional list<string> OrganisationalUnit;
	11: optional string pictureUrl;
	
}

exception LDAPException {
	1: string message;
}

exception NoPictureFound{
	1: string message;
}

service DirectoryService {
	list<Person> searchPersons(1: string nameOrSciper) throws (1: LDAPException le);
	string getProfilePicture(1: string sciper) throws (1: NoPictureFound npf);
	list<string> autocomplete(1:string constraint);
}