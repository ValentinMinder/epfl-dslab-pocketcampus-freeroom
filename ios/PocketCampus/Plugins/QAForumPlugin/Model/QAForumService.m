//
//  QAForumService.m
//  PocketCampus
//
//

#import "QAForumService.h"

#import "ObjectArchiver.h"

@implementation QAForumService

static NSString* kLastSessionIdKey = @"lastSessionId";
static NSString* LastQuestionsKey = @"lastQuestionsKey";
static NSString* LastnotifiCash = @"lastNotif";

static QAForumService* instance __weak = nil;

- (id)init {
    @synchronized(self) {
        if (instance) {
            @throw [NSException exceptionWithName:@"Double instantiation attempt" reason:@"QAForumService cannot be instancied more than once at a time, use sharedInstance instead" userInfo:nil];
        }
        self = [super initWithServiceName:@"qaforum"];
        if (self) {
            instance = self;
        }
        return self;
    }
}

+ (id)sharedInstanceToRetain {
    @synchronized (self) {
        if (instance) {
            return instance;
        }
#if __has_feature(objc_arc)
        return [[[self class] alloc] init];
#else
        return [[[[self class] alloc] init] autorelease];
#endif
    }
}

- (id)thriftServiceClientInstance {
#if __has_feature(objc_arc)
    return [[QAForumServiceClient alloc] initWithProtocol:[self thriftProtocolInstance]];
#else
    return [[[QAforumServiceClient alloc] initWithProtocol:[self thriftProtocolInstance]] autorelease];
#endif
}

+ (s_session*)lastSessionId {
    return (s_session*)[ObjectArchiver objectForKey:kLastSessionIdKey andPluginName:@"qaforum"];
}

+ (BOOL)saveSessionId:(s_session*)sessionId {
    return [ObjectArchiver saveObject:sessionId forKey:kLastSessionIdKey andPluginName:@"qaforum"];
}

+ (BOOL)saveMyQuestionsList:(NSString*)ques {
    return [ObjectArchiver saveObject:ques forKey:LastQuestionsKey andPluginName:@"qaforum"];
}

+ (NSString*)lastQuestionsList {
    return (NSString*)[ObjectArchiver objectForKey:LastQuestionsKey andPluginName:@"qaforum"];
}

+ (BOOL)saveLastNotif:(NSDictionary*)completeMsg {
    return [ObjectArchiver saveObject:completeMsg forKey:LastnotifiCash andPluginName:@"qaforum"];
}

+ (NSDictionary*)lastNotif {
    return (NSDictionary*)[ObjectArchiver objectForKey:LastnotifiCash andPluginName:@"qaforum"];
}

//TODO: implement service methods defined in header

- (void)getTequilaTokenForQAforumWithDelegate:(id)delegate {
    ServiceRequest* operation = [[ServiceRequest alloc] initWithThriftServiceClient:[self thriftServiceClientInstance] service:self delegate:delegate];
    operation.serviceClientSelector = @selector(getTequilaTokenForQAforum);
    operation.delegateDidReturnSelector = @selector(getTequilaTokenForQAforumDidReturn:);
    operation.delegateDidFailSelector = @selector(getTequilaTokenForQAforumFailed);
    operation.returnType = ReturnTypeObject;
    [operationQueue addOperation:operation];
    [operation release];
}

- (void)getSessionIdWithTequilaToken:(QATequilaToken*)token delegate:(id)delegate {
    ServiceRequest* operation = [[ServiceRequest alloc] initWithThriftServiceClient:[self thriftServiceClientInstance] service:self delegate:delegate];
    operation.serviceClientSelector = @selector(getSessionid:);
    operation.delegateDidReturnSelector = @selector(getSessionIdWithTequilaToken:didReturn:);
    operation.delegateDidFailSelector = @selector(getSessionIdFailedForTequilaToken:);
    [operation addObjectArgument:token];
    operation.returnType = ReturnTypeObject;
    [operationQueue addOperation:operation];
    [operation release];
}

- (void)askQuestionWithQuestion:(s_ask*)question delegate:(id)delegate {
    ServiceRequest* operation = [[ServiceRequest alloc] initWithThriftServiceClient:[self thriftServiceClientInstance] service:self delegate:delegate];
    operation.serviceClientSelector = @selector(askQuestion:);
    operation.delegateDidReturnSelector = @selector(askQuestionWithQuestion:didReturn:);
    operation.delegateDidFailSelector = @selector(askQuestionFailed);
    [operation addObjectArgument:question];
    operation.returnType = ReturnTypeInt;
    [operationQueue addOperation:operation];
    [operation release];
}

