//
//  MyEduService.m
//  PocketCampus
//
//  ARC enabled
//

#import "MyEduService.h"

#import "ObjectArchiver.h"

#import "PCUtils.h"

@interface MyEduService ()

@property (strong) MyEduSession* session;

@end

static NSString* kMyEduSessionIdentifier = @"myEduSession";

@implementation MyEduService

static MyEduService* instance = nil;

+ (id)sharedInstanceToRetain {
    if (instance != nil) {
        return instance;
    }
    @synchronized(self) {
        if (instance == nil) {
            instance = [[[self class] alloc] initWithServiceName:@"myedu"];
        }
    }
    return instance;
}

- (id)thriftServiceClientInstance {
    return [[MyEduServiceClient alloc] initWithProtocol:[self thriftProtocolInstance]];
}

- (MyEduRequest*)createMyEduRequest {
    return [[MyEduRequest alloc] initWithIMyEduSession:[self lastSession] iLanguage:[PCUtils userLanguageCode]];
}

- (MyEduSession*)lastSession {
    if (self.session) {
        return self.session;
    }
    return (MyEduSession*)[ObjectArchiver objectForKey:kMyEduSessionIdentifier andPluginName:@"myedu"];
}

- (BOOL)saveSession:(MyEduSession*)session {
    self.session = session;
    return [ObjectArchiver saveObject:session forKey:kMyEduSessionIdentifier andPluginName:@"myedu"];
}

- (BOOL)deleteSession {
    return [ObjectArchiver saveObject:nil forKey:kMyEduSessionIdentifier andPluginName:@"myedu"];
}

- (void)getTequilaTokenForMyEduWithDelegate:(id)delegate {
    ServiceRequest* operation = [[ServiceRequest alloc] initWithThriftServiceClient:[self thriftServiceClientInstance] service:self delegate:delegate];
    operation.serviceClientSelector = @selector(getTequilaTokenForMyEdu);
    operation.delegateDidReturnSelector = @selector(getTequilaTokenForMyEduDidReturn:);
    operation.delegateDidFailSelector = @selector(getTequilaTokenForMyEduFailed);
    operation.returnType = ReturnTypeObject;
    [operationQueue addOperation:operation];
}

- (void)getMyEduSessionForTequilaToken:(MyEduTequilaToken*)tequilaToken delegate:(id)delegate {
    ServiceRequest* operation = [[ServiceRequest alloc] initWithThriftServiceClient:[self thriftServiceClientInstance] service:self delegate:delegate];
    operation.serviceClientSelector = @selector(getMyEduSession:);
    operation.delegateDidReturnSelector = @selector(getMyEduSessionForTequilaToken:didReturn:);
    operation.delegateDidFailSelector = @selector(getMyEduSessionFailedForTequilaToken:);
    [operation addObjectArgument:tequilaToken];
    operation.returnType = ReturnTypeObject;
    [operationQueue addOperation:operation];
}

- (void)getSubscribedCoursesListForRequest:(MyEduRequest*)myeduRequest delegate:(id)delegate {
    ServiceRequest* operation = [[ServiceRequest alloc] initWithThriftServiceClient:[self thriftServiceClientInstance] service:self delegate:delegate];
    operation.serviceClientSelector = @selector(getSubscribedCoursesList:);
    operation.delegateDidReturnSelector = @selector(getSubscribedCoursesListForRequest:didReturn:);
    operation.delegateDidFailSelector = @selector(getSubscribedCoursesListFailedForRequest:);
    operation.keepInCache = YES;
    operation.skipCache = YES;
    [operation addObjectArgument:myeduRequest];
    operation.returnType = ReturnTypeObject;
    [operationQueue addOperation:operation];
}

- (MyEduSubscribedCoursesListReply*)getFromCacheSubscribedCoursesListForRequest:(MyEduRequest*)myeduRequest {
    ServiceRequest* operation = [[ServiceRequest alloc] initForCachedResponseOnlyWithService:self];
    operation.serviceClientSelector = @selector(getSubscribedCoursesList:);
    operation.delegateDidReturnSelector = @selector(getSubscribedCoursesListForRequest:didReturn:);
    operation.delegateDidFailSelector = @selector(getSubscribedCoursesListFailedForRequest:);
    [operation addObjectArgument:myeduRequest];
    operation.returnType = ReturnTypeObject;
    return [operation cachedResponseObjectEvenIfStale:YES];
}

