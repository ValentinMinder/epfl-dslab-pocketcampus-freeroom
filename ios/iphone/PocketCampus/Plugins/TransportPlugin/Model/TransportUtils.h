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
+ (int)numberOfChangeForTrip:(TransportTrip*)trip; //excluding feet parts
+ (NSString*)firstLineNameForTrip:(TransportTrip*)trip;
+ (BOOL)isFeetConnection:(TransportConnection*)part;
+ (NSString*)nicerName:(NSString*)currentName;

@end
