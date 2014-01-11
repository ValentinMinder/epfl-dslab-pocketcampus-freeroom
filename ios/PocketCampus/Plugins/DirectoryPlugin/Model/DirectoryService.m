

//  Created by Loïc Gardiol on 28.02.12.


#import "DirectoryService.h"

@implementation DirectoryService

static DirectoryService* instance __weak = nil;

#pragma mark - Init

- (id)init {
    @synchronized(self) {
        if (instance) {
            @throw [NSException exceptionWithName:@"Double instantiation attempt" reason:@"DirectoryService cannot be instancied more than once at a time, use sharedInstance instead" userInfo:nil];
        }
        self = [super initWithServiceName:@"directory" thriftServiceClientClassName:NSStringFromClass(DirectoryServiceClient.class)];
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

#pragma mark - Service methods

- (void)searchForRequest:(DirectoryRequest*)request delegate:(id)delegate {
    [PCUtils throwExceptionIfObject:request notKindOfClass:[DirectoryRequest class]];
    ServiceRequest* operation = [[ServiceRequest alloc] initWithThriftServiceClient:[self thriftServiceClientInstance] service:self delegate:delegate];
    operation.serviceClientSelector = @selector(searchDirectory:);
    operation.delegateDidReturnSelector = @selector(searchForRequest:didReturn:);
    operation.delegateDidFailSelector = @selector(searchFailedForRequest:);
    [operation addObjectArgument:request];
    operation.returnType = ReturnTypeObject;
    [self.operationQueue addOperation:operation];
}

- (void)searchPersons:(NSString *)nameOrSciper delegate:(id)delegate {
    if (![nameOrSciper isKindOfClass:[NSString class]]) {
        @throw [NSException exceptionWithName:@"bad nameOrSciper" reason:@"nameOrSciper is either nil or not of class NSString" userInfo:nil];
    }
    ServiceRequest* operation = [[ServiceRequest alloc] initWithThriftServiceClient:[self thriftServiceClientInstance] service:self delegate:delegate];
    operation.keepInCache = YES;
    operation.cacheValidityInterval = 60; //1 min
    operation.serviceClientSelector = @selector(searchPersons:);
    operation.delegateDidReturnSelector = @selector(searchDirectoryFor:didReturn:);
    operation.delegateDidFailSelector = @selector(searchDirectoryFailedFor:);
    [operation addObjectArgument:nameOrSciper];
    operation.returnType = ReturnTypeObject;
    [self.operationQueue addOperation:operation];
}

- (void)autocomplete:(NSString *)constraint delegate:(id)delegate {
    if (![constraint isKindOfClass:[NSString class]]) {
        @throw [NSException exceptionWithName:@"bad constraint" reason:@"constraint is either nil or not of class NSString" userInfo:nil];
    }
    ServiceRequest* operation = [[ServiceRequest alloc] initWithThriftServiceClient:[self thriftServiceClientInstance] service:self delegate:delegate];
    operation.serviceClientSelector = @selector(autocomplete:);
    operation.delegateDidReturnSelector = @selector(autocompleteFor:didReturn:);
    operation.delegateDidFailSelector = @selector(autocompleteFailedFor:);
    [operation addObjectArgument:constraint];
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