- (void)getCourseDetailsForRequest:(MyEduCourseDetailsRequest*)request myeduRequest:(MyEduRequest*)myeduRequest delegate:(id)delegate {
    ServiceRequest* operation = [[ServiceRequest alloc] initWithThriftServiceClient:[self thriftServiceClientInstance] service:self delegate:delegate];
    operation.serviceClientSelector = @selector(getCourseDetails::);
    operation.delegateDidReturnSelector = @selector(getCourseDetailsForRequest:myeduRequest:didReturn:);
    operation.delegateDidFailSelector = @selector(getCourseDetailsFailedForRequest:myeduRequest:);
    operation.keepInCache = YES;
    operation.skipCache = YES;
    [operation addObjectArgument:myeduRequest];
    [operation addObjectArgument:request];
    operation.returnType = ReturnTypeObject;
    [operationQueue addOperation:operation];
}

- (MyEduCourseDetailsReply*)getFromCacheCourseDetailsForRequest:(MyEduCourseDetailsRequest*)request myeduRequest:(MyEduRequest*)myeduRequest {
    ServiceRequest* operation = [[ServiceRequest alloc] initForCachedResponseOnlyWithService:self];
    operation.serviceClientSelector = @selector(getCourseDetails::);
    operation.delegateDidReturnSelector = @selector(getCourseDetailsForRequest:myeduRequest:didReturn:);
    operation.delegateDidFailSelector = @selector(getCourseDetailsFailedForRequest:myeduRequest:);
    [operation addObjectArgument:myeduRequest];
    [operation addObjectArgument:request];
    operation.returnType = ReturnTypeObject;
    return [operation cachedResponseObjectEvenIfStale:YES];
}

- (void)getSectionDetailsForRequest:(MyEduSectionDetailsRequest*)request myeduRequest:(MyEduRequest*)myeduRequest delegate:(id)delegate {
    ServiceRequest* operation = [[ServiceRequest alloc] initWithThriftServiceClient:[self thriftServiceClientInstance] service:self delegate:delegate];
    operation.serviceClientSelector = @selector(getSectionDetails::);
    operation.delegateDidReturnSelector = @selector(getSectionDetailsForRequest:myeduRequest:didReturn:);
    operation.delegateDidFailSelector = @selector(getSectionDetailsFailedForRequest:myeduRequest:);
    operation.keepInCache = YES;
    operation.skipCache = YES;
    [operation addObjectArgument:myeduRequest];
    [operation addObjectArgument:request];
    operation.returnType = ReturnTypeObject;
    [operationQueue addOperation:operation];
}

- (MyEduCourseDetailsReply*)getFromCacheSectionDetailsForRequest:(MyEduSectionDetailsRequest*)request myeduRequest:(MyEduRequest*)myeduRequest {
    ServiceRequest* operation = [[ServiceRequest alloc] initForCachedResponseOnlyWithService:self];
    operation.serviceClientSelector = @selector(getSectionDetails::);
    operation.delegateDidReturnSelector = @selector(getSectionDetailsForRequest:myeduRequest:didReturn:);
    operation.delegateDidFailSelector = @selector(getSectionDetailsFailedForRequest:myeduRequest:);
    [operation addObjectArgument:myeduRequest];
    [operation addObjectArgument:request];
    operation.returnType = ReturnTypeObject;
    return [operation cachedResponseObjectEvenIfStale:YES];
}

- (void)getModuleDetailsForRequest:(MyEduModuleDetailsRequest*)request myeduRequest:(MyEduRequest*)myeduRequest delegate:(id)delegate {
    ServiceRequest* operation = [[ServiceRequest alloc] initWithThriftServiceClient:[self thriftServiceClientInstance] service:self delegate:delegate];
    operation.serviceClientSelector = @selector(getModuleDetails::);
    operation.delegateDidReturnSelector = @selector(getModuleDetailsForRequest:myeduRequest:didReturn:);
    operation.delegateDidFailSelector = @selector(getModuleDetailsFailedForRequest:myeduRequest:);
    [operation addObjectArgument:myeduRequest];
    [operation addObjectArgument:request];
    operation.returnType = ReturnTypeObject;
    [operationQueue addOperation:operation];
}

- (void)submitFeedbackWithRequest:(MyEduSubmitFeedbackRequest*)request myeduRequest:(MyEduRequest*)myeduRequest delegate:(id)delegate {
    ServiceRequest* operation = [[ServiceRequest alloc] initWithThriftServiceClient:[self thriftServiceClientInstance] service:self delegate:delegate];
    operation.serviceClientSelector = @selector(submitFeedback::);
    operation.delegateDidReturnSelector = @selector(submitFeedbackForRequest:myeduRequest:didReturn:);
    operation.delegateDidFailSelector = @selector(submitFeedbackFailedForRequest:myeduRequest:);
    [operation addObjectArgument:myeduRequest];
    [operation addObjectArgument:request];
    operation.returnType = ReturnTypeObject;
    [operationQueue addOperation:operation];
}

- (void)dealloc
{
    instance = nil;
}

@end