- (void)updateSettingWithSetting: (s_session *) setting delegate:(id)delegate {
    ServiceRequest* operation = [[ServiceRequest alloc] initWithThriftServiceClient:[self thriftServiceClientInstance] service:self delegate:delegate];
    operation.serviceClientSelector = @selector(updateSetting:);
    operation.delegateDidReturnSelector = @selector(updateSettingWithSetting:didReturn:);
    operation.delegateDidFailSelector = @selector(updateSettingFailed);
    [operation addObjectArgument:setting];
    operation.returnType = ReturnTypeInt;
    [operationQueue addOperation:operation];
    [operation release];
}

- (void)acceptNotifWithAccept: (s_accept *) accept delegate:(id)delegate {
    ServiceRequest* operation = [[ServiceRequest alloc] initWithThriftServiceClient:[self thriftServiceClientInstance] service:self delegate:delegate];
    operation.serviceClientSelector = @selector(acceptNotif:);
    operation.delegateDidReturnSelector = @selector(acceptNotifWithAccept:didReturn:);
    operation.delegateDidFailSelector = @selector(acceptNotifFailed);
    [operation addObjectArgument:accept];
    operation.returnType = ReturnTypeObject;
    [operationQueue addOperation:operation];
    [operation release];
}

- (void)answerQuestionWithAnswer: (s_answer *) answer delegate:(id)delegate {
    ServiceRequest* operation = [[ServiceRequest alloc] initWithThriftServiceClient:[self thriftServiceClientInstance] service:self delegate:delegate];
    operation.serviceClientSelector = @selector(answerQuestion:);
    operation.delegateDidReturnSelector = @selector(answerQuestionWithAnswer:didReturn:);
    operation.delegateDidFailSelector = @selector(answerQuestionFailed);
    [operation addObjectArgument:answer];
    operation.returnType = ReturnTypeInt;
    [operationQueue addOperation:operation];
    [operation release];
}

- (void)feedbackQuestionWithFeedback: (s_feedback *)feedback delegate:(id)delegate {
    ServiceRequest* operation = [[ServiceRequest alloc] initWithThriftServiceClient:[self thriftServiceClientInstance] service:self delegate:delegate];
    operation.serviceClientSelector = @selector(feedbackQuestion:);
    operation.delegateDidReturnSelector = @selector(feedbackQuestionWithFeedback:didReturn:);
    operation.delegateDidFailSelector = @selector(feedbackQuestionFailed);
    [operation addObjectArgument:feedback];
    operation.returnType = ReturnTypeInt;
    [operationQueue addOperation:operation];
    [operation release];
}

- (void)requestQuestionWithRequest: (s_request *)request delegate:(id)delegate {
    if (![request isKindOfClass:[s_request class]]) {
        @throw [NSException exceptionWithName:@"bad request" reason:@"request is either nil or not of class request" userInfo:nil];
    }
    ServiceRequest* operation = [[ServiceRequest alloc] initWithThriftServiceClient:[self thriftServiceClientInstance] service:self delegate:delegate];
    operation.serviceClientSelector = @selector(requestQuestion:);
    operation.delegateDidReturnSelector = @selector(requestQuestionWithRequest:didReturn:);
    operation.delegateDidFailSelector = @selector(requestQuestionFailed);
    [operation addObjectArgument:request];
    operation.returnType = ReturnTypeObject;
    [operationQueue addOperation:operation];
    [operation release];
}

- (void)reportQuestionWithReport: (s_report *)report delegate:(id)delegate {
    ServiceRequest* operation = [[ServiceRequest alloc] initWithThriftServiceClient:[self thriftServiceClientInstance] service:self delegate:delegate];
    operation.serviceClientSelector = @selector(reportQuestion:);
    operation.delegateDidReturnSelector = @selector(reportQuestionWithReport:didReturn:);
    operation.delegateDidFailSelector = @selector(reportQuestionFailed);
    [operation addObjectArgument:report];
    operation.returnType = ReturnTypeInt;
    [operationQueue addOperation:operation];
    [operation release];
}

