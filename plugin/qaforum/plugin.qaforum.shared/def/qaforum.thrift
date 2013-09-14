namespace java org.pocketcampus.plugin.qaforum.shared
namespace csharp org.pocketcampus.plugin.qaforum.shared

struct QATequilaToken {
	1: required string iTequilaKey;
	2: optional string loginCookie;
}

struct s_ask{
	1: required string sessionid;
	2: required string content;
	3: required i32 topic;
	4: required string tags;
	5: required i32 expirytime;
	6: required i32 quesid;
}

struct s_accept{
	1: required i32 notificationid;
	2: required i32 accept;
}

struct s_request{
	1: required string sessionid;
	2:required string topics;
	3:required string tags;
}

struct s_answer{
	1: required string sessionid;
	2:required i32 forwardid;
	3:required string answer;
	4:required i32 typeid;
}

struct s_report{
	1:required string sessionid;
	2:required i32 forwardid;
	3:required i32 type;
	4:required string comment;
}

struct s_feedback{
	1: required string sessionid;
	2:required i32 forwardid;
	3:required string feedback;
	4:required double rate;
}

struct s_questionlist{
	1:required i32 forwardid;
	2:required i32 topic;
	3:required string tags;
	4:required string askername;
	5:required string time;
	6:required string content;
	7: required string sessionid;
}

struct s_forwardQuestions{
	1:required i32 number;
	2:required list<s_questionlist> questionlist;
}

struct s_session{
	1:required string sessionid;
	2:required i32	accept;
	3:required i32	resttime;
	4:required string language;
	5:required string topic;
	6:required i32 asktopic;
	7:required i32 askexpiry;
	8:required i32 intro;
}

struct s_latest{
	1:required string userid;
	2:required i32 quesid;
}

struct s_delete{
	1:required string userid;
	2:required i32 forwardid;
	3:required i32 type;
}

struct s_relation{
	1:required string myuserid;
	2:required string otheruserid;
}

struct s_tag{
	1:required string userid;
	2:required string tag;
}

service QAforumService {
	QATequilaToken getTequilaTokenForQAforum();
	s_session getSessionid(1: QATequilaToken token);
	i32 updateSetting(1: s_session setting);
	string acceptNotif (1: s_accept accept);
	i32 askQuestion(1: s_ask ask);
	i32 answerQuestion(1: s_answer answer);
	i32 feedbackQuestion(1: s_feedback feedback);
	string requestQuestion(1: s_request request);
	i32 reportQuestion(1: s_report report);
	string requestInformation(1: string sessionid);
	string myQuestions(1:string userid);
	string oneQuestion(1:i32 questionid);
	string myAnswers(1:string userid);
	string oneAnswer(1:i32 forwardid);
	string latestQuestions(1:string userid);
	string oneLatestQuestion(1:s_latest onelatest);
	string pendingNotifications(1:string userid);
	i32 deleteNotification(1:s_delete deleteinfo);
	string questionMatching(1:string question);
	string relationship(1:s_relation relation);
	i32 tagInterested(1:s_tag taguser);
	i32 closeIntro(1:string userid);
}
