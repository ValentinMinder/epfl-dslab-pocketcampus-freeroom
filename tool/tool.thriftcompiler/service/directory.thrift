namespace java org.pocketcampus.plugin.directory.shared

include "../include/common.thrift"

typedef i32 int


//useless but to remember how to do an enum if needed
enum status {
	STUDENT;
	PHD_CANDIDATE;
	PROFESSOR;
	COWORKER;
}

// COMMENTED SO THE MODIFICATIONS IN SHARED WONT BE LOST
//struct Person {
//	1: required string firstName;
//	2: required string lastName;
//	3: required string sciper;
//	4: optional string mail;
//	5: optional string web;
//	6: optional string phone_number;
//	7: optional string office;
//	8: optional string gaspar;
//	9: optional string ou;
//	10: optional string picture_url;
//	
//}

exception LDAPException {
	1: string message;
}

exception NoPictureFound{
	1: string message;
}

service DirectoryService {
	list<Person> search(1: string param) throws (1: LDAPException le);
	string getProfilePicture(1: string sciper) throws (1: NoPictureFound npf);
}