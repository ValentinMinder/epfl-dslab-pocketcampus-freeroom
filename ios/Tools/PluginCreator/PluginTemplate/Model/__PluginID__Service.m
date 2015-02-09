/* 
 * Copyright (c) 2014, PocketCampus.Org
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 *  * Redistributions of source code must retain the above copyright
 *    notice, this list of conditions and the following disclaimer.
 *  * Redistributions in binary form must reproduce the above copyright
 *    notice, this list of conditions and the following disclaimer in the
 *    documentation and/or other materials provided with the distribution.
 *  * Neither the name of PocketCampus.Org nor the
 *    names of its contributors may be used to endorse or promote products
 *    derived from this software without specific prior written permission.
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

//  Created by ___FULLUSERNAME___ on ___DATE___.
 
#import "__PluginID__Service.h"

@implementation __PluginID__Service

static __PluginID__Service* instance __weak = nil;

#pragma mark - Init

- (id)init {
    @synchronized(self) {
        if (instance) {
            @throw [NSException exceptionWithName:@"Double instantiation attempt" reason:@"__PluginID__Service cannot be instancied more than once at a time, use sharedInstance instead" userInfo:nil];
        }
        self = [super initWithServiceName:@"__PluginID_low__" thriftServiceClientClassName:NSStringFromClass(__PluginID__ServiceClient.class)];
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

// ----------------------------------------  TODO ----------------------------------------  //
// Implement async methods declared in header (__PluginID__Service.h)
// This is done very easily by using the ServiceRequest class.
// 
// Examples (see definitions in __PluginID__Service.h)
//
// 1) Method with 0 argument:
//  - (void)getMealsWithDelegate:(id)delegate {    
//      ServiceRequest* operation = [[PCServiceRequest alloc] initWithThriftServiceClient:[self thriftServiceClientInstance] service:self delegate:delegate];
//      operation.serviceClientSelector = @selector(getMeals); //corresponds to Thrift method definition
//      operation.delegateDidReturnSelector = @selector(getMealsDidReturn:); //corresponding *didReturn* definition
//      operation.delegateDidFailSelector = @selector(getMealsFailed); //corresponding *Failed* definition
//      operation.returnType = ReturnTypeObject; //result type. Can be object or any standard primitive types (ReturnTypeInt, ...)
//      [self.operationQueue addOperation:operation]; //schedule operation in background
//      [operation release]; //If you do NOT use ARC: release operation (has been retained by operationQueue)
//  }
//
// 2) Method with arguments:
//  - (void)setRatingForMeal:(Id)mealId rating:(double)rating delegate:(id)delegate {
//      ServiceRequest* operation = [[PCServiceRequest alloc] initWithThriftServiceClient:[self thriftServiceClientInstance] service:self delegate:delegate];
//      operation.serviceClientSelector = @selector(setRating::); //notice double columns, because Thrift setRating has 2 arguments
//      operation.delegateDidReturnSelector = @selector(setRatingForMeal:rating:didReturn:);
//      operation.delegateDidFailSelector = @selector(setRatingFailedForMeal:rating:);
//      [operation addLongLongArgument:mealId]; //add arguments in order in which they appear
//      [operation addDoubleArgument:rating];
//      operation.returnType = ReturnTypeInt; //return type is int this time
//      [self.operationQueue addOperation:operation]; //schedule operation in background
//      [operation release]; //If you do NOT use ARC: release operation (has been retained by operationQueue)
//  } 
//
// --------------------------------------------------------------------------------------  //


#pragma mark - Dealloc

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
