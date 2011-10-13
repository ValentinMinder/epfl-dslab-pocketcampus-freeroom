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
	3: required string mail;
	4: required string uid;
	5: optional string web;
	6: optional string phone_number;
	7: optional string office;
	
}

service DirectoryService {
	list<Person> searchBySciper(1: string sciper);
	list<Person> searchByName(1: string firstName, 2: string lastName);
	list<Person> searchByApproxName(1: string firstName, 2: string lastName);
}