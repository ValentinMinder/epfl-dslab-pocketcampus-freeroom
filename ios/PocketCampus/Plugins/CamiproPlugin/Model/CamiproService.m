

//  Created by Loïc Gardiol on 17.05.12.


#import "CamiproService.h"

#import "PCObjectArchiver.h"

static CamiproService* instance __weak = nil;

@implementation CamiproService

@synthesize camiproSession = _camiproSession;

#pragma mark - Init

- (id)init {
    @synchronized(self) {
        if (instance) {
            @throw [NSException exceptionWithName:@"Double instantiation attempt" reason:@"CamiproService cannot be instancied more than once at a time, use sharedInstance instead" userInfo:nil];
        }
        self = [super initWithServiceName:@"camipro" thriftServiceClientClassName:NSStringFromClass(CamiproServiceClient.class)];
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
#if __has_feature(objc_arc)
        return [[[self class] alloc] init];
#else
        return [[[[self class] alloc] init] autorelease];
#endif
    }
}

#pragma mark - Properties

static NSString* const kCamiproSession = @"camiproSession";

- (CamiproSession*)camiproSession {
    if (_camiproSession) {
        return _camiproSession;
    }
    _camiproSession = (CamiproSession*)[PCObjectArchiver objectForKey:kCamiproSession andPluginName:@"camipro"];
    return _camiproSession;
}

- (void)setCamiproSession:(CamiproSession *)camiproSession {
    _camiproSession = camiproSession;
    [PCObjectArchiver saveObject:camiproSession forKey:kCamiproSession andPluginName:@"camipro"];
}

#pragma mark - Service methods

- (void)getTequilaTokenForCamiproDelegate:(id)delegate {
    ServiceRequest* operation = [[ServiceRequest alloc] initWithThriftServiceClient:[self thriftServiceClientInstance] service:self delegate:delegate];
    operation.serviceClientSelector = @selector(getTequilaTokenForCamipro);
    operation.delegateDidReturnSelector = @selector(getTequilaTokenForCamiproDidReturn:);
    operation.delegateDidFailSelector = @selector(getTequilaTokenForCamiproFailed);
    operation.returnType = ReturnTypeObject;
    [self.operationQueue addOperation:operation];
}

- (void)getSessionIdForServiceWithTequilaKey:(TequilaToken*)tequilaKey delegate:(id)delegate {
    ServiceRequest* operation = [[ServiceRequest alloc] initWithThriftServiceClient:[self thriftServiceClientInstance] service:self delegate:delegate];
    operation.serviceClientSelector = @selector(getCamiproSession:);
    operation.delegateDidReturnSelector = @selector(getSessionIdForServiceWithTequilaKey:didReturn:);
    operation.delegateDidFailSelector = @selector(getSessionIdForServiceFailedForTequilaKey:);
    [operation addObjectArgument:tequilaKey];
    operation.returnType = ReturnTypeObject;
    [self.operationQueue addOperation:operation];
}

- (void)getBalanceAndTransactions:(CamiproRequest*)camiproRequest delegate:(id)delegate {
    if (![camiproRequest isKindOfClass:[CamiproRequest class]]) {
        @throw [NSException exceptionWithName:@"bad camiproRequest" reason:@"camiproRequest is either nil or not of class CamiproRequest" userInfo:nil];
    }
    ServiceRequest* operation = [[ServiceRequest alloc] initWithThriftServiceClient:[self thriftServiceClientInstance] service:self delegate:delegate];
    operation.serviceClientSelector = @selector(getBalanceAndTransactions:);
    operation.delegateDidReturnSelector = @selector(getBalanceAndTransactionsForCamiproRequest:didReturn:);
    operation.delegateDidFailSelector = @selector(getBalanceAndTransactionsFailedForCamiproRequest:);
    [operation addObjectArgument:camiproRequest];
    operation.returnType = ReturnTypeObject;
    [self.operationQueue addOperation:operation];
}

- (void)getStatsAndLoadingInfo:(CamiproRequest*)camiproRequest delegate:(id)delegate {
    if (![camiproRequest isKindOfClass:[CamiproRequest class]]) {
        @throw [NSException exceptionWithName:@"bad camiproRequest" reason:@"camiproRequest is either nil or not of class CamiproRequest" userInfo:nil];
    }
    ServiceRequest* operation = [[ServiceRequest alloc] initWithThriftServiceClient:[self thriftServiceClientInstance] service:self delegate:delegate];
    operation.serviceClientSelector = @selector(getStatsAndLoadingInfo:);
    operation.delegateDidReturnSelector = @selector(getStatsAndLoadingInfoForCamiproRequest:didReturn:);
    operation.delegateDidFailSelector = @selector(getStatsAndLoadingInfoFailedForCamiproRequest:);
    [operation addObjectArgument:camiproRequest];
    operation.returnType = ReturnTypeObject;
    [self.operationQueue addOperation:operation];
}

- (void)sendLoadingInfoByEmail:(CamiproRequest*)camiproRequest delegate:(id)delegate {
    if (![camiproRequest isKindOfClass:[CamiproRequest class]]) {
        @throw [NSException exceptionWithName:@"bad camiproRequest" reason:@"camiproRequest is either nil or not of class CamiproRequest" userInfo:nil];
    }
    ServiceRequest* operation = [[ServiceRequest alloc] initWithThriftServiceClient:[self thriftServiceClientInstance] service:self delegate:delegate];
    operation.serviceClientSelector = @selector(sendLoadingInfoByEmail:);
    operation.delegateDidReturnSelector = @selector(sendLoadingInfoByEmailForCamiproRequest:didReturn:);
    operation.delegateDidFailSelector = @selector(sendLoadingInfoByEmailFailedForCamiproRequest:);
    [operation addObjectArgument:camiproRequest];
    operation.returnType = ReturnTypeObject;
    [self.operationQueue addOperation:operation];
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
