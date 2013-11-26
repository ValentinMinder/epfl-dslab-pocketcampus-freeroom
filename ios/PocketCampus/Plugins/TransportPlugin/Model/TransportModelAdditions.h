//
//  TransportModelAdditions.h
//  PocketCampus
//
//  Created by Loïc Gardiol on 23.11.13.
//  Copyright (c) 2013 EPFL. All rights reserved.
//

#import "transport.h"

#pragma mark - TransportStation (Additions)

@interface TransportStation (Additions)

/*
 * Returns a shortened name for some common stations
 * (e.g. Ecublens VD, EPFL => EPFL)
 * For other stations, returns original name
 */
@property (nonatomic, readonly) NSString* shortName;

- (BOOL)isEqual:(id)object;
- (BOOL)isEqualToTransportStation:(TransportStation*)transportStation;
- (NSUInteger)hash;

@end

#pragma mark - TransportConnection (Additions)

@interface TransportConnection (Additions)

/*
 * Returns whether self is a connection that is to be done by walk
 * Caracterized by the fact that these connections have nil line name and
 * are less or equal to 3 min long
 */
@property (nonatomic, readonly, getter = isFeetConnection) BOOL feetConnection;

@end

#pragma mark - TransportLine (Additions)

@interface TransportLine (Additions)

/*
 * Returns a shortened name for some common transport line
 * (e.g. UMetm1 => M1)
 * For other lines, returns original name
 */
@property (nonatomic, readonly) NSString* shortName;

@end
