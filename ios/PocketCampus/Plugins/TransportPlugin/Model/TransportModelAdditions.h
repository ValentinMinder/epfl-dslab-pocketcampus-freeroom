//
//  TransportModelAdditions.h
//  PocketCampus
//
//  Created by Loïc Gardiol on 23.11.13.
//  Copyright (c) 2013 EPFL. All rights reserved.
//

#import "transport.h"

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

@interface TransportConnection (Additions)

/*
 * Returns whether self is a connection that is to be done by walk
 * Caracterized by the fact that these connections have nil line name and
 * are less or equal to 3 min long
 */
@property (nonatomic, readonly, getter = isFeetConnection) BOOL feetConnection;

/*
 * Returns YES if departureTime is in the past
 * NO otherwise
 */
@property (nonatomic, readonly) BOOL hasLeft;

@end

@interface TransportLine (Additions)

/*
 * Returns a shortened name for some common transport line
 * (e.g. UMetm1 => M1)
 * For other lines, returns original name
 */
@property (nonatomic, readonly) NSString* shortName;

/*
 * Returns shortName if shortName's length is smaller or equal to 3
 * otherwise, returns shortName first two caracters followed by '..'.
 */
@property (nonatomic, readonly) NSString* veryShortName;

@end

@interface TransportTrip (Additions)

/*
 * Returns the first connection that is not a feet connection
 * If the trip is composed of only a feet connection, it returns it
 * Returns nil if the trip is composed of no connection
 */
- (TransportConnection*)firstConnection;

/*
 * Returns number of changes (excluding feet parts)
 * So for example:
 * Lausanne-Flon (M2) - Lausanne (M2) - walk - Lausanne station - Genève
 * is one change (switching from M2 to train)
 */
@property (nonatomic, readonly) NSUInteger numberOfChanges;

@end

@interface QueryTripsResult (Additions)

/*
 * Returns QueryTripsResult.connections purged of ones that are already left at the the time of method call
 */
- (NSArray*)nonLeftTrips;

@end

