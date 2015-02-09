namespace java org.pocketcampus.plugin.cloudprint.shared

enum CloudPrintStatusCode {
    OK = 200,
    AUTHENTICATION_ERROR = 407,
    PRINT_ERROR = 404
}

enum CloudPrintNbPagesPerSheet {
    TWO = 2,
    FOUR = 4,
    SIX = 6,
    NINE = 9,
    SIXTEEN = 16
}

// WARNING do not change the names of the enums in this file. They are used "by name" in the Android app

enum CloudPrintMultiPageLayout { // DO NOT change the names of these, otherwise server code breaks
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
	2: required CloudPrintMultiPageLayout layout;
}

enum CloudPrintDoubleSidedConfig {
    //starting from 1 such that can differentiate with not set in Objective-C
    LONG_EDGE = 1,
    SHORT_EDGE = 2,
}

enum CloudPrintColorConfig {
    BLACK_WHITE = 1,
    COLOR = 2,
}

enum CloudPrintOrientation { // MUST not change the ints mapped to these, otherwise the server code breaks
	// http://www.cups.org/documentation.php/options.html
	PORTRAIT = 3, // no rotation (default)
	LANDSCAPE = 4, // 90 degrees
	REVERSE_LANDSCAPE = 5, // 270 degrees (seascape)
	REVERSE_PORTRAIT = 6, // 180 degrees (upside-down)
}

struct CloudPrintPageRange {
    1: required i32 pageFrom;
    2: required i32 pageTo;
}

struct CloudPrintMultipleCopies {
    1: required i32 numberOfCopies; // default: 1
    2: required bool collate; // default: no
}

struct PrintDocumentRequest {
    1: required i64 documentId;
    5: optional CloudPrintPageRange pageSelection;
    6: optional CloudPrintMultiPageConfig multiPageConfig;
    7: optional CloudPrintDoubleSidedConfig doubleSided;
    8: optional CloudPrintOrientation orientation;
    9: optional CloudPrintMultipleCopies multipleCopies;
    10: optional CloudPrintColorConfig colorConfig;
}

struct PrintDocumentResponse {
    1: required CloudPrintStatusCode statusCode;
}

struct PrintPreviewDocumentResponse {
    1: required CloudPrintStatusCode statusCode;
    2: optional i32 numberOfPages;
}


service CloudPrintService {
	PrintDocumentResponse printDocument( 1: PrintDocumentRequest request );
	PrintPreviewDocumentResponse printPreview( 1: PrintDocumentRequest request );
}
