//
//  QAForumService.h
//  PocketCampus
//
//

#import <Foundation/Foundation.h>

#import "Service.h"

#import "qaforum.h"

@interface QAForumService : Service <ServiceProtocol>

//TODO: prototype methods with delegate for each method of QAForumService.h

/*
 - (QATequilaToken *) getTequilaTokenForQAforum;  // throws TException
 - (s_session *) getSessionid: (QATequilaToken *) token;  // throws TException
 - (int32_t) updateSetting: (s_session *) setting;  // throws TException
 - (NSString *) acceptNotif: (s_accept *) accept;  // throws TException
 - (int32_t) askQuestion: (s_ask *) ask;  // throws TException
 - (int32_t) answerQuestion: (s_answer *) answer;  // throws TException
 - (int32_t) feedbackQuestion: (s_feedback *) feedback;  // throws TException
 - (NSString *) requestQuestion: (s_request *) request;  // throws TException
 - (int32_t) reportQuestion: (s_report *) report;  // throws TException
 - (NSString *) requestInformation: (NSString *) sessionid;  // throws TException
 - (NSString *) myQuestions: (NSString *) userid;  // throws TException
 - (NSString *) oneQuestion: (int32_t) questionid;  // throws TException
 - (NSString *) myAnswers: (NSString *) userid;  // throws TException
 - (NSString *) oneAnswer: (int32_t) forwardid;  // throws TException
 - (NSString *) latestQuestions: (NSString *) userid;  // throws TException
 - (NSString *) oneLatestQuestion: (s_latest *) onelatest;  // throws TException
 - (NSString *) pendingNotifications: (NSString *) userid;  // throws TException
 - (int32_t) deleteNotification: (s_delete *) deleteinfo;  // throws TException
 - (NSString *) questionMatching: (NSString *) question;  // throws TException
 - (NSString *) relationship: (s_relation *) relation;  // throws TException
 - (int32_t) tagInterested: (s_tag *) taguser;  // throws TException
 - (int32_t) closeIntro: (NSString *) userid;  // throws TException
 */

//TODO: prototype methods with delegate for each method of QAForumService.h

+ (s_session*)lastSessionId;
+ (BOOL)saveSessionId:(s_session*)sessionId;
+ (BOOL)saveMyQuestionsList:(NSString*)ques;
+ (NSString*)lastQuestionsList;
+ (BOOL)saveLastNotif:(NSDictionary*)completeMsg;
+ (NSDictionary*)lastNotif;

- (void)getTequilaTokenForQAforumWithDelegate:(id)delegate;  // throws TException
- (void)getSessionIdWithTequilaToken:(QATequilaToken *)token delegate:(id)delegate;
- (void)askQuestionWithQuestion:(s_ask *)question delegate:(id)delegate;
- (void)updateSettingWithSetting: (s_session *) setting delegate:(id)delegate;
- (void)acceptNotifWithAccept: (s_accept *) accept delegate:(id)delegate;
- (void)answerQuestionWithAnswer: (s_answer *) answer delegate:(id)delegate;
- (void)feedbackQuestionWithFeedback: (s_feedback *)feedback delegate:(id)delegate;
- (void)requestQuestionWithRequest: (s_request *)request delegate:(id)delegate;
- (void)reportQuestionWithReport: (s_report *)report delegate:(id)delegate;
- (void)requestInformationWithSessionid: (NSString *)sessionid delegate:(id)delegate;
- (void)myQuestionsWithUserid: (NSString *)userid delegate:(id)delegate;
- (void)oneQuestionWithQuestionid: (int)questionid delegate:(id)delegate;
- (void) myAnswersWithUserid: (NSString *) userid delegate:(id)delegate;
- (void) oneAnswerWithUserid: (int)forwardid delegate:(id)delegate;
- (void) LatestQuestionsWithUserid: (NSString*)userid delegate:(id)delegate;
- (void) OneLatestQuestionsWithOne: (s_latest*)onelatest delegate:(id)delegate;
- (void) PendingNotificationsWithUserid: (NSString*)userid delegate:(id)delegate;
- (void) DeleteNotificationWithDelete: (s_delete*)deleteinfo delegate:(id)delegate;
- (void) QuestionMatchingWithQuestion: (NSString*)question delegate:(id)delegate;
- (void) RelationshipWithRelation: (s_relation*)relation delegate:(id)delegate;
- (void) TagInterestedWithTag: (s_tag*)taguser delegate:(id)delegate;
- (void) closeIntro: (NSString*)userid delegate:(id)delegate;
@end

