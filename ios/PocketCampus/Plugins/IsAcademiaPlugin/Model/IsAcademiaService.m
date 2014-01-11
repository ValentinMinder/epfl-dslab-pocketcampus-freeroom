





#import "IsAcademiaService.h"

@implementation IsAcademiaService

static IsAcademiaService* instance __weak = nil;

#pragma mark - Init

- (id)init {
    @synchronized(self) {
        if (instance) {
            @throw [NSException exceptionWithName:@"Double instantiation attempt" reason:@"IsAcademiaService cannot be instancied more than once at a time, use sharedInstance instead" userInfo:nil];
        }
        self = [super initWithServiceName:@"isacademia" thriftServiceClientClassName:NSStringFromClass(IsAcademiaServiceClient.class)];
        if (self) {
            instance = self;
        }
        return self;
    }
}

#pragma mark - ServiceProtocol

+ (id)sharedInstanceToRetain {
    @synchronized (self) {
        if (instance) {
            return instance;
        }
        return [[[self class] alloc] init];
    }
}

#pragma mark - Service methods

- (void)getScheduleTokenWithDelegate:(id<IsAcademiaServiceDelegate>)delegate {
    ServiceRequest* operation = [[ServiceRequest alloc] initWithThriftServiceClient:[self thriftServiceClientInstance] service:self delegate:delegate];
    operation.serviceClientSelector = @selector(getScheduleToken);
    operation.delegateDidReturnSelector = @selector(getScheduleTokenDidReturn:);
    operation.delegateDidFailSelector = @selector(getScheduleTokenFailed);
    operation.returnType = ReturnTypeObject;
    [self.operationQueue addOperation:operation];
}

- (void)getScheduleWithRequest:(ScheduleRequest*)request delegate:(id<IsAcademiaServiceDelegate>)delegate {
    [PCUtils throwExceptionIfObject:request notKindOfClass:[ScheduleRequest class]];
    ServiceRequest* operation = [[ServiceRequest alloc] initWithThriftServiceClient:[self thriftServiceClientInstance] service:self delegate:delegate];
    operation.serviceClientSelector = @selector(getSchedule:);
    operation.delegateDidReturnSelector = @selector(getScheduleForRequest:didReturn:);
    operation.delegateDidFailSelector = @selector(getScheduleFailedForRequest:);
    [operation addObjectArgument:request];
    operation.returnType = ReturnTypeObject;
    [self.operationQueue addOperation:operation];
}

#pragma mark - Dealloc

- (void)dealloc
{
    @synchronized(self) {
        instance = nil;
    }
}

@end
