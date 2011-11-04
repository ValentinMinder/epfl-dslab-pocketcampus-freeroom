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

struct Person {
	1: required string firstName;
	2: required string lastName;
	3: required string sciper;
	4: optional string mail;
	5: optional string web;
	6: optional string phone_number;
	7: optional string office;
	8: optional string gaspar;
	9: optional string ou;
	
}

exception LDAPException {
	1: string message;
}

service DirectoryService {
	list<Person> search(1: string param) throws (1: LDAPException le);
}