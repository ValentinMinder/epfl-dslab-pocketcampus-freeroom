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


//  Created by Loïc Gardiol on 08.12.13.

#import "NSOperationWithDelegate.h"

@class Service;

typedef enum {
    ReturnTypeNotSet = 0,
    ReturnTypeObject,
    ReturnTypeBool,
    ReturnTypeChar,
    ReturnTypeUnsignedChar,
    ReturnTypeDouble,
    ReturnTypeFloat,
    ReturnTypeInt,
    ReturnTypeUnsignedInt,
    ReturnTypeLong,
    ReturnTypeUnsignedLong,
    ReturnTypeLongLong,
    ReturnTypeUnsignedLongLong,
    ReturnTypeShort,
    ReturnTypeUnsignedShort
} ReturnType;

@interface ServiceRequest : NSOperationWithDelegate

- (id)initWithThriftServiceClient:(id)serviceClient service:(Service*)service delegate:(id)delegate;
- (id)initForCachedResponseOnlyWithService:(Service*)service;

@property (nonatomic, readonly, strong) id thriftServiceClient;
@property (nonatomic, readonly, weak) Service* service;
@property SEL serviceClientSelector;
@property ReturnType returnType;

- (void)setCustomTimeout:(NSTimeInterval)timeout __attribute__((deprecated));

- (void)addObjectArgument:(id)object;
- (void)addBoolArgument:(BOOL)val;
- (void)addCharArgument:(char)val;
- (void)addUnsignedCharArgument:(unsigned char)val;
- (void)addDoubleArgument:(double)val;
- (void)addFloatArgument:(float)val;
- (void)addIntArgument:(int)val;
- (void)addUnsignedIntArgument:(unsigned int)val;
- (void)addLongArgument:(long)val;
- (void)addUnsignedLongArgument:(unsigned long) val;
- (void)addLongLongArgument:(long long)val;
- (void)addUnsignedLongLongArgument:(unsigned long long)val;
- (void)addShortArgument:(short)val;
- (void)addUnsignedShortArgument:(unsigned short)val;


- (id)cachedResponseObjectEvenIfStale:(BOOL)evenIfStale;

@property (nonatomic) BOOL keepInCache;
@property (nonatomic) BOOL skipCache;
@property (nonatomic) NSTimeInterval cacheValidityInterval;
@property (nonatomic) BOOL returnEvenStaleCacheIfNoInternetConnection;

@end