@protocol QAForumServiceDelegate <ServiceDelegate>

@optional
//TODO: prototype 2 callbacks methods (success and failure) for each service method defined above  
- (void)getTequilaTokenForQAforumDidReturn:(QATequilaToken *)tequilaToken;
- (void)getTequilaTokenForQAforumFailed;

- (void)getSessionIdWithTequilaToken:(QATequilaToken*)token didReturn:(s_session*)session;
- (void)getSessionIdFailedForTequilaToken:(QATequilaToken *)token;

- (void)askQuestionWithQuestion:(s_ask*)data didReturn:(int32_t)result;
- (void)askQuestionFailed;

- (void)updateSettingWithSetting:(s_session*)data didReturn:(int32_t)result;
- (void)updateSettingFailed;

- (void)acceptNotifWithAccept:(s_accept*)data didReturn:(NSString *)result;
- (void)acceptNotifFailed;

- (void)answerQuestionWithAnswer:(s_answer*)data didReturn:(int32_t)result;
- (void)answerQuestionFailed;

- (void)feedbackQuestionWithFeedback:(s_feedback*)data didReturn:(int32_t)result;
- (void)feedbackQuestionFailed;

- (void)requestQuestionWithRequest:(s_request*)data didReturn:(NSString *)result;
- (void)requestQuestionFailed;

- (void)reportQuestionWithReport:(s_report*)data didReturn:(int32_t)result;
- (void)reportQuestionFailed;

- (void)requestInformationWithInformation:(NSString*)data didReturn:(NSString *)result;
- (void)requestInformationFailed;

- (void)myQuestionWithUserid:(NSString*)data didReturn:(NSString *)result;
- (void)myQuestionFailed;

- (void)oneQuestionWithQuestionid:(int)data didReturn:(NSString *)result;
- (void)oneQuestionFailed;

- (void)myAnswerWithUserid:(NSString*)data didReturn:(NSString*) result;
- (void)myAnswerFailed;

- (void)oneAnswerWithForwardid:(int)data didReturn:(NSString*) result;
- (void)oneAnswerFailed;

- (void) LatestQuestionsWithUserid: (NSString*)userid didReturn:(NSString*) result;
- (void) LatestQuestionsFailed;

- (void) OneLatestQuestionsWithOne: (s_latest*)onelatest didReturn:(NSString*) result;
- (void) OneLatestQuestionsFailed;

- (void) PendingNotificationsWithUserid: (NSString*)userid didReturn:(NSString*) result;
- (void) PendingNotificationsFailed;

- (void) DeleteNotificationWithDelete: (s_delete*)deleteinfo didReturn:(int32_t) result;
- (void) DeleteNotificationFailed;

- (void) QuestionMatchingWithQuestion: (NSString*)question didReturn:(NSString*) result;
- (void) QuestionMatchingFailed;

- (void) RelationshipWithRelation: (s_relation*)relation didReturn:(NSString*) result;
- (void) RelationshipFailed;

- (void) TagInterestedWithTag: (s_tag*)taguser didReturn:(NSString*) result;
- (void) TagInterestedFailed;

- (void) closeIntro: (NSString*)userid didReturn:(NSString*) result;
- (void) closeIntroFailed;

@end
