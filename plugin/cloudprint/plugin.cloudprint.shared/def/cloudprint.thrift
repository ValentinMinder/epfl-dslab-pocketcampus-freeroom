namespace java org.pocketcampus.plugin.cloudprint.shared

enum CloudPrintStatusCode {
    OK = 200,
    AUTHENTICATION_ERROR = 407,
    PRINT_ERROR = 404
}

enum CloudPrintNbPagesPerSheet {
    ONE = 1,
    TWO = 2,
    FOUR = 4,
    SIX = 6,
    NINE = 9,
    SIXTEEN = 16
}

enum CloudPrintMultiPageLayout {
    // These two make sense
    LEFT_TO_RIGHT_TOP_TO_BOTTOM = 0, //default
    TOP_TO_BOTTOM_LEFT_TO_RIGHT,
    // This does not really make sense, but is yet supported
    BOTTOM_TO_TOP_LEFT_TO_RIGHT,
    BOTTOM_TO_TOP_RIGHT_TO_LEFT,
    LEFT_TO_RIGHT_BOTTOM_TO_TOP,
    RIGHT_TO_LEFT_BOTTOM_TO_TOP,
    RIGHT_TO_LEFT_TOP_TO_BOTTOM,
    TOP_TO_BOTTOM_RIGHT_TO_LEFT
}

struct CloudPrintMultiPageConfig {
// http://www.math.fsu.edu/Computer/printer_cl.math
    1: required CloudPrintNbPagesPerSheet nbPagesPerSheet;
    2: required CloudPrintMultiPageLayout layou;
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
