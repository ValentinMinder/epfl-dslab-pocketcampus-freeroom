//
//  ServiceRequest.h
//  PocketCampus
//
//  Created by Loïc Gardiol on 08.12.13.
//  Copyright (c) 2013 EPFL. All rights reserved.
//

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
