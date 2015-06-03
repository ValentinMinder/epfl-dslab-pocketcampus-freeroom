/* 
 * Copyright (c) 2014, PocketCampus.Org
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 * 	* Redistributions of source code must retain the above copyright
 * 	  notice, this list of conditions and the following disclaimer.
 * 	* Redistributions in binary form must reproduce the above copyright
 * 	  notice, this list of conditions and the following disclaimer in the
 * 	  documentation and/or other materials provided with the distribution.
 * 	* Neither the name of PocketCampus.Org nor the
 * 	  names of its contributors may be used to endorse or promote products
 * 	  derived from this software without specific prior written permission.
 * 
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND
 * ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED
 * WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 * DISCLAIMED. IN NO EVENT SHALL <COPYRIGHT HOLDER> BE LIABLE FOR ANY
 * DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES
 * (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES;
 * LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND
 * ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
 * (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
 * SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE. 
 */


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

- (void)getScheduleWithRequest:(ScheduleRequest*)request skipCache:(BOOL)skipCache delegate:(id<IsAcademiaServiceDelegate>)delegate {
    [PCUtils throwExceptionIfObject:request notKindOfClass:[ScheduleRequest class]];
    PCServiceRequest* operation = [[PCServiceRequest alloc] initWithThriftServiceClient:[self thriftServiceClientInstance] service:self delegate:delegate];
    operation.skipCache = skipCache;
    operation.cacheValidityInterval = 1800.0; //30 min
    operation.returnEvenStaleCacheIfNoInternetConnection = YES;
    operation.keepInCache = YES;
    operation.keepInCacheBlock = ^BOOL(void* returnedValue) {
        ScheduleResponse* response = (__bridge id)returnedValue;
        return (response.statusCode == IsaStatusCode_OK);
    };
    operation.serviceClientSelector = @selector(getSchedule:);
    operation.delegateDidReturnSelector = @selector(getScheduleForRequest:didReturn:);
    operation.delegateDidFailSelector = @selector(getScheduleFailedForRequest:);
    [operation addObjectArgument:request];
    operation.returnType = ReturnTypeObject;
    [self.operationQueue addOperation:operation];
}

- (void)getGradesWithDelegate:(id<IsAcademiaServiceDelegate>)delegate {
    PCServiceRequest* operation = [[PCServiceRequest alloc] initWithThriftServiceClient:[self thriftServiceClientInstance] service:self delegate:delegate];
    operation.skipCache = YES;
    operation.returnEvenStaleCacheIfNoInternetConnection = YES;
    operation.keepInCache = YES;
    operation.keepInCacheBlock = ^BOOL(void* returnedValue) {
        IsaGradesResponse* response = (__bridge id)returnedValue;
        return (response.statusCode == IsaStatusCode_OK);
    };
    operation.serviceClientSelector = @selector(getGrades);
    operation.delegateDidReturnSelector = @selector(getGradesDidReturn:);
    operation.delegateDidFailSelector = @selector(getGradesFailed);
    operation.returnType = ReturnTypeObject;
    [self.operationQueue addOperation:operation];
}

- (IsaGradesResponse*)getGradesFromCache {
    PCServiceRequest* operation = [[PCServiceRequest alloc] initForCachedResponseOnlyWithService:self];
    operation.serviceClientSelector = @selector(getGrades);
    operation.delegateDidReturnSelector = @selector(getGradesDidReturn:);
    operation.delegateDidFailSelector = @selector(getGradesFailed);
    operation.returnType = ReturnTypeObject;
    return [operation cachedResponseObjectEvenIfStale:YES];
}

#pragma mark - Dealloc

- (void)dealloc
{
    @synchronized(self) {
        instance = nil;
    }
}

@end
