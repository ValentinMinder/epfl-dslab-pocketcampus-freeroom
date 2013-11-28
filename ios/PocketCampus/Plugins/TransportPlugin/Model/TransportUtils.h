//
//  TransportUtils.h
//  PocketCampus
//
//  Created by Loïc Gardiol on 24.03.12.
//  Copyright (c) 2012 EPFL. All rights reserved.
//

#import <Foundation/Foundation.h>

#import "transport.h"

@interface TransportUtils : NSObject

+ (NSString*)durationgStringForInterval:(NSTimeInterval)interval;
+ (NSString*)hourMinutesStringForTimestamp:(NSTimeInterval)timestamp;
+ (NSString*)automaticHoursMinutesLeftStringForTimestamp:(NSTimeInterval)timestamp;
+ (NSString*)automaticTimeStringForTimestamp:(NSTimeInterval)timestamp maxIntervalForMinutesLeftString:(NSTimeInterval)maxIntervalSeconds;
+ (NSTimeInterval)secondsBetweenNowAndTimestamp:(NSTimeInterval)timestamp;
+ (NSArray*)nextRedundantDeparturesFromMessyResult:(QueryTripsResult*)queryTripResult; //returns an array of TransportConnection of redundant (and most direct) departures (metro line for ex.) in the query trip result, if some are found. nil otherwise

+ (NSArray*)connectionsWithoutAlreadyLeft:(NSArray*)connections __attribute__((deprecated)); //takes an array of TransportConnection and returns a new array of TransportConnection without the ones that are already left (defined by @"Left" of automaticHoursMinutesLeftStringForTimestamp
+ (int)numberOfChangeForTrip:(TransportTrip*)trip __attribute__((deprecated)); //excluding feet parts
+ (BOOL)isFeetConnection:(TransportConnection*)part __attribute__((deprecated));
+ (NSString*)nicerName:(NSString*)currentName __attribute__((deprecated)); //use "shortName" on respective classes
+ (NSString*)firstLineNameForTrip:(TransportTrip*)trip __attribute__((deprecated));

@end
