namespace java org.pocketcampus.shared.plugin.library

typedef i32 int

struct Book {
	1: string title;
	2: string author;
	3: int year;
	4: int docNumber;
	5: list<string> librairies;
}

struct BookAvailability {
	
}

service ILibraryServer {
	list<Book> search(1:string terms, 2:int pageNumber),
	BookAvailability getAvailability(1:int docNumber)
}