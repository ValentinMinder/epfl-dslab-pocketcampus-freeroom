//
//  MyEduService.m
//  PocketCampus
//
//

#import "MyEduService.h"

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
    return [instance autorelease];
}

- (id)thriftServiceClientInstance {
    return [[[MyEduServiceClient alloc] initWithProtocol:[self thriftProtocolInstance]] autorelease];
}

- (void)getTequilaTokenForMyEduWithDelegate:(id)delegate {
    ServiceRequest* operation = [[ServiceRequest alloc] initWithThriftServiceClient:[self thriftServiceClientInstance] service:self delegate:delegate];
    operation.serviceClientSelector = @selector(getTequilaTokenForMyEdu);
    operation.delegateDidReturnSelector = @selector(getTequilaTokenForMyEduDidReturn:);
    operation.delegateDidFailSelector = @selector(getTequilaTokenForMyEduFailed);
    operation.returnType = ReturnTypeObject;
    [operationQueue addOperation:operation];
    [operation release];
}

- (void)getMyEduSessionForTequilaToken:(MyEduTequilaToken*)tequilaToken delegate:(id)delegate {
    ServiceRequest* operation = [[ServiceRequest alloc] initWithThriftServiceClient:[self thriftServiceClientInstance] service:self delegate:delegate];
    operation.serviceClientSelector = @selector(getMyEduSession:);
    operation.delegateDidReturnSelector = @selector(getMyEduSessionForTequilaToken:didReturn:);
    operation.delegateDidFailSelector = @selector(getMyEduSessionFailedForTequilaToken:);
    [operation addObjectArgument:tequilaToken];
    operation.returnType = ReturnTypeObject;
    [operationQueue addOperation:operation];
    [operation release];
}

- (void)dealloc
{
    instance = nil;
    [super dealloc];
}

@end