- (void)requestInformationWithSessionid: (NSString *)sessionid delegate:(id)delegate {
    ServiceRequest* operation = [[ServiceRequest alloc] initWithThriftServiceClient:[self thriftServiceClientInstance] service:self delegate:delegate];
    operation.serviceClientSelector = @selector(requestInformation:);
    operation.delegateDidReturnSelector = @selector(requestInformationWithInformation:didReturn:);
    operation.delegateDidFailSelector = @selector(requestInformationFailed);
    [operation addObjectArgument:sessionid];
    operation.returnType = ReturnTypeObject;
    [operationQueue addOperation:operation];
    [operation release];
}

- (void)myQuestionsWithUserid: (NSString *)userid delegate:(id)delegate {
    ServiceRequest* operation = [[ServiceRequest alloc] initWithThriftServiceClient:[self thriftServiceClientInstance] service:self delegate:delegate];
    operation.serviceClientSelector = @selector(myQuestions:);
    operation.delegateDidReturnSelector = @selector(myQuestionWithUserid:didReturn:);
    operation.delegateDidFailSelector = @selector(myQuestionFailed);
    [operation addObjectArgument:userid];
    operation.returnType = ReturnTypeObject;
    [operationQueue addOperation:operation];
    [operation release];
}

- (void)oneQuestionWithQuestionid:(int)questionid delegate:(id)delegate {
    ServiceRequest* operation = [[ServiceRequest alloc] initWithThriftServiceClient:[self thriftServiceClientInstance] service:self delegate:delegate];
    operation.serviceClientSelector = @selector(oneQuestion:);
    operation.delegateDidReturnSelector = @selector(oneQuestionWithQuestionid:didReturn:);
    operation.delegateDidFailSelector = @selector(oneQuestionFailed);
    [operation addIntArgument:questionid];
    operation.returnType = ReturnTypeObject;
    [operationQueue addOperation:operation];
    [operation release];
}


- (void)myAnswersWithUserid:(NSString *)userid delegate:(id)delegate {
    ServiceRequest* operation = [[ServiceRequest alloc] initWithThriftServiceClient:[self thriftServiceClientInstance] service:self delegate:delegate];
    operation.serviceClientSelector = @selector(myAnswers:);
    operation.delegateDidReturnSelector = @selector(myAnswerWithUserid:didReturn:);
    operation.delegateDidFailSelector = @selector(myAnswerFailed);
    [operation addObjectArgument:userid];
    operation.returnType = ReturnTypeObject;
    [operationQueue addOperation:operation];
    [operation release];
}

- (void)oneAnswerWithUserid:(int)forwardid delegate:(id)delegate {
    ServiceRequest* operation = [[ServiceRequest alloc] initWithThriftServiceClient:[self thriftServiceClientInstance] service:self delegate:delegate];
    operation.serviceClientSelector = @selector(oneAnswer:);
    operation.delegateDidReturnSelector = @selector(oneAnswerWithForwardid:didReturn:);
    operation.delegateDidFailSelector = @selector(oneAnswerFailed);
    [operation addIntArgument:forwardid];
    operation.returnType = ReturnTypeObject;
    [operationQueue addOperation:operation];
    [operation release];
}

- (void)LatestQuestionsWithUserid:(NSString *)userid delegate:(id)delegate {
    ServiceRequest* operation = [[ServiceRequest alloc] initWithThriftServiceClient:[self thriftServiceClientInstance] service:self delegate:delegate];
    operation.serviceClientSelector = @selector(latestQuestions:);
    operation.delegateDidReturnSelector = @selector(LatestQuestionsWithUserid:didReturn:);
    operation.delegateDidFailSelector = @selector(LatestQuestionsFailed);
    [operation addObjectArgument:userid];
    operation.returnType = ReturnTypeObject;
    [operationQueue addOperation:operation];
    [operation release];
}

- (void)OneLatestQuestionsWithOne:(s_latest *)onelatest delegate:(id)delegate {
    ServiceRequest* operation = [[ServiceRequest alloc] initWithThriftServiceClient:[self thriftServiceClientInstance] service:self delegate:delegate];
    operation.serviceClientSelector = @selector(oneLatestQuestion:);
    operation.delegateDidReturnSelector = @selector(OneLatestQuestionsWithOne:didReturn:);
    operation.delegateDidFailSelector = @selector(OneLatestQuestionsFailed);
    [operation addObjectArgument:onelatest];
    operation.returnType = ReturnTypeObject;
    [operationQueue addOperation:operation];
    [operation release];
}

