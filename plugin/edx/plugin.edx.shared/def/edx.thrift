namespace java org.pocketcampus.plugin.edx.shared


struct EdxCourse {
	1: required string courseId;
	2: required string courseTitle;
	3: required string courseNumber;
}

struct EdxModule {
	1: required string moduleUrl;
	2: required string moduleTitle;
}

struct EdxSection {
	1: required string sectionTitle;
	2: required list<EdxModule> sectionModules;
}




enum EdxItemType {
	VIDEO;
	HTML;
	PROBLEM;
}

struct EdxItemVideo{
	1: required string itemId;
	2: required string youtubeId;
	3: required string title;
	4: optional string html;
}

struct EdxItemHtml{
	1: required string itemId;
	2: required string htmlContent;
}

struct EdxItemProblem{
	1: required string itemId;
}

struct EdxSequence {
	1: required string verticalId;
	2: required list<EdxItemType> items;
	3: optional map<i32, EdxItemVideo> videoItems;
	4: optional map<i32, EdxItemHtml> htmlItems;
	5: optional map<i32, EdxItemProblem> problemItems;
}




struct EdxLoginReq {
	1: required string email;
	2: required string password;
}

struct EdxLoginResp {
	1: required i32 status;
	2: optional string sessionId;
	3: optional string userName; 
}

struct EdxReq {
	1: required string sessionId;
	2: optional string courseId;
	3: optional string moduleUrl;
}

struct EdxResp {
	1: required i32 status;
	2: optional list<EdxCourse> userCourses;
	3: optional list<EdxSection> courseSections;
	4: optional list<EdxSequence> moduleDetails;
}




enum MsgPsgMessageType {
	MESSAGE;
	AUDIO;
}

struct MsgPsgSendBroadcastReq {
	1: required string senderRef;
	2: required string roomRef;
	3: required string messageHeader;
	4: required MsgPsgMessageType messageType;
	5: optional string textBody;
	6: optional binary binaryBody;
}

struct MsgPsgSendBroadcastResp {
	1: required i32 status;
	2: optional i64 messageRef;
}

struct MsgPsgReceiveBroadcastReq {
	1: required string receiverRef;
	2: required string roomRef;
	3: required i64 lastMessageRef;
	4: required i32 pendingAudio;
}

struct MsgPsgMessage {
	1: required i64 messageRef;
	2: required string senderRef;
	3: required string messageHeader;
	4: required MsgPsgMessageType messageType;
	5: optional string textBody;
	6: optional binary binaryBody;
}

struct MsgPsgReceiveBroadcastResp {
	1: required i32 status;
	2: optional list<MsgPsgMessage> messages;
}

struct MsgPsgGetActiveRoomsReq {
	1: required string match;
}

struct MsgPsgRoom {
	1: required string roomRef;
	2: required i32 occupancy;
}

struct MsgPsgGetActiveRoomsResp {
	1: required i32 status;
	2: optional list<MsgPsgRoom> rooms;
}







service EdXService {
	EdxLoginResp doLogin(1: EdxLoginReq req);
	EdxResp getUserCourses(1: EdxReq req);
	EdxResp getCourseSections(1: EdxReq req);
	EdxResp getModuleDetails(1: EdxReq req);

	MsgPsgSendBroadcastResp sendBroadcast(1: MsgPsgSendBroadcastReq req);
	MsgPsgReceiveBroadcastResp receiveBroadcast(1: MsgPsgReceiveBroadcastReq req);
	MsgPsgGetActiveRoomsResp getActiveRooms(1: MsgPsgGetActiveRoomsReq req);
}
