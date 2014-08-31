namespace java org.pocketcampus.plugin.cloudprint.shared






enum CloudPrintStatusCode {
    OK = 200,
    AUTHENTICATION_ERROR = 407,
    PRINT_ERROR = 404
}


struct CloudPrintPageRange {
    1: required i32 pageFrom;
    2: required i32 pageTo;
}


struct PrintDocumentRequest {
    1: required i64 documentId;
    2: required bool doubleSided;
    3: required bool blackAndWhite;
    4: required i32 numberOfCopies;
    5: optional CloudPrintPageRange pageSelection;
}

struct PrintDocumentResponse {
    1: required CloudPrintStatusCode statusCode;
}


service CloudPrintService {
	PrintDocumentResponse printDocument( 1: PrintDocumentRequest request );
}