-(void)PendingNotificationsWithUserid:(NSString *)userid delegate:(id)delegate {
    ServiceRequest* operation = [[ServiceRequest alloc] initWithThriftServiceClient:[self thriftServiceClientInstance] service:self delegate:delegate];
    operation.serviceClientSelector = @selector(pendingNotifications:);
    operation.delegateDidReturnSelector = @selector(PendingNotificationsWithUserid:didReturn:);
    operation.delegateDidFailSelector = @selector(PendingNotificationsFailed);
    [operation addObjectArgument:userid];
    operation.returnType = ReturnTypeObject;
    [operationQueue addOperation:operation];
    [operation release];
}

-(void)DeleteNotificationWithDelete:(s_delete *)deleteinfo delegate:(id)delegate {
    ServiceRequest* operation = [[ServiceRequest alloc] initWithThriftServiceClient:[self thriftServiceClientInstance] service:self delegate:delegate];
    operation.serviceClientSelector = @selector(deleteNotification:);
    operation.delegateDidReturnSelector = @selector(DeleteNotificationWithDelete:didReturn:);
    operation.delegateDidFailSelector = @selector(DeleteNotificationFailed);
    [operation addObjectArgument:deleteinfo];
    operation.returnType = ReturnTypeInt;
    [operationQueue addOperation:operation];
    [operation release];
}

-(void)QuestionMatchingWithQuestion:(NSString *)question delegate:(id)delegate {
    ServiceRequest* operation = [[ServiceRequest alloc] initWithThriftServiceClient:[self thriftServiceClientInstance] service:self delegate:delegate];
    operation.serviceClientSelector = @selector(questionMatching:);
    operation.delegateDidReturnSelector = @selector(QuestionMatchingWithQuestion:didReturn:);
    operation.delegateDidFailSelector = @selector(QuestionMatchingFailed);
    [operation addObjectArgument:question];
    operation.returnType = ReturnTypeObject;
    [operationQueue addOperation:operation];
    [operation release];
}

-(void)RelationshipWithRelation:(s_relation *)relation delegate:(id)delegate {
    ServiceRequest* operation = [[ServiceRequest alloc] initWithThriftServiceClient:[self thriftServiceClientInstance] service:self delegate:delegate];
    operation.serviceClientSelector = @selector(relationship:);
    operation.delegateDidReturnSelector = @selector(RelationshipWithRelation:didReturn:);
    operation.delegateDidFailSelector = @selector(RelationshipFailed);
    [operation addObjectArgument:relation];
    operation.returnType = ReturnTypeObject;
    [operationQueue addOperation:operation];
    [operation release];
}

-(void)TagInterestedWithTag:(s_tag *)taguser delegate:(id)delegate {
    ServiceRequest* operation = [[ServiceRequest alloc] initWithThriftServiceClient:[self thriftServiceClientInstance] service:self delegate:delegate];
    operation.serviceClientSelector = @selector(tagInterested:);
    operation.delegateDidReturnSelector = @selector(TagInterestedWithTag:didReturn:);
    operation.delegateDidFailSelector = @selector(TagInterestedFailed);
    [operation addObjectArgument:taguser];
    operation.returnType = ReturnTypeInt;
    [operationQueue addOperation:operation];
    [operation release];

}

- (void)closeIntro: (NSString*)userid delegate:(id)delegate {
    ServiceRequest* operation = [[ServiceRequest alloc] initWithThriftServiceClient:[self thriftServiceClientInstance] service:self delegate:delegate];
    operation.serviceClientSelector = @selector(closeIntro:);
    operation.delegateDidReturnSelector = @selector(closeIntro:didReturn:);
    operation.delegateDidFailSelector = @selector(closeIntroFailed);
    [operation addObjectArgument:userid];
    operation.returnType = ReturnTypeInt;
    [operationQueue addOperation:operation];
    [operation release];
    
}

- (void)dealloc
{
    @synchronized(self) {
        instance = nil;
    }
#if __has_feature(objc_arc)
#else
    [super dealloc];
#endif
}

@end